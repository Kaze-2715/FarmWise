package com.farmwise.monitoring.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.monitoring.dto.EnvironmentThresholdRow;
import com.farmwise.monitoring.model.EnvironmentThreshold;

@Mapper
public interface EnvironmentThresholdMapper {
    @Select("""
            SELECT
                et.land_id,
                et.metric,
                et.min_value,
                et.max_value,
                et.enabled,
                COALESCE(NULLIF(u.real_name, ''), u.username) AS creator,
                et.updated_at
            FROM environment_thresholds et
            JOIN users u
            ON et.creator_id = u.id
            WHERE et.land_id = #{landId}
            ORDER BY et.metric ASC
            """)
    List<EnvironmentThresholdRow> findAllByLandId(
            @Param("landId") String landId);

    @Select("""
            SELECT
                et.land_id,
                et.metric,
                et.min_value,
                et.max_value,
                et.enabled,
                COALESCE(NULLIF(u.real_name, ''), u.username) AS creator,
                et.updated_at
            FROM environment_thresholds et
            JOIN users u
            ON et.creator_id = u.id
            WHERE et.land_id = #{landId}
            AND et.metric = #{metric}
            """)
    EnvironmentThresholdRow findByMetricAndLandId(
            @Param("landId") String landId,
            @Param("metric") String metric);

    @Insert("""
            INSERT INTO environment_thresholds (
                land_id,
                metric,
                min_value,
                max_value,
                enabled,
                creator_id,
                updated_at
            ) VALUE (
                #{landId},
                #{metric},
                #{minValue},
                #{maxValue},
                #{enabled},
                #{creatorId},
                #{updatedAt}
            )""")
    int addThreshold(EnvironmentThreshold threshold);

    @Update("""
            UPDATE environment_thresholds
            SET min_value = #{min},
                max_value = #{max},
                enabled = #{enabled},
                updated_at = #{updatedAt}
            WHERE land_id = #{landId}
              AND metric = #{metric}
            """)
    int updateThreshold(
            @Param("landId") String landId,
            @Param("metric") String metric,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max,
            @Param("enabled") Boolean enabled,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Delete("""
            DELETE FROM environment_thresholds
            WHERE land_id = #{landId}
            AND metric = #{metric}
            """)
    int deleteByLandIdAndMetric(
            @Param("landId") String landId,
            @Param("metric") String metric);
}
