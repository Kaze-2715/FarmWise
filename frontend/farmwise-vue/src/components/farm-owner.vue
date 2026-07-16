<template>
    <main class="mx-auto w-full max-w-6xl space-y-8">
        <div class="space-y-8">
            <!-- 土地列表和消息窗口 -->
            <div class="space-y-8">
                <div>
                    <div class="mb-6 flex items-center justify-between">
                        <h2 class="text-2xl font-bold text-dark flex items-center">
                            <i class="fa fa-map-marker text-primary mr-3"></i>土地列表
                        </h2>
                        <button type="button"
                            class="inline-flex items-center rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm"
                            @click="openAddLandModal">
                            <i class="fa fa-plus mr-2"></i>新增土地
                        </button>
                    </div>

                    <!-- 统计卡片 -->
                    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
                        <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
                            <div class="text-3xl font-bold text-green-600 mb-2">{{ totalLands }}</div>
                            <p class="text-gray-600">土地总数</p>
                        </div>
                        <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
                            <div class="text-3xl font-bold text-green-600 mb-2">{{ totalArea }}</div>
                            <p class="text-gray-600">总面积(亩)</p>
                        </div>
                        <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
                            <div class="text-3xl font-bold text-green-600 mb-2">{{ soilTypesCount }}</div>
                            <p class="text-gray-600">土壤类型</p>
                        </div>
                    </div>

                    <!-- 土地列表 -->
                    <div id="lands" class="space-y-4">
                        <div v-for="land in lands" :key="land.id"
                            class="bg-white rounded-xl p-6 shadow-sm border border-gray-100 transition-all duration-200 hover:-translate-y-0.5 hover:shadow">
                            <div class="flex flex-col md:flex-row md:items-center justify-between">
                                <div class="mb-4 md:mb-0">
                                    <div class="flex items-center mb-3">
                                        <div
                                            class="w-12 h-12 bg-green-50 rounded-lg flex items-center justify-center mr-4">
                                            <i class="fa fa-map text-green-600 text-xl"></i>
                                        </div>
                                        <div>
                                            <h3 class="font-bold text-lg text-dark">{{ land.name }}</h3>
                                            <p class="text-sm text-gray-500">ID: {{ land.id }}</p>
                                        </div>
                                    </div>
                                    <div class="grid grid-cols-2 gap-4 mt-4">
                                        <div>
                                            <p class="text-sm text-gray-500">面积</p>
                                            <p class="font-semibold">{{ land.area }} 亩</p>
                                        </div>
                                        <div>
                                            <p class="text-sm text-gray-500">土壤类型</p>
                                            <p class="font-semibold">{{ land.type }}</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="flex flex-col space-y-3">
                                    <button type="button" @click="openEditLandModal(land)"
                                        class="inline-flex items-center justify-center rounded-lg border border-green-300 px-6 py-2 text-sm font-medium text-green-600 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-green-500 hover:shadow active:translate-y-0 active:shadow-sm">
                                        <i class="fa fa-pencil mr-2"></i>修改土地信息
                                    </button>
                                    <button type="button" @click="openDeleteLandModal(land)"
                                        class="inline-flex items-center justify-center rounded-lg border border-red-300 px-6 py-2 text-sm font-medium text-red-500 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-red-500 hover:shadow active:translate-y-0 active:shadow-sm">
                                        <i class="fa fa-trash mr-2"></i>删除土地
                                    </button>

                                    <a v-if="land.attachmentPath" :href="land.attachmentPath" target="_blank"
                                        class="inline-flex items-center justify-center rounded-lg border border-gray-300 px-6 py-2 text-sm font-medium text-gray-700 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow active:translate-y-0 active:shadow-sm">
                                        <i class="fa fa-paperclip mr-2"></i>查看附件
                                    </a>
                                    <span v-else class="text-gray-400 text-sm">无附件</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 加载状态 -->
                    <div v-if="loading || !success || (success && lands.length === 0)" class="text-center py-12">
                        <div class="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"
                            v-if="loading">
                        </div>
                        <p v-if="loading" class="text-gray-500 mt-4">加载土地数据中...</p>
                        <p v-else-if="success && (lands.length === 0)">
                            暂无土地数据 <br>
                            点击右上角“新增土地”创建您的第一块土地记录吧！
                        </p>
                        <p v-else-if="!success" class="text-red-500 mt-4">加载土地数据失败，请联系技术支持！</p>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- 新增/编辑土地弹窗 -->
    <div v-if="addLandModalVisible"
        class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center p-4 z-50"
        @click.self="closeAddLandModal">
        <div class="bg-white rounded-xl w-full max-w-2xl overflow-hidden shadow-xl">
            <div class="p-6 border-b border-gray-200">
                <div class="flex items-center justify-between">
                    <h3 class="text-lg font-bold text-dark flex items-center">
                        <i class="fa text-green-500 mr-3" :class="editingLandId ? 'fa-pencil' : 'fa-plus-circle'"></i>
                        {{ editingLandId ? '编辑土地' : '新增土地' }}
                    </h3>
                    <button type="button" class="text-gray-400 hover:text-gray-500" @click="closeAddLandModal">
                        <i class="fa fa-times"></i>
                    </button>
                </div>
            </div>
            <div class="p-6">
                <form @submit.prevent="submitLand" class="space-y-5">
                    <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
                        <div>
                            <label class="block text-gray-700 mb-2 font-medium">土地名称</label>
                            <input v-model="form.name" placeholder="如：东区1号水田" required
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                        </div>
                        <div>
                            <label class="block text-gray-700 mb-2 font-medium">土地类型</label>
                            <input v-model="form.type" placeholder="如：水田、旱地、温室" required
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                        </div>
                        <div>
                            <label class="block text-gray-700 mb-2 font-medium">面积（亩）</label>
                            <input v-model="form.area" placeholder="请输入土地面积" type="number" min="0" step="0.01" required
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                        </div>
                        <div>
                            <label class="block text-gray-700 mb-2 font-medium">当前作物</label>
                            <input v-model="form.crop" placeholder="未种植时可不填"
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                        </div>
                        <div>
                            <label class="block text-gray-700 mb-2 font-medium">使用状态</label>
                            <select v-model="form.status" required
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                                <option value="未启用">未启用</option>
                                <option value="正常种植">正常种植</option>
                                <option value="休耕">休耕</option>
                                <option value="异常">异常</option>
                            </select>
                        </div>
                        <div>
                            <label class="block text-gray-700 mb-2 font-medium">位置</label>
                            <input v-model="form.location" placeholder="如：农场东区" required
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                        </div>
                        <div>
                            <label class="block text-gray-700 mb-2 font-medium">经度</label>
                            <input v-model="form.longitude" type="number" step="any" required
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                        </div>
                        <div>
                            <label class="block text-gray-700 mb-2 font-medium">纬度</label>
                            <input v-model="form.latitude" type="number" step="any" required
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                        </div>
                    </div>

                    <div class="flex justify-end space-x-3 pt-2">
                        <button type="button"
                            class="w-1/3 rounded-lg border border-gray-500 bg-gray-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-gray-600 hover:shadow active:translate-y-0 active:shadow-sm"
                            @click="closeAddLandModal">
                            取消
                        </button>
                        <button type="submit" :disabled="submitting"
                            class="w-1/3 rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm disabled:cursor-not-allowed disabled:opacity-60">
                            {{ submitting ? '保存中...' : editingLandId ? '保存修改' : '创建' }}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- 删除土地确认弹窗 -->
    <div v-if="deleteLandModalVisible && pendingDeleteLand"
        class="fixed inset-0 bg-black/40 flex items-center justify-center p-4 z-50"
        @click.self="closeDeleteLandModal">
        <div class="bg-white rounded-xl w-full max-w-md p-6 shadow-xl">
            <h3 class="text-lg font-bold text-gray-800">确认删除</h3>
            <p class="mt-3 text-gray-600">确认删除土地“{{ pendingDeleteLand.name }}”吗？</p>
            <p v-if="deleteLandError" class="mt-3 text-sm text-red-500">{{ deleteLandError }}</p>
            <div class="mt-6 flex justify-end gap-3">
                <button type="button"
                    class="rounded-lg border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-50"
                    @click="closeDeleteLandModal">
                    取消
                </button>
                <button type="button"
                    class="rounded-lg bg-red-500 px-4 py-2 text-white hover:bg-red-600"
                    @click="confirmDeleteLand">
                    删除
                </button>
            </div>
        </div>
    </div>

</template>

<script setup>
import { computed, ref } from 'vue';
import { toast } from '../utils/toast';
import { useFarmStore } from '../composables/useFarmStore';

const { lands, addLand, updateLand, deleteLand } = useFarmStore();

const form = ref({
    name: '',
    type: '',
    area: '',
    crop: '',
    status: '未启用',
    location: '',
    longitude: '',
    latitude: ''
});

// 开关
const loading = ref(false);
const success = ref(true);
const submitting = ref(false);
const addLandModalVisible = ref(false);
const editingLandId = ref(null);
const deleteLandModalVisible = ref(false);
const pendingDeleteLand = ref(null);
const deleteLandError = ref('');

const totalLands = computed(() => lands.value.length);
const totalArea = computed(() => lands.value.reduce((sum, land) => sum + Number(land.area), 0));
const soilTypesCount = computed(() => new Set(lands.value.map(land => land.type)).size);

const resetLandForm = () => {
    form.value = {
        name: '',
        type: '',
        area: '',
        crop: '',
        status: '未启用',
        location: '',
        longitude: '',
        latitude: ''
    };
};

const openAddLandModal = () => {
    editingLandId.value = null;
    resetLandForm();
    addLandModalVisible.value = true;
};

const openEditLandModal = (land) => {
    editingLandId.value = land.id;
    form.value = {
        name: land.name,
        type: land.type,
        area: land.area,
        crop: land.crop ?? '',
        status: land.status,
        location: land.location,
        longitude: land.longitude,
        latitude: land.latitude
    };
    addLandModalVisible.value = true;
};

const closeAddLandModal = () => {
    if (submitting.value) {
        return;
    }

    resetLandForm();
    editingLandId.value = null;
    addLandModalVisible.value = false;
};

const openDeleteLandModal = (land) => {
    pendingDeleteLand.value = land;
    deleteLandError.value = '';
    deleteLandModalVisible.value = true;
};

const closeDeleteLandModal = () => {
    pendingDeleteLand.value = null;
    deleteLandError.value = '';
    deleteLandModalVisible.value = false;
};

const confirmDeleteLand = () => {
    if (!pendingDeleteLand.value) {
        return;
    }

    try {
        deleteLand(pendingDeleteLand.value.id);
        toast('土地删除成功！');
        closeDeleteLandModal();
    } catch (error) {
        deleteLandError.value = error.message || '土地删除失败';
    }
};

const submitLand = () => {
    if (submitting.value) {
        return;
    }
    submitting.value = true;

    try {
        if (editingLandId.value) {
            updateLand(editingLandId.value, form.value);
            toast('土地修改成功！');
        } else {
            addLand(form.value);
            toast('土地添加成功！');
        }
        resetLandForm();
        editingLandId.value = null;
        addLandModalVisible.value = false;
    } catch (error) {
        console.error(error);
        toast(error.message || '土地保存失败');
    } finally {
        submitting.value = false;
    }
};

</script>
