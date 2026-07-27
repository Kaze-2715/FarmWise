INSERT INTO users (
    id,
    username,
    password_hash,
    real_name,
    email,
    email_verified,
    organization,
    province,
    city,
    position,
    status,
    created_at,
    updated_at
) VALUES (
    '50000000-0000-4000-8000-000000000001',
    'farmwise_demo',
    '$2a$10$CZ7PV6PF8z7ncZkU4LXo5e8teaSTXn6M7GnjzRy7fGJjwdjVY3UjO',
    'FarmWise 演示用户',
    'demo@farmwise.local',
    TRUE,
    'FarmWise 虚拟农场',
    '北京市',
    '北京市',
    '农场主',
    'active',
    UTC_TIMESTAMP(3),
    UTC_TIMESTAMP(3)
), (
    '50000000-0000-4000-8000-000000000002',
    'farmwise_admin',
    '$2a$10$CZ7PV6PF8z7ncZkU4LXo5e8teaSTXn6M7GnjzRy7fGJjwdjVY3UjO',
    'FarmWise 演示管理员',
    'admin@farmwise.local',
    TRUE,
    'FarmWise 虚拟农场',
    '北京市',
    '北京市',
    '系统管理员',
    'active',
    UTC_TIMESTAMP(3),
    UTC_TIMESTAMP(3)
), (
    '50000000-0000-4000-8000-000000000003',
    'farmwise_analyst',
    '$2a$10$CZ7PV6PF8z7ncZkU4LXo5e8teaSTXn6M7GnjzRy7fGJjwdjVY3UjO',
    'FarmWise 演示数据分析师',
    'analyst@farmwise.local',
    TRUE,
    'FarmWise 虚拟农场',
    '北京市',
    '北京市',
    '数据分析师',
    'active',
    UTC_TIMESTAMP(3),
    UTC_TIMESTAMP(3)
);

INSERT INTO user_roles (
    user_id,
    role_code,
    created_at
) VALUES (
    '50000000-0000-4000-8000-000000000001',
    'farm_owner',
    UTC_TIMESTAMP(3)
), (
    '50000000-0000-4000-8000-000000000002',
    'sys_admin',
    UTC_TIMESTAMP(3)
), (
    '50000000-0000-4000-8000-000000000003',
    'data_analyst',
    UTC_TIMESTAMP(3)
);

INSERT INTO lands (
    id,
    owner_id,
    name,
    land_type,
    area,
    crop,
    status,
    location,
    longitude,
    latitude,
    created_at,
    updated_at
) VALUES (
    '60000000-0000-4000-8000-000000000001',
    '50000000-0000-4000-8000-000000000001',
    'FarmWise 虚拟温室',
    'greenhouse',
    20.00,
    '番茄',
    'cultivating',
    '北京市朝阳区虚拟农业示范园',
    116.3975000,
    39.9087000,
    UTC_TIMESTAMP(3),
    UTC_TIMESTAMP(3)
);

SET @sim_owner_id = '50000000-0000-4000-8000-000000000001';
SET @sim_land_id = '60000000-0000-4000-8000-000000000001';

INSERT INTO devices (
    id,
    owner_id,
    land_id,
    name,
    device_type,
    status,
    battery,
    last_reported_at,
    model,
    install_date,
    longitude,
    latitude,
    created_at,
    updated_at
)
SELECT
    seed.id,
    @sim_owner_id,
    @sim_land_id,
    seed.name,
    seed.device_type,
    'offline',
    seed.battery,
    NULL,
    seed.model,
    UTC_DATE(),
    land.longitude,
    land.latitude,
    UTC_TIMESTAMP(3),
    UTC_TIMESTAMP(3)
FROM (
    SELECT
        '10000000-0000-4000-8000-000000000001' AS id,
        '虚拟土壤湿度传感器 01' AS name,
        'soil_moisture_sensor' AS device_type,
        95.00 AS battery,
        'SIM-SOIL-MOISTURE' AS model
    UNION ALL
    SELECT
        '10000000-0000-4000-8000-000000000002',
        '虚拟土壤湿度传感器 02',
        'soil_moisture_sensor',
        92.00,
        'SIM-SOIL-MOISTURE'
    UNION ALL
    SELECT
        '10000000-0000-4000-8000-000000000003',
        '虚拟土壤湿度传感器 03',
        'soil_moisture_sensor',
        88.00,
        'SIM-SOIL-MOISTURE'
    UNION ALL
    SELECT
        '20000000-0000-4000-8000-000000000001',
        '虚拟空气温湿度传感器 01',
        'air_temp_humidity_sensor',
        94.00,
        'SIM-AIR-TEMP-HUMIDITY'
    UNION ALL
    SELECT
        '20000000-0000-4000-8000-000000000002',
        '虚拟空气温湿度传感器 02',
        'air_temp_humidity_sensor',
        90.00,
        'SIM-AIR-TEMP-HUMIDITY'
    UNION ALL
    SELECT
        '20000000-0000-4000-8000-000000000003',
        '虚拟空气温湿度传感器 03',
        'air_temp_humidity_sensor',
        86.00,
        'SIM-AIR-TEMP-HUMIDITY'
    UNION ALL
    SELECT
        '30000000-0000-4000-8000-000000000001',
        '虚拟光照传感器 01',
        'light_sensor',
        93.00,
        'SIM-LIGHT'
    UNION ALL
    SELECT
        '30000000-0000-4000-8000-000000000002',
        '虚拟光照传感器 02',
        'light_sensor',
        89.00,
        'SIM-LIGHT'
    UNION ALL
    SELECT
        '30000000-0000-4000-8000-000000000003',
        '虚拟光照传感器 03',
        'light_sensor',
        85.00,
        'SIM-LIGHT'
    UNION ALL
    SELECT
        '40000000-0000-4000-8000-000000000001',
        '虚拟土壤 pH 传感器 01',
        'soil_ph_sensor',
        91.00,
        'SIM-SOIL-PH'
    UNION ALL
    SELECT
        '40000000-0000-4000-8000-000000000002',
        '虚拟土壤 pH 传感器 02',
        'soil_ph_sensor',
        87.00,
        'SIM-SOIL-PH'
    UNION ALL
    SELECT
        '40000000-0000-4000-8000-000000000003',
        '虚拟土壤 pH 传感器 03',
        'soil_ph_sensor',
        83.00,
        'SIM-SOIL-PH'
) AS seed
JOIN lands AS land
    ON land.id = @sim_land_id
WHERE NOT EXISTS (
    SELECT 1
    FROM devices AS existing
    WHERE existing.id = seed.id
);
