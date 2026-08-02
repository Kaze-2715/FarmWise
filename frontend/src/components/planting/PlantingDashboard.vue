<template>
  <!-- 数据总览 -->
  <section class="bg-white rounded-xl card-shadow p-6 ">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold">数据总览</h2>
      <div class="flex space-x-2">
        <button
          type="button"
          :disabled="refreshing"
          class="inline-flex items-center rounded-lg border border-primary px-3 py-1.5 text-sm text-primary shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:shadow active:translate-y-0 active:shadow-sm disabled:cursor-not-allowed disabled:opacity-60"
          @click="emit('refresh')">
          <i :class="['fa mr-1', refreshing ? 'fa-spinner fa-spin' : 'fa-refresh']"></i>
          {{ refreshing ? '刷新中' : '刷新' }}
        </button>
      </div>
    </div>

    <!-- 数据卡片组 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <!-- 土壤湿度 -->
      <div class="min-h-20 bg-blue-50 rounded-xl p-4 border border-blue-100">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-gray-500 text-sm">土壤湿度</p>
            <h3 class="text-2xl font-bold mt-1">
              {{ dashboardEnvironmentSummary.soilMoisture.value }}{{ dashboardEnvironmentSummary.soilMoisture.unit }}
            </h3>
          </div>
          <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-500">
            <i class="fa fa-tint"></i>
          </div>
        </div>
      </div>

      <!-- 空气温度 -->
      <div class="min-h-20 bg-orange-50 rounded-xl p-4 border border-orange-100">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-gray-500 text-sm">空气温度</p>
            <h3 class="text-2xl font-bold mt-1">
              {{ dashboardEnvironmentSummary.airTemperature.value }}{{ dashboardEnvironmentSummary.airTemperature.unit
              }}
            </h3>
          </div>
          <div class="w-10 h-10 rounded-full bg-orange-100 flex items-center justify-center text-orange-500">
            <i class="fa fa-thermometer"></i>
          </div>
        </div>
      </div>

      <!-- 空气湿度 -->
      <div class="min-h-20 bg-green-50 rounded-xl p-4 border border-green-100">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-gray-500 text-sm">空气湿度</p>
            <h3 class="text-2xl font-bold mt-1">
              {{ dashboardEnvironmentSummary.airHumidity.value }}{{ dashboardEnvironmentSummary.airHumidity.unit }}
            </h3>
          </div>
          <div class="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center text-green-500">
            <i class="fa fa-cloud"></i>
          </div>
        </div>
      </div>

      <!-- 预警数量 -->
      <div class="min-h-20 bg-red-50 rounded-xl p-4 border border-red-100">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-gray-500 text-sm">未处理预警</p>
            <h3 class="text-2xl font-bold mt-1">{{ unhandledWarningCount }} 条</h3>
          </div>
          <div class="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center text-red-500">
            <i class="fa fa-bell"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- 当前土地设备运行情况 -->
    <div class="mb-6 rounded-xl border border-gray-100 bg-gray-50 p-4">
      <h3 class="mb-4 font-medium">设备运行情况</h3>
      <div class="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">设备总数</p>
          <p class="mt-1 text-2xl font-bold text-gray-800">{{ currentLandDevices.length }}</p>
        </div>
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">在线设备</p>
          <p class="mt-1 text-2xl font-bold text-green-600">{{ currentLandOnlineDeviceCount }}</p>
        </div>
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">离线设备</p>
          <p class="mt-1 text-2xl font-bold text-gray-600">{{ currentLandOfflineDeviceCount }}</p>
        </div>
        <div class="rounded-lg bg-white p-4">
          <p class="text-sm text-gray-500">低电量设备</p>
          <p class="mt-1 text-2xl font-bold text-amber-600">{{ currentLandLowBatteryDeviceCount }}</p>
        </div>
      </div>
    </div>

    <!-- 趋势图表 -->
    <div>
      <div class="bg-gray-50 rounded-xl p-4">
        <div class="mb-4">
          <h3 class="font-medium">环境数据趋势（最近 24 小时 · 每 2 小时平均）</h3>
        </div>
        <div v-if="environmentTrendSeries.length" class="grid grid-cols-1 gap-4 md:grid-cols-2">
          <SensorTrendChart v-for="series in environmentTrendSeries" :key="series.metric"
            :metric="series.metric" :label="series.label" :unit="series.unit" :color="series.color" :points="series.points" />
        </div>
        <div v-else class="flex h-64 items-center justify-center rounded-lg border border-dashed border-gray-200 text-sm text-gray-400">
          最近 24 小时暂无环境数据
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import SensorTrendChart from './SensorTrendChart.vue'

defineProps({
  dashboardEnvironmentSummary: {
    type: Object,
    required: true
  },
  unhandledWarningCount: {
    type: Number,
    required: true
  },
  currentLandDevices: {
    type: Array,
    required: true
  },
  currentLandOnlineDeviceCount: {
    type: Number,
    required: true
  },
  currentLandOfflineDeviceCount: {
    type: Number,
    required: true
  },
  currentLandLowBatteryDeviceCount: {
    type: Number,
    required: true
  },
  refreshing: {
    type: Boolean,
    required: true
  },
  environmentTrendSeries: {
    type: Array,
    required: true
  }
});

const emit = defineEmits(['refresh']);
</script>
