<template>
  <div>
    <div class="flex flex-col md:flex-row gap-6">
      <!-- 左侧导航 -->
      <aside class="md:w-64 bg-white rounded-xl card-shadow p-4 flex-shrink-0">
        <nav class="space-y-1">
          <button type="button" @click="activeSection = 'dashboard'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'dashboard' }">
            <i class="fa fa-dashboard w-5 text-center flex-shrink-0"></i>
            <span>数据总览</span>
          </button>
          <button type="button" @click="activeSection = 'planting-plan'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'planting-plan' }">
            <i class="fa fa-calendar-check-o w-5 text-center flex-shrink-0"></i>
            <span>种植规划</span>
          </button>
          <button type="button" @click="activeSection = 'environment-monitor'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'environment-monitor' }">
            <i class="fa fa-thermometer-half w-5 text-center flex-shrink-0"></i>
            <span>环境监控</span>
          </button>
          <button type="button" @click="activeSection = 'pest-warning'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'pest-warning' }">
            <i class="fa fa-exclamation-triangle text-danger w-5 text-center flex-shrink-0"></i>
            <span>病虫害预警</span>
            <span class="ml-auto bg-danger text-white text-xs px-2 py-1 rounded-full">{{
              unhandledWarningCount }}</span>
          </button>
          <button type="button" @click="activeSection = 'handle-record'"
            class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
            :class="{ 'nav-active': activeSection === 'handle-record' }">
            <i class="fa fa-list-alt w-5 text-center flex-shrink-0"></i>
            <span>处理记录</span>
          </button>
        </nav>

        <!-- 土地选择 -->
        <div class="mt-3 px-4 shadow-none">
          <label for="current-land" class="block text-sm font-medium text-gray-700 mb-2">当前土地</label>
          <select id="current-land" v-model="selectedLandId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm shadow-none focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option v-for="land in lands" :key="land.id" :value="land.id">
              {{ land.name }}
            </option>
          </select>
        </div>

        <!-- 农场信息卡片 -->
        <div class="mt-3 bg-primary/5 rounded-xl p-4">
          <h3 class="font-medium text-primary mb-3">农场信息</h3>
          <div class="space-y-2 text-sm">
            <div class="flex justify-between">
              <span class="text-gray-600">土地类型</span>
              <span class="font-medium">{{ currentLand.type }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">种植面积</span>
              <span class="font-medium">{{ currentLand.area }} 亩</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">当前作物</span>
              <span class="font-medium">{{ currentLand.crop }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">传感器数量</span>
              <span class="font-medium">{{ currentLandDevices.length }} 个</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">预警处理率</span>
              <span class="font-medium text-primary">{{ warningHandleRate }}%</span>
            </div>
          </div>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <main class="flex-1 space-y-8">
        <!-- 数据总览 -->
        <section v-if="activeSection === 'dashboard'" class="bg-white rounded-xl card-shadow p-6 ">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold">数据总览</h2>
            <div class="flex space-x-2">
              <button
                class="inline-flex items-center rounded-lg border border-primary px-3 py-1.5 text-sm text-primary shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:shadow active:translate-y-0 active:shadow-sm">
                <i class="fa fa-refresh mr-1"></i> 刷新
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
                    {{ dashboardEnvironmentSummary.airTemperature.value }}{{ dashboardEnvironmentSummary.airTemperature.unit }}
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
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div class="bg-gray-50 rounded-xl p-4">
              <h3 class="font-medium mb-4">环境数据趋势（7天）</h3>
              <div
                class="h-64 flex items-center justify-center text-sm text-gray-400 border border-dashed border-gray-200 rounded-lg">
                图表待接入
              </div>
            </div>
            <div class="bg-gray-50 rounded-xl p-4">
              <h3 class="font-medium mb-4">作物生长状态</h3>
              <div
                class="h-64 flex items-center justify-center text-sm text-gray-400 border border-dashed border-gray-200 rounded-lg">
                图表待接入
              </div>
            </div>
          </div>
        </section>

        <!-- 种植规划 -->
        <section v-if="activeSection === 'planting-plan'" class="bg-white rounded-xl card-shadow p-6 ">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold">种植规划</h2>
            <button
              class="inline-flex items-center rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm"
              @click="addPlanModalVisible = true">
              <i class="fa fa-plus mr-1"></i> 新增规划
            </button>
          </div>

          <!-- 种植计划表格 -->
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead>
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    规划名称</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    作物类型</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    种植面积(亩)</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    种植时间</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    操作</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="plan in filteredPlans" :key="plan.id">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm font-medium">{{ plan.planName }}</div>
                  </td>

                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm">{{ plan.cropType }}</div>
                  </td>

                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm">{{ plan.area }}</div>
                  </td>

                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm">
                      {{ plan.plantingTime }} 至 {{ plan.expectedHarvestTime }}
                    </div>
                  </td>

                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                      {{ plan.status }}
                    </span>
                  </td>

                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div class="flex items-center gap-2">
                      <button type="button" @click="editPlan(plan)"
                        class="rounded-md border border-primary/30 px-2.5 py-1 text-primary transition-all duration-200 hover:-translate-y-0.5 hover:border-primary hover:shadow-sm active:translate-y-0">
                        编辑
                      </button>
                      <button type="button" @click="viewPlan(plan)"
                        class="rounded-md border border-gray-300 px-2.5 py-1 text-gray-600 transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow-sm active:translate-y-0">
                        查看
                      </button>
                      <button type="button" @click="openDeletePlanModal(plan)"
                        class="rounded-md border border-red-300 px-2.5 py-1 text-red-500 transition-all duration-200 hover:-translate-y-0.5 hover:border-red-500 hover:shadow-sm active:translate-y-0">
                        删除
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- 环境监控 -->
        <section v-if="activeSection === 'environment-monitor'" class="bg-white rounded-xl card-shadow p-6">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold">环境监控</h2>
            <div class="flex space-x-2">
              <button
                class="inline-flex items-center rounded-lg border border-gray-300 px-3 py-1.5 text-sm text-gray-700 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow active:translate-y-0 active:shadow-sm"
                @click="exportSensorTrendData">
                <i class="fa fa-download mr-1"></i> 导出数据
              </button>
            </div>
          </div>

          <!-- 实时数据卡片 -->
          <div class="mb-6 grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
            <div v-if="realtimeIndicators.length === 0"
              class="col-span-full rounded-xl border border-dashed border-gray-200 bg-gray-50 p-6 text-center text-sm text-gray-500">
              当前地块还没有启用的环境阈值，请先配置监测标准。
            </div>
            <div v-for="indicator in realtimeIndicators" :key="indicator.metric"
              class="rounded-xl border border-gray-100 bg-gray-50 p-4">
              <div class="mb-3 flex items-start justify-between gap-3">
                <div>
                  <h3 class="font-medium text-gray-800">{{ indicator.label }}</h3>
                  <p class="mt-1 text-xs text-gray-500">
                    建议区间：{{ indicator.min }} - {{ indicator.max }} {{ indicator.unit }}
                  </p>
                </div>
                <span class="rounded-full px-2.5 py-1 text-xs font-medium" :class="getSensorStatusClass(indicator.status)">
                  {{ sensorStatusLabels[indicator.status] }}
                </span>
              </div>

              <div class="flex items-end gap-2">
                <span class="text-3xl font-bold text-gray-900">{{ indicator.value ?? '--' }}</span>
                <span class="pb-1 text-sm text-gray-500">{{ indicator.unit }}</span>
              </div>

              <p class="mt-3 text-xs text-gray-400">
                更新时间：{{ formatRecordedAt(indicator.recordedAt) }}
              </p>
            </div>
          </div>

          <!-- 历史趋势图表 -->
          <div class="bg-gray-50 rounded-xl p-4 mb-6">
            <div class="flex justify-between items-center mb-4">
              <h3 class="font-medium">历史趋势</h3>
              <div class="flex items-center gap-2">
                <select v-model="sensorTrendRange"
                  class="rounded-lg border border-gray-300 bg-white px-3 py-1 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary/50">
                  <option value="day">日</option>
                  <option value="week">周</option>
                  <option value="month">月</option>
                  <option value="season">季</option>
                </select>
                <select v-model="selectedTrendMetric"
                  class="rounded-lg border border-gray-300 bg-white px-3 py-1 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary/50">
                  <option v-for="option in trendMetricOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </option>
                </select>
              </div>
            </div>
            <div class="min-h-80 rounded-lg border border-dashed border-gray-200 bg-white p-4 text-sm">
              <div class="mb-3 text-gray-600">
                当前范围内共有 {{ filteredTrendReadings.length }} 条传感器数据
              </div>

              <div v-if="filteredTrendReadings.length === 0" class="flex h-64 items-center justify-center text-gray-400">
                当前时间范围暂无趋势数据
              </div>

              <div v-else-if="visibleTrendMetricSummaries.length === 0"
                class="flex h-64 items-center justify-center text-gray-400">
                当前指标暂无趋势数据
              </div>

              <div v-else class="grid grid-cols-1 gap-4 lg:grid-cols-2">
                <div v-for="summary in visibleTrendMetricSummaries" :key="summary.metric"
                  class="rounded-lg bg-white p-4 shadow-sm">
                  <div class="mb-3 flex items-start justify-between">
                    <div>
                      <h4 class="font-medium text-gray-800">{{ summary.label }}</h4>
                      <p class="mt-1 text-xs text-gray-500">数据点：{{ summary.count }}</p>
                    </div>
                    <div class="text-right">
                      <div class="text-xl font-bold text-gray-900">
                        {{ summary.latestValue ?? '--' }} {{ summary.unit }}
                      </div>
                    </div>
                  </div>

                  <div class="flex flex-wrap gap-2">
                    <span v-for="point in summary.points" :key="point.time"
                      class="rounded bg-gray-100 px-2 py-1 text-xs text-gray-600">
                      {{ point.value }} ({{ formatTrendPointTime(point.time) }})
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 传感器状态表格 -->
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead>
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    传感器编号</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    安装位置</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    监测类型</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    最新数据</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    更新时间</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    状态</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="row in currentLandSensorRows" :key="row.id">
                  <td class="px-6 py-4 whitespace-nowrap text-sm">{{ row.name }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm">{{ row.location }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm">{{ row.metrics }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm">{{ row.latestData }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm">{{ formatRecordedAt(row.updatedAt) }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                      :class="getDeviceStatusClass(row.status)">
                      {{ deviceStatusLabels[row.status] || row.status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- 病虫害预警 -->
        <section v-if="activeSection === 'pest-warning'" class="bg-white rounded-xl card-shadow p-6">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold">病虫害预警</h2>
            <div class="flex space-x-2">
              <div class="relative">
                <select v-model="warningFilter"
                  class="appearance-none bg-gray-50 border border-gray-300 text-gray-700 py-2 pl-3 pr-8 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
                  <option value="all">全部预警</option>
                  <option value="red">红色预警</option>
                  <option value="yellow">黄色预警</option>
                  <option value="handled">已处理</option>
                </select>
                <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                  <i class="fa fa-chevron-down text-xs"></i>
                </div>
              </div>
            </div>
          </div>

          <!-- 预警统计 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <div class="bg-red-50 rounded-xl p-4 border border-red-100">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm">红色预警</p>
                  <h3 class="text-2xl font-bold mt-1 text-red-600">{{ redWarningCount }} 条</h3>
                </div>
                <div class="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center text-red-500">
                  <i class="fa fa-exclamation-circle text-xl"></i>
                </div>
              </div>
            </div>
            <div class="bg-yellow-50 rounded-xl p-4 border border-yellow-100">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm">黄色预警</p>
                  <h3 class="text-2xl font-bold mt-1 text-yellow-600">{{ yellowWarningCount }} 条
                  </h3>
                </div>
                <div class="w-12 h-12 rounded-full bg-yellow-100 flex items-center justify-center text-yellow-500">
                  <i class="fa fa-exclamation-triangle text-xl"></i>
                </div>
              </div>
            </div>
            <div class="bg-green-50 rounded-xl p-4 border border-green-100">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm">已处理预警</p>
                  <h3 class="text-2xl font-bold mt-1 text-green-600">{{ handledWarningCount }} 条</h3>
                </div>
                <div class="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center text-green-500">
                  <i class="fa fa-check-circle text-xl"></i>
                </div>
              </div>
            </div>
          </div>

          <div class="space-y-4">
            <div v-for="warning in filteredWarnings" :key="warning.id" class="border border-gray-200 rounded-lg p-4">
              <div class="flex justify-between items-start mb-2">
                <div>
                  <div class="font-medium">
                    {{ warning.message }}
                  </div>
                  <div class="text-sm text-gray-500">
                    作物类型：{{ warning.cropType }}
                  </div>
                </div>
                <span class="px-2 py-1 rounded-full text-xs font-medium"
                  :class="getWarningLevelClass(warning.handled ? 'green' : warning.level)">
                  {{ warning.handled ? '已处理' : getWarningLevelText(warning.level) }}
                </span>
              </div>

              <p class="text-sm text-gray-600">
                {{ warning.suggestion }}
              </p>
              <button type="button" v-if="!warning.handled"
                class="mt-3 inline-flex items-center rounded-lg px-3 py-1.5 text-sm text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:shadow active:translate-y-0 active:shadow-sm"
                :class="getWarningButtonClass(warning.level)"
                @click="handleWarning(warning)">处理预警</button>
            </div>
          </div>
        </section>

        <!-- 处理记录 -->
        <section v-if="activeSection === 'handle-record'" class="bg-white rounded-xl card-shadow p-6">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-bold">预警处理记录</h2>
            <div class="relative">
              <input v-model="recordKeyword" type="text" placeholder="搜索预警编号/作物类型"
                class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 w-64">
              <i class="fa fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
            </div>
          </div>

          <!-- 处理记录表格 -->
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead>
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    记录ID</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    作物类型</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    预警类型</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    处理措施</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    处理时间</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    处理效果</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    操作人</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-if="filteredHandleRecords.length === 0">
                  <td colspan="7" class="px-6 py-4 text-center text-gray-500">暂无处理记录</td>
                </tr>

                <template v-else>
                  <tr v-for="record in filteredHandleRecords" :key="record.id">
                    <td class="px-6 py-4 whitespace-nowrap">{{ record.id }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">{{ record.cropType }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="px-2 py-1 rounded-full text-xs font-medium"
                        :class="getWarningLevelClass(record.level)">
                        {{ getWarningLevelText(record.level) }}
                      </span>
                    </td>
                    <td class="px-6 py-4">{{ record.measure }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">{{ record.handleTime }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">{{ record.effect }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">{{ record.operator }}</td>
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>

    <!-- 处理预警模态框 -->
    <div v-if="handleModalVisible && selectedWarning"
      class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-xl w-full max-w-md mx-4 overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-bold">处理预警 - {{ selectedWarning.message }}</h3>
            <button class="text-gray-400 hover:text-gray-500" @click="closeHandleModal">
              <i class="fa fa-times"></i>
            </button>
          </div>
          <p class="text-sm text-gray-500 mt-2">请记录针对该病虫害预警的处理措施</p>
        </div>
        <div class="p-6">
          <form @submit.prevent="submitHandleWarning">
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-1">处理措施</label>
              <textarea v-model="handleForm.measure" rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="请详细描述处理措施..."></textarea>
            </div>
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-1">使用药剂</label>
              <input type="text" v-model="handleForm.pesticide"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="如：多菌灵可湿性粉剂">
            </div>
            <div class="grid grid-cols-2 gap-4 mb-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">用药浓度</label>
                <input type="text" v-model="handleForm.concentration"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="如：800倍稀释">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">处理时间</label>
                <input type="datetime-local" v-model="handleForm.handleTime"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
              </div>
            </div>
            <div class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-1">效果反馈</label>
              <select v-model="handleForm.effect"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                <option value="完全控制">完全控制</option>
                <option value="效果良好">效果良好</option>
                <option value="部分控制">部分控制</option>
                <option value="无明显效果">无明显效果</option>
              </select>
            </div>
            <div class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-1">备注</label>
              <input type="text" v-model="handleForm.remark"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="其他需要说明的信息">
            </div>
            <div class="flex space-x-3">
              <button type="button" class="flex-1 btn-outline" @click="closeHandleModal">
                取消
              </button>
              <button type="submit" class="flex-1 btn-primary">
                提交处理记录
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 新增种植计划模态框 -->
    <div v-if="addPlanModalVisible" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl w-full max-w-2xl mx-4 overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-bold">新增种植规划</h3>
            <button class="text-gray-400 hover:text-gray-500" @click="closeAddPlanModal">
              <i class="fa fa-times"></i>
            </button>
          </div>
        </div>
        <div class="p-6">
          <form @submit.prevent="submitNewPlan">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">规划名称</label>
                <input type="text" v-model="newPlanForm.planName"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="如：2024年晚稻种植">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">作物类型</label>
                <input v-model="newPlanForm.cropType" type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="如：水稻">
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">种植面积(亩)</label>
                <input v-model="newPlanForm.area" type="number" step="0.1"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入种植面积">
              </div>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">种植开始时间</label>
                <input v-model="newPlanForm.plantingTime" type="date"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">预计收获时间</label>
                <input v-model="newPlanForm.expectedHarvestTime" type="date"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
              </div>
            </div>
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-1">种植地块</label>
              <select name="landId" v-model="newPlanForm.landId"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                <option value="">请选择地块</option>
                <option v-for="land in lands" :key="land.id" :value="land.id">
                  {{ land.name }}
                </option>
              </select>
            </div>
            <div class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-1">备注说明</label>
              <textarea rows="3" v-model="newPlanForm.remark"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="请输入备注信息..."></textarea>
            </div>
            <div class="flex space-x-3">
              <button type="button" class="flex-1 btn-outline" @click="closeAddPlanModal">
                取消
              </button>
              <button type="submit" class="flex-1 btn-primary">
                保存种植规划
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 编辑种植计划模态框 -->
    <div v-if="editPlanModalVisible && selectedPlan"
      class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-xl w-full max-w-2xl mx-4 overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-bold">编辑种植规划</h3>
            <button @click="closeEditPlanModal" class="text-gray-400 hover:text-gray-500">
              <i class="fa fa-times"></i>
            </button>
          </div>
        </div>
        <div class="p-6">
          <form @submit.prevent="submitEditPlan">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">规划名称</label>
                <input v-model="editPlanForm.planName" type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="如：2024年晚稻种植">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">作物类型</label>
                <input v-model="editPlanForm.cropType" type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="如：水稻">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">种植面积(亩)</label>
                <input v-model="editPlanForm.area" type="number" step="0.1"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入种植面积">
              </div>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">种植开始时间</label>
                <input v-model="editPlanForm.plantingTime" type="date"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">预计收获时间</label>
                <input v-model="editPlanForm.expectedHarvestTime" type="date"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
              </div>
            </div>
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-1">种植地块</label>
              <select v-model="editPlanForm.landId" name="landId"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                <option value="">请选择地块</option>
                <option v-for="land in lands" :key="land.id" :value="land.id">
                  {{ land.name }}
                </option>
              </select>
            </div>
            <div class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-1">备注说明</label>
              <textarea v-model="editPlanForm.remark" rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="请输入备注信息..."></textarea>
            </div>
            <div class="flex justify-end space-x-3">
              <button type="button"
                class="w-1/3 rounded-lg border border-gray-500 bg-gray-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-gray-600 hover:shadow active:translate-y-0 active:shadow-sm"
                @click="closeEditPlanModal">
                取消
              </button>
              <button type="submit"
                class="w-1/3 rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm">
                保存修改
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 查看种植计划模态框 -->
    <div v-if="viewPlanModalVisible && selectedPlan"
      class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-xl w-full max-w-2xl mx-4 overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-bold">查看种植规划</h3>
            <button class="text-gray-400 hover:text-gray-500" @click="closeViewPlanModal">
              <i class="fa fa-times"></i>
            </button>
          </div>
        </div>
        <div class="p-6">
          <div class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">规划名称</label>
                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
                  selectedPlan.planName }}</div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">作物类型</label>
                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
                  selectedPlan.cropType }}</div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">种植面积(亩)</label>
                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
                  selectedPlan.area }} 亩</div>
              </div>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">种植开始时间</label>
                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
                  selectedPlan.plantingTime }}</div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">预计收获时间</label>
                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
                  selectedPlan.expectedHarvestTime }}</div>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">种植地块</label>
              <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
                getLandName(selectedPlan.landId) ||
                '未选择' }}</div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">备注说明</label>
              <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
                selectedPlan.remark ||
                '无备注' }}</div>
            </div>
          </div>
          <div class="mt-6 flex justify-end">
            <button type="button"
              class="w-1/3 rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm"
              @click="closeViewPlanModal">
              关闭
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 删除种植计划确认框 -->
    <div v-if="deletePlanModalVisible && selectedDeletePlan"
      class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-xl w-full max-w-sm mx-4 overflow-hidden shadow-xl">
        <div class="p-5 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-bold">确认删除</h3>
            <button class="text-gray-400 hover:text-gray-500" @click="closeDeletePlanModal">
              <i class="fa fa-times"></i>
            </button>
          </div>
        </div>
        <div class="p-5">
          <p class="text-center text-sm text-gray-600">
            是否确认删除「{{ selectedDeletePlan.planName }}」？
          </p>
          <div class="mt-6 flex justify-center space-x-3">
            <button type="button"
              class="w-1/3 rounded-lg border border-gray-500 bg-gray-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-gray-600 hover:shadow active:translate-y-0 active:shadow-sm"
              @click="closeDeletePlanModal">
              取消
            </button>
            <button type="button"
              class="w-1/3 rounded-lg border border-red-500 bg-red-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-red-600 hover:shadow active:translate-y-0 active:shadow-sm"
              @click="confirmDeletePlan">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useFarmStore } from "../composables/useFarmStore";

const { lands, devices, sensorReadings, environmentThresholds } = useFarmStore();

const sensorMetricLabels = {
  soil_moisture: '土壤湿度',
  air_temperature: '空气温度',
  air_humidity: '空气湿度',
  light: '光照强度',
  soil_ph: '土壤 pH'
};

const sensorStatusLabels = {
  no_data: '暂无数据',
  unmatched_data_type: '指标不匹配',
  low: '偏低',
  high: '偏高',
  normal: '适宜'
};

const deviceStatusLabels = {
  online: '在线',
  offline: '离线'
};

const sensorTrendRangeDays = {
  day: 1,
  week: 7,
  month: 30,
  season: 90
};

// Mock 数据先独立定义，再用于初始化响应式状态。
const mockPlans = [
  {
    id: 1,
    planName: '2024年早稻种植',
    cropType: '水稻',
    area: 15.2,
    plantingTime: '2024-03-15',
    expectedHarvestTime: '2024-07-20',
    status: '生长中',
    landId: 'LAND-001',
    remark: ''
  },
  {
    id: 2,
    planName: '2024年冬小麦种植',
    cropType: '小麦',
    area: 13.3,
    plantingTime: '2024-10-05',
    expectedHarvestTime: '2025-06-10',
    status: '播种期',
    landId: 'LAND-002',
    remark: ''
  }
];

const mockWarnings = [
  {
    id: 1,
    landId: 'LAND-001',
    level: 'red',
    cropType: '水稻',
    message: '检测到真菌病害高风险',
    suggestion: '建议尽快巡田并记录处理措施',
    handled: false
  },
  {
    id: 2,
    landId: 'LAND-002',
    level: 'yellow',
    cropType: '小麦',
    message: '土壤湿度连续偏高',
    suggestion: '建议检查排水情况',
    handled: false
  },
  {
    id: 3,
    landId: 'LAND-003',
    level: 'yellow',
    cropType: '玉米',
    message: '发现轻微虫害迹象',
    suggestion: '建议持续观察',
    handled: true
  }
];

const addPlanModalVisible = ref(false);
const editPlanModalVisible = ref(false);
const viewPlanModalVisible = ref(false);
const deletePlanModalVisible = ref(false);
const handleModalVisible = ref(false);
const selectedPlan = ref(null);
const selectedDeletePlan = ref(null);
const selectedTrendMetric = ref('all');
const activeSection = ref('dashboard');
const selectedWarning = ref(null);
const warningFilter = ref('all');
const recordKeyword = ref('');
const selectedLandId = ref(lands.value[0]?.id || '');
const sensorTrendRange = ref('day');

watch(selectedLandId, () => {
  selectedTrendMetric.value = 'all';
});

watch(sensorTrendRange, () => {
  selectedTrendMetric.value = 'all';
});

const newPlanForm = ref({
  planName: '',
  cropType: '',
  area: '',
  plantingTime: '',
  expectedHarvestTime: '',
  landId: '',
  remark: ''
});

const editPlanForm = ref({
  id: null,
  planName: '',
  cropType: '',
  area: '',
  plantingTime: '',
  expectedHarvestTime: '',
  landId: '',
  remark: ''
});

const handleForm = ref({
  measure: '',
  pesticide: '',
  concentration: '',
  handleTime: '',
  effect: '完全控制',
  remark: ''
});

const dashboardEnvironmentSummary = computed(() => {
  const soilMoisture = latestSensorReadings.value.soil_moisture;
  const airTemperature = latestSensorReadings.value.air_temperature;
  const airHumidity = latestSensorReadings.value.air_humidity;

  return {
    soilMoisture: {
      value: soilMoisture?.value ?? '--',
      unit: soilMoisture?.unit ?? '%'
    },
    airTemperature: {
      value: airTemperature?.value ?? '--',
      unit: airTemperature?.unit ?? '℃'
    },
    airHumidity: {
      value: airHumidity?.value ?? '--',
      unit: airHumidity?.unit ?? '%'
    }
  };
});

const plans = ref([...mockPlans]);
const warnings = ref([...mockWarnings]);

const filteredPlans = computed(() => {
  return plans.value.filter(plan => plan.landId === selectedLandId.value);
});

const totalArea = computed(() => {
  return filteredPlans.value.reduce((sum, plan) => {
    return sum + Number(plan.area || 0);
  }, 0);
});

const currentLand = computed(() => {
  return lands.value.find(land => land.id === selectedLandId.value);
});

const currentLandDevices = computed(() => {
  return devices.value.filter(device => device.landId === selectedLandId.value);
});

const currentLandOnlineDeviceCount = computed(() => {
  return currentLandDevices.value.filter(device => device.status === 'online').length;
});

const currentLandOfflineDeviceCount = computed(() => {
  return currentLandDevices.value.filter(device => device.status === 'offline').length;
});

const currentLandLowBatteryDeviceCount = computed(() => {
  return currentLandDevices.value.filter(device => device.battery !== null && device.battery < 20).length;
});

const currentLandThresholds = computed(() => environmentThresholds.value.filter(threshold => threshold.enabled && threshold.landId === selectedLandId.value));

const getReadingStatus = (reading, threshold) => {
  if (!reading || !Number.isFinite(reading.value)) {
    return 'no_data';
  }
  if (reading.metric !== threshold.metric) {
    return 'unmatched_data_type';
  }
  if (reading.value <= threshold.min) {
    return 'low';
  }
  if (reading.value >= threshold.max) {
    return 'high';
  }
  return 'normal';
};

const getSensorStatusClass = (status) => {
  const classes = {
    no_data: 'bg-gray-100 text-gray-600',
    unmatched_data_type: 'bg-purple-100 text-purple-700',
    low: 'bg-blue-100 text-blue-700',
    high: 'bg-red-100 text-red-700',
    normal: 'bg-green-100 text-green-700'
  };

  return classes[status] || 'bg-gray-100 text-gray-600';
};

const getDeviceStatusClass = (status) => {
  const classes = {
    online: 'bg-green-100 text-green-800',
    offline: 'bg-gray-100 text-gray-600'
  };

  return classes[status] || 'bg-gray-100 text-gray-600';
};

const formatRecordedAt = (recordedAt) => {
  if (!recordedAt) {
    return '暂无数据';
  }

  const recordedTime = new Date(recordedAt);
  if (!Number.isFinite(recordedTime.getTime())) {
    return '时间格式异常';
  }

  const parts = new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).formatToParts(recordedTime);

  const dateTime = Object.fromEntries(parts.map(part => [part.type, part.value]));

  return `${dateTime.year} 年 ${dateTime.month} 月 ${dateTime.day} 日 ${dateTime.hour}:${dateTime.minute}`;
};

const formatTrendPointTime = (recordedAt) => {
  if (!recordedAt) {
    return '';
  }

  const recordedTime = new Date(recordedAt);
  if (!Number.isFinite(recordedTime.getTime())) {
    return '';
  }

  const parts = new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).formatToParts(recordedTime);

  const dateTime = Object.fromEntries(parts.map(part => [part.type, part.value]));

  return `${dateTime.month} 月 ${dateTime.day} 日 ${dateTime.hour}:${dateTime.minute}`;
};

const getLandName = (landId) => {
  if (!landId) {
    return '';
  }

  return lands.value.find(land => land.id === landId)?.name || '未知土地';
};

const currentCrops = computed(() => {
  const crops = filteredPlans.value.map(plan => plan.cropType).filter(Boolean);
  return [...new Set(crops)].join('、') || '无';
});

const currentLandWarnings = computed(() => {
  return warnings.value.filter(warning => warning.landId === selectedLandId.value);
});

const currentLandSensorReadings = computed(() => sensorReadings.value.filter(reading => reading.landId === selectedLandId.value));

const unhandledWarningCount = computed(() => {
  return currentLandWarnings.value.filter(warning => !warning.handled).length;
});

const redWarningCount = computed(() => {
  return currentLandWarnings.value.filter(warning => warning.level === 'red' && !warning.handled).length;
});

const yellowWarningCount = computed(() => {
  return currentLandWarnings.value.filter(warning => warning.level === 'yellow' && !warning.handled).length;
});

const handledWarningCount = computed(() => {
  return currentLandWarnings.value.filter(warning => warning.handled).length;
});

const warningHandleRate = computed(() => {
  if (currentLandWarnings.value.length === 0) {
    return 0;
  }

  return Math.round((handledWarningCount.value / currentLandWarnings.value.length) * 100);
});

const filteredWarnings = computed(() => {
  if (warningFilter.value === 'handled') {
    return currentLandWarnings.value.filter(warning => warning.handled);
  }

  if (warningFilter.value === 'all') {
    return currentLandWarnings.value;
  }

  return currentLandWarnings.value.filter(warning => warning.level === warningFilter.value && !warning.handled);
});

const realtimeIndicators = computed(() => {
  return currentLandThresholds.value.map(threshold => {
    const reading = latestSensorReadings.value[threshold.metric];

    return {
      metric: threshold.metric,
      label: sensorMetricLabels[threshold.metric] || threshold.metric,
      value: reading?.value ?? null,
      unit: reading?.unit ?? '',
      recordedAt: reading?.recordedAt ?? null,
      min: threshold.min,
      max: threshold.max,
      status: getReadingStatus(reading, threshold)
    };
  });
});

const filteredTrendReadings = computed(() => {
  const days = sensorTrendRangeDays[sensorTrendRange.value];
  const startTime = Date.now() - days * 24 * 60 * 60 * 1000;

  return currentLandSensorReadings.value.filter(reading => {
    return new Date(reading.recordedAt).getTime() >= startTime;
  });
});

const trendReadingsByMetric = computed(() => {
  const result = filteredTrendReadings.value.reduce((groups, reading) => {
    if (!groups[reading.metric]) {
      groups[reading.metric] = [];
    }

    groups[reading.metric].push({
      time: reading.recordedAt,
      value: reading.value,
      unit: reading.unit,
      deviceId: reading.deviceId
    });

    return groups;
  }, {});

  Object.values(result).forEach(readings => {
    readings.sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime());
  });

  return result;
});

const trendMetricSummaries = computed(() => {
  return Object.entries(trendReadingsByMetric.value).map(([metric, readings]) => {
    const firstReading = readings[0];
    const latestReading = readings[readings.length - 1];

    return {
      metric,
      label: sensorMetricLabels[metric] || metric,
      unit: latestReading?.unit || firstReading?.unit || '',
      count: readings.length,
      latestValue: latestReading?.value ?? null,
      points: readings.map(reading => ({
        time: reading.time,
        value: reading.value
      }))
    };
  });
});

const trendMetricOptions = computed(() => {
  return [
    { value: 'all', label: '全部指标' },
    ...trendMetricSummaries.value.map(summary => ({
      value: summary.metric,
      label: summary.label
    }))
  ];
});

const visibleTrendMetricSummaries = computed(() => {
  if (selectedTrendMetric.value === 'all') {
    return trendMetricSummaries.value;
  }
  return trendMetricSummaries.value.filter(summary => summary.metric === selectedTrendMetric.value);
});

const exportableTrendReadings = computed(() => {
  if (selectedTrendMetric.value === 'all') {
    return filteredTrendReadings.value;
  }

  return filteredTrendReadings.value.filter(reading => reading.metric === selectedTrendMetric.value);
});

const handleRecords = computed(() => {
  return currentLandWarnings.value.filter(warning => warning.handled && warning.handleRecord)
    .map(warning => ({
      id: warning.id,
      cropType: warning.cropType,
      level: warning.level,
      measure: warning.handleRecord.measure,
      handleTime: warning.handleRecord.handleTime,
      effect: warning.handleRecord.effect,
      operator: '农场主'
    }));
});

const filteredHandleRecords = computed(() => {
  const keyword = recordKeyword.value.trim();
  if (!keyword) {
    return handleRecords.value;
  }

  return handleRecords.value.filter(record => {
    return String(record.id).includes(keyword) || record.cropType.includes(keyword);
  });
});

const latestSensorReadings = computed(() => {
  return currentLandSensorReadings.value.reduce((result, reading) => {
    const previousReading = result[reading.metric];
    if (!previousReading || new Date(reading.recordedAt).getTime() > new Date(previousReading.recordedAt).getTime()) {
      result[reading.metric] = reading;
    }
    return result;
  }, {});
});

const currentLandSensorRows = computed(() => {
  return currentLandDevices.value.map(device => {
    const latestReadings = Object.values(latestSensorReadings.value).filter(reading => reading.deviceId === device.id);

    const latestUpdatedAt = latestReadings.reduce((latestTime, reading) => {
      if (!latestTime) {
        return reading.recordedAt;
      }

      return new Date(reading.recordedAt).getTime() > new Date(latestTime).getTime() ? reading.recordedAt : latestTime;
    }, null);

    return {
      id: device.id,
      name: device.name,
      location: getLandName(device.landId),
      metrics: latestReadings.map(reading => sensorMetricLabels[reading.metric] || reading.metric).join('、') || '暂无数据',
      latestData: latestReadings.map(reading => `${sensorMetricLabels[reading.metric] || reading.metric}: ${reading.value} ${reading.unit}`).join('，') || '暂无数据',
      updatedAt: latestUpdatedAt,
      status: device.status
    };
  });
});

const resetNewPlanForm = () => {
  newPlanForm.value = {
    planName: '',
    cropType: '',
    area: '',
    plantingTime: '',
    expectedHarvestTime: '',
    landId: '',
    remark: ''
  };
};

const resetHandleForm = () => {
  handleForm.value = {
    measure: '',
    pesticide: '',
    concentration: '',
    handleTime: '',
    effect: '完全控制',
    remark: ''
  };
};

const resetEditPlanForm = () => {
  editPlanForm.value = {
    id: null,
    planName: '',
    cropType: '',
    area: '',
    plantingTime: '',
    expectedHarvestTime: '',
    landId: '',
    remark: ''
  };
};

const closeAddPlanModal = () => {
  resetNewPlanForm();
  addPlanModalVisible.value = false;
};

const closeHandleModal = () => {
  resetHandleForm();
  selectedWarning.value = null;
  handleModalVisible.value = false;
};

const closeEditPlanModal = () => {
  resetEditPlanForm();
  selectedPlan.value = null;
  editPlanModalVisible.value = false;
};

const closeViewPlanModal = () => {
  selectedPlan.value = null;
  viewPlanModalVisible.value = false;
};

const closeDeletePlanModal = () => {
  selectedDeletePlan.value = null;
  deletePlanModalVisible.value = false;
};

const validatePlanForm = (form) => {
  if (!form.planName.trim()) {
    alert('请输入规划名称');
    return false;
  }

  if (!form.cropType.trim()) {
    alert('请输入作物类型');
    return false;
  }

  if (!form.area) {
    alert('请输入种植面积');
    return false;
  }

  if (!form.plantingTime) {
    alert('请选择种植开始时间');
    return false;
  }

  if (!form.expectedHarvestTime) {
    alert('请选择预计收获时间');
    return false;
  }

  if (form.expectedHarvestTime < form.plantingTime) {
    alert('预计收获时间不能早于种植开始时间');
    return false;
  }

  if (!form.landId) {
    alert('请选择种植地块');
    return false;
  }

  return true;
};

const submitNewPlan = () => {
  const form = newPlanForm.value;

  if (!validatePlanForm(form)) {
    return;
  }

  plans.value.push({
    id: Date.now(),
    planName: form.planName,
    cropType: form.cropType,
    area: form.area,
    plantingTime: form.plantingTime,
    expectedHarvestTime: form.expectedHarvestTime,
    status: '待开始',
    landId: form.landId,
    remark: form.remark
  });

  closeAddPlanModal();
};

const openDeletePlanModal = (plan) => {
  selectedDeletePlan.value = plan;
  deletePlanModalVisible.value = true;
};

const confirmDeletePlan = () => {
  if (!selectedDeletePlan.value) {
    return;
  }

  plans.value = plans.value.filter(plan => plan.id !== selectedDeletePlan.value.id);
  closeDeletePlanModal();
};

const viewPlan = (plan) => {
  selectedPlan.value = plan;
  viewPlanModalVisible.value = true;
};

const editPlan = (plan) => {
  selectedPlan.value = plan;
  editPlanForm.value = { ...plan };
  editPlanModalVisible.value = true;
};

const submitEditPlan = () => {
  const form = editPlanForm.value;

  if (!validatePlanForm(form)) {
    return;
  }

  const index = plans.value.findIndex(plan => plan.id === form.id);

  if (index === -1) {
    return;
  }

  plans.value[index] = {
    ...plans.value[index],
    ...form
  };

  closeEditPlanModal();
};

const handleWarning = (warning) => {
  selectedWarning.value = warning;
  handleModalVisible.value = true;
};

const submitHandleWarning = () => {
  if (!selectedWarning.value) {
    return;
  }

  const index = warnings.value.findIndex(warning => selectedWarning.value.id === warning.id);

  if (index === -1) {
    return;
  }

  warnings.value[index] = {
    ...warnings.value[index],
    handled: true,
    handleRecord: {
      ...handleForm.value
    }
  };

  closeHandleModal();
};

const getWarningLevelText = (level) => {
  const levelMap = {
    red: '红色预警',
    yellow: '黄色预警',
    green: '已处理'
  };

  return levelMap[level] || '未知预警';
};

const getWarningLevelClass = (level) => {
  const classMap = {
    red: 'text-red-600 bg-red-50',
    yellow: 'text-yellow-600 bg-yellow-50',
    green: 'text-green-600 bg-green-50'
  };

  return classMap[level] || 'text-gray-600 bg-gray-50';
};

const getWarningButtonClass = (level) => {
  const classMap = {
    red: 'border border-red-500 bg-red-500 hover:bg-red-600',
    yellow: 'border border-yellow-500 bg-yellow-500 hover:bg-yellow-600',
    green: 'border border-green-500 bg-green-500 hover:bg-green-600'
  };

  return classMap[level] || 'border border-gray-500 bg-gray-500 hover:bg-gray-600';
};

const exportSensorTrendData = () => {
  if (exportableTrendReadings.value.length === 0) {
    window.alert('当前筛选条件下没有可导出的传感器数据');
    return;
  }

  const exportData = {
    landId: selectedLandId.value,
    landName: currentLand.value?.name || '',
    range: sensorTrendRange.value,
    metric: selectedTrendMetric.value,
    exportedAt: new Date().toISOString(),
    readings: exportableTrendReadings.value
  };

  const content = JSON.stringify(exportData, null, 2);
  const blob = new Blob([content], { type: 'application/json;charset=utf-8' });
  const url = URL.createObjectURL(blob);

  const link = document.createElement('a');
  link.href = url;
  link.download = `sensor-readings-${selectedLandId.value}-${sensorTrendRange.value}.json`;
  link.click();

  URL.revokeObjectURL(url);
};
</script>
