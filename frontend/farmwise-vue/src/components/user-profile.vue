<template>
  <main class="mx-auto w-full max-w-6xl space-y-6">
    <div>
      <h2 class="flex items-center text-2xl font-bold text-gray-800">
        <i class="fa fa-user-circle-o mr-3 text-green-600"></i>个人中心
      </h2>
      <p class="mt-2 text-sm text-gray-500">维护账户资料并查看当前角色与权限</p>
    </div>

    <section class="overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm">
      <div class="bg-gradient-to-r from-green-600 to-emerald-500 px-6 py-7 text-white sm:px-8">
        <div class="flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
          <div class="flex items-center gap-4">
            <div class="relative shrink-0">
              <img
                v-if="avatarPreview"
                :src="avatarPreview"
                alt="用户头像"
                class="h-20 w-20 rounded-full border-4 border-white/70 object-cover shadow-md"
              >
              <span
                v-else
                class="flex h-20 w-20 items-center justify-center rounded-full border-4 border-white/70 bg-white/20 text-2xl font-bold shadow-md"
              >
                {{ savedUser.username.slice(0, 1) }}
              </span>
              <button
                type="button"
                class="absolute -bottom-1 -right-1 flex h-8 w-8 items-center justify-center rounded-full bg-white text-green-700 shadow transition hover:bg-green-50"
                aria-label="更换头像"
                @click="avatarInput?.click()"
              >
                <i class="fa fa-camera"></i>
              </button>
              <input
                ref="avatarInput"
                type="file"
                accept="image/jpeg,image/png,image/webp"
                class="hidden"
                @change="handleAvatarChange"
              >
            </div>
            <div>
              <div class="flex flex-wrap items-center gap-2">
                <h3 class="text-2xl font-bold">{{ savedUser.username }}</h3>
                <span class="rounded-full bg-white/20 px-2.5 py-1 text-xs font-medium">
                  {{ statusLabel(savedUser.status) }}
                </span>
              </div>
              <p class="mt-1 text-sm text-green-50">{{ savedUser.realName || '未填写真实姓名' }} · {{ savedUser.id }}</p>
              <div class="mt-3 flex flex-wrap gap-2">
                <span v-for="roleCode in savedUser.roles" :key="roleCode" class="rounded-full bg-white/15 px-2.5 py-1 text-xs">
                  {{ roleName(roleCode) }}
                </span>
              </div>
            </div>
          </div>
          <div class="grid grid-cols-2 gap-x-8 gap-y-3 text-sm sm:text-right">
            <div>
              <p class="text-green-100">注册时间</p>
              <p class="mt-1 font-medium">{{ formatDateTime(savedUser.createdAt) }}</p>
            </div>
            <div>
              <p class="text-green-100">最近登录</p>
              <p class="mt-1 font-medium">{{ formatDateTime(savedUser.lastLoginAt) }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="border-b border-gray-100 bg-green-50/60 px-6 py-3 text-sm text-gray-600 sm:px-8">
        <i class="fa fa-info-circle mr-2 text-green-600"></i>
        支持 JPG、PNG、WebP 格式头像，文件大小不超过 5 MB
      </div>
    </section>

    <form class="grid gap-6 lg:grid-cols-[minmax(0,1fr)_340px]" @submit.prevent="handleSubmit">
      <div class="space-y-6">
        <section class="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
          <div class="mb-5">
            <h3 class="font-semibold text-gray-800"><i class="fa fa-id-card-o mr-2 text-green-600"></i>账户资料</h3>
            <p class="mt-1 text-sm text-gray-500">用户名和联系方式用于登录提示与业务联系</p>
          </div>

          <div class="grid gap-5 sm:grid-cols-2">
            <label class="block">
              <span class="mb-1.5 block text-sm font-medium text-gray-700">用户名 <span class="text-red-500">*</span></span>
              <input v-model.trim="form.username" type="text" maxlength="20" class="form-input" placeholder="2～20 个字符">
              <span v-if="errors.username" class="mt-1 block text-xs text-red-500">{{ errors.username }}</span>
            </label>
            <label class="block">
              <span class="mb-1.5 block text-sm font-medium text-gray-700">真实姓名</span>
              <input v-model.trim="form.realName" type="text" maxlength="30" class="form-input" placeholder="选填">
            </label>
            <label class="block sm:col-span-2">
              <span class="mb-1.5 flex items-center gap-2 text-sm font-medium text-gray-700">
                邮箱 <span class="text-red-500">*</span>
                <span
                  class="rounded-full px-2 py-0.5 text-xs font-normal"
                  :class="emailChanged ? 'bg-orange-50 text-orange-600' : savedUser.emailVerified ? 'bg-green-50 text-green-700' : 'bg-gray-100 text-gray-500'"
                >
                  {{ emailChanged ? '修改后需验证' : savedUser.emailVerified ? '已验证' : '未验证' }}
                </span>
              </span>
              <input v-model.trim="form.email" type="email" class="form-input" placeholder="name@example.com">
              <span v-if="errors.email" class="mt-1 block text-xs text-red-500">{{ errors.email }}</span>
            </label>
            <label class="block sm:col-span-2">
              <span class="mb-1.5 block text-sm font-medium text-gray-700">手机号</span>
              <input v-model.trim="form.phone" type="tel" maxlength="11" class="form-input" placeholder="选填，11 位手机号">
              <span v-if="errors.phone" class="mt-1 block text-xs text-red-500">{{ errors.phone }}</span>
            </label>
          </div>
        </section>

        <section class="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
          <div class="mb-5">
            <h3 class="font-semibold text-gray-800"><i class="fa fa-leaf mr-2 text-green-600"></i>农业身份信息</h3>
            <p class="mt-1 text-sm text-gray-500">用于标识用户所属农业组织和业务身份</p>
          </div>

          <div class="grid gap-5 sm:grid-cols-2">
            <label class="block sm:col-span-2">
              <span class="mb-1.5 block text-sm font-medium text-gray-700">所属农场或组织</span>
              <input v-model.trim="form.organization" type="text" maxlength="50" class="form-input" placeholder="选填">
            </label>
            <label class="block">
              <span class="mb-1.5 block text-sm font-medium text-gray-700">省份</span>
              <input v-model.trim="form.province" type="text" maxlength="20" class="form-input" placeholder="例如：河南省">
            </label>
            <label class="block">
              <span class="mb-1.5 block text-sm font-medium text-gray-700">城市</span>
              <input v-model.trim="form.city" type="text" maxlength="20" class="form-input" placeholder="例如：郑州市">
            </label>
            <label class="block sm:col-span-2">
              <span class="mb-1.5 block text-sm font-medium text-gray-700">职务或身份</span>
              <input v-model.trim="form.position" type="text" maxlength="50" class="form-input" placeholder="例如：农场负责人">
            </label>
          </div>
        </section>

        <div class="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
          <button
            type="button"
            class="rounded-lg border border-gray-200 bg-white px-5 py-2.5 text-sm font-medium text-gray-600 transition hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
            :disabled="!isDirty || saving"
            @click="resetForm"
          >
            取消修改
          </button>
          <button
            type="submit"
            class="rounded-lg bg-green-500 px-6 py-2.5 text-sm font-medium text-white transition hover:bg-green-600 disabled:cursor-not-allowed disabled:bg-gray-300"
            :disabled="!isDirty || saving"
          >
            <i :class="['fa mr-2', saving ? 'fa-spinner fa-spin' : 'fa-save']"></i>{{ saving ? '保存中' : '保存修改' }}
          </button>
        </div>
      </div>

      <aside class="space-y-6">
        <section class="rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
          <h3 class="font-semibold text-gray-800"><i class="fa fa-lock mr-2 text-green-600"></i>账户状态</h3>
          <dl class="mt-4 space-y-4 text-sm">
            <div class="flex items-center justify-between gap-4">
              <dt class="text-gray-500">用户 ID</dt>
              <dd class="font-medium text-gray-800">{{ savedUser.id }}</dd>
            </div>
            <div class="flex items-center justify-between gap-4">
              <dt class="text-gray-500">账号状态</dt>
              <dd class="font-medium text-green-700">{{ statusLabel(savedUser.status) }}</dd>
            </div>
            <div class="flex items-center justify-between gap-4">
              <dt class="text-gray-500">邮箱验证</dt>
              <dd :class="savedUser.emailVerified ? 'text-green-700' : 'text-orange-600'">
                {{ savedUser.emailVerified ? '已验证' : '未验证' }}
              </dd>
            </div>
            <div class="flex items-center justify-between gap-4">
              <dt class="text-gray-500">权限数量</dt>
              <dd class="font-medium text-gray-800">{{ savedUser.permissions.length }} 项</dd>
            </div>
          </dl>
        </section>

        <section class="rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
          <div>
            <h3 class="font-semibold text-gray-800"><i class="fa fa-shield mr-2 text-green-600"></i>角色与权限</h3>
            <p class="mt-1 text-sm text-gray-500">由系统管理员统一配置</p>
          </div>
          <div class="mt-4 flex flex-wrap gap-2">
            <span v-for="roleCode in savedUser.roles" :key="roleCode" class="rounded-full bg-green-50 px-2.5 py-1 text-xs font-medium text-green-700">
              {{ roleName(roleCode) }}
            </span>
          </div>
          <div class="mt-5 space-y-2">
            <details v-for="group in permissionGroups" :key="group.module" class="group rounded-lg border border-gray-100 bg-gray-50">
              <summary class="flex cursor-pointer list-none items-center justify-between px-3 py-2.5 text-sm font-medium text-gray-700">
                <span>{{ group.module }}</span>
                <span class="flex items-center gap-2 text-xs font-normal text-gray-400">
                  {{ group.items.length }} 项
                  <i class="fa fa-angle-down transition-transform group-open:rotate-180"></i>
                </span>
              </summary>
              <ul class="space-y-2 border-t border-gray-100 bg-white px-3 py-3">
                <li v-for="permission in group.items" :key="permission.code" class="text-xs text-gray-500">
                  <span class="block text-gray-700">{{ permission.name }}</span>
                  <span class="mt-0.5 block text-gray-400">{{ permission.code }}</span>
                </li>
              </ul>
            </details>
          </div>
        </section>
      </aside>
    </form>
  </main>

  <div
    v-if="emailDialogVisible"
    class="fixed inset-0 z-[70] flex items-center justify-center bg-black/40 px-4"
    @click.self="closeEmailDialog"
  >
    <section class="w-full max-w-md overflow-hidden rounded-2xl bg-white shadow-2xl">
      <div class="flex items-center justify-between border-b border-gray-100 px-6 py-5">
        <div>
          <h3 class="text-lg font-bold text-gray-800">验证新邮箱</h3>
          <p class="mt-1 text-sm text-gray-500">验证码将发送至 {{ form.email }}</p>
        </div>
        <button type="button" class="text-xl text-gray-400 hover:text-gray-700" aria-label="关闭" @click="closeEmailDialog">×</button>
      </div>
      <div class="space-y-4 p-6">
        <div class="flex gap-2">
          <input
            v-model.trim="verificationCode"
            type="text"
            inputmode="numeric"
            maxlength="6"
            class="form-input"
            placeholder="请输入 6 位验证码"
          >
          <button
            type="button"
            class="shrink-0 rounded-lg border border-green-200 bg-green-50 px-3 text-sm font-medium text-green-700 transition hover:bg-green-100 disabled:cursor-not-allowed disabled:opacity-50"
            :disabled="codeCountdown > 0"
            @click="sendVerificationCode"
          >
            {{ codeCountdown > 0 ? `${codeCountdown}s` : codeSent ? '重新发送' : '发送验证码' }}
          </button>
        </div>
        <p class="rounded-lg bg-blue-50 px-3 py-2 text-sm text-blue-700">
          <i class="fa fa-info-circle mr-1.5"></i>前端演示验证码：123456
        </p>
        <p v-if="verificationError" class="text-sm text-red-500">{{ verificationError }}</p>
      </div>
      <div class="flex justify-end gap-3 border-t border-gray-100 bg-gray-50 px-6 py-4">
        <button type="button" class="rounded-lg border border-gray-200 bg-white px-4 py-2 text-sm text-gray-600 hover:bg-gray-50" @click="closeEmailDialog">取消</button>
        <button
          type="button"
          class="rounded-lg bg-green-500 px-4 py-2 text-sm font-medium text-white hover:bg-green-600 disabled:cursor-not-allowed disabled:bg-gray-300"
          :disabled="saving || verificationCode.length !== 6"
          @click="confirmEmailChange"
        >
          验证并保存
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onUnmounted, ref } from 'vue'
import { mockCurrentUserId, mockPermissions, mockRoles, mockUsers } from '../mocks/user-data'
import { toast } from '../utils/toast'

const cloneData = (data) => JSON.parse(JSON.stringify(data))
const currentUserId = localStorage.getItem('userId') || mockCurrentUserId
const sourceUser = mockUsers.find((user) => user.id === currentUserId) || mockUsers[0]

const savedUser = ref(cloneData(sourceUser))
const form = ref(editableFields(savedUser.value))
const errors = ref({})
const avatarInput = ref(null)
const avatarFile = ref(null)
const avatarPreview = ref(savedUser.value.avatarUrl)
const saving = ref(false)

const emailDialogVisible = ref(false)
const verificationCode = ref('')
const verificationError = ref('')
const codeSent = ref(false)
const codeCountdown = ref(0)
let countdownTimer = null

function editableFields(user) {
  return {
    username: user.username || '',
    realName: user.realName || '',
    email: user.email || '',
    phone: user.phone || '',
    organization: user.organization || '',
    province: user.province || '',
    city: user.city || '',
    position: user.position || ''
  }
}

const emailChanged = computed(() => form.value.email !== savedUser.value.email)

const isDirty = computed(() => {
  return avatarFile.value !== null
    || JSON.stringify(form.value) !== JSON.stringify(editableFields(savedUser.value))
})

const permissionGroups = computed(() => {
  const grouped = new Map()
  mockPermissions
    .filter((permission) => savedUser.value.permissions.includes(permission.code))
    .forEach((permission) => {
      if (!grouped.has(permission.module)) grouped.set(permission.module, [])
      grouped.get(permission.module).push(permission)
    })
  return [...grouped.entries()].map(([module, items]) => ({ module, items }))
})

const roleName = (roleCode) => mockRoles.find((role) => role.code === roleCode)?.name || roleCode

const statusLabel = (status) => status === 'active' ? '正常' : '已停用'

const formatDateTime = (value) => {
  if (!value) return '暂无记录'
  return new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(new Date(value))
}

const handleAvatarChange = (event) => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return

  const acceptedTypes = ['image/jpeg', 'image/png', 'image/webp']
  if (!acceptedTypes.includes(file.type)) {
    toast('头像仅支持 JPG、PNG、WebP 格式', 'bg-red-500')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    toast('头像文件不能超过 5 MB', 'bg-red-500')
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    avatarFile.value = file
    avatarPreview.value = reader.result
  }
  reader.onerror = () => toast('头像读取失败，请重新选择', 'bg-red-500')
  reader.readAsDataURL(file)
}

const validateForm = () => {
  const nextErrors = {}
  if (form.value.username.length < 2 || form.value.username.length > 20) {
    nextErrors.username = '用户名长度应为 2～20 个字符'
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
    nextErrors.email = '请输入有效的邮箱地址'
  }
  if (form.value.phone && !/^1\d{10}$/.test(form.value.phone)) {
    nextErrors.phone = '请输入有效的 11 位手机号'
  }
  errors.value = nextErrors
  return Object.keys(nextErrors).length === 0
}

const handleSubmit = () => {
  if (!validateForm()) return
  if (emailChanged.value) {
    verificationCode.value = ''
    verificationError.value = ''
    emailDialogVisible.value = true
    return
  }
  persistChanges()
}

const sendVerificationCode = () => {
  codeSent.value = true
  codeCountdown.value = 60
  clearInterval(countdownTimer)
  countdownTimer = window.setInterval(() => {
    codeCountdown.value -= 1
    if (codeCountdown.value <= 0) clearInterval(countdownTimer)
  }, 1000)
  toast('验证码已发送')
}

const closeEmailDialog = () => {
  emailDialogVisible.value = false
  verificationCode.value = ''
  verificationError.value = ''
  codeSent.value = false
  codeCountdown.value = 0
  clearInterval(countdownTimer)
  countdownTimer = null
}

const confirmEmailChange = () => {
  if (!codeSent.value) {
    verificationError.value = '请先发送验证码'
    return
  }
  if (verificationCode.value !== '123456') {
    verificationError.value = '验证码不正确'
    return
  }
  persistChanges('123456')
}

const persistChanges = async (verificationCodeValue = null) => {
  saving.value = true
  await new Promise((resolve) => window.setTimeout(resolve, avatarFile.value ? 350 : 180))

  const updatedUser = {
    ...savedUser.value,
    ...form.value,
    phone: form.value.phone || null,
    realName: form.value.realName || null,
    organization: form.value.organization || null,
    province: form.value.province || null,
    city: form.value.city || null,
    position: form.value.position || null,
    avatarUrl: avatarPreview.value || null,
    emailVerified: emailChanged.value ? Boolean(verificationCodeValue) : savedUser.value.emailVerified
  }

  const mockUser = mockUsers.find((user) => user.id === updatedUser.id)
  if (mockUser) Object.assign(mockUser, cloneData(updatedUser))

  savedUser.value = cloneData(updatedUser)
  form.value = editableFields(savedUser.value)
  avatarFile.value = null
  errors.value = {}
  saving.value = false
  closeEmailDialog()

  localStorage.setItem('userId', updatedUser.id)
  localStorage.setItem('username', updatedUser.username)
  window.dispatchEvent(new CustomEvent('farmwise:user-profile-updated', {
    detail: { username: updatedUser.username, avatarUrl: updatedUser.avatarUrl }
  }))
  toast('个人资料保存成功')
}

const resetForm = () => {
  form.value = editableFields(savedUser.value)
  avatarFile.value = null
  avatarPreview.value = savedUser.value.avatarUrl
  errors.value = {}
}

onUnmounted(() => clearInterval(countdownTimer))
</script>

<style scoped>
.form-input {
  width: 100%;
  border: 1px solid rgb(229 231 235);
  border-radius: 0.5rem;
  background: white;
  padding: 0.625rem 0.75rem;
  font-size: 0.875rem;
  color: rgb(55 65 81);
  outline: none;
  transition: border-color 150ms, box-shadow 150ms;
}

.form-input:focus {
  border-color: rgb(34 197 94);
  box-shadow: 0 0 0 3px rgb(220 252 231);
}
</style>
