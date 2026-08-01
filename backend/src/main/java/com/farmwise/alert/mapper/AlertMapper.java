package com.farmwise.alert.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.alert.dto.AlertQueryRow;
import com.farmwise.alert.dto.LatestAlertStateRow;
import com.farmwise.alert.model.Alert;

@Mapper
public interface AlertMapper {
    @Select("""
            <script>
            SELECT
                a.id,
                a.land_id,
                a.type,
                a.severity,
                a.title,
                a.description,
                a.suggestion,
                a.status,
                a.occurred_at,
                a.source_device_id,
                a.source_metric,
                a.source_value,
                a.source_unit,
                a.handle_measure,
                a.handled_at,
                a.handle_result,
                a.handle_remark,
                COALESCE(NULLIF(u.real_name, ''), u.username) AS handle_operator
            FROM alerts a
            LEFT JOIN users u
              ON u.id = a.handle_operator_id
            WHERE a.land_id = #{landId}
            <if test="type != null">
                AND a.type = #{type}
            </if>
            <if test="severity != null">
                AND a.severity = #{severity}
            </if>
            <if test="status != null">
                AND a.status = #{status}
            </if>
            ORDER BY a.occurred_at DESC, a.id DESC
            </script>
            """)
    List<AlertQueryRow> findAllByConditions(
            @Param("landId") String landId,
            @Param("type") String type,
            @Param("severity") String severity,
            @Param("status") String status);

    @Select("""
            SELECT
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
            FROM alerts
            WHERE land_id = #{landId}
              AND status IN ('pending', 'processing')
            ORDER BY occurred_at DESC, id DESC
            """)
    List<Alert> findActiveByLandId(@Param("landId") String landId);

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

    @Select("""
            SELECT *
            FROM alerts
            WHERE id = #{alertId}
            FOR UPDATE
            """)
    Optional<Alert> findByIdForUpdate(@Param("alertId") String alertId);

    @Update("""
            UPDATE alerts
            SET status = 'processing',
                updated_at = UTC_TIMESTAMP(3)
            WHERE status = 'pending'
              AND id = #{alertId}
            """)
    int startIfPending(@Param("alertId") String alertId);

    @Update("""
            UPDATE alerts
            SET status = 'resolved',
                handle_measure = #{measure},
                handled_at = #{handledAt},
                handle_result = #{result},
                handle_remark = #{remark},
                handle_operator_id = #{operatorId},
                updated_at = #{updatedAt}
            WHERE id = #{alertId}
              AND status = 'processing'
            """)
    int resolveIfProcessing(
            @Param("alertId") String alertId,
            @Param("measure") String measure,
            @Param("handledAt") LocalDateTime handledAt,
            @Param("result") String result,
            @Param("remark") String remark,
            @Param("operatorId") String operatorId,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE alerts
            SET status = 'ignored',
                handle_measure = '无需处理',
                handled_at = #{handledAt},
                handle_result = '已忽略',
                handle_remark = #{remark},
                handle_operator_id = #{operatorId},
                updated_at = #{updatedAt}
            WHERE id = #{alertId}
              AND status = 'pending'
            """)
    int ignoreIfPending(
            @Param("alertId") String alertId,
            @Param("remark") String remark,
            @Param("operatorId") String operatorId,
            @Param("handledAt") LocalDateTime handledAt,
            @Param("updatedAt") LocalDateTime updatedAt);

}
