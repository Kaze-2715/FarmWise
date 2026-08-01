package com.farmwise.ai.service;

import static com.farmwise.common.util.ValidationUtil.validateFilter;
import static com.farmwise.common.util.ValidationUtil.validateRequired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.farmwise.ai.dto.ConversationResponse;
import com.farmwise.ai.dto.ConversationSummaryResponse;
import com.farmwise.ai.dto.MessageResponse;
import com.farmwise.ai.dto.ReferenceResponse;
import com.farmwise.ai.dto.TaskDraftResponse;
import com.farmwise.ai.dto.CreateConversationRequest;
import com.farmwise.ai.dto.CreateTaskFromMessageRequest;
import com.farmwise.ai.dto.SendMessageRequest;
import com.farmwise.ai.dto.SendMessageResponse;
import com.farmwise.ai.mapper.ConversationMapper;
import com.farmwise.ai.model.AdvisorResult;
import com.farmwise.ai.model.Conversation;
import com.farmwise.ai.model.ConversationMessage;
import com.farmwise.ai.model.TaskDraftSuggestion;
import com.farmwise.ai.store.ConversationGenerationLock;
import com.farmwise.ai.tool.AdvisorTools;
import com.farmwise.ai.tool.ReferenceCollector;
import com.farmwise.common.exception.BizException;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.task.dto.CreateFarmTaskRequest;
import com.farmwise.task.dto.FarmTaskResponse;
import com.farmwise.task.mapper.FarmTaskMapper;
import com.farmwise.task.service.FarmTaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {
    private static final Set<String> STATUSES = Set.of("active", "closed");
    private static final int HISTORY_MESSAGE_LIMIT = 20;
    private static final int MAX_TOOL_CALL_ROUNDS = 6;
    private static final String TASK_SOURCE_TYPE = "aiMessage";
    private static final Set<String> TASK_TYPES = Set.of(
            "irrigation",
            "fertilization",
            "pesticide",
            "weeding",
            "inspection",
            "harvest",
            "other");

    private static final Set<String> TASK_PRIORITIES = Set.of("low", "medium", "high");
    private static final String SYSTEM_PROMPT = """
            你是 FarmWise 智慧农业平台的 AI 技术顾问。
            只根据对话历史和业务工具返回的真实数据进行判断。
            需要实时数据时必须调用工具，不得编造监测值、业务状态或记录 ID。
            信息不足时应明确说明缺少哪些数据。
            不得声称已经执行灌溉、创建设备、创建任务或修改业务数据。

            只有在回答明确建议用户执行一项具体、可操作的农事工作，并且已有足够真实数据支持时，才生成一份 taskDraft。
            如果只是知识问答、情况说明、数据不足，或者没有明确行动建议，taskDraft 必须为 null。
            任务草稿只是等待用户确认的建议，不代表任务已经创建或执行。
            taskType 只能是 irrigation、fertilization、pesticide、weeding、inspection、harvest、other。
            priority 只能是 low、medium、high。
            title 必须简洁明确，不超过 150 个字符。
            description 必须说明建议执行的工作及原因，不超过 1000 个字符。
            不得编造负责人、截止时间、业务记录 ID 或已经完成的操作。
            """;
    private static final TypeReference<List<ReferenceResponse>> REFERENCE_LIST_TYPE = new TypeReference<List<ReferenceResponse>>() {
    };

    private final ObjectMapper objectMapper;

    private final ConversationMapper conversationMapper;
    private final LandMapper landMapper;
    private final FarmTaskMapper farmTaskMapper;
    private final ChatClient chatClient;
    private final AdvisorTools advisorTools;
    private final TransactionTemplate transactionTemplate;
    private final ToolCallingManager toolCallingManager;
    private final FarmTaskService farmTaskService;

    private final ConversationGenerationLock generationLock;

    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> listConversations(
            String userId,
            String landId,
            String status) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        status = validateFilter(status, STATUSES, "不支持的对话状态");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "地块不存在或不属于该用户"));
        if (status == null) {
            status = "active";
        }

        return conversationMapper.findAllByLandIdAndStatus(landId, status)
                .stream()
                .map(ConversationSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConversationResponse getConversation(
            String userId,
            String conversationId) {
        String convId = validateRequired(conversationId, "对话 ID 不能为空");
        Conversation conversation = conversationMapper
                .findConversationByIdAndOwnerId(convId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "对话不存在或不属于当前用户"));

        List<MessageResponse> messages = conversationMapper
                .findMessagesByConversationIds(List.of(convId))
                .stream()
                .map(this::toMessageResponse)
                .toList();

        return ConversationResponse.from(conversation, messages);
    }

    private MessageResponse toMessageResponse(ConversationMessage message) {
        try {
            List<ReferenceResponse> references = objectMapper.readValue(message.referencesJson(),
                    REFERENCE_LIST_TYPE);
            TaskDraftResponse taskDraft = message.taskDraftJson() == null ? null
                    : objectMapper.readValue(message.taskDraftJson(), TaskDraftResponse.class);
            return MessageResponse.from(message, references, taskDraft);
        } catch (JacksonException exception) {
            throw new IllegalStateException("AI 消息 JSON 解析失败，messageId=" + message.id(), exception);
        }
    }

    @Transactional
    public ConversationResponse createConversation(String userId, CreateConversationRequest request) {
        String landId = validateRequired(request.landId(), "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "地块不存在或不属于该用户"));
        String title = validateRequired(request.title(), "对话标题不能为空");
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Conversation conversation = new Conversation(id, landId, title, "active", userId, now, now);

        int affectedRows = conversationMapper.addConversation(conversation);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "创建对话失败");
        }
        return ConversationResponse.from(conversation, List.of());
    }

    public SendMessageResponse sendMessage(String userId, String conversationId,
            SendMessageRequest request) {
        String convId = validateRequired(conversationId, "对话 ID 不能为空");
        String userContent = validateRequired(request.content(), "用户消息不能为空");
        LocalDateTime userCreatedAt = LocalDateTime.now(ZoneOffset.UTC);
        Conversation conversation = conversationMapper.findConversationByIdAndOwnerId(convId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "对话不存在或不属于该用户"));

        if (!"active".equals(conversation.status())) {
            throw new BizException(HttpStatus.CONFLICT, "该对话已关闭");
        }
        String lockToken = generationLock.tryAcquire(convId)
                .orElseThrow(() -> new BizException(HttpStatus.CONFLICT, "该对话正在生成回复，请稍后再试"));
        try {
            List<ConversationMessage> recentMessages = conversationMapper.findRecentMessagesByConversationId(convId,
                    HISTORY_MESSAGE_LIMIT);

            List<ConversationMessage> history = List.copyOf(recentMessages.reversed());

            List<Message> conversationMessages = history.stream().map(this::toSpringAiMessage).toList();

            List<Message> promptMessages = new ArrayList<>(conversationMessages.size() + 2);

            promptMessages.add(new SystemMessage(SYSTEM_PROMPT));
            promptMessages.addAll(conversationMessages);
            promptMessages.add(new UserMessage(userContent));

            Prompt prompt = new Prompt(List.copyOf(promptMessages));

            ReferenceCollector referenceCollector = new ReferenceCollector();

            Map<String, Object> toolContext = Map.of(
                    "userId", userId,
                    "landId", conversation.landId(),
                    "referenceCollector", referenceCollector);

            AtomicInteger toolCallRounds = new AtomicInteger();

            ToolCallingAdvisor toolCallingAdvisor = ToolCallingAdvisor.builder()
                    .toolCallingManager(toolCallingManager)
                    .toolExecutionEligibilityChecker(response -> {
                        if (response == null || !response.hasToolCalls()) {
                            return false;
                        }
                        if (toolCallRounds.incrementAndGet() > MAX_TOOL_CALL_ROUNDS) {
                            throw new BizException(
                                    HttpStatus.SERVICE_UNAVAILABLE,
                                    "AI 查询步骤过多，请缩小问题范围后重试");
                        }
                        return true;
                    }).build();

            AdvisorResult result = chatClient
                    .prompt(prompt)
                    .advisors(toolCallingAdvisor)
                    .tools(advisorTools)
                    .toolContext(toolContext)
                    .call()
                    .entity(AdvisorResult.class);

            List<ReferenceResponse> references = referenceCollector.snapshot();

            if (result == null
                    || result.content() == null
                    || result.content().isBlank()) {
                throw new IllegalStateException("AI 模型没有返回有效的回复内容");
            }

            String assistantContent = result.content().strip();

            TaskDraftResponse taskDraft = toTaskDraftResponse(result.taskDraft());

            String referencesJson = writeJson(references);

            String taskDraftJson = taskDraft == null ? null : writeJson(taskDraft);

            LocalDateTime assistantCreatedAt = LocalDateTime.now(ZoneOffset.UTC);

            ConversationMessage userMessage = new ConversationMessage(
                    UUID.randomUUID().toString(),
                    convId,
                    "user",
                    userContent,
                    "[]",
                    null,
                    userCreatedAt);

            ConversationMessage assistantMessage = new ConversationMessage(
                    UUID.randomUUID().toString(),
                    convId,
                    "assistant",
                    assistantContent,
                    referencesJson,
                    taskDraftJson,
                    assistantCreatedAt);

            transactionTemplate.executeWithoutResult(status -> {
                Conversation lockedConversation = conversationMapper
                        .findConversationByIdAndOwnerIdForUpdate(convId, userId)
                        .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "对话不存在或不属于当前用户"));
                if (!"active".equals(lockedConversation.status())) {
                    throw new BizException(
                            HttpStatus.CONFLICT,
                            "该对话已关闭");
                }

                int insertedRows = conversationMapper.addMessages(
                        List.of(userMessage, assistantMessage));

                if (insertedRows != 2) {
                    throw new BizException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "保存对话消息失败");
                }

                int updatedRows = conversationMapper.updateUpdatedAt(
                        convId,
                        assistantMessage.createdAt());

                if (updatedRows != 1) {
                    throw new BizException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "更新对话时间失败");
                }
            });

            MessageResponse userMessageResponse = MessageResponse.from(userMessage, List.of(), null);

            MessageResponse assistantMessageResponse = MessageResponse.from(assistantMessage, references, taskDraft);

            return new SendMessageResponse(userMessageResponse, assistantMessageResponse);
        } finally {
            try {
                generationLock.release(convId, lockToken);
            } catch (RuntimeException exception) {
                log.warn(
                        "释放 AI 对话生成锁失败，conversationId={}",
                        convId,
                        exception);
            }
        }
    }

    @Transactional
    public FarmTaskResponse createTaskFromMessage(
            String userId,
            String conversationId,
            String messageId,
            CreateTaskFromMessageRequest request) {
        String convId = validateRequired(conversationId, "对话 ID 不能为空");

        String msgId = validateRequired(messageId, "消息 ID 不能为空");

        Conversation conversation = conversationMapper.findConversationByIdAndOwnerIdForUpdate(convId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "对话不存在或不属于当前用户"));

        if (!"active".equals(conversation.status())) {
            throw new BizException(
                    HttpStatus.CONFLICT,
                    "对话已关闭，不能创建任务");
        }

        ConversationMessage message = conversationMapper
                .findMessageByIdAndConversationIdForUpdate(convId, msgId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "消息不存在或不属于当前对话"));

        if (!"assistant".equals(message.role())) {
            throw new BizException(
                    HttpStatus.BAD_REQUEST,
                    "只有 AI 助手消息可以生成任务");
        }

        if (message.taskDraftJson() == null) {
            throw new BizException(
                    HttpStatus.CONFLICT,
                    "该消息没有可创建的任务草稿");
        }

        TaskDraftResponse taskDraft = readTaskDraft(message);

        boolean existsTask = farmTaskMapper.existsBySourceTypeAndSourceId(TASK_SOURCE_TYPE, message.id());

        if (existsTask) {
            throw new BizException(HttpStatus.CONFLICT, "该消息已经创建过任务");
        }

        CreateFarmTaskRequest taskRequest = new CreateFarmTaskRequest(
                conversation.landId(),
                taskDraft.taskType(),
                taskDraft.title(),
                taskDraft.description(),
                taskDraft.priority(),
                request.assigneeId(),
                request.deadline(),
                null);

        return farmTaskService.createFarmTask(userId, taskRequest, TASK_SOURCE_TYPE, message.id());
    }

    @Transactional
    public void closeConversation(
            String userId,
            String conversationId) {
        String convId = validateRequired(conversationId, "对话 ID 不能为空");

        Conversation conversation = conversationMapper
                .findConversationByIdAndOwnerIdForUpdate(
                        convId,
                        userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "对话不存在或不属于当前用户"));

        if (!"active".equals(conversation.status())) {
            throw new BizException(
                    HttpStatus.CONFLICT,
                    "只有进行中的对话可以关闭");
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        int affectedRows = conversationMapper.closeIfActive(
                convId,
                now);

        if (affectedRows != 1) {
            throw new BizException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "关闭对话失败");
        }
    }

    private TaskDraftResponse readTaskDraft(
            ConversationMessage message) {
        try {
            TaskDraftResponse taskDraft = objectMapper.readValue(
                    message.taskDraftJson(),
                    TaskDraftResponse.class);

            if (taskDraft == null) {
                throw new IllegalStateException(
                        "AI 消息中的任务草稿为空，messageId="
                                + message.id());
            }

            return taskDraft;
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "AI 任务草稿 JSON 解析失败，messageId="
                            + message.id(),
                    exception);
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "AI 消息 JSON 序列化失败",
                    exception);
        }
    }

    private Message toSpringAiMessage(ConversationMessage message) {
        return switch (message.role()) {
            case "user" -> new UserMessage(message.content());
            case "assistant" -> new AssistantMessage(message.content());
            default -> throw new IllegalStateException(
                    "AI 消息不合法，messageId=%s, role=%s".formatted(message.id(), message.role()));
        };
    }

    private TaskDraftResponse toTaskDraftResponse(TaskDraftSuggestion suggestion) {
        if (suggestion == null) {
            return null;
        }

        String taskType = suggestion.taskType() == null
                ? null
                : suggestion.taskType().strip();

        String title = suggestion.title() == null
                ? null
                : suggestion.title().strip();

        String description = suggestion.description() == null
                ? null
                : suggestion.description().strip();

        String priority = suggestion.priority() == null
                ? null
                : suggestion.priority().strip();

        boolean invalid = taskType == null
                || !TASK_TYPES.contains(taskType)
                || title == null
                || title.isBlank()
                || title.length() > 150
                || description == null
                || description.isBlank()
                || description.length() > 1000
                || priority == null
                || !TASK_PRIORITIES.contains(priority);

        if (invalid) {
            log.warn(
                    "AI 模型返回了无效任务草稿，taskType={}, priority={}",
                    taskType,
                    priority);
            return null;
        }

        return new TaskDraftResponse(
                taskType,
                title,
                description,
                priority,
                null,
                null);
    }
}
