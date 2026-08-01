package com.farmwise.device.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.farmwise.device.model.SensorReading;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TelemetryMapper {
    @Insert("""
            INSERT INTO telemetry_messages (
                device_id,
                message_id,
                reported_at,
                received_at
            ) VALUES (
                #{deviceId },
                #{messageId },
                #{reportedAt },
                UTC_TIMESTAMP(3)
            )
            """)
    int recordMessage(
            @Param("deviceId") String deviceId,
            @Param("messageId") String messageId,
            @Param("reportedAt") LocalDateTime reportedAt);

    @Insert("""
            <script>
            INSERT INTO sensor_readings (
                device_id,
                land_id,
                recorded_at,
                metric,
                unit,
                value,
                created_at
            ) VALUES
            <foreach collection="readings" item="reading" separator=",">
                (
                    #{reading.deviceId },
                    #{reading.landId },
                    #{reading.recordedAt },
                    #{reading.metric },
                    #{reading.unit },
                    #{reading.value },
                    UTC_TIMESTAMP(3)
                )
            </foreach>
            </script>
            """)
    int insertReadings(@Param("readings") List<SensorReading> readings);

    @Insert("""
            <script>
            INSERT INTO latest_sensor_readings (
                device_id,
                metric,
                land_id,
                recorded_at,
                unit,
                value,
                updated_at
            ) VALUES
            <foreach collection="readings" item="reading" separator=",">
                (
                    #{reading.deviceId},
                    #{reading.metric},
                    #{reading.landId},
                    #{reading.recordedAt},
                    #{reading.unit},
                    #{reading.value},
                    UTC_TIMESTAMP(3)
                )
            </foreach>
            AS incoming
            ON DUPLICATE KEY UPDATE
                land_id = IF(
                    incoming.recorded_at &gt;= latest_sensor_readings.recorded_at,
                    incoming.land_id,
                    latest_sensor_readings.land_id
                ),
                unit = IF(
                    incoming.recorded_at &gt;= latest_sensor_readings.recorded_at,
                    incoming.unit,
                    latest_sensor_readings.unit
                ),
                value = IF(
                    incoming.recorded_at &gt;= latest_sensor_readings.recorded_at,
                    incoming.value,
                    latest_sensor_readings.value
                ),
                updated_at = IF(
                    incoming.recorded_at &gt;= latest_sensor_readings.recorded_at,
                    UTC_TIMESTAMP(3),
                    latest_sensor_readings.updated_at
                ),
                recorded_at = GREATEST(
                    latest_sensor_readings.recorded_at,
                    incoming.recorded_at
                )
            </script>
            """)
    int upsertLatestReadings(@Param("readings") List<SensorReading> readings);
}
