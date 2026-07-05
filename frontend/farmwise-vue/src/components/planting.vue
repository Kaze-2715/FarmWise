<template>
    <div>
        <div class="container mx-auto px-4 pt-24 pb-16 flex flex-col md:flex-row gap-6">
            <!-- 左侧导航 -->
            <aside class="md:w-64 bg-white rounded-xl card-shadow p-4 flex-shrink-0">
                <nav class="space-y-1">
                    <button type="button" @click="activeSection = 'dashboard'" class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full" :class="{ 'nav-active': activeSection === 'dashboard' }">
                        <i class="fa fa-dashboard"></i>
                        <span>数据总览</span>
                    </button>
                    <button type="button" @click="activeSection = 'planting-plan'"
                        class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
                        :class="{ 'nav-active': activeSection === 'planting-plan' }">
                        <i class="fa fa-calendar-check-o"></i>
                        <span>种植规划</span>
                    </button>
                    <button type="button" @click="activeSection = 'environment-monitor'"
                        class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
                        :class="{ 'nav-active': activeSection === 'environment-monitor' }">
                        <i class="fa fa-thermometer-half"></i>
                        <span>环境监控</span>
                    </button>
                    <button type="button" @click="activeSection = 'pest-warning'"
                        class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
                        :class="{ 'nav-active': activeSection === 'pest-warning' }">
                        <i class="fa fa-exclamation-triangle text-danger"></i>
                        <span>病虫害预警</span>
                        <span class="ml-auto bg-danger text-white text-xs px-2 py-1 rounded-full">{{ unhandledWarningCount }}</span>
                    </button>
                    <button type="button" @click="activeSection = 'handle-record'"
                        class="flex items-center space-x-3 px-4 py-3 rounded-lg w-full hover:bg-gray-50 transition-colors"
                        :class="{ 'nav-active': activeSection === 'handle-record' }">
                        <i class="fa fa-list-alt"></i>
                        <span>处理记录</span>
                    </button>
                </nav>

                <!-- 农场信息卡片 -->
                <div class="mt-8 bg-primary/5 rounded-xl p-4">
                    <h3 class="font-medium text-primary mb-3">农场信息</h3>
                    <div class="space-y-2 text-sm">
                        <div class="flex justify-between">
                            <span class="text-gray-600">种植面积</span>
                            <span class="font-medium">{{ totalPlantingArea }} 亩</span>
                        </div>
                        <div class="flex justify-between">
                            <span class="text-gray-600">当前作物</span>
                            <span class="font-medium">{{ currentCrops }}</span>
                        </div>
                        <div class="flex justify-between">
                            <span class="text-gray-600">传感器数量</span>
                            <span class="font-medium">8 个</span>
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
                            <button class="btn-outline text-sm px-3 py-1.5">
                                <i class="fa fa-refresh mr-1"></i> 刷新
                            </button>
                        </div>
                    </div>

                    <!-- 数据卡片组 -->
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
                        <!-- 土壤湿度 -->
                        <div class="bg-blue-50 rounded-xl p-4 border border-blue-100">
                            <div class="flex justify-between items-start">
                                <div>
                                    <p class="text-gray-500 text-sm">土壤湿度</p>
                                    <h3 class="text-2xl font-bold mt-1">{{ environmentData.humidity }}%</h3>
                                </div>
                                <div
                                    class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-500">
                                    <i class="fa fa-tint"></i>
                                </div>
                            </div>
                            <div class="mt-3 flex items-center text-sm">
                                <span class="text-red-500 flex items-center">
                                    <i class="fa fa-arrow-up mr-1"></i> 3.2%
                                </span>
                                <span class="text-gray-400 ml-2">较昨日</span>
                            </div>
                        </div>

                        <!-- 土壤温度 -->
                        <div class="bg-orange-50 rounded-xl p-4 border border-orange-100">
                            <div class="flex justify-between items-start">
                                <div>
                                    <p class="text-gray-500 text-sm">土壤温度</p>
                                    <h3 class="text-2xl font-bold mt-1">{{ environmentData.temperature }}℃</h3>
                                </div>
                                <div
                                    class="w-10 h-10 rounded-full bg-orange-100 flex items-center justify-center text-orange-500">
                                    <i class="fa fa-thermometer"></i>
                                </div>
                            </div>
                            <div class="mt-3 flex items-center text-sm">
                                <span class="text-red-500 flex items-center">
                                    <i class="fa fa-arrow-up mr-1"></i> 1.5℃
                                </span>
                                <span class="text-gray-400 ml-2">较昨日</span>
                            </div>
                        </div>

                        <!-- 产量预测 -->
                        <div class="bg-green-50 rounded-xl p-4 border border-green-100">
                            <div class="flex justify-between items-start">
                                <div>
                                    <p class="text-gray-500 text-sm">产量预测</p>
                                    <h3 class="text-2xl font-bold mt-1">
                                        <template v-if="dashboardData.yieldPrediction.value !== null">
                                            {{ dashboardData.yieldPrediction.value }} {{ dashboardData.yieldPrediction.unit }}
                                        </template>
                                        <template v-else>
                                            待生成
                                        </template>
                                    </h3>
                                </div>
                                <div
                                    class="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center text-green-500">
                                    <i class="fa fa-bar-chart"></i>
                                </div>
                            </div>
                            <div class="mt-3 flex items-center text-sm">
                                <span v-if="dashboardData.yieldPrediction.trendRate !== null" class="text-green-500 flex items-center">
                                    <i class="fa fa-arrow-up mr-1"></i> {{ dashboardData.yieldPrediction.trendRate }}%
                                </span>
                                <span v-else class="text-gray-500 flex items-center">
                                    <i class="fa fa-clock-o mr-1"></i> 后端接入后生成
                                </span>
                                <span v-if="dashboardData.yieldPrediction.trendRate !== null" class="text-gray-400 ml-2">较上季</span>
                            </div>
                        </div>

                        <!-- 预警数量 -->
                        <div class="bg-red-50 rounded-xl p-4 border border-red-100">
                            <div class="flex justify-between items-start">
                                <div>
                                    <p class="text-gray-500 text-sm">未处理预警</p>
                                    <h3 class="text-2xl font-bold mt-1">{{ unhandledWarningCount }} 条</h3>
                                </div>
                                <div
                                    class="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center text-red-500">
                                    <i class="fa fa-bell"></i>
                                </div>
                            </div>
                            <div class="mt-3 flex items-center text-sm">
                                <span class="text-red-500 flex items-center">
                                    <i class="fa fa-arrow-up mr-1"></i> 1 条
                                </span>
                                <span class="text-gray-400 ml-2">较昨日</span>
                            </div>
                        </div>
                    </div>

                    <!-- 趋势图表 -->
                    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        <div class="bg-gray-50 rounded-xl p-4">
                            <h3 class="font-medium mb-4">环境数据趋势（7天）</h3>
                            <div class="h-64 flex items-center justify-center text-sm text-gray-400 border border-dashed border-gray-200 rounded-lg">
                                图表待接入
                            </div>
                        </div>
                        <div class="bg-gray-50 rounded-xl p-4">
                            <h3 class="font-medium mb-4">作物生长状态</h3>
                            <div class="h-64 flex items-center justify-center text-sm text-gray-400 border border-dashed border-gray-200 rounded-lg">
                                图表待接入
                            </div>
                        </div>
                    </div>
                </section>

                <!-- 种植规划 -->
                <section v-if="activeSection === 'planting-plan'" class="bg-white rounded-xl card-shadow p-6 ">
                    <div class="flex justify-between items-center mb-6">
                        <h2 class="text-xl font-bold">种植规划</h2>
                        <button class="btn-primary" @click="addPlanModalVisible = true">
                            <i class="fa fa-plus mr-1"></i> 新增规划
                        </button>
                    </div>

                    <!-- 种植计划表格 -->
                    <div class="overflow-x-auto">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead>
                                <tr>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        规划名称</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        作物类型</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        种植面积(亩)</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        种植时间</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        状态</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        操作</th>
                                </tr>
                            </thead>
                            <tbody class="bg-white divide-y divide-gray-200">
                                <tr v-for="plan in plans" :key="plan.id">
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm font-medium">{{ plan.planName }}</div>
                                    </td>

                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm">{{ plan.cropType }}</div>
                                    </td>

                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm">{{ plan.plantingArea }}</div>
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
                                        <button type="button" @click="editPlan(plan)" class="text-primary hover:text-primary/80 mr-3">编辑</button>
                                        <button type="button" @click="viewPlan(plan)" class="text-gray-500 hover:text-gray-700">查看</button>
                                        <button type="button" @click="deletePlan(plan.id)" class="text-red-500 hover:text-red-700">删除</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                <!-- 环境监控 -->
                <section v-if="activeSection === 'environment-monitor'"
                    class="bg-white rounded-xl card-shadow p-6">
                    <div class="flex justify-between items-center mb-6">
                        <h2 class="text-xl font-bold">环境监控</h2>
                        <div class="flex space-x-2">
                            <div class="relative">
                                <select
                                    class="appearance-none bg-gray-50 border border-gray-300 text-gray-700 py-2 pl-3 pr-8 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
                                    <option>全部土地</option>
                                    <option>1号地块</option>
                                    <option>2号地块</option>
                                    <option>3号地块</option>
                                </select>
                                <div
                                    class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                                    <i class="fa fa-chevron-down text-xs"></i>
                                </div>
                            </div>
                            <button class="btn-outline text-sm px-3 py-1.5">
                                <i class="fa fa-download mr-1"></i> 导出数据
                            </button>
                        </div>
                    </div>

                    <!-- 实时数据卡片 -->
                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                        <div class="border border-gray-200 rounded-xl p-4">
                            <div class="flex justify-between items-center mb-3">
                                <h3 class="font-medium">土壤湿度</h3>
                                <span class="text-xs text-gray-500">{{ environmentData.humidityUpdateTime }}</span>
                            </div>
                            <div class="flex items-center">
                                <div class="text-3xl font-bold mr-3">{{ environmentData.humidity }}%</div>
                                <div
                                    class="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-500">
                                    <i class="fa fa-tint"></i>
                                </div>
                            </div>
                            <div class="flex justify-between text-xs text-gray-500 mt-2">
                                <span>适宜范围: 60%-80%</span>
                                <span class="text-red-500">略高</span>
                            </div>
                        </div>

                        <div class="border border-gray-200 rounded-xl p-4">
                            <div class="flex justify-between items-center mb-3">
                                <h3 class="font-medium">土壤温度</h3>
                                <span class="text-xs text-gray-500">{{ environmentData.temperatureUpdateTime }}</span>
                            </div>
                            <div class="flex items-center">
                                <div class="text-3xl font-bold mr-3">{{ environmentData.temperature }}℃</div>
                                <div
                                    class="h-10 w-10 rounded-full bg-orange-100 flex items-center justify-center text-orange-500">
                                    <i class="fa fa-thermometer"></i>
                                </div>
                            </div>
                            <div class="flex justify-between text-xs text-gray-500 mt-2">
                                <span>适宜范围: 15℃-30℃</span>
                                <span class="text-green-500">适宜</span>
                            </div>
                        </div>

                        <div class="border border-gray-200 rounded-xl p-4">
                            <div class="flex justify-between items-center mb-3">
                                <h3 class="font-medium">土壤pH值</h3>
                                <span class="text-xs text-gray-500">{{ environmentData.phUpdateTime }}</span>
                            </div>
                            <div class="flex items-center">
                                <div class="text-3xl font-bold mr-3">{{ environmentData.ph }}</div>
                                <div
                                    class="h-10 w-10 rounded-full bg-purple-100 flex items-center justify-center text-purple-500">
                                    <i class="fa fa-balance-scale"></i>
                                </div>
                            </div>
                            <div class="flex justify-between text-xs text-gray-500 mt-2">
                                <span>适宜范围: 6.0-7.5</span>
                                <span class="text-green-500">适宜</span>
                            </div>
                        </div>
                    </div>

                    <!-- 历史趋势图表 -->
                    <div class="bg-gray-50 rounded-xl p-4 mb-6">
                        <div class="flex justify-between items-center mb-4">
                            <h3 class="font-medium">历史趋势</h3>
                            <div class="flex space-x-2">
                                <button class="text-sm px-3 py-1 bg-primary text-white rounded-lg">日</button>
                                <button class="text-sm px-3 py-1 bg-gray-200 text-gray-700 rounded-lg">周</button>
                                <button class="text-sm px-3 py-1 bg-gray-200 text-gray-700 rounded-lg">月</button>
                                <button class="text-sm px-3 py-1 bg-gray-200 text-gray-700 rounded-lg">季</button>
                            </div>
                        </div>
                        <div class="h-80 flex items-center justify-center text-sm text-gray-400 border border-dashed border-gray-200 rounded-lg">
                            历史趋势图待接入
                        </div>
                    </div>

                    <!-- 传感器状态表格 -->
                    <div class="overflow-x-auto">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead>
                                <tr>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        传感器编号</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        安装位置</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        监测类型</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        最新数据</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        更新时间</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        状态</th>
                                </tr>
                            </thead>
                            <tbody class="bg-white divide-y divide-gray-200">
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm">SN-2024001</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm">1号地块</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm">土壤湿度、温度</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm">湿度: {{ environmentData.humidity }}% 温度: {{ environmentData.temperature }}℃</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm">2024-05-20 14:32:15</td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <span
                                            class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                            正常
                                        </span>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                <!-- 病虫害预警 -->
                <section v-if="activeSection === 'pest-warning'"
                    class="bg-white rounded-xl card-shadow p-6">
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
                                <div
                                    class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
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
                                <div
                                    class="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center text-red-500">
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
                                <div
                                    class="w-12 h-12 rounded-full bg-yellow-100 flex items-center justify-center text-yellow-500">
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
                                <div
                                    class="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center text-green-500">
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
                            <button type="button" v-if="!warning.handled" class="mt-3 btn-danger text-sm" @click="handleWarning(warning)">处理预警</button>
                        </div>
                    </div>
                </section>

                <!-- 处理记录 -->
                <section v-if="activeSection === 'handle-record'"
                    class="bg-white rounded-xl card-shadow p-6">
                    <div class="flex justify-between items-center mb-6">
                        <h2 class="text-xl font-bold">预警处理记录</h2>
                        <div class="relative">
                            <input v-model="recordKeyword" type="text" placeholder="搜索预警编号/作物类型"
                                class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 w-64">
                            <i
                                class="fa fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
                        </div>
                    </div>

                    <!-- 处理记录表格 -->
                    <div class="overflow-x-auto">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead>
                                <tr>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        记录ID</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        作物类型</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        预警类型</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        处理措施</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        处理时间</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        处理效果</th>
                                    <th
                                        class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
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
            class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
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
                                <input v-model="newPlanForm.plantingArea" type="number" step="0.1"
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
                            <select name="land" v-model="newPlanForm.land"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                                <option value="">请选择地块</option>
                                <option v-for="land in mockLandOptions" :key="land.id" :value="land.name">
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
            class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
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
                                <input v-model="editPlanForm.plantingArea" type="number" step="0.1"
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
                            <select v-model="editPlanForm.land" name="land"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
                                <option value="">请选择地块</option>
                                <option v-for="land in mockLandOptions" :key="land.id" :value="land.name">
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
                        <div class="flex space-x-3">
                            <button type="button" class="flex-1 btn-outline" @click="closeEditPlanModal">
                                取消
                            </button>
                            <button type="submit" class="flex-1 btn-primary">
                                保存修改
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- 查看种植计划模态框 -->
        <div v-if="viewPlanModalVisible && selectedPlan"
            class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
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
                                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{ selectedPlan.planName }}</div>
                            </div>
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">作物类型</label>
                                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{ selectedPlan.cropType }}</div>
                            </div>
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">种植面积(亩)</label>
                                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{ selectedPlan.plantingArea }} 亩</div>
                            </div>
                        </div>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">种植开始时间</label>
                                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{ selectedPlan.plantingTime }}</div>
                            </div>
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">预计收获时间</label>
                                <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{ selectedPlan.expectedHarvestTime }}</div>
                            </div>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">种植地块</label>
                            <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{ selectedPlan.land || '未选择' }}</div>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">备注说明</label>
                            <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{ selectedPlan.remark || '无备注' }}</div>
                        </div>
                    </div>
                    <div class="mt-6 flex justify-end">
                        <button type="button" class="btn-primary" @click="closeViewPlanModal">
                            关闭
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from "vue";

// Mock 数据先独立定义，再用于初始化响应式状态。
const mockPlans = [
    {
        id: 1,
        planName: '2024年早稻种植',
        cropType: '水稻',
        plantingArea: 15.2,
        plantingTime: '2024-03-15',
        expectedHarvestTime: '2024-07-20',
        status: '生长中',
        land: '',
        remark: ''
    },
    {
        id: 2,
        planName: '2024年冬小麦种植',
        cropType: '小麦',
        plantingArea: 13.3,
        plantingTime: '2024-10-05',
        expectedHarvestTime: '2025-06-10',
        status: '播种期',
        land: '',
        remark: ''
    }
];

const mockWarnings = [
    {
        id: 1,
        level: 'red',
        cropType: '水稻',
        message: '检测到真菌病害高风险',
        suggestion: '建议尽快巡田并记录处理措施',
        handled: false
    },
    {
        id: 2,
        level: 'yellow',
        cropType: '小麦',
        message: '土壤湿度连续偏高',
        suggestion: '建议检查排水情况',
        handled: false
    },
    {
        id: 3,
        level: 'yellow',
        cropType: '玉米',
        message: '发现轻微虫害迹象',
        suggestion: '建议持续观察',
        handled: true
    }
];

const mockEnvironmentData = {
    humidity: 78.5,
    temperature: 27.8,
    ph: 6.8,
    humidityUpdateTime: '1分钟前更新',
    temperatureUpdateTime: '2分钟前更新',
    phUpdateTime: '5分钟前更新'
};

const mockDashboardData = {
    yieldPrediction: {
        value: null,
        unit: '吨',
        trendRate: null,
        status: 'pending'
    }
};

const mockLandOptions = [
    { id: 1, name: '1号地块' },
    { id: 2, name: '2号地块' },
    { id: 3, name: '3号地块' }
];

const addPlanModalVisible = ref(false);
const editPlanModalVisible = ref(false);
const viewPlanModalVisible = ref(false);
const handleModalVisible = ref(false);
const selectedPlan = ref(null);
const activeSection = ref('dashboard');
const selectedWarning = ref(null);
const warningFilter = ref('all');
const recordKeyword = ref('');

const newPlanForm = ref({
    planName: '',
    cropType: '',
    plantingArea: '',
    plantingTime: '',
    expectedHarvestTime: '',
    land: '',
    remark: ''
});

const editPlanForm = ref({
    id: null,
    planName: '',
    cropType: '',
    plantingArea: '',
    plantingTime: '',
    expectedHarvestTime: '',
    land: '',
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

const environmentData = ref({ ...mockEnvironmentData });
const dashboardData = ref({ ...mockDashboardData });

const plans = ref([...mockPlans]);
const warnings = ref([...mockWarnings]);

const totalPlantingArea = computed(() => {
    return plans.value.reduce((sum, plan) => {
        return sum + Number(plan.plantingArea || 0);
    }, 0);
});

const currentCrops = computed(() => {
    const crops = plans.value.map(plan => plan.cropType).filter(Boolean);
    return [...new Set(crops)].join('、') || '无';
});

const unhandledWarningCount = computed(() => {
    return warnings.value.filter(warning => !warning.handled).length;
});

const redWarningCount = computed(() => {
    return warnings.value.filter(warning => warning.level === 'red' && !warning.handled).length;
});

const yellowWarningCount = computed(() => {
    return warnings.value.filter(warning => warning.level === 'yellow' && !warning.handled).length;
});

const handledWarningCount = computed(() => {
    return warnings.value.filter(warning => warning.handled).length;
});

const warningHandleRate = computed(() => {
    if (warnings.value.length === 0) {
        return 0;
    }

    return Math.round((handledWarningCount.value / warnings.value.length) * 100);
});

const filteredWarnings = computed(() => {
    if (warningFilter.value === 'handled') {
        return warnings.value.filter(warning => warning.handled);
    }

    if (warningFilter.value === 'all') {
        return warnings.value;
    }

    return warnings.value.filter(warning => warning.level === warningFilter.value && !warning.handled);
});

const handleRecords = computed(() => {
    return warnings.value.filter(warning => warning.handled && warning.handleRecord)
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

const resetNewPlanForm = () => {
    newPlanForm.value = {
        planName: '',
        cropType: '',
        plantingArea: '',
        plantingTime: '',
        expectedHarvestTime: '',
        land: '',
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
        plantingArea: '',
        plantingTime: '',
        expectedHarvestTime: '',
        land: '',
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

const validatePlanForm = (form) => {
    if (!form.planName.trim()) {
        alert('请输入规划名称');
        return false;
    }

    if (!form.cropType.trim()) {
        alert('请输入作物类型');
        return false;
    }

    if (!form.plantingArea) {
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

    if (!form.land) {
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
        plantingArea: form.plantingArea,
        plantingTime: form.plantingTime,
        expectedHarvestTime: form.expectedHarvestTime,
        status: '待开始',
        land: form.land,
        remark: form.remark
    });

    closeAddPlanModal();
};

const deletePlan = (id) => {
    plans.value = plans.value.filter(plan => plan.id !== id);
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
</script>
