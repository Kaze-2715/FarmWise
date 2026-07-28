package com.farmwise.planting.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.planting.model.PlantingPlan;

@Mapper
public interface PlantingPlanMapper {

    @Select("""
            SELECT
                id,
                land_id,
                plan_name,
                crop_type,
                area,
                planting_date,
                expected_harvest_date,
                status,
                remark,
                created_at,
                updated_at
            FROM planting_plans
            WHERE land_id = #{landId}
            ORDER BY created_at DESC, id DESC
            """)
    List<PlantingPlan> findAllByLandId(@Param("landId") String landId);

    @Select("""
            SELECT
                pp.id,
                pp.land_id,
                pp.plan_name,
                pp.crop_type,
                pp.area,
                pp.planting_date,
                pp.expected_harvest_date,
                pp.status,
                pp.remark,
                pp.created_at,
                pp.updated_at
            FROM planting_plans pp
            JOIN lands l ON l.id = pp.land_id
            WHERE pp.id = #{planId}
              AND l.owner_id = #{ownerId}
            """)
    Optional<PlantingPlan> findByIdAndOwnerId(
            @Param("planId") String planId,
            @Param("ownerId") String ownerId);

    @Select("""
            SELECT
                pp.id,
                pp.land_id,
                pp.plan_name,
                pp.crop_type,
                pp.area,
                pp.planting_date,
                pp.expected_harvest_date,
                pp.status,
                pp.remark,
                pp.created_at,
                pp.updated_at
            FROM planting_plans pp
            JOIN lands l ON l.id = pp.land_id
            WHERE pp.id = #{planId}
              AND l.owner_id = #{ownerId}
            FOR UPDATE
            """)
    Optional<PlantingPlan> findByIdAndOwnerIdForUpdate(
            @Param("planId") String planId,
            @Param("ownerId") String ownerId);

    @Insert("""
            INSERT INTO planting_plans (
                id,
                land_id,
                plan_name,
                crop_type,
                area,
                planting_date,
                expected_harvest_date,
                status,
                remark,
                created_at,
                updated_at
            ) VALUES (
                #{id},
                #{landId},
                #{planName},
                #{cropType},
                #{area},
                #{plantingDate},
                #{expectedHarvestDate},
                #{status},
                #{remark},
                #{createdAt},
                #{updatedAt}
            )
            """)
    int add(PlantingPlan plantingPlan);

    @Update("""
            UPDATE planting_plans
            SET land_id = #{landId},
                plan_name = #{planName},
                crop_type = #{cropType},
                area = #{area},
                planting_date = #{plantingDate},
                expected_harvest_date = #{expectedHarvestDate},
                remark = #{remark},
                updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    int update(PlantingPlan plantingPlan);

    @Delete("""
            DELETE FROM planting_plans
            WHERE id = #{planId}
            """)
    int delete(
        @Param("planId") String planId
    );

    @Update("""
            UPDATE planting_plans
            SET status = #{status},
            updated_at = #{updatedAt}
            WHERE id = #{planId}
            """)
    int updateStatus(
        @Param("planId") String planId,
        @Param("status") String status,
        @Param("updatedAt") LocalDateTime updatedAt);
}
