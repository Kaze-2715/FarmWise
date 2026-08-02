<template>
  <section class="space-y-6">
    <header class="flex flex-col gap-4 rounded-xl bg-white p-6 card-shadow sm:flex-row sm:items-center sm:justify-between">
      <div>
        <div class="flex items-center gap-3">
          <span class="flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-50 text-emerald-600">
            <i class="fa fa-tasks"></i>
          </span>
          <div>
            <h1 class="text-xl font-bold text-gray-800">农事任务</h1>
            <p class="mt-1 text-sm text-gray-500">安排并跟踪当前地块的农事工作</p>
          </div>
        </div>
      </div>

      <button type="button" @click="openCreateModal"
        class="inline-flex items-center justify-center gap-2 rounded-lg bg-emerald-600 px-4 py-2.5 text-sm font-medium text-white shadow-sm transition-colors hover:bg-emerald-700">
        <i class="fa fa-plus"></i>
        新建任务
      </button>
    </header>

    <div class="rounded-xl bg-white p-5 card-shadow">
      <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <div class="grid flex-1 gap-4 sm:grid-cols-3">
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">任务类型</span>
            <select v-model="selectedTaskType"
              class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-sm text-gray-700 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
              <option value="all">全部类型</option>
              <option value="irrigation">灌溉</option>
              <option value="fertilization">施肥</option>
              <option value="pesticide">施药</option>
              <option value="weeding">除草</option>
              <option value="inspection">巡检</option>
              <option value="harvest">收获</option>
              <option value="other">其他</option>
            </select>
          </label>

          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">任务状态</span>
            <select v-model="selectedStatus"
              class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-sm text-gray-700 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
              <option value="all">全部状态</option>
              <option value="pending">待处理</option>
              <option value="processing">进行中</option>
              <option value="completed">已完成</option>
              <option value="cancelled">已取消</option>
            </select>
          </label>

          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">任务优先级</span>
            <select v-model="selectedPriority"
              class="w-full rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-sm text-gray-700 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
              <option value="all">全部优先级</option>
              <option value="high">高</option>
              <option value="medium">中</option>
              <option value="low">低</option>
            </select>
          </label>
        </div>

        <p class="whitespace-nowrap text-sm text-gray-500">
          当前地块共
          <span class="font-semibold text-emerald-600">{{ filteredTasks.length }}</span>
          个农事任务
        </p>
      </div>
    </div>

    <div v-if="filteredTasks.length" class="space-y-5">
      <article v-for="task in filteredTasks" :key="task.id"
        class="overflow-hidden rounded-xl border border-gray-100 bg-white card-shadow transition-shadow hover:shadow-md">
        <div class="flex items-start justify-between gap-4 border-b border-gray-100 px-5 py-4">
          <div class="min-w-0">
            <div class="mb-2 flex flex-wrap items-center gap-2">
              <span class="rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-medium text-emerald-700">
                {{ taskTypeLabels[task.taskType] || task.taskType }}
              </span>
              <span class="rounded-full px-2.5 py-1 text-xs font-medium" :class="priorityClasses[task.priority]">
                {{ priorityLabels[task.priority] || task.priority }}
              </span>
              <span class="rounded-full px-2.5 py-1 text-xs font-medium" :class="statusClasses[task.status]">
                {{ statusLabels[task.status] || task.status }}
              </span>
            </div>
            <h2 class="truncate text-base font-semibold text-gray-800">{{ task.title }}</h2>
          </div>
          <span class="shrink-0 text-xs font-medium text-gray-400">{{ task.id }}</span>
        </div>

        <div class="space-y-4 px-5 py-4">
          <p class="min-h-10 text-sm leading-6 text-gray-600">{{ task.description }}</p>

          <div class="grid gap-3 rounded-lg bg-gray-50 p-4 text-sm sm:grid-cols-2">
            <div>
              <p class="text-xs text-gray-400">负责人</p>
              <p class="mt-1 font-medium text-gray-700">{{ task.assignee || '暂未分配' }}</p>
            </div>
            <div>
              <p class="text-xs text-gray-400">截止时间</p>
              <p class="mt-1 font-medium text-gray-700">{{ formatDateTime(task.deadline) }}</p>
            </div>
            <div>
              <p class="text-xs text-gray-400">任务来源</p>
              <p class="mt-1 font-medium text-gray-700">
                {{ sourceTypeLabels[task.sourceType] || task.sourceType }}
                <span v-if="task.sourceId" class="text-xs text-gray-400">（{{ task.sourceId }}）</span>
              </p>
            </div>
            <div>
              <p class="text-xs text-gray-400">创建时间</p>
              <p class="mt-1 font-medium text-gray-700">{{ formatDateTime(task.createdAt) }}</p>
            </div>
          </div>

          <div v-if="task.remark" class="flex gap-2 rounded-lg border border-gray-100 px-3 py-2.5 text-sm text-gray-500">
            <i class="fa fa-sticky-note-o mt-0.5 text-gray-400"></i>
            <span>{{ task.remark }}</span>
          </div>

          <div v-if="task.status === 'completed' && task.result"
            class="rounded-lg border border-emerald-100 bg-emerald-50 px-4 py-3 text-sm text-emerald-800">
            <span class="font-medium">完成结果：</span>{{ task.result }}
          </div>

          <div v-if="task.status === 'pending' || task.status === 'processing'"
            class="flex flex-wrap justify-end gap-2 border-t border-gray-100 pt-4">
            <button v-if="task.status === 'pending'" type="button" @click="startTask(task)"
              class="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-emerald-700">
              开始任务
            </button>
            <button v-if="task.status === 'processing'" type="button" @click="openCompleteModal(task)"
              class="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-emerald-700">
              完成任务
            </button>
            <button type="button" @click="openCancelModal(task)"
              class="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-600 transition-colors hover:bg-gray-50">
              取消任务
            </button>
          </div>
        </div>
      </article>
    </div>

    <div v-else class="rounded-xl bg-white px-6 py-16 text-center card-shadow">
      <span class="mx-auto flex h-14 w-14 items-center justify-center rounded-full bg-gray-100 text-xl text-gray-400">
        <i class="fa fa-clipboard"></i>
      </span>
      <h2 class="mt-4 font-semibold text-gray-700">当前地块暂无农事任务</h2>
      <p class="mt-2 text-sm text-gray-500">可以新建任务，或从异常预警生成处理任务。</p>
    </div>

    <div v-if="showCreateModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
      @click.self="closeCreateModal">
      <div class="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-xl bg-white p-6 shadow-xl">
        <div class="mb-5 flex items-start justify-between">
          <div>
            <h2 class="text-lg font-bold text-gray-800">新建农事任务</h2>
            <p class="mt-1 text-sm text-gray-500">任务将添加到当前地块，并从待处理状态开始。</p>
          </div>
          <button type="button" class="text-xl text-gray-400 hover:text-gray-600" @click="closeCreateModal">&times;</button>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">任务类型</span>
            <select v-model="taskForm.taskType" class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
              <option value="irrigation">灌溉</option>
              <option value="fertilization">施肥</option>
              <option value="pesticide">施药</option>
              <option value="weeding">除草</option>
              <option value="inspection">巡检</option>
              <option value="harvest">收获</option>
              <option value="other">其他</option>
            </select>
          </label>
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">优先级</span>
            <select v-model="taskForm.priority" class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
              <option value="high">高</option>
              <option value="medium">中</option>
              <option value="low">低</option>
            </select>
          </label>
          <label class="block sm:col-span-2">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">任务标题</span>
            <input v-model.trim="taskForm.title" type="text" class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100" placeholder="请输入任务标题">
          </label>
          <label class="block sm:col-span-2">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">任务说明</span>
            <textarea v-model.trim="taskForm.description" rows="3" class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100" placeholder="请输入具体工作内容"></textarea>
          </label>
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">负责人</span>
            <input v-model.trim="taskForm.assignee" type="text" class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100" placeholder="请输入负责人">
          </label>
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">截止时间</span>
            <input v-model="taskForm.deadline" type="datetime-local" class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100">
          </label>
          <label class="block sm:col-span-2">
            <span class="mb-1.5 block text-sm font-medium text-gray-700">备注</span>
            <textarea v-model.trim="taskForm.remark" rows="2" class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100" placeholder="可选"></textarea>
          </label>
        </div>

        <div class="mt-6 flex justify-end gap-3">
          <button type="button" class="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50" @click="closeCreateModal">取消</button>
          <button type="button" class="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white hover:bg-emerald-700" @click="createTask">创建任务</button>
        </div>
      </div>
    </div>

    <div v-if="showActionModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
      @click.self="closeActionModal">
      <div class="w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
        <h2 class="text-lg font-bold text-gray-800">{{ actionType === 'complete' ? '完成任务' : '取消任务' }}</h2>
        <p class="mt-1 text-sm text-gray-500">{{ selectedTask?.title }}</p>

        <label class="mt-5 block">
          <span class="mb-1.5 block text-sm font-medium text-gray-700">
            {{ actionType === 'complete' ? '完成结果' : '取消原因' }}
          </span>
          <textarea v-model.trim="actionRemark" rows="4" class="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100"
            :placeholder="actionType === 'complete' ? '请填写任务完成情况' : '请填写取消任务的原因'"></textarea>
        </label>

        <div class="mt-6 flex justify-end gap-3">
          <button type="button" class="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50" @click="closeActionModal">返回</button>
          <button type="button" class="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white hover:bg-emerald-700" @click="submitTaskAction">确认</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useFarmStore } from '../../composables/useFarmStore';

const props = defineProps({
  landId: {
    type: String,
    required: true
  }
});

const { farmTasks } = useFarmStore();

const selectedTaskType = ref('all');
const selectedStatus = ref('all');
const selectedPriority = ref('all');
const showCreateModal = ref(false);
const showActionModal = ref(false);
const selectedTask = ref(null);
const actionType = ref('');
const actionRemark = ref('');

const emptyTaskForm = {
  taskType: 'inspection',
  title: '',
  description: '',
  priority: 'medium',
  assignee: '',
  deadline: '',
  remark: ''
};

const taskForm = ref({ ...emptyTaskForm });

const taskTypeLabels = {
  irrigation: '灌溉',
  fertilization: '施肥',
  pesticide: '施药',
  weeding: '除草',
  inspection: '巡检',
  harvest: '收获',
  other: '其他'
};

const priorityLabels = { high: '高优先级', medium: '中优先级', low: '低优先级' };
const statusLabels = { pending: '待处理', processing: '进行中', completed: '已完成', cancelled: '已取消' };
const sourceTypeLabels = {
  manual: '人工创建',
  alert: '预警触发',
  plan: '农事计划',
  system: '系统生成',
  aiMessage: 'AI 顾问建议'
};
const priorityClasses = {
  high: 'bg-red-50 text-red-700',
  medium: 'bg-amber-50 text-amber-700',
  low: 'bg-gray-100 text-gray-600'
};
const statusClasses = {
  pending: 'bg-orange-50 text-orange-700',
  processing: 'bg-blue-50 text-blue-700',
  completed: 'bg-emerald-50 text-emerald-700',
  cancelled: 'bg-gray-100 text-gray-500'
};

const currentLandTasks = computed(() =>
  farmTasks.value.filter(task => task.landId === props.landId)
);

const filteredTasks = computed(() => currentLandTasks.value.filter(task => {
  const typeMatched = selectedTaskType.value === 'all' || task.taskType === selectedTaskType.value;
  const statusMatched = selectedStatus.value === 'all' || task.status === selectedStatus.value;
  const priorityMatched = selectedPriority.value === 'all' || task.priority === selectedPriority.value;
  return typeMatched && statusMatched && priorityMatched;
}));

const formatDateTime = value => {
  if (!value) return '暂未设置';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
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

const openCreateModal = () => {
  taskForm.value = { ...emptyTaskForm };
  showCreateModal.value = true;
};

const closeCreateModal = () => {
  showCreateModal.value = false;
};

const createTask = () => {
  if (!taskForm.value.title || !taskForm.value.description || !taskForm.value.assignee || !taskForm.value.deadline) {
    return window.alert('请填写任务标题、任务说明、负责人和截止时间');
  }

  const deadline = new Date(taskForm.value.deadline);
  if (Number.isNaN(deadline.getTime())) {
    return window.alert('截止时间格式不正确');
  }

  farmTasks.value.unshift({
    id: `TASK-${crypto.randomUUID().slice(0, 8).toUpperCase()}`,
    landId: props.landId,
    sourceType: 'manual',
    sourceId: null,
    taskType: taskForm.value.taskType,
    title: taskForm.value.title,
    description: taskForm.value.description,
    priority: taskForm.value.priority,
    status: 'pending',
    assignee: taskForm.value.assignee,
    deadline: deadline.toISOString(),
    createdAt: new Date().toISOString(),
    completedAt: null,
    result: '',
    remark: taskForm.value.remark
  });

  closeCreateModal();
};

const startTask = task => {
  const target = farmTasks.value.find(item => item.id === task.id);
  if (!target || target.status !== 'pending') {
    return window.alert('任务状态已发生变化，请刷新后重试');
  }
  target.status = 'processing';
};

const openCompleteModal = task => {
  selectedTask.value = task;
  actionType.value = 'complete';
  actionRemark.value = '';
  showActionModal.value = true;
};

const openCancelModal = task => {
  selectedTask.value = task;
  actionType.value = 'cancel';
  actionRemark.value = '';
  showActionModal.value = true;
};

const closeActionModal = () => {
  showActionModal.value = false;
  selectedTask.value = null;
  actionType.value = '';
  actionRemark.value = '';
};

const submitTaskAction = () => {
  if (!actionRemark.value) {
    return window.alert(actionType.value === 'complete' ? '请填写任务完成结果' : '请填写取消原因');
  }

  const target = farmTasks.value.find(task => task.id === selectedTask.value?.id);
  if (!target || !['pending', 'processing'].includes(target.status)) {
    closeActionModal();
    return window.alert('任务状态已发生变化，请刷新后重试');
  }

  if (actionType.value === 'complete') {
    if (target.status !== 'processing') {
      return window.alert('只有进行中的任务可以完成');
    }
    target.status = 'completed';
    target.completedAt = new Date().toISOString();
    target.result = actionRemark.value;
  } else {
    target.status = 'cancelled';
    target.remark = actionRemark.value;
    target.completedAt = null;
    target.result = '';
  }

  closeActionModal();
};
</script>
