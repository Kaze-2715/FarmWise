<template>
    <div class="flex items-center justify-center min-h-screen bg-gray-100">
        <section class="bg-white rounded-xl shadow-lg p-6">
            <div class="text-center mb-4">
                <i class="fa fa-leaf text-green-500 text-3xl"></i>
                <h1 class="text-xl font-bold mt-2">智慧农业种植监控系统</h1>
                <p class="text-gray-500 text-sm">用户注册</p>
            </div>

            <form @submit.prevent="register" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">注册方式</label>
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

                <div v-show="form.type === 'EMAIL'">
                    <label class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
                    <div class="flex space-x-2">
                        <input type="text" v-model="form.code" placeholder="请输入验证码"
                            class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500">
                        <button type="button" @click="sendCode"
                            class="px-3 py-2 border border-gray-300 rounded-lg hover:border-green-500 hover:text-green-500 text-sm">发送验证码</button>
                    </div>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
                    <input type="password" v-model="form.password" required placeholder="请输入密码"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
                    <input type="password" v-model="form.password2" required placeholder="请再次输入密码"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500">
                </div>

                <button type="submit"
                    class="w-full bg-green-500 text-white py-2 rounded-lg hover:bg-green-600 transition">
                    注册
                </button>
            </form>

            <div class="text-center mt-4 text-sm">
                已有账号？
                <router-link to="/login" class="text-green-600 hover:underline">立即登录</router-link>
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
    code: '',
    password: '',
    password2: ''
});

const sendCode = async () => {
    const email = form.value.account;
    if (!email) {
        return toast('请先输入邮箱', 'bg-orange-500');
    }
    try {
        await fetch('/api/verify-code/send?target=' + email + '&type=EMAIL', { method: 'POST' });
        toast('验证码已发送');  
    } catch (e) {
        toast('发送失败：' + e.message, 'bg-red-500');
    }
}

const register = async () => {
    const type = form.value.type;
    const login = form.value.account;
    const code = type === 'EMAIL' ? form.value.code : '';
    const pwd = form.value.password;
    const pwd2 = form.value.password2;

    if (pwd !== pwd2) {
        return toast('两次密码不一致', 'bg-red-500');
    }

    try {
        const res = await fetch('/api/user/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ type, login, password: pwd, verifyCode: code })
        });
        if (!res.ok) throw new Error(await res.text());
        toast('注册成功！即将跳转登录页');
        setTimeout(() => router.push('/login'), 1200);

    } catch (e) {
        toast('注册失败：' + e.message, 'bg-red-500');
    }
}
</script>
