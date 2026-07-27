CREATE TABLE telemetry_messages (
    device_id CHAR(36) NOT NULL,
    message_id CHAR(36) NOT NULL,
    reported_at DATETIME(3) NOT NULL,
    received_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_telemetry_messages
        PRIMARY KEY (device_id, message_id),

    CONSTRAINT fk_telemetry_messages_device
        FOREIGN KEY (device_id)
        REFERENCES devices (id)
        ON DELETE RESTRICT,

    INDEX idx_telemetry_messages_received_at (received_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
