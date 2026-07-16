<template>
    <main class="mx-auto w-full max-w-7xl space-y-6">
        <div>
            <h2 class="flex items-center text-2xl font-bold text-gray-800">
                <i class="fa fa-users mr-3 text-green-600"></i>用户中心
            </h2>
            <p class="mt-2 text-sm text-gray-500">查看用户角色及其权限信息</p>
        </div>

        <section class="overflow-hidden rounded-xl border border-gray-100 bg-white shadow-sm">
            <div class="flex items-center justify-between border-b border-gray-100 px-6 py-5">
                <div>
                    <h3 class="font-semibold text-gray-800">用户角色管理</h3>
                    <p class="mt-1 text-sm text-gray-500">当前共 {{ userRoles.length }} 条角色记录</p>
                </div>
                <span class="inline-flex items-center rounded-full bg-green-50 px-3 py-1 text-sm font-medium text-green-700">
                    <i class="fa fa-shield mr-2"></i>权限管理
                </span>
            </div>

            <div class="overflow-x-auto">
                    <table class="min-w-full divide-y divide-gray-100">
                        <thead class="bg-gray-50">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">用户 ID</th>
                                <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">用户名</th>
                                <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">角色</th>
                                <th class="px-6 py-3 text-right text-xs font-medium uppercase tracking-wider text-gray-500">操作</th>
                            </tr>
                        </thead>
                        <tbody id="user-role-tbody" class="divide-y divide-gray-100 bg-white">
                            <tr v-for="userRole in userRoles" :key="userRole.id"
                                class="transition-colors hover:bg-gray-50">
                                <td class="whitespace-nowrap px-6 py-4 text-sm text-gray-500">{{ userRole.userId }}</td>
                                <td class="whitespace-nowrap px-6 py-4">
                                    <div class="flex items-center gap-3">
                                        <span class="flex h-9 w-9 items-center justify-center rounded-full bg-green-50 font-medium text-green-700">
                                            {{ userRole.username?.slice(0, 1) || '?' }}
                                        </span>
                                        <span class="text-sm font-medium text-gray-800">{{ userRole.username }}</span>
                                    </div>
                                </td>
                                <td class="whitespace-nowrap px-6 py-4">
                                    <span class="inline-flex rounded-full bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-700">
                                        {{ userRole.role }}
                                    </span>
                                </td>
                                <td class="whitespace-nowrap px-6 py-4 text-right">
                                    <div class="inline-flex items-center gap-2">
                                        <button type="button"
                                            class="inline-flex items-center rounded-lg border border-red-200 bg-red-50 px-3 py-1.5 text-sm font-medium text-red-600 transition-all hover:-translate-y-0.5 hover:border-red-300 hover:bg-red-100 active:translate-y-0">
                                            <i class="fa fa-trash-o mr-1.5"></i>删除
                                        </button>
                                        <button type="button"
                                            class="inline-flex items-center rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-sm font-medium text-gray-600 transition-all hover:-translate-y-0.5 hover:border-gray-300 hover:bg-gray-50 active:translate-y-0"
                                            @click="revokeRole(userRole)">
                                            <i class="fa fa-ban mr-1.5"></i>撤销
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <tr v-if="userRoles.length === 0">
                                <td colspan="4" class="px-6 py-14 text-center text-sm text-gray-400">
                                    <i class="fa fa-user-o mb-3 block text-3xl"></i>
                                    暂无用户角色记录
                                </td>
                            </tr>
                        </tbody>
                    </table>
            </div>
        </section>
    </main>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { toast } from '../utils/toast';
import { onMounted, ref } from 'vue';

const username = ref('');
const userRoles = ref([]);
const permissions = ref([]);
const router = useRouter();

onMounted(() => {
    username.value = localStorage.getItem('username') || '游客';
    loadUserRoles();
    loadPermissions();

    // Mock 数据用于样式测试
    userRoles.value = [
        { id: 1, userId: 'U001', username: '张三', role: '管理员' },
        { id: 2, userId: 'U002', username: '李四', role: '用户' },
        { id: 3, userId: 'U003', username: '王五', role: '访客' }
    ];

    permissions.value = [
        '查看数据',
        '编辑数据',
        '删除数据',
        '导出数据',
        '管理用户'
    ];
})

const loadPermissions = async () => {
    // 加载权限数据
    try {
        const response = await fetch('/api/users/permissions');

        if (!response.ok) {
            throw new Error(`HTTP Error： ${response.status}`);
        }

        permissions.value = await response.json();
    } catch (e) {
        toast('加载权限数据失败！' + e.message, 'bg-red-500');
    }
}

const loadUserRoles = async () => {
    // 加载用户角色数据
    try {
        const response = await fetch('/api/users/user-roles');
        if (!response.ok) {
            throw new Error(`HTTP Error: ${response.status}`);
        }
        userRoles.value = await response.json();
    } catch (e) {
        toast('加载用户角色失败！' + e.message, 'bg-red-500');
    }
}
</script>
