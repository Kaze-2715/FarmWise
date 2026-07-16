<template>
  <main class="container mx-auto px-4 py-8">
    <div class="grid grid-cols-1 gap-8 lg:grid-cols-3">
      <aside class="lg:col-span-1">
        <div class="rounded-2xl bg-white p-8 shadow-lg">
          <h2 class="mb-6 flex items-center text-2xl font-bold text-dark">
            <i class="fa fa-map-marker mr-3 text-primary"></i>AI 顾问上下文
          </h2>

          <div class="mb-6">
            <label class="mb-3 block font-medium text-gray-700">选择咨询地块</label>
            <select v-model="currentLandId"
              class="w-full rounded-lg border border-gray-300 px-4 py-3 outline-none transition-all focus:border-primary focus:ring-2 focus:ring-primary/20">
              <option v-if="lands.length === 0" value="" disabled>暂无地块</option>
              <option v-for="land in lands" :key="land.id" :value="land.id">
                {{ land.name }} | {{ land.area }} ㎡ | {{ land.type }}
              </option>
            </select>
          </div>

          <div v-if="currentLand" class="space-y-4 rounded-xl bg-light p-6">
            <div>
              <h3 class="text-lg font-bold text-dark">{{ currentLand.name }}</h3>
              <p class="text-sm text-gray-500">{{ currentLand.id }}</p>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-sm text-gray-500">种植作物</p>
                <p class="font-semibold">{{ currentLand.crop || '暂无' }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">土地类型</p>
                <p class="font-semibold">{{ currentLand.type }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">设备</p>
                <p class="font-semibold">{{ currentAiContext?.devices.length ?? 0 }} 台</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">最新指标</p>
                <p class="font-semibold">{{ currentAiContext?.sensorReadings.length ?? 0 }} 项</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">活跃预警</p>
                <p class="font-semibold">{{ currentAiContext?.activeAlerts.length ?? 0 }} 条</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">进行中任务</p>
                <p class="font-semibold">{{ currentAiContext?.activeTasks.length ?? 0 }} 项</p>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <section class="lg:col-span-2">
        <div class="min-h-full rounded-2xl bg-white p-8 shadow-lg">
          <div class="mb-8 flex items-start justify-between gap-4">
            <div>
              <h2 class="flex items-center text-2xl font-bold text-dark">
                <i class="fa fa-comments mr-3 text-primary"></i>AI 技术顾问
              </h2>
              <p class="mt-2 text-sm text-gray-500">
                回答基于当前地块的设备、环境、灌溉、预警和任务上下文。
              </p>
            </div>
            <span v-if="currentConversation"
              class="rounded-full bg-green-50 px-3 py-1 text-xs font-medium text-green-700">
              {{ currentConversation.status === 'active' ? '进行中' : '已归档' }}
            </span>
          </div>

          <div v-if="messages.length === 0"
            class="rounded-xl border border-dashed border-gray-200 py-16 text-center text-gray-500">
            <i class="fa fa-comment-o mb-3 text-4xl text-gray-300"></i>
            <p>当前地块暂无 AI 对话</p>
          </div>

          <div v-else class="space-y-5">
            <article v-for="message in messages" :key="message.id" class="rounded-xl border p-5" :class="message.role === 'assistant'
              ? 'border-green-100 bg-green-50/40'
              : 'border-blue-100 bg-blue-50/40'">
              <div class="mb-3 flex items-center justify-between gap-4">
                <div class="flex items-center gap-3">
                  <div class="flex h-8 w-8 items-center justify-center rounded-full"
                    :class="message.role === 'assistant' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'">
                    <i class="fa" :class="message.role === 'assistant' ? 'fa-leaf' : 'fa-user'"></i>
                  </div>
                  <span class="font-semibold text-gray-800">
                    {{ message.role === 'assistant' ? 'AI 技术顾问' : '农场主' }}
                  </span>
                </div>
                <time class="text-xs text-gray-500">{{ formatDate(message.createdAt) }}</time>
              </div>

              <p class="whitespace-pre-wrap leading-relaxed text-gray-700">{{ message.content }}</p>

              <div v-if="message.references.length > 0" class="mt-5 border-t border-gray-200 pt-4">
                <h3 class="mb-3 text-sm font-semibold text-gray-700">参考数据</h3>
                <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
                  <div v-for="reference in message.references"
                    :key="`${reference.type}:${reference.sourceId}:${reference.label}`"
                    class="rounded-lg bg-white p-3 text-sm shadow-sm">
                    <p class="text-gray-500">{{ reference.label }}</p>
                    <p class="mt-1 font-semibold text-gray-800">
                      {{ reference.value }}{{ reference.unit }}
                    </p>
                  </div>
                </div>
              </div>

              <div v-if="message.taskDraft" class="mt-5 rounded-xl border border-orange-200 bg-orange-50 p-4">
                <div class="mb-3 flex items-center justify-between gap-3">
                  <h3 class="font-semibold text-orange-900">农事任务草稿</h3>
                  <span class="rounded-full bg-orange-100 px-2 py-1 text-xs text-orange-700">
                    {{ message.taskDraft.priority }}
                  </span>
                </div>
                <p class="font-medium text-gray-800">{{ message.taskDraft.title }}</p>
                <p class="mt-2 text-sm leading-relaxed text-gray-600">{{ message.taskDraft.description }}</p>
                <div v-if="taskDraftForms[message.id]" class="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
                  <label class="text-sm text-gray-700">
                    <span class="mb-1 block">负责人</span>
                    <input v-model="taskDraftForms[message.id].assignee" type="text" placeholder="请输入负责人"
                      :disabled="createdTaskMessageIds.has(message.id)"
                      class="w-full rounded-lg border border-orange-200 bg-white px-3 py-2 outline-none focus:border-orange-400">
                  </label>
                  <label class="text-sm text-gray-700">
                    <span class="mb-1 block">截止时间</span>
                    <input v-model="taskDraftForms[message.id].deadline" type="datetime-local"
                      :disabled="createdTaskMessageIds.has(message.id)"
                      class="w-full rounded-lg border border-orange-200 bg-white px-3 py-2 outline-none focus:border-orange-400">
                  </label>
                </div>
                <div class="mt-4 flex items-center justify-between gap-3">
                  <p class="text-xs text-orange-700">
                    {{ createdTaskMessageIds.has(message.id) ? '已创建为正式农事任务。' : '填写执行信息并确认后创建正式任务。' }}
                  </p>
                  <button type="button" :disabled="isTaskDraftSubmitDisabled(message)"
                    @click="confirmTaskDraft(message)"
                    class="rounded-lg bg-orange-500 px-4 py-2 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-50">
                    {{ creatingTaskMessageId === message.id ? '创建中...' : createdTaskMessageIds.has(message.id) ? '已创建'
                    : '确认创建' }}
                  </button>
                </div>
              </div>
            </article>
          </div>

          <div class="mt-8 rounded-xl bg-light p-5 text-sm text-gray-500">
            <div v-if="!currentConversation">
              当前地块暂无对话
            </div>
            <div v-else>
              输入消息询问 AI 技术顾问
            </div>
            <input type="text" placeholder="请输入内容" :disabled="!currentLandId" v-model="messageText"
              @keyup.enter.prevent="sendMessage">
            <button type="button" :disabled="!currentLandId || !messageText.trim()" @click="sendMessage">发送</button>
          </div>
        </div>
      </section>
    </div>
  </main>
</template>

<script setup>
import { computed, ref, reactive, watch } from 'vue';
import { useFarmStore } from '../composables/useFarmStore';
import { generateMockAiResponse } from '../utils/mock-ai-agent';
import { buildAiContext } from '../utils/ai-context';

const {
  lands,
  devices,
  sensorReadings,
  environmentThresholds,
  irrigationConfigs,
  irrigationRecords,
  alerts,
  farmTasks,
  plans,
  aiConversations,
  appendAiMessage,
  createAiConversation,
  createFarmTaskFromAiDraft
} = useFarmStore();

const defaultLandId = aiConversations.value[0]?.landId ?? lands.value[0]?.id ?? '';
const currentLandId = ref(defaultLandId);
const messageText = ref('');
const taskDraftForms = reactive({});
const creatingTaskMessageId = ref('');

const createdTaskMessageIds = computed(() => new Set(
  farmTasks.value
    .filter(task => task.sourceType === 'aiMessage')
    .map(task => task.sourceId)
));

const currentLand = computed(() =>
  lands.value.find(land => land.id === currentLandId.value) ?? null
);

const currentConversation = computed(() =>
  aiConversations.value.find(conversation =>
    conversation.landId === currentLandId.value && conversation.status === 'active'
  ) ?? null
);

const messages = computed(() => currentConversation.value?.messages ?? []);

const currentAiContext = computed(() => buildAiContext({
  landId: currentLandId.value,
  lands: lands.value,
  devices: devices.value,
  sensorReadings: sensorReadings.value,
  environmentThresholds: environmentThresholds.value,
  irrigationConfigs: irrigationConfigs.value,
  irrigationRecords: irrigationRecords.value,
  alerts: alerts.value,
  farmTasks: farmTasks.value,
  plans: plans.value
}));

watch(messages, (currentMessages) => {
  currentMessages.forEach(message => {
    if (!message.taskDraft) {
      return;
    }
    if (taskDraftForms[message.id]) {
      return;
    }
    taskDraftForms[message.id] = {
      assignee: message.taskDraft.assignee ?? '',
      deadline: message.taskDraft.deadline ?? ''
    };
  });
}, { immediate: true, deep: true });

const formatDate = value => {
  if (!value) return '';
  return new Date(value).toLocaleString('zh-CN');
};

const sendMessage = () => {
  const text = messageText.value.trim();
  if (!text) {
    alert('发送内容不能为空');
    return;
  }

  if (!currentLand.value || !currentAiContext.value) {
    alert('未选择地块、上下文信息不足');
    return;
  }

  try {
    const response = generateMockAiResponse({
      question: text,
      context: currentAiContext.value
    });

    let conversation = currentConversation.value ?? null;

    if (!conversation) {
      conversation = createAiConversation({
        landId: currentLandId.value,
        title: text
      });
    }

    appendAiMessage(conversation.id,
      {
        role: 'user',
        content: text
      });

    appendAiMessage(conversation.id,
      {
        role: 'assistant',
        content: response.content,
        references: response.references,
        taskDraft: response.taskDraft
      }
    );
    messageText.value = '';
  } catch (error) {
    console.error(error);
    alert(error.message);
  }
};

const isTaskDraftSubmitDisabled = (message) => {
  const form = taskDraftForms[message.id];
  return !form ||
    !form.assignee.trim() ||
    !form.deadline ||
    creatingTaskMessageId.value === message.id ||
    createdTaskMessageIds.value.has(message.id);
};

const confirmTaskDraft = (message) => {
  const form = taskDraftForms[message.id];

  if (!currentConversation.value || !form || !message.taskDraft) {
    alert("任务草稿不存在");
    return;
  }

  if (!form.deadline || !form.deadline.trim()) {
    alert("日期为空!");
    return;
  }

  const deadlineDate = new Date(form.deadline);

  if (Number.isNaN(deadlineDate.getTime())) {
    alert("日期无效");
    return;
  }

  const validDeadline = deadlineDate.toISOString();

  creatingTaskMessageId.value = message.id;

  try {
    createFarmTaskFromAiDraft({
      conversationId: currentConversation.value.id,
      messageId: message.id,
      assignee: form.assignee,
      deadline: validDeadline
    });
  } catch (error) {
    alert("创建失败，原因是：" + error.message);
    console.error(error);
  } finally {
    creatingTaskMessageId.value = '';
  }
};
</script>
