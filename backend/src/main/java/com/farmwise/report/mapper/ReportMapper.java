package com.farmwise.report.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.report.model.Report;
import com.farmwise.report.model.ReportSummary;

@Mapper
public interface ReportMapper {
    @Select("""
            <script>
            SELECT
                r.id,
                r.land_id,
                r.type,
                r.title,
                r.start_date,
                r.end_date,
                r.status,
                r.creator_id,
                r.created_at,
                r.generated_at,
                r.summary
            FROM reports r
            JOIN lands l ON l.id = r.land_id
            WHERE l.owner_id = #{ownerId}
            <if test="landId != null">
                AND r.land_id = #{landId}
            </if>
            <if test="type != null">
                AND r.type = #{type}
            </if>
            <if test="status != null">
                AND r.status = #{status}
            </if>
            <if test="startDate != null">
                AND r.end_date &gt;= #{startDate}
            </if>
            <if test="endDate != null">
                AND r.start_date &lt;= #{endDate}
            </if>
            <if test="keyword != null">
                AND r.title LIKE CONCAT('%', #{keyword}, '%')
            </if>
            ORDER BY r.generated_at DESC, r.id DESC
            </script>
            """)
    List<ReportSummary> findAllByConditions(
            @Param("ownerId") String ownerId,
            @Param("landId") String landId,
            @Param("type") String type,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword);

    @Select("""
            SELECT
                r.id,
                r.land_id,
                r.type,
                r.title,
                r.start_date,
                r.end_date,
                r.status,
                r.creator_id,
                r.created_at,
                r.generated_at,
                r.summary,
                r.snapshot_json
            FROM reports r
            JOIN lands l ON l.id = r.land_id
            WHERE r.id = #{reportId}
              AND l.owner_id = #{ownerId}
            """)
    Optional<Report> findByIdAndOwnerId(
            @Param("reportId") String reportId,
            @Param("ownerId") String ownerId);

    @Select("""
            SELECT
                r.id,
                r.land_id,
                r.type,
                r.title,
                r.start_date,
                r.end_date,
                r.status,
                r.creator_id,
                r.created_at,
                r.generated_at,
                r.summary
            FROM reports r
            JOIN lands l ON l.id = r.land_id
            WHERE r.id = #{reportId}
              AND l.owner_id = #{ownerId}
            FOR UPDATE OF r
            """)
    Optional<ReportSummary> findSummaryByIdAndOwnerIdForUpdate(
            @Param("reportId") String reportId,
            @Param("ownerId") String ownerId);

    @Insert("""
            INSERT INTO reports (
                id,
                land_id,
                type,
                title,
                start_date,
                end_date,
                status,
                creator_id,
                created_at,
                generated_at,
                summary,
                snapshot_json
            ) VALUES (
                #{id},
                #{landId},
                #{type},
                #{title},
                #{startDate},
                #{endDate},
                #{status},
                #{creatorId},
                #{createdAt},
                #{generatedAt},
                #{summary},
                #{snapshotJson}
            )
            """)
    int addReport(Report report);

    @Update("""
            UPDATE reports
            SET status = 'archived'
            WHERE id = #{reportId}
              AND status = 'generated'
            """)
    int archiveIfGenerated(@Param("reportId") String reportId);
}
