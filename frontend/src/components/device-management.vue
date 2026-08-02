<template>
  <main class="mx-auto flex h-full min-h-0 w-full max-w-7xl flex-col gap-6">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h2 class="flex items-center text-2xl font-bold text-gray-800">
          <i class="fa fa-microchip mr-3 text-green-600"></i>设备管理
        </h2>
      </div>
      <button
        type="button"
        class="inline-flex items-center justify-center rounded-lg bg-green-500 px-4 py-2 text-sm font-medium text-white shadow-sm transition hover:bg-green-600"
        @click="openAddModal"
      >
        <i class="fa fa-plus mr-2"></i>新增设备
      </button>
    </div>

    <section class="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
      <div class="rounded-xl border border-gray-100 bg-white p-5 shadow-sm">
        <p class="text-sm text-gray-500">设备总数</p>
        <p class="mt-2 text-3xl font-bold text-gray-800">{{ deviceCount }}</p>
      </div>
      <div class="rounded-xl border border-green-100 bg-green-50 p-5">
        <p class="text-sm text-green-700">在线设备</p>
        <p class="mt-2 text-3xl font-bold text-green-600">{{ onlineDeviceCount }}</p>
      </div>
      <div class="rounded-xl border border-gray-200 bg-gray-100 p-5">
        <p class="text-sm text-gray-600">离线设备</p>
        <p class="mt-2 text-3xl font-bold text-gray-700">{{ offlineDeviceCount }}</p>
      </div>
      <div class="rounded-xl border border-amber-100 bg-amber-50 p-5">
        <p class="text-sm text-amber-700">低电量设备</p>
        <p class="mt-2 text-3xl font-bold text-amber-600">{{ lowBatteryDeviceCount }}</p>
      </div>
    </section>

    <section class="rounded-xl border border-gray-100 bg-white p-5 shadow-sm">
      <div class="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-5">
        <input
          v-model="deviceKeyword"
          type="text"
          placeholder="搜索设备名称"
          class="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-green-500 focus:ring-2 focus:ring-green-500/20"
        >
        <select v-model="landFilter" class="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-green-500">
          <option value="all">全部土地</option>
          <option value="unbound">未绑定土地</option>
          <option v-for="land in lands" :key="land.id" :value="land.id">{{ land.name }}</option>
        </select>
        <select v-model="typeFilter" class="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-green-500">
          <option value="all">全部类型</option>
          <option v-for="type in deviceTypes" :key="type" :value="type">{{ getDeviceTypeLabel(type) }}</option>
        </select>
        <select v-model="statusFilter" class="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-green-500">
          <option value="all">全部状态</option>
          <option value="online">在线</option>
          <option value="offline">离线</option>
        </select>
        <button type="button" class="rounded-lg border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:border-green-500 hover:text-green-600" @click="resetFilters">
          重置筛选
        </button>
      </div>
    </section>

    <section class="min-h-0 flex-1 overflow-hidden rounded-xl border border-gray-100 bg-white shadow-sm">
      <div class="h-full overflow-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="sticky top-0 z-10 bg-gray-50">
            <tr>
              <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">设备</th>
              <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">类型</th>
              <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">绑定土地</th>
              <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">型号</th>
              <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">状态</th>
              <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">电量</th>
              <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">最后上报</th>
              <th class="px-5 py-3 text-right text-xs font-medium uppercase tracking-wider text-gray-500">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100 bg-white">
            <tr v-for="device in filteredDevices" :key="device.id" class="hover:bg-gray-50">
              <td class="whitespace-nowrap px-5 py-4">
                <p class="font-medium text-gray-800">{{ device.name }}</p>
                <p class="mt-1 text-xs text-gray-400">{{ device.id }}</p>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-gray-600">{{ getDeviceTypeLabel(device.deviceType) }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-gray-600">{{ getLandName(device.landId) }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-gray-600">{{ device.model }}</td>
              <td class="whitespace-nowrap px-5 py-4">
                <span
                  class="inline-flex rounded-full px-2.5 py-1 text-xs font-medium"
                  :class="device.status === 'online' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'"
                >
                  {{ device.status === 'online' ? '在线' : '离线' }}
                </span>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-sm">
                <span v-if="device.battery === null" class="text-gray-400">暂无数据</span>
                <span v-else :class="device.battery < 20 ? 'font-medium text-amber-600' : 'text-gray-600'">
                  {{ device.battery }}%
                </span>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-gray-600">
                {{ formatLastReportedAt(device.lastReportedAt) }}
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-right text-sm">
                <div class="inline-flex items-center gap-2">
                  <button type="button"
                    class="inline-flex items-center rounded-lg border border-green-200 bg-green-50 px-3 py-1.5 font-medium text-green-700 transition-all hover:-translate-y-0.5 hover:border-green-300 hover:bg-green-100 active:translate-y-0"
                    @click="openEditModal(device)">
                    <i class="fa fa-pencil mr-1.5"></i>编辑
                  </button>
                  <button type="button"
                    class="inline-flex items-center rounded-lg border border-red-200 bg-red-50 px-3 py-1.5 font-medium text-red-600 transition-all hover:-translate-y-0.5 hover:border-red-300 hover:bg-red-100 active:translate-y-0"
                    @click="openDeleteModal(device)">
                    <i class="fa fa-trash-o mr-1.5"></i>删除
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="devicesLoading">
              <td colspan="8" class="px-5 py-14 text-center text-gray-400">
                <i class="fa fa-spinner fa-spin mb-3 block text-3xl"></i>
                正在加载设备
              </td>
            </tr>
            <tr v-else-if="filteredDevices.length === 0">
              <td colspan="8" class="px-5 py-14 text-center text-gray-400">
                <i class="fa fa-inbox mb-3 block text-3xl"></i>
                没有符合当前条件的设备
              </td>
            </tr>
          </tbody>
          <tfoot class="bg-gray-50">
            <tr>
              <td colspan="8" class="px-5 py-3 text-sm text-gray-500">
                显示 {{ filteredDevices.length }} 台，共 {{ deviceCount }} 台设备
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </section>
  </main>

  <div v-if="formModalVisible" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4" @click.self="closeFormModal">
    <div class="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-xl bg-white shadow-xl">
      <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
        <h3 class="text-lg font-bold text-gray-800">{{ editingDeviceId ? '编辑设备' : '新增设备' }}</h3>
        <button type="button" class="text-gray-400 hover:text-gray-600" @click="closeFormModal">
          <i class="fa fa-times"></i>
        </button>
      </div>
      <form class="grid grid-cols-1 gap-4 p-6 sm:grid-cols-2" @submit.prevent="submitDevice">
        <label class="space-y-2">
          <span class="text-sm font-medium text-gray-700">设备名称</span>
          <input v-model="deviceForm.name" required class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-green-500">
        </label>
        <label class="space-y-2">
          <span class="text-sm font-medium text-gray-700">设备类型</span>
          <select v-model="deviceForm.deviceType" required class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-green-500">
            <option value="" disabled>请选择设备类型</option>
            <option v-for="option in deviceTypeOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </label>
        <label class="space-y-2">
          <span class="text-sm font-medium text-gray-700">绑定土地</span>
          <select v-model="deviceForm.landId" class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-green-500">
            <option value="">暂不绑定</option>
            <option v-for="land in lands" :key="land.id" :value="land.id">{{ land.name }}</option>
          </select>
        </label>
        <label class="space-y-2">
          <span class="text-sm font-medium text-gray-700">设备型号</span>
          <input v-model="deviceForm.model" required class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-green-500">
        </label>
        <label class="space-y-2">
          <span class="text-sm font-medium text-gray-700">安装时间</span>
          <input v-model="deviceForm.installDate" type="date" class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-green-500">
        </label>
        <div></div>
        <label class="space-y-2">
          <span class="text-sm font-medium text-gray-700">经度</span>
          <input v-model="deviceForm.longitude" type="number" step="any" required class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-green-500">
        </label>
        <label class="space-y-2">
          <span class="text-sm font-medium text-gray-700">纬度</span>
          <input v-model="deviceForm.latitude" type="number" step="any" required class="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:border-green-500">
        </label>
        <p v-if="formError" class="text-sm text-red-500 sm:col-span-2">{{ formError }}</p>
        <div class="flex justify-end gap-3 pt-2 sm:col-span-2">
          <button type="button" class="rounded-lg border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-50" @click="closeFormModal">取消</button>
          <button type="submit" class="rounded-lg bg-green-500 px-5 py-2 font-medium text-white hover:bg-green-600">
            {{ editingDeviceId ? '保存修改' : '新增设备' }}
          </button>
        </div>
      </form>
    </div>
  </div>

  <div v-if="deleteModalVisible && pendingDeleteDevice" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4" @click.self="closeDeleteModal">
    <div class="w-full max-w-md rounded-xl bg-white p-6 shadow-xl">
      <h3 class="text-lg font-bold text-gray-800">确认删除</h3>
      <p class="mt-3 text-gray-600">确认删除设备“{{ pendingDeleteDevice.name }}”吗？</p>
      <p v-if="deleteError" class="mt-3 text-sm text-red-500">{{ deleteError }}</p>
      <div class="mt-6 flex justify-end gap-3">
        <button type="button" class="rounded-lg border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-50" @click="closeDeleteModal">取消</button>
        <button type="button" class="rounded-lg bg-red-500 px-4 py-2 text-white hover:bg-red-600" @click="confirmDelete">删除</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useFarmStore } from "../composables/useFarmStore";
import { parseUtcDateTime } from '../utils/dateTime';

const { devices, devicesLoading, lands, addDevice, updateDevice, deleteDevice } = useFarmStore();

const deviceTypeOptions = [
  { value: 'soil_moisture_sensor', label: '土壤湿度传感器' },
  { value: 'air_temp_humidity_sensor', label: '空气温湿度传感器' },
  { value: 'light_sensor', label: '光照传感器' },
  { value: 'soil_ph_sensor', label: '土壤 pH 传感器' },
  { value: 'pest_camera', label: '虫情摄像头' },
  { value: 'irrigation_controller', label: '灌溉控制器' }
];

const deviceTypeLabels = new Map(
  deviceTypeOptions.map(option => [option.value, option.label])
);

const createEmptyDeviceForm = () => ({
  name: '',
  deviceType: '',
  landId: '',
  model: '',
  installDate: '',
  longitude: '',
  latitude: ''
});

const deviceKeyword = ref('');
const landFilter = ref('all');
const typeFilter = ref('all');
const statusFilter = ref('all');
const formModalVisible = ref(false);
const deleteModalVisible = ref(false);
const editingDeviceId = ref(null);
const pendingDeleteDevice = ref(null);
const deviceForm = ref(createEmptyDeviceForm());
const formError = ref('');
const deleteError = ref('');

const deviceCount = computed(() => devices.value.length);
const onlineDeviceCount = computed(() => devices.value.filter(device => device.status === 'online').length);
const offlineDeviceCount = computed(() => devices.value.filter(device => device.status === 'offline').length);
const lowBatteryDeviceCount = computed(() =>
  devices.value.filter(device => device.battery !== null && device.battery < 20).length
);
const deviceTypes = computed(() => [...new Set(devices.value.map(device => device.deviceType).filter(Boolean))]);

const filteredDevices = computed(() => {
  const keyword = deviceKeyword.value.trim().toLowerCase();

  return devices.value.filter(device => {
    const matchesKeyword = device.name.toLowerCase().includes(keyword);
    const matchesType = typeFilter.value === 'all' || device.deviceType === typeFilter.value;
    const matchesStatus = statusFilter.value === 'all' || device.status === statusFilter.value;

    let matchesLand = true;
    if (landFilter.value === 'unbound') {
      matchesLand = !device.landId;
    } else if (landFilter.value !== 'all') {
      matchesLand = device.landId === landFilter.value;
    }

    return matchesKeyword && matchesLand && matchesType && matchesStatus;
  });
});

const getLandName = (landId) => {
  if (!landId) {
    return "未绑定";
  }

  return lands.value.find(land => land.id === landId)?.name || "未知土地";
};

const getDeviceTypeLabel = (deviceType) => deviceTypeLabels.get(deviceType) || deviceType;

const formatLastReportedAt = value => {
  if (!value) return '尚未上报';
  const date = parseUtcDateTime(value);
  if (Number.isNaN(date.getTime())) return '时间格式异常';

  return new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).format(date);
};

const resetFilters = () => {
  deviceKeyword.value = '';
  landFilter.value = 'all';
  typeFilter.value = 'all';
  statusFilter.value = 'all';
};

const openAddModal = () => {
  editingDeviceId.value = null;
  deviceForm.value = createEmptyDeviceForm();
  formError.value = '';
  formModalVisible.value = true;
};

const openEditModal = (device) => {
  editingDeviceId.value = device.id;
  deviceForm.value = {
    name: device.name,
    deviceType: device.deviceType,
    landId: device.landId || '',
    model: device.model,
    installDate: device.installDate || '',
    longitude: device.longitude,
    latitude: device.latitude
  };
  formError.value = '';
  formModalVisible.value = true;
};

const closeFormModal = () => {
  formModalVisible.value = false;
  editingDeviceId.value = null;
  deviceForm.value = createEmptyDeviceForm();
  formError.value = '';
};

const submitDevice = async () => {
  formError.value = '';

  try {
    if (editingDeviceId.value) {
      await updateDevice(editingDeviceId.value, deviceForm.value);
    } else {
      await addDevice(deviceForm.value);
    }
    closeFormModal();
  } catch (error) {
    formError.value = error.message || '保存设备失败';
  }
};

const openDeleteModal = (device) => {
  pendingDeleteDevice.value = device;
  deleteError.value = '';
  deleteModalVisible.value = true;
};

const closeDeleteModal = () => {
  deleteModalVisible.value = false;
  pendingDeleteDevice.value = null;
  deleteError.value = '';
};

const confirmDelete = async () => {
  if (!pendingDeleteDevice.value) {
    return;
  }

  try {
    await deleteDevice(pendingDeleteDevice.value.id);
    closeDeleteModal();
  } catch (error) {
    deleteError.value = error.message || '删除设备失败';
  }
};
</script>
