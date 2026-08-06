<template>
  <!-- 环境监控 -->
  <section class="min-w-0 rounded-xl bg-white p-6 card-shadow">
    <div class="mb-6 flex items-center justify-between">
      <h2 class="text-xl font-bold">环境监控</h2>
      <div class="flex space-x-2">
        <button
          class="inline-flex items-center rounded-lg border border-gray-300 px-3 py-1.5 text-sm text-gray-700 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow active:translate-y-0 active:shadow-sm"
          @click="emit('export-data')">
          <i class="fa fa-download mr-1"></i> 导出数据
        </button>
      </div>
    </div>

    <!-- 实时数据卡片 -->
    <div class="mb-6 grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
      <div v-if="realtimeIndicators.length === 0"
        class="col-span-full rounded-xl border border-dashed border-gray-200 bg-gray-50 p-6 text-center text-sm text-gray-500">
        当前地块还没有启用的环境阈值，请先配置监测标准。
      </div>
      <div v-for="indicator in realtimeIndicators" :key="indicator.metric"
        class="rounded-xl border border-gray-100 bg-gray-50 p-4">
        <div class="mb-3 flex items-start justify-between gap-3">
          <div>
            <h3 class="font-medium text-gray-800">{{ indicator.label }}</h3>
            <p class="mt-1 text-xs text-gray-500">
              建议区间：{{ formatSensorMetricValue(indicator.metric, indicator.min) }} - {{ formatSensorMetricValue(indicator.metric, indicator.max) }} {{ indicator.unit }}
            </p>
          </div>
          <span class="rounded-full px-2.5 py-1 text-xs font-medium" :class="getSensorStatusClass(indicator.status)">
            {{ sensorStatusLabels[indicator.status] }}
          </span>
        </div>

        <div class="flex items-end gap-2">
          <span class="text-3xl font-bold text-gray-900">{{ formatSensorMetricValue(indicator.metric, indicator.value) }}</span>
          <span class="pb-1 text-sm text-gray-500">{{ indicator.unit }}</span>
        </div>

        <p class="mt-3 text-xs text-gray-400">
          更新时间：{{ formatRecordedAt(indicator.recordedAt) }}
        </p>
      </div>
    </div>

    <!-- 历史趋势图表 -->
    <div class="mb-6 min-w-0 rounded-xl bg-gray-50 p-4">
      <div class="mb-4 flex items-center justify-between">
        <div>
          <h3 class="font-medium">历史趋势</h3>
          <p class="mt-1 text-xs text-gray-400">最近 24 小时 · 每 2 小时平均</p>
        </div>
        <div class="flex items-center gap-2">
          <select v-model="selectedTrendMetric"
            class="rounded-lg border border-gray-300 bg-white px-3 py-1 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option v-for="option in trendMetricOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>
      </div>
      <div class="h-[30rem] min-w-0 overflow-y-auto overflow-x-hidden rounded-lg border border-gray-200 bg-white p-4 text-sm">
        <div class="mb-3 text-gray-600">
          当前范围内共有 {{ filteredTrendReadings.length }} 个聚合趋势点
        </div>

        <div v-if="filteredTrendReadings.length === 0" class="flex h-64 items-center justify-center text-gray-400">
          当前时间范围暂无趋势数据
        </div>

        <div v-else-if="trendChartSeries.length === 0"
          class="flex h-64 items-center justify-center text-gray-400">
          当前指标暂无趋势数据
        </div>

        <div v-else class="grid min-w-0 grid-cols-1 gap-4 md:grid-cols-2">
          <SensorTrendChart v-for="series in trendChartSeries" :key="series.metric"
            :metric="series.metric" :label="series.label" :unit="series.unit" :color="series.color" :points="series.points" />

          <details class="min-w-0 rounded-xl border border-gray-100 bg-gray-50 md:col-span-2">
            <summary class="cursor-pointer px-4 py-3 font-medium text-gray-700">查看本页数据明细</summary>
            <div class="grid min-w-0 grid-cols-1 gap-4 border-t border-gray-100 p-4 lg:grid-cols-2">
              <div v-for="summary in visibleTrendMetricSummaries" :key="summary.metric"
                class="min-w-0 rounded-lg bg-white p-4 shadow-sm">
                <div class="mb-3 flex items-start justify-between">
                  <div>
                    <h4 class="font-medium text-gray-800">{{ summary.label }}</h4>
                    <p class="mt-1 text-xs text-gray-500">本页数据点：{{ summary.count }}</p>
                  </div>
                  <div class="text-right">
                    <div class="text-xl font-bold text-gray-900">
                      {{ formatSensorMetricValue(summary.metric, summary.latestValue) }} {{ summary.unit }}
                    </div>
                  </div>
                </div>

                <div class="flex min-w-0 flex-wrap gap-2">
                  <span v-for="point in summary.points" :key="point.time"
                    class="rounded bg-gray-100 px-2 py-1 text-xs text-gray-600">
                    {{ formatSensorMetricValue(summary.metric, point.value) }} ({{ formatTrendPointTime(point.time) }})
                  </span>
                </div>
              </div>
            </div>
          </details>
        </div>

        <div v-if="trendTotalCount > 0"
          class="mt-4 flex flex-col gap-3 border-t border-gray-100 pt-4 sm:flex-row sm:items-center sm:justify-between">
          <div class="flex items-center gap-2 text-sm text-gray-500">
            <span>共 {{ trendTotalCount }} 个趋势点</span>
            <select v-model.number="trendPageSize"
              class="rounded-lg border border-gray-200 bg-white px-2 py-1 text-sm text-gray-700">
              <option :value="50">50 条/页</option>
              <option :value="100">100 条/页</option>
              <option :value="200">200 条/页</option>
            </select>
          </div>
          <div class="flex items-center gap-2">
            <button type="button" :disabled="trendPage === 1" class="page-button" @click="trendPage = 1">首页</button>
            <button type="button" :disabled="trendPage === 1" class="page-button" @click="trendPage -= 1">上一页</button>
            <span class="min-w-24 text-center text-sm text-gray-600">{{ trendPage }} / {{ trendTotalPages }}</span>
            <button type="button" :disabled="trendPage === trendTotalPages" class="page-button" @click="trendPage += 1">下一页</button>
            <button type="button" :disabled="trendPage === trendTotalPages" class="page-button" @click="trendPage = trendTotalPages">末页</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 当前地块阈值规则 -->
    <div class="mb-6 rounded-xl border border-gray-100 bg-gray-50 p-4">
      <div class="mb-4 flex items-center justify-between">
        <h3 class="font-medium">当前地块阈值规则</h3>
        <button type="button" :disabled="availableMetrics.length === 0"
          class="rounded-lg px-3 py-1.5 text-sm transition-colors" :class="availableMetrics.length === 0
            ? 'cursor-not-allowed bg-gray-100 text-gray-400'
            : 'bg-green-500 text-white hover:bg-green-600'" @click="addThreshold">
          {{ availableMetrics.length === 0 ? '指标已全部配置' : '新增阈值规则' }}
        </button>
      </div>

      <div v-if="currentLandThresholds.length === 0"
        class="rounded-lg border border-dashed border-gray-200 bg-white p-4 text-center text-sm text-gray-400">
        当前地块暂无阈值规则
      </div>

      <div v-else class="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-3">
        <div v-for="threshold in currentLandThresholds" :key="`${threshold.landId}-${threshold.metric}`"
          class="rounded-lg bg-white p-4">
          <div class="mb-2 flex items-start justify-between">
            <div>
              <p class="font-medium text-gray-800">
                {{ sensorMetricLabels[threshold.metric] || threshold.metric }}
              </p>
              <p class="mt-1 text-sm text-gray-500">
                适宜范围：{{ threshold.min }} - {{ threshold.max }}
              </p>
            </div>
            <span class="rounded-full px-2 py-1 text-xs"
              :class="threshold.enabled ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'">
              {{ threshold.enabled ? '启用' : '停用' }}
            </span>
          </div>

          <div class="mt-3 text-xs text-gray-400">
            <div>创建人：{{ threshold.creator || '未记录' }}</div>
            <div>更新时间：{{ formatRecordedAt(threshold.updatedAt) }}</div>
          </div>
          <div class="mt-4 flex justify-end gap-2 border-t border-gray-100 pt-3">
            <button type="button"
              class="rounded-lg border border-green-200 px-3 py-1.5 text-sm text-green-700 transition-colors hover:border-green-300 hover:bg-green-50"
              @click="editThreshold(threshold)">
              编辑规则
            </button>
            <button type="button"
              class="rounded-lg border border-red-200 px-3 py-1.5 text-sm text-red-600 transition-colors hover:border-red-300 hover:bg-red-50"
              @click="deleteThreshold(threshold)">
              删除规则
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 传感器状态表格 -->
    <div class="max-h-96 min-w-0 overflow-auto rounded-xl border border-gray-100 bg-white">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="sticky top-0 z-10 bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              传感器编号</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              安装位置</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              监测类型</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              最新数据</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              更新时间</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              状态</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="row in currentLandSensorRows" :key="row.id">
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ row.name }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ row.location }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ row.metrics }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ row.latestData }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ formatRecordedAt(row.updatedAt) }}</td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                :class="getDeviceStatusClass(row.status)">
                {{ deviceStatusLabels[row.status] || row.status }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>

<!-- 编辑环境监测阈值模态框 -->
<div v-if="editThresholdModalVisible && selectedThreshold"
  class="fixed inset-0 z-50 flex items-center justify-center bg-white/30 backdrop-blur-sm">
  <div class="mx-4 w-full max-w-lg overflow-hidden rounded-xl bg-white shadow-xl">
    <div class="flex items-center justify-between border-b border-gray-200 p-6">
      <div>
        <h3 class="text-lg font-bold text-gray-800">编辑环境监测阈值</h3>
        <p class="mt-1 text-sm text-gray-500">调整当前地块的指标适宜范围</p>
      </div>
      <button type="button" class="text-gray-400 transition-colors hover:text-gray-600"
        @click="quitEditThreshold">
        <i class="fa fa-times"></i>
      </button>
    </div>

    <form class="p-6" @submit.prevent="submitEditThreshold">
      <div class="mb-4 rounded-lg bg-gray-50 p-4">
        <div class="flex items-center justify-between">
          <span class="text-sm text-gray-500">监测指标</span>
          <span class="font-medium text-gray-800">
            {{ sensorMetricLabels[thresholdForm.metric] || thresholdForm.metric }}
          </span>
        </div>
      </div>

      <div class="mb-4 grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">最小值</label>
          <input v-model.number="thresholdForm.min" type="number"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary/50">
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">最大值</label>
          <input v-model.number="thresholdForm.max" type="number"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary/50">
        </div>
      </div>

      <label class="mb-6 flex cursor-pointer items-center justify-between rounded-lg border border-gray-200 p-4">
        <div>
          <span class="block text-sm font-medium text-gray-700">启用规则</span>
          <span class="mt-1 block text-xs text-gray-400">停用后，该指标不再参与实时状态判断</span>
        </div>
        <input v-model="thresholdForm.enabled" type="checkbox"
          class="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary/50">
      </label>

      <div class="flex justify-end gap-3">
        <button type="button"
          class="rounded-lg border border-gray-300 px-5 py-2 text-sm text-gray-700 transition-colors hover:bg-gray-50"
          @click="quitEditThreshold">
          取消
        </button>
        <button type="submit"
          class="rounded-lg bg-green-500 px-5 py-2 text-sm text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0">
          保存修改
        </button>
      </div>
    </form>
  </div>
</div>
<!-- 添加阈值规则弹窗 -->
<div v-if="addThresholdModalVisible && props.landId"
  class="fixed inset-0 z-50 flex items-center justify-center bg-white/30 backdrop-blur-sm">
  <div class="mx-4 w-full max-w-lg overflow-hidden rounded-xl bg-white shadow-xl">
    <div class="flex items-center justify-between border-b border-gray-200 p-6">
      <div>
        <h3 class="text-lg font-bold text-gray-800">新增环境监测阈值</h3>
        <p class="mt-1 text-sm text-gray-500">为当前地块配置新的指标适宜范围</p>
      </div>
      <button type="button" class="text-gray-400 transition-colors hover:text-gray-600"
        @click="quitAddThreshold">
        <i class="fa fa-times"></i>
      </button>
    </div>

    <form class="p-6" @submit.prevent="submitAddThreshold">
      <div class="mb-4">
        <label class="mb-1 block text-sm font-medium text-gray-700">指标类型</label>
        <select v-model="thresholdForm.metric"
          class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary/50">
          <option value="" disabled>请选择指标类型</option>
          <option v-for="metric in availableMetrics" :key="metric" :value="metric">
            {{ sensorMetricLabels[metric] || metric }}
          </option>
        </select>
      </div>

      <div class="mb-4 grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">最小值</label>
          <input v-model.number="thresholdForm.min" type="number"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary/50">
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">最大值</label>
          <input v-model.number="thresholdForm.max" type="number"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary/50">
        </div>
      </div>

      <label class="mb-6 flex cursor-pointer items-center justify-between rounded-lg border border-gray-200 p-4">
        <div>
          <span class="block text-sm font-medium text-gray-700">立即启用</span>
          <span class="mt-1 block text-xs text-gray-400">启用后，该指标会参与实时状态判断</span>
        </div>
        <input v-model="thresholdForm.enabled" type="checkbox"
          class="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary/50">
      </label>

      <div class="flex justify-end gap-3">
        <button type="button"
          class="rounded-lg border border-gray-300 px-5 py-2 text-sm text-gray-700 transition-colors hover:bg-gray-50"
          @click="quitAddThreshold">
          取消
        </button>
        <button type="submit"
          class="rounded-lg bg-green-500 px-5 py-2 text-sm text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0">
          保存规则
        </button>
      </div>
    </form>
  </div>
</div>

</template>

<script setup>
import { ref } from 'vue';
import { useFarmStore } from '../../composables/useFarmStore';
import SensorTrendChart from './SensorTrendChart.vue';
import { formatSensorMetricValue } from '../../utils/sensorMetric';

// 双向筛选状态
const sensorTrendRange = defineModel('sensorTrendRange', {
  type: String,
  required: true
});

const selectedTrendMetric = defineModel('selectedTrendMetric', {
  type: String,
  required: true
});

const trendPage = defineModel('trendPage', {
  type: Number,
  required: true
});

const trendPageSize = defineModel('trendPageSize', {
  type: Number,
  required: true
});

// 只读属性
const props = defineProps({
  landId: {
    type: String,
    required: true
  },
  realtimeIndicators: {
    type: Array,
    required: true
  },
  sensorStatusLabels: {
    type: Object,
    required: true
  },
  trendMetricOptions: {
    type: Array,
    required: true
  },
  filteredTrendReadings: {
    type: Array,
    required: true
  },
  visibleTrendMetricSummaries: {
    type: Array,
    required: true
  },
  trendChartSeries: {
    type: Array,
    required: true
  },
  trendTotalCount: {
    type: Number,
    required: true
  },
  trendTotalPages: {
    type: Number,
    required: true
  },
  availableMetrics: {
    type: Array,
    required: true
  },
  currentLandThresholds: {
    type: Array,
    required: true
  },
  sensorMetricLabels: {
    type: Object,
    required: true
  },
  currentLandSensorRows: {
    type: Array,
    required: true
  },
  deviceStatusLabels: {
    type: Object,
    required: true
  },
  getSensorStatusClass: {
    type: Function,
    required: true
  },
  getDeviceStatusClass: {
    type: Function,
    required: true
  },
  formatRecordedAt: {
    type: Function,
    required: true
  },
  formatTrendPointTime: {
    type: Function,
    required: true
  }
});

const emit = defineEmits([
  'export-data'
]);

const {
  addThreshold: createThreshold,
  editThreshold: updateThreshold,
  removeThreshold
} = useFarmStore();
const editThresholdModalVisible = ref(false);
const addThresholdModalVisible = ref(false);
const selectedThreshold = ref(null);

const createThresholdForm = () => ({
  landId: '',
  metric: '',
  min: 0,
  max: 100,
  enabled: false,
  creator: '',
  updatedAt: ''
});

const thresholdForm = ref(createThresholdForm());

const editThreshold = (threshold) => {
  selectedThreshold.value = threshold;
  thresholdForm.value = { ...threshold };
  editThresholdModalVisible.value = true;
};

const deleteThreshold = async (threshold) => {
  const metricLabel = props.sensorMetricLabels[threshold.metric] || threshold.metric;
  if (!window.confirm(`确定要删除“${metricLabel}”阈值规则吗？`)) return;
  try {
    await removeThreshold(threshold.landId, threshold.metric);
  } catch (error) {
    window.alert(error.message || '删除阈值失败');
  }
};

const quitEditThreshold = () => {
  selectedThreshold.value = null;
  thresholdForm.value = createThresholdForm();
  editThresholdModalVisible.value = false;
};

const submitEditThreshold = async () => {
  const form = thresholdForm.value;
  if (!selectedThreshold.value) return alert('未选中阈值条目!');
  if (!Number.isFinite(form.min) || !Number.isFinite(form.max)) return alert('数字输入无效!');
  if (form.min >= form.max) return alert('无效的阈值范围!');
  try {
    await updateThreshold(selectedThreshold.value.landId, selectedThreshold.value.metric, {
      min: Number(form.min),
      max: Number(form.max),
      enabled: Boolean(form.enabled)
    });
    quitEditThreshold();
  } catch (error) {
    alert(error.message || '更新阈值失败');
  }
};

const addThreshold = () => {
  if (props.availableMetrics.length === 0) return alert('当前地块的监测指标已全部配置！');
  thresholdForm.value = { ...createThresholdForm(), landId: props.landId };
  addThresholdModalVisible.value = true;
};

const quitAddThreshold = () => {
  addThresholdModalVisible.value = false;
};

const submitAddThreshold = async () => {
  const form = thresholdForm.value;
  if (!form.metric) return alert('必须选择指标类型!');
  if (!Number.isFinite(form.min) || !Number.isFinite(form.max)) return alert('请填写有效数字!');
  if (form.min >= form.max) return alert('无效的阈值范围');
  try {
    await createThreshold(form.landId, {
      metric: form.metric,
      min: Number(form.min),
      max: Number(form.max),
      enabled: Boolean(form.enabled)
    });
    quitAddThreshold();
  } catch (error) {
    alert(error.message || '创建阈值失败');
  }
};
</script>

<style scoped>
.page-button {
  border: 1px solid rgb(229 231 235);
  border-radius: 0.5rem;
  background: white;
  padding: 0.375rem 0.625rem;
  font-size: 0.875rem;
  color: rgb(55 65 81);
}

.page-button:hover:not(:disabled) {
  border-color: rgb(34 197 94);
  color: rgb(21 128 61);
}

.page-button:disabled {
  cursor: not-allowed;
  opacity: 0.4;
}
</style>
