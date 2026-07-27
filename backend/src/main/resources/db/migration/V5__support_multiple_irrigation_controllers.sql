CREATE TABLE irrigation_config_controllers (
    land_id CHAR(36) NOT NULL,
    controller_device_id CHAR(36) NOT NULL,
    created_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_irrigation_config_controllers
        PRIMARY KEY (land_id, controller_device_id),

    CONSTRAINT uk_irrigation_config_controllers_device
        UNIQUE (controller_device_id),

    CONSTRAINT fk_irrigation_config_controllers_config
        FOREIGN KEY (land_id)
        REFERENCES irrigation_configs (land_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_irrigation_config_controllers_device
        FOREIGN KEY (controller_device_id)
        REFERENCES devices (id)
        ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO irrigation_config_controllers (
    land_id,
    controller_device_id,
    created_at
)
SELECT
    land_id,
    controller_device_id,
    updated_at
FROM irrigation_configs;

ALTER TABLE irrigation_configs
    DROP FOREIGN KEY fk_irrigation_configs_controller;

ALTER TABLE irrigation_configs
    DROP INDEX uk_irrigation_configs_controller;

ALTER TABLE irrigation_configs
    DROP COLUMN controller_device_id;

ALTER TABLE irrigation_records
    ADD COLUMN batch_id CHAR(36) DEFAULT NULL
        AFTER id;

UPDATE irrigation_records
SET batch_id = id
WHERE batch_id IS NULL;

ALTER TABLE irrigation_records
    MODIFY COLUMN batch_id CHAR(36) NOT NULL,
    ADD INDEX idx_irrigation_records_batch_status (
        batch_id,
        status
    );
