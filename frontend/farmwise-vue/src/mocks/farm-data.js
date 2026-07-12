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

export const newIrrigationConfig = {
    landId: '',                 // 所属地块
    controllerDeviceId: '',     // 灌溉控制器
    mode: 'manual',             // manual / automatic
    enabled: false,             // 自动灌溉是否启用
    triggerMoisture: 40,        // 湿度低于或等于该值时启动
    targetMoisture: 65,         // 达到该湿度时停止
    defaultDuration: 30,        // 手动灌溉默认时长，分钟
    updatedBy: '',              // 最后修改人
    updatedAt: ''               // 最后修改时间
}

export const newIrrigationRecord = {
    id: '',                     // 执行记录编号
    landId: '',                 // 灌溉地块
    controllerDeviceId: '',     // 执行控制器
    source: 'manual',           // manual / automatic
    status: 'pending',          // pending /running / completed / failed
    startedAt: '',              // 开始时间
    endedAt: '',                // 结束时间
    plannedDuration: 0,         // 计划持续分钟数
    duration: 0,                // 实际持续分钟数
    waterUsage: null,           // 用水量，设备不能提供时允许为空
    triggerReason: '',          // 手动操作或土壤湿度低于阈值
    operator: ''                // 操作人；自动执行时可填“系统”
}

export const mockWarnings = [
  {
    id: 1,
    landId: 'LAND-001',
    level: 'red',
    cropType: '水稻',
    message: '检测到真菌病害高风险',
    suggestion: '建议尽快巡田并记录处理措施',
    handled: false
  },
  {
    id: 2,
    landId: 'LAND-002',
    level: 'yellow',
    cropType: '小麦',
    message: '土壤湿度连续偏高',
    suggestion: '建议检查排水情况',
    handled: false
  },
  {
    id: 3,
    landId: 'LAND-003',
    level: 'yellow',
    cropType: '玉米',
    message: '发现轻微虫害迹象',
    suggestion: '建议持续观察',
    handled: true
  }
];

export const mockPlans = [
    {
        id: 1,
        planName: '2024年早稻种植',
        cropType: '水稻',
        area: 15.2,
        plantingTime: '2024-03-15',
        expectedHarvestTime: '2024-07-20',
        status: '生长中',
        landId: 'LAND-001',
        remark: ''
    },
    {
        id: 2,
        planName: '2024年冬小麦种植',
        cropType: '小麦',
        area: 13.3,
        plantingTime: '2024-10-05',
        expectedHarvestTime: '2025-06-10',
        status: '播种期',
        landId: 'LAND-002',
        remark: ''
    }
];

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
    },
    {
        id: 'DEV-007',
        name: '西区灌溉控制器',
        type: '灌溉控制器',
        landId: 'LAND-002',
        status: 'online',
        battery: 78,
        lastReportedAt: '2026-07-09 10:26:00',
        model: 'FW-IC200',
        installTime: '2026-04-03',
        latitude: 28.2314,
        longitude: 112.9258
    },
    {
        id: 'DEV-008',
        name: '温室灌溉控制器',
        type: '灌溉控制器',
        landId: 'LAND-003',
        status: 'online',
        battery: 91,
        lastReportedAt: '2026-07-09 10:27:00',
        model: 'FW-IC210',
        installTime: '2026-05-20',
        latitude: 28.2199,
        longitude: 112.9461
    }
]

export const mockIrrigationConfigs = [
    {
        landId: 'LAND-001',
        controllerDeviceId: 'DEV-002',
        mode: 'automatic',
        enabled: true,
        triggerMoisture: 45,
        targetMoisture: 68,
        defaultDuration: 30,
        updatedBy: '技术顾问张三',
        updatedAt: '2026-07-09T09:20:00+08:00'
    },
    {
        landId: 'LAND-002',
        controllerDeviceId: 'DEV-007',
        mode: 'manual',
        enabled: false,
        triggerMoisture: 35,
        targetMoisture: 55,
        defaultDuration: 25,
        updatedBy: '技术顾问张三',
        updatedAt: '2026-07-08T16:10:00+08:00'
    },
    {
        landId: 'LAND-003',
        controllerDeviceId: 'DEV-008',
        mode: 'automatic',
        enabled: false,
        triggerMoisture: 50,
        targetMoisture: 72,
        defaultDuration: 15,
        updatedBy: '技术顾问李四',
        updatedAt: '2026-07-07T11:35:00+08:00'
    }
]

export const mockIrrigationRecords = [
    {
        id: 'IRR-001',
        landId: 'LAND-001',
        controllerDeviceId: 'DEV-002',
        source: 'automatic',
        status: 'completed',
        startedAt: '2026-07-09T05:30:00+08:00',
        endedAt: '2026-07-09T06:02:00+08:00',
        plannedDuration: 30,
        duration: 32,
        waterUsage: 18.6,
        triggerReason: '土壤湿度低于 45%',
        operator: '系统'
    },
    {
        id: 'IRR-002',
        landId: 'LAND-001',
        controllerDeviceId: 'DEV-002',
        source: 'manual',
        status: 'failed',
        startedAt: '2026-07-08T16:20:00+08:00',
        endedAt: '2026-07-08T16:21:00+08:00',
        plannedDuration: 30,
        duration: 1,
        waterUsage: null,
        triggerReason: '人工补充灌溉',
        operator: '技术顾问张三'
    },
    {
        id: 'IRR-003',
        landId: 'LAND-002',
        controllerDeviceId: 'DEV-007',
        source: 'manual',
        status: 'completed',
        startedAt: '2026-07-09T07:40:00+08:00',
        endedAt: '2026-07-09T08:05:00+08:00',
        plannedDuration: 25,
        duration: 25,
        waterUsage: 14.2,
        triggerReason: '人工日常灌溉',
        operator: '农场主李四'
    },
    {
        id: 'IRR-004',
        landId: 'LAND-002',
        controllerDeviceId: 'DEV-007',
        source: 'manual',
        status: 'running',
        startedAt: '2026-07-09T10:15:00+08:00',
        endedAt: '',
        plannedDuration: 25,
        duration: 0,
        waterUsage: null,
        triggerReason: '播种后补水',
        operator: '农场主李四'
    },
    {
        id: 'IRR-005',
        landId: 'LAND-003',
        controllerDeviceId: 'DEV-008',
        source: 'automatic',
        status: 'pending',
        startedAt: '',
        endedAt: '',
        plannedDuration: 15,
        duration: 0,
        waterUsage: null,
        triggerReason: '等待达到自动灌溉条件',
        operator: '系统'
    },
    {
        id: 'IRR-006',
        landId: 'LAND-003',
        controllerDeviceId: 'DEV-008',
        source: 'manual',
        status: 'completed',
        startedAt: '2026-07-08T09:10:00+08:00',
        endedAt: '2026-07-08T09:25:00+08:00',
        plannedDuration: 15,
        duration: 15,
        waterUsage: 8.4,
        triggerReason: '温室日常补水',
        operator: '技术顾问李四'
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
