const ACTIVE_ALERT_STATUSES = new Set(['pending', 'processing']);
const ACTIVE_TASK_STATUSES = new Set(['pending', 'processing']);

const getLatestSensorReadings = (landId, sensorReadings) => {
    const latestReadings = new Map();

    sensorReadings
        .filter(reading => reading.landId === landId)
        .forEach(reading => {
            const key = `${reading.deviceId}:${reading.metric}`;
            const current = latestReadings.get(key);

            if (!current || reading.recordedAt > current.recordedAt) {
                latestReadings.set(key, reading);
            }
        });

    return Array.from(latestReadings.values()).map(reading => ({
        deviceId: reading.deviceId,
        metric: reading.metric,
        value: reading.value,
        unit: reading.unit,
        recordedAt: reading.recordedAt
    }));
};

const getRecentIrrigationRecords = (landId, irrigationRecords) => {
    return irrigationRecords
        .filter(record => record.landId === landId)
        .sort((a, b) => b.startedAt.localeCompare(a.startedAt))
        .slice(0, 5)
        .map(record => ({
            id: record.id,
            controllerDeviceId: record.controllerDeviceId,
            source: record.source,
            status: record.status,
            startedAt: record.startedAt,
            endedAt: record.endedAt,
            plannedDuration: record.plannedDuration,
            duration: record.duration,
            waterUsage: record.waterUsage,
            triggerReason: record.triggerReason
        }));
};

export const buildAiContext = ({
    landId,
    lands,
    devices,
    sensorReadings,
    environmentThresholds,
    irrigationConfigs,
    irrigationRecords,
    alerts,
    farmTasks,
    plans
}) => {
    const land = lands.find(item => item.id === landId);

    if (!land) {
        return null;
    }

    const irrigationConfig = irrigationConfigs.find(config => config.landId === landId);

    return {
        land: {
            id: land.id,
            name: land.name,
            type: land.type,
            area: land.area,
            crop: land.crop,
            status: land.status,
            location: land.location
        },
        devices: devices
            .filter(device => device.landId === landId)
            .map(device => ({
                id: device.id,
                name: device.name,
                type: device.type,
                status: device.status,
                battery: device.battery,
                lastReportedAt: device.lastReportedAt
            })),
        sensorReadings: getLatestSensorReadings(landId, sensorReadings),
        thresholds: environmentThresholds
            .filter(threshold => threshold.landId === landId && threshold.enabled)
            .map(threshold => ({
                metric: threshold.metric,
                min: threshold.min,
                max: threshold.max,
                updatedAt: threshold.updatedAt
            })),
        irrigationConfig: irrigationConfig ? { ...irrigationConfig } : null,
        recentIrrigationRecords: getRecentIrrigationRecords(landId, irrigationRecords),
        activeAlerts: alerts
            .filter(alert => alert.landId === landId && ACTIVE_ALERT_STATUSES.has(alert.status))
            .map(alert => ({
                id: alert.id,
                type: alert.type,
                severity: alert.severity,
                title: alert.title,
                description: alert.description,
                suggestion: alert.suggestion,
                status: alert.status,
                occurredAt: alert.occurredAt,
                source: { ...alert.source }
            })),
        activeTasks: farmTasks
            .filter(task => task.landId === landId && ACTIVE_TASK_STATUSES.has(task.status))
            .map(task => ({
                id: task.id,
                sourceType: task.sourceType,
                sourceId: task.sourceId,
                taskType: task.taskType,
                title: task.title,
                description: task.description,
                priority: task.priority,
                status: task.status,
                assignee: task.assignee,
                deadline: task.deadline
            })),
        plans: plans
            .filter(plan => plan.landId === landId)
            .map(plan => ({
                id: plan.id,
                planName: plan.planName,
                cropType: plan.cropType,
                area: plan.area,
                plantingTime: plan.plantingTime,
                expectedHarvestTime: plan.expectedHarvestTime,
                status: plan.status
            }))
    };
};
