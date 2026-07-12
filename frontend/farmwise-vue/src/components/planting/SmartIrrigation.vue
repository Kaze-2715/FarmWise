<template>
  <section class="rounded-xl bg-white p-6 card-shadow">
    <div class="mb-6 flex items-center justify-between">
      <h2 class="text-xl font-bold text-gray-800">智能灌溉</h2>
      <span v-if="currentIrrigationConfig" class="rounded-full px-3 py-1 text-xs font-medium"
        :class="currentIrrigationConfig.enabled ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'">
        {{ currentIrrigationConfig.enabled ? '自动灌溉已启用' : '自动灌溉未启用' }}
      </span>
    </div>

    <div v-if="!currentIrrigationConfig"
      class="rounded-xl border border-dashed border-gray-200 bg-gray-50 p-6 text-center text-sm text-gray-500">
      当前地块尚未配置智能灌溉规则。
    </div>

    <div v-else class="rounded-xl border border-blue-100 bg-blue-50/60 p-5">
      <div class="mb-5 flex flex-col gap-3 border-b border-blue-100 pb-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p class="text-sm text-gray-500">灌溉控制器</p>
          <h3 class="mt-1 font-semibold text-gray-800">
            {{ currentController?.name || currentIrrigationConfig.controllerDeviceId }}
          </h3>
        </div>
        <span class="w-fit rounded-full px-2.5 py-1 text-xs font-medium"
          :class="currentController?.status === 'online'
            ? 'bg-green-100 text-green-700'
            : 'bg-gray-200 text-gray-600'">
          {{ currentController?.status === 'online' ? '设备在线' : '设备离线' }}
        </span>
      </div>

      <div class="grid grid-cols-2 gap-4 md:grid-cols-3 xl:grid-cols-6">
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">运行模式</p>
          <p class="mt-1 text-lg font-semibold text-gray-800">
            {{ currentIrrigationConfig.mode === 'automatic' ? '自动模式' : '手动模式' }}
          </p>
        </div>
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">当前土壤湿度</p>
          <p class="mt-1 text-lg font-semibold text-blue-700">
            {{ latestSoilMoisture ? `${latestSoilMoisture.value}${latestSoilMoisture.unit}` : '--' }}
          </p>
        </div>
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">灌溉需求</p>
          <p class="mt-1 text-lg font-semibold"
            :class="irrigationNeedClasses[irrigationNeedStatus] || irrigationNeedClasses.no_data">
            {{ irrigationNeedLabels[irrigationNeedStatus] || irrigationNeedLabels.no_data }}
          </p>
        </div>
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">触发湿度</p>
          <p class="mt-1 text-lg font-semibold text-blue-700">
            ≤ {{ currentIrrigationConfig.triggerMoisture }}%
          </p>
        </div>
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">目标湿度</p>
          <p class="mt-1 text-lg font-semibold text-green-700">
            {{ currentIrrigationConfig.targetMoisture }}%
          </p>
        </div>
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">默认灌溉时长</p>
          <p class="mt-1 text-lg font-semibold text-gray-800">
            {{ currentIrrigationConfig.defaultDuration }} 分钟
          </p>
        </div>
      </div>

      <div class="mt-4 flex flex-wrap items-center justify-between gap-3 border-t border-blue-100 pt-4">
        <p class="text-xs text-gray-400">
          最后更新：{{ currentIrrigationConfig.updatedBy || '未记录' }} ·
          {{ formatDateTime(currentIrrigationConfig.updatedAt) }}
        </p>
        <div class="flex items-center gap-2">
          <button type="button" :disabled="currentController?.status !== 'online'"
            class="rounded-lg px-4 py-2 text-sm transition-colors"
            :class="currentController?.status === 'online'
              ? 'bg-blue-600 text-white hover:bg-blue-700'
              : 'cursor-not-allowed bg-gray-200 text-gray-400'"
            @click="openManualControl(currentController)">
            {{ getManualControlButtonLabel(currentController?.id) }}
          </button>
          <button type="button"
            class="rounded-lg border border-blue-200 px-4 py-2 text-sm text-blue-700 transition-colors hover:bg-blue-50"
            @click="openConfigModal">
            编辑灌溉配置
          </button>
        </div>
      </div>
    </div>

    <div class="mt-6 rounded-xl border border-gray-100 bg-gray-50 p-5">
      <h3 class="mb-4 font-medium text-gray-800">灌溉执行记录</h3>

      <div v-if="currentIrrigationRecords.length === 0" class="rounded-lg border border-dashed border-gray-200 bg-white
      p-6 text-center text-sm text-gray-400">
        当前地块暂无灌溉记录
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="border-b border-gray-200 text-gray-500">
            <tr>
              <th class="px-3 py-3 font-medium">启动时间</th>
              <th class="px-3 py-3 font-medium">执行方式</th>
              <th class="px-3 py-3 font-medium">状态</th>
              <th class="px-3 py-3 font-medium">计划时长</th>
              <th class="px-3 py-3 font-medium">实际时长</th>
              <th class="px-3 py-3 font-medium">操作人</th>
            </tr>
          </thead>

          <tbody class="divide-y divide-gray-100 bg-white">
            <tr v-for="record in currentIrrigationRecords" :key="record.id" class="text-gray-700">
              <td class="px-3 py-3">
                {{ formatDateTime(record.startedAt) }}
              </td>
              <td class="px-3 py-3">
                {{ record.source === 'automatic' ? '自动灌溉' : '手动灌溉' }}
              </td>
              <td class="px-3 py-3">
                <span class="inline-flex rounded-full px-2.5 py-1 text-xs font-medium"
                  :class="irrigationStatusClasses[record.status] || irrigationStatusClasses.unknown">
                  {{ irrigationStatusLabels[record.status] || record.status || '未知状态' }}
                </span>
              </td>
              <td class="px-3 py-3">
                {{ record.plannedDuration }} 分钟
              </td>
              <td class="px-3 py-3">
                {{ record.status === 'completed' ? `${record.duration}
                分钟` : '--' }}
              </td>
              <td class="px-3 py-3">
                {{ record.operator || '未记录' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>

  <div v-if="manualControlModalVisible && selectedController"
    class="fixed inset-0 z-50 flex items-center justify-center bg-white/30 backdrop-blur-sm">
    <div class="mx-4 w-full max-w-md overflow-hidden rounded-xl bg-white shadow-xl">
      <div class="flex items-center justify-between border-b border-gray-200 p-6">
        <div>
          <h3 class="text-lg font-bold text-gray-800">手动灌溉</h3>
          <p class="mt-1 text-sm text-gray-500">{{ selectedController.name }}</p>
        </div>
        <button type="button" class="text-gray-400 hover:text-gray-600" @click="closeManualControl">
          <i class="fa fa-times"></i>
        </button>
      </div>

      <div class="p-6">
        <label class="block">
          <span class="mb-1.5 block text-sm font-medium text-gray-700">灌溉时长</span>
          <div class="relative">
            <input v-model.number="manualDuration" type="number" min="1" max="180" :disabled="hasRunningIrrigation"
              class="w-full rounded-lg border border-gray-300 px-3 py-2 pr-14 outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 disabled:bg-gray-100">
            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-400">分钟</span>
          </div>
        </label>

        <p v-if="hasRunningIrrigation" class="mt-3 text-sm text-blue-600">
          该控制器正在灌溉，可以提前停止任务。
        </p>

        <div class="mt-6 flex justify-end gap-3">
          <button type="button" class="rounded-lg border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
            @click="closeManualControl">取消</button>
          <button type="button" :disabled="manualControlDisabled"
            class="rounded-lg px-4 py-2 text-sm font-medium text-white"
            :class="manualControlDisabled
              ? 'cursor-not-allowed bg-gray-300'
              : hasRunningIrrigation ? 'bg-red-500 hover:bg-red-600' : 'bg-blue-600 hover:bg-blue-700'"
            @click="handleManualIrrigation">
            {{ getManualControlButtonLabel(selectedController.id) }}
          </button>
        </div>
      </div>
    </div>
  </div>

  <div v-if="configModalVisible"
    class="fixed inset-0 z-50 flex items-center justify-center bg-white/30 backdrop-blur-sm">
    <div class="mx-4 w-full max-w-lg overflow-hidden rounded-xl bg-white shadow-xl">
      <div class="flex items-center justify-between border-b border-gray-200 p-6">
        <div>
          <h3 class="text-lg font-bold text-gray-800">编辑灌溉配置</h3>
          <p class="mt-1 text-sm text-gray-500">调整当前地块的智能灌溉规则</p>
        </div>
        <button type="button" class="text-gray-400 hover:text-gray-600" @click="closeConfigModal">
          <i class="fa fa-times"></i>
        </button>
      </div>

      <form class="p-6" @submit.prevent="submitIrrigationConfig">
        <div class="mb-4">
          <label class="mb-1.5 block text-sm font-medium text-gray-700">运行模式</label>
          <select v-model="irrigationConfigForm.mode"
            class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100">
            <option value="manual">手动模式</option>
            <option value="automatic">自动模式</option>
          </select>
        </div>

        <label class="mb-4 flex items-center justify-between rounded-lg border border-gray-200 p-4"
          :class="irrigationConfigForm.mode !== 'automatic' ? 'bg-gray-50' : 'bg-white'">
          <div>
            <span class="block text-sm font-medium text-gray-700">启用自动灌溉</span>
            <span class="mt-1 block text-xs text-gray-400">手动模式下自动关闭</span>
          </div>
          <input v-model="irrigationConfigForm.enabled" type="checkbox"
            :disabled="irrigationConfigForm.mode !== 'automatic'"
            class="h-4 w-4 rounded border-gray-300 text-blue-600 disabled:cursor-not-allowed">
        </label>

        <div class="mb-4 grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <label class="mb-1.5 block text-sm font-medium text-gray-700">触发湿度（%）</label>
            <input v-model.number="irrigationConfigForm.triggerMoisture" type="number" min="0" max="100"
              class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100">
          </div>
          <div>
            <label class="mb-1.5 block text-sm font-medium text-gray-700">目标湿度（%）</label>
            <input v-model.number="irrigationConfigForm.targetMoisture" type="number" min="0" max="100"
              class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100">
          </div>
        </div>

        <div>
          <label class="mb-1.5 block text-sm font-medium text-gray-700">默认灌溉时长（分钟）</label>
          <input v-model.number="irrigationConfigForm.defaultDuration" type="number" min="1" max="180"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100">
        </div>

        <div class="mt-6 flex justify-end gap-3">
          <button type="button"
            class="rounded-lg border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
            @click="closeConfigModal">
            取消
          </button>
          <button type="submit" class="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700">
            保存配置
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useFarmStore } from '../../composables/useFarmStore';

const props = defineProps({
  landId: {
    type: String,
    required: true
  }
});

const { devices, sensorReadings, irrigationConfigs, irrigationRecords } = useFarmStore();

const manualDuration = ref(30);
const manualControlModalVisible = ref(false);
const selectedController = ref(null);
const configModalVisible = ref(false);
const irrigationConfigForm = ref({});

const irrigationStatusLabels = {
  pending: '待执行',
  running: '执行中',
  completed: '已完成',
  failed: '执行失败'
};

const irrigationStatusClasses = {
  pending: 'bg-amber-100 text-amber-700',
  running: 'bg-blue-100 text-blue-700',
  completed: 'bg-green-100 text-green-700',
  failed: 'bg-red-100 text-red-700',
  unknown: 'bg-gray-100 text-gray-600'
};

const irrigationNeedLabels = {
  needed: '需要灌溉',
  not_needed: '暂不需要',
  no_data: '暂无数据'
};

const irrigationNeedClasses = {
  needed: 'text-amber-600',
  not_needed: 'text-green-600',
  no_data: 'text-gray-400'
};

const currentIrrigationConfig = computed(() => {
  return irrigationConfigs.value.find(config => config.landId === props.landId) ?? null;
});

const currentIrrigationRecords = computed(() => {
  return irrigationRecords.value.filter(record => record.landId === props.landId);
});

const latestSoilMoisture = computed(() => {
  return sensorReadings.value.reduce((latestReading, reading) => {
    if (reading.landId !== props.landId || reading.metric !== 'soil_moisture') {
      return latestReading;
    }

    if (!latestReading || new Date(reading.recordedAt).getTime() > new Date(latestReading.recordedAt).getTime()) {
      return reading;
    }

    return latestReading;
  }, null);
});

const irrigationNeedStatus = computed(() => {
  if (!latestSoilMoisture.value || !currentIrrigationConfig.value) {
    return 'no_data';
  }

  return latestSoilMoisture.value.value <= currentIrrigationConfig.value.triggerMoisture
    ? 'needed'
    : 'not_needed';
});

const currentController = computed(() => {
  const controllerDeviceId = currentIrrigationConfig.value?.controllerDeviceId;

  if (!controllerDeviceId) {
    return null;
  }

  return devices.value.find(device => device.id === controllerDeviceId) ?? null;
});

const getRunningRecord = (controllerDeviceId) => {
  return currentIrrigationRecords.value.find(record =>
    record.controllerDeviceId === controllerDeviceId && record.status === 'running'
  ) ?? null;
};

const getManualControlButtonLabel = (controllerDeviceId) => {
  if (getRunningRecord(controllerDeviceId)) {
    return '停止灌溉';
  }

  return currentIrrigationConfig.value?.mode === 'automatic' ? '人工接管' : '开始灌溉';
};

const currentRunningIrrigation = computed(() => {
  return selectedController.value ? getRunningRecord(selectedController.value.id) : null;
});

const hasRunningIrrigation = computed(() => {
  return currentRunningIrrigation.value !== null;
});

const manualControlDisabled = computed(() => {
  return selectedController.value?.status !== 'online'
    || (!hasRunningIrrigation.value && (
      !Number.isFinite(manualDuration.value)
      || manualDuration.value < 1
      || manualDuration.value > 180
    ));
});

watch(currentIrrigationConfig, (config) => {
  manualDuration.value = config?.defaultDuration ?? 30;
}, { immediate: true });

watch(() => irrigationConfigForm.value.mode, (mode) => {
  if (mode === 'manual') {
    irrigationConfigForm.value.enabled = false;
  }
});

const openManualControl = (controller) => {
  selectedController.value = controller;
  manualDuration.value = currentIrrigationConfig.value?.defaultDuration ?? 30;
  manualControlModalVisible.value = true;
};

const closeManualControl = () => {
  manualControlModalVisible.value = false;
  selectedController.value = null;
};

const openConfigModal = () => {
  irrigationConfigForm.value = {
    ...currentIrrigationConfig.value
  };
  configModalVisible.value = true;
};

const closeConfigModal = () => {
  configModalVisible.value = false;
  irrigationConfigForm.value = {};
};

const submitIrrigationConfig = () => {
  const form = irrigationConfigForm.value;
  const triggerMoisture = Number(form.triggerMoisture);
  const targetMoisture = Number(form.targetMoisture);
  const defaultDuration = Number(form.defaultDuration);

  if (!['manual', 'automatic'].includes(form.mode)) {
    return window.alert('请选择有效的运行模式');
  }
  if (!Number.isFinite(triggerMoisture) || triggerMoisture < 0 || triggerMoisture > 100) {
    return window.alert('触发湿度必须在 0 到 100 之间');
  }
  if (!Number.isFinite(targetMoisture) || targetMoisture < 0 || targetMoisture > 100) {
    return window.alert('目标湿度必须在 0 到 100 之间');
  }
  if (triggerMoisture >= targetMoisture) {
    return window.alert('触发湿度必须小于目标湿度');
  }
  if (!Number.isFinite(defaultDuration) || defaultDuration < 1 || defaultDuration > 180) {
    return window.alert('默认灌溉时长必须在 1 到 180 分钟之间');
  }

  const configIndex = irrigationConfigs.value.findIndex(config =>
    config.landId === form.landId && config.controllerDeviceId === form.controllerDeviceId
  );

  if (configIndex === -1) {
    return window.alert('灌溉配置不存在');
  }

  irrigationConfigs.value[configIndex] = {
    ...irrigationConfigs.value[configIndex],
    ...form,
    enabled: form.mode === 'automatic' && Boolean(form.enabled),
    triggerMoisture,
    targetMoisture,
    defaultDuration,
    updatedBy: localStorage.getItem('username') || '游客',
    updatedAt: new Date().toISOString()
  };

  closeConfigModal();
};

const startManualIrrigation = () => {
  if (manualControlDisabled.value || !selectedController.value) {
    return;
  }

  irrigationRecords.value.unshift({
    id: `IRR-${crypto.randomUUID()}`,
    landId: props.landId,
    controllerDeviceId: selectedController.value.id,
    source: 'manual',
    status: 'running',
    startedAt: new Date().toISOString(),
    endedAt: '',
    plannedDuration: manualDuration.value,
    duration: 0,
    waterUsage: null,
    triggerReason: currentIrrigationConfig.value?.mode === 'automatic'
      ? '自动模式下人工接管'
      : '手动启动灌溉',
    operator: localStorage.getItem('username') || '游客'
  });
};

const stopManualIrrigation = () => {
  const runningRecord = currentRunningIrrigation.value;

  if (!runningRecord || selectedController.value?.status !== 'online') {
    return;
  }

  const endedAt = new Date();
  const startedAt = new Date(runningRecord.startedAt);
  const duration = (endedAt.getTime() - startedAt.getTime()) / 60000;

  runningRecord.status = 'completed';
  runningRecord.endedAt = endedAt.toISOString();
  runningRecord.duration = Math.max(0, Number(duration.toFixed(1)));
  closeManualControl();
};

const handleManualIrrigation = () => {
  if (hasRunningIrrigation.value) {
    stopManualIrrigation();
    return;
  }

  startManualIrrigation();
};

const formatDateTime = (dateTime) => {
  if (!dateTime) {
    return '未记录';
  }

  return new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(new Date(dateTime));
};
</script>
