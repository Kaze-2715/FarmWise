ALTER TABLE irrigation_configs
    ADD COLUMN id CHAR(36) DEFAULT NULL
        FIRST,
    ADD COLUMN name VARCHAR(100) DEFAULT NULL
        AFTER land_id;

UPDATE irrigation_configs
SET id = UUID(),
    name = '默认灌溉配置';

ALTER TABLE irrigation_config_controllers
    ADD COLUMN config_id CHAR(36) DEFAULT NULL
        FIRST;

UPDATE irrigation_config_controllers controller
JOIN irrigation_configs config
    ON config.land_id = controller.land_id
SET controller.config_id = config.id;

ALTER TABLE irrigation_config_controllers
    ADD INDEX idx_irrigation_config_controllers_device (
        controller_device_id
    );

ALTER TABLE irrigation_config_controllers
    DROP FOREIGN KEY fk_irrigation_config_controllers_config,
    DROP PRIMARY KEY,
    DROP INDEX uk_irrigation_config_controllers_device;

ALTER TABLE irrigation_configs
    DROP PRIMARY KEY,
    MODIFY COLUMN id CHAR(36) NOT NULL,
    MODIFY COLUMN name VARCHAR(100) NOT NULL,
    ADD COLUMN active_land_id CHAR(36)
        GENERATED ALWAYS AS (
            CASE
                WHEN enabled = TRUE THEN land_id
                ELSE NULL
            END
        ) STORED,
    ADD CONSTRAINT pk_irrigation_configs
        PRIMARY KEY (id),
    ADD CONSTRAINT uk_irrigation_configs_land_name
        UNIQUE (land_id, name),
    ADD CONSTRAINT uk_irrigation_configs_active_land
        UNIQUE (active_land_id);

ALTER TABLE irrigation_config_controllers
    DROP COLUMN land_id,
    MODIFY COLUMN config_id CHAR(36) NOT NULL,
    ADD CONSTRAINT pk_irrigation_config_controllers
        PRIMARY KEY (config_id, controller_device_id),
    ADD CONSTRAINT fk_irrigation_config_controllers_config
        FOREIGN KEY (config_id)
        REFERENCES irrigation_configs (id)
        ON DELETE CASCADE;
