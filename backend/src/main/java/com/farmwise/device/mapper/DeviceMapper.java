package com.farmwise.device.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.device.model.Device;

@Mapper
public interface DeviceMapper {
    @Select("""
            <script>
            SELECT *
            FROM devices
            <where>
                owner_id = #{ownerId}
                <if test="landId != null">
                    AND land_id = #{landId}
                </if>
                <if test="deviceType != null">
                    AND device_type = #{deviceType}
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
                <if test="keyword != null">
                    AND name LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY created_at DESC, id DESC
            </script>
            """)
    List<Device> findAllByOwnerId(
            @Param("ownerId") String ownerId,
            @Param("landId") String landId,
            @Param("deviceType") String deviceType,
            @Param("status") String status,
            @Param("keyword") String keyword);

    @Insert("""
            INSERT INTO devices (
                id,
                owner_id,
                land_id,
                name,
                device_type,
                status,
                battery,
                last_reported_at,
                model,
                install_date,
                longitude,
                latitude,
                created_at,
                updated_at
            ) VALUES (
#{id },
#{ownerId },
#{landId },
#{name },
#{deviceType },
#{status },
#{battery },
#{lastReportedAt },
#{model },
#{installDate },
#{longitude },
#{latitude },
#{createdAt },
#{updatedAt }
            )
            """)
    int addDevice(Device device);

    @Select("""
            SELECT *
            FROM devices
            WHERE id = #{deviceId}
            AND owner_id = #{
        ownerId}
            """)
    Optional<Device> findByIdAndOwnerId(
            @Param("deviceId") String deviceId,
            @Param("ownerId") String ownerId);

    @Update("""
            UPDATE devices
            SET name = #{name},
            device_type = #{deviceType},
            land_id = #{landId},
            model = #{model},
            install_date = #{installDate},
            longitude = #{longitude},
            latitude = #{latitude},
            updated_at = #{updatedAt}
            WHERE id = #{id}
            AND owner_id = #{
        ownerId}
            """)
    int updateDevice(Device device);

    @Delete("""
            DELETE FROM devices
            WHERE id = #{deviceId}
            AND  owner_id = #{
        ownerId}
            """)
    int deleteByIdAndOwnerId(
            @Param("deviceId") String deviceId,
            @Param("ownerId") String ownerId);

    @Select("""
            SELECT *
            FROM devices
            WHERE id = #{
        deviceId}
            """)
    Optional<Device> findById(
            @Param("deviceId") String deviceId);

    @Update("""
            UPDATE devices
            SET status = 'online',
                battery = CASE
                    WHEN last_reported_at IS NULL OR last_reported_at < #{reportedAt}
                    THEN COALESCE(#{battery}, battery)
                    ELSE battery
                END,
                last_reported_at = CASE
                    WHEN last_reported_at IS NULL
                             OR last_reported_at < #{reportedAt}
                    THEN #{reportedAt}
                    ELSE last_reported_at
                END,
                updated_at = UTC_TIMESTAMP(3)
            WHERE id = #{
        deviceId}
            """)
    int updateStateFromTelemetry(
            @Param("deviceId") String deviceId,
            @Param("battery") BigDecimal battery,
            @Param("reportedAt") LocalDateTime reportedAt);

    @Update("""
            UPDATE devices
            SET status = #{status},
                updated_at = UTC_TIMESTAMP(3)
            WHERE id = #{
        deviceId}
            """)
    int updateStatus(@Param("deviceId") String deviceId, @Param("status") String status);

    @Update("""
            UPDATE devices
            SET status = 'offline',
                updated_at = UTC_TIMESTAMP(3)
            WHERE status = 'online'
              AND last_reported_at IS NOT NULL
              AND last_reported_at < #{
        cutoff}
            """)
    int markTimedOutDevicesOffline(
            @Param("cutoff") LocalDateTime cutoff);

    @Select("""
            SELECT
                device.id,
                device.owner_id,
                device.land_id,
                device.name,
                device.device_type,
                device.status,
                device.battery,
                device.last_reported_at,
                device.model,
                device.install_date,
                device.longitude,
                device.latitude,
                device.created_at,
                device.updated_at
            FROM devices device
            JOIN irrigation_config_controllers controller
            ON controller.controller_device_id = device.id
            WHERE controller.config_id = #{configId}
            ORDER BY controller.created_at, device.id
            """)
    List<Device> findByIrrigationConfigId(@Param("configId") String configId);
}
