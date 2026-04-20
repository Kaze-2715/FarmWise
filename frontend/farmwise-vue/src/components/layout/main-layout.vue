<template>
    <div class="min-h-screen bg-gray-50">
        <!-- 顶部导航 -->
        <header class="shadow-lg sticky top-0 z-50" style="background-color: rgba(31,41,55,.95);">
            <div class="container mx-auto px-4 py-4">
                <div class="flex justify-between items-center">
                    <!-- 左侧 Logo -->
                    <router-link to="/" class="flex items-center space-x-2">
                        <i class="fa fa-leaf text-2xl text-green-500"></i>
                        <span class="text-xl font-bold text-white">Agri Platform</span>
                        <span class="text-gray-300 ml-4">| 智慧农业控制台</span>
                    </router-link>

                    <!-- 中间导航按钮 -->
                    <nav class="flex space-x-2">
                        <!-- 农场主功能 -->
                        <button v-if="hasRole('farm_owner')" @click="$router.push('/planning')"
                            class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
                            种植监控
                        </button>
                        <button v-if="hasRole('farm_owner')" @click="$router.push('/land')"
                            class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
                            土地管理
                        </button>
                        <button v-if="hasRole('farm_owner')" @click="$router.push('/advisor')"
                            class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
                            技术顾问
                        </button>

                        <!-- 管理员功能 -->
                        <button v-if="hasRole('sys_admin')" @click="$router.push('/users')"
                            class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
                            用户中心
                        </button>

                        <!-- 数据分析师功能 -->
                        <button v-if="hasRole('data_analyst')" @click="$router.push('/market')"
                            class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
                            产量市场
                        </button>
                        <button v-if="hasRole('data_analyst')" @click="$router.push('/reports')"
                            class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
                            报告中心
                        </button>
                    </nav>

                    <!-- 右侧用户下拉 -->
                    <div class="relative flex items-center space-x-2 group">
                        <div class="flex items-center space-x-2 cursor-pointer">
                            <img src="https://picsum.photos/id/1005/40/40" alt="头像"
                                class="w-8 h-8 rounded-full object-cover">
                            <span class="text-sm font-medium text-white">{{ username }}</span>
                        </div>
                        <!-- 下拉菜单 -->
                        <div
                            class="hidden group-hover:block absolute right-0 top-full w-48 bg-white rounded-xl shadow-lg p-3 z-50 flex flex-col gap-2">
                            <button @click="$router.push('/profile')"
                                class="w-full bg-green-500 hover:bg-green-600 text-white text-sm py-2 rounded transition">
                                完善信息
                            </button>
                            <button @click="logout"
                                class="w-full border border-gray-300 hover:border-green-500 hover:text-green-500 text-sm py-2 rounded transition">
                                退出登录
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </header>

        <!-- 子页面内容区域（替代 iframe） -->
        <main class="container mx-auto px-4 py-6 min-h-[calc(100vh-80px)]">
            <router-view />
        </main>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('游客')
const roles = ref([])

// 检查角色权限
const hasRole = (role) => {
    return roles.value.includes(role)
}

// 退出登录
const logout = () => {
    localStorage.clear()
    router.push('/login')
}

// 页面加载时读取用户信息
onMounted(() => {
    const uname = localStorage.getItem('username')
    const rolesRaw = localStorage.getItem('roles')

    if (uname) {
        username.value = uname
    }

    if (rolesRaw && rolesRaw !== 'undefined') {
        try {
            roles.value = JSON.parse(rolesRaw)
        } catch (e) {
            console.error('解析角色失败:', e)
        }
    } else {
        // 没有角色，跳转到登录页
        // console.warn('没有可用角色，跳转登录页')
        // router.push('/login')

        // mock 数据，测试用
        roles.value = ['farm_owner', 'data_analyst', 'sys_admin']
        console.log('使用 mock 角色:', roles.value)
    }
})
</script>