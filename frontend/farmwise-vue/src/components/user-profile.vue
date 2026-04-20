<template>
    <!-- 主内容 -->
    <div class="container mx-auto px-4 pt-24 pb-16">
      <section class="bg-white rounded-xl p-6 max-w-xl mx-auto" style="box-shadow: 0 4px 20px rgba(0,0,0,.08);">
        <h2 class="text-xl font-bold mb-4">完善用户信息</h2>
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input 
              type="text" 
              v-model="form.username" 
              placeholder="选填"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            >
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
            <input 
              type="email" 
              v-model="form.email" 
              placeholder="选填"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            >
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">手机号</label>
            <input 
              type="tel" 
              v-model="form.phone" 
              placeholder="选填"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            >
          </div>
          <button 
            type="submit" 
            class="bg-green-500 text-white font-medium py-2 px-4 rounded hover:bg-green-600 transition"
          >
            保存
          </button>
        </form>
      </section>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { toast } from '../utils/toast'

// 响应式数据
const username = ref('游客')
const form = ref({
  username: '',
  email: '',
  phone: ''
})

// 页面加载时获取 localStorage 数据
onMounted(() => {
  const uname = localStorage.getItem('uname')
  if (uname) {
    username.value = uname
  }
})

// 提交处理
const handleSubmit = async () => {
  const uid = localStorage.getItem('uid')
  if (!uid) {
    toast('请先登录', 'bg-orange-500')
    return
  }
  
  const dto = {
    userId: uid,
    username: form.value.username,
    email: form.value.email,
    phoneNumber: form.value.phone
  }
  
  try {
    const res = await fetch('/api/user/update', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dto)
    })
    if (!res.ok) throw new Error(await res.text())
    toast('保存成功')
  } catch (err) {
    toast('保存失败：' + err.message, 'bg-red-500')
  }
}
</script>