<template>
  <!-- 异常预警 -->
  <section class="bg-white rounded-xl card-shadow p-6">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold">异常预警</h2>
      <div class="flex flex-wrap items-center gap-2">
        <button type="button"
          class="inline-flex items-center gap-2 rounded-lg border border-emerald-600 bg-emerald-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all hover:border-emerald-700 hover:bg-emerald-700 hover:shadow focus:outline-none focus:ring-2 focus:ring-emerald-500/40"
          @click="openCreateAlertModal">
          <i class="fa fa-plus"></i>
          添加预警
        </button>
        <div class="relative">
          <select v-model="alertTypeFilter"
            class="appearance-none bg-gray-50 border border-gray-300 text-gray-700 py-2 pl-3 pr-8 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="all">全部类型</option>
            <option value="environment">环境异常</option>
            <option value="device">设备异常</option>
            <option value="pest">病虫害异常</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
            <i class="fa fa-chevron-down text-xs"></i>
          </div>
        </div>
        <div class="relative">
          <select v-model="alertSeverityFilter"
            class="appearance-none bg-gray-50 border border-gray-300 text-gray-700 py-2 pl-3 pr-8 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="all">全部级别</option>
            <option value="high">高</option>
            <option value="medium">中</option>
            <option value="low">低</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
            <i class="fa fa-chevron-down text-xs"></i>
          </div>
        </div>
        <div class="relative">
          <select v-model="alertStatusFilter"
            class="appearance-none bg-gray-50 border border-gray-300 text-gray-700 py-2 pl-3 pr-8 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="all">全部状态</option>
            <option value="pending">待处理</option>
            <option value="processing">处理中</option>
            <option value="resolved">已解决</option>
            <option value="ignored">已忽略</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
            <i class="fa fa-chevron-down text-xs"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- 预警统计 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="bg-orange-50 rounded-xl p-4 border border-orange-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">待处理</p>
            <h3 class="text-2xl font-bold mt-1 text-orange-600">{{ pendingAlertCount }} 条</h3>
          </div>
          <div class="w-12 h-12 rounded-full bg-orange-100 flex items-center justify-center text-orange-500">
            <i class="fa fa-exclamation-circle text-xl"></i>
          </div>
        </div>
      </div>
      <div class="bg-blue-50 rounded-xl p-4 border border-blue-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">处理中</p>
            <h3 class="text-2xl font-bold mt-1 text-blue-600">{{ processingAlertCount }} 条
            </h3>
          </div>
          <div class="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center text-blue-500">
            <i class="fa fa-spinner text-xl"></i>
          </div>
        </div>
      </div>
      <div class="bg-green-50 rounded-xl p-4 border border-green-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">已结束</p>
            <h3 class="text-2xl font-bold mt-1 text-green-600">{{ endedAlertCount }} 条</h3>
          </div>
          <div class="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center text-green-500">
            <i class="fa fa-check-circle text-xl"></i>
          </div>
        </div>
      </div>
    </div>

    <div class="space-y-4">
      <div v-if="filteredAlerts.length === 0" class="py-10 text-center text-sm text-gray-500">
        当前筛选条件下暂无预警
      </div>

      <div v-for="alert in filteredAlerts" :key="alert.id" class="border border-gray-200 rounded-lg p-4">
        <div class="flex justify-between items-start gap-4 mb-3">
          <div>
            <div class="flex flex-wrap items-center gap-2 mb-2">
              <span class="px-2 py-1 rounded-full text-xs font-medium" :class="alertTypeClasses[alert.type]">
                {{ alertTypeLabels[alert.type] || '未知类型' }}
              </span>
              <span class="px-2 py-1 rounded-full text-xs font-medium"
                :class="alertSeverityClasses[alert.severity]">
                {{ alertSeverityLabels[alert.severity] || '未知级别' }}
              </span>
            </div>

            <h3 class="font-medium text-gray-900">{{ alert.title }}</h3>
            <p class="text-sm text-gray-500 mt-1">
              {{ formatAlertTime(alert.occurredAt) }} · {{ getAlertSourceText(alert) }}
            </p>
          </div>

          <span class="shrink-0 px-2 py-1 rounded-full text-xs font-medium"
            :class="alertStatusClasses[alert.status]">
            {{ alertStatusLabels[alert.status] || '未知状态' }}
          </span>
        </div>

        <p class="text-sm text-gray-600 leading-6">
          {{ alert.description }}
        </p>
        <p class="text-sm text-gray-500 mt-2 leading-6">
          建议：{{ alert.suggestion }}
        </p>

        <div v-if="alert.handleRecord" class="mt-3 rounded-lg bg-gray-50 p-3 text-sm text-gray-600">
          <p><span class="font-medium">处理结果：</span>{{ alert.handleRecord.result }}</p>
          <p class="mt-1"><span class="font-medium">备注：</span>{{ alert.handleRecord.remark || '无' }}</p>
        </div>

        <div v-if="hasGeneratedTask(alert.id) || alert.status === 'pending' || alert.status === 'processing'"
          class="mt-4 flex flex-wrap items-center gap-2">
          <span v-if="hasGeneratedTask(alert.id)"
            class="inline-flex items-center gap-1.5 rounded-lg bg-emerald-50 px-3 py-1.5 text-sm font-medium text-emerald-700">
            <i class="fa fa-check-circle"></i>
            已生成任务
          </span>
          <button v-if="alert.status === 'pending'" type="button"
            class="rounded-lg bg-blue-600 px-3 py-1.5 text-sm text-white transition-colors hover:bg-blue-700"
            @click="startProcessing(alert)">开始处理</button>
          <button v-if="alert.status === 'pending'" type="button"
            class="rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm text-gray-700 transition-colors hover:bg-gray-50"
            @click="openHandleModal(alert, 'ignore')">忽略</button>
          <button v-if="alert.status === 'processing'" type="button"
            class="rounded-lg px-3 py-1.5 text-sm text-white transition-colors"
            :class="canResolveAlert(alert.id) ? 'bg-green-600 hover:bg-green-700' : 'cursor-not-allowed bg-gray-400'"
            :disabled="!canResolveAlert(alert.id)"
            :title="canResolveAlert(alert.id) ? '' : '请先完成关联农事任务'"
            @click="openHandleModal(alert, 'resolve')">
            {{ canResolveAlert(alert.id) ? '完成处理' : '等待任务完成' }}
          </button>
        </div>
      </div>
    </div>
  </section>

<!-- 人工添加预警模态框 -->
<div v-if="createAlertModalVisible"
  class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
  <div class="bg-white rounded-xl w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
    <div class="p-6 border-b border-gray-200">
      <div class="flex justify-between items-center">
        <div>
          <h3 class="text-lg font-bold">人工添加预警</h3>
          <p class="text-sm text-gray-500 mt-1">新预警将记录到当前地块，并从待处理状态开始。</p>
        </div>
        <button type="button" class="text-gray-400 hover:text-gray-500" @click="closeCreateAlertModal">
          <i class="fa fa-times"></i>
        </button>
      </div>
    </div>

    <form class="p-6" @submit.prevent="submitCreateAlert">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">预警类型</label>
          <select v-model="createAlertForm.type"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="environment">环境异常</option>
            <option value="device">设备异常</option>
            <option value="pest">病虫害异常</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">严重程度</label>
          <select v-model="createAlertForm.severity"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="high">高风险</option>
            <option value="medium">中风险</option>
            <option value="low">低风险</option>
          </select>
        </div>
      </div>

      <div class="mt-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">预警标题</label>
        <input v-model="createAlertForm.title" type="text"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
          placeholder="请输入简明的预警标题">
      </div>

      <div class="mt-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">详细描述</label>
        <textarea v-model="createAlertForm.description" rows="3"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
          placeholder="请描述异常现象和影响"></textarea>
      </div>

      <div class="mt-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">处理建议</label>
        <textarea v-model="createAlertForm.suggestion" rows="2"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
          placeholder="请填写建议采取的处理措施"></textarea>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">发生时间</label>
          <input v-model="createAlertForm.occurredAt" type="datetime-local"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">来源设备（可选）</label>
          <select v-model="createAlertForm.deviceId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="">人工上报 / 不关联设备</option>
            <option v-for="device in currentLandDevices" :key="device.id" :value="device.id">
              {{ device.name }}
            </option>
          </select>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">指标类型（可选）</label>
          <input v-model="createAlertForm.metric" type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="如 soil_moisture">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">指标值（可选）</label>
          <input v-model="createAlertForm.value" type="number" step="any"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="如 45.6">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">单位（可选）</label>
          <input v-model="createAlertForm.unit" type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="如 %">
        </div>
      </div>

      <div class="flex gap-3 mt-6">
        <button type="button"
          class="flex-1 rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-sm font-medium text-gray-700 shadow-sm transition-colors hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-300/60"
          @click="closeCreateAlertModal">取消</button>
        <button type="submit"
          class="flex-1 rounded-lg border border-emerald-600 bg-emerald-600 px-4 py-2.5 text-sm font-medium text-white shadow-sm transition-all hover:border-emerald-700 hover:bg-emerald-700 hover:shadow focus:outline-none focus:ring-2 focus:ring-emerald-500/40">
          添加预警
        </button>
      </div>
    </form>
  </div>
</div>

<!-- 处理预警模态框 -->
<div v-if="handleModalVisible && selectedAlert"
  class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
  <div class="bg-white rounded-xl w-full max-w-md mx-4 overflow-hidden">
    <div class="p-6 border-b border-gray-200">
      <div class="flex justify-between items-center">
        <h3 class="text-lg font-bold">
          {{ handleAction === 'resolve' ? '完成处理' : '忽略预警' }} - {{ selectedAlert.title }}
        </h3>
        <button class="text-gray-400 hover:text-gray-500" @click="closeHandleModal">
          <i class="fa fa-times"></i>
        </button>
      </div>
      <p class="text-sm text-gray-500 mt-2">
        {{ handleAction === 'resolve' ? '请记录处理措施和最终结果' : '请说明忽略该预警的原因' }}
      </p>
    </div>
    <div class="p-6">
      <form @submit.prevent="submitHandleAlert">
        <div v-if="handleAction === 'resolve'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">处理措施</label>
          <textarea v-model="handleForm.measure" rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="请详细描述处理措施..."></textarea>
        </div>

        <div v-if="handleAction === 'resolve'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">处理结果</label>
          <input type="text" v-model="handleForm.result"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="请填写处理结果">
        </div>

        <div v-if="handleAction === 'resolve'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">处理时间</label>
          <input type="datetime-local" v-model="handleForm.handledAt"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
        </div>

        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-1">
            {{ handleAction === 'ignore' ? '忽略原因' : '备注' }}
          </label>
          <textarea v-model="handleForm.remark" rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            :placeholder="handleAction === 'ignore' ? '请填写忽略原因' : '记录专业处理信息或其他说明'"></textarea>
        </div>
        <div class="flex space-x-3">
          <button type="button" class="flex-1 btn-outline" @click="closeHandleModal">
            取消
          </button>
          <button type="submit" class="flex-1 btn-primary">
            {{ handleAction === 'resolve' ? '确认完成' : '确认忽略' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- 开始处理模态框 -->
<div v-if="startModalVisible && selectedStartAlert"
  class="fixed inset-0 z-50 flex items-center justify-center bg-white/30 p-4 backdrop-blur-sm">
  <div class="w-full max-w-lg overflow-hidden rounded-xl bg-white shadow-xl">
    <div class="flex items-start justify-between border-b border-gray-200 p-6">
      <div>
        <h3 class="text-lg font-bold text-gray-800">开始处理预警</h3>
        <p class="mt-1 text-sm text-gray-500">{{ selectedStartAlert.title }}</p>
      </div>
      <button type="button" class="text-gray-400 transition-colors hover:text-gray-600" @click="closeStartModal">
        <i class="fa fa-times"></i>
      </button>
    </div>

    <div class="space-y-5 p-6">
      <label class="flex items-start gap-3 rounded-lg border border-gray-200 p-4"
        :class="existingTaskForStart ? 'bg-gray-50' : 'cursor-pointer hover:border-emerald-300'">
        <input v-model="generateTaskChecked" type="checkbox" :disabled="existingTaskForStart"
          class="mt-0.5 h-4 w-4 rounded border-gray-300 text-emerald-600 focus:ring-emerald-500">
        <span>
          <span class="block text-sm font-medium text-gray-800">同时生成农事任务</span>
          <span class="mt-1 block text-xs text-gray-500">
            {{ existingTaskForStart ? '该预警已经生成过农事任务，不会重复创建。' : '确认后将创建一条与当前预警关联的待处理任务。' }}
          </span>
        </span>
      </label>

      <div v-if="generateTaskChecked && !existingTaskForStart" class="grid gap-4 sm:grid-cols-2">
        <label class="block">
          <span class="mb-1.5 block text-sm font-medium text-gray-700">任务类型</span>
          <select v-model="startTaskForm.taskType"
            class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
            <option value="irrigation">灌溉</option>
            <option value="fertilization">施肥</option>
            <option value="pesticide">施药</option>
            <option value="weeding">除草</option>
            <option value="inspection">巡检</option>
            <option value="harvest">收获</option>
            <option value="other">其他</option>
          </select>
        </label>

        <label class="block">
          <span class="mb-1.5 block text-sm font-medium text-gray-700">优先级</span>
          <select v-model="startTaskForm.priority"
            class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
            <option value="high">高</option>
            <option value="medium">中</option>
            <option value="low">低</option>
          </select>
        </label>

        <label class="block">
          <span class="mb-1.5 block text-sm font-medium text-gray-700">负责人</span>
          <input v-model.trim="startTaskForm.assignee" type="text" placeholder="请输入负责人"
            class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
        </label>

        <label class="block">
          <span class="mb-1.5 block text-sm font-medium text-gray-700">截止时间</span>
          <input v-model="startTaskForm.deadline" type="datetime-local"
            class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
        </label>
      </div>
    </div>

    <div class="flex justify-end gap-3 border-t border-gray-200 px-6 py-4">
      <button type="button"
        class="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-gray-50"
        @click="closeStartModal">取消</button>
      <button type="button"
        class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-blue-700"
        @click="confirmStartProcessing">确认开始</button>
    </div>
  </div>
</div>


</template>

<script setup>
import { computed, ref } from 'vue';
import { useFarmStore } from '../../composables/useFarmStore';

const props = defineProps({ landId: { type: String, required: true } });
const { alerts, devices, farmTasks } = useFarmStore();

const alertTypeLabels = {
  environment: '环境异常',
  device: '设备异常',
  pest: '病虫害异常'
};

const alertTypeClasses = {
  environment: 'bg-emerald-50 text-emerald-700',
  device: 'bg-sky-50 text-sky-700',
  pest: 'bg-violet-50 text-violet-700'
};

const alertSeverityLabels = {
  high: '高风险',
  medium: '中风险',
  low: '低风险'
};

const alertSeverityClasses = {
  high: 'bg-red-50 text-red-600',
  medium: 'bg-amber-50 text-amber-600',
  low: 'bg-blue-50 text-blue-600'
};

const alertStatusLabels = {
  pending: '待处理',
  processing: '处理中',
  resolved: '已解决',
  ignored: '已忽略'
};

const alertStatusClasses = {
  pending: 'bg-orange-50 text-orange-600',
  processing: 'bg-blue-50 text-blue-600',
  resolved: 'bg-green-50 text-green-600',
  ignored: 'bg-gray-100 text-gray-600'
};

const sensorMetricLabels = {
  soil_moisture: '土壤湿度',
  air_temperature: '空气温度',
  air_humidity: '空气湿度',
  light: '光照强度',
  soil_ph: '土壤 pH',
  battery: '设备电量'
};

const alertTypeFilter = ref('all');
const alertSeverityFilter = ref('all');
const alertStatusFilter = ref('all');
const createAlertModalVisible = ref(false);
const createAlertFormState = () => ({
  type: 'environment',
  severity: 'medium',
  title: '',
  description: '',
  suggestion: '',
  occurredAt: '',
  deviceId: '',
  metric: '',
  value: '',
  unit: ''
});
const createAlertForm = ref(createAlertFormState());
const selectedAlert = ref(null);
const handleAction = ref('resolve');
const handleModalVisible = ref(false);
const createHandleForm = () => ({
  measure: '',
  handledAt: '',
  result: '',
  remark: ''
});
const handleForm = ref(createHandleForm());
const startModalVisible = ref(false);
const selectedStartAlert = ref(null);
const generateTaskChecked = ref(false);
const createStartTaskForm = () => ({
  taskType: 'inspection',
  priority: 'medium',
  assignee: '',
  deadline: ''
});
const startTaskForm = ref(createStartTaskForm());

const currentLandAlerts = computed(() => alerts.value.filter(alert => alert.landId === props.landId));
const currentLandDevices = computed(() => devices.value.filter(device => device.landId === props.landId));
const pendingAlertCount = computed(() => currentLandAlerts.value.filter(alert => alert.status === 'pending').length);
const processingAlertCount = computed(() => currentLandAlerts.value.filter(alert => alert.status === 'processing').length);
const endedAlertCount = computed(() => currentLandAlerts.value.filter(alert => alert.status === 'resolved' || alert.status === 'ignored').length);
const filteredAlerts = computed(() => {
  return currentLandAlerts.value.filter(alert => {
    const matchesType = alertTypeFilter.value === 'all' || alert.type === alertTypeFilter.value;
    const matchesSeverity = alertSeverityFilter.value === 'all' || alert.severity === alertSeverityFilter.value;
    const matchesStatus = alertStatusFilter.value === 'all' || alert.status === alertStatusFilter.value;

    return matchesType && matchesSeverity && matchesStatus;
  });
});

const formatAlertTime = value => {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '时间未知';

  return new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(date);
};

const hasGeneratedTask = alertId => {
  return farmTasks.value.some(task =>
    task.sourceType === 'alert' && task.sourceId === alertId
  );
};

const getAlertTasks = alertId => {
  return farmTasks.value.filter(task =>
    task.sourceType === 'alert' && task.sourceId === alertId
  );
};

const canResolveAlert = alertId => {
  return !getAlertTasks(alertId).some(task =>
    task.status === 'pending' ||
    task.status === 'processing'
  );
};

const existingTaskForStart = computed(() =>
  selectedStartAlert.value ? hasGeneratedTask(selectedStartAlert.value.id) : false
);

const getAlertSourceText = alert => {
  const source = alert.source;
  if (!source) return '来源未知';

  const deviceName = devices.value.find(device => device.id === source.deviceId)?.name;
  const metricName = sensorMetricLabels[source.metric];
  const reading = source.value !== null && source.value !== undefined
    ? `${source.value}${source.unit || ''}`
    : '';

  return [deviceName, metricName, reading].filter(Boolean).join(' · ') || '人工上报';
};

const getAlertIndex = alertId => alerts.value.findIndex(alert => alert.id === alertId);

const getDefaultTaskType = alert => {
  if (alert.type === 'pest') return 'pesticide';
  if (alert.type === 'device') return 'inspection';
  if (alert.type === 'environment' && alert.source?.metric === 'soil_moisture') return 'irrigation';
  return 'inspection';
};

const startProcessing = alert => {
  const index = getAlertIndex(alert.id);
  if (index === -1) return window.alert('预警不存在');
  if (alerts.value[index].status !== 'pending') return window.alert('只有待处理预警可以开始处理');

  selectedStartAlert.value = alerts.value[index];
  generateTaskChecked.value = hasGeneratedTask(alert.id);
  startTaskForm.value = {
    taskType: getDefaultTaskType(alert),
    priority: alert.severity,
    assignee: localStorage.getItem('username') || '',
    deadline: ''
  };
  startModalVisible.value = true;
};

const closeStartModal = () => {
  startModalVisible.value = false;
  selectedStartAlert.value = null;
  generateTaskChecked.value = false;
  startTaskForm.value = createStartTaskForm();
};

const getNextTaskId = () => {
  const largestId = farmTasks.value.reduce((largest, task) => {
    const numericId = Number.parseInt(String(task.id).replace(/\D/g, ''), 10);
    return Number.isFinite(numericId) ? Math.max(largest, numericId) : largest;
  }, 0);

  return `TASK-${String(largestId + 1).padStart(3, '0')}`;
};

const confirmStartProcessing = () => {
  if (!selectedStartAlert.value) return;

  const index = getAlertIndex(selectedStartAlert.value.id);
  if (index === -1) return window.alert('预警不存在');
  if (alerts.value[index].status !== 'pending') {
    closeStartModal();
    return window.alert('预警状态已发生变化，请刷新后重试');
  }

  const shouldCreateTask = generateTaskChecked.value && !hasGeneratedTask(selectedStartAlert.value.id);
  if (shouldCreateTask) {
    if (!startTaskForm.value.assignee.trim() || !startTaskForm.value.deadline) {
      return window.alert('请填写任务负责人和截止时间');
    }

    const deadline = new Date(startTaskForm.value.deadline);
    if (Number.isNaN(deadline.getTime())) {
      return window.alert('截止时间格式不正确');
    }

    const alert = alerts.value[index];
    farmTasks.value.unshift({
      id: getNextTaskId(),
      landId: alert.landId,
      sourceType: 'alert',
      sourceId: alert.id,
      taskType: startTaskForm.value.taskType,
      title: `处理：${alert.title}`,
      description: alert.suggestion,
      priority: startTaskForm.value.priority,
      status: 'pending',
      assignee: startTaskForm.value.assignee.trim(),
      deadline: deadline.toISOString(),
      createdAt: new Date().toISOString(),
      completedAt: null,
      result: '',
      remark: `由预警 ${alert.id} 生成`
    });
  }

  alerts.value[index] = {
    ...alerts.value[index],
    status: 'processing'
  };

  closeStartModal();
};

const getLocalDateTimeInput = () => {
  const now = new Date();
  const localTime = new Date(now.getTime() - now.getTimezoneOffset() * 60 * 1000);
  return localTime.toISOString().slice(0, 16);
};

const getNextAlertId = () => {
  const largestId = alerts.value.reduce((largest, alert) => {
    const numericId = Number.parseInt(String(alert.id).replace(/\D/g, ''), 10);
    return Number.isFinite(numericId) ? Math.max(largest, numericId) : largest;
  }, 0);

  return `ALT-${String(largestId + 1).padStart(3, '0')}`;
};

const openCreateAlertModal = () => {
  createAlertForm.value = {
    ...createAlertFormState(),
    occurredAt: getLocalDateTimeInput()
  };
  createAlertModalVisible.value = true;
};

const closeCreateAlertModal = () => {
  createAlertForm.value = createAlertFormState();
  createAlertModalVisible.value = false;
};

const submitCreateAlert = () => {
  const form = createAlertForm.value;
  if (!form.title.trim() || !form.description.trim() || !form.suggestion.trim() || !form.occurredAt) {
    return window.alert('请填写预警标题、详细描述、处理建议和发生时间');
  }

  const hasValue = form.value !== '';
  const numericValue = hasValue ? Number(form.value) : null;
  if (hasValue && !Number.isFinite(numericValue)) {
    return window.alert('请填写有效的指标值');
  }

  const deviceBelongsToLand = !form.deviceId || currentLandDevices.value.some(device => device.id === form.deviceId);
  if (!deviceBelongsToLand) {
    return window.alert('所选设备不属于当前地块');
  }

  alerts.value.unshift({
    id: getNextAlertId(),
    landId: props.landId,
    type: form.type,
    severity: form.severity,
    title: form.title.trim(),
    description: form.description.trim(),
    suggestion: form.suggestion.trim(),
    status: 'pending',
    occurredAt: new Date(form.occurredAt).toISOString(),
    source: {
      deviceId: form.deviceId || null,
      metric: form.metric.trim() || null,
      value: numericValue,
      unit: form.unit.trim() || null
    },
    handleRecord: null
  });

  closeCreateAlertModal();
};

const openHandleModal = (alert, action) => {
  selectedAlert.value = alert;
  handleAction.value = action;
  handleForm.value = {
    ...createHandleForm(),
    handledAt: getLocalDateTimeInput()
  };
  handleModalVisible.value = true;
};

const closeHandleModal = () => {
  handleForm.value = createHandleForm();
  selectedAlert.value = null;
  handleModalVisible.value = false;
};

const submitHandleAlert = () => {
  if (!selectedAlert.value) return;

  const index = getAlertIndex(selectedAlert.value.id);
  if (index === -1) return window.alert('预警不存在');

  if (handleAction.value === 'resolve' && !canResolveAlert(selectedAlert.value.id)) {
    return window.alert('请先完成该预警关联的农事任务');
  }

  if (handleAction.value === 'resolve') {
    if (!handleForm.value.measure.trim() || !handleForm.value.result.trim() || !handleForm.value.handledAt) {
      return window.alert('请填写处理措施、处理结果和处理时间');
    }
  } else if (!handleForm.value.remark.trim()) {
    return window.alert('请填写忽略原因');
  }

  const resolved = handleAction.value === 'resolve';
  alerts.value[index] = {
    ...alerts.value[index],
    status: resolved ? 'resolved' : 'ignored',
    handleRecord: {
      measure: resolved ? handleForm.value.measure.trim() : '无需处理',
      handledAt: resolved ? new Date(handleForm.value.handledAt).toISOString() : new Date().toISOString(),
      result: resolved ? handleForm.value.result.trim() : '已忽略',
      remark: handleForm.value.remark.trim(),
      operator: localStorage.getItem('username') || '游客'
    }
  };

  closeHandleModal();
};
</script>
