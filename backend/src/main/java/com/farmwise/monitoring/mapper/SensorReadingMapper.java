package com.farmwise.monitoring.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.farmwise.device.model.SensorReading;
import com.farmwise.monitoring.dto.LatestSensorReadingRow;
import com.farmwise.report.dto.ReportSnapshotResponse.EnvironmentSnapshot;

@Mapper
public interface SensorReadingMapper {
    @Select("""
            <script>
            SELECT
                device_id,
                land_id,
                recorded_at,
                metric,
                unit,
                value
            FROM sensor_readings
            WHERE land_id = #{landId}
            <if test="metric != null">
                AND metric = #{metric}
            </if>
            <if test="startedAt != null">
                AND recorded_at >= #{startedAt}
            </if>
            <if test="endedAt != null">
                AND recorded_at &lt;= #{endedAt}
            </if>
            ORDER BY recorded_at ASC
            </script>
            """)
    List<SensorReading> findByLandAndMetricAndTime(
            @Param("landId") String landId,
            @Param("metric") String metric,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("endedAt") LocalDateTime endedAt);

    @Select("""
            SELECT device_id,
                land_id,
                recorded_at,
                metric,
                unit,
                value
            FROM sensor_readings
            WHERE land_id = #{landId}
            AND device_id = #{deviceId}
            AND metric = #{metric}
            AND recorded_at > #{startedAt}
            AND recorded_at < #{endedAt}
            ORDER BY recorded_at ASC, id ASC
            """)
    List<SensorReading> findForAlertStateRebuild(
            @Param("landId") String landId,
            @Param("deviceId") String deviceId,
            @Param("metric") String metric,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("endedAt") LocalDateTime endedAt);

    @Select("""
            SELECT
                latest.device_id,
                device.name AS device_name,
                latest.land_id,
                latest.recorded_at,
                latest.metric,
                latest.unit,
                latest.value
            FROM latest_sensor_readings latest
            JOIN devices device
              ON device.id = latest.device_id
             AND device.land_id = latest.land_id
            WHERE latest.land_id = #{landId}
            ORDER BY latest.metric ASC, latest.device_id ASC
            """)
    List<LatestSensorReadingRow> findLatestByLandId(
            @Param("landId") String landId);

    @Select("""
            WITH ranked_readings AS (
                SELECT
                    sr.id,
                    sr.land_id,
                    sr.metric,
                    sr.value,
                    sr.unit,
                    sr.recorded_at,
                    ROW_NUMBER() OVER (
                        PARTITION BY sr.metric
                        ORDER BY sr.recorded_at DESC, sr.id DESC
                    ) AS rn
                FROM sensor_readings sr
                WHERE sr.land_id = #{landId}
                AND sr.recorded_at >= #{startAt}
                AND sr.recorded_at < #{endAt}
            )
            SELECT
                rr.metric,
                rr.value,
                rr.unit,
                CASE
                    WHEN et.metric IS NULL THEN 'unconfigured'
                    WHEN rr.value < et.min_value THEN 'low'
                    WHEN rr.value > et.max_value THEN 'high'
                    ELSE 'normal'
                END AS status,
                rr.recorded_at
            FROM ranked_readings rr
            LEFT JOIN environment_thresholds et
            ON et.land_id = rr.land_id
            AND et.metric = rr.metric
            AND et.enabled = TRUE
            WHERE rr.rn = 1
            ORDER BY rr.metric
            """)
    List<EnvironmentSnapshot> snapshot(
            @Param("landId") String landId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);
}
