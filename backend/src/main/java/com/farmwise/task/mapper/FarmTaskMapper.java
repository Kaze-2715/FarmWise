package com.farmwise.task.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.task.model.FarmTask;

@Mapper
public interface FarmTaskMapper {
    @Select("""
            <script>
            SELECT
                id,
                land_id,
                source_type,
                source_id,
                task_type,
                title,
                description,
                priority,
                status,
                assignee_id,
                deadline,
                created_at,
                completed_at,
                result,
                remark,
                updated_at
            FROM farm_tasks
            WHERE land_id = #{landId}
            <if test="taskType != null">
                AND task_type = #{taskType}
            </if>
            <if test="priority != null">
                AND priority = #{priority}
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
            ORDER BY created_at DESC, id DESC
            </script>
            """)
    List<FarmTask> findAllByConditions(
            @Param("landId") String landId,
            @Param("taskType") String taskType,
            @Param("priority") String priority,
            @Param("status") String status);

    @Select("""
            SELECT
                id,
                land_id,
                source_type,
                source_id,
                task_type,
                title,
                description,
                priority,
                status,
                assignee_id,
                deadline,
                created_at,
                completed_at,
                result,
                remark,
                updated_at
            FROM farm_tasks
            WHERE land_id = #{landId}
              AND status IN ('pending', 'processing')
            ORDER BY deadline IS NULL, deadline, created_at DESC, id DESC
            """)
    List<FarmTask> findActiveByLandId(@Param("landId") String landId);

    @Select("""
            SELECT EXISTS (
                SELECT *
                FROM farm_tasks
                WHERE source_type = #{sourceType}
                AND source_id = #{sourceId}
            )""")
    boolean existsBySourceTypeAndSourceId(
            @Param("sourceType") String sourceType,
            @Param("sourceId") String sourceId);

    @Insert("""
            INSERT INTO farm_tasks (
                id,
                land_id,
                source_type,
                source_id,
                task_type,
                title,
                description,
                priority,
                status,
                assignee_id,
                deadline,
                created_at,
                completed_at,
                result,
                remark,
                updated_at
            ) VALUES (
                #{id},
                #{landId},
                #{sourceType},
                #{sourceId},
                #{taskType},
                #{title},
                #{description},
                #{priority},
                #{status},
                #{assigneeId},
                #{deadline},
                #{createdAt},
                #{completedAt},
                #{result},
                #{remark},
                #{updatedAt}
            )
            """)
    int addTask(FarmTask task);

    @Select("""
            SELECT t.*
            FROM farm_tasks t
            JOIN lands l
              ON t.land_id = l.id
            WHERE t.id = #{taskId}
              AND l.owner_id = #{ownerId}
            FOR UPDATE
            """)
    Optional<FarmTask> findByIdAndOwnerIdForUpdate(
            @Param("taskId") String taskId,
            @Param("ownerId") String ownerId);

    @Update("""
            UPDATE farm_tasks
            SET status = 'processing',
                updated_at = #{updatedAt}
            WHERE id = #{taskId}
              AND status = 'pending'
            """)
    int startIfPending(
            @Param("taskId") String taskId,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE farm_tasks
            SET status = 'completed',
                completed_at = #{completedAt},
                result = #{result},
                updated_at = #{completedAt}
            WHERE id = #{taskId}
              AND status = 'processing'
            """)
    int completeIfProcessing(
            @Param("taskId") String taskId,
            @Param("result") String result,
            @Param("completedAt") LocalDateTime completedAt);

    @Update("""
            UPDATE farm_tasks
            SET status = 'cancelled',
                remark = #{reason},
                updated_at = #{updatedAt}
            WHERE id = #{taskId}
              AND status IN ('pending', 'processing')
            """)
    int cancelIfActive(
            @Param("taskId") String taskId,
            @Param("reason") String reason,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Select("""
            SELECT EXISTS (
                SELECT 1
                FROM farm_tasks
                WHERE source_type = 'alert'
                  AND source_id = #{alertId}
                  AND status IN ('pending', 'processing')
            )""")
    boolean existsActiveByAlertId(
            @Param("alertId") String alertId);
}
