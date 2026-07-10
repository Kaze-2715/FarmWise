export const newLand = {
    id: '', // 编号
    name: '', // 可识别的命名
    type: '', // 土壤类型
    area: '', // 面积
    crop: '', // 种植作物
    status: '', // 使用状态（未启用、正常种植、休耕、异常）
    location: '', // 地址
    longitude: '', // 经度
    latitude: '' // 纬度
};

export const newDevice = {
    id: '', // 设备编号
    name: '', // 可识别的命名
    type: '', // 设备监测数据类别
    landId: '', // 绑定的地块编号
    status: '', // 在线状态
    battery: '', // 电量
    lastReportedAt: '', // 最后更新数据时间
    model: '', // 型号
    installTime: '', // 安装时间
    latitude: '', // 部署位置纬度
    longitude: '' // 部署位置经度
}

export const newEnvironmentThreshold = {
    landId: '', // 所在土地
    metric: '', // 约束指标
    min: 0,
    max: 100,
    enabled: false, // 是否启用
    creator: '', // 创建人
    updatedAt: '' // 更新时间
}

export const newSensorData = {
    deviceId: '', // 采集设备 ID
    landId: null, // 采集时绑定的土地 ID，未绑定时为 null
    recordedAt: '', // 采集时间
    metric: '', // 指标类型
    unit: '', // 单位
    value: 0, // 测量值
}

export const mockLands = [
    {
        id: 'LAND-001',
        name: '东区1号水田',
        type: '水田',
        area: 15.2,
        crop: '水稻',
        status: '正常种植',
        location: '农场东区',
        longitude: 112.9388,
        latitude: 28.2282
    },
    {
        id: 'LAND-002',
        name: '西区2号旱地',
        type: '旱地',
        area: 13.3,
        crop: '小麦',
        status: '正常种植',
        location: '农场西区',
        longitude: 112.9256,
        latitude: 28.2315
    },
    {
        id: 'LAND-003',
        name: '南区育苗温室',
        type: '温室',
        area: 6.8,
        crop: '番茄',
        status: '未启用',
        location: '农场南区',
        longitude: 112.9462,
        latitude: 28.2198
    }
]

export const mockDevices = [
    {
        id: 'DEV-001',
        name: '东区土壤湿度传感器',
        type: '土壤湿度传感器',
        landId: 'LAND-001',
        status: 'online',
        battery: 86,
        lastReportedAt: '2026-07-09 10:30:00',
        model: 'FW-SM100',
        installTime: '2026-03-12',
        latitude: 28.2284,
        longitude: 112.9385
    },
    {
        id: 'DEV-002',
        name: '东区灌溉控制器',
        type: '灌溉控制器',
        landId: 'LAND-001',
        status: 'offline',
        battery: 64,
        lastReportedAt: '2026-07-09 08:15:00',
        model: 'FW-IC200',
        installTime: '2026-03-15',
        latitude: 28.2281,
        longitude: 112.9391
    },
    {
        id: 'DEV-003',
        name: '西区空气温湿度传感器',
        type: '空气温湿度传感器',
        landId: 'LAND-002',
        status: 'online',
        battery: 72,
        lastReportedAt: '2026-07-09 10:28:00',
        model: 'FW-AT120',
        installTime: '2026-04-02',
        latitude: 28.2317,
        longitude: 112.9252
    },
    {
        id: 'DEV-004',
        name: '西区虫情摄像头',
        type: '虫情摄像头',
        landId: 'LAND-002',
        status: 'online',
        battery: 18,
        lastReportedAt: '2026-07-09 10:20:00',
        model: 'FW-PC300',
        installTime: '2026-04-05',
        latitude: 28.2312,
        longitude: 112.9261
    },
    {
        id: 'DEV-005',
        name: '温室光照传感器',
        type: '光照传感器',
        landId: 'LAND-003',
        status: 'online',
        battery: 93,
        lastReportedAt: '2026-07-09 10:29:00',
        model: 'FW-LS110',
        installTime: '2026-05-18',
        latitude: 28.2201,
        longitude: 112.9459
    },
    {
        id: 'DEV-006',
        name: '温室土壤pH传感器',
        type: '土壤pH传感器',
        landId: 'LAND-003',
        status: 'offline',
        battery: 9,
        lastReportedAt: '2026-07-08 22:40:00',
        model: 'FW-PH130',
        installTime: '2026-05-18',
        latitude: 28.2196,
        longitude: 112.9465
    }
]

export const mockEnvironmentThresholds = [
    {
        landId: 'LAND-001',
        metric: 'soil_moisture',
        min: 60,
        max: 80,
        enabled: true,
        creator: '技术顾问张三',
        updatedAt: '2026-07-09T09:00:00+08:00'
    },
    {
        landId: 'LAND-002',
        metric: 'air_temperature',
        min: 18,
        max: 30,
        enabled: true,
        creator: '技术顾问李四',
        updatedAt: '2026-07-09T09:10:00+08:00'
    },
    {
        landId: 'LAND-002',
        metric: 'air_humidity',
        min: 50,
        max: 80,
        enabled: true,
        creator: '技术顾问李四',
        updatedAt: '2026-07-09T09:10:00+08:00'
    },
    {
        landId: 'LAND-003',
        metric: 'light',
        min: 10000,
        max: 30000,
        enabled: true,
        creator: '技术顾问王五',
        updatedAt: '2026-07-09T09:20:00+08:00'
    },
    {
        landId: 'LAND-003',
        metric: 'soil_ph',
        min: 6,
        max: 7.5,
        enabled: true,
        creator: '技术顾问王五',
        updatedAt: '2026-07-09T09:20:00+08:00'
    }
]

export const mockSensorReadings = [
    {
        deviceId: 'DEV-001',
        landId: 'LAND-001',
        recordedAt: '2026-06-12T10:30:00+08:00',
        metric: 'soil_moisture',
        unit: '%',
        value: 61.8
    },
    {
        deviceId: 'DEV-001',
        landId: 'LAND-001',
        recordedAt: '2026-06-19T10:30:00+08:00',
        metric: 'soil_moisture',
        unit: '%',
        value: 64.2
    },
    {
        deviceId: 'DEV-001',
        landId: 'LAND-001',
        recordedAt: '2026-06-26T10:30:00+08:00',
        metric: 'soil_moisture',
        unit: '%',
        value: 68.7
    },
    {
        deviceId: 'DEV-001',
        landId: 'LAND-001',
        recordedAt: '2026-07-03T10:30:00+08:00',
        metric: 'soil_moisture',
        unit: '%',
        value: 72.4
    },
    {
        deviceId: 'DEV-001',
        landId: 'LAND-001',
        recordedAt: '2026-07-08T10:30:00+08:00',
        metric: 'soil_moisture',
        unit: '%',
        value: 75.6
    },
    {
        deviceId: 'DEV-001',
        landId: 'LAND-001',
        recordedAt: '2026-07-09T02:30:00+08:00',
        metric: 'soil_moisture',
        unit: '%',
        value: 76.9
    },
    {
        deviceId: 'DEV-001',
        landId: 'LAND-001',
        recordedAt: '2026-07-09T06:30:00+08:00',
        metric: 'soil_moisture',
        unit: '%',
        value: 77.8
    },
    {
        deviceId: 'DEV-001',
        landId: 'LAND-001',
        recordedAt: '2026-07-09T10:30:00+08:00',
        metric: 'soil_moisture',
        unit: '%',
        value: 78.5
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-06-12T10:28:00+08:00',
        metric: 'air_temperature',
        unit: '℃',
        value: 22.1
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-06-19T10:28:00+08:00',
        metric: 'air_temperature',
        unit: '℃',
        value: 23.4
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-06-26T10:28:00+08:00',
        metric: 'air_temperature',
        unit: '℃',
        value: 24.8
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-03T10:28:00+08:00',
        metric: 'air_temperature',
        unit: '℃',
        value: 25.6
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-08T10:28:00+08:00',
        metric: 'air_temperature',
        unit: '℃',
        value: 24.9
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-09T02:28:00+08:00',
        metric: 'air_temperature',
        unit: '℃',
        value: 21.7
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-09T06:28:00+08:00',
        metric: 'air_temperature',
        unit: '℃',
        value: 22.9
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-09T10:28:00+08:00',
        metric: 'air_temperature',
        unit: '℃',
        value: 24.6
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-06-12T10:28:00+08:00',
        metric: 'air_humidity',
        unit: '%',
        value: 58.4
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-06-19T10:28:00+08:00',
        metric: 'air_humidity',
        unit: '%',
        value: 56.8
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-06-26T10:28:00+08:00',
        metric: 'air_humidity',
        unit: '%',
        value: 54.1
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-03T10:28:00+08:00',
        metric: 'air_humidity',
        unit: '%',
        value: 53.6
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-08T10:28:00+08:00',
        metric: 'air_humidity',
        unit: '%',
        value: 51.8
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-09T02:28:00+08:00',
        metric: 'air_humidity',
        unit: '%',
        value: 57.2
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-09T06:28:00+08:00',
        metric: 'air_humidity',
        unit: '%',
        value: 55.7
    },
    {
        deviceId: 'DEV-003',
        landId: 'LAND-002',
        recordedAt: '2026-07-09T10:28:00+08:00',
        metric: 'air_humidity',
        unit: '%',
        value: 52.3
    },
    {
        deviceId: 'DEV-005',
        landId: 'LAND-003',
        recordedAt: '2026-06-12T10:29:00+08:00',
        metric: 'light',
        unit: 'lx',
        value: 16800
    },
    {
        deviceId: 'DEV-005',
        landId: 'LAND-003',
        recordedAt: '2026-06-19T10:29:00+08:00',
        metric: 'light',
        unit: 'lx',
        value: 17500
    },
    {
        deviceId: 'DEV-005',
        landId: 'LAND-003',
        recordedAt: '2026-06-26T10:29:00+08:00',
        metric: 'light',
        unit: 'lx',
        value: 18200
    },
    {
        deviceId: 'DEV-005',
        landId: 'LAND-003',
        recordedAt: '2026-07-03T10:29:00+08:00',
        metric: 'light',
        unit: 'lx',
        value: 19100
    },
    {
        deviceId: 'DEV-005',
        landId: 'LAND-003',
        recordedAt: '2026-07-08T10:29:00+08:00',
        metric: 'light',
        unit: 'lx',
        value: 18600
    },
    {
        deviceId: 'DEV-005',
        landId: 'LAND-003',
        recordedAt: '2026-07-09T02:29:00+08:00',
        metric: 'light',
        unit: 'lx',
        value: 0
    },
    {
        deviceId: 'DEV-005',
        landId: 'LAND-003',
        recordedAt: '2026-07-09T06:29:00+08:00',
        metric: 'light',
        unit: 'lx',
        value: 4200
    },
    {
        deviceId: 'DEV-005',
        landId: 'LAND-003',
        recordedAt: '2026-07-09T10:29:00+08:00',
        metric: 'light',
        unit: 'lx',
        value: 18800
    },
    {
        deviceId: 'DEV-006',
        landId: 'LAND-003',
        recordedAt: '2026-06-12T22:40:00+08:00',
        metric: 'soil_ph',
        unit: 'pH',
        value: 6.4
    },
    {
        deviceId: 'DEV-006',
        landId: 'LAND-003',
        recordedAt: '2026-06-19T22:40:00+08:00',
        metric: 'soil_ph',
        unit: 'pH',
        value: 6.5
    },
    {
        deviceId: 'DEV-006',
        landId: 'LAND-003',
        recordedAt: '2026-06-26T22:40:00+08:00',
        metric: 'soil_ph',
        unit: 'pH',
        value: 6.6
    },
    {
        deviceId: 'DEV-006',
        landId: 'LAND-003',
        recordedAt: '2026-07-03T22:40:00+08:00',
        metric: 'soil_ph',
        unit: 'pH',
        value: 6.7
    },
    {
        deviceId: 'DEV-006',
        landId: 'LAND-003',
        recordedAt: '2026-07-07T22:40:00+08:00',
        metric: 'soil_ph',
        unit: 'pH',
        value: 6.6
    },
    {
        deviceId: 'DEV-006',
        landId: 'LAND-003',
        recordedAt: '2026-07-08T10:40:00+08:00',
        metric: 'soil_ph',
        unit: 'pH',
        value: 6.5
    },
    {
        deviceId: 'DEV-006',
        landId: 'LAND-003',
        recordedAt: '2026-07-08T22:40:00+08:00',
        metric: 'soil_ph',
        unit: 'pH',
        value: 6.5
    }
]
