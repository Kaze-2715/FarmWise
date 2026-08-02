package com.farmwise.ai.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.farmwise.ai.model.Conversation;
import com.farmwise.ai.model.ConversationMessage;
import com.farmwise.report.dto.AiAdviceSnapshotRow;

@Mapper
public interface ConversationMapper {
    @Select("""
            SELECT
                id,
                land_id,
                title,
                status,
                created_by,
                created_at,
                updated_at
            FROM ai_conversations
            WHERE land_id = #{landId}
            AND status = #{status}
            ORDER BY updated_at DESC, created_at DESC
            """)
    List<Conversation> findAllByLandIdAndStatus(
            @Param("landId") String landId,
            @Param("status") String status);

    @Select("""
            <script>
            SELECT
                id,
                conversation_id,
                role,
                content,
                references_json,
                task_draft_json,
                created_at
            FROM ai_messages
            WHERE conversation_id IN
            <foreach collection="conversationIds"
                     item="conversationId"
                     open="("
                     separator=","
                     close=")">
                #{conversationId}
            </foreach>
            ORDER BY conversation_id, created_at ASC, id ASC
            </script>
            """)
    List<ConversationMessage> findMessagesByConversationIds(
            @Param("conversationIds") List<String> conversationIds);

    @Insert("""
            INSERT INTO ai_conversations (
                id,
                land_id,
                title,
                status,
                created_by,
                created_at,
                updated_at
            ) VALUES (
                #{id},
                #{landId},
                #{title},
                #{status},
                #{createdBy},
                #{createdAt},
                #{updatedAt}
            )
            """)
    int addConversation(Conversation conversation);

    @Select("""
            SELECT
                ac.id,
                ac.land_id,
                ac.title,
                ac.status,
                ac.created_by,
                ac.created_at,
                ac.updated_at
            FROM ai_conversations ac
            JOIN lands l ON l.id = ac.land_id
            WHERE ac.id = #{conversationId}
              AND l.owner_id = #{userId}
            """)
    Optional<Conversation> findConversationByIdAndOwnerId(
            @Param("conversationId") String conversationId,
            @Param("userId") String userId);

    @Select("""
            SELECT
                id,
                conversation_id,
                role,
                content,
                references_json,
                task_draft_json,
                created_at
            FROM ai_messages
            WHERE conversation_id = #{conversationId}
            ORDER BY created_at DESC, id DESC
            LIMIT #{limit}
            """)
    List<ConversationMessage> findRecentMessagesByConversationId(
            @Param("conversationId") String conversationId,
            @Param("limit") int limit);

    @Select("""
            SELECT
                ac.id,
                ac.land_id,
                ac.title,
                ac.status,
                ac.created_by,
                ac.created_at,
                ac.updated_at
            FROM ai_conversations ac
            JOIN lands l ON l.id = ac.land_id
            WHERE ac.id = #{conversationId}
            AND l.owner_id = #{userId}
            FOR UPDATE OF ac
            """)
    Optional<Conversation> findConversationByIdAndOwnerIdForUpdate(
            @Param("conversationId") String conversationId,
            @Param("userId") String userId);

    @Insert("""
            <script>
            INSERT INTO ai_messages (
                id,
                conversation_id,
                role,
                content,
                references_json,
                task_draft_json,
                created_at
            ) VALUES
            <foreach collection="messages"
                     item="message"
                     separator=",">
                (
                    #{message.id},
                    #{message.conversationId},
                    #{message.role},
                    #{message.content},
                    #{message.referencesJson},
                    #{message.taskDraftJson},
                    #{message.createdAt}
                )
            </foreach>
            </script>
            """)
    int addMessages(
            @Param("messages") List<ConversationMessage> messages);

    @Update("""
            UPDATE ai_conversations
            SET updated_at = #{updatedAt}
            WHERE id = #{conversationId}
            """)
    int updateUpdatedAt(
            @Param("conversationId") String conversationId,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Select("""
            SELECT
                id,
                conversation_id,
                role,
                content,
                references_json,
                task_draft_json,
                created_at
            FROM ai_messages
            WHERE id = #{messageId}
              AND conversation_id = #{conversationId}
            FOR UPDATE
            """)
    Optional<ConversationMessage> findMessageByIdAndConversationIdForUpdate(
            @Param("conversationId") String conversationId,
            @Param("messageId") String messageId);

    @Update("""
            UPDATE ai_conversations
            SET status = 'closed',
                updated_at = #{updatedAt}
            WHERE id = #{conversationId}
              AND status = 'active'
            """)
    int closeIfActive(
            @Param("conversationId") String conversationId,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Select("""
            SELECT
                m.id AS message_id,
                m.content,
                m.references_json,
                m.created_at
            FROM ai_messages m
            JOIN ai_conversations c
              ON c.id = m.conversation_id
            WHERE c.land_id = #{landId}
              AND m.role = 'assistant'
              AND m.created_at >= #{startAt}
              AND m.created_at < #{endAt}
            ORDER BY m.created_at ASC, m.id ASC
            """)
    List<AiAdviceSnapshotRow> snapshot(
            @Param("landId") String landId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);
}
