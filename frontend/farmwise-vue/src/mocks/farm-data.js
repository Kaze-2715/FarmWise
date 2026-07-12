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
};

export const newAlert = {
    id: '',
    landId: '',
    type: 'environment',   // environment / device / pest
    severity: 'medium',    // high / medium / low
    title: '',
    description: '',
    suggestion: '',
    status: 'pending',     // pending / processing / resolved / ignored
    occurredAt: '',
    source: {
        deviceId: null,
        metric: null,
        value: null,
        unit: null
    },
    handleRecord: null
};

export const newFarmTask = {
    id: '',
    landId: '',
    sourceType: '',
    sourceId: '',

    taskType: '',
    title: '',
    description: '',
    priority: 'medium',

    status: 'pending',
    assignee: '',
    deadline: '',
    createdAt: '',

    completedAt: null,
    result: '',
    remark: ''
};

export const mockFarmTasks = [
    {
        id: 'TASK-001',
        landId: 'LAND-001',
        sourceType: 'alert',
        sourceId: 'ALT-001',

        taskType: 'irrigation',
        title: '处理东区土壤湿度过低',
        description: '检查土壤湿度和灌溉设备，根据现场情况完成补水。',
        priority: 'high',

        status: 'pending',
        assignee: '农场主李四',
        deadline: '2026-07-10T18:00:00+08:00',
        createdAt: '2026-07-09T10:35:00+08:00',

        completedAt: null,
        result: '',
        remark: '由土壤湿度异常预警生成'
    },
    {
        id: 'TASK-002',
        landId: 'LAND-001',
        sourceType: 'alert',
        sourceId: 'ALT-002',

        taskType: 'inspection',
        title: '检查东区灌溉控制器',
        description: '检查控制器供电、网络连接和现场运行状态。',
        priority: 'high',

        status: 'processing',
        assignee: '技术顾问张三',
        deadline: '2026-07-09T12:00:00+08:00',
        createdAt: '2026-07-09T08:25:00+08:00',

        completedAt: null,
        result: '',
        remark: '已前往现场检查'
    },
    {
        id: 'TASK-003',
        landId: 'LAND-001',
        sourceType: 'plan',
        sourceId: 1,

        taskType: 'harvest',
        title: '完成东区早稻收获',
        description: '按照种植计划完成成熟水稻收获，并记录现场产量。',
        priority: 'medium',

        status: 'completed',
        assignee: '农场主李四',
        deadline: '2024-07-20T18:00:00+08:00',
        createdAt: '2024-07-15T09:00:00+08:00',

        completedAt: '2024-07-20T16:30:00+08:00',
        result: '已完成早稻收获并登记产量',
        remark: '收获过程正常'
    },
    {
        id: 'TASK-004',
        landId: 'LAND-002',
        sourceType: 'manual',
        sourceId: null,

        taskType: 'weeding',
        title: '清理西区田埂杂草',
        description: '清理西区田埂及排水沟附近杂草，保持巡检通道畅通。',
        priority: 'low',

        status: 'cancelled',
        assignee: '农场主李四',
        deadline: '2026-07-11T17:00:00+08:00',
        createdAt: '2026-07-09T14:00:00+08:00',

        completedAt: null,
        result: '',
        remark: '因持续降雨取消，后续重新安排'
    },
    {
        id: 'TASK-005',
        landId: 'LAND-002',
        sourceType: 'alert',
        sourceId: 'ALT-006',

        taskType: 'pesticide',
        title: '处理西区虫害风险',
        description: '核对虫害发生范围，并根据现场情况进行定点施药。',
        priority: 'high',

        status: 'pending',
        assignee: '技术顾问李四',
        deadline: '2026-07-10T12:00:00+08:00',
        createdAt: '2026-07-09T09:20:00+08:00',

        completedAt: null,
        result: '',
        remark: '施药前需确认药剂类型和安全间隔期'
    },
    {
        id: 'TASK-006',
        landId: 'LAND-003',
        sourceType: 'system',
        sourceId: null,

        taskType: 'fertilization',
        title: '完成温室苗期追肥',
        description: '根据当前苗期安排追肥，并观察作物后续生长状态。',
        priority: 'medium',

        status: 'processing',
        assignee: '技术顾问王五',
        deadline: '2026-07-12T16:00:00+08:00',
        createdAt: '2026-07-10T08:00:00+08:00',

        completedAt: null,
        result: '',
        remark: '系统根据作物生长周期自动生成'
    },
    {
        id: 'TASK-007',
        landId: 'LAND-003',
        sourceType: 'manual',
        sourceId: null,

        taskType: 'other',
        title: '整理温室生产工具',
        description: '清点并整理温室内常用生产工具和防护用品。',
        priority: 'low',

        status: 'completed',
        assignee: '农场主李四',
        deadline: '2026-07-09T18:00:00+08:00',
        createdAt: '2026-07-09T09:30:00+08:00',

        completedAt: '2026-07-09T17:10:00+08:00',
        result: '工具已完成分类清点并归位',
        remark: ''
    }
];

export const mockAlerts = [
    {
        id: 'ALT-001',
        landId: 'LAND-001',
        type: 'environment',
        severity: 'high',
        title: '土壤湿度低于适宜范围',
        description: '东区1号水田的土壤湿度降至56%，低于当前地块设定的60%下限。',
        suggestion: '建议检查土壤湿度传感器，并根据现场情况安排灌溉。',
        status: 'processing',
        occurredAt: '2026-07-09T10:30:00+08:00',
        source: { deviceId: 'DEV-001', metric: 'soil_moisture', value: 56, unit: '%' },
        handleRecord: null
    },
    {
        id: 'ALT-002',
        landId: 'LAND-001',
        type: 'device',
        severity: 'high',
        title: '灌溉控制器离线',
        description: '东区灌溉控制器无法连接，暂时不能接收灌溉指令。',
        suggestion: '建议检查设备供电、网络和现场控制状态。',
        status: 'processing',
        occurredAt: '2026-07-09T08:20:00+08:00',
        source: { deviceId: 'DEV-002', metric: null, value: null, unit: null },
        handleRecord: null
    },
    {
        id: 'ALT-003',
        landId: 'LAND-001',
        type: 'pest',
        severity: 'medium',
        title: '水稻真菌病害风险',
        description: '巡田时发现叶片出现少量疑似真菌病斑。',
        suggestion: '建议复查发病范围，并持续观察病斑变化。',
        status: 'resolved',
        occurredAt: '2026-07-08T15:10:00+08:00',
        source: { deviceId: null, metric: null, value: null, unit: null },
        handleRecord: {
            measure: '完成现场复查并对受影响区域进行定点处理',
            handledAt: '2026-07-08T17:30:00+08:00',
            result: '病斑未继续扩大',
            remark: '使用多菌灵可湿性粉剂，800倍稀释后局部喷施。',
            operator: '技术顾问张三'
        }
    },
    {
        id: 'ALT-004',
        landId: 'LAND-002',
        type: 'environment',
        severity: 'medium',
        title: '空气温度偏高',
        description: '西区2号旱地空气温度达到32.6℃，高于适宜范围上限。',
        suggestion: '建议关注高温持续时间，必要时安排降温或补水。',
        status: 'processing',
        occurredAt: '2026-07-09T13:40:00+08:00',
        source: { deviceId: 'DEV-003', metric: 'air_temperature', value: 32.6, unit: '℃' },
        handleRecord: null
    },
    {
        id: 'ALT-005',
        landId: 'LAND-002',
        type: 'device',
        severity: 'low',
        title: '虫情摄像头电量偏低',
        description: '西区虫情摄像头剩余电量为18%，建议及时补充电量。',
        suggestion: '建议安排巡检并检查充电设施。',
        status: 'ignored',
        occurredAt: '2026-07-09T10:20:00+08:00',
        source: { deviceId: 'DEV-004', metric: 'battery', value: 18, unit: '%' },
        handleRecord: {
            measure: '核对设备续航和当日巡检计划',
            handledAt: '2026-07-09T10:45:00+08:00',
            result: '已忽略',
            remark: '设备尚可支持当日采集，已纳入明日充电计划。',
            operator: '技术顾问李四'
        }
    },
    {
        id: 'ALT-006',
        landId: 'LAND-002',
        type: 'pest',
        severity: 'high',
        title: '小麦蚌虫数量快速增加',
        description: '近期巡田发现蚌虫数量明显增加，存在扩散风险。',
        suggestion: '建议尽快核对发生范围并制定防治方案。',
        status: 'processing',
        occurredAt: '2026-07-09T09:15:00+08:00',
        source: { deviceId: 'DEV-004', metric: null, value: null, unit: null },
        handleRecord: null
    },
    {
        id: 'ALT-007',
        landId: 'LAND-003',
        type: 'environment',
        severity: 'low',
        title: '温室光照短时偏低',
        description: '南区育苗温室光照强度短时降至9500 lx，低于适宜范围。',
        suggestion: '建议观察后续数据，确认是否需要开启补光设备。',
        status: 'ignored',
        occurredAt: '2026-07-08T16:20:00+08:00',
        source: { deviceId: 'DEV-005', metric: 'light', value: 9500, unit: 'lx' },
        handleRecord: {
            measure: '核对后续光照数据',
            handledAt: '2026-07-08T16:50:00+08:00',
            result: '已忽略',
            remark: '由短时云层遮挡引起，后续数据已恢复正常。',
            operator: '技术顾问王五'
        }
    },
    {
        id: 'ALT-008',
        landId: 'LAND-003',
        type: 'device',
        severity: 'high',
        title: '土壤pH传感器离线',
        description: '温室土壤pH传感器长时间未上报数据，当前pH数据可能已失效。',
        suggestion: '建议检查设备电量、通信和传感器接线。',
        status: 'resolved',
        occurredAt: '2026-07-08T22:45:00+08:00',
        source: { deviceId: 'DEV-006', metric: 'soil_ph', value: null, unit: null },
        handleRecord: {
            measure: '更换电池并重新连接设备',
            handledAt: '2026-07-09T08:40:00+08:00',
            result: '设备恢复上报',
            remark: '恢复后已校验首条pH数据。',
            operator: '技术顾问王五'
        }
    },
    {
        id: 'ALT-009',
        landId: 'LAND-003',
        type: 'pest',
        severity: 'medium',
        title: '番茄叶片出现虫害迹象',
        description: '育苗区部分番茄叶片发现轻微取食痕迹。',
        suggestion: '建议对叶片背面和相邻植株进行进一步检查。',
        status: 'pending',
        occurredAt: '2026-07-09T11:05:00+08:00',
        source: { deviceId: null, metric: null, value: null, unit: null },
        handleRecord: null
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
