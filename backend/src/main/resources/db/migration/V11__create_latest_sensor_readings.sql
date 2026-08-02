CREATE TABLE latest_sensor_readings (
    device_id CHAR(36) NOT NULL,
    metric VARCHAR(50) NOT NULL,
    land_id CHAR(36) DEFAULT NULL,
    recorded_at DATETIME(3) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    value DECIMAL(16, 4) NOT NULL,
    updated_at DATETIME(3) NOT NULL,

    CONSTRAINT pk_latest_sensor_readings PRIMARY KEY (device_id, metric),
    CONSTRAINT fk_latest_sensor_readings_device FOREIGN KEY (device_id)
        REFERENCES devices (id) ON DELETE CASCADE,
    CONSTRAINT fk_latest_sensor_readings_land FOREIGN KEY (land_id)
        REFERENCES lands (id) ON DELETE RESTRICT,
    CONSTRAINT chk_latest_sensor_readings_metric CHECK (metric IN (
        'soil_moisture',
        'air_temperature',
        'air_humidity',
        'light',
        'soil_ph',
        'battery'
    )),
    INDEX idx_latest_sensor_readings_land_metric (land_id, metric)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO latest_sensor_readings (
    device_id,
    metric,
    land_id,
    recorded_at,
    unit,
    value,
    updated_at
)
SELECT
    ranked.device_id,
    ranked.metric,
    ranked.land_id,
    ranked.recorded_at,
    ranked.unit,
    ranked.value,
    UTC_TIMESTAMP(3)
FROM (
    SELECT
        sr.id,
        sr.device_id,
        sr.metric,
        sr.land_id,
        sr.recorded_at,
        sr.unit,
        sr.value,
        ROW_NUMBER() OVER (
            PARTITION BY sr.device_id, sr.metric
            ORDER BY sr.recorded_at DESC, sr.id DESC
        ) AS rn
    FROM sensor_readings sr
) ranked
WHERE ranked.rn = 1;
