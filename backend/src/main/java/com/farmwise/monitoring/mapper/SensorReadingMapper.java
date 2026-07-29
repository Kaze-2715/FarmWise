package com.farmwise.monitoring.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.farmwise.device.model.SensorReading;

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
                AND recorded_at <= #{endedAt}
            </if>
            ORDER BY recorded_at ASC
            </script>
            """)
    List<SensorReading> findByLandAndMetricAndTime(
            @Param("landId") String landId,
            @Param("metric") String metric,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("endedAt") LocalDateTime endedAt
    );
}
