package com.farmwise.report.service;

import static com.farmwise.common.util.ValidationUtil.validateFilter;
import static com.farmwise.common.util.ValidationUtil.validateOptional;
import static com.farmwise.common.util.ValidationUtil.validateRequired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.ai.mapper.ConversationMapper;
import com.farmwise.alert.mapper.AlertMapper;
import com.farmwise.common.exception.BizException;
import com.farmwise.device.mapper.DeviceMapper;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.land.model.Land;
import com.farmwise.monitoring.mapper.SensorReadingMapper;
import com.farmwise.report.dto.AiAdviceSnapshotRow;
import com.farmwise.report.dto.GenerateReportRequest;
import com.farmwise.report.dto.ReportResponse;
import com.farmwise.report.dto.ReportSnapshotResponse;
import com.farmwise.report.dto.ReportSnapshotResponse.*;
import com.farmwise.report.dto.ReportSummaryResponse;
import com.farmwise.report.mapper.ReportMapper;
import com.farmwise.report.model.Report;
import com.farmwise.report.model.ReportSummary;
import com.farmwise.task.mapper.FarmTaskMapper;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ReportService {
    private static final Set<String> TYPES = Set.of(
            "comprehensive",
            "device",
            "environment",
            "alert",
            "task");
    private static final Set<String> STATUSES = Set.of("generated", "archived");
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Shanghai");
    private static final TypeReference<List<ReferenceSnapshot>> REFERENCE_SNAPSHOT_LIST_TYPE = new TypeReference<List<ReferenceSnapshot>>() {
    };

    private final ReportMapper reportMapper;
    private final LandMapper landMapper;
    private final ObjectMapper objectMapper;
    private final DeviceMapper deviceMapper;
    private final SensorReadingMapper sensorReadingMapper;
    private final AlertMapper alertMapper;
    private final FarmTaskMapper farmTaskMapper;
    private final ConversationMapper conversationMapper;

    @Transactional(readOnly = true)
    public List<ReportSummaryResponse> listReports(
            String userId,
            String landId,
            String type,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            String keyword) {
        landId = validateOptional(landId);
        type = validateFilter(type, TYPES, "不支持的报告类型");
        status = validateFilter(status, STATUSES, "不支持的报告状态");
        keyword = validateOptional(keyword);

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "开始日期不能晚于结束日期");
        }

        if (landId != null) {
            landMapper.findByIdAndOwnerId(landId, userId)
                    .orElseThrow(() -> new BizException(
                            HttpStatus.NOT_FOUND,
                            "地块不存在或不属于当前用户"));
        }

        return reportMapper.findAllByConditions(
                userId,
                landId,
                type,
                status,
                startDate,
                endDate,
                keyword)
                .stream()
                .map(ReportSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(String userId, String reportId) {
        String id = validateRequired(reportId, "报告 ID 不能为空");

        Report report = reportMapper.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "报告不存在或不属于当前用户"));

        try {
            ReportSnapshotResponse snapshot = objectMapper.readValue(
                    report.snapshotJson(),
                    ReportSnapshotResponse.class);
            return ReportResponse.from(report, snapshot);
        } catch (JacksonException exception) {
            throw new IllegalStateException("报告快照解析失败，reportId=" + report.id(), exception);
        }
    }

    @Transactional
    public ReportSummaryResponse archiveReport(String userId, String reportId) {
        String id = validateRequired(reportId, "报告 ID 不能为空");

        ReportSummary report = reportMapper.findSummaryByIdAndOwnerIdForUpdate(id, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "报告不存在或不属于当前用户"));

        if (!"generated".equals(report.status())) {
            throw new BizException(HttpStatus.CONFLICT, "只有已生成的报告可以归档");
        }

        int updatedRows = reportMapper.archiveIfGenerated(id);
        if (updatedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "归档报告失败");
        }

        ReportSummary archivedReport = new ReportSummary(
                report.id(),
                report.landId(),
                report.type(),
                report.title(),
                report.startDate(),
                report.endDate(),
                "archived",
                report.creatorId(),
                report.createdAt(),
                report.generatedAt(),
                report.summary());

        return ReportSummaryResponse.from(archivedReport);
    }

    @Transactional
    public ReportResponse generateReport(String userId, GenerateReportRequest request) {
        String landId = validateRequired(request.landId(), "地块 ID 不能为空");
        String type = validateRequired(request.type(), "报告类型不能为空");
        String title = validateRequired(request.title(), "报告标题不能为空");

        if (request.startDate().isAfter(request.endDate())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "开始日期不能晚于结束日期");
        }
        LocalDateTime startAt = request.startDate()
                .atStartOfDay(BUSINESS_ZONE)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
        LocalDateTime endAt = request.endDate()
                .plusDays(1)
                .atStartOfDay(BUSINESS_ZONE)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
        Land land = landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "地块不存在或不属于该用户"));

        LandSnapshot landSnapshot = new LandSnapshot(land.id(), land.name(), land.crop(),
                land.area());

        DeviceSnapshot deviceSnapshot = deviceMapper.snapshot(landId);

        List<EnvironmentSnapshot> environmentSnapshots = sensorReadingMapper.snapshot(landId, startAt, endAt);

        AlertSnapshot alertSnapshot = alertMapper.snapshot(landId, startAt, endAt);

        TaskSnapshot taskSnapshot = farmTaskMapper.snapshot(landId, startAt, endAt);

        List<AiAdviceSnapshot> aiAdviceSnapshots = conversationMapper.snapshot(landId, startAt, endAt).stream()
                .map(this::toAiAdviceSnapshot)
                .toList();

        ReportSnapshotResponse snapshot = new ReportSnapshotResponse(landSnapshot, deviceSnapshot,
                List.copyOf(environmentSnapshots), alertSnapshot, taskSnapshot, List.copyOf(aiAdviceSnapshots));

        String summary = buildSummary(type, snapshot);

        String snapshotJson = writeSnapshot(snapshot);

        String reportId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        Report report = new Report(
                reportId,
                landId,
                type,
                title,
                request.startDate(),
                request.endDate(),
                "generated",
                userId,
                now,
                now,
                summary,
                snapshotJson);

        int insertedRows = reportMapper.addReport(report);

        if (insertedRows != 1) {
            throw new BizException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "生成报告失败");
        }

        return ReportResponse.from(report, snapshot);
    }

    private String writeSnapshot(ReportSnapshotResponse snapshot) {
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JacksonException exception) {
            throw new IllegalStateException("报告快照序列化失败", exception);
        }
    }

    private AiAdviceSnapshot toAiAdviceSnapshot(AiAdviceSnapshotRow row) {
        try {
            List<ReferenceSnapshot> references = objectMapper.readValue(row.referencesJson(),
                    REFERENCE_SNAPSHOT_LIST_TYPE);

            return new AiAdviceSnapshot(row.messageId(), row.content(), row.createdAt(), List.copyOf(references));
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "报告 AI 建议引用解析失败，messageId=" + row.messageId(),
                    exception);
        }
    }

    private String buildSummary(String type, ReportSnapshotResponse snapshot) {
        int abnormalEnvironmentCount = (int) snapshot.environment()
                .stream()
                .filter(environment -> "low".equals(environment.status())
                        || "high".equals(environment.status()))
                .count();

        int activeAlertCount = snapshot.alerts().pending() + snapshot.alerts().processing();

        int activeTaskCount = snapshot.tasks().pending() + snapshot.tasks().processing();

        int aiAdviceCount = snapshot.aiAdvice().size();
        String aiAdviceSummary = aiAdviceCount > 0
                ? "本期收录了 %d 条 AI 顾问建议。".formatted(aiAdviceCount)
                : "本期暂无 AI 顾问建议。";

        return switch (type) {
            case "comprehensive" -> ("本期记录 %d 项环境指标，其中 %d 项异常；"
                    + "存在 %d 条未结束预警和 %d 项未结束农事任务。%s").formatted(
                            snapshot.environment().size(),
                            abnormalEnvironmentCount,
                            activeAlertCount,
                            activeTaskCount,
                            aiAdviceSummary);
            case "device" -> ("当前地块共有 %d 台设备，%d 台在线，"
                    + "%d 台离线，%d 台处于低电量状态。").formatted(
                            snapshot.devices().total(),
                            snapshot.devices().online(),
                            snapshot.devices().offline(),
                            snapshot.devices().lowBattery());
            case "environment" -> "本期记录 %d 项环境指标，其中 %d 项超出适宜范围。".formatted(
                    snapshot.environment().size(),
                    abnormalEnvironmentCount);
            case "alert" -> ("本期共有 %d 条异常预警，%d 条待处理，"
                    + "%d 条处理中，%d 条已解决，%d 条已忽略。").formatted(
                            snapshot.alerts().total(),
                            snapshot.alerts().pending(),
                            snapshot.alerts().processing(),
                            snapshot.alerts().resolved(),
                            snapshot.alerts().ignored());
            case "task" -> ("本期共有 %d 项农事任务，%d 项待处理，"
                    + "%d 项进行中，%d 项已完成，%d 项已取消。").formatted(
                            snapshot.tasks().total(),
                            snapshot.tasks().pending(),
                            snapshot.tasks().processing(),
                            snapshot.tasks().completed(),
                            snapshot.tasks().cancelled());
            default -> throw new IllegalStateException("不支持的报告类型：" + type);
        };
    }
}
