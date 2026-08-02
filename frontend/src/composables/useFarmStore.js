import { ref } from 'vue';
import {
    createLand as createLandApi,
    deleteLand as deleteLandApi,
    listLands,
    updateLand as updateLandApi
} from '../api/land';
import {
    createDevice as createDeviceApi,
    deleteDevice as deleteDeviceApi,
    listDevices,
    updateDevice as updateDeviceApi
} from '../api/device';
import {
    createPlantingPlan,
    deletePlantingPlan,
    listPlantingPlans,
    updatePlantingPlan,
    updatePlantingPlanStatus
} from '../api/planting-plan';
import {
    createEnvironmentThreshold,
    deleteEnvironmentThreshold,
    listEnvironmentThresholds,
    listLatestSensorReadings,
    listSensorReadings,
    updateEnvironmentThreshold
} from '../api/monitoring';
import {
    createIrrigationConfig,
    deleteIrrigationConfig as deleteIrrigationConfigApi,
    enableIrrigationConfig,
    listIrrigationConfigs,
    listIrrigationRecords,
    startIrrigation,
    stopIrrigation,
    updateIrrigationConfig
} from '../api/irrigation';
import {
    createAlert,
    ignoreAlert,
    listAlerts,
    resolveAlert,
    startAlert
} from '../api/alert';
import {
    cancelFarmTask,
    completeFarmTask,
    createFarmTask,
    listFarmTasks,
    startFarmTask
} from '../api/farm-task';
import {
    archiveReport as archiveReportApi,
    generateReport as generateReportApi,
    getReport,
    listReports
} from '../api/report';
import {
    closeConversation,
    createConversation,
    createTaskFromMessage,
    getConversation,
    listConversations,
    sendMessage
} from '../api/ai';

const lands = ref([]);
const devices = ref([]);
const sensorReadings = ref([]);
const latestSensorReadings = ref([]);
const environmentThresholds = ref([]);
const plans = ref([]);
const alerts = ref([]);
const irrigationConfigs = ref([]);
const irrigationRecords = ref([]);
const farmTasks = ref([]);
const reports = ref([]);
const aiConversations = ref([]);

const landsLoading = ref(false);
const landsLoadError = ref(null);
const devicesLoading = ref(false);
const devicesLoadError = ref(null);
const landModulesLoading = ref(false);
const latestReadingsLoading = ref(false);

const replaceForLand = (state, landId, items) => {
    state.value = [
        ...state.value.filter(item => item.landId !== landId),
        ...items
    ];
};

const replaceById = (state, item) => {
    const index = state.value.findIndex(current => current.id === item.id);
    if (index === -1) {
        state.value.unshift(item);
    } else {
        state.value[index] = item;
    }
    return item;
};

const normalizeLandForm = form => ({
    name: form.name.trim(),
    landType: form.landType.trim(),
    area: Number(form.area),
    crop: form.crop?.trim() || null,
    status: form.status,
    location: form.location.trim(),
    longitude: Number(form.longitude),
    latitude: Number(form.latitude)
});

const normalizeDeviceForm = form => ({
    name: form.name.trim(),
    deviceType: form.deviceType.trim(),
    landId: form.landId || null,
    model: form.model.trim(),
    installDate: form.installDate || null,
    longitude: Number(form.longitude),
    latitude: Number(form.latitude)
});

const loadLands = async () => {
    landsLoading.value = true;
    landsLoadError.value = null;
    try {
        lands.value = await listLands();
        return lands.value;
    } catch (error) {
        landsLoadError.value = error;
        throw error;
    } finally {
        landsLoading.value = false;
    }
};

const loadDevices = async (filters = {}) => {
    devicesLoading.value = true;
    devicesLoadError.value = null;
    try {
        devices.value = await listDevices(filters);
        return devices.value;
    } catch (error) {
        devicesLoadError.value = error;
        throw error;
    } finally {
        devicesLoading.value = false;
    }
};

const loadPlans = async landId => {
    const items = await listPlantingPlans(landId);
    replaceForLand(plans, landId, items);
    return items;
};

const loadReadings = async landId => {
    const startAt = new Date(Date.now() - 90 * 24 * 60 * 60 * 1000);
    const items = await listSensorReadings({ landId, startAt });
    replaceForLand(sensorReadings, landId, items);
    return items;
};

const loadLatestReadings = async landId => {
    if (!landId || latestReadingsLoading.value) return [];
    latestReadingsLoading.value = true;
    try {
        const items = await listLatestSensorReadings(landId);
        replaceForLand(latestSensorReadings, landId, items);
        return items;
    } finally {
        latestReadingsLoading.value = false;
    }
};

const loadThresholds = async landId => {
    const items = await listEnvironmentThresholds(landId);
    replaceForLand(environmentThresholds, landId, items);
    return items;
};

const loadIrrigation = async landId => {
    const [configItems, recordItems] = await Promise.all([
        listIrrigationConfigs(landId),
        listIrrigationRecords({ landId })
    ]);
    replaceForLand(irrigationConfigs, landId, configItems);
    replaceForLand(irrigationRecords, landId, recordItems);
    return { configs: configItems, records: recordItems };
};

const loadAlerts = async landId => {
    const items = await listAlerts({ landId });
    replaceForLand(alerts, landId, items);
    return items;
};

const loadTasks = async landId => {
    const items = await listFarmTasks({ landId });
    replaceForLand(farmTasks, landId, items);
    return items;
};

const loadLandModules = async landId => {
    if (!landId) return;
    landModulesLoading.value = true;
    try {
        await Promise.all([
            loadPlans(landId),
            loadReadings(landId),
            loadLatestReadings(landId),
            loadThresholds(landId),
            loadIrrigation(landId),
            loadAlerts(landId),
            loadTasks(landId)
        ]);
    } finally {
        landModulesLoading.value = false;
    }
};

const addLand = async form => {
    const item = await createLandApi(normalizeLandForm(form));
    lands.value.push(item);
    return item;
};

const updateLand = async (id, form) => replaceById(
    lands,
    await updateLandApi(id, normalizeLandForm(form))
);

const deleteLand = async id => {
    await deleteLandApi(id);
    lands.value = lands.value.filter(item => item.id !== id);
};

const addDevice = async form => replaceById(
    devices,
    await createDeviceApi(normalizeDeviceForm(form))
);

const updateDevice = async (id, form) => replaceById(
    devices,
    await updateDeviceApi(id, normalizeDeviceForm(form))
);

const deleteDevice = async id => {
    await deleteDeviceApi(id);
    devices.value = devices.value.filter(item => item.id !== id);
};

const addPlan = async form => replaceById(plans, await createPlantingPlan(form));
const updatePlan = async (id, form) => replaceById(plans, await updatePlantingPlan(id, form));
const changePlanStatus = async (id, status) => replaceById(
    plans,
    await updatePlantingPlanStatus(id, { status })
);
const removePlan = async id => {
    await deletePlantingPlan(id);
    plans.value = plans.value.filter(item => item.id !== id);
};

const addThreshold = async (landId, form) => {
    const item = await createEnvironmentThreshold(landId, form);
    const index = environmentThresholds.value.findIndex(
        current => current.landId === landId && current.metric === item.metric
    );
    if (index === -1) environmentThresholds.value.push(item);
    else environmentThresholds.value[index] = item;
    return item;
};
const editThreshold = async (landId, metric, form) => {
    const item = await updateEnvironmentThreshold(landId, metric, form);
    const index = environmentThresholds.value.findIndex(
        current => current.landId === landId && current.metric === metric
    );
    if (index === -1) environmentThresholds.value.push(item);
    else environmentThresholds.value[index] = item;
    return item;
};
const removeThreshold = async (landId, metric) => {
    await deleteEnvironmentThreshold(landId, metric);
    environmentThresholds.value = environmentThresholds.value.filter(
        item => item.landId !== landId || item.metric !== metric
    );
};

const saveIrrigationConfig = async form => {
    const body = {
        name: form.name.trim(),
        controllerDeviceIds: form.controllerDeviceIds,
        mode: form.mode,
        enabled: Boolean(form.enabled),
        triggerMoisture: Number(form.triggerMoisture),
        targetMoisture: Number(form.targetMoisture),
        defaultDuration: Number(form.defaultDuration)
    };
    const response = form.id
        ? await updateIrrigationConfig(form.landId, form.id, body)
        : await createIrrigationConfig(form.landId, body);
    return replaceById(irrigationConfigs, response);
};

const activateIrrigationConfig = async config => {
    await enableIrrigationConfig(config.landId, config.id);
    await loadIrrigation(config.landId);
};

const deleteIrrigationConfig = async config => {
    await deleteIrrigationConfigApi(config.landId, config.id);
    irrigationConfigs.value = irrigationConfigs.value.filter(item => item.id !== config.id);
};

const startManualIrrigation = async (landId, controllerDeviceId, plannedDuration) => {
    const batch = await startIrrigation({
        landId,
        controllerDeviceIds: [controllerDeviceId],
        plannedDuration: Number(plannedDuration)
    });
    batch.records.forEach(record => replaceById(irrigationRecords, record));
    return batch;
};

const stopManualIrrigation = async record => {
    await stopIrrigation(record.id);
    await loadIrrigation(record.landId);
};

const addAlert = async form => replaceById(alerts, await createAlert(form));
const beginAlert = async (id, form) => {
    const response = await startAlert(id, form);
    replaceById(alerts, response.alert);
    if (response.createdTask) replaceById(farmTasks, response.createdTask);
    return response;
};
const finishAlert = async (alert, form) => {
    await resolveAlert(alert.id, form);
    await loadAlerts(alert.landId);
};
const dismissAlert = async (alert, form) => {
    await ignoreAlert(alert.id, form);
    await loadAlerts(alert.landId);
};

const addTask = async form => replaceById(farmTasks, await createFarmTask(form));
const beginTask = async id => replaceById(farmTasks, await startFarmTask(id));
const finishTask = async (id, result) => replaceById(
    farmTasks,
    await completeFarmTask(id, { result })
);
const dismissTask = async (id, reason) => replaceById(
    farmTasks,
    await cancelFarmTask(id, { reason })
);

const loadReports = async (filters = {}) => {
    reports.value = await listReports(filters);
    return reports.value;
};
const loadReport = async id => replaceById(reports, await getReport(id));
const createReport = async form => replaceById(reports, await generateReportApi(form));
const archiveReport = async id => replaceById(reports, await archiveReportApi(id));

const loadConversations = async landId => {
    const items = await listConversations({ landId, status: 'active' });
    replaceForLand(aiConversations, landId, items);
    return items;
};
const loadConversation = async id => replaceById(aiConversations, await getConversation(id));
const createAiConversation = async form => replaceById(aiConversations, await createConversation(form));
const sendAiMessage = async (conversationId, content) => {
    const response = await sendMessage(conversationId, { content });
    const conversation = aiConversations.value.find(item => item.id === conversationId);
    if (conversation) {
        conversation.messages ??= [];
        conversation.messages.push(response.userMessage, response.assistantMessage);
        conversation.updatedAt = response.assistantMessage.createdAt;
    }
    return response;
};
const closeAiConversation = async conversation => {
    await closeConversation(conversation.id);
    conversation.status = 'closed';
};
const createFarmTaskFromAiDraft = async ({ conversationId, messageId, assigneeId, deadline }) => {
    const task = await createTaskFromMessage(conversationId, messageId, { assigneeId, deadline });
    replaceById(farmTasks, task);
    return task;
};

const clearFarmData = () => {
    [lands, devices, sensorReadings, latestSensorReadings, environmentThresholds, plans, alerts,
        irrigationConfigs, irrigationRecords, farmTasks, reports, aiConversations]
        .forEach(state => { state.value = []; });
};

export const useFarmStore = () => ({
    lands, devices, sensorReadings, latestSensorReadings, environmentThresholds, plans, alerts,
    irrigationConfigs, irrigationRecords, farmTasks, reports, aiConversations,
    landsLoading, landsLoadError, devicesLoading, devicesLoadError, landModulesLoading, latestReadingsLoading,
    loadLands, loadDevices, loadPlans, loadReadings, loadLatestReadings, loadThresholds, loadIrrigation,
    loadAlerts, loadTasks, loadLandModules, loadReports, loadReport,
    loadConversations, loadConversation, clearFarmData,
    addLand, updateLand, deleteLand, addDevice, updateDevice, deleteDevice,
    addPlan, updatePlan, changePlanStatus, removePlan,
    addThreshold, editThreshold, removeThreshold,
    saveIrrigationConfig, activateIrrigationConfig, deleteIrrigationConfig,
    startManualIrrigation, stopManualIrrigation,
    addAlert, beginAlert, finishAlert, dismissAlert,
    addTask, beginTask, finishTask, dismissTask,
    createReport, archiveReport,
    createAiConversation, sendAiMessage, closeAiConversation,
    createFarmTaskFromAiDraft
});
