<template>
  <section class="bg-white rounded-xl card-shadow p-6">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold">预警处理记录</h2>
      <div class="relative">
        <input v-model="recordKeyword" type="text" placeholder="搜索预警编号/标题/处理措施"
          class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 w-64">
        <i class="fa fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
      </div>
    </div>

    <!-- 处理记录表格 -->
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              记录ID</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              预警标题</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              预警类型</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              严重程度</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              处理状态</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              处理措施</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              处理时间</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              处理结果</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              操作人</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              备注</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-if="filteredHandleRecords.length === 0">
            <td colspan="10" class="px-6 py-4 text-center text-gray-500">暂无处理记录</td>
          </tr>

          <template v-else>
            <tr v-for="record in filteredHandleRecords" :key="record.id">
              <td class="px-6 py-4 whitespace-nowrap">{{ record.id }}</td>
              <td class="px-6 py-4 min-w-48">{{ record.title }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 rounded-full text-xs font-medium">
                  {{ alertTypeLabels[record.type] || '未知类型' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 rounded-full text-xs font-medium" :class="alertSeverityClasses[record.severity]">
                  {{ alertSeverityLabels[record.severity] || '未知级别' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 rounded-full text-xs font-medium" :class="alertStatusClasses[record.status]">
                  {{ alertStatusLabels[record.status] || '未知状态' }}
                </span>
              </td>
              <td class="px-6 py-4">{{ record.measure }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ formatHandledAt(record.handledAt) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ record.result }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ record.operator }}</td>
              <td class="px-6 py-4 min-w-48">{{ record.remark || '无' }}</td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useFarmStore } from '../../composables/useFarmStore';

const props = defineProps({ landId: { type: String, required: true } });
const { alerts } = useFarmStore();
const recordKeyword = ref('');
const alertTypeLabels = { environment: '环境异常', device: '设备异常', pest: '病虫害异常' };
const alertSeverityLabels = { high: '高风险', medium: '中风险', low: '低风险' };
const alertSeverityClasses = {
  high: 'bg-red-50 text-red-600',
  medium: 'bg-amber-50 text-amber-600',
  low: 'bg-blue-50 text-blue-600'
};
const alertStatusLabels = { resolved: '已解决', ignored: '已忽略' };
const alertStatusClasses = {
  resolved: 'bg-green-50 text-green-600',
  ignored: 'bg-gray-100 text-gray-600'
};

const handleRecords = computed(() => alerts.value
  .filter(alert => alert.landId === props.landId
    && (alert.status === 'resolved' || alert.status === 'ignored')
    && alert.handleRecord)
  .map(alert => ({
    id: alert.id,
    title: alert.title,
    type: alert.type,
    severity: alert.severity,
    status: alert.status,
    measure: alert.handleRecord.measure,
    handledAt: alert.handleRecord.handledAt,
    result: alert.handleRecord.result,
    operator: alert.handleRecord.operator,
    remark: alert.handleRecord.remark
  })));

const filteredHandleRecords = computed(() => {
  const keyword = recordKeyword.value.trim();
  if (!keyword) return handleRecords.value;
  return handleRecords.value.filter(record => String(record.id).includes(keyword)
    || record.title.includes(keyword)
    || record.measure.includes(keyword));
});

const formatHandledAt = value => {
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
</script>
