import { ref } from "vue";
import {
    mockLands,
    mockDevices,
    mockSensorReadings,
    mockEnvironmentThresholds,
    mockPlans,
    mockAlerts,
    mockIrrigationConfigs,
    mockIrrigationRecords,
    mockFarmTasks,
    mockReports,
    mockAiConversations
} from "../mocks/farm-data";

const allowedMessageRoles = new Set(['user', 'assistant']);

const allowedPriorities = new Set(['low', 'medium', 'high']);

const lands = ref(
    mockLands.map(land => ({ ...land }))
);

const devices = ref(
    mockDevices.map(device => ({ ...device }))
);

const sensorReadings = ref(mockSensorReadings.map(reading => ({ ...reading })));

const environmentThresholds = ref(mockEnvironmentThresholds.map(threshold => ({ ...threshold })));

const plans = ref(mockPlans.map(plan => ({ ...plan })));

const alerts = ref(mockAlerts.map(alert => ({
    ...alert,
    source: { ...alert.source },
    handleRecord: alert.handleRecord ? { ...alert.handleRecord } : null
})));

const irrigationConfigs = ref(mockIrrigationConfigs.map(config => ({ ...config })));

const irrigationRecords = ref(mockIrrigationRecords.map(record => ({ ...record })));

const farmTasks = ref(mockFarmTasks.map(task => ({ ...task })));

const reports = ref(mockReports.map(report => ({
    ...report,
    snapshot: {
        land: { ...report.snapshot.land },
        devices: { ...report.snapshot.devices },
        environment: report.snapshot.environment.map(reading => ({ ...reading })),
        alerts: { ...report.snapshot.alerts },
        tasks: { ...report.snapshot.tasks },
        aiAdvice: report.snapshot.aiAdvice.map(advice => ({
            ...advice,
            references: advice.references.map(reference => ({ ...reference }))
        }))
    }
})));

const aiConversations = ref(
    mockAiConversations.map(conversation => ({
        ...conversation,
        messages: conversation.messages.map(message => ({
            ...message,
            references: message.references.map(reference => ({
                ...reference
            })),
            taskDraft: message.taskDraft ? { ...message.taskDraft } : null
        }))
    }))
);

const appendAiMessage = (conversationId, messageData) => {
    const conversation = aiConversations.value.find(conversation => conversation.id === conversationId);

    if (!conversation) {
        throw new Error("对话不存在：" + conversationId);
    }

    const role = messageData.role;

    const validRole = allowedMessageRoles.has(role);

    if (!validRole) {
        throw new Error("无效的对话角色：" + messageData.role);
    }

    if (!messageData.content || typeof messageData.content !== 'string') {
        throw new Error("content 字段不存在");
    }

    const content = messageData.content.trim();

    if (!content) {
        throw new Error("对话内容不能为空");
    }

    const id = crypto.randomUUID();
    const createdAt = new Date().toISOString();
    const references = messageData.references ?? [];
    const taskDraft = messageData.taskDraft ?? null;

    if (!Array.isArray(references)) {
        throw new Error("references 应为数组");
    }

    if (taskDraft !== null && (typeof taskDraft !== 'object' || Array.isArray(taskDraft))) {
        throw new Error("taskDraft 类型应为非数组的对象");
    }

    const message = {
        id,
        role,
        content,
        createdAt,
        references: references.map(reference => ({
            ...reference
        })),
        taskDraft: taskDraft === null ? null : { ...taskDraft }
    };

    conversation.messages.push(message);

    conversation.updatedAt = createdAt;

    return message;
};

const createAiConversation = ({
    landId,
    title
}) => {
    const index = lands.value.findIndex(land => land.id === landId);

    if (index === -1) {
        throw new Error("地块不存在");
    }

    const existsActiveConversation = aiConversations.value.some(conversation => conversation.landId === landId && conversation.status === 'active');

    if (existsActiveConversation) {
        throw new Error("当前地块已经存在活跃对话");
    }

    if (typeof title !== "string") {
        throw new Error("title的类型应该为字符串，而不是: " + typeof title);
    }

    const validTitle = title.trim();

    if (!validTitle) {
        throw new Error("标题不能为空!");
    }

    const now = new Date().toISOString();

    const conversation = {
        id: crypto.randomUUID(),
        landId,
        title: validTitle,
        status: 'active',
        createdAt: now,
        updatedAt: now,
        messages: []
    };

    aiConversations.value.unshift(conversation);

    return conversation;
};

const createFarmTaskFromAiDraft = ({
    conversationId,
    messageId,
    assignee,
    deadline
}) => {
    const conversation = aiConversations.value.find(conversation => conversation.id === conversationId);

    if (!conversation) {
        throw new Error("对话不存在");
    }

    const message = conversation.messages.find(message => message.id === messageId);

    if (!message) {
        throw new Error("AI 消息不存在");
    }

    if (message.role !== 'assistant') {
        throw new Error("只有 AI 顾问消息可以生成农事任务");
    }

    const draft = message.taskDraft ?? null;

    if (!draft) {
        throw new Error("draft 对象为空");
    }

    if (typeof draft !== "object" || Array.isArray(draft)) {
        throw new Error("draft 类型错误，应为非数组的普通对象");
    }

    const validTaskType = validateString(draft.taskType, '任务类型');
    const validTitle = validateString(draft.title, '任务标题');
    const validDescription = validateString(draft.description, '任务描述');
    const validAssignee = validateString(assignee, '负责人');
    const validDeadline = validateString(deadline, '截止时间');
    const validPriority = validateString(draft.priority, '优先级');

    if (!allowedPriorities.has(validPriority)) {
        throw new Error("无效的优先级");
    }

    const taskAlreadyCreated = farmTasks.value.some(task =>
        task.sourceType === 'aiMessage' && task.sourceId === message.id
    );

    if (taskAlreadyCreated) {
        throw new Error("该 AI 消息已经创建过农事任务");
    }

    const task = {
        id: crypto.randomUUID(),
        landId: conversation.landId,
        sourceType: 'aiMessage',
        sourceId: message.id,
        status: 'pending',
        createdAt: new Date().toISOString(),
        completedAt: null,
        result: '',
        remark: '由 AI 技术顾问建议生成',
        taskType: validTaskType,
        title: validTitle,
        description: validDescription,
        priority: validPriority,
        assignee: validAssignee,
        deadline: validDeadline
    };

    farmTasks.value.unshift(task);

    return task;
};

const validateString = (string, fieldName) => {
    const type = typeof string;

    if (type !== "string") {
        throw new Error(`${fieldName}的类型应为 string，当前为 ${type}`);
    }

    const validString = string.trim();

    if (!validString) {
        throw new Error(`${fieldName}不能为空`);
    }

    return validString;
};

const addLand = (formData) => {
    const area = Number(formData.area);
    const longitude = Number(formData.longitude);
    const latitude = Number(formData.latitude);

    if (!formData.name?.trim() || !formData.type?.trim() || !formData.location?.trim()) {
        throw new Error('土地名称、类型和位置不能为空');
    }

    if (!Number.isFinite(area) || area <= 0) {
        throw new Error('土地面积必须大于 0');
    }

    if (!Number.isFinite(longitude) || longitude < -180 || longitude > 180) {
        throw new Error('经度必须在 -180 到 180 之间');
    }

    if (!Number.isFinite(latitude) || latitude < -90 || latitude > 90) {
        throw new Error('纬度必须在 -90 到 90 之间');
    }

    const land = {
        ...formData,
        id: crypto.randomUUID(),
        name: formData.name.trim(),
        type: formData.type.trim(),
        location: formData.location.trim(),
        area,
        longitude,
        latitude
    };

    lands.value.push(land);
    return land;
};

const deleteLand = (landId) => {
    const deletedLand = lands.value.find(land => land.id === landId);

    if (!deletedLand) {
        throw new Error("不存在该土地，无法删除");
    }

    const haveBind = devices.value.some(device => device.landId === landId);

    if (haveBind) {
        throw new Error('该土地仍绑定设备，无法删除');
    }

    lands.value = lands.value.filter(land => land.id !== landId);

    return deletedLand;
};

const normalizeDeviceForm = (formData) => {
    const longitude = Number(formData.longitude);
    const latitude = Number(formData.latitude);

    if (!formData.name?.trim()) {
        throw new Error("设备名称不能为空");
    }
    if (!formData.type?.trim()) {
        throw new Error("设备类型不能为空");
    }
    if (!formData.model?.trim()) {
        throw new Error("设备型号不能为空");
    }
    if (formData.longitude === '' || formData.longitude === null || formData.longitude === undefined
        || !Number.isFinite(longitude) || longitude < -180 || longitude > 180) {
        throw new Error("经度必须在 -180 到 180 之间");
    }
    if (formData.latitude === '' || formData.latitude === null || formData.latitude === undefined
        || !Number.isFinite(latitude) || latitude < -90 || latitude > 90) {
        throw new Error("纬度必须在 -90 到 90 之间");
    }

    return {
        ...formData,
        name: formData.name.trim(),
        type: formData.type.trim(),
        model: formData.model.trim(),
        longitude,
        latitude
    };
};

const addDevice = (formData) => {
    const deviceData = normalizeDeviceForm(formData);

    const newDevice = {
        ...deviceData,
        id: crypto.randomUUID(),
        status: 'offline',
        battery: null,
        lastReportedAt: null
    };

    devices.value.push(newDevice);
    return newDevice;
};

const updateDevice = (deviceId, formData) => {
    const deviceIndex = devices.value.findIndex(device => device.id === deviceId);

    if (deviceIndex === -1) {
        throw new Error("设备不存在，无法更新");
    }

    const currentDevice = devices.value[deviceIndex];
    const deviceData = normalizeDeviceForm(formData);
    const updatedDevice = {
        ...currentDevice,
        ...deviceData,
        id: currentDevice.id,
        status: currentDevice.status,
        battery: currentDevice.battery,
        lastReportedAt: currentDevice.lastReportedAt
    };

    devices.value[deviceIndex] = updatedDevice;
    return updatedDevice;
};

const deleteDevice = (deviceId) => {
    const deletedDevice = devices.value.find(device => device.id === deviceId);

    if (!deletedDevice) {
        throw new Error("设备不存在，无法删除");
    }

    devices.value = devices.value.filter(device => device.id !== deviceId);

    return deletedDevice;
};

export const useFarmStore = () => {
    return {
        devices,
        lands,
        sensorReadings,
        environmentThresholds,
        plans,
        alerts,
        irrigationConfigs,
        irrigationRecords,
        farmTasks,
        reports,
        aiConversations,
        addLand,
        deleteLand,
        addDevice,
        updateDevice,
        deleteDevice,
        appendAiMessage,
        createAiConversation,
        createFarmTaskFromAiDraft
    };
};
