<template>
    <div class="space-y-6">
        <!-- 查询区域 -->
        <div class="bg-white rounded-xl card-shadow p-6">
            <div class="mb-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                <div>
                    <h2 class="text-xl font-bold">报告管理</h2>
                </div>
                <button type="button"
                    class="inline-flex items-center rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm"
                    @click="openGenerateReportModal">
                    <i class="fa fa-plus mr-2"></i>生成报告
                </button>
            </div>
            <div class="flex flex-col gap-4 md:grid md:grid-cols-2 xl:flex xl:flex-row xl:items-end">
                <div class="xl:w-44 xl:shrink-0">
                    <label class="block text-sm font-medium text-gray-700 mb-1">所属地块</label>
                    <select v-model="searchReportForm.landId"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        <option value="all">全部地块</option>
                        <option v-for="land in lands" :key="land.id" :value="land.id">
                            {{ land.name }}
                        </option>
                    </select>
                </div>
                <div class="xl:w-44 xl:shrink-0">
                    <label class="block text-sm font-medium text-gray-700 mb-1">报告类型</label>
                    <select v-model="searchReportForm.type"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        <option value="all">全部</option>
                        <option v-for="type in types" :key="type" :value="type">{{ getType(type) }}</option>
                    </select>
                </div>
                <div class="xl:w-44 xl:shrink-0">
                    <label class="block text-sm font-medium text-gray-700 mb-1">开始日期</label>
                    <input v-model="searchReportForm.startDate" type="date"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                </div>
                <div class="xl:w-44 xl:shrink-0">
                    <label class="block text-sm font-medium text-gray-700 mb-1">结束日期</label>
                    <input v-model="searchReportForm.endDate" type="date"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                </div>
                <div class="xl:w-44 xl:shrink-0">
                    <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
                    <select v-model="searchReportForm.status" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        <option value="all">全部</option>
                        <option v-for="status in allStatus" :key="status" :value="status">{{ getStatus(status) }}</option>
                    </select>
                </div>
                <div class="xl:min-w-0 xl:flex-1">
                    <label class="block text-sm font-medium text-gray-700 mb-1">关键词</label>
                    <div class="flex items-center gap-2">
                        <input v-model="searchReportForm.keyword" type="text" placeholder="请输入关键词"
                            class="min-w-0 flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        <button type="button"
                            class="inline-flex shrink-0 items-center whitespace-nowrap rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow active:translate-y-0 active:shadow-sm"
                            @click="resetQuery">
                            <i class="fa fa-refresh mr-2"></i>重置查询
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 报告列表 -->
        <div>
            <div v-if="selectedReports.length === 0" class="bg-white rounded-xl card-shadow p-8 text-center text-sm text-gray-500">
                暂无报告
            </div>
            <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <div v-for="report in selectedReports" :key="report.id" class="bg-white rounded-xl card-shadow p-6">
                    <div class="flex items-start justify-between gap-4">
                        <div>
                            <span class="mb-3 inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium"
                                :class="reportTypeClasses[report.type] || reportTypeClasses.comprehensive">
                                <i class="fa" :class="reportTypeIcons[report.type] || reportTypeIcons.comprehensive"></i>
                                {{ getType(report.type) }}
                            </span>
                            <h3 class="font-bold text-lg">{{ report.title }}</h3>
                        </div>
                        <span class="inline-flex items-center whitespace-nowrap rounded-full px-2.5 py-1 text-xs font-medium"
                            :class="reportStatusClasses[report.status] || reportStatusClasses.draft">
                            {{ getStatus(report.status) }}
                        </span>
                    </div>
                    <p class="text-sm text-gray-600 leading-6 mt-3">{{ report.summary }}</p>
                    <div class="flex flex-wrap gap-x-4 gap-y-2 mt-4 text-sm text-gray-500">
                        <span>{{ report.startDate }} 至 {{ report.endDate }}</span>
                        <span>生成时间：{{ formatReportTime(report.generatedAt || report.createdAt) }}</span>
                        <span>作者：{{ report.creator }}</span>
                    </div>
                    <div class="flex justify-end gap-2 mt-4">
                        <button type="button" @click="viewReport(report)"
                            class="rounded-md border border-gray-300 px-2.5 py-1 text-sm text-gray-700 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow-sm active:translate-y-0">
                            查看
                        </button>
                        <button v-if="report.status === 'generated'" type="button" @click="archiveReport(report)"
                            class="rounded-md border border-green-500 bg-green-500 px-2.5 py-1 text-sm text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow-sm active:translate-y-0">
                            归档
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 生成报告模态框 -->
    <div v-if="generateReportModalVisible" tabindex="-1"
        class="fixed inset-0 z-50 flex items-center justify-center bg-white/30 backdrop-blur-sm px-4 py-6" @keyup.esc="quitGenerating">
        <div class="bg-white rounded-xl card-shadow p-6 w-full max-w-4xl max-h-[calc(100vh-3rem)] overflow-y-auto">
            <!-- 头部 -->
            <div class="border-b border-gray-200 pb-4">
                <h3 class="text-lg font-bold">生成报告</h3>
            </div>

            <!-- 内容区 -->
            <div class="space-y-4 mt-4">
                <!-- 1. 基本信息 -->
                <section class="bg-gray-50 rounded-xl p-4 border border-gray-100">
                    <h4 class="font-medium mb-3">① 报告基本信息</h4>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">所属地块</label>
                            <select v-model="reportForm.landId"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                                <option value="" disabled>请选择地块</option>
                                <option v-for="land in lands" :key="land.id" :value="land.id">
                                    {{ land.name }}
                                </option>
                            </select>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">报告类型</label>
                            <select v-model="reportForm.type"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                                <option value="" disabled>请选择报告类型</option>
                                <option v-for="type in types" :key="type" :value="type">
                                    {{ getType(type) }}
                                </option>
                            </select>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">报告标题</label>
                            <input v-model="reportForm.title" type="text" placeholder="请输入报告标题"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">开始日期</label>
                            <input v-model="reportForm.startDate" type="date"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">结束日期</label>
                            <input v-model="reportForm.endDate" type="date"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">报告作者</label>
                            <input v-model="reportForm.creator" type="text" placeholder="请输入作者姓名"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        </div>
                    </div>
                </section>

                <!-- 2. 生成说明 -->
                <section class="bg-gray-50 rounded-xl p-4 border border-gray-100">
                    <h4 class="font-medium mb-2">② 报告内容</h4>
                    <p class="text-sm leading-6 text-gray-600">
                        系统将根据所选地块和日期范围，汇总设备、环境监测、异常预警和农事任务数据并生成快照。
                    </p>
                </section>

            </div>

            <!-- 底部按钮 -->
            <div class="flex justify-end gap-3 mt-6">
                <button type="button"
                    class="w-1/3 rounded-lg border border-gray-500 bg-gray-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-gray-600 hover:shadow active:translate-y-0 active:shadow-sm"
                    @click="quitGenerating">
                    取消
                </button>
                <button type="button"
                    class="w-1/3 rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm"
                    @click="generateReport">
                    立即生成
                </button>
            </div>
        </div>
    </div>

    <!-- 查看报告模态框 -->
    <div v-if="viewReportModalVisible && currentReport" tabindex="-1"
        class="fixed inset-0 z-50 flex items-center justify-center bg-white/30 backdrop-blur-sm px-4 py-6" @keyup.esc="quitViewing">
        <div class="relative flex max-h-[90vh] w-full max-w-2xl flex-col overflow-hidden rounded-xl bg-white card-shadow">
            <button type="button" class="absolute right-4 top-4 z-10 text-2xl text-gray-400 hover:text-gray-600"
                @click="quitViewing">&times;</button>
            <div class="shrink-0 border-b border-gray-100 bg-white p-6 pr-12">
                <div class="space-y-3">
                    <span class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium"
                        :class="reportTypeClasses[currentReport.type] || reportTypeClasses.comprehensive">
                        <i class="fa" :class="reportTypeIcons[currentReport.type] || reportTypeIcons.comprehensive"></i>
                        {{ getType(currentReport.type) }}
                    </span>
                    <h3 class="text-lg font-bold">{{ currentReport.title }}</h3>
                    <p class="text-sm text-gray-500">{{ currentReport.startDate }} 至 {{ currentReport.endDate }}</p>
                    <p class="text-sm text-gray-500">
                        生成时间：{{ formatReportTime(currentReport.generatedAt || currentReport.createdAt) }}
                    </p>
                </div>
            </div>
            <div class="min-h-0 flex-1 overflow-y-auto p-6">
                <div class="space-y-3">
                <div class="rounded-lg bg-gray-50 p-4">
                    <h4 class="mb-2 text-sm font-medium text-gray-700">报告摘要</h4>
                    <p class="text-sm leading-6 text-gray-600">{{ currentReport.summary || '暂无摘要' }}</p>
                </div>
                <div v-if="currentReport.snapshot?.land"
                    class="grid grid-cols-2 gap-4 rounded-lg border border-gray-100 p-4">
                    <div>
                        <p class="text-xs text-gray-400">地块</p>
                        <p class="mt-1 text-sm font-medium text-gray-700">{{ currentReport.snapshot.land.name }}</p>
                    </div>
                    <div>
                        <p class="text-xs text-gray-400">当前作物</p>
                        <p class="mt-1 text-sm font-medium text-gray-700">{{ currentReport.snapshot.land.crop || '暂无作物' }}</p>
                    </div>
                    <div>
                        <p class="text-xs text-gray-400">种植面积</p>
                        <p class="mt-1 text-sm font-medium text-gray-700">{{ currentReport.snapshot.land.area }} 亩</p>
                    </div>
                    <div>
                        <p class="text-xs text-gray-400">报告状态</p>
                        <p class="mt-1 text-sm font-medium text-gray-700">{{ getStatus(currentReport.status) }}</p>
                    </div>
                </div>
                <div v-if="currentReport.snapshot?.devices && shouldShowSnapshotSection('device')"
                    class="rounded-lg border border-gray-100 p-4">
                    <h4 class="mb-3 text-sm font-medium text-gray-700">设备运行情况</h4>
                    <div class="grid grid-cols-2 gap-3 sm:grid-cols-4">
                        <div class="rounded-lg bg-gray-50 p-3 text-center">
                            <p class="text-xl font-bold text-gray-800">{{ currentReport.snapshot.devices.total }}</p>
                            <p class="mt-1 text-xs text-gray-500">设备总数</p>
                        </div>
                        <div class="rounded-lg bg-emerald-50 p-3 text-center">
                            <p class="text-xl font-bold text-emerald-700">{{ currentReport.snapshot.devices.online }}</p>
                            <p class="mt-1 text-xs text-emerald-600">在线</p>
                        </div>
                        <div class="rounded-lg bg-red-50 p-3 text-center">
                            <p class="text-xl font-bold text-red-700">{{ currentReport.snapshot.devices.offline }}</p>
                            <p class="mt-1 text-xs text-red-600">离线</p>
                        </div>
                        <div class="rounded-lg bg-amber-50 p-3 text-center">
                            <p class="text-xl font-bold text-amber-700">{{ currentReport.snapshot.devices.lowBattery }}</p>
                            <p class="mt-1 text-xs text-amber-600">低电量</p>
                        </div>
                    </div>
                </div>
                <div v-if="currentReport.snapshot?.environment && shouldShowSnapshotSection('environment')"
                    class="rounded-lg border border-gray-100 p-4">
                    <h4 class="mb-3 text-sm font-medium text-gray-700">环境监测数据</h4>
                    <div v-if="currentReport.snapshot.environment.length" class="space-y-3">
                        <div v-for="reading in currentReport.snapshot.environment" :key="reading.metric"
                            class="flex flex-col gap-3 rounded-lg bg-gray-50 p-3 sm:flex-row sm:items-center sm:justify-between">
                            <div>
                                <p class="text-sm font-medium text-gray-700">{{ metricLabels[reading.metric] || reading.metric }}</p>
                                <p class="mt-1 text-xs text-gray-400">{{ formatReportTime(reading.recordedAt) }}</p>
                            </div>
                            <div class="flex items-center gap-3">
                                <span class="text-base font-semibold text-gray-800">{{ reading.value }} {{ reading.unit }}</span>
                                <span class="rounded-full px-2.5 py-1 text-xs font-medium"
                                    :class="environmentStatusClasses[reading.status] || environmentStatusClasses.no_data">
                                    {{ environmentStatusLabels[reading.status] || '未知状态' }}
                                </span>
                            </div>
                        </div>
                    </div>
                    <p v-else class="rounded-lg bg-gray-50 px-4 py-8 text-center text-sm text-gray-500">
                        当前报告时间范围内暂无环境监测数据
                    </p>
                </div>
                <div v-if="currentReport.snapshot?.alerts && shouldShowSnapshotSection('alert')"
                    class="rounded-lg border border-gray-100 p-4">
                    <h4 class="mb-3 text-sm font-medium text-gray-700">异常预警统计</h4>
                    <div class="grid grid-cols-2 gap-3 sm:grid-cols-5">
                        <div class="rounded-lg bg-gray-50 p-3 text-center">
                            <p class="text-xl font-bold text-gray-800">{{ currentReport.snapshot.alerts.total }}</p>
                            <p class="mt-1 text-xs text-gray-500">预警总数</p>
                        </div>
                        <div class="rounded-lg bg-orange-50 p-3 text-center">
                            <p class="text-xl font-bold text-orange-700">{{ currentReport.snapshot.alerts.pending }}</p>
                            <p class="mt-1 text-xs text-orange-600">待处理</p>
                        </div>
                        <div class="rounded-lg bg-blue-50 p-3 text-center">
                            <p class="text-xl font-bold text-blue-700">{{ currentReport.snapshot.alerts.processing }}</p>
                            <p class="mt-1 text-xs text-blue-600">处理中</p>
                        </div>
                        <div class="rounded-lg bg-emerald-50 p-3 text-center">
                            <p class="text-xl font-bold text-emerald-700">{{ currentReport.snapshot.alerts.resolved }}</p>
                            <p class="mt-1 text-xs text-emerald-600">已解决</p>
                        </div>
                        <div class="rounded-lg bg-gray-100 p-3 text-center">
                            <p class="text-xl font-bold text-gray-600">{{ currentReport.snapshot.alerts.ignored }}</p>
                            <p class="mt-1 text-xs text-gray-500">已忽略</p>
                        </div>
                    </div>
                </div>
                <div v-if="currentReport.snapshot?.tasks && shouldShowSnapshotSection('task')"
                    class="rounded-lg border border-gray-100 p-4">
                    <h4 class="mb-3 text-sm font-medium text-gray-700">农事任务统计</h4>
                    <div class="grid grid-cols-2 gap-3 sm:grid-cols-5">
                        <div class="rounded-lg bg-gray-50 p-3 text-center">
                            <p class="text-xl font-bold text-gray-800">{{ currentReport.snapshot.tasks.total }}</p>
                            <p class="mt-1 text-xs text-gray-500">任务总数</p>
                        </div>
                        <div class="rounded-lg bg-orange-50 p-3 text-center">
                            <p class="text-xl font-bold text-orange-700">{{ currentReport.snapshot.tasks.pending }}</p>
                            <p class="mt-1 text-xs text-orange-600">待处理</p>
                        </div>
                        <div class="rounded-lg bg-blue-50 p-3 text-center">
                            <p class="text-xl font-bold text-blue-700">{{ currentReport.snapshot.tasks.processing }}</p>
                            <p class="mt-1 text-xs text-blue-600">进行中</p>
                        </div>
                        <div class="rounded-lg bg-emerald-50 p-3 text-center">
                            <p class="text-xl font-bold text-emerald-700">{{ currentReport.snapshot.tasks.completed }}</p>
                            <p class="mt-1 text-xs text-emerald-600">已完成</p>
                        </div>
                        <div class="rounded-lg bg-gray-100 p-3 text-center">
                            <p class="text-xl font-bold text-gray-600">{{ currentReport.snapshot.tasks.cancelled }}</p>
                            <p class="mt-1 text-xs text-gray-500">已取消</p>
                        </div>
                    </div>
                </div>
                <div v-if="currentReport.snapshot?.aiAdvice && shouldShowSnapshotSection('aiAdvice')"
                    class="rounded-lg border border-emerald-100 bg-emerald-50/30 p-4">
                    <h4 class="mb-3 flex items-center gap-2 text-sm font-medium text-gray-700">
                        <i class="fa fa-leaf text-emerald-600"></i>
                        AI 顾问建议
                    </h4>
                    <div v-if="currentReport.snapshot.aiAdvice.length" class="space-y-3">
                        <article v-for="advice in currentReport.snapshot.aiAdvice" :key="advice.messageId"
                            class="rounded-lg border border-emerald-100 bg-white p-4">
                            <p class="whitespace-pre-wrap text-sm leading-6 text-gray-700">{{ advice.content }}</p>
                            <div v-if="advice.references?.length"
                                class="mt-4 grid grid-cols-1 gap-2 border-t border-gray-100 pt-3 sm:grid-cols-2">
                                <div v-for="reference in advice.references"
                                    :key="`${reference.type}:${reference.sourceId}:${reference.label}`"
                                    class="rounded-md bg-gray-50 px-3 py-2">
                                    <p class="text-xs text-gray-400">{{ reference.label }}</p>
                                    <p class="mt-1 text-sm font-medium text-gray-700">
                                        {{ reference.value }}{{ reference.unit }}
                                    </p>
                                </div>
                            </div>
                            <p class="mt-3 text-xs text-gray-400">{{ formatReportTime(advice.createdAt) }}</p>
                        </article>
                    </div>
                    <p v-else class="rounded-lg bg-white px-4 py-8 text-center text-sm text-gray-500">
                        当前报告时间范围内暂无 AI 建议
                    </p>
                </div>
                <div class="pt-3 border-t border-gray-100 text-sm text-gray-500">
                    作者：{{ currentReport.creator }}
                </div>
            </div>
            </div>
        </div>
    </div>

</template>

<script setup>
import { computed, ref } from "vue";
import { toast } from '../utils/toast';
import { useFarmStore } from '../composables/useFarmStore';

const emptyReportForm = {
    id: '',
    landId: '',
    type: '',
    title: '',
    startDate: '',
    endDate: '',
    creator: ''
};

const emptySearchReportForm = {
    landId: 'all',
    type: 'all',
    startDate: '',
    endDate: '',
    status: 'all',
    keyword: ''
};

// 开关变量
const generateReportModalVisible = ref(false);
const viewReportModalVisible = ref(false);

// 数据模型
// ref
const reportForm = ref({ ...emptyReportForm });
const searchReportForm = ref({ ...emptySearchReportForm });

const {
    lands,
    devices,
    sensorReadings,
    environmentThresholds,
    alerts,
    farmTasks,
    reports,
    aiConversations
} = useFarmStore();
const types = ref(['comprehensive', 'device', 'environment', 'alert', 'task']);
const allStatus = ref(['draft', 'generated', 'archived']);

const currentReport = ref(null);

const reportTypeMap = {
    comprehensive: '综合运行报告',
    device: '设备运行报告',
    environment: '环境监测报告',
    alert: '异常预警报告',
    task: '农事任务报告',
};

const reportStatusMap = {
    draft: '草稿',
    generated: '已生成',
    archived: '已归档',
};

const reportTypeClasses = {
    comprehensive: 'bg-emerald-50 text-emerald-700',
    device: 'bg-sky-50 text-sky-700',
    environment: 'bg-teal-50 text-teal-700',
    alert: 'bg-red-50 text-red-700',
    task: 'bg-violet-50 text-violet-700'
};

const reportTypeIcons = {
    comprehensive: 'fa-pie-chart',
    device: 'fa-microchip',
    environment: 'fa-thermometer-half',
    alert: 'fa-exclamation-triangle',
    task: 'fa-tasks'
};

const reportStatusClasses = {
    draft: 'bg-amber-50 text-amber-700',
    generated: 'bg-blue-50 text-blue-700',
    archived: 'bg-gray-100 text-gray-500'
};

const metricLabels = {
    soil_moisture: '土壤湿度',
    air_temperature: '空气温度',
    air_humidity: '空气湿度',
    light: '光照强度',
    soil_ph: '土壤 pH'
};

const environmentStatusLabels = {
    normal: '正常',
    low: '偏低',
    high: '偏高',
    no_data: '无数据',
    unconfigured: '未配置阈值'
};

const environmentStatusClasses = {
    normal: 'bg-emerald-50 text-emerald-700',
    low: 'bg-blue-50 text-blue-700',
    high: 'bg-red-50 text-red-700',
    no_data: 'bg-gray-100 text-gray-500',
    unconfigured: 'bg-gray-100 text-gray-600'
};

const formatReportTime = value => {
    if (!value) return '尚未生成';

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return '时间未知';

    return new Intl.DateTimeFormat('zh-CN', {
        timeZone: 'Asia/Shanghai',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    }).format(date);
};

const isWithinDateRange = (value, startDate, endDate) => {
    const time = new Date(value).getTime();
    const startTime = new Date(`${startDate}T00:00:00+08:00`).getTime();
    const endTime = new Date(`${endDate}T23:59:59.999+08:00`).getTime();

    return Number.isFinite(time) && time >= startTime && time <= endTime;
};

const getSnapshotReadingStatus = (reading, threshold) => {
    if (!Number.isFinite(reading.value)) return 'no_data';
    if (!threshold) return 'unconfigured';
    if (reading.value <= threshold.min) return 'low';
    if (reading.value >= threshold.max) return 'high';
    return 'normal';
};

const buildReportSnapshot = (landId, startDate, endDate) => {
    const land = lands.value.find(item => item.id === landId);
    if (!land) throw new Error('报告所属地块不存在');

    const landDevices = devices.value.filter(device => device.landId === landId);
    const periodReadings = sensorReadings.value.filter(reading =>
        reading.landId === landId &&
        isWithinDateRange(reading.recordedAt, startDate, endDate)
    );

    const latestReadingByMetric = periodReadings.reduce((result, reading) => {
        const current = result[reading.metric];
        if (!current || new Date(reading.recordedAt) > new Date(current.recordedAt)) {
            result[reading.metric] = reading;
        }
        return result;
    }, {});

    const environment = Object.values(latestReadingByMetric).map(reading => {
        const threshold = environmentThresholds.value.find(item =>
            item.landId === landId &&
            item.metric === reading.metric &&
            item.enabled
        );

        return {
            metric: reading.metric,
            value: reading.value,
            unit: reading.unit,
            status: getSnapshotReadingStatus(reading, threshold),
            recordedAt: reading.recordedAt
        };
    });

    const periodAlerts = alerts.value.filter(alert =>
        alert.landId === landId &&
        isWithinDateRange(alert.occurredAt, startDate, endDate)
    );
    const periodTasks = farmTasks.value.filter(task =>
        task.landId === landId &&
        isWithinDateRange(task.createdAt, startDate, endDate)
    );
    const periodAiAdvice = aiConversations.value
        .filter(conversation => conversation.landId === landId)
        .flatMap(conversation => conversation.messages)
        .filter(message =>
            message.role === 'assistant' &&
            isWithinDateRange(message.createdAt, startDate, endDate))
        .map(message => ({
            messageId: message.id,
            content: message.content,
            createdAt: message.createdAt,
            references: message.references.map(reference => ({ ...reference }))
        }));

    return {
        land: {
            id: land.id,
            name: land.name,
            crop: land.crop,
            area: land.area
        },
        devices: {
            total: landDevices.length,
            online: landDevices.filter(device => device.status === 'online').length,
            offline: landDevices.filter(device => device.status === 'offline').length,
            lowBattery: landDevices.filter(device =>
                Number.isFinite(device.battery) && device.battery < 20
            ).length
        },
        environment,
        alerts: {
            total: periodAlerts.length,
            pending: periodAlerts.filter(alert => alert.status === 'pending').length,
            processing: periodAlerts.filter(alert => alert.status === 'processing').length,
            resolved: periodAlerts.filter(alert => alert.status === 'resolved').length,
            ignored: periodAlerts.filter(alert => alert.status === 'ignored').length
        },
        tasks: {
            total: periodTasks.length,
            pending: periodTasks.filter(task => task.status === 'pending').length,
            processing: periodTasks.filter(task => task.status === 'processing').length,
            completed: periodTasks.filter(task => task.status === 'completed').length,
            cancelled: periodTasks.filter(task => task.status === 'cancelled').length
        },
        aiAdvice: periodAiAdvice
    };
};

const buildReportSummary = (type, snapshot) => {
    const abnormalEnvironmentCount = snapshot.environment.filter(reading =>
        reading.status === 'low' || reading.status === 'high'
    ).length;
    const activeAlertCount = snapshot.alerts.pending + snapshot.alerts.processing;
    const activeTaskCount = snapshot.tasks.pending + snapshot.tasks.processing;
    const aiAdviceCount = snapshot.aiAdvice.length;
    const aiAdviceSummary = aiAdviceCount > 0
        ? `本期收录了 ${aiAdviceCount} 条 AI 顾问建议。`
        : '本期暂无 AI 顾问建议。';

    const summaryBuilders = {
        comprehensive: () =>
            `本期记录 ${snapshot.environment.length} 项环境指标，其中 ${abnormalEnvironmentCount} 项异常；` +
            `存在 ${activeAlertCount} 条未结束预警和 ${activeTaskCount} 项未结束农事任务。` +
            aiAdviceSummary,
        device: () =>
            `当前地块共有 ${snapshot.devices.total} 台设备，${snapshot.devices.online} 台在线，` +
            `${snapshot.devices.offline} 台离线，${snapshot.devices.lowBattery} 台处于低电量状态。`,
        environment: () =>
            `本期记录 ${snapshot.environment.length} 项环境指标，其中 ${abnormalEnvironmentCount} 项超出适宜范围。`,
        alert: () =>
            `本期共有 ${snapshot.alerts.total} 条异常预警，${snapshot.alerts.pending} 条待处理，` +
            `${snapshot.alerts.processing} 条处理中，${snapshot.alerts.resolved} 条已解决，` +
            `${snapshot.alerts.ignored} 条已忽略。`,
        task: () =>
            `本期共有 ${snapshot.tasks.total} 项农事任务，${snapshot.tasks.pending} 项待处理，` +
            `${snapshot.tasks.processing} 项进行中，${snapshot.tasks.completed} 项已完成，` +
            `${snapshot.tasks.cancelled} 项已取消。`
    };

    return summaryBuilders[type]?.() || '暂无报告摘要';
};

const getNextReportId = () => {
    const largestId = reports.value.reduce((largest, report) => {
        const numericId = Number.parseInt(String(report.id).replace(/\D/g, ''), 10);
        return Number.isFinite(numericId) ? Math.max(largest, numericId) : largest;
    }, 0);

    return `REP-${String(largestId + 1).padStart(3, '0')}`;
};

// computed
const selectedReports = computed(() => {
    const landId = searchReportForm.value.landId;
    const type = searchReportForm.value.type.trim();
    const startDate = searchReportForm.value.startDate.trim();
    const endDate = searchReportForm.value.endDate.trim();
    const status = searchReportForm.value.status.trim();
    const keyword = searchReportForm.value.keyword.trim();

    return reports.value.filter(report => {
        const landMatched = landId === 'all' || report.landId === landId;
        const typeMatched = !type || type === 'all' || report.type === type;
        const startDateMatched = !startDate || report.endDate >= startDate;
        const endDateMatched = !endDate || report.startDate <= endDate;
        const statusMatched = !status || status === 'all' || report.status === status;
        const keywordMatched = !keyword || report.title.includes(keyword);

        return landMatched && typeMatched && startDateMatched && endDateMatched && statusMatched && keywordMatched;
    });
});

// 交互方法
const openGenerateReportModal = () => {
    reportForm.value = {
        ...emptyReportForm,
        creator: localStorage.getItem('username') || ''
    };
    generateReportModalVisible.value = true;
};

const validateReport = report => {
    if (!report.landId) {
        toast('请选择报告所属地块', 'bg-orange-500');
        return false;
    }

    if (!report.type) {
        toast('请选择报告类型', 'bg-orange-500');
        return false;
    }

    if (!report.title.trim()) {
        toast('请输入报告标题', 'bg-orange-500');
        return false;
    }

    if (!report.startDate) {
        toast('请选择开始日期', 'bg-orange-500');
        return false;
    }

    if (!report.endDate) {
        toast('请选择结束日期', 'bg-orange-500');
        return false;
    }

    if (report.endDate < report.startDate) {
        toast('结束时间不能早于开始时间', 'bg-orange-500');
        return false;
    }

    if (!report.creator.trim()) {
        toast('请输入报告作者', 'bg-orange-500');
        return false;
    }

    return true;
};

const generateReport = () => {
    const form = { ...reportForm.value };
    if (!validateReport(form)) {
        return;
    }

    try {
        const snapshot = buildReportSnapshot(form.landId, form.startDate, form.endDate);
        const now = new Date().toISOString();

        reports.value.unshift({
            id: getNextReportId(),
            landId: form.landId,
            type: form.type,
            title: form.title.trim(),
            startDate: form.startDate,
            endDate: form.endDate,
            status: 'generated',
            creator: form.creator.trim(),
            createdAt: now,
            generatedAt: now,
            summary: buildReportSummary(form.type, snapshot),
            snapshot
        });
        toast('报告生成成功');

        reportForm.value = { ...emptyReportForm };
    } catch (error) {
        console.error(error);
        toast('生成报告失败：' + error.message, 'bg-red-500');
    } finally {
        generateReportModalVisible.value = false;
    }
};

const quitGenerating = () => {
    reportForm.value = { ...emptyReportForm };
    generateReportModalVisible.value = false;
};

const resetQuery = () => {
    searchReportForm.value = { ...emptySearchReportForm };
};

const viewReport = (report) => {
    if (!report) {
        toast('未选中报告', 'bg-orange-500');
        return;
    }
    currentReport.value = report;
    viewReportModalVisible.value = true;
};

const quitViewing = () => {
    currentReport.value = null;
    viewReportModalVisible.value = false;
};

const archiveReport = report => {
    const target = reports.value.find(item => item.id === report.id);
    if (!target) {
        return toast('报告不存在', 'bg-red-500');
    }

    target.status = 'archived';
    toast('报告已归档');
};

const getType = (type) => {
    return reportTypeMap[type] || type;
};

const getStatus = (status) => {
    return reportStatusMap[status] || status;
};

const shouldShowSnapshotSection = section => {
    return currentReport.value?.type === 'comprehensive' || currentReport.value?.type === section;
};

</script>
