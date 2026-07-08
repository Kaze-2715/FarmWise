<template>
    <header class="fixed w-full top-0 z-50 transition-all duration-300 bg-transparent">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex justify-between items-center py-4">
                <!-- Logo -->
                <a href="#home" class="flex items-center space-x-2">
                    <i class="fa fa-leaf text-green-500 text-2xl"></i>
                    <span class="text-xl font-bold text-white drop-shadow-md">FarmWise</span>
                </a>

                <!-- 桌面端导航 -->
                <nav class="hidden md:flex items-center space-x-8">
                    <a href="#home"
                        class="text-white hover:text-amber-500 transition-colors duration-300 font-medium">首页</a>
                    <a href="#features"
                        class="text-white hover:text-amber-500 transition-colors duration-300 font-medium">功能</a>
                    <a href="#contact"
                        class="text-white hover:text-amber-500 transition-colors duration-300 font-medium">联系我们</a>
                </nav>

                <!-- 登录/注册按钮 -->
                <div class="hidden md:flex items-center space-x-4">
                    <router-link to="/login"
                        class="inline-flex items-center justify-center rounded-full border-2 border-white px-6 py-2 font-medium text-white transition-all duration-300 hover:bg-white hover:text-green-500">登录</router-link>
                    <router-link to="/register" class="inline-flex items-center justify-center rounded-full bg-green-500 px-6 py-2 font-medium text-white shadow-md transition-all duration-300 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow-lg">免费注册</router-link>
                </div>
            </div>
        </div>
    </header>
    <!-- 英雄区 -->
    <section id="home" class="relative h-screen flex items-center justify-center overflow-hidden">
        <!-- 背景图 -->
        <div class="absolute inset-0 z-0">
            <img src="https://picsum.photos/id/1059/1920/1080" alt="智慧农业背景" class="w-full h-full object-cover">
            <div class="absolute inset-0 bg-gradient-to-r from-gray-900/70 to-gray-900/50"></div>
        </div>

        <!-- 内容 -->
        <div class="container mx-auto px-4 sm:px-6 lg:px-8 relative z-10 text-center">
            <h1
                class="text-[clamp(2.5rem,5vw,4rem)] font-bold text-white leading-tight drop-shadow-md mb-6">
                科技赋能 <span class="text-green-500">智慧农业</span>
            </h1>
            <p class="text-[clamp(1rem,2vw,1.25rem)] text-gray-100 max-w-3xl mx-auto mb-10 leading-relaxed">
                基于物联网、大数据和AI技术，为农业生产提供全方位智能解决方案，提升产量、降低成本、绿色环保
            </p>
            <div class="flex flex-col sm:flex-row justify-center gap-4 mb-16">
                <router-link to="/login" class="inline-flex items-center justify-center rounded-full bg-green-500 px-8 py-3 text-lg font-medium text-white shadow-md transition-all duration-300 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow-lg">
                    立即体验 <i class="fa fa-arrow-right ml-2"></i>
                </router-link>
            </div>

            <!-- 滚动提示 -->
            <div class="absolute bottom-10 left-1/2 transform -translate-x-1/2 animate-bounce">
                <a href="#features" class="text-white text-2xl">
                    <i class="fa fa-angle-down"></i>
                </a>
            </div>
        </div>
    </section>

    <!-- 功能区 -->
    <section id="features" class="py-20 bg-white">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8">
            <div class="text-center mb-16">
                <h2 class="text-[clamp(1.8rem,3vw,2.5rem)] font-bold text-gray-800 mb-4">核心功能</h2>
                <p class="text-gray-600 max-w-2xl mx-auto text-lg">
                    全方位覆盖农业生产全流程，用科技助力农业现代化转型
                </p>
                <div class="w-24 h-1 bg-green-500 mx-auto mt-6 rounded-full"></div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                <!-- 功能卡片1 -->
                <div class="bg-gray-50 rounded-2xl p-8 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
                    <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
                        <i class="fa fa-tint text-green-500 text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">智能灌溉</h3>
                    <p class="text-gray-600 mb-6">
                        基于土壤湿度传感器和天气预报，自动调节灌溉量和时间，节水30%以上
                    </p>
                </div>

                <!-- 功能卡片2 -->
                <div class="bg-gray-50 rounded-2xl p-8 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
                    <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
                        <i class="fa fa-thermometer-half text-green-500 text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">环境监测</h3>
                    <p class="text-gray-600 mb-6">
                        实时监测温度、湿度、光照、CO2浓度等环境数据，异常情况及时预警
                    </p>
                </div>

                <!-- 功能卡片3 -->
                <div class="bg-gray-50 rounded-2xl p-8 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
                    <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
                        <i class="fa fa-bar-chart text-green-500 text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">数据分析</h3>
                    <p class="text-gray-600 mb-6">
                        大数据分析作物生长规律，提供精准种植建议，优化生产决策
                    </p>
                </div>

                <!-- 功能卡片4 -->
                <div class="bg-gray-50 rounded-2xl p-8 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
                    <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
                        <i class="fa fa-bug text-green-500 text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">病虫害预警</h3>
                    <p class="text-gray-600 mb-6">
                        AI图像识别技术，快速检测病虫害，提供科学防治方案
                    </p>
                </div>

                <!-- 功能卡片5 -->
                <div class="bg-gray-50 rounded-2xl p-8 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
                    <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
                        <i class="fa fa-map-marker text-green-500 text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">地块管理</h3>
                    <p class="text-gray-600 mb-6">
                        数字化管理农田信息，记录种植历史，一键生成农事报告
                    </p>
                </div>

                <!-- 功能卡片6 -->
                <div class="bg-gray-50 rounded-2xl p-8 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
                    <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
                        <i class="fa fa-desktop text-green-500 text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-semibold mb-4">PC管理</h3>
                    <p class="text-gray-600 mb-6">
                        电脑网页端实时查看数据、远程控制设备，随时随地管理农田
                    </p>
                </div>
            </div>
        </div>
    </section>

    <!-- 产品展示区 -->
    <section class="py-20 bg-white">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex flex-col lg:flex-row items-center gap-12">
                <div class="lg:w-1/2">
                    <h2 class="text-[clamp(1.8rem,3vw,2.5rem)] font-bold text-gray-800 mb-6">
                        智能硬件 + 软件平台 <br>
                        <span class="text-green-500">一站式解决方案</span>
                    </h2>
                    <p class="text-gray-600 mb-8 text-lg leading-relaxed">
                        我们提供全套智能农业设备和云端管理平台，从传感器部署到数据分析，从远程控制到决策支持，全方位满足现代农业生产需求。
                    </p>

                    <div class="space-y-6 mb-8">
                        <div class="flex items-start">
                            <div
                                class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center mt-1 mr-4 flex-shrink-0">
                                <i class="fa fa-check text-green-500"></i>
                            </div>
                            <div>
                                <h3 class="font-semibold text-lg mb-1">高精度传感器</h3>
                                <p class="text-gray-600">数据采集精度达99%，稳定可靠，适应恶劣环境</p>
                            </div>
                        </div>

                        <div class="flex items-start">
                            <div
                                class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center mt-1 mr-4 flex-shrink-0">
                                <i class="fa fa-check text-green-500"></i>
                            </div>
                            <div>
                                <h3 class="font-semibold text-lg mb-1">低功耗设计</h3>
                                <p class="text-gray-600">设备续航长达3年，无需频繁更换电池，降低维护成本</p>
                            </div>
                        </div>

                        <div class="flex items-start">
                            <div
                                class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center mt-1 mr-4 flex-shrink-0">
                                <i class="fa fa-check text-green-500"></i>
                            </div>
                            <div>
                                <h3 class="font-semibold text-lg mb-1">易用性设计</h3>
                                <p class="text-gray-600">一键部署，傻瓜式操作，无需专业知识也能轻松使用</p>
                            </div>
                        </div>
                    </div>

                    <a href="#features" class="inline-flex items-center justify-center rounded-full bg-green-500 px-6 py-2 font-medium text-white shadow-md transition-all duration-300 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow-lg">
                        查看产品详情 <i class="fa fa-arrow-right ml-2"></i>
                    </a>
                </div>

                <div class="lg:w-1/2 relative">
                    <div class="relative z-10 rounded-2xl overflow-hidden shadow-2xl transform rotate-2">
                        <img src="https://picsum.photos/id/1076/800/600" alt="智能农业设备" class="w-full h-auto">
                    </div>
                    <div
                        class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-full h-full bg-green-200 rounded-2xl -rotate-3">
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 联系区 -->
    <section id="contact" class="py-20 bg-white">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8">
            <div class="bg-green-50 rounded-3xl p-8 md:p-12">
                <div class="max-w-3xl">
                    <h2 class="text-[clamp(1.8rem,3vw,2.5rem)] font-bold text-gray-800 mb-6">
                        联系我们，<br>
                        <span class="text-green-500">开启智慧农业之旅</span>
                    </h2>
                    <p class="text-gray-600 mb-8 text-lg leading-relaxed">
                        如果你对项目实现、后端接口或后续功能规划感兴趣，可以通过以下方式联系我。
                    </p>

                    <div class="space-y-6 mb-8">
                        <div class="flex items-start">
                            <div
                                class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mt-1 mr-4 flex-shrink-0">
                                <i class="fa fa-map-marker text-green-500 text-xl"></i>
                            </div>
                            <div>
                                <h3 class="font-semibold text-lg mb-1">所在位置</h3>
                                <p class="text-gray-600">西北工业大学长安校区</p>
                            </div>
                        </div>

                        <div class="flex items-start">
                            <div
                                class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mt-1 mr-4 flex-shrink-0">
                                <i class="fa fa-envelope text-green-500 text-xl"></i>
                            </div>
                            <div>
                                <h3 class="font-semibold text-lg mb-1">电子邮箱</h3>
                                <p class="text-gray-600">zhaofangming@mail.nwpu.edu.cn</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 页脚 -->
    <footer class="bg-gray-800 text-white py-12">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mb-12">
                <div>
                    <div class="flex items-center space-x-2 mb-6">
                        <i class="fa fa-leaf text-green-500 text-2xl"></i>
                        <span class="text-xl font-bold">FarmWise</span>
                    </div>
                    <p class="text-gray-400 mb-6">
                        用科技赋能农业，让每一寸土地都实现价值最大化
                    </p>
                </div>

                <div>
                    <h4 class="text-lg font-semibold mb-6">快速链接</h4>
                    <ul class="space-y-3">
                        <li><a href="#home"
                                class="text-gray-400 hover:text-green-500 transition-colors duration-300">首页</a></li>
                        <li><a href="#features"
                                class="text-gray-400 hover:text-green-500 transition-colors duration-300">功能介绍</a></li>
                        <li><a href="#contact"
                                class="text-gray-400 hover:text-green-500 transition-colors duration-300">联系我们</a></li>
                    </ul>
                </div>

                <div>
                    <h4 class="text-lg font-semibold mb-6">联系我们</h4>
                    <ul class="space-y-3">
                        <li class="flex items-start">
                            <i class="fa fa-map-marker text-green-500 mt-1 mr-3"></i>
                            <span class="text-gray-400">西北工业大学长安校区</span>
                        </li>
                        <li class="flex items-start">
                            <i class="fa fa-envelope text-green-500 mt-1 mr-3"></i>
                            <span class="text-gray-400">zhaofangming@mail.nwpu.edu.cn</span>
                        </li>
                    </ul>
                </div>
            </div>

            <div class="border-t border-gray-800 pt-8 flex flex-col md:flex-row justify-between items-center">
                <p class="text-gray-500 mb-4 md:mb-0">© 2025 FarmWise. 保留所有权利。</p>
            </div>
        </div>
    </footer>
</template>
