<template>
    <div class="flex items-center justify-center min-h-screen bg-gray-100">
        <section class="bg-white rounded-xl shadow-lg p-6">
            <div class="text-center mb-4">
                <i class="fa fa-leaf text-green-500 text-3xl"></i>
                <h1 class="text-xl font-bold mt-2">智慧农业种植监控系统</h1>
                <p class="text-gray-500 text-sm">用户登录</p>
            </div>

            <form @submit.prevent="handleLogin" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">登录方式</label>
                    <select v-model="form.type"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500">
                        <option value="USERNAME">用户名</option>
                        <option value="EMAIL">邮箱</option>
                        <option value="PHONE">手机号</option>
                    </select>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">账号</label>
                    <input type="text" v-model="form.account" required placeholder="请输入账号"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
                    <input type="password" v-model="form.password" required placeholder="请输入密码"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500">
                </div>

                <button type="submit"
                    class="w-full bg-green-500 text-white py-2 rounded-lg hover:bg-green-600 transition">
                    登录
                </button>
            </form>

            <div class="text-center mt-4 text-sm">
                还没有账号？
                <router-link to="/register" class="text-green-600 hover:underline">立即注册</router-link>
            </div>
        </section>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from '../utils/toast';

const router = useRouter();

const form = ref({
    type: 'USERNAME',
    account: '',
    password: ''
})

const handleLogin = async () => {
    const type = form.value.type;
    const login = form.value.account;
    const pwd = form.value.password;

    try {
        const res = await fetch('/api/user/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ login, password: pwd, type, ip: '' })
        });
        if (!res.ok) throw new Error(await res.text());

        const data = await res.json();
        localStorage.setItem('uid', data.userId);
        localStorage.setItem('uname', data.username);
        localStorage.setItem('perms', JSON.stringify(data.permissions));
        localStorage.setItem('roles', JSON.stringify(data.role));

        toast('登录成功');
        // 跳回主控制台
        router.push('/profile')
    } catch (err) {
        toast('登录失败：' + err.message, 'bg-red-500');
    }
}
</script>

<style scoped>
</style>