const isTemperatureQuestion = question => {
    return question.includes('温度') || question.includes('高温');
};

const createFallbackResponse = () => {
    return {
        content: '当前 mock Agent 暂时无法判断这个问题。后续会继续补充环境、设备、灌溉和病虫害处理规则。',
        references: [],
        taskDraft: null
    };
};

export const generateMockAiResponse = ({ question, context }) => {
    const normalizedQuestion = question.trim();

    if (!normalizedQuestion || !context) {
        return createFallbackResponse();
    }

    if (!isTemperatureQuestion(normalizedQuestion)) {
        return createFallbackResponse();
    }

    const temperatureAlert = context.activeAlerts.find(alert =>
        alert.source.metric === 'air_temperature'
    );

    const temperatureThreshold = context.thresholds.find(threshold =>
        threshold.metric === 'air_temperature'
    );

    if (!temperatureAlert || !temperatureThreshold) {
        return createFallbackResponse();
    }

    return {
        content: `当前空气温度为 ${temperatureAlert.source.value}${temperatureAlert.source.unit}，已经超过适宜范围上限。建议先进行现场巡查，确认高温持续情况、作物状态和土壤墒情，再决定是否补水。`,
        references: [
            {
                type: 'alert',
                sourceId: temperatureAlert.id,
                label: '预警记录温度',
                value: temperatureAlert.source.value,
                unit: temperatureAlert.source.unit
            },
            {
                type: 'environmentThreshold',
                sourceId: `${context.land.id}:air_temperature`,
                label: '适宜空气温度',
                value: `${temperatureThreshold.min}～${temperatureThreshold.max}`,
                unit: temperatureAlert.source.unit
            },
            {
                type: 'alert',
                sourceId: temperatureAlert.id,
                label: '关联预警',
                value: temperatureAlert.title,
                unit: ''
            }
        ],
        taskDraft: {
            taskType: 'inspection',
            title: `巡查${context.land.name}高温影响`,
            description: '检查高温持续情况、作物状态和土壤墒情，根据现场情况决定是否补水。',
            priority: temperatureAlert.severity,
            assignee: '',
            deadline: ''
        }
    };
};
