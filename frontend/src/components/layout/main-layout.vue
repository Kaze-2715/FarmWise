<template>
  <div class="flex h-screen flex-col overflow-hidden bg-gray-50">
    <!-- 顶部导航 -->
    <header class="sticky top-0 z-50 shrink-0 shadow-lg" style="background-color: rgba(31,41,55,.95);">
      <div class="container mx-auto px-4 py-4">
        <div class="flex justify-between items-center">
          <!-- 左侧 Logo -->
          <router-link to="/dashboard" class="flex items-center space-x-2">
            <i class="fa fa-leaf text-2xl text-green-500"></i>
            <span class="text-xl font-bold text-white">FarmWise</span>
            <span class="text-gray-300 ml-1">| 智慧农业控制台</span>
          </router-link>

          <!-- 中间导航按钮 -->
          <nav class="flex space-x-2">
            <!-- 农场主功能 -->
            <button v-if="hasRole('farm_owner')" @click="$router.push('/dashboard/planting')"
              class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
              种植监控
            </button>
            <button v-if="hasRole('farm_owner')" @click="$router.push('/dashboard/land')"
              class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
              土地管理
            </button>
            <button v-if="hasRole('farm_owner')" @click="$router.push('/dashboard/devices')"
              class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
              设备管理
            </button>
            <button v-if="hasRole('farm_owner')" @click="$router.push('/dashboard/advisor')"
              class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
              技术顾问
            </button>

            <!-- 管理员功能 -->
            <button v-if="hasRole('sys_admin')" @click="$router.push('/dashboard/roles')"
              class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
              用户中心
            </button>

            <!-- 数据分析师功能 -->
            <button v-if="hasRole('data_analyst')" @click="$router.push('/dashboard/market')"
              class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
              产量市场
            </button>
            <button v-if="hasRole('data_analyst')" @click="$router.push('/dashboard/reports')"
              class="px-3 py-1.5 rounded hover:bg-green-500/20 text-white hover:text-green-500 transition-colors">
              报告中心
            </button>
          </nav>

          <!-- 右侧用户下拉 -->
          <div class="relative flex items-center space-x-2 group">
            <button type="button" class="flex items-center space-x-2 cursor-pointer">
              <img v-if="avatarUrl" :src="avatarUrl" alt="头像" class="w-8 h-8 rounded-full object-cover">
              <span v-else class="flex h-8 w-8 items-center justify-center rounded-full bg-green-500 text-sm font-semibold text-white">
                {{ username.slice(0, 1) }}
              </span>
              <span class="text-sm font-medium text-white">{{ username }}</span>
            </button>
            <!-- 下拉菜单 -->
            <div
              class="absolute right-0 top-full z-50 hidden w-48 pt-2 group-hover:block group-focus-within:block">
              <div class="flex flex-col gap-3 rounded-xl bg-white p-3 shadow-lg">
                <button @click="$router.push('/dashboard/user-profile')"
                  class="w-full bg-green-500 hover:bg-green-600 text-white text-sm py-2 rounded transition">
                  个人中心
                </button>
                <button @click="logout"
                  class="w-full border border-gray-300 hover:border-green-500 hover:text-green-500 text-sm py-2 rounded transition">
                  退出登录
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="min-h-0 w-full flex-1 overflow-y-auto px-4 py-6">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { logout as logoutApi } from "../../api/auth";
import { toast } from "../../utils/toast";
import { useFarmStore } from '../../composables/useFarmStore'
import { useAuthSession } from '../../composables/useAuthSession'

const router = useRouter()
const { loadLands, loadDevices, clearFarmData } = useFarmStore()
const { currentUser, setCurrentUser, clearCurrentUser } = useAuthSession()
const username = computed(() => currentUser.value?.username || '游客')
const avatarUrl = computed(() => currentUser.value?.avatarUrl || null)
const roles = computed(() => currentUser.value?.roles || [])

const handleProfileUpdated = (event) => {
  if (!currentUser.value) return

  setCurrentUser({
    ...currentUser.value,
    username: event.detail?.username || currentUser.value.username,
    avatarUrl: event.detail?.avatarUrl ?? currentUser.value.avatarUrl
  })
}

// 检查角色权限
const hasRole = (role) => {
  return roles.value.includes(role)
}

// 退出登录
const logout = async () => {
  try {
    await logoutApi();
    clearCurrentUser();
    clearFarmData();

    await router.replace('/login');
  } catch (error) {
    toast(`退出失败：${error.message}`, 'bg-red-500');
  }
}

// 页面加载时读取用户信息
onMounted(async () => {
  window.addEventListener('farmwise:user-profile-updated', handleProfileUpdated)

  try {
    await Promise.all([loadLands(), loadDevices()])
  } catch (error) {
    toast(`加载农场数据失败：${error.message}`, 'bg-red-500')
  }
})

onUnmounted(() => window.removeEventListener('farmwise:user-profile-updated', handleProfileUpdated))
</script>
