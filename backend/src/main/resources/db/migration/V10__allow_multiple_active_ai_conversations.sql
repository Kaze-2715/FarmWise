ALTER TABLE ai_conversations
    DROP INDEX uk_ai_conversations_active_land,
    DROP COLUMN active_land_id;
