<template>
    <div class="container mx-auto px-4 pt-24 pb-16 space-y-6">
        <div class="bg-white rounded-xl card-shadow p-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
                <p class="text-sm font-medium text-primary mb-1">Report Center</p>
                <h2 class="text-xl font-bold">报告管理</h2>
            </div>
            <button class="btn-primary" type="button" @click="openGenerateReportModal">生成报告</button>
        </div>

        <!-- 查询区域 -->
        <div class="bg-white rounded-xl card-shadow p-6">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">报告类型</label>
                    <select v-model="searchReportForm.type"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        <option value="all">全部</option>
                        <option v-for="type in types" :key="type" :value="type">{{ getType(type) }}</option>
                    </select>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">开始日期</label>
                    <input v-model="searchReportForm.startDate" type="date"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">结束日期</label>
                    <input v-model="searchReportForm.endDate" type="date"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
                    <select v-model="searchReportForm.status" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                        <option value="all">全部</option>
                        <option v-for="status in allStatus" :key="status" :value="status">{{ getStatus(status) }}</option>
                    </select>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">关键词</label>
                    <input v-model="searchReportForm.keyword" type="text" placeholder="请输入关键词"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                </div>
            </div>
            <div class="flex justify-end mt-4">
                <button type="button" class="btn-outline text-sm px-3 py-1.5" @click="resetQuery">重置查询</button>
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
                            <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-primary/10 text-primary mb-3">{{ getType(report.type) }}</span>
                            <h3 class="font-bold text-lg">{{ report.title }}</h3>
                        </div>
                        <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-blue-50 text-blue-600 whitespace-nowrap">{{ getStatus(report.status) }}</span>
                    </div>
                    <p class="text-sm text-gray-600 leading-6 mt-3">{{ report.summary }}</p>
                    <div class="flex flex-wrap gap-x-4 gap-y-2 mt-4 text-sm text-gray-500">
                        <span>{{ report.startDate }} 至 {{ report.endDate }}</span>
                        <span>生成时间：{{ report.generateTime }}</span>
                        <span>作者：{{ report.creator }}</span>
                    </div>
                    <div class="flex justify-end gap-2 mt-4">
                        <button type="button" @click="viewReport(report)" class="btn-outline text-sm px-3 py-1.5">查看</button>
                        <button type="button" @click="editReport(report)" class="btn-outline text-sm px-3 py-1.5">编辑</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 生成报告模态框 -->
    <div v-if="generateReportModalVisible" tabindex="-1"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4 py-6" @keyup.esc="quitGenerating">
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

                <!-- 2. 动态内容 -->
                <section class="bg-gray-50 rounded-xl p-4 border border-gray-100">
                    <h4 class="font-medium mb-3">② 报告内容</h4>
                    <textarea v-model="reportForm.content" rows="6" placeholder="请输入报告正文或生成要求"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"></textarea>
                </section>

            </div>

            <!-- 底部按钮 -->
            <div class="flex justify-end gap-3 mt-6">
                <button class="btn-outline" type="button" @click="quitGenerating">取消</button>
                <button class="btn-primary" type="button" :disabled="generatingReport" @click="generateReport">
                    {{ generatingReport ? '生成中...' : '立即生成' }}
                </button>
            </div>
        </div>
    </div>

    <!-- 查看报告模态框 -->
    <div v-if="viewReportModalVisible && currentReport" tabindex="-1"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 px-4 py-6" @keyup.esc="quitViewing">
        <div class="relative bg-white rounded-xl card-shadow p-6 w-full max-w-2xl">
            <span class="absolute top-3 right-4 text-2xl text-gray-400 cursor-pointer hover:text-gray-600" @click="quitViewing">&times;</span>
            <div class="space-y-3">
                <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-primary/10 text-primary">{{ getType(currentReport.type) }}</span>
                <h3 class="text-lg font-bold">{{ currentReport.title }}</h3>
                <p class="text-sm text-gray-500">{{ currentReport.startDate }} 至 {{ currentReport.endDate }}</p>
                <p class="text-sm text-gray-500">生成时间：{{ currentReport.generateTime }}</p>
                <div class="text-sm text-gray-600 leading-6 space-y-2">
                    <p>{{ currentReport.content }}</p>
                </div>
                <div class="pt-3 border-t border-gray-100 text-sm text-gray-500">
                    作者：{{ currentReport.creator }}
                </div>
            </div>
        </div>
    </div>

    <!-- 编辑报告模态框 -->
    <div v-if="editReportModalVisible" tabindex="-1"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 px-4 py-6" @keyup.esc="quitEditing">
        <div class="relative bg-white rounded-xl card-shadow p-6 w-full max-w-2xl">
            <span class="absolute top-3 right-4 text-2xl text-gray-400 cursor-pointer hover:text-gray-600" @click="quitEditing">&times;</span>
            <h3 class="text-lg font-bold mb-4">编辑报告</h3>
            <div class="space-y-4">
                <section class="bg-gray-50 rounded-xl p-4 border border-gray-100">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
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
                            <label class="block text-sm font-medium text-gray-700 mb-1">报告状态</label>
                            <select v-model="reportForm.status" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                                <option v-for="status in allStatus" :key="status" :value="status">{{ getStatus(status) }}</option>
                            </select>
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
                    </div>
                </section>
                <section class="bg-gray-50 rounded-xl p-4 border border-gray-100">
                    <label class="block text-sm font-medium text-gray-700 mb-1">报告内容</label>
                    <textarea v-model="reportForm.content" rows="6" placeholder="请输入报告内容"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"></textarea>
                </section>
                <div class="flex justify-end gap-3">
                    <button type="button" class="btn-outline" @click="quitEditing">取消</button>
                    <button type="button" :disabled="submittingEdit" @click="submitEdit" class="btn-primary">
                        {{ submittingEdit ? '提交中...' : '提交修改' }}
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { toast } from '../utils/toast';
// mock 数据
const mockReports = [
    {
        id: 1,
        type: 'business',
        title: '温室番茄月度经营报告',
        startDate: '2026-06-01',
        endDate: '2026-06-30',
        generateTime: '2026-07-01 09:30',
        status: 'generated',
        creator: '李明',
        summary: '汇总温室番茄的采收、成本、销售和风险情况，为下月种植管理提供参考。',
        content: '本期温室番茄采收稳定，销售收入较上月小幅提升。后续需要重点关注高温天气下的水肥调控，并保持病虫害巡检频率。',
    },
    {
        id: 2,
        type: 'yield',
        title: '七月叶菜产量预测报告',
        startDate: '2026-07-01',
        endDate: '2026-07-31',
        generateTime: '2026-07-01 14:20',
        status: 'generating',
        creator: '王雪',
        summary: '结合种植计划、历史采收记录和近期环境数据，预估七月叶菜产量变化趋势。',
        content: '七月叶菜产量预计保持平稳，短期波动主要来自温度和降雨变化。建议结合地块环境数据动态调整采收节奏。',
    },
    {
        id: 3,
        type: 'pest',
        title: '二号地块病虫害巡检记录',
        startDate: '2026-06-20',
        endDate: '2026-06-27',
        generateTime: '2026-06-27 17:10',
        status: 'draft',
        creator: '赵强',
        summary: '记录二号地块近期病虫害巡检结果、风险点和后续处理建议。',
        content: '二号地块暂未发现大面积病虫害，但局部叶片存在轻微异常。建议继续观察异常区域，并在三天内完成复查。',
    },
];

const mockStatus = [
    'draft',
    'generating',
    'generated',
];

const emptyReportForm = {
    id: '',
    type: '',
    title: '',
    startDate: '',
    endDate: '',
    generateTime: '',
    creator: '',
    status: '',
    summary: '',
    content: ''
};

const emptySearchReportForm = {
    type: 'all',
    startDate: '',
    endDate: '',
    status: 'all',
    keyword: ''
};

// 开关变量
const generateReportModalVisible = ref(false);
const viewReportModalVisible = ref(false);
const editReportModalVisible = ref(false);

// 状态变量
const generatingReport = ref(false);
const submittingEdit = ref(false);

// 数据模型
// ref
const reportForm = ref({ ...emptyReportForm });
const searchReportForm = ref({ ...emptySearchReportForm });

const reports = ref([...mockReports]);
const types = ref(['business', 'yield', 'pest', 'task']);
const allStatus = ref([...mockStatus]);

const currentReport = ref(null);

const reportTypeMap = {
    business: '经营分析报告',
    yield: '产量预测报告',
    pest: '病虫害巡检报告',
    task: '农事任务报告',
};

const reportStatusMap = {
    draft: '草稿',
    generating: '生成中',
    generated: '已生成',
};

const formatDateTime = (date) => {
    const pad = (value) => String(value).padStart(2, '0');

    return [
        date.getFullYear(),
        pad(date.getMonth() + 1),
        pad(date.getDate()),
    ].join('-') + ' ' + [
        pad(date.getHours()),
        pad(date.getMinutes()),
    ].join(':');
};

// computed
const selectedReports = computed(() => {
    const type = searchReportForm.value.type.trim();
    const startDate = searchReportForm.value.startDate.trim();
    const endDate = searchReportForm.value.endDate.trim();
    const status = searchReportForm.value.status.trim();
    const keyword = searchReportForm.value.keyword.trim();

    return reports.value.filter(report => {
        const typeMatched = !type || type === 'all' || report.type === type;
        const startDateMatched = !startDate || report.endDate >= startDate;
        const endDateMatched = !endDate || report.startDate <= endDate;
        const statusMatched = !status || status === 'all' || report.status === status;
        const keywordMatched = !keyword || report.title.includes(keyword);

        return typeMatched && startDateMatched && endDateMatched && statusMatched && keywordMatched;
    });
});

// 交互方法
const openGenerateReportModal = () => {
    generateReportModalVisible.value = true;
};

const validateReport = (report, needStatus = false) => {
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

    if (needStatus && !report.status) {
        toast('请选择报告状态', 'bg-orange-500');
        return false;
    }

    if (!report.content.trim()) {
        toast('请输入报告内容', 'bg-orange-500');
        return false;
    }

    return true;
};

const generateReport = async () => {
    const newReport = { ...reportForm.value };
    if (!validateReport(newReport)) {
        return;
    }

    generatingReport.value = true;

    try {
        // const res = await fetch('/api/...', {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json'
        //     },
        //     body: JSON.stringify(newReport)
        // });

        // if (!res.ok) {
        //     throw new Error("提交报告失败：" + res.status + ": " + res.statusText);
        // }

        reports.value.unshift({
            ...newReport,
            id: Date.now(),
            generateTime: formatDateTime(new Date()),
            status: 'generated',
            summary: newReport.content.slice(0, 36),
        });
        toast('报告生成成功');

        reportForm.value = { ...emptyReportForm };
    } catch (error) {
        console.error(error);
        toast('生成报告失败：' + error.message, 'bg-red-500');
    } finally {
        generatingReport.value = false;
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

const quitEditing = () => {
    reportForm.value = { ...emptyReportForm };
    currentReport.value = null;
    editReportModalVisible.value = false;
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

const editReport = (report) => {
    const newReport = { ...report };
    currentReport.value = newReport;
    reportForm.value = { ...newReport };
    editReportModalVisible.value = true;
};

const submitEdit = () => {
    const newReport = { ...reportForm.value };
    if (!validateReport(newReport, true)) {
        return;
    }

    submittingEdit.value = true;

    try {
        // const res = await fetch('/api/...', {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json'
        //     },
        //     body: JSON.stringify(newReport)
        // });

        // if (!res.ok) {
        //     throw new Error("提交修改失败：" + res.status + ": " + res.statusText);
        // }

        const targetIndex = reports.value.findIndex(report => report.id === newReport.id);

        if (targetIndex === -1) {
            throw new Error('未找到要修改的报告');
        }

        reports.value[targetIndex] = {
            ...reports.value[targetIndex],
            ...newReport,
            summary: newReport.content.slice(0, 36),
        };

        reportForm.value = { ...emptyReportForm };
        toast('报告修改成功');
    } catch (error) {
        console.error(error);
        toast('提交修改失败：' + error.message, 'bg-red-500');
    } finally {
        submittingEdit.value = false;
        editReportModalVisible.value = false;
        currentReport.value = null;
    }
};

const getType = (type) => {
    return reportTypeMap[type] || type;
};

const getStatus = (status) => {
    return reportStatusMap[status] || status;
};

</script>
