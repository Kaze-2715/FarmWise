CREATE TABLE users (
    id CHAR(36) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    real_name VARCHAR(50) DEFAULT NULL,
    email VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone VARCHAR(30) DEFAULT NULL,
    avatar_file_id CHAR(36) DEFAULT NULL,
    organization VARCHAR(100) DEFAULT NULL,
    province VARCHAR(50) DEFAULT NULL,
    city VARCHAR(50) DEFAULT NULL,
    position VARCHAR(100) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,
    last_login_at DATETIME(3) DEFAULT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_status CHECK (status IN ('active', 'disabled'))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE roles (
    code VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    created_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (code),
    CONSTRAINT uk_roles_name UNIQUE (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE permissions (
    code VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    module VARCHAR(50) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,

    CONSTRAINT pk_permissions PRIMARY KEY (code),
    INDEX idx_permissions_module (module)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE user_roles (
    user_id CHAR(36) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    created_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_code),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_code)
        REFERENCES roles (code) ON DELETE CASCADE,
    INDEX idx_user_roles_role_code (role_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE role_permissions (
    role_code VARCHAR(50) NOT NULL,
    permission_code VARCHAR(100) NOT NULL,
    created_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_code, permission_code),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_code)
        REFERENCES roles (code) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_code)
        REFERENCES permissions (code) ON DELETE CASCADE,
    INDEX idx_role_permissions_permission_code (permission_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE files (
    id CHAR(36) NOT NULL,
    owner_id CHAR(36) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    purpose VARCHAR(30) NOT NULL,
    storage_provider VARCHAR(30) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    created_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_files PRIMARY KEY (id),
    CONSTRAINT uk_files_storage_key UNIQUE (storage_key),
    CONSTRAINT fk_files_owner FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_files_size CHECK (size > 0),
    CONSTRAINT chk_files_purpose CHECK (purpose IN ('avatar')),
    INDEX idx_files_owner_purpose (owner_id, purpose)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

ALTER TABLE users
    ADD CONSTRAINT fk_users_avatar_file FOREIGN KEY (avatar_file_id)
        REFERENCES files (id) ON DELETE SET NULL;

CREATE TABLE lands (
    id CHAR(36) NOT NULL,
    owner_id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    land_type VARCHAR(30) NOT NULL,
    area DECIMAL(10, 2) NOT NULL,
    crop VARCHAR(100) DEFAULT NULL,
    status VARCHAR(30) NOT NULL,
    location VARCHAR(255) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_lands PRIMARY KEY (id),
    CONSTRAINT uk_lands_owner_name UNIQUE (owner_id, name),
    CONSTRAINT fk_lands_owner FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_lands_type CHECK (land_type IN ('paddy', 'dryland', 'greenhouse')),
    CONSTRAINT chk_lands_area CHECK (area > 0),
    CONSTRAINT chk_lands_status CHECK (status IN ('inactive', 'cultivating', 'fallow', 'abnormal')),
    CONSTRAINT chk_lands_longitude CHECK (longitude BETWEEN -180 AND 180),
    CONSTRAINT chk_lands_latitude CHECK (latitude BETWEEN -90 AND 90),
    INDEX idx_lands_owner_status (owner_id, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE devices (
    id CHAR(36) NOT NULL,
    owner_id CHAR(36) NOT NULL,
    land_id CHAR(36) DEFAULT NULL,
    name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'offline',
    battery DECIMAL(5, 2) DEFAULT NULL,
    last_reported_at DATETIME(3) DEFAULT NULL,
    model VARCHAR(100) NOT NULL,
    install_date DATE DEFAULT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_devices PRIMARY KEY (id),
    CONSTRAINT uk_devices_owner_name UNIQUE (owner_id, name),
    CONSTRAINT fk_devices_owner FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_devices_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT chk_devices_type CHECK (device_type IN (
        'soil_moisture_sensor',
        'air_temp_humidity_sensor',
        'light_sensor',
        'soil_ph_sensor',
        'pest_camera',
        'irrigation_controller'
    )),
    CONSTRAINT chk_devices_status CHECK (status IN ('online', 'offline')),
    CONSTRAINT chk_devices_battery CHECK (battery IS NULL OR battery BETWEEN 0 AND 100),
    CONSTRAINT chk_devices_longitude CHECK (longitude BETWEEN -180 AND 180),
    CONSTRAINT chk_devices_latitude CHECK (latitude BETWEEN -90 AND 90),
    INDEX idx_devices_owner_status (owner_id, status),
    INDEX idx_devices_land_type_status (land_id, device_type, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE planting_plans (
    id CHAR(36) NOT NULL,
    land_id CHAR(36) NOT NULL,
    plan_name VARCHAR(100) NOT NULL,
    crop_type VARCHAR(100) NOT NULL,
    area DECIMAL(10, 2) NOT NULL,
    planting_date DATE NOT NULL,
    expected_harvest_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'planned',
    remark VARCHAR(500) NOT NULL DEFAULT '',
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_planting_plans PRIMARY KEY (id),
    CONSTRAINT fk_planting_plans_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT chk_planting_plans_area CHECK (area > 0),
    CONSTRAINT chk_planting_plans_dates CHECK (expected_harvest_date >= planting_date),
    CONSTRAINT chk_planting_plans_status CHECK (status IN (
        'planned', 'sowing', 'growing', 'harvested', 'cancelled'
    )),
    INDEX idx_planting_plans_land_status (land_id, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE sensor_readings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_id CHAR(36) NOT NULL,
    land_id CHAR(36) DEFAULT NULL,
    recorded_at DATETIME(3) NOT NULL,
    metric VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    value DECIMAL(16, 4) NOT NULL,
    created_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_sensor_readings PRIMARY KEY (id),
    CONSTRAINT fk_sensor_readings_device FOREIGN KEY (device_id)
        REFERENCES devices (id) ON DELETE RESTRICT,
    CONSTRAINT fk_sensor_readings_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT chk_sensor_readings_metric CHECK (metric IN (
        'soil_moisture',
        'air_temperature',
        'air_humidity',
        'light',
        'soil_ph',
        'battery'
    )),
    INDEX idx_sensor_readings_land_metric_time (land_id, metric, recorded_at),
    INDEX idx_sensor_readings_device_time (device_id, recorded_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE environment_thresholds (
    land_id CHAR(36) NOT NULL,
    metric VARCHAR(50) NOT NULL,
    min_value DECIMAL(16, 4) NOT NULL,
    max_value DECIMAL(16, 4) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    creator_id CHAR(36) NOT NULL,
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_environment_thresholds PRIMARY KEY (land_id, metric),
    CONSTRAINT fk_environment_thresholds_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT fk_environment_thresholds_creator FOREIGN KEY (creator_id)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_environment_thresholds_metric CHECK (metric IN (
        'soil_moisture',
        'air_temperature',
        'air_humidity',
        'light',
        'soil_ph',
        'battery'
    )),
    CONSTRAINT chk_environment_thresholds_range CHECK (min_value < max_value),
    INDEX idx_environment_thresholds_creator (creator_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE irrigation_configs (
    land_id CHAR(36) NOT NULL,
    controller_device_id CHAR(36) NOT NULL,
    mode VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    trigger_moisture DECIMAL(5, 2) NOT NULL,
    target_moisture DECIMAL(5, 2) NOT NULL,
    default_duration INT NOT NULL,
    updated_by CHAR(36) NOT NULL,
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_irrigation_configs PRIMARY KEY (land_id),
    CONSTRAINT uk_irrigation_configs_controller UNIQUE (controller_device_id),
    CONSTRAINT fk_irrigation_configs_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT fk_irrigation_configs_controller FOREIGN KEY (controller_device_id)
        REFERENCES devices (id) ON DELETE RESTRICT,
    CONSTRAINT fk_irrigation_configs_updated_by FOREIGN KEY (updated_by)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_irrigation_configs_mode CHECK (mode IN ('manual', 'automatic')),
    CONSTRAINT chk_irrigation_configs_trigger CHECK (trigger_moisture BETWEEN 0 AND 100),
    CONSTRAINT chk_irrigation_configs_target CHECK (target_moisture BETWEEN 0 AND 100),
    CONSTRAINT chk_irrigation_configs_range CHECK (trigger_moisture < target_moisture),
    CONSTRAINT chk_irrigation_configs_duration CHECK (default_duration BETWEEN 1 AND 180)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE irrigation_records (
    id CHAR(36) NOT NULL,
    land_id CHAR(36) NOT NULL,
    controller_device_id CHAR(36) NOT NULL,
    source VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    started_at DATETIME(3) DEFAULT NULL,
    ended_at DATETIME(3) DEFAULT NULL,
    planned_duration INT NOT NULL,
    duration INT NOT NULL DEFAULT 0,
    water_usage DECIMAL(12, 3) DEFAULT NULL,
    trigger_reason VARCHAR(500) NOT NULL,
    operator_id CHAR(36) DEFAULT NULL,
    operator_name VARCHAR(100) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_irrigation_records PRIMARY KEY (id),
    CONSTRAINT fk_irrigation_records_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT fk_irrigation_records_controller FOREIGN KEY (controller_device_id)
        REFERENCES devices (id) ON DELETE RESTRICT,
    CONSTRAINT fk_irrigation_records_operator FOREIGN KEY (operator_id)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_irrigation_records_source CHECK (source IN ('manual', 'automatic')),
    CONSTRAINT chk_irrigation_records_status CHECK (status IN (
        'pending', 'running', 'completed', 'failed'
    )),
    CONSTRAINT chk_irrigation_records_planned_duration CHECK (planned_duration BETWEEN 1 AND 180),
    CONSTRAINT chk_irrigation_records_duration CHECK (duration >= 0),
    CONSTRAINT chk_irrigation_records_water_usage CHECK (water_usage IS NULL OR water_usage >= 0),
    CONSTRAINT chk_irrigation_records_dates CHECK (
        ended_at IS NULL OR started_at IS NULL OR ended_at >= started_at
    ),
    INDEX idx_irrigation_records_land_created (land_id, created_at),
    INDEX idx_irrigation_records_controller_status (controller_device_id, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE alerts (
    id CHAR(36) NOT NULL,
    land_id CHAR(36) NOT NULL,
    type VARCHAR(30) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    suggestion VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    occurred_at DATETIME(3) NOT NULL,
    source_device_id CHAR(36) DEFAULT NULL,
    source_metric VARCHAR(50) DEFAULT NULL,
    source_value DECIMAL(16, 4) DEFAULT NULL,
    source_unit VARCHAR(20) DEFAULT NULL,
    handle_measure VARCHAR(1000) DEFAULT NULL,
    handled_at DATETIME(3) DEFAULT NULL,
    handle_result VARCHAR(1000) DEFAULT NULL,
    handle_remark VARCHAR(1000) DEFAULT NULL,
    handle_operator_id CHAR(36) DEFAULT NULL,
    handle_operator_name VARCHAR(100) DEFAULT NULL,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_alerts PRIMARY KEY (id),
    CONSTRAINT fk_alerts_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT fk_alerts_source_device FOREIGN KEY (source_device_id)
        REFERENCES devices (id) ON DELETE RESTRICT,
    CONSTRAINT fk_alerts_handle_operator FOREIGN KEY (handle_operator_id)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_alerts_type CHECK (type IN ('environment', 'device', 'pest')),
    CONSTRAINT chk_alerts_severity CHECK (severity IN ('low', 'medium', 'high')),
    CONSTRAINT chk_alerts_status CHECK (status IN ('pending', 'processing', 'resolved', 'ignored')),
    CONSTRAINT chk_alerts_source_metric CHECK (
        source_metric IS NULL OR source_metric IN (
            'soil_moisture',
            'air_temperature',
            'air_humidity',
            'light',
            'soil_ph',
            'battery'
        )
    ),
    INDEX idx_alerts_land_status_occurred (land_id, status, occurred_at),
    INDEX idx_alerts_source_device_occurred (source_device_id, occurred_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE farm_tasks (
    id CHAR(36) NOT NULL,
    land_id CHAR(36) NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    source_id CHAR(36) DEFAULT NULL,
    task_type VARCHAR(30) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    assignee VARCHAR(100) NOT NULL,
    deadline DATETIME(3) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    completed_at DATETIME(3) DEFAULT NULL,
    result VARCHAR(1000) NOT NULL DEFAULT '',
    remark VARCHAR(1000) NOT NULL DEFAULT '',
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_farm_tasks PRIMARY KEY (id),
    CONSTRAINT fk_farm_tasks_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT chk_farm_tasks_source_type CHECK (source_type IN (
        'manual', 'alert', 'plan', 'system', 'aiMessage'
    )),
    CONSTRAINT chk_farm_tasks_source_id CHECK (
        (source_type = 'manual' AND source_id IS NULL)
        OR (source_type <> 'manual' AND source_id IS NOT NULL)
    ),
    CONSTRAINT chk_farm_tasks_type CHECK (task_type IN (
        'irrigation',
        'fertilization',
        'pesticide',
        'weeding',
        'inspection',
        'harvest',
        'other'
    )),
    CONSTRAINT chk_farm_tasks_priority CHECK (priority IN ('low', 'medium', 'high')),
    CONSTRAINT chk_farm_tasks_status CHECK (status IN (
        'pending', 'processing', 'completed', 'cancelled'
    )),
    INDEX idx_farm_tasks_land_status_created (land_id, status, created_at),
    INDEX idx_farm_tasks_source (source_type, source_id),
    INDEX idx_farm_tasks_deadline_status (deadline, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE ai_conversations (
    id CHAR(36) NOT NULL,
    land_id CHAR(36) NOT NULL,
    title VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_by CHAR(36) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,
    active_land_id CHAR(36) GENERATED ALWAYS AS (
        CASE WHEN status = 'active' THEN land_id ELSE NULL END
    ) STORED,

    CONSTRAINT pk_ai_conversations PRIMARY KEY (id),
    CONSTRAINT uk_ai_conversations_active_land UNIQUE (active_land_id),
    CONSTRAINT fk_ai_conversations_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ai_conversations_created_by FOREIGN KEY (created_by)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_ai_conversations_status CHECK (status IN ('active', 'closed')),
    INDEX idx_ai_conversations_land_status_updated (land_id, status, updated_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE ai_messages (
    id CHAR(36) NOT NULL,
    conversation_id CHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    references_json JSON NOT NULL,
    task_draft_json JSON DEFAULT NULL,
    created_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_ai_messages PRIMARY KEY (id),
    CONSTRAINT fk_ai_messages_conversation FOREIGN KEY (conversation_id)
        REFERENCES ai_conversations (id) ON DELETE CASCADE,
    CONSTRAINT chk_ai_messages_role CHECK (role IN ('user', 'assistant')),
    INDEX idx_ai_messages_conversation_created (conversation_id, created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE reports (
    id CHAR(36) NOT NULL,
    land_id CHAR(36) NOT NULL,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(150) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'generated',
    creator_id CHAR(36) NOT NULL,
    creator_name VARCHAR(100) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    generated_at DATETIME(3) NOT NULL,
    summary TEXT NOT NULL,
    snapshot_json JSON NOT NULL,

    CONSTRAINT pk_reports PRIMARY KEY (id),
    CONSTRAINT fk_reports_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT fk_reports_creator FOREIGN KEY (creator_id)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT chk_reports_type CHECK (type IN (
        'comprehensive', 'device', 'environment', 'alert', 'task'
    )),
    CONSTRAINT chk_reports_status CHECK (status IN ('generated', 'archived')),
    CONSTRAINT chk_reports_dates CHECK (end_date >= start_date),
    INDEX idx_reports_land_type_status_created (land_id, type, status, created_at),
    INDEX idx_reports_status_created (status, created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
