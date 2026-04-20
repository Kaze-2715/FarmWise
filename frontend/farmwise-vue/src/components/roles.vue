<template>
    <div class="container mx-auto px-4 pt-24 pb-16">
        <section class="bg-white rounded-xl card-shadow p-6">
            <div class="flex justify-between items-center mb-4">
                <h2 class="text-xl font-bold">用户角色管理</h2>
            </div>

            <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div>
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">用户ID</th>
                                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">用户名</th>
                                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">角色</th>
                                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">操作</th>
                            </tr>
                        </thead>
                        <tbody id="user-role-tbody" class="bg-white divide-y divide-gray-200">
                            <tr v-for="userRole in userRoles" :key="userRole.id"
                                class="bg-white divide-y divide-gray-200">
                                <td class="px-3 py-2 text-sm text-gray-900">{{ userRole.userId }}</td>
                                <td class="px-3 py-2 text-sm text-gray-900">{{ userRole.username }}</td>
                                <td class="px-3 py-2 text-sm text-gray-900">{{ userRole.role }}</td>
                                <td class="px-3 py-2 text-sm text-gray-900">
                                    <button class="btn-danger text-sm">删除</button>
                                    <button class="btn-outline text-xs" @click="revokeRole(userRole)">撤销</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <!-- TODO：修改角色弹窗 -->
        </section>
    </div>
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