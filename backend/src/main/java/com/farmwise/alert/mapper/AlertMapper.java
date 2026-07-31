package com.farmwise.alert.mapper;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.alert.dto.LatestAlertStateRow;
import com.farmwise.alert.model.Alert;

@Mapper
public interface AlertMapper {
    @Select("""
            SELECT id, status, source_device_id, handled_at
            FROM alerts
            WHERE type = #{type}
            AND land_id = #{landId}
            AND source_metric = #{metric}
            ORDER BY occurred_at DESC, id DESC
            LIMIT 1
            """)
    Optional<LatestAlertStateRow> findLatestState(
            @Param("type") String type,
            @Param("landId") String landId,
            @Param("metric") String metric);

    @Select("""
            SELECT id, status, source_device_id, handled_at
            FROM alerts
            WHERE type = #{type}
            AND land_id = #{landId}
            AND source_metric = #{metric}
            AND status in ('pending', 'processing')
            ORDER BY occurred_at DESC, id DESC
            LIMIT 1
            """)
    Optional<LatestAlertStateRow> findActiveByLandAndMetric(
            @Param("type") String type,
            @Param("landId") String landId,
            @Param("metric") String metric);

    @Insert("""
            INSERT INTO alerts (
                id,
                land_id,
                type,
                severity,
                title,
                description,
                suggestion,
                status,
                occurred_at,
                source_device_id,
                source_metric,
                source_value,
                source_unit,
                handle_measure,
                handled_at,
                handle_result,
                handle_remark,
                handle_operator_id,
                created_at,
                updated_at
            ) VALUES (
                #{id},
                #{landId},
                #{type},
                #{severity},
                #{title},
                #{description},
                #{suggestion},
                #{status},
                #{occurredAt},
                #{sourceDeviceId},
                #{sourceMetric},
                #{sourceValue},
                #{sourceUnit},
                #{handleMeasure},
                #{handledAt},
                #{handleResult},
                #{handleRemark},
                #{handleOperatorId},
                #{createdAt},
                #{updatedAt}
            )
            """)
    int add(Alert alert);

    @Select("""
            SELECT *
            FROM alerts
            WHERE id = #{alertId}
            AND land_id = #{landId}
            AND type = #{type}
            AND source_metric = #{metric}
            FOR UPDATE
            """)
    Optional<Alert> findForUpdate(
            @Param("alertId") String alertId,
            @Param("type") String type,
            @Param("landId") String landId,
            @Param("metric") String metric);

    @Update("""
            UPDATE alerts
            SET status = 'resolved',
                handle_measure = '系统连续监测确认指标恢复正常',
                handled_at = #{handledAt},
                handle_result = '监测指标已恢复正常',
                handle_remark = NULL,
                handle_operator_id = NULL,
                updated_at = #{updatedAt}
            WHERE id = #{alertId}
              AND status IN ('pending', 'processing')
            """)
    int autoResolve(
            @Param("alertId") String alertId,
            @Param("handledAt") LocalDateTime handledAt,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Select("""
            SELECT *
            FROM alerts
            WHERE id = #{alertId}
            """)
    Optional<Alert> findById(
            @Param("alertId") String alertId);
}
