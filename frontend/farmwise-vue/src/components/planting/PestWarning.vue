<template>
  <!-- 病虫害预警 -->
  <section class="bg-white rounded-xl card-shadow p-6">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold">病虫害预警</h2>
      <div class="flex space-x-2">
        <div class="relative">
          <select v-model="warningFilter"
            class="appearance-none bg-gray-50 border border-gray-300 text-gray-700 py-2 pl-3 pr-8 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="all">全部预警</option>
            <option value="red">红色预警</option>
            <option value="yellow">黄色预警</option>
            <option value="handled">已处理</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
            <i class="fa fa-chevron-down text-xs"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- 预警统计 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="bg-red-50 rounded-xl p-4 border border-red-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">红色预警</p>
            <h3 class="text-2xl font-bold mt-1 text-red-600">{{ redWarningCount }} 条</h3>
          </div>
          <div class="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center text-red-500">
            <i class="fa fa-exclamation-circle text-xl"></i>
          </div>
        </div>
      </div>
      <div class="bg-yellow-50 rounded-xl p-4 border border-yellow-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">黄色预警</p>
            <h3 class="text-2xl font-bold mt-1 text-yellow-600">{{ yellowWarningCount }} 条
            </h3>
          </div>
          <div class="w-12 h-12 rounded-full bg-yellow-100 flex items-center justify-center text-yellow-500">
            <i class="fa fa-exclamation-triangle text-xl"></i>
          </div>
        </div>
      </div>
      <div class="bg-green-50 rounded-xl p-4 border border-green-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">已处理预警</p>
            <h3 class="text-2xl font-bold mt-1 text-green-600">{{ handledWarningCount }} 条</h3>
          </div>
          <div class="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center text-green-500">
            <i class="fa fa-check-circle text-xl"></i>
          </div>
        </div>
      </div>
    </div>

    <div class="space-y-4">
      <div v-for="warning in filteredWarnings" :key="warning.id" class="border border-gray-200 rounded-lg p-4">
        <div class="flex justify-between items-start mb-2">
          <div>
            <div class="font-medium">
              {{ warning.message }}
            </div>
            <div class="text-sm text-gray-500">
              作物类型：{{ warning.cropType }}
            </div>
          </div>
          <span class="px-2 py-1 rounded-full text-xs font-medium"
            :class="getWarningLevelClass(warning.handled ? 'green' : warning.level)">
            {{ warning.handled ? '已处理' : getWarningLevelText(warning.level) }}
          </span>
        </div>

        <p class="text-sm text-gray-600">
          {{ warning.suggestion }}
        </p>
        <button type="button" v-if="!warning.handled"
          class="mt-3 inline-flex items-center rounded-lg px-3 py-1.5 text-sm text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:shadow active:translate-y-0 active:shadow-sm"
          :class="getWarningButtonClass(warning.level)" @click="handleWarning(warning)">处理预警</button>
      </div>
    </div>
  </section>

<!-- 处理预警模态框 -->
<div v-if="handleModalVisible && selectedWarning"
  class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
  <div class="bg-white rounded-xl w-full max-w-md mx-4 overflow-hidden">
    <div class="p-6 border-b border-gray-200">
      <div class="flex justify-between items-center">
        <h3 class="text-lg font-bold">处理预警 - {{ selectedWarning.message }}</h3>
        <button class="text-gray-400 hover:text-gray-500" @click="closeHandleModal">
          <i class="fa fa-times"></i>
        </button>
      </div>
      <p class="text-sm text-gray-500 mt-2">请记录针对该病虫害预警的处理措施</p>
    </div>
    <div class="p-6">
      <form @submit.prevent="submitHandleWarning">
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">处理措施</label>
          <textarea v-model="handleForm.measure" rows="4"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="请详细描述处理措施..."></textarea>
        </div>
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">使用药剂</label>
          <input type="text" v-model="handleForm.pesticide"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="如：多菌灵可湿性粉剂">
        </div>
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用药浓度</label>
            <input type="text" v-model="handleForm.concentration"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="如：800倍稀释">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">处理时间</label>
            <input type="datetime-local" v-model="handleForm.handleTime"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
          </div>
        </div>
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-1">效果反馈</label>
          <select v-model="handleForm.effect"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="完全控制">完全控制</option>
            <option value="效果良好">效果良好</option>
            <option value="部分控制">部分控制</option>
            <option value="无明显效果">无明显效果</option>
          </select>
        </div>
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-1">备注</label>
          <input type="text" v-model="handleForm.remark"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="其他需要说明的信息">
        </div>
        <div class="flex space-x-3">
          <button type="button" class="flex-1 btn-outline" @click="closeHandleModal">
            取消
          </button>
          <button type="submit" class="flex-1 btn-primary">
            提交处理记录
          </button>
        </div>
      </form>
    </div>
  </div>
</div>


</template>

<script setup>
import { computed, ref } from 'vue';
import { useFarmStore } from '../../composables/useFarmStore';

const props = defineProps({ landId: { type: String, required: true } });
const { warnings } = useFarmStore();
const warningFilter = ref('all');
const selectedWarning = ref(null);
const handleModalVisible = ref(false);
const createHandleForm = () => ({
  measure: '',
  pesticide: '',
  concentration: '',
  handleTime: '',
  effect: '完全控制',
  remark: ''
});
const handleForm = ref(createHandleForm());

const currentLandWarnings = computed(() => warnings.value.filter(warning => warning.landId === props.landId));
const redWarningCount = computed(() => currentLandWarnings.value.filter(w => w.level === 'red' && !w.handled).length);
const yellowWarningCount = computed(() => currentLandWarnings.value.filter(w => w.level === 'yellow' && !w.handled).length);
const handledWarningCount = computed(() => currentLandWarnings.value.filter(w => w.handled).length);
const filteredWarnings = computed(() => {
  if (warningFilter.value === 'handled') return currentLandWarnings.value.filter(w => w.handled);
  if (warningFilter.value === 'all') return currentLandWarnings.value;
  return currentLandWarnings.value.filter(w => w.level === warningFilter.value && !w.handled);
});

const getWarningLevelText = level => ({ red: '红色预警', yellow: '黄色预警', green: '已处理' }[level] || '未知预警');
const getWarningLevelClass = level => ({ red: 'text-red-600 bg-red-50', yellow: 'text-yellow-600 bg-yellow-50', green: 'text-green-600 bg-green-50' }[level] || 'text-gray-600 bg-gray-50');
const getWarningButtonClass = level => ({ red: 'border border-red-500 bg-red-500 hover:bg-red-600', yellow: 'border border-yellow-500 bg-yellow-500 hover:bg-yellow-600', green: 'border border-green-500 bg-green-500 hover:bg-green-600' }[level] || 'border border-gray-500 bg-gray-500 hover:bg-gray-600');

const handleWarning = warning => {
  selectedWarning.value = warning;
  handleModalVisible.value = true;
};
const closeHandleModal = () => {
  handleForm.value = createHandleForm();
  selectedWarning.value = null;
  handleModalVisible.value = false;
};
const submitHandleWarning = () => {
  if (!selectedWarning.value) return;
  const index = warnings.value.findIndex(w => w.id === selectedWarning.value.id);
  if (index === -1) return;
  warnings.value[index] = { ...warnings.value[index], handled: true, handleRecord: { ...handleForm.value } };
  closeHandleModal();
};
</script>
