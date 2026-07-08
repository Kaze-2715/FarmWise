<template>
  <div>
    <div class="flex flex-col md:flex-row gap-6">
      <aside class="md:w-64 bg-white rounded-xl card-shadow p-4 flex-shrink-0">
        <nav class="space-y-1">
          <button v-for="section in sections" :key="section.key" type="button"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === section.key }" @click="activeSection = section.key">
            <i :class="[section.icon, 'w-5 text-center flex-shrink-0']"></i>
            <span>{{ section.label }}</span>
          </button>
        </nav>
      </aside>

      <main class="flex-1 space-y-8">
        <section v-if="activeSection === 'yield'" class="bg-white rounded-xl card-shadow p-6">
          <h2 class="text-xl font-bold mb-4">产量预测</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">种植计划</label>
              <select v-model="yieldForm.planId"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                <option value="" disabled>请选择种植计划</option>
                <option v-for="plan in plans" :key="plan.id" :value="plan.id">
                  {{ plan.planName }}
                </option>
              </select>
              <button type="button" :disabled="yieldLoading"
                class="mt-4 inline-flex items-center rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm disabled:cursor-not-allowed disabled:opacity-60"
                @click="generateYieldPrediction">
                <i class="fa fa-line-chart mr-2"></i>
                {{ yieldLoading ? '生成中...' : '生成预测' }}
              </button>
              <div v-if="yieldResult" class="mt-6">
                <h3 class="font-medium mb-2">预测结果</h3>
                <div>预估产量：
                  <span class="font-bold">{{ yieldResult.amount }} {{ yieldResult.unit }}</span>
                </div>
                <div>置信度：{{ yieldResult.confidence }}%</div>
                <div>说明：{{ yieldResult.summary }}</div>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">预测模型</label>
              <select v-model="yieldForm.modelId"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                <option value="" disabled>请选择预测模型</option>
                <option v-for="model in availableYieldModels" :key="model.id" :value="model.id">
                  {{ model.name }}
                </option>
              </select>
            </div>
          </div>
        </section>

        <section v-if="activeSection === 'market'" class="bg-white rounded-xl card-shadow p-6">
          <h2 class="text-xl font-bold mb-4">市场趋势</h2>
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div>
              <h3 class="font-medium mb-2">价格走势</h3>
              <div
                class="h-64 flex items-center justify-center text-sm text-gray-400 border border-dashed border-gray-200 rounded-lg">
                价格走势图待接入
              </div>
            </div>

            <div>
              <h3 class="font-medium mb-2">电商对比</h3>
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">平台</th>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">价格（元/斤）</th>
                    <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">销量指数</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="item in marketData" :key="item.platform">
                    <td class="px-3 py-2 text-sm">{{ item.platform }}</td>
                    <td class="px-3 py-2 text-sm">{{ item.price }}</td>
                    <td class="px-3 py-2 text-sm">{{ item.salesIndex }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </section>

        <section v-if="activeSection === 'suggest'" class="bg-white rounded-xl card-shadow p-6">
          <h2 class="text-xl font-bold mb-4">销售建议</h2>
          <div class="flex flex-wrap gap-4 mb-4">
            <select v-model="suggestForm.crop"
              class="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
              <option value="" disabled>选择作物</option>
              <option v-for="crop in cropOptions" :key="crop" :value="crop">
                {{ crop }}
              </option>
            </select>

            <select v-model="suggestForm.channel"
              class="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
              <option value="" disabled>销售渠道</option>
              <option v-for="channel in channelOptions" :key="channel" :value="channel">
                {{ channel }}
              </option>
            </select>

            <button type="button" :disabled="suggestLoading"
              class="inline-flex items-center rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow active:translate-y-0 active:shadow-sm disabled:cursor-not-allowed disabled:opacity-60"
              @click="generateSuggestion">
              <i class="fa fa-magic mr-2"></i>
              {{ suggestLoading ? '生成中...' : '获取建议' }}
            </button>
          </div>

          <div v-if="suggestResult">
            <h3 class="font-medium mb-2">建议结果</h3>
            <div class="bg-gray-50 p-4 rounded-lg text-sm space-y-1">
              <div>最佳销售时间：<span class="font-bold">{{ suggestResult.bestTime }}</span></div>
              <div>建议价格区间：{{ suggestResult.priceRange }} 元/斤</div>
              <div>建议渠道：{{ suggestResult.channel }}</div>
              <div>原因：{{ suggestResult.reason }}</div>
            </div>
          </div>
        </section>

        <section v-if="activeSection === 'report'" class="bg-white rounded-xl card-shadow p-6">
          <h2 class="text-xl font-bold mb-4">分析报告</h2>
          <div class="flex flex-wrap gap-3 mb-4">
            <input v-model="reportForm.title" placeholder="搜索标题"
              class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
            <input v-model="reportForm.creator" placeholder="搜索分析师"
              class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
            <input v-model="reportForm.time" placeholder="搜索时间"
              class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
          </div>
          <div v-if="selectedReports.length === 0" class="py-6 text-center text-sm text-gray-500">暂无分析报告</div>
          <div v-else>
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">标题</th>
                  <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">生成时间</th>
                  <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">分析师</th>
                  <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">操作</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="report in selectedReports" :key="report.id">
                  <td class="px-3 py-2 text-sm">{{ report.title }}</td>
                  <td class="px-3 py-2 text-sm">{{ report.createTime }}</td>
                  <td class="px-3 py-2 text-sm">{{ report.creator }}</td>
                  <td class="px-3 py-2 text-sm">
                    <div class="flex items-center gap-2">
                      <button type="button" @click="viewReport(report)"
                        class="rounded-md border border-gray-300 px-2.5 py-1 text-gray-700 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow-sm active:translate-y-0">
                        查看
                      </button>
                      <button type="button"
                        class="rounded-md border border-green-500 bg-green-500 px-2.5 py-1 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow-sm active:translate-y-0">
                        导出
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-if="activeSection === 'alert'" class="bg-white rounded-xl card-shadow p-6">
          <h2 class="text-xl font-bold mb-4">价格波动报警</h2>
          <div class="mb-4">
            <label class="text-sm text-gray-700">波动阈值（%）</label>
            <input v-model.number="alertThreshold" type="number"
              class="ml-2 px-3 py-2 border border-gray-300 rounded-lg w-32">
          </div>

          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">作物</th>
                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">当前价格</th>
                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">昨日价格</th>
                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">波动幅度</th>
                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">状态</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-if="filteredPriceAlerts.length === 0">
                <td colspan="5" class="text-center text-sm text-gray-400 py-4">暂无价格波动预警</td>
              </tr>
              <template v-else>
                <tr v-for="alert in filteredPriceAlerts" :key="alert.id">
                  <td class="px-3 py-2 text-sm">{{ alert.crop }}</td>
                  <td class="px-3 py-2 text-sm">{{ alert.currentPrice }}</td>
                  <td class="px-3 py-2 text-sm">{{ alert.previousPrice }}</td>
                  <td class="px-3 py-2 text-sm text-orange-600">{{ alert.changeRate }}%</td>
                  <td class="px-3 py-2 text-sm">超过阈值</td>
                </tr>
              </template>
            </tbody>
          </table>
        </section>
        </main>
      </div>

      <div v-if="reportModalVisible && selectedReport"
        class="fixed inset-0 z-50 flex items-center justify-center bg-white/30 p-4 backdrop-blur-sm"
        @click.self="closeReportModal">
        <div class="w-full max-w-2xl overflow-hidden rounded-xl bg-white shadow-xl">
          <div class="border-b border-gray-200 p-6">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-bold text-gray-800">查看分析报告</h3>
              <button type="button" class="text-gray-400 hover:text-gray-500" @click="closeReportModal">
                <i class="fa fa-times"></i>
              </button>
            </div>
          </div>
          <div class="space-y-4 p-6">
            <div>
              <p class="text-sm text-gray-500">报告标题</p>
              <p class="mt-1 font-semibold text-gray-800">{{ selectedReport.title }}</p>
            </div>
            <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
              <div>
                <p class="text-sm text-gray-500">生成时间</p>
                <p class="mt-1 text-gray-800">{{ selectedReport.createTime }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">分析师</p>
                <p class="mt-1 text-gray-800">{{ selectedReport.creator }}</p>
              </div>
            </div>
            <div>
              <p class="text-sm text-gray-500">报告摘要</p>
              <p class="mt-2 rounded-lg bg-gray-50 p-4 text-sm leading-relaxed text-gray-700">
                {{ selectedReport.summary }}
              </p>
            </div>
            <div>
              <p class="text-sm text-gray-500">分析内容</p>
              <div class="mt-2 whitespace-pre-wrap rounded-lg border border-gray-200 p-4 text-sm leading-relaxed text-gray-700">
                {{ selectedReport.content }}
              </div>
            </div>
          </div>
          <div class="flex justify-end border-t border-gray-200 p-6">
            <button type="button"
              class="w-1/3 rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm"
              @click="closeReportModal">
              关闭
            </button>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const mockPlans = [
  { id: 1, planName: '2026年早稻种植', cropType: '水稻', landName: '1号地块', area: 15.2 },
  { id: 2, planName: '2026年冬小麦种植', cropType: '小麦', landName: '2号地块', area: 13.3 },
  { id: 3, planName: '2026年玉米轮作', cropType: '玉米', landName: '3号地块', area: 10.8 }
];

const mockYieldModels = [
  { id: 'rice-v1', cropType: '水稻', name: '水稻产量基础模型', baseYield: 950, confidence: 86 },
  { id: 'wheat-v1', cropType: '小麦', name: '小麦季节修正模型', baseYield: 720, confidence: 82 },
  { id: 'corn-v1', cropType: '玉米', name: '玉米生长周期模型', baseYield: 880, confidence: 84 }
];

const mockMarketData = [
  { platform: '惠农网', price: 2.61, salesIndex: 79 },
  { platform: '京东农场', price: 2.76, salesIndex: 68 },
  { platform: '拼多多农货', price: 2.55, salesIndex: 88 }
];

const mockSuggestions = [
  {
    crop: '小麦',
    channel: '批发',
    bestTime: '2026-06-15 ~ 2026-07-05',
    priceRange: '2.6 ~ 2.9',
    reason: '面粉加工企业集中补库，批发渠道出货稳定。'
  },
  {
    crop: '水稻',
    channel: '电商',
    bestTime: '2026-09-20 ~ 2026-10-10',
    priceRange: '2.9 ~ 3.2',
    reason: '新米上市初期关注度高，适合做产地直发。'
  },
  {
    crop: '玉米',
    channel: '零售',
    bestTime: '2026-10-05 ~ 2026-10-25',
    priceRange: '2.1 ~ 2.3',
    reason: '饲料端需求回升，零售端可配合小批量销售。'
  }
];

const mockReports = [
  {
    id: 1,
    title: '2026 小麦市场分析报告',
    createTime: '2026-06-20 10:00',
    creator: '分析师A',
    summary: '小麦批发端需求稳定，短期价格波动主要受加工企业补库节奏影响。',
    content: '1. 市场价格保持温和波动，批发渠道成交量稳定。\n2. 建议关注 6 月下旬至 7 月上旬的集中采购窗口。\n3. 若库存压力较低，可分批出货以平衡价格和现金流。'
  },
  {
    id: 2,
    title: '2026 水稻价格走势回顾',
    createTime: '2026-06-21 16:20',
    creator: '分析师B',
    summary: '水稻价格受新米预期和产地供应影响，电商渠道对优质产地标识更敏感。',
    content: '1. 新米上市前市场询价增加，但实际成交仍偏谨慎。\n2. 电商渠道适合突出产地、绿色种植和品质分级。\n3. 建议提前准备包装和物流方案。'
  },
  {
    id: 3,
    title: '玉米电商渠道对比简报',
    createTime: '2026-06-22 09:15',
    creator: '分析师A',
    summary: '玉米线上销售更适合小批量、高频次策略，价格优势需要与物流成本一起评估。',
    content: '1. 平台销量指数差异明显，促销活动对短期销量影响较大。\n2. 建议优先测试区域近场配送，降低履约成本。\n3. 后续可结合历史订单数据优化投放节奏。'
  }
];

const mockPriceAlerts = [
  { id: 1, crop: '小麦', currentPrice: 2.78, previousPrice: 2.31, changeRate: 20.3 },
  { id: 2, crop: '水稻', currentPrice: 3.08, previousPrice: 2.84, changeRate: 8.5 },
  { id: 3, crop: '玉米', currentPrice: 2.35, previousPrice: 1.95, changeRate: 20.5 }
];

const activeSection = ref('yield');
const plans = ref([...mockPlans]);
const models = ref([...mockYieldModels]);
const marketData = ref([...mockMarketData]);
const reports = ref([...mockReports]);
const yieldResult = ref(null);
const yieldLoading = ref(false);
const suggestResult = ref(null);
const suggestLoading = ref(false);
const selectedReport = ref(null);
const reportModalVisible = ref(false);
const alertThreshold = ref(20);

const yieldForm = ref({
  planId: '',
  modelId: ''
});

const suggestForm = ref({
  crop: '',
  channel: ''
});

const reportForm = ref({
  title: '',
  creator: '',
  time: ''
});

const selectedYieldPlan = computed(() => {
  return plans.value.find(plan => plan.id === yieldForm.value.planId);
});

const selectedYieldModel = computed(() => {
  return models.value.find(model => model.id === yieldForm.value.modelId);
});

const availableYieldModels = computed(() => {
  if (!selectedYieldPlan.value) {
    return [];
  }

  return models.value.filter(model => model.cropType === selectedYieldPlan.value.cropType);
});

const selectedReports = computed(() => {
  const creatorKeyword = reportForm.value.creator.trim();
  const timeKeyword = reportForm.value.time.trim();
  const titleKeyword = reportForm.value.title.trim();

  return reports.value.filter((report) => {
    const matchCreator = !creatorKeyword || report.creator.includes(creatorKeyword);
    const matchTime = !timeKeyword || report.createTime.includes(timeKeyword);
    const matchTitle = !titleKeyword || report.title.includes(titleKeyword);
    return matchTitle && matchCreator && matchTime;
  });
});

const filteredPriceAlerts = computed(() => {
  return mockPriceAlerts.filter(alert => alert.changeRate >= Number(alertThreshold.value || 0));
});

watch(() => yieldForm.value.planId, () => {
  yieldForm.value.modelId = '';
  yieldResult.value = null;
});

const sections = [
  { key: 'yield', label: '产量预测', icon: 'fa fa-line-chart' },
  { key: 'market', label: '市场趋势', icon: 'fa fa-shopping-cart' },
  { key: 'suggest', label: '销售建议', icon: 'fa fa-bullhorn' },
  { key: 'report', label: '分析报告', icon: 'fa fa-file-text' },
  { key: 'alert', label: '价格波动报警', icon: 'fa fa-exclamation-triangle' }
];

const channelOptions = ['批发', '零售', '电商'];

const cropOptions = computed(() => {
  return [...new Set(plans.value.map(plan => plan.cropType))];
});

const predictYield = async (payload) => {
  return {
    amount: 12880,
    unit: '千克',
    confidence: 86,
    summary: '模型服务返回的 mock 预测结果。'
  };
};

const generateYieldPrediction = async () => {
  if (!yieldForm.value.planId || !yieldForm.value.modelId) {
    alert('请选择种植计划和预测模型！');
    return;
  }

  yieldLoading.value = true;

  try {
    yieldResult.value = await predictYield({
      planId: yieldForm.value.planId,
      modelId: yieldForm.value.modelId
    });
  } catch (error) {
    console.error(error);
  } finally {
    yieldLoading.value = false;
  }
};

const getSuggestion = async (form) => {
  return mockSuggestions.find(item => {
    return item.crop === form.crop && item.channel === form.channel;
  }) || {
    crop: form.crop,
    channel: form.channel,
    bestTime: '待后端分析生成',
    priceRange: '待生成',
    reason: '当前 mock 数据中没有匹配建议，后续由后端分析服务返回。'
  };
};

const generateSuggestion = async () => {
  if (!suggestForm.value.crop || !suggestForm.value.channel) {
    alert('请选择作物种类和平台');
    return;
  }

  const form = suggestForm.value;

  suggestLoading.value = true;

  try {
    suggestResult.value = await getSuggestion(form);
  } catch (error) {
    console.error(error);
  } finally {
    suggestLoading.value = false;
  }
};

const viewReport = (report) => {
  selectedReport.value = report;
  reportModalVisible.value = true;
};

const closeReportModal = () => {
  selectedReport.value = null;
  reportModalVisible.value = false;
};
</script>
