<template>
  <div>
    <div class="flex flex-col md:flex-row gap-6">
      <!-- 左侧导航 -->
      <aside class="md:w-64 bg-white rounded-xl card-shadow p-4 flex-shrink-0">
        <nav class="space-y-1">
          <button type="button" @click="activeSection = 'dashboard'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'dashboard' }">
            <i class="fa fa-dashboard w-5 text-center flex-shrink-0"></i>
            <span>数据总览</span>
          </button>
          <button type="button" @click="activeSection = 'planting-plan'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'planting-plan' }">
            <i class="fa fa-calendar-check-o w-5 text-center flex-shrink-0"></i>
            <span>种植规划</span>
          </button>
          <button type="button" @click="activeSection = 'environment-monitor'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'environment-monitor' }">
            <i class="fa fa-thermometer-half w-5 text-center flex-shrink-0"></i>
            <span>环境监控</span>
          </button>
          <button type="button" @click="activeSection = 'smart-irrigation'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'smart-irrigation' }">
            <i class="fa fa-tint w-5 text-center flex-shrink-0"></i>
            <span>智能灌溉</span>
          </button>
          <button type="button" @click="activeSection = 'alert-center'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'alert-center' }">
            <i class="fa fa-exclamation-triangle text-danger w-5 text-center flex-shrink-0"></i>
            <span>异常预警</span>
            <span class="ml-auto bg-danger text-white text-xs px-2 py-1 rounded-full">{{
              unhandledWarningCount }}</span>
          </button>
          <button type="button" @click="activeSection = 'handle-record'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'handle-record' }">
            <i class="fa fa-list-alt w-5 text-center flex-shrink-0"></i>
            <span>处理记录</span>
          </button>
        </nav>

        <!-- 土地选择 -->
        <div class="mt-3 px-4 shadow-none">
          <label for="current-land" class="block text-sm font-medium text-gray-700 mb-2">当前土地</label>
          <select id="current-land" v-model="selectedLandId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm shadow-none focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option v-for="land in lands" :key="land.id" :value="land.id">
              {{ land.name }}
            </option>
          </select>
        </div>

        <!-- 农场信息卡片 -->
        <div class="mt-3 bg-primary/5 rounded-xl p-4">
          <h3 class="font-medium text-primary mb-3">农场信息</h3>
          <div class="space-y-2 text-sm">
            <div class="flex justify-between">
              <span class="text-gray-600">土地类型</span>
              <span class="font-medium">{{ currentLand.type }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">种植面积</span>
              <span class="font-medium">{{ currentLand.area }} 亩</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">当前作物</span>
              <span class="font-medium">{{ currentLand.crop }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">传感器数量</span>
              <span class="font-medium">{{ currentLandDevices.length }} 个</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">预警处理率</span>
              <span class="font-medium text-primary">{{ warningHandleRate }}%</span>
            </div>
          </div>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <main class="flex-1 space-y-8">
        <PlantingDashboard v-if="activeSection === 'dashboard'"
          :dashboard-environment-summary="dashboardEnvironmentSummary" :unhandled-warning-count="unhandledWarningCount"
          :current-land-devices="currentLandDevices" :current-land-online-device-count="currentLandOnlineDeviceCount"
          :current-land-offline-device-count="currentLandOfflineDeviceCount" :current-land-low-battery-device-count="currentLandLowBatteryDeviceCount" />

        <PlantingPlan v-if="activeSection === 'planting-plan'" :land-id="selectedLandId" />

        <EnvironmentMonitor v-if="activeSection === 'environment-monitor'"
        :land-id="selectedLandId"
        v-model:sensor-trend-range="sensorTrendRange"
        v-model:selected-trend-metric="selectedTrendMetric"
        :realtime-indicators="realtimeIndicators"
          :sensor-status-labels="sensorStatusLabels" :trend-metric-options="trendMetricOptions"
          :filtered-trend-readings="filteredTrendReadings" :visible-trend-metric-summaries="visibleTrendMetricSummaries"
          :available-metrics="availableMetrics" :current-land-thresholds="currentLandThresholds"
          :sensor-metric-labels="sensorMetricLabels" :current-land-sensor-rows="currentLandSensorRows"
          :device-status-labels="deviceStatusLabels" :get-sensor-status-class="getSensorStatusClass"
          :get-device-status-class="getDeviceStatusClass" :format-recorded-at="formatRecordedAt"
          :format-trend-point-time="formatTrendPointTime" @export-data="exportSensorTrendData" />

          <SmartIrrigation v-if="activeSection === 'smart-irrigation'" :land-id="selectedLandId" />

        <AlertCenter v-if="activeSection === 'alert-center'" :land-id="selectedLandId" />

        <HandleRecord v-if="activeSection === 'handle-record'" :land-id="selectedLandId" />
      </main>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useFarmStore } from "../composables/useFarmStore";
import PlantingDashboard from "./planting/PlantingDashboard.vue";
import PlantingPlan from "./planting/PlantingPlan.vue";
import EnvironmentMonitor from "./planting/EnvironmentMonitor.vue";
import AlertCenter from "./planting/AlertCenter.vue";
import HandleRecord from "./planting/HandleRecord.vue";
import SmartIrrigation from "./planting/SmartIrrigation.vue";

const { lands, devices, sensorReadings, environmentThresholds, alerts } = useFarmStore();

const sensorMetricLabels = {
  soil_moisture: '土壤湿度',
  air_temperature: '空气温度',
  air_humidity: '空气湿度',
  light: '光照强度',
  soil_ph: '土壤 pH'
};

const sensorStatusLabels = {
  no_data: '暂无数据',
  unmatched_data_type: '指标不匹配',
  low: '偏低',
  high: '偏高',
  normal: '适宜'
};

const deviceStatusLabels = {
  online: '在线',
  offline: '离线'
};

const sensorTrendRangeDays = {
  day: 1,
  week: 7,
  month: 30,
  season: 90
};

const selectedTrendMetric = ref('all');
const activeSection = ref('dashboard');
const selectedLandId = ref(lands.value[0]?.id || '');
const sensorTrendRange = ref('day');

watch(selectedLandId, () => {
  selectedTrendMetric.value = 'all';
});

watch(sensorTrendRange, () => {
  selectedTrendMetric.value = 'all';
});

const dashboardEnvironmentSummary = computed(() => {
  const soilMoisture = latestSensorReadings.value.soil_moisture;
  const airTemperature = latestSensorReadings.value.air_temperature;
  const airHumidity = latestSensorReadings.value.air_humidity;

  return {
    soilMoisture: {
      value: soilMoisture?.value ?? '--',
      unit: soilMoisture?.unit ?? '%'
    },
    airTemperature: {
      value: airTemperature?.value ?? '--',
      unit: airTemperature?.unit ?? '℃'
    },
    airHumidity: {
      value: airHumidity?.value ?? '--',
      unit: airHumidity?.unit ?? '%'
    }
  };
});

const availableMetrics = computed(() => {
  const existedMetrics = new Set(
    environmentThresholds.value
      .filter(threshold => threshold.landId === selectedLandId.value)
      .map(threshold => threshold.metric)
  );

  return Object.keys(sensorMetricLabels).filter(
    metric => !existedMetrics.has(metric)
  );
});

const currentLand = computed(() => {
  return lands.value.find(land => land.id === selectedLandId.value);
});

const getLandName = (landId) => {
  return lands.value.find(land => land.id === landId)?.name || '未绑定地块';
};

const currentLandDevices = computed(() => {
  return devices.value.filter(device => device.landId === selectedLandId.value);
});

const currentLandOnlineDeviceCount = computed(() => {
  return currentLandDevices.value.filter(device => device.status === 'online').length;
});

const currentLandOfflineDeviceCount = computed(() => {
  return currentLandDevices.value.filter(device => device.status === 'offline').length;
});

const currentLandLowBatteryDeviceCount = computed(() => {
  return currentLandDevices.value.filter(device => device.battery !== null && device.battery < 20).length;
});

const currentLandThresholds = computed(() => environmentThresholds.value.filter(threshold => threshold.landId === selectedLandId.value));

const activeCurrentLandThresholds = computed(() => currentLandThresholds.value.filter(threshold => threshold.enabled));

const getReadingStatus = (reading, threshold) => {
  if (!reading || !Number.isFinite(reading.value)) {
    return 'no_data';
  }
  if (reading.metric !== threshold.metric) {
    return 'unmatched_data_type';
  }
  if (reading.value <= threshold.min) {
    return 'low';
  }
  if (reading.value >= threshold.max) {
    return 'high';
  }
  return 'normal';
};

const getSensorStatusClass = (status) => {
  const classes = {
    no_data: 'bg-gray-100 text-gray-600',
    unmatched_data_type: 'bg-purple-100 text-purple-700',
    low: 'bg-blue-100 text-blue-700',
    high: 'bg-red-100 text-red-700',
    normal: 'bg-green-100 text-green-700'
  };

  return classes[status] || 'bg-gray-100 text-gray-600';
};

const getDeviceStatusClass = (status) => {
  const classes = {
    online: 'bg-green-100 text-green-800',
    offline: 'bg-gray-100 text-gray-600'
  };

  return classes[status] || 'bg-gray-100 text-gray-600';
};

const formatRecordedAt = (recordedAt) => {
  if (!recordedAt) {
    return '暂无数据';
  }

  const recordedTime = new Date(recordedAt);
  if (!Number.isFinite(recordedTime.getTime())) {
    return '时间格式异常';
  }

  const parts = new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).formatToParts(recordedTime);

  const dateTime = Object.fromEntries(parts.map(part => [part.type, part.value]));

  return `${dateTime.year} 年 ${dateTime.month} 月 ${dateTime.day} 日 ${dateTime.hour}:${dateTime.minute}`;
};

const formatTrendPointTime = (recordedAt) => {
  if (!recordedAt) {
    return '';
  }

  const recordedTime = new Date(recordedAt);
  if (!Number.isFinite(recordedTime.getTime())) {
    return '';
  }

  const parts = new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).formatToParts(recordedTime);

  const dateTime = Object.fromEntries(parts.map(part => [part.type, part.value]));

  return `${dateTime.month} 月 ${dateTime.day} 日 ${dateTime.hour}:${dateTime.minute}`;
};

const currentLandAlerts = computed(() => {
  return alerts.value.filter(alert => alert.landId === selectedLandId.value);
});

const currentLandSensorReadings = computed(() => sensorReadings.value.filter(reading => reading.landId === selectedLandId.value));

const unhandledWarningCount = computed(() => {
  return currentLandAlerts.value.filter(alert => alert.status === 'pending' || alert.status === 'processing').length;
});

const endedAlertCount = computed(() => {
  return currentLandAlerts.value.filter(alert => alert.status === 'resolved' || alert.status === 'ignored').length;
});

const warningHandleRate = computed(() => {
  if (currentLandAlerts.value.length === 0) {
    return 0;
  }

  return Math.round((endedAlertCount.value / currentLandAlerts.value.length) * 100);
});

const realtimeIndicators = computed(() => {
  return activeCurrentLandThresholds.value.map(threshold => {
    const reading = latestSensorReadings.value[threshold.metric];

    return {
      metric: threshold.metric,
      label: sensorMetricLabels[threshold.metric] || threshold.metric,
      value: reading?.value ?? null,
      unit: reading?.unit ?? '',
      recordedAt: reading?.recordedAt ?? null,
      min: threshold.min,
      max: threshold.max,
      status: getReadingStatus(reading, threshold)
    };
  });
});

const filteredTrendReadings = computed(() => {
  const days = sensorTrendRangeDays[sensorTrendRange.value];
  const startTime = Date.now() - days * 24 * 60 * 60 * 1000;

  return currentLandSensorReadings.value.filter(reading => {
    return new Date(reading.recordedAt).getTime() >= startTime;
  });
});

const trendReadingsByMetric = computed(() => {
  const result = filteredTrendReadings.value.reduce((groups, reading) => {
    if (!groups[reading.metric]) {
      groups[reading.metric] = [];
    }

    groups[reading.metric].push({
      time: reading.recordedAt,
      value: reading.value,
      unit: reading.unit,
      deviceId: reading.deviceId
    });

    return groups;
  }, {});

  Object.values(result).forEach(readings => {
    readings.sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime());
  });

  return result;
});

const trendMetricSummaries = computed(() => {
  return Object.entries(trendReadingsByMetric.value).map(([metric, readings]) => {
    const firstReading = readings[0];
    const latestReading = readings[readings.length - 1];

    return {
      metric,
      label: sensorMetricLabels[metric] || metric,
      unit: latestReading?.unit || firstReading?.unit || '',
      count: readings.length,
      latestValue: latestReading?.value ?? null,
      points: readings.map(reading => ({
        time: reading.time,
        value: reading.value
      }))
    };
  });
});

const trendMetricOptions = computed(() => {
  return [
    { value: 'all', label: '全部指标' },
    ...trendMetricSummaries.value.map(summary => ({
      value: summary.metric,
      label: summary.label
    }))
  ];
});

const visibleTrendMetricSummaries = computed(() => {
  if (selectedTrendMetric.value === 'all') {
    return trendMetricSummaries.value;
  }
  return trendMetricSummaries.value.filter(summary => summary.metric === selectedTrendMetric.value);
});

const exportableTrendReadings = computed(() => {
  if (selectedTrendMetric.value === 'all') {
    return filteredTrendReadings.value;
  }

  return filteredTrendReadings.value.filter(reading => reading.metric === selectedTrendMetric.value);
});

const latestSensorReadings = computed(() => {
  return currentLandSensorReadings.value.reduce((result, reading) => {
    const previousReading = result[reading.metric];
    if (!previousReading || new Date(reading.recordedAt).getTime() > new Date(previousReading.recordedAt).getTime()) {
      result[reading.metric] = reading;
    }
    return result;
  }, {});
});

const currentLandSensorRows = computed(() => {
  return currentLandDevices.value.map(device => {
    const latestReadings = Object.values(latestSensorReadings.value).filter(reading => reading.deviceId === device.id);

    const latestUpdatedAt = latestReadings.reduce((latestTime, reading) => {
      if (!latestTime) {
        return reading.recordedAt;
      }

      return new Date(reading.recordedAt).getTime() > new Date(latestTime).getTime() ? reading.recordedAt : latestTime;
    }, null);

    return {
      id: device.id,
      name: device.name,
      location: getLandName(device.landId),
      metrics: latestReadings.map(reading => sensorMetricLabels[reading.metric] || reading.metric).join('、') || '暂无数据',
      latestData: latestReadings.map(reading => `${sensorMetricLabels[reading.metric] || reading.metric}: ${reading.value} ${reading.unit}`).join('，') || '暂无数据',
      updatedAt: latestUpdatedAt,
      status: device.status
    };
  });
});

const exportSensorTrendData = () => {
  if (exportableTrendReadings.value.length === 0) {
    window.alert('当前筛选条件下没有可导出的传感器数据');
    return;
  }

  const exportData = {
    landId: selectedLandId.value,
    landName: currentLand.value?.name || '',
    range: sensorTrendRange.value,
    metric: selectedTrendMetric.value,
    exportedAt: new Date().toISOString(),
    readings: exportableTrendReadings.value
  };

  const content = JSON.stringify(exportData, null, 2);
  const blob = new Blob([content], { type: 'application/json;charset=utf-8' });
  const url = URL.createObjectURL(blob);

  const link = document.createElement('a');
  link.href = url;
  link.download = `sensor-readings-${selectedLandId.value}-${sensorTrendRange.value}.json`;
  link.click();

  URL.revokeObjectURL(url);
};

</script>
