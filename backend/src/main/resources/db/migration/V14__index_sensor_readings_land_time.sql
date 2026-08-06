CREATE INDEX idx_sensor_readings_land_time
    ON sensor_readings (land_id, recorded_at);
