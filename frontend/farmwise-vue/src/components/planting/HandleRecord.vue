<template>
  <section class="bg-white rounded-xl card-shadow p-6">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold">预警处理记录</h2>
      <div class="relative">
        <input v-model="recordKeyword" type="text" placeholder="搜索预警编号/作物类型"
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
              作物类型</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              预警类型</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              处理措施</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              处理时间</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              处理效果</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              操作人</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-if="filteredHandleRecords.length === 0">
            <td colspan="7" class="px-6 py-4 text-center text-gray-500">暂无处理记录</td>
          </tr>

          <template v-else>
            <tr v-for="record in filteredHandleRecords" :key="record.id">
              <td class="px-6 py-4 whitespace-nowrap">{{ record.id }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ record.cropType }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 rounded-full text-xs font-medium" :class="getWarningLevelClass(record.level)">
                  {{ getWarningLevelText(record.level) }}
                </span>
              </td>
              <td class="px-6 py-4">{{ record.measure }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ record.handleTime }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ record.effect }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ record.operator }}</td>
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
const { warnings } = useFarmStore();
const recordKeyword = ref('');
const getWarningLevelText = level => ({ red: '红色预警', yellow: '黄色预警', green: '已处理' }[level] || '未知预警');
const getWarningLevelClass = level => ({ red: 'text-red-600 bg-red-50', yellow: 'text-yellow-600 bg-yellow-50', green: 'text-green-600 bg-green-50' }[level] || 'text-gray-600 bg-gray-50');
const handleRecords = computed(() => warnings.value
  .filter(warning => warning.landId === props.landId && warning.handled && warning.handleRecord)
  .map(warning => ({
    id: warning.id,
    cropType: warning.cropType,
    level: warning.level,
    measure: warning.handleRecord.measure,
    handleTime: warning.handleRecord.handleTime,
    effect: warning.handleRecord.effect,
    operator: '农场主'
  })));
const filteredHandleRecords = computed(() => {
  const keyword = recordKeyword.value.trim();
  if (!keyword) return handleRecords.value;
  return handleRecords.value.filter(record => String(record.id).includes(keyword) || record.cropType.includes(keyword));
});
</script>
