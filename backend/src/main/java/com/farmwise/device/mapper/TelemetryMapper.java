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
}
