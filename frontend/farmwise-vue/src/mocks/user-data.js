export const mockPermissions = [
  { code: 'land:read', name: '查看地块', module: '农场管理' },
  { code: 'land:create', name: '新增地块', module: '农场管理' },
  { code: 'land:update', name: '修改地块', module: '农场管理' },
  { code: 'land:delete', name: '删除地块', module: '农场管理' },
  { code: 'device:read', name: '查看设备', module: '设备管理' },
  { code: 'device:create', name: '新增设备', module: '设备管理' },
  { code: 'device:update', name: '修改设备', module: '设备管理' },
  { code: 'device:delete', name: '删除设备', module: '设备管理' },
  { code: 'device:control', name: '控制设备', module: '设备管理' },
  { code: 'planting_plan:read', name: '查看种植计划', module: '种植管理' },
  { code: 'planting_plan:manage', name: '管理种植计划', module: '种植管理' },
  { code: 'environment:read', name: '查看环境监测', module: '种植管理' },
  { code: 'environment_threshold:manage', name: '管理环境阈值', module: '种植管理' },
  { code: 'irrigation:read', name: '查看灌溉信息', module: '灌溉与预警' },
  { code: 'irrigation:configure', name: '配置智能灌溉', module: '灌溉与预警' },
  { code: 'alert:read', name: '查看异常预警', module: '灌溉与预警' },
  { code: 'alert:manage', name: '处理异常预警', module: '灌溉与预警' },
  { code: 'farm_task:read', name: '查看农事任务', module: '任务管理' },
  { code: 'farm_task:manage', name: '管理农事任务', module: '任务管理' },
  { code: 'ai_advisor:use', name: '使用 AI 顾问', module: 'AI 与报告' },
  { code: 'report:read', name: '查看报告', module: 'AI 与报告' },
  { code: 'report:generate', name: '生成报告', module: 'AI 与报告' },
  { code: 'report:archive', name: '归档报告', module: 'AI 与报告' },
  { code: 'user:read', name: '查看用户', module: '系统管理' },
  { code: 'user:grant', name: '配置用户角色', module: '系统管理' },
  { code: 'role:read', name: '查看角色权限', module: '系统管理' },
  { code: 'role:manage', name: '管理角色权限', module: '系统管理' }
]

const farmOwnerPermissions = mockPermissions
  .filter((permission) => !['report:read', 'report:generate', 'report:archive', 'user:read', 'user:grant', 'role:read', 'role:manage'].includes(permission.code))
  .map((permission) => permission.code)

const analystPermissions = [
  'environment:read',
  'alert:read',
  'farm_task:read',
  'ai_advisor:use',
  'report:read',
  'report:generate',
  'report:archive'
]

export const mockRoles = [
  { code: 'farm_owner', name: '农场主', description: '管理地块、设备及日常农业生产', permissions: farmOwnerPermissions },
  { code: 'data_analyst', name: '数据分析师', description: '查看农业数据并生成分析报告', permissions: analystPermissions },
  { code: 'sys_admin', name: '系统管理员', description: '管理系统用户、角色与全部权限', permissions: mockPermissions.map((permission) => permission.code) }
]

const rolePermissionMap = new Map(mockRoles.map((role) => [role.code, role.permissions]))

const createUser = (user) => ({
  ...user,
  permissions: [...new Set(user.roles.flatMap((roleCode) => rolePermissionMap.get(roleCode) || []))]
})

export const mockUsers = [
  {
    id: 'U001',
    username: '系统管理员',
    realName: '周明远',
    email: 'admin@farmwise.cn',
    emailVerified: true,
    phone: '13800000001',
    avatarUrl: 'https://picsum.photos/id/1005/160/160',
    organization: 'FarmWise 运营中心',
    province: '河南省',
    city: '郑州市',
    position: '系统管理员',
    status: 'active',
    roles: ['sys_admin'],
    createdAt: '2026-05-18T09:00:00+08:00',
    lastLoginAt: '2026-07-16T08:42:00+08:00'
  },
  {
    id: 'U002',
    username: '张青禾',
    realName: '张建国',
    email: 'zhang@farmwise.cn',
    emailVerified: true,
    phone: '13800001208',
    avatarUrl: null,
    organization: '青禾家庭农场',
    province: '河南省',
    city: '洛阳市',
    position: '农场负责人',
    status: 'active',
    roles: ['farm_owner'],
    createdAt: '2026-06-12T09:30:00+08:00',
    lastLoginAt: '2026-07-15T18:26:00+08:00'
  },
  {
    id: 'U003',
    username: '李知田',
    realName: '李文博',
    email: 'li@farmwise.cn',
    emailVerified: true,
    phone: '13900004521',
    avatarUrl: null,
    organization: '豫农数据服务中心',
    province: '河南省',
    city: '开封市',
    position: '农业数据分析师',
    status: 'active',
    roles: ['data_analyst'],
    createdAt: '2026-06-20T14:10:00+08:00',
    lastLoginAt: '2026-07-16T07:55:00+08:00'
  },
  {
    id: 'U004',
    username: '王晓农',
    realName: '王晓农',
    email: 'wang@farmwise.cn',
    emailVerified: true,
    phone: '13600003306',
    avatarUrl: null,
    organization: '晓农生态种植基地',
    province: '山东省',
    city: '潍坊市',
    position: '技术负责人',
    status: 'active',
    roles: ['farm_owner', 'data_analyst'],
    createdAt: '2026-06-28T11:20:00+08:00',
    lastLoginAt: '2026-07-14T16:18:00+08:00'
  },
  {
    id: 'U005',
    username: '赵禾',
    realName: null,
    email: 'zhao@farmwise.cn',
    emailVerified: false,
    phone: null,
    avatarUrl: null,
    organization: null,
    province: null,
    city: null,
    position: null,
    status: 'disabled',
    roles: ['farm_owner'],
    createdAt: '2026-07-01T10:05:00+08:00',
    lastLoginAt: null
  },
  {
    id: 'U006',
    username: '陈雨',
    realName: '陈雨',
    email: 'chen@farmwise.cn',
    emailVerified: true,
    phone: '13700009083',
    avatarUrl: null,
    organization: '中原农业研究所',
    province: '河南省',
    city: '新乡市',
    position: '数据分析员',
    status: 'active',
    roles: ['data_analyst'],
    createdAt: '2026-07-03T15:40:00+08:00',
    lastLoginAt: '2026-07-15T09:12:00+08:00'
  }
].map(createUser)

export const mockCurrentUserId = 'U001'
