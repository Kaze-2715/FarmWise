<template>
  <div id="app" class="flex flex-col min-h-screen">
    <!-- Header -->
    <header class="bg-white shadow-sm top-0 left-0 right-0 z-50">
      <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <i class="fa fa-leaf text-green-500 text-2xl"></i>
          <h1 class="text-xl font-bold">智慧农业种植监控系统</h1>
        </div>
        <div class="flex items-center space-x-4">
          <div class="relative flex items-center space-x-2 group">
            <img src="https://picsum.photos/id/1005/40/40" alt="用户头像" class="w-8 h-8 rounded-full object-cover">
            <span class="text-sm font-medium" id="top-username">{{ username }}</span>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-1">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="bg-white border-t border-gray-200 py-4 mt-auto">
      <div class="container mx-auto px-4 text-center text-sm text-gray-500">
        <p>© 2025 智慧农业种植监控系统 - 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  name: 'App',
  setup() {
    const username = ref('');

    onMounted(() => {
      username.value = localStorage.getItem('username') || '游客';
    });

    return {
      username,
    };
  },
};
</script>

<style>
html,
body,
#app {
  height: 100%;
  margin: 0;
}

main {
  flex: 1;
  /* 主内容区域占据剩余空间 */
}
</style>