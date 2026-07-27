ALTER TABLE reports
    DROP COLUMN creator_name;

ALTER TABLE farm_tasks
    DROP COLUMN assignee,
    ADD COLUMN assignee_id CHAR(36) NOT NULL
        AFTER status,
    ADD CONSTRAINT fk_farm_tasks_assignee
        FOREIGN KEY (assignee_id)
        REFERENCES users (id)
        ON DELETE RESTRICT,
    ADD INDEX idx_farm_tasks_assignee_status (
        assignee_id,
        status
    );
