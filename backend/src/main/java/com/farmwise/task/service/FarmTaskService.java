package com.farmwise.task.service;

import static com.farmwise.common.util.ValidationUtil.validateFilter;
import static com.farmwise.common.util.ValidationUtil.validateRequired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.common.exception.BizException;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.task.dto.CancelFarmTaskRequest;
import com.farmwise.task.dto.CompleteFarmTaskRequest;
import com.farmwise.task.dto.CreateFarmTaskRequest;
import com.farmwise.task.dto.FarmTaskResponse;
import com.farmwise.task.event.FarmTaskStatusChangedEvent;
import com.farmwise.task.mapper.FarmTaskMapper;
import com.farmwise.task.model.FarmTask;
import com.farmwise.user.mapper.UserMapper;
import com.farmwise.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmTaskService {
    private static final Set<String> TASK_TYPES = Set.of(
            "irrigation",
            "fertilization",
            "pesticide",
            "weeding",
            "inspection",
            "harvest",
            "other");
    private static final Set<String> PRIORITIES = Set.of("low", "medium", "high");
    private static final Set<String> STATUSES = Set.of("pending", "processing", "completed", "cancelled");
    private static final Set<String> SOURCE_TYPES =
            Set.of("manual", "alert", "plan", "system", "aiMessage");

    private final FarmTaskMapper farmTaskMapper;
    private final LandMapper landMapper;
    private final UserMapper userMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public FarmTaskResponse createFarmTask(
            String userId,
            CreateFarmTaskRequest request,
            String sourceType,
            String sourceId) {
        String landId = validateRequired(request.landId(), "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));

        String assigneeId = validateRequired(request.assigneeId(), "执行人 ID 不能为空");

        User assignee = userMapper.findById(assigneeId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "执行人用户不存在"));
        if (!"active".equals(assignee.status())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "执行人账号已被禁用");
        }

        String taskType = validateRequired(request.taskType(), "任务类型不能为空");
        validateFilter(taskType, TASK_TYPES, "不支持的任务类型");

        String priority = validateRequired(request.priority(), "优先级不能为空");
        validateFilter(priority, PRIORITIES, "不支持的优先级类型");

        sourceType = validateRequired(sourceType, "任务来源类型不能为空");
        validateFilter(sourceType, SOURCE_TYPES, "不支持的任务来源");

        if ("manual".equals(sourceType)) {
            if (sourceId != null) {
                throw new BizException(HttpStatus.BAD_REQUEST, "手动创建任务不应有关联任务");
            }
        } else {
            sourceId = validateRequired(sourceId, "自动生成任务来源 ID 不能为空");
        }

        String title = validateRequired(request.title(), "任务标题不能为空");
        String description = validateRequired(request.description(), "任务描述不能为空");
        String remark = request.remark() == null ? "" : request.remark().strip();

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        FarmTask task = new FarmTask(
                UUID.randomUUID().toString(),
                landId,
                sourceType,
                sourceId,
                taskType,
                title,
                description,
                priority,
                "pending",
                assigneeId,
                request.deadline(),
                now,
                null,
                "",
                remark,
                now);

        int affectedRows = farmTaskMapper.addTask(task);
        if (affectedRows != 1) {
            throw new BizException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "创建农事任务失败");
        }

        return FarmTaskResponse.from(task);
    }

    @Transactional(readOnly = true)
    public List<FarmTaskResponse> listFarmTasks(
            String userId,
            String landId,
            String taskType,
            String priority,
            String status) {
        landId = validateRequired(landId, "地块 ID 不能为空");
        landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));

        taskType = validateFilter(taskType, TASK_TYPES, "不支持的任务类型");
        priority = validateFilter(priority, PRIORITIES, "不支持的任务优先级");
        status = validateFilter(status, STATUSES, "不支持的任务状态");

        return farmTaskMapper.findAllByConditions(landId, taskType, priority, status)
                .stream()
                .map(FarmTaskResponse::from)
                .toList();
    }

    @Transactional
    public FarmTaskResponse startFarmTask(String userId, String taskId) {
        taskId = validateRequired(taskId, "任务 ID 不能为空");
        FarmTask task = farmTaskMapper.findByIdAndOwnerIdForUpdate(taskId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "要更新的任务不存在或不属于当前用户"));
        if (!"pending".equals(task.status())) {
            throw new BizException(HttpStatus.CONFLICT, "只有待处理任务可以启动");
        }
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        int affectedRows = farmTaskMapper.startIfPending(task.id(), now);
        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "启动农事任务失败");
        }
        FarmTask newTask = new FarmTask(
                task.id(),
                task.landId(),
                task.sourceType(),
                task.sourceId(),
                task.taskType(),
                task.title(),
                task.description(),
                task.priority(),
                "processing",
                task.assigneeId(),
                task.deadline(),
                task.createdAt(),
                task.completedAt(),
                task.result(),
                task.remark(),
                now);
        return FarmTaskResponse.from(newTask);
    }

    @Transactional
    public FarmTaskResponse completeFarmTask(
            String userId,
            String taskId,
            CompleteFarmTaskRequest request) {
        taskId = validateRequired(taskId, "任务 ID 不能为空");
        FarmTask task = farmTaskMapper.findByIdAndOwnerIdForUpdate(taskId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "要完成的任务不存在或不属于当前用户"));
        if (!"processing".equals(task.status())) {
            throw new BizException(HttpStatus.CONFLICT, "只有进行中的任务可以完成");
        }

        String result = request.result().strip();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int affectedRows = farmTaskMapper.completeIfProcessing(
                task.id(),
                result,
                now);
        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "完成农事任务失败");
        }

        eventPublisher.publishEvent(new FarmTaskStatusChangedEvent(task.sourceType(), task.sourceId(), "completed", now));

        FarmTask completedTask = new FarmTask(
                task.id(),
                task.landId(),
                task.sourceType(),
                task.sourceId(),
                task.taskType(),
                task.title(),
                task.description(),
                task.priority(),
                "completed",
                task.assigneeId(),
                task.deadline(),
                task.createdAt(),
                now,
                result,
                task.remark(),
                now);
        return FarmTaskResponse.from(completedTask);
    }

    @Transactional
    public FarmTaskResponse cancelFarmTask(
            String userId,
            String taskId,
            CancelFarmTaskRequest request) {
        taskId = validateRequired(taskId, "任务 ID 不能为空");

        FarmTask task = farmTaskMapper.findByIdAndOwnerIdForUpdate(taskId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "任务不存在或不属于当前用户"));

        if (!"pending".equals(task.status()) && !"processing".equals(task.status())) {
            throw new BizException(HttpStatus.CONFLICT, "只有待处理或进行中的任务可以取消");
        }

        String reason = request.reason().strip();

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        int affectedRows = farmTaskMapper.cancelIfActive(task.id(), reason, now);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "更新任务状态失败");
        }

        eventPublisher
                .publishEvent(new FarmTaskStatusChangedEvent(task.sourceType(), task.sourceId(), "cancelled", now));

        return FarmTaskResponse.from(new FarmTask(
                task.id(),
                task.landId(),
                task.sourceType(),
                task.sourceId(),
                task.taskType(),
                task.title(),
                task.description(),
                task.priority(),
                "cancelled",
                task.assigneeId(),
                task.deadline(),
                task.createdAt(),
                task.completedAt(),
                task.result(),
                reason,
                now));
    }

}
