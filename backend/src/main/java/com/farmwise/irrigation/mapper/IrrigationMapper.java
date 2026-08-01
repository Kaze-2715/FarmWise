package com.farmwise.irrigation.mapper;

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

import com.farmwise.irrigation.dto.IrrigationConfigControllerRow;
import com.farmwise.irrigation.dto.IrrigationConfigRow;
import com.farmwise.irrigation.model.IrrigationConfig;
import com.farmwise.irrigation.model.IrrigationRecord;

@Mapper
public interface IrrigationMapper {
    @Select("""
            SELECT *
            FROM irrigation_records
            WHERE id = #{recordId}
            FOR UPDATE
            """)
    Optional<IrrigationRecord> findByIdForUpdate(@Param("recordId") String recordId);

    @Update("""
            UPDATE irrigation_records
            SET status = 'running',
            started_at = #{occurredAt},
            updated_at = UTC_TIMESTAMP(3)
            WHERE id = #{recordId}
            AND status = 'pending'
            """)
    int markRunning(@Param("recordId") String recordId, @Param("occurredAt") LocalDateTime occuredAt);

    @Update("""
            UPDATE irrigation_records
            SET status = 'completed',
            started_at = COALESCE(started_at, #{inferredStartedAt}),
            ended_at = #{endedAt},
            duration = #{duration},
            water_usage = #{waterUsage},
            updated_at = UTC_TIMESTAMP(3)
            WHERE id = #{recordId}
            AND status in ('pending', 'running')
            """)
    int markCompleted(
            @Param("recordId") String recordId,
            @Param("inferredStartedAt") LocalDateTime inferredStartedAt,
            @Param("endedAt") LocalDateTime endedAt,
            @Param("duration") int duration,
            @Param("waterUsage") BigDecimal waterUsage);

    @Update("""
            UPDATE irrigation_records
            SET status = 'failed',
            ended_at = #{endedAt},
            updated_at = UTC_TIMESTAMP(3)
            WHERE id = #{recordId}
            AND status in ('pending', 'running')
            """)
    int markFailed(
            @Param("recordId") String recordId,
            @Param("endedAt") LocalDateTime endedAt);

    @Insert("""
            <script>
                INSERT INTO irrigation_records (
                    id,
                    batch_id,
                    land_id,
                    controller_device_id,
                    source,
                    status,
                    started_at,
                    planned_duration,
                    trigger_reason,
                    operator_id,
                    created_at,
                    updated_at
                ) VALUES
                <foreach collection="records" item="record" separator=",">
                (
#{record.id },
#{record.batchId },
#{record.landId },
#{record.controllerDeviceId },
#{record.source },
#{record.status },
#{record.startedAt },
#{record.plannedDuration },
#{record.triggerReason },
#{record.operatorId },
                    UTC_TIMESTAMP(3),
                    UTC_TIMESTAMP(3)
                )
                </foreach>
                </script>
                """)
    int addIrrigationRecord(
            @Param("records") List<IrrigationRecord> records);

    @Select("""
            SELECT ir.*
            FROM irrigation_records ir
            JOIN lands l ON l.id = ir.land_id
            WHERE ir.id = #{recordId}
            AND l.owner_id = #{
        userId}
            """)
    Optional<IrrigationRecord> findByIdAndOwnerId(
            @Param("recordId") String recordId,
            @Param("userId") String userId);

    @Insert("""
            INSERT INTO irrigation_configs (
                id,
                land_id,
                name,
                mode,
                enabled,
                trigger_moisture,
                target_moisture,
                default_duration,
                updated_by,
                updated_at
            ) VALUES (
#{id },
#{landId },
#{name },
#{mode },
#{enabled },
#{triggerMoisture },
#{targetMoisture },
#{defaultDuration },
#{updatedBy },
#{updatedAt }
            )
            """)
    int addConfig(IrrigationConfig config);

    @Select("""
            SELECT EXISTS (
                SELECT 1
                FROM irrigation_configs
                WHERE id = #{configId}
                AND land_id = #{landId}
            )""")
    boolean existsConfigByIdAndLandId(
            @Param("configId") String configId,
            @Param("landId") String landId);

    @Select("""
            SELECT EXISTS (
                SELECT 1
                FROM irrigation_records
                WHERE land_id = #{landId}
                AND status in ('pending', 'running')
            )""")
    boolean existsActiveRecordByLandId(
            @Param("landId") String landId);

    @Update("""
            UPDATE irrigation_configs
            SET enabled = FALSE,
                updated_by = #{updatedBy},
                updated_at = #{updatedAt}
            WHERE land_id = #{landId}
              AND enabled = TRUE
            """)
    int disableConfigsByLandId(
            @Param("landId") String landId,
            @Param("updatedBy") String updatedBy,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Insert("""
            INSERT INTO irrigation_config_controllers (
                config_id,
                controller_device_id,
                created_at
            ) VALUES (
#{configId },
#{controllerDeviceId },
#{createdAt }
            )
            """)
    int addConfigController(
            @Param("configId") String configId,
            @Param("controllerDeviceId") String controllerDeviceId,
            @Param("createdAt") LocalDateTime createdAt);

    @Delete("""
            DELETE
            FROM irrigation_configs
            WHERE id = #{configId}
              AND land_id = #{
        landId}
            """)
    int deleteConfigByIdAndLandId(
            @Param("configId") String configId,
            @Param("landId") String landId);

    @Select("""
            SELECT
                id,
                land_id,
                name,
                mode,
                enabled,
                trigger_moisture,
                target_moisture,
                default_duration,
                updated_by,
                updated_at
            FROM irrigation_configs
            WHERE land_id = #{landId}
            ORDER BY updated_at DESC, id DESC
            """)
    List<IrrigationConfigRow> findConfigRowsByLandId(
            @Param("landId") String landId);

    @Select("""
            SELECT
                controller.config_id,
                controller.controller_device_id
            FROM irrigation_config_controllers controller
            JOIN irrigation_configs config
            ON config.id = controller.config_id
            WHERE config.land_id = #{landId}
            ORDER BY
                controller.created_at,
                controller.controller_device_id
            """)
    List<IrrigationConfigControllerRow> findConfigControllerRowsByLandId(
            @Param("landId") String landId);

    @Update("""
            UPDATE irrigation_configs
            SET name = #{name},
                mode = #{mode},
                enabled = #{enabled},
                trigger_moisture = #{triggerMoisture},
                target_moisture = #{targetMoisture},
                default_duration = #{defaultDuration},
                updated_by = #{updatedBy},
                updated_at = #{updatedAt}
            WHERE id = #{id}
            AND land_id = #{
        landId}
            """)
    int updateConfig(IrrigationConfig config);

    @Delete("""
            DELETE FROM irrigation_config_controllers
            WHERE config_id = #{
        configId}
            """)
    int deleteConfigControllersByConfigId(
            @Param("configId") String configId);

    @Update("""
            UPDATE irrigation_configs
            SET enabled = TRUE,
                updated_by = #{updatedBy},
                updated_at = #{updatedAt}
            WHERE id = #{configId}
            AND land_id = #{
        landId}
            """)
    int enableConfig(
            @Param("landId") String landId,
            @Param("configId") String configId,
            @Param("updatedBy") String updatedBy,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Select("""
            SELECT
                id,
                land_id,
                name,
                mode,
                enabled,
                trigger_moisture,
                target_moisture,
                default_duration,
                updated_by,
                updated_at
            FROM irrigation_configs
            WHERE land_id = #{landId}
              AND enabled = TRUE
              AND mode = 'automatic'
            """)
    Optional<IrrigationConfigRow> findEnabledAutomaticConfig(@Param("landId") String landId);

    @Select("""
            SELECT
                id,
                land_id,
                name,
                mode,
                enabled,
                trigger_moisture,
                target_moisture,
                default_duration,
                updated_by,
                updated_at
            FROM irrigation_configs
            WHERE id = #{configId}
            AND land_id = #{landId}
            AND enabled = TRUE
            AND mode = 'automatic'
            FOR UPDATE
            """)
    Optional<IrrigationConfigRow> findEnabledAutomaticConfigForUpdate(
            @Param("configId") String configId,
            @Param("landId") String landId);

    @Select("""
            SELECT
                id,
                batch_id,
                land_id,
                controller_device_id,
                source,
                status,
                started_at,
                ended_at,
                planned_duration,
                duration,
                water_usage,
                trigger_reason,
                operator_id,
                created_at,
                updated_at
            FROM irrigation_records
            WHERE land_id = #{landId}
              AND source = 'automatic'
              AND status IN ('pending', 'running')
            ORDER BY created_at, id
            """)
    List<IrrigationRecord> findActiveAutomaticRecordsByLandId(@Param("landId") String landId);

    @Select("""
            SELECT
                id,
                batch_id,
                land_id,
                controller_device_id,
                source,
                status,
                started_at,
                ended_at,
                planned_duration,
                duration,
                water_usage,
                trigger_reason,
                operator_id,
                created_at,
                updated_at
            FROM irrigation_records
            WHERE land_id = #{landId}
            ORDER BY created_at DESC, id DESC
            LIMIT 10
            """)
    List<IrrigationRecord> findRecentRecordsByLandId(@Param("landId") String landId);
}
