<template>
  <main class="container mx-auto h-full min-h-0 px-4 py-2">
    <div class="grid h-full min-h-0 grid-cols-1 gap-8 lg:grid-cols-3">
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
                {{ land.name }} | {{ land.area }} 亩 | {{ getLandTypeLabel(land.landType) }}
              </option>
            </select>
          </div>

          <div class="mb-6">
            <label class="mb-3 block font-medium text-gray-700">选择对话</label>
            <select v-model="currentConversationId"
              :disabled="!currentLandId"
              class="w-full rounded-lg border border-gray-300 px-4 py-3 outline-none transition-all focus:border-primary focus:ring-2 focus:ring-primary/20 disabled:cursor-not-allowed disabled:bg-gray-100">
              <option value="">开始新对话</option>
              <option v-for="conversation in activeConversations" :key="conversation.id" :value="conversation.id">
                {{ conversation.title }}
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
                <p class="font-semibold">{{ getLandTypeLabel(currentLand.landType) }}</p>
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

      <section class="min-h-0 lg:col-span-2">
        <div class="flex h-full min-h-0 flex-col rounded-2xl bg-white p-8 shadow-lg">
          <div class="mb-6 flex shrink-0 items-start justify-between gap-4">
            <div>
              <h2 class="flex items-center text-2xl font-bold text-dark">
                <i class="fa fa-comments mr-3 text-primary"></i>AI 技术顾问
              </h2>
              <p class="mt-2 text-sm text-gray-500">
                回答基于当前地块的设备、环境、灌溉、预警和任务上下文。
              </p>
            </div>
            <div v-if="currentConversation" class="flex items-center gap-3">
              <span class="rounded-full bg-green-50 px-3 py-1 text-xs font-medium text-green-700">
                进行中
              </span>
              <button type="button"
                class="rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-600 transition-colors hover:border-gray-400 hover:bg-gray-50"
                @click="archiveCurrentConversation">
                结束对话并归档
              </button>
            </div>
          </div>

          <div ref="messageListElement" class="min-h-0 flex-1 overflow-y-auto pr-2">
            <div v-if="messages.length === 0"
              class="rounded-xl border border-dashed border-gray-200 py-16 text-center text-gray-500">
              <i class="fa fa-comment-o mb-3 text-4xl text-gray-300"></i>
              <p>当前地块暂无 AI 对话</p>
            </div>

            <div v-else class="space-y-5">
            <div v-for="message in messages" :key="message.id" class="flex"
              :class="message.role === 'user' ? 'justify-end' : 'justify-start'">
              <article class="w-fit max-w-[88%] border p-5 sm:max-w-[78%]" :class="message.role === 'assistant'
                ? 'rounded-2xl rounded-tl-sm border-green-100 bg-green-50/60'
                : 'rounded-2xl rounded-tr-sm border-blue-100 bg-blue-50/70'">
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

              <details v-if="message.role === 'assistant' && message.references?.length"
                class="group mt-5 overflow-hidden rounded-xl border border-green-200 bg-white/80">
                <summary class="flex cursor-pointer list-none items-center justify-between gap-3 px-4 py-3 text-sm font-semibold text-gray-700">
                  <span class="flex items-center gap-2">
                    <i class="fa fa-database text-green-600"></i>
                    参考设备与数据
                    <span class="rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-700">
                      {{ message.references.length }} 项
                    </span>
                  </span>
                  <i class="fa fa-chevron-down text-xs text-gray-400 transition-transform group-open:rotate-180"></i>
                </summary>
                <div class="grid grid-cols-1 gap-3 border-t border-green-100 p-4 sm:grid-cols-2">
                  <div v-for="reference in message.references"
                    :key="`${reference.type}:${reference.sourceId}:${reference.label}`"
                    class="rounded-lg border border-gray-100 bg-white p-3 text-sm shadow-sm">
                    <div class="flex items-start justify-between gap-2">
                      <p class="text-gray-500">{{ reference.label }}</p>
                      <span class="shrink-0 rounded bg-gray-100 px-1.5 py-0.5 text-[11px] text-gray-500">
                        {{ getReferenceTypeLabel(reference.type) }}
                      </span>
                    </div>
                    <p class="mt-1 font-semibold text-gray-800">
                      {{ reference.value }}{{ reference.unit }}
                    </p>
                  </div>
                </div>
              </details>

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
                    <input v-model="taskDraftForms[message.id].assigneeId" type="text" placeholder="请输入负责人用户 ID"
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
          </div>
          </div>

          <div class="mt-5 shrink-0 rounded-xl border border-green-100 bg-green-50/40 p-5">
            <div class="mb-3 flex items-center gap-2 text-sm text-gray-600">
              <i class="fa fa-leaf text-green-600"></i>
              <span>{{ currentConversation ? '输入消息继续咨询 AI 技术顾问' : '输入问题开始新的 AI 对话' }}</span>
            </div>
            <div class="flex flex-col gap-3 sm:flex-row">
              <input v-model="messageText" type="text" placeholder="例如：当前土壤湿度是否需要灌溉？"
                :disabled="!currentLandId" @keyup.enter.prevent="sendMessage"
                class="min-w-0 flex-1 rounded-lg border border-gray-300 bg-white px-4 py-3 text-sm text-gray-700 outline-none transition-all placeholder:text-gray-400 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 disabled:cursor-not-allowed disabled:bg-gray-100 disabled:text-gray-400">
              <button type="button" :disabled="!currentLandId || !messageText.trim() || sending" @click="sendMessage"
                class="inline-flex items-center justify-center rounded-lg bg-green-600 px-6 py-3 text-sm font-medium text-white transition-colors hover:bg-green-700 disabled:cursor-not-allowed disabled:bg-gray-300">
                <i class="fa fa-paper-plane mr-2"></i>{{ sending ? '正在生成...' : '发送' }}
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </main>
</template>

<script setup>
import { computed, nextTick, ref, reactive, watch } from 'vue';
import { useFarmStore } from '../composables/useFarmStore';
import { useAuthSession } from '../composables/useAuthSession';
import { getLandTypeLabel } from '../utils/landType';
import { parseUtcDateTime } from '../utils/dateTime';

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
  loadLandModules,
  loadConversations,
  loadConversation,
  createAiConversation,
  sendAiMessage,
  closeAiConversation,
  createFarmTaskFromAiDraft
} = useFarmStore();
const { currentUser } = useAuthSession();

const defaultLandId = lands.value[0]?.id ?? '';
const currentLandId = ref(defaultLandId);
const currentConversationId = ref('');
const messageText = ref('');
const taskDraftForms = reactive({});
const creatingTaskMessageId = ref('');
const sending = ref(false);
const messageListElement = ref(null);

const referenceTypeLabels = {
  land: '地块',
  plantingPlan: '种植计划',
  device: '设备',
  environmentThreshold: '环境阈值',
  sensorReading: '监测数据',
  alert: '预警',
  farmTask: '农事任务',
  irrigationConfig: '灌溉配置',
  irrigationRecord: '灌溉记录'
};

const getReferenceTypeLabel = type => referenceTypeLabels[type] || '参考数据';

const createdTaskMessageIds = computed(() => new Set(
  farmTasks.value
    .filter(task => task.sourceType === 'aiMessage')
    .map(task => task.sourceId)
));

const currentLand = computed(() =>
  lands.value.find(land => land.id === currentLandId.value) ?? null
);

watch(lands, (currentLands) => {
  const currentLandExists = currentLands.some(land => land.id === currentLandId.value);

  if (!currentLandExists) {
    currentLandId.value = currentLands[0]?.id ?? '';
  }
}, { immediate: true });

const activeConversations = computed(() =>
  aiConversations.value.filter(conversation =>
    conversation.landId === currentLandId.value && conversation.status === 'active'
  )
);

const currentConversation = computed(() =>
  aiConversations.value.find(conversation =>
    conversation.id === currentConversationId.value &&
    conversation.landId === currentLandId.value &&
    conversation.status === 'active'
  ) ?? null
);

const messages = computed(() => currentConversation.value?.messages ?? []);

watch(() => messages.value.length, async () => {
  await nextTick();
  const messageList = messageListElement.value;
  if (messageList) {
    messageList.scrollTop = messageList.scrollHeight;
  }
}, { immediate: true });

const currentAiContext = computed(() => ({
  devices: devices.value.filter(item => item.landId === currentLandId.value),
  sensorReadings: sensorReadings.value.filter(item => item.landId === currentLandId.value),
  activeAlerts: alerts.value.filter(item =>
    item.landId === currentLandId.value && ['pending', 'processing'].includes(item.status)
  ),
  activeTasks: farmTasks.value.filter(item =>
    item.landId === currentLandId.value && ['pending', 'processing'].includes(item.status)
  )
}));

watch(currentLandId, async landId => {
  messageText.value = '';
  currentConversationId.value = '';
  if (!landId) return;
  try {
    const conversations = await loadConversations(landId);
    await loadLandModules(landId);
    currentConversationId.value = conversations[0]?.id ?? '';
  } catch (error) {
    alert(error.message || '加载 AI 对话失败');
  }
}, { immediate: true });

watch(currentConversationId, async conversationId => {
  if (!conversationId) return;
  const conversation = aiConversations.value.find(item => item.id === conversationId);
  if (conversation?.messages) return;
  try {
    await loadConversation(conversationId);
  } catch (error) {
    alert(error.message || '加载对话详情失败');
  }
});

watch(messages, (currentMessages) => {
  currentMessages.forEach(message => {
    if (!message.taskDraft) {
      return;
    }
    if (taskDraftForms[message.id]) {
      return;
    }
    taskDraftForms[message.id] = {
      assigneeId: message.taskDraft.assigneeId ?? currentUser.value?.id ?? '',
      deadline: message.taskDraft.deadline ?? ''
    };
  });
}, { immediate: true, deep: true });

const formatDate = value => {
  if (!value) return '';
  return new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(parseUtcDateTime(value));
};

const sendMessage = async () => {
  const text = messageText.value.trim();
  if (!text) {
    alert('发送内容不能为空');
    return;
  }

  if (!currentLand.value) {
    alert('请选择咨询地块');
    return;
  }

  sending.value = true;
  try {
    let conversation = currentConversation.value ?? null;

    if (!conversation) {
      conversation = await createAiConversation({
        landId: currentLandId.value,
        title: text
      });
      currentConversationId.value = conversation.id;
    }
    await sendAiMessage(conversation.id, text);
    messageText.value = '';
  } catch (error) {
    console.error(error);
    alert(error.message);
  } finally {
    sending.value = false;
  }
};

const archiveCurrentConversation = async () => {
  if (!currentConversation.value) {
    return;
  }

  try {
    await closeAiConversation(currentConversation.value);
    const conversations = await loadConversations(currentLandId.value);
    currentConversationId.value = conversations[0]?.id ?? '';
    messageText.value = '';
  } catch (error) {
    console.error(error);
    alert(error.message || '结束对话失败');
  }
};

const isTaskDraftSubmitDisabled = (message) => {
  const form = taskDraftForms[message.id];
  return !form ||
    !form.assigneeId.trim() ||
    !form.deadline ||
    creatingTaskMessageId.value === message.id ||
    createdTaskMessageIds.value.has(message.id);
};

const confirmTaskDraft = async (message) => {
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
    await createFarmTaskFromAiDraft({
      conversationId: currentConversation.value.id,
      messageId: message.id,
      assigneeId: form.assigneeId,
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
