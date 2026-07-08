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
                            <p class="text-gray-600">总面积(㎡)</p>
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
                                            <h3 class="font-bold text-lg text-dark">土地编号</h3>
                                            <p class="text-sm text-gray-500">ID: {{ land.id }}</p>
                                        </div>
                                    </div>
                                    <div class="grid grid-cols-2 gap-4 mt-4">
                                        <div>
                                            <p class="text-sm text-gray-500">面积</p>
                                            <p class="font-semibold">{{ land.area }} ㎡</p>
                                        </div>
                                        <div>
                                            <p class="text-sm text-gray-500">土壤类型</p>
                                            <p class="font-semibold">{{ land.soilType }}</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="flex flex-col space-y-3">
                                    <button @click="getSuggestion(land.id)"
                                        class="inline-flex items-center justify-center rounded-lg border border-green-500 bg-green-500 px-6 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm">
                                        <i class="fa fa-magic mr-2"></i>生成种植建议
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

                <!-- 消息窗口 -->
                <div class="bg-white rounded-2xl shadow-lg p-8" ref="messageContainer">
                    <h2 class="text-2xl font-bold text-dark mb-6 flex items-center">
                        <i class="fa fa-comments text-primary mr-3"></i>与技术顾问沟通
                    </h2>

                    <!-- 土地选择 -->
                    <div class="mb-6">
                        <label class="block text-gray-700 mb-3 font-medium">选择土地查看相关消息</label>
                        <select v-model="currentLandId"
                            class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all">
                            <option value="">请选择土地...</option>
                            <option v-for="land in lands" :key="land.id" :value="land.id">
                                {{ land.id }} - {{ land.area }}㎡ - {{ land.soilType }}
                            </option>
                        </select>
                    </div>

                    <!-- 消息列表 -->
                    <div class="mb-6">
                        <div
                            class="space-y-4 max-h-[300px] overflow-y-auto p-4 border border-gray-100 rounded-lg scrollbar-thin">
                            <div v-if="!currentLandId">
                                <div class="text-center py-8 text-gray-400">
                                    <i class="fa fa-commenting-o text-3xl mb-4"></i>
                                    <p>选择土地后显示沟通记录</p>
                                </div>
                            </div>
                            <div v-else-if="messages.length === 0">
                                <div class="text-center py-8 text-gray-400">
                                    <i class="fa fa-commenting-o text-3xl mb-4"></i>
                                    <p>暂无沟通记录</p>
                                    <p class="text-sm mt-2">开始与技术顾问交流吧</p>
                                </div>
                            </div>
                            <div v-else class="space-y-4">
                                <div v-for="msg in messages" :key="msg.id" class="p-4 rounded-lg" :class="{
                                    'message-owner': msg.sender === 'owner',
                                    'message-advisor': msg.sender === 'advisor',
                                    'bg-gray-100': msg.sender === 'system'
                                }">
                                    <div class="flex justify-between items-start mb-2">
                                        <div class="flex items-center">
                                            <div class="w-8 h-8 rounded-full flex items-center justify-center mr-3"
                                                :class="{
                                                    'bg-blue-100': msg.sender === 'owner',
                                                    'bg-primary/10': msg.sender === 'advisor',
                                                    'bg-gray-200': msg.sender === 'system'
                                                }">
                                                <i class="fa" :class="{
                                                    'fa-user text-blue-500': msg.sender === 'owner',
                                                    'fa-user-md text-primary': msg.sender === 'advisor',
                                                    'fa-info-circle text-gray-500': msg.sender === 'system'
                                                }"></i>
                                            </div>
                                            <span class="font-semibold">
                                                {{ msg.sender === 'owner' ? '农场主（我）' : msg.sender === 'advisor' ? '技术顾问'
                                                    :
                                                    '系统消息' }}
                                            </span>
                                        </div>
                                        <span class="text-sm text-gray-500">{{ new
                                            Date(msg.createdAt).toLocaleString('zh-CN')
                                        }}</span>
                                    </div>
                                    <div class="text-gray-700 ml-11">{{ msg.text }}</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 发送消息 -->
                    <div class="bg-light rounded-xl p-6">
                        <h3 class="font-bold text-dark mb-4">发送消息给技术顾问</h3>
                        <div class="flex gap-4">
                            <div class="flex-1">
                                <input v-model="messageText" :disabled="!currentLandId || sendingMessage" @keydown.enter.prevent="sendMessage"
                                    class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all"
                                    placeholder="输入消息内容...">
                            </div>
                            <button @click="sendMessage"
                                class="inline-flex items-center justify-center rounded-lg border border-green-500 bg-green-500 px-8 py-3 font-semibold text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm disabled:cursor-not-allowed disabled:opacity-60 whitespace-nowrap"
                                :disabled="!currentLandId || !messageText.trim() || sendingMessage">
                                <i class="fa fa-send mr-2"></i>{{ sendingMessage ? '发送中...' : '发送' }}
                            </button>
                        </div>
                    </div>

                    <!-- 历史方案 -->
                    <div class="mt-8">
                        <h3 class="text-xl font-bold text-dark mb-4 flex items-center">
                            <i class="fa fa-history text-primary mr-3"></i>历史技术方案
                        </h3>
                        <div v-if="!currentLandId" class="bg-light rounded-xl p-6">
                            <div class="text-center py-8 text-gray-400">
                                <i class="fa fa-file-text-o text-3xl mb-4"></i>
                                <p>选择土地后显示历史技术方案</p>
                            </div>
                        </div>
                        <div v-else>
                            <div v-if="loadingAdvice" class="text-center py-8">
                                <div
                                    class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-primary">
                                </div>
                                <p class="text-gray-500 mt-4">正在加载技术方案...</p>
                            </div>
                            <div v-else-if="errorAdvice" class="text-center py-8 text-red-500">
                                <i class="fa fa-exclamation-triangle text-3xl mb-4"></i>
                                <p>加载技术方案失败，请稍后重试</p>
                            </div>
                            <div v-else-if="adviceHistory.length === 0" class="text-center py-8 text-gray-400">
                                <i class="fa fa-file-text-o text-3xl mb-4"></i>
                                <p>暂无历史技术方案</p>
                            </div>
                            <div v-else>
                                <div v-for="advice in adviceHistory" :key="advice.id">
                                    <div class="flex justify-between items-start mb-3">
                                        <div>
                                            <span class="font-semibold text-dark">{{ advice.advisorName }}</span>
                                            <span class="text-sm text-gray-500 ml-3">顾问</span>
                                        </div>
                                        <span class="text-sm text-gray-500">{{ new
                                            Date(advice.createdAt).toLocaleString('zh-CN') }}</span>
                                    </div>
                                    <div
                                        class="text-gray-700 leading-relaxed whitespace-pre-wrap bg-light/50 p-4 rounded">
                                        {{ advice.content }}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- 新增土地弹窗 -->
    <div v-if="addLandModalVisible"
        class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center p-4 z-50"
        @click.self="closeAddLandModal">
        <div class="bg-white rounded-xl w-full max-w-lg overflow-hidden shadow-xl">
            <div class="p-6 border-b border-gray-200">
                <div class="flex items-center justify-between">
                    <h3 class="text-lg font-bold text-dark flex items-center">
                        <i class="fa fa-plus-circle text-green-500 mr-3"></i>新增土地
                    </h3>
                    <button type="button" class="text-gray-400 hover:text-gray-500" @click="closeAddLandModal">
                        <i class="fa fa-times"></i>
                    </button>
                </div>
            </div>
            <div class="p-6">
                <form @submit.prevent="createLand" class="space-y-5">
                    <div>
                        <label class="block text-gray-700 mb-2 font-medium">土地ID</label>
                        <input v-model="form.landId" required
                            class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                    </div>

                    <div>
                        <label class="block text-gray-700 mb-2 font-medium">面积（平方米）</label>
                        <input v-model.number="form.area" placeholder="请输入土地面积" type="number" step="0.01" required
                            class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                    </div>

                    <div>
                        <label class="block text-gray-700 mb-2 font-medium">土壤类型</label>
                        <input v-model="form.soilType" placeholder="如：红壤、黑土等" required
                            class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all">
                    </div>

                    <div>
                        <label class="block text-gray-700 mb-2 font-medium">附件上传</label>
                        <div
                            class="border-2 border-dashed border-gray-300 rounded-lg p-5 text-center transition-colors hover:border-green-500">
                            <input type="file" name="attachment" id="attachment" class="hidden"
                                @change="handleAttachmentChange">
                            <label for="attachment" class="cursor-pointer">
                                <i class="fa fa-cloud-upload text-3xl text-gray-400 mb-3"></i>
                                <p class="text-gray-500">点击上传土地相关文件</p>
                                <p class="text-sm text-gray-400 mt-2">支持图片、PDF、文档等格式</p>
                                <p v-if="form.attachment" class="text-sm text-green-600 mt-2">
                                    已选择：{{ form.attachment.name }}
                                </p>
                            </label>
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
                            {{ submitting ? '创建中...' : '创建' }}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- 建议弹窗 -->
    <div v-if="suggestionModalVisible"
        class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center p-4 z-50"
        @click.self="suggestionModalVisible = false">
        <div class="bg-white rounded-xl max-w-lg w-full max-h-[80vh] overflow-hidden shadow-xl">
            <div class="bg-green-500 text-white p-6">
                <h3 class="text-xl font-bold flex items-center">
                    <i class="fa fa-lightbulb-o mr-3"></i>种植建议
                </h3>
            </div>
            <div class="p-6 overflow-y-auto max-h-[60vh]">
                <div class="text-gray-700 leading-relaxed">
                    <div v-if="suggestionLoading" class="text-gray-500">
                        正在生成建议...
                    </div>

                    <div v-else-if="suggestionError" class="text-red-500">
                        {{ suggestionError }}
                    </div>

                    <div v-else-if="!suggestionContent" class="text-gray-500">
                        暂无可用建议
                    </div>

                    <div v-else class="text-gray-700 leading-relaxed whitespace-pre-wrap">
                        {{ suggestionContent }}
                    </div>
                </div>
            </div>
            <div class="border-t p-6 flex justify-end">
                <button @click="suggestionModalVisible = false"
                    class="w-1/3 rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm">
                    关闭
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { toast } from '../utils/toast';

const form = ref({
    landId: '',
    area: '',
    soilType: '',
    attachment: null
});

const CURRENT_SENDER = 'owner';

// 开关
const loading = ref(false);
const loadingAdvice = ref(false);
const success = ref(true);
const errorAdvice = ref(false);
const submitting = ref(false);
const suggestionModalVisible = ref(false);
const addLandModalVisible = ref(false);
const suggestionLoading = ref(false);
const suggestionError = ref('');
const sendingMessage = ref(false);

const suggestionContent = ref('');
const adviceHistory = ref([]);

const lands = ref([]);
const totalLands = computed(() => lands.value.length);
const totalArea = computed(() => lands.value.reduce((sum, land) => sum + Number(land.area), 0));
const soilTypesCount = computed(() => new Set(lands.value.map(land => land.soilType)).size);

const messages = ref([]);
const messageContainer = ref(null);
const messageText = ref('');

const currentLandId = ref(null);

const resetLandForm = () => {
    form.value.landId = '';
    form.value.area = '';
    form.value.soilType = '';
    form.value.attachment = null;
};

const openAddLandModal = () => {
    addLandModalVisible.value = true;
};

const closeAddLandModal = () => {
    if (submitting.value) {
        return;
    }

    resetLandForm();
    addLandModalVisible.value = false;
};

watch(messages, () => {
    if (messageContainer.value) {
        messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }
})

watch(currentLandId, (newId) => {
    messages.value = [];
    adviceHistory.value = [];

    if (!newId) {
        return;
    }

    loadMessages(newId);
    loadAdvice(newId);
});

// 添加 watch 触发加载新的土地数据，修复 createLand() 表单失效的问题，修复 messageContainer 绑定错误的问题

onMounted(() => {
    loadLands();
});

// 加载土地数据
const loadLands = async () => {
    loading.value = true;
    success.value = false;
    try {
        // 获取土地列表
        //! 这个 api 很存疑，应该是要获取当前用户的土地列表，而不是所有土地
        const res = await fetch(`/api/lands/`);
        if (!res.ok) {
            throw new Error(res.status + res.statusText);
        }
        // 检查土地列表长度是否为空
        const data = await res.json();
        if (data.length === 0) {
            toast('您还没有添加任何土地，快去创建吧！');
        }
        lands.value = data;
        // 更新数据
        success.value = true;
    } catch (error) {
        console.error('加载土地数据失败:', error.status);
        toast('加载土地数据失败，请稍后再试');
        success.value = false;
    } finally {
        loading.value = false;
    }
};
// 更新 与技术顾问沟通

// 新增土地 表单的提交
const createLand = async () => {
    // 设置 api，提交
    if (submitting.value) {
        return;
    }
    submitting.value = true;

    const data = new FormData();
    data.append('landId', form.value.landId);
    data.append('area', form.value.area);
    data.append('soilType', form.value.soilType);

    if (form.value.attachment) {
        data.append('attachment', form.value.attachment);
    }

    try {
        const res = await fetch("/api/lands", {
            method: 'POST',
            body: data
        });
        if (!res.ok) {
            throw new Error("土地添加失败！");
        }
        toast("土地添加成功！");
        // 刷新数据
        await loadLands();

        resetLandForm();
        addLandModalVisible.value = false;
    } catch (error) {
        console.error(error);
        toast(error.message || "土地添加失败");
    } finally {
        submitting.value = false;
    }
};

// 加载消息
const loadMessages = async (landId) => {
    try {
        const res = await fetch(`/api/advice/messages/${landId}`);
        if (!res.ok) {
            throw new Error("消息加载失败");
        }
        messages.value = await res.json();
    } catch (error) {
        console.error(error);
    }
}
// 加载技术方案
const loadAdvice = async (landId) => {
    loadingAdvice.value = true;
    errorAdvice.value = false;
    try {
        const res = await fetch(`/api/advice/land/${landId}`);
        if (!res.ok) {
            throw new Error('加载建议失败');
        }
        const data = await res.json();

        if (data.length === 0) {
            adviceHistory.value = [];
        } else {
            adviceHistory.value = data;
        }
    } catch (error) {
        console.error('加载方案失败：', error);
        errorAdvice.value = true;
    } finally {
        loadingAdvice.value = false;
    }
};
// 选择土地后更新网页区域？可以直接动态绑定吧？

// 获取土地种植建议
const getSuggestion = async (landId) => {
    // 设置可见性
    suggestionModalVisible.value = true;
    suggestionLoading.value = true;
    suggestionError.value = '';
    suggestionContent.value = '';

    // 网络请求
    try {
        const res = await fetch(`/api/lands/${landId}/suggestion`);
        if (!res.ok) {
            throw new Error('获取建议失败');
        }

        const text = await res.text();
        suggestionContent.value = text;
    } catch (error) {
        suggestionError.value = '获取建议失败，请稍后重试';
        console.error('获取建议失败:', error);
    } finally {
        suggestionLoading.value = false;
    }
};

const sendMessage = async () => {
    const text = messageText.value.trim();


    if (!currentLandId.value || !text || sendingMessage.value) {
        return;
    }

    try {
        sendingMessage.value = true;
        const res = await fetch('/api/advice/message', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                landId: Number(currentLandId.value),
                sender: CURRENT_SENDER,
                text
            })
        });

        if (!res.ok) {
            throw new Error('发送失败');
        }

        messageText.value = '';
        loadMessages(currentLandId.value);
    } catch (error) {
        console.error(error);
        toast('发送失败，请重试', 'bg-red-500')
    } finally {
        sendingMessage.value = false;
    }

};

// 提交附件
const handleAttachmentChange = (event) => {
    const file = event.target.files?.[0] || null;
    form.value.attachment = file;
}
</script>
