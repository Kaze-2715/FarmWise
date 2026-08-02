<template>
    <div class="flex items-center justify-center min-h-screen bg-gray-100 px-4">
        <section class="w-full max-w-md bg-white rounded-xl shadow-lg p-6">
            <div class="text-center mb-4">
                <div class="flex items-center justify-center gap-2">
                    <i class="fa fa-leaf text-green-500 text-3xl"></i>
                    <h1 class="text-xl font-bold">智慧农业种植监控系统</h1>
                </div>
                <p class="text-gray-500 text-base mt-4">用户登录</p>
            </div>

            <form @submit.prevent="handleLogin" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">{{ isEmailLogin ? '邮箱' : '用户名' }}</label>
                    <input :type="isEmailLogin ? 'email' : 'text'" v-model="form.account" :placeholder="isEmailLogin ? '请输入邮箱' : '请输入用户名'"
                    required
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
                <button type="button" class="w-full text-sm text-green-600 hover:underline" 
                @click="toggleLoginType">
                    {{ isEmailLogin ? '使用用户名登录' : '使用邮箱登录' }}
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
import { useRoute, useRouter } from 'vue-router';
import { toast } from '../utils/toast';
import { login as loginApi } from "../api/auth";
import { setCurrentUser } from '../composables/useAuthSession';

const route = useRoute();
const router = useRouter();

const isEmailLogin = ref(true);

const form = ref({
    account: '',
    password: ''
})

const handleLogin = async () => {
    const loginType = isEmailLogin.value ? 'email' : 'username';

    try {
        const response = await loginApi({
            loginType,
            account: form.value.account.trim(),
            password: form.value.password
        });

        setCurrentUser(response.user);

        toast('登录成功');

        const redirect = typeof route.query.redirect === 'string' && route.query.redirect.startsWith('/dashboard') ? route.query.redirect : '/dashboard';

        await router.replace(redirect);
    } catch (error) {
        toast(`登录失败：${error.message}`, 'bg-red-500');
    }
};

const toggleLoginType = () => {
    isEmailLogin.value = !isEmailLogin.value;
    form.value.account = '';
};
</script>
