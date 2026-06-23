<template>
    <main class="container mx-auto px-4 py-8">
        <!-- 页面标题 -->
        <div class="mb-10 text-center">
            <h1 class="text-4xl font-bold text-dark mb-4">技术方案管理</h1>
            <p class="text-gray-600 max-w-2xl mx-auto">
                为农场主提供专业种植建议，通过即时沟通优化农业生产方案
            </p>
            <div class="w-24 h-1 bg-primary mx-auto mt-6 rounded-full"></div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- 左侧：土地选择和详情 -->
            <div class="lg:col-span-1">
                <div class="bg-white rounded-2xl shadow-lg p-8">
                    <h2 class="text-2xl font-bold text-dark mb-6 flex items-center">
                        <i class="fa fa-map-marker text-primary mr-3"></i>选择土地
                    </h2>

                    <div class="mb-6">
                        <label class="block text-gray-700 mb-3 font-medium">选择要服务的土地</label>
                        <select v-model="currentLandId"
                            class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all">
                            <option value="">请选择土地...</option>
                            <option v-for="land in lands" :key="land.id" :value="land.id">
                                {{ land.landId }} | {{ land.area }} ㎡ | {{ land.soilType }}
                            </option>
                        </select>
                    </div>

                    <!-- 土地详情 -->
                    <div id="landDetail" class="bg-light rounded-xl p-6 mb-8">
                        <div v-if="!currentLand" class="text-center py-8">
                            <i class="fa fa-map-o text-4xl text-gray-300 mb-4"></i>
                            <p class="text-gray-500">请从左侧选择一块土地</p>
                        </div>

                        <div v-else class="space-y-4">
                            <div>
                                <h3 class="font-bold text-lg text-dark">
                                    {{ currentLand.landId }}
                                </h3>
                                <p class="text-sm text-gray-500">
                                    ID: {{ currentLand.id }}
                                </p>
                            </div>

                            <div class="grid grid-cols-2 gap-4">
                                <div>
                                    <p class="text-sm text-gray-500">土地面积</p>
                                    <p class="font-semibold">{{ currentLand.area }} ㎡</p>
                                </div>
                                <div>
                                    <p class="text-sm text-gray-500">土壤类型</p>
                                    <p class="font-semibold">{{ currentLand.soilType }}</p>
                                </div>
                            </div>

                            <a v-if="currentLand.attachmentPath" :href="currentLand.attachmentPath" target="_blank">
                                查看土地附件
                            </a>
                        </div>
                    </div>

                    <!-- 提交方案表单 -->
                    <form @submit.prevent="submitAdvice" class="bg-primary/5 rounded-xl p-6">
                        <h3 class="text-xl font-bold text-dark mb-4 flex items-center">
                            <i class="fa fa-edit text-primary mr-3"></i>提交技术方案
                        </h3>

                        <div class="mb-4">
                            <label class="block text-gray-700 mb-2 font-medium">顾问姓名</label>
                            <input v-model="adviceForm.advisorName" required
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all"
                                placeholder="请输入您的姓名">
                        </div>

                        <div class="mb-6">
                            <label class="block text-gray-700 mb-2 font-medium">方案内容</label>
                            <textarea v-model="adviceForm.content" required rows="6"
                                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all resize-none"
                                placeholder="请输入详细的技术方案和建议..."></textarea>
                        </div>

                        <button type="submit" :disabled="submittingAdvice"
                            class="w-full bg-primary hover:bg-secondary text-white font-semibold py-3 px-6 rounded-lg transition-all duration-300 transform hover:-translate-y-0.5 shadow-md hover:shadow-lg">
                            <i class="fa fa-paper-plane mr-2"></i>
                            {{ submittingAdvice ? '提交中' : '提交方案' }}
                        </button>
                    </form>
                </div>
            </div>

            <!-- 右侧：沟通记录 -->
            <div class="lg:col-span-2">
                <div class="bg-white rounded-2xl shadow-lg p-8 h-full">
                    <div class="flex justify-between items-center mb-8">
                        <h2 class="text-2xl font-bold text-dark flex items-center">
                            <i class="fa fa-comments text-primary mr-3"></i>沟通记录
                        </h2>
                        <div v-if="currentLand" class="text-sm text-gray-500" >
                            当前土地：<span class="font-semibold">{{ currentLand.landId }}</span>
                        </div>
                    </div>

                    <!-- 消息列表 -->
                    <div class="space-y-4 mb-8 max-h-[400px] overflow-y-auto p-4 border border-gray-100 rounded-lg scrollbar-thin">
                        <div v-if="!currentLand" class="text-center py-12">
                            选择土地后显示沟通记录
                        </div>

                        <div v-else-if="messages.length === 0" class="text-center py-12">
                            暂无沟通记录
                        </div>

                        <div v-else class="space-y-4">
                            <div v-for="msg in messages" :key="msg.id" class="p-4 rounded-lg" :class="{
                                'message-advisor': msg.sender === 'advisor',
                                'message-owner': msg.sender === 'owner'
                            }">
                                <div class="flex justify-between items-start mb-2">
                                    <div class="flex items-center">
                                        <div class="w-8 h-8 rounded-full flex items-center justify-center mr-3" :class="{
                                            'bg-primary/10': msg.sender === 'advisor',
                                            'bg-blue-100': msg.sender === 'owner'
                                        }">
                                            <i class="fa" :class="{
                                                'fa-user-md text-primary': msg.sender === 'advisor',
                                                'fa-user text-blue-500': msg.sender === 'owner'
                                            }"></i>
                                        </div>

                                        <span class="font-semibold">
                                            {{ msg.sender === 'advisor' ? '技术顾问' : '农场主' }}
                                        </span>
                                    </div>

                                    <span class="text-sm text-gray-500">
                                        {{ formatDate(msg.createdAt) }}
                                    </span>
                                </div>
                                <div class="text-gray-700 ml-11">
                                    {{ msg.text }}
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 发送消息 -->
                    <div class="bg-light rounded-xl p-6">
                        <h3 class="font-bold text-dark mb-4">发送新消息</h3>
                        <div class="flex gap-4">
                            <div class="flex-1">
                                <input v-model="messageText" :disabled="!currentLandId || sendingMessage" @keydown.enter.prevent="sendMessage" class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all" placeholder="输入消息内容...">
                            </div>
                            <button type="button" :disabled="!currentLandId || !messageText.trim() || sendingMessage" @click="sendMessage" class="bg-primary hover:bg-secondary text-white font-semibold py-3 px-8 rounded-lg transition-colors whitespace-nowrap">
                                <i class="fa fa-send mr-2"></i> {{ sendingMessage ? '发送中...' : '发送' }}
                            </button>
                        </div>
                        <div class="mt-4 text-sm text-gray-500 flex items-center">
                            <i class="fa fa-info-circle mr-2"></i>
                            消息将实时同步到农场主的控制台
                        </div>
                    </div>

                    <!-- 历史方案 -->
                    <div class="mt-8">
                        <h3 class="text-xl font-bold text-dark mb-4 flex items-center">
                            <i class="fa fa-history text-primary mr-3"></i>历史技术方案
                        </h3>
                        <div id="adviceHistory" class="bg-light rounded-xl p-6">
                            <div v-if="!currentLand">
                                选择土地后显示历史技术方案
                            </div>

                            <div v-else-if="adviceHistory.length === 0">
                                暂无历史方案记录
                            </div>

                            <div v-else class="space-y-4">
                                <div v-for="advice in adviceHistory" :key="advice.id"
                                    class="bg-white rounded-lg p-6 mb-4 border border-gray-100">
                                    <div class="flex justify-between items-start mb-3">
                                        <div>
                                            <span class="font-semibold text-dark">{{ advice.advisorName }}</span>
                                            <span class="text-sm text-gray-500 ml-3">顾问</span>
                                        </div>

                                        <span class="text-sm text-gray-500">
                                            {{ formatDate(advice.createdAt) }}
                                        </span>
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
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { toast } from "../utils/toast";

const adviceForm = ref({
    advisorName: '',
    content: ''
})

const submittingAdvice = ref(false);

const lands = ref([]);
const currentLandId = ref('');
const currentLand = computed(() => lands.value.find(land => land.id === currentLandId.value));
const messages = ref([]);
const adviceHistory = ref([]);
const messageText = ref('');
const sendingMessage = ref(false);

onMounted(() => {
    loadLands();
})

watch(currentLandId, (newId) => {
    messages.value = [];
    adviceHistory.value = [];

    if (!newId) {
        return;
    }

    loadMessages(newId);
    loadAdvice(newId);
})

const formatDate = (value) => {
    if (!value) {
        return '';
    }

    return new Date(value).toLocaleString('zh-CN');
}

const sendMessage = async () => {
    const text = messageText.value.trim();

    if (!currentLandId.value || !text || sendingMessage.value) {
        return;
    }

    sendingMessage.value = true;

    try {
        const res = await fetch('/api/advice/message', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                landId: Number(currentLandId.value),
                sender: 'advisor',
                text
            })
        });

        if (!res.ok) {
            throw new Error('发送消息失败');
        }

        messageText.value = '';
        await loadMessages(currentLandId.value);
    } catch (error) {
        console.error('发送消息失败', error);
        toast('发送消息失败', 'bg-red-500');
    } finally {
        sendingMessage.value = false;
    }
}

const loadMessages = async (landId) => {
    try {
        const res = await fetch(`/api/advice/messages/${landId}`);

        if (!res.ok) {
            throw new Error('加载消息失败');
        }

        messages.value = await res.json();
    } catch (error) {
        console.error('加载消息失败：', error);
    }
}

const loadAdvice = async (landId) => {
    try {
        const res = await fetch(`/api/advice/land/${landId}`);

        if (!res.ok) {
            throw new Error("加载技术方案失败");
        }

        adviceHistory.value = await res.json();
    } catch (error) {
        console.error('加载技术方案失败：', error);
    }
}

const loadLands = async () => {
    try {
        const res = await fetch('/api/lands');

        if (!res.ok) {
            throw new Error('加载土地失败');
        }

        lands.value = await res.json();

        if (lands.value.length > 0) {
            currentLandId.value = lands.value[0].id;
        }
    } catch (error) {
        console.error('加载土地失败', error);
    }
};

const submitAdvice = async () => {
    const advisorName = adviceForm.value.advisorName.trim();
    const content = adviceForm.value.content.trim();

    if (!currentLandId.value || !advisorName || !content || submittingAdvice.value) {
        return;
    }

    submittingAdvice.value = true;

    try {
        const res = await fetch('/api/advice', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                landId: Number(currentLandId.value),
                advisorName,
                content
            })
        });

        if (!res.ok) {
            throw new Error("提交技术方案失败");
        }

        adviceForm.value.content = '';

        toast('技术方案提交成功');

        await fetch('/api/advice/message', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                landId: Number(currentLandId.value),
                sender: 'advisor',
                text: `${advisorName} 提交了新的技术方案，请查看。`
            })
        });

        await loadAdvice(currentLandId.value);
        await loadMessages(currentLandId.value);
    } catch (error) {
        console.error("提交技术方案失败：", error);
        toast('提交技术方案失败', 'bg-red-500');
    } finally {
        submittingAdvice.value = false;
    }
}
</script>