<template>
  <main class="mx-auto w-full max-w-7xl space-y-6">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <h2 class="flex items-center text-2xl font-bold text-gray-800">
          <i class="fa fa-users mr-3 text-green-600"></i>用户中心
        </h2>
        <p class="mt-2 text-sm text-gray-500">管理系统用户角色和角色权限</p>
      </div>
      <span class="inline-flex w-fit items-center rounded-full bg-green-50 px-3 py-1.5 text-sm font-medium text-green-700">
        <i class="fa fa-shield mr-2"></i>RBAC 权限管理
      </span>
    </div>

    <section class="overflow-hidden rounded-xl border border-gray-100 bg-white shadow-sm">
      <div class="flex border-b border-gray-100 px-4 sm:px-6">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          type="button"
          class="relative px-4 py-4 text-sm font-medium transition-colors"
          :class="activeTab === tab.value ? 'text-green-700' : 'text-gray-500 hover:text-gray-800'"
          @click="activeTab = tab.value"
        >
          <i :class="['fa', tab.icon, 'mr-2']"></i>{{ tab.label }}
          <span
            v-if="activeTab === tab.value"
            class="absolute inset-x-2 bottom-0 h-0.5 rounded-full bg-green-500"
          ></span>
        </button>
      </div>

      <div v-if="activeTab === 'users'" class="space-y-6 p-4 sm:p-6">
        <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <article v-for="stat in userStats" :key="stat.label" class="rounded-xl border border-gray-100 bg-gray-50 p-4">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-500">{{ stat.label }}</p>
                <p class="mt-2 text-2xl font-bold text-gray-800">{{ stat.value }}</p>
              </div>
              <span :class="['flex h-10 w-10 items-center justify-center rounded-lg', stat.iconBackground, stat.iconColor]">
                <i :class="['fa', stat.icon]"></i>
              </span>
            </div>
          </article>
        </div>

        <div class="grid gap-3 rounded-xl border border-gray-100 bg-gray-50 p-4 md:grid-cols-[minmax(220px,1fr)_180px_180px_auto]">
          <label class="relative block">
            <span class="sr-only">搜索用户</span>
            <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
            <input
              v-model.trim="filters.keyword"
              type="search"
              placeholder="搜索用户 ID、用户名或邮箱"
              class="w-full rounded-lg border border-gray-200 bg-white py-2.5 pl-9 pr-3 text-sm outline-none transition focus:border-green-500 focus:ring-2 focus:ring-green-100"
            >
          </label>
          <select
            v-model="filters.status"
            class="rounded-lg border border-gray-200 bg-white px-3 py-2.5 text-sm text-gray-700 outline-none focus:border-green-500 focus:ring-2 focus:ring-green-100"
          >
            <option value="">全部状态</option>
            <option value="active">正常</option>
            <option value="disabled">已停用</option>
          </select>
          <select
            v-model="filters.roleCode"
            class="rounded-lg border border-gray-200 bg-white px-3 py-2.5 text-sm text-gray-700 outline-none focus:border-green-500 focus:ring-2 focus:ring-green-100"
          >
            <option value="">全部角色</option>
            <option v-for="role in roles" :key="role.code" :value="role.code">{{ role.name }}</option>
          </select>
          <button
            type="button"
            class="rounded-lg border border-gray-200 bg-white px-4 py-2.5 text-sm font-medium text-gray-600 transition hover:border-green-300 hover:text-green-700"
            @click="resetFilters"
          >
            <i class="fa fa-refresh mr-2"></i>重置
          </button>
        </div>

        <div class="overflow-hidden rounded-xl border border-gray-100">
          <div class="flex items-center justify-between border-b border-gray-100 px-5 py-4">
            <div>
              <h3 class="font-semibold text-gray-800">用户列表</h3>
              <p class="mt-1 text-sm text-gray-500">共找到 {{ filteredUsers.length }} 位用户</p>
            </div>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-100">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">用户</th>
                  <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">联系方式</th>
                  <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">状态</th>
                  <th class="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">角色</th>
                  <th class="px-5 py-3 text-right text-xs font-medium uppercase tracking-wider text-gray-500">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 bg-white">
                <tr v-for="user in filteredUsers" :key="user.id" class="transition-colors hover:bg-gray-50">
                  <td class="whitespace-nowrap px-5 py-4">
                    <div class="flex items-center gap-3">
                      <span class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-green-50 font-semibold text-green-700">
                        {{ user.username.slice(0, 1) }}
                      </span>
                      <div>
                        <p class="text-sm font-medium text-gray-800">{{ user.username }}</p>
                        <p class="mt-0.5 text-xs text-gray-400">{{ user.id }}</p>
                      </div>
                    </div>
                  </td>
                  <td class="whitespace-nowrap px-5 py-4">
                    <p class="text-sm text-gray-700">{{ user.email }}</p>
                    <p class="mt-0.5 text-xs text-gray-400">{{ user.phone || '未填写手机号' }}</p>
                  </td>
                  <td class="whitespace-nowrap px-5 py-4">
                    <span
                      class="inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium"
                      :class="user.status === 'active' ? 'bg-green-50 text-green-700' : 'bg-gray-100 text-gray-500'"
                    >
                      <span class="mr-1.5 h-1.5 w-1.5 rounded-full" :class="user.status === 'active' ? 'bg-green-500' : 'bg-gray-400'"></span>
                      {{ user.status === 'active' ? '正常' : '已停用' }}
                    </span>
                  </td>
                  <td class="min-w-52 px-5 py-4">
                    <div class="flex flex-wrap gap-2">
                      <span
                        v-for="roleCode in user.roles"
                        :key="roleCode"
                        :class="['inline-flex rounded-full px-2.5 py-1 text-xs font-medium', roleBadgeClass(roleCode)]"
                      >
                        {{ getRoleName(roleCode) }}
                      </span>
                    </div>
                  </td>
                  <td class="whitespace-nowrap px-5 py-4 text-right">
                    <button
                      type="button"
                      class="inline-flex items-center rounded-lg border border-green-200 bg-green-50 px-3 py-1.5 text-sm font-medium text-green-700 transition hover:border-green-300 hover:bg-green-100"
                      @click="openRoleDialog(user)"
                    >
                      <i class="fa fa-pencil-square-o mr-1.5"></i>配置角色
                    </button>
                  </td>
                </tr>
                <tr v-if="filteredUsers.length === 0">
                  <td colspan="5" class="px-6 py-16 text-center text-sm text-gray-400">
                    <i class="fa fa-search mb-3 block text-3xl"></i>
                    没有符合条件的用户
                    <button type="button" class="ml-2 text-green-600 hover:underline" @click="resetFilters">清除筛选</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div v-else class="grid gap-6 p-4 lg:grid-cols-[280px_minmax(0,1fr)] sm:p-6">
        <aside class="space-y-3">
          <div class="mb-4">
            <h3 class="font-semibold text-gray-800">系统角色</h3>
            <p class="mt-1 text-sm text-gray-500">选择角色后配置对应权限</p>
          </div>
          <button
            v-for="role in roles"
            :key="role.code"
            type="button"
            class="w-full rounded-xl border p-4 text-left transition"
            :class="selectedRoleCode === role.code ? 'border-green-400 bg-green-50 shadow-sm' : 'border-gray-100 bg-white hover:border-green-200 hover:bg-gray-50'"
            @click="selectRole(role.code)"
          >
            <div class="flex items-start justify-between gap-3">
              <div>
                <p :class="['font-medium', selectedRoleCode === role.code ? 'text-green-800' : 'text-gray-800']">{{ role.name }}</p>
                <p class="mt-1 text-xs text-gray-400">{{ role.code }}</p>
              </div>
              <span class="rounded-full bg-white px-2 py-1 text-xs text-gray-500 shadow-sm">{{ role.permissions.length }} 项</span>
            </div>
            <p class="mt-3 text-sm leading-5 text-gray-500">{{ role.description }}</p>
          </button>
        </aside>

        <section class="overflow-hidden rounded-xl border border-gray-100">
          <div class="flex flex-col gap-4 border-b border-gray-100 px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <div class="flex items-center gap-2">
                <h3 class="font-semibold text-gray-800">{{ selectedRole?.name }}权限</h3>
                <span v-if="permissionDirty" class="rounded-full bg-orange-50 px-2 py-0.5 text-xs text-orange-600">未保存</span>
              </div>
              <p class="mt-1 text-sm text-gray-500">已选择 {{ permissionDraft.length }} / {{ permissions.length }} 项权限</p>
            </div>
            <div class="flex flex-wrap gap-2">
              <button type="button" class="rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-600 hover:bg-gray-50" @click="selectAllPermissions">
                全部选择
              </button>
              <button
                type="button"
                class="rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-600 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
                :disabled="!permissionDirty"
                @click="resetPermissionDraft"
              >
                恢复
              </button>
              <button
                type="button"
                class="rounded-lg bg-green-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-600 disabled:cursor-not-allowed disabled:bg-gray-300"
                :disabled="!permissionDirty"
                @click="saveRolePermissions"
              >
                保存配置
              </button>
            </div>
          </div>

          <div class="grid gap-4 bg-gray-50 p-4 xl:grid-cols-2 sm:p-5">
            <article v-for="group in permissionGroups" :key="group.module" class="rounded-xl border border-gray-100 bg-white p-4">
              <div class="mb-3 flex items-center justify-between border-b border-gray-100 pb-3">
                <div>
                  <h4 class="font-medium text-gray-800">{{ group.module }}</h4>
                  <p class="mt-1 text-xs text-gray-400">已选择 {{ selectedCountInModule(group) }} / {{ group.items.length }} 项</p>
                </div>
                <label class="flex cursor-pointer items-center gap-2 text-sm text-green-700">
                  <input
                    type="checkbox"
                    class="h-4 w-4 rounded border-gray-300 text-green-500 focus:ring-green-500"
                    :checked="isModuleSelected(group)"
                    @change="togglePermissionModule(group)"
                  >
                  全选
                </label>
              </div>
              <div class="space-y-2">
                <label
                  v-for="permission in group.items"
                  :key="permission.code"
                  class="flex cursor-pointer items-start gap-3 rounded-lg px-2 py-2 transition hover:bg-gray-50"
                >
                  <input
                    v-model="permissionDraft"
                    type="checkbox"
                    :value="permission.code"
                    class="mt-0.5 h-4 w-4 rounded border-gray-300 text-green-500 focus:ring-green-500"
                  >
                  <span>
                    <span class="block text-sm font-medium text-gray-700">{{ permission.name }}</span>
                    <span class="mt-0.5 block text-xs text-gray-400">{{ permission.code }}</span>
                  </span>
                </label>
              </div>
            </article>
          </div>
        </section>
      </div>
    </section>
  </main>

  <div
    v-if="roleDialogVisible"
    class="fixed inset-0 z-[60] flex items-center justify-center bg-black/40 px-4"
    @click.self="closeRoleDialog"
  >
    <section class="w-full max-w-lg overflow-hidden rounded-2xl bg-white shadow-2xl">
      <div class="flex items-center justify-between border-b border-gray-100 px-6 py-5">
        <div>
          <h3 class="text-lg font-bold text-gray-800">配置用户角色</h3>
          <p class="mt-1 text-sm text-gray-500">{{ editingUser?.username }}（{{ editingUser?.id }}）</p>
        </div>
        <button type="button" class="text-xl text-gray-400 hover:text-gray-700" aria-label="关闭" @click="closeRoleDialog">×</button>
      </div>
      <div class="space-y-3 p-6">
        <label
          v-for="role in roles"
          :key="role.code"
          class="flex cursor-pointer items-start gap-3 rounded-xl border p-4 transition"
          :class="roleDraft.includes(role.code) ? 'border-green-300 bg-green-50' : 'border-gray-100 hover:border-gray-200'"
        >
          <input
            v-model="roleDraft"
            type="checkbox"
            :value="role.code"
            class="mt-1 h-4 w-4 rounded border-gray-300 text-green-500 focus:ring-green-500"
          >
          <span class="flex-1">
            <span class="flex items-center justify-between gap-3">
              <span class="font-medium text-gray-800">{{ role.name }}</span>
              <span class="text-xs text-gray-400">{{ role.code }}</span>
            </span>
            <span class="mt-1 block text-sm text-gray-500">{{ role.description }}</span>
          </span>
        </label>
        <p v-if="roleDraft.length === 0 || roleDialogError" class="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600">
          <i class="fa fa-exclamation-circle mr-1.5"></i>{{ roleDraft.length === 0 ? '每位用户至少需要保留一个角色' : roleDialogError }}
        </p>
      </div>
      <div class="flex justify-end gap-3 border-t border-gray-100 bg-gray-50 px-6 py-4">
        <button type="button" class="rounded-lg border border-gray-200 bg-white px-4 py-2 text-sm text-gray-600 hover:bg-gray-50" @click="closeRoleDialog">
          取消
        </button>
        <button
          type="button"
          class="rounded-lg bg-green-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-600 disabled:cursor-not-allowed disabled:bg-gray-300"
          :disabled="roleDraft.length === 0"
          @click="saveUserRoles"
        >
          保存角色
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { mockPermissions, mockRoles, mockUsers } from '../mocks/user-data'
import { toast } from '../utils/toast'

const cloneData = (data) => JSON.parse(JSON.stringify(data))

const tabs = [
  { value: 'users', label: '用户管理', icon: 'fa-user-circle-o' },
  { value: 'permissions', label: '角色权限', icon: 'fa-key' }
]

const activeTab = ref('users')
const users = ref(cloneData(mockUsers))
const roles = ref(cloneData(mockRoles))
const permissions = cloneData(mockPermissions)
const currentUserId = localStorage.getItem('userId') || 'U001'

const filters = ref({ keyword: '', status: '', roleCode: '' })
const roleDialogVisible = ref(false)
const editingUser = ref(null)
const roleDraft = ref([])
const roleDialogError = ref('')

const selectedRoleCode = ref(roles.value[0].code)
const permissionDraft = ref([...roles.value[0].permissions])

const filteredUsers = computed(() => {
  const keyword = filters.value.keyword.toLowerCase()
  return users.value.filter((user) => {
    const matchesKeyword = !keyword || [user.id, user.username, user.email]
      .some((value) => value.toLowerCase().includes(keyword))
    const matchesStatus = !filters.value.status || user.status === filters.value.status
    const matchesRole = !filters.value.roleCode || user.roles.includes(filters.value.roleCode)
    return matchesKeyword && matchesStatus && matchesRole
  })
})

const userStats = computed(() => [
  { label: '用户总数', value: users.value.length, icon: 'fa-users', iconBackground: 'bg-blue-50', iconColor: 'text-blue-600' },
  { label: '正常用户', value: users.value.filter((user) => user.status === 'active').length, icon: 'fa-check-circle', iconBackground: 'bg-green-50', iconColor: 'text-green-600' },
  { label: '已停用用户', value: users.value.filter((user) => user.status === 'disabled').length, icon: 'fa-ban', iconBackground: 'bg-gray-100', iconColor: 'text-gray-500' },
  { label: '系统管理员', value: users.value.filter((user) => user.roles.includes('sys_admin')).length, icon: 'fa-shield', iconBackground: 'bg-purple-50', iconColor: 'text-purple-600' }
])

const selectedRole = computed(() => roles.value.find((role) => role.code === selectedRoleCode.value))

const permissionDirty = computed(() => {
  const saved = [...(selectedRole.value?.permissions || [])].sort()
  const draft = [...permissionDraft.value].sort()
  return JSON.stringify(saved) !== JSON.stringify(draft)
})

const permissionGroups = computed(() => {
  const grouped = new Map()
  permissions.forEach((permission) => {
    if (!grouped.has(permission.module)) grouped.set(permission.module, [])
    grouped.get(permission.module).push(permission)
  })
  return [...grouped.entries()].map(([module, items]) => ({ module, items }))
})

const getRoleName = (roleCode) => roles.value.find((role) => role.code === roleCode)?.name || roleCode

const roleBadgeClass = (roleCode) => ({
  farm_owner: 'bg-green-50 text-green-700',
  data_analyst: 'bg-blue-50 text-blue-700',
  sys_admin: 'bg-purple-50 text-purple-700'
}[roleCode] || 'bg-gray-100 text-gray-600')

const resetFilters = () => {
  filters.value = { keyword: '', status: '', roleCode: '' }
}

const openRoleDialog = (user) => {
  editingUser.value = user
  roleDraft.value = [...user.roles]
  roleDialogError.value = ''
  roleDialogVisible.value = true
}

const closeRoleDialog = () => {
  roleDialogVisible.value = false
  editingUser.value = null
  roleDraft.value = []
  roleDialogError.value = ''
}

const permissionsForRoles = (roleCodes) => [
  ...new Set(roleCodes.flatMap((roleCode) => roles.value.find((role) => role.code === roleCode)?.permissions || []))
]

const saveUserRoles = () => {
  roleDialogError.value = ''
  if (roleDraft.value.length === 0) {
    roleDialogError.value = '每位用户至少需要保留一个角色'
    return
  }

  const removesOwnAdminRole = editingUser.value.id === currentUserId
    && editingUser.value.roles.includes('sys_admin')
    && !roleDraft.value.includes('sys_admin')
  if (removesOwnAdminRole) {
    roleDialogError.value = '不能移除当前登录用户自己的系统管理员角色'
    return
  }

  const remainingAdminCount = users.value.filter((user) => {
    const roleCodes = user.id === editingUser.value.id ? roleDraft.value : user.roles
    return roleCodes.includes('sys_admin')
  }).length
  if (remainingAdminCount === 0) {
    roleDialogError.value = '系统中必须至少保留一名系统管理员'
    return
  }

  editingUser.value.roles = [...roleDraft.value]
  editingUser.value.permissions = permissionsForRoles(roleDraft.value)
  const username = editingUser.value.username
  closeRoleDialog()
  toast(`已更新${username}的角色`)
}

const selectRole = (roleCode) => {
  selectedRoleCode.value = roleCode
  permissionDraft.value = [...(roles.value.find((role) => role.code === roleCode)?.permissions || [])]
}

const selectedCountInModule = (group) => group.items.filter((item) => permissionDraft.value.includes(item.code)).length

const isModuleSelected = (group) => group.items.every((item) => permissionDraft.value.includes(item.code))

const togglePermissionModule = (group) => {
  const groupCodes = group.items.map((item) => item.code)
  if (isModuleSelected(group)) {
    permissionDraft.value = permissionDraft.value.filter((code) => !groupCodes.includes(code))
  } else {
    permissionDraft.value = [...new Set([...permissionDraft.value, ...groupCodes])]
  }
}

const selectAllPermissions = () => {
  permissionDraft.value = permissions.map((permission) => permission.code)
}

const resetPermissionDraft = () => {
  permissionDraft.value = [...(selectedRole.value?.permissions || [])]
}

const saveRolePermissions = () => {
  if (!selectedRole.value) return
  selectedRole.value.permissions = [...permissionDraft.value]
  users.value.forEach((user) => {
    user.permissions = permissionsForRoles(user.roles)
  })
  toast(`已保存${selectedRole.value.name}的权限配置`)
}
</script>
