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
