# FarmWise 接口文档

本文档覆盖当前 Vue 核心原型需要的认证、权限和农业业务接口。按“认证与 RBAC 基础 → 农业业务 → RBAC 后台管理”的顺序实现。

## 通用约定

- 接口默认使用 JSON；文件上传使用 `multipart/form-data`。成功时直接返回对象或数组，列表无数据返回 `[]`。
- 创建成功返回 `201`，删除成功返回 `204`，设备控制命令受理返回 `202`。
- 错误响应统一为 `{ "message": "错误原因" }`。
- 登录成功后使用 `Authorization: Bearer <accessToken>` 访问受保护接口；前端不传 `userId`。
- 短期 JWT 访问令牌由 Spring Security 校验；刷新令牌通过 HttpOnly Cookie 传递并在 Redis 中保存有效状态。
- 资源 ID、操作者和创建/更新时间由后端生成；时间使用 ISO 8601，日期使用 `YYYY-MM-DD`。
- 列表筛选使用查询参数，资源 ID 使用路径参数，新增和修改数据使用 JSON 请求体。
- Controller 必须通过 `@RequiredPermission` 校验表中权限。
- 权限码统一使用小写 `resource:action` 格式；多单词资源使用下划线，例如 `farm_task:manage`。
- 设备上报数据后续通过 MQTT 接入，不提供前端可调用的 REST 写接口。

## 枚举值

- `landType`：`paddy`（水田）、`dryland`（旱地）、`greenhouse`（温室）
- 地块 `status`：`inactive`（未启用）、`cultivating`（正常种植）、`fallow`（休耕）、`abnormal`（异常）
- `deviceType`：`soil_moisture_sensor`（土壤湿度传感器）、`air_temp_humidity_sensor`（空气温湿度传感器）、`light_sensor`（光照传感器）、`soil_ph_sensor`（土壤 pH 传感器）、`pest_camera`（虫情摄像头）、`irrigation_controller`（灌溉控制器）
- 设备 `status`：`online`（在线）、`offline`（离线）
- `metric`：`soil_moisture`（土壤湿度）、`air_temperature`（空气温度）、`air_humidity`（空气湿度）、`light`（光照强度）、`soil_ph`（土壤 pH）、`battery`（设备电量）
- 种植计划 `status`：`planned`（待开始）、`sowing`（播种期）、`growing`（生长中）、`harvested`（已收获）、`cancelled`（已取消）
- 灌溉 `mode`：`manual`（手动模式）、`automatic`（自动模式）
- 灌溉 `source`：`manual`（人工启动）、`automatic`（系统自动启动）
- 灌溉 `status`：`pending`（待执行）、`running`（执行中）、`completed`（已完成）、`failed`（执行失败）
- 预警 `type`：`environment`（环境预警）、`device`（设备预警）、`pest`（病虫害预警）
- `severity` / `priority`：`low`（低）、`medium`（中）、`high`（高）
- 预警 `status`：`pending`（待处理）、`processing`（处理中）、`resolved`（已解决）、`ignored`（已忽略）
- `taskType`：`irrigation`（灌溉）、`fertilization`（施肥）、`pesticide`（施药）、`weeding`（除草）、`inspection`（巡检）、`harvest`（收获）、`other`（其他）
- 任务 `sourceType`：`manual`（人工创建）、`alert`（预警触发）、`plan`（种植计划）、`system`（系统生成）、`aiMessage`（AI 顾问建议）
- 任务 `status`：`pending`（待处理）、`processing`（进行中）、`completed`（已完成）、`cancelled`（已取消）
- AI 对话 `status`：`active`（进行中）、`closed`（已结束）；消息 `role`：`user`（用户）、`assistant`（AI 顾问）
- AI 引用 `type`：`alert`（预警）、`environmentThreshold`（环境阈值）
- 报告 `type`：`comprehensive`（综合运行）、`device`（设备运行）、`environment`（环境监测）、`alert`（异常预警）、`task`（农事任务）
- 报告 `status`：`generated`（已生成）、`archived`（已归档）
- 报告环境项 `status`：`normal`（正常）、`low`（偏低）、`high`（偏高）、`no_data`（无数据）、`unconfigured`（未配置阈值）
- 登录 `loginType`：`email`（邮箱）、`username`（用户名）
- 系统角色 `roleCode`：`farm_owner`（农场主）、`data_analyst`（数据分析员）、`sys_admin`（系统管理员）
- 用户 `status`：`active`（正常）、`disabled`（已禁用）
- 验证码 `scene`：`register`（注册）、`change_email`（修改邮箱）
- 文件 `purpose`：`avatar`（用户头像）；`attachment`（业务附件）留待附件功能接入时启用

## 实现顺序

下表中的“已实现”表示后端代码已合并到 `main` 并通过编译，不代表已经完成前后端和外部服务联调；“已确认”表示接口契约已经确定但后端尚未实现。

### 认证与 RBAC 基础

| 顺序 | 接口 | 权限 | 状态 |
| --- | --- | --- | --- |
| 1 | `POST /api/auth/verification-codes` | 无 | 已实现 |
| 2 | `POST /api/auth/register` | 无 | 已实现 |
| 3 | `POST /api/auth/login` | 无 | 已实现 |
| 4 | `POST /api/auth/refresh` | 无 | 已实现 |
| 5 | `POST /api/auth/logout` | 已登录 | 已实现 |
| 6 | `GET /api/users/me` | 已登录 | 已实现 |
| 7 | `PUT /api/users/me` | 已登录 | 已实现 |
| 8 | `POST /api/files` | 已登录 | 已实现 |
| 9 | `GET /api/files/{fileId}/content` | 无 | 已实现 |

### 农业业务

| 顺序 | 接口 | 权限 | 状态 |
| --- | --- | --- | --- |
| 1 | `GET /api/lands` | `land:read` | 已确认 |
| 2 | `POST /api/lands` | `land:create` | 已确认 |
| 3 | `PUT /api/lands/{landId}` | `land:update` | 已确认 |
| 4 | `DELETE /api/lands/{landId}` | `land:delete` | 已确认 |
| 5 | `GET /api/devices` | `device:read` | 已确认 |
| 6 | `POST /api/devices` | `device:create` | 已确认 |
| 7 | `PUT /api/devices/{deviceId}` | `device:update` | 已确认 |
| 8 | `DELETE /api/devices/{deviceId}` | `device:delete` | 已确认 |
| 9 | `GET /api/planting-plans` | `planting_plan:read` | 已确认 |
| 10 | `POST /api/planting-plans` | `planting_plan:manage` | 已确认 |
| 11 | `PUT /api/planting-plans/{planId}` | `planting_plan:manage` | 已确认 |
| 12 | `DELETE /api/planting-plans/{planId}` | `planting_plan:manage` | 已确认 |
| 13 | `GET /api/sensor-readings` | `environment:read` | 已确认 |
| 14 | `GET /api/lands/{landId}/environment-thresholds` | `environment:read` | 已确认 |
| 15 | `POST /api/lands/{landId}/environment-thresholds` | `environment_threshold:manage` | 已确认 |
| 16 | `PUT /api/lands/{landId}/environment-thresholds/{metric}` | `environment_threshold:manage` | 已确认 |
| 17 | `DELETE /api/lands/{landId}/environment-thresholds/{metric}` | `environment_threshold:manage` | 已确认 |
| 18 | `GET /api/lands/{landId}/irrigation-config` | `irrigation:read` | 已确认 |
| 19 | `PUT /api/lands/{landId}/irrigation-config` | `irrigation:configure` | 已确认 |
| 20 | `DELETE /api/lands/{landId}/irrigation-config` | `irrigation:configure` | 已确认 |
| 21 | `GET /api/irrigation-records` | `irrigation:read` | 已确认 |
| 22 | `POST /api/irrigations` | `device:control` | 已确认 |
| 23 | `POST /api/irrigations/{recordId}/stop` | `device:control` | 已确认 |
| 24 | `GET /api/alerts` | `alert:read` | 已确认 |
| 25 | `POST /api/alerts` | `alert:manage` | 已确认 |
| 26 | `POST /api/alerts/{alertId}/start` | `alert:manage` | 已确认 |
| 27 | `POST /api/alerts/{alertId}/resolve` | `alert:manage` | 已确认 |
| 28 | `POST /api/alerts/{alertId}/ignore` | `alert:manage` | 已确认 |
| 29 | `GET /api/farm-tasks` | `farm_task:read` | 已确认 |
| 30 | `POST /api/farm-tasks` | `farm_task:manage` | 已确认 |
| 31 | `POST /api/farm-tasks/{taskId}/start` | `farm_task:manage` | 已确认 |
| 32 | `POST /api/farm-tasks/{taskId}/complete` | `farm_task:manage` | 已确认 |
| 33 | `POST /api/farm-tasks/{taskId}/cancel` | `farm_task:manage` | 已确认 |
| 34 | `GET /api/ai/conversations` | `ai_advisor:use` | 已确认 |
| 35 | `POST /api/ai/conversations` | `ai_advisor:use` | 已确认 |
| 36 | `POST /api/ai/conversations/{conversationId}/messages` | `ai_advisor:use` | 已确认 |
| 37 | `POST /api/ai/conversations/{conversationId}/messages/{messageId}/task` | `farm_task:manage` | 已确认 |
| 38 | `POST /api/ai/conversations/{conversationId}/close` | `ai_advisor:use` | 已确认 |
| 39 | `GET /api/reports` | `report:read` | 已确认 |
| 40 | `GET /api/reports/{reportId}` | `report:read` | 已确认 |
| 41 | `POST /api/reports` | `report:generate` | 已确认 |
| 42 | `POST /api/reports/{reportId}/archive` | `report:archive` | 已确认 |

### RBAC 后台管理

| 顺序 | 接口 | 权限 | 状态 |
| --- | --- | --- | --- |
| 1 | `GET /api/admin/users` | `user:read` | 已确认 |
| 2 | `PUT /api/admin/users/{userId}/roles` | `user:grant` | 已确认 |
| 3 | `GET /api/admin/roles` | `role:read` | 已确认 |
| 4 | `GET /api/admin/permissions` | `role:read` | 已确认 |
| 5 | `PUT /api/admin/roles/{roleCode}/permissions` | `role:manage` | 已确认 |

## 1. 认证与用户

`UserProfile`：`id`、`username`、`realName|null`、`email`、`emailVerified`、`phone|null`、`avatarUrl|null`、`organization|null`、`province|null`、`city|null`、`position|null`、`status`、`roles`、`permissions`、`createdAt`、`lastLoginAt|null`。

`FileResource`：`id`、`originalName`、`contentType`、`size`（字节）、`url`、`purpose`、`createdAt`。

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `POST /api/auth/verification-codes` | `email, scene` | `202`；`register` 无需登录，`change_email` 必须登录；验证码存入 Redis，不在响应中返回 |
| `POST /api/auth/register` | `email, password, verificationCode` | `201 UserProfile`；邮箱唯一，默认分配 `farm_owner` |
| `POST /api/auth/login` | `loginType, account, password` | `{ accessToken, tokenType, expiresIn, user }`；同时写入刷新令牌 Cookie |
| `POST /api/auth/refresh` | 无请求体，读取刷新令牌 Cookie | 返回新的访问令牌并轮换刷新令牌 |
| `POST /api/auth/logout` | 无请求体 | `204`；删除刷新令牌状态和 Cookie |
| `GET /api/users/me` | 无 | `UserProfile` |
| `PUT /api/users/me` | `username, realName?, email, phone?, organization?, province?, city?, position?, avatarFileId?, verificationCode?` | `UserProfile`；修改邮箱时必须提交新邮箱的 `change_email` 验证码；未修改邮箱时忽略验证码 |
| `POST /api/files` | `multipart/form-data`：`file, purpose`；当前 `purpose=avatar` | `201 FileResource`；头像只允许 JPEG、PNG、WebP，最大 5 MB；上传成功不自动修改用户资料 |
| `GET /api/files/{fileId}/content` | 路径参数 `fileId` | 返回文件二进制内容；当前头像内容公开读取，附件接入时另行补充用途授权 |

密码只保存哈希值。访问令牌过期时间和刷新令牌有效期由配置决定；退出后访问令牌最多存活到自身过期。

头像更新顺序为先上传文件取得 `FileResource.id`，再将其作为 `avatarFileId` 提交到 `PUT /api/users/me`。客户端不直接提交外部头像 URL。

## 2. 权限管理

`Role`：`code`、`name`、`permissions`；`Permission`：`code`、`name`、`module`。

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/admin/users` | 可选 `keyword, status, roleCode` | `UserProfile[]` |
| `PUT /api/admin/users/{userId}/roles` | `roleCodes` | `UserProfile`；整体替换用户角色，至少保留一个角色 |
| `GET /api/admin/roles` | 无 | `Role[]` |
| `GET /api/admin/permissions` | 可选 `module` | `Permission[]` |
| `PUT /api/admin/roles/{roleCode}/permissions` | `permissionCodes` | `Role`；整体替换角色权限 |

系统角色和权限码由 Flyway 初始化。管理员不能移除自己的最后一个 `sys_admin` 角色，系统中必须至少保留一名系统管理员。

## 3. 地块

`Land`：`id`、`name`、`landType`、`area`（亩）、`crop|null`、`status`、`location`、`longitude`、`latitude`。

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/lands` | 无 | `Land[]`，仅返回当前用户地块 |
| `POST /api/lands` | `name, landType, area, crop?, status, location, longitude, latitude` | `201 Land`；`crop` 可为 `null`，字符串去除首尾空格 |
| `PUT /api/lands/{landId}` | 字段与新增相同 | `Land`；不能修改 `id` 和所属用户 |
| `DELETE /api/lands/{landId}` | 路径参数 `landId` | `204`；不存在或不属于当前用户返回 `404`；有关联业务数据返回 `409` |

## 4. 设备

`Device`：`id`、`name`、`deviceType`、`landId|null`、`status`、`battery|null`、`lastReportedAt|null`、`model`、`installDate|null`、`longitude`、`latitude`。

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/devices` | 查询参数均可选：`landId, deviceType, status, keyword` | `Device[]`；只查询当前用户设备 |
| `POST /api/devices` | `name, deviceType, landId?, model, installDate?, longitude, latitude` | `201 Device`；初始 `status=offline`，遥测字段为 `null` |
| `PUT /api/devices/{deviceId}` | 字段与新增相同 | `Device`；不能修改 `status, battery, lastReportedAt` |
| `DELETE /api/devices/{deviceId}` | 无请求体 | `204`；存在监测、灌溉或预警记录时返回 `409` |

`landId` 可为 `null`，非空时必须属于当前用户。经纬度范围与地块相同，`battery` 范围为 `0～100`。

## 5. 种植计划

`PlantingPlan`：`id`、`landId`、`planName`、`cropType`、`area`、`plantingDate`、`expectedHarvestDate`、`status`、`remark`。

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/planting-plans` | 必填查询参数 `landId` | `PlantingPlan[]` |
| `POST /api/planting-plans` | 除 `id, status` 外的计划字段 | `201 PlantingPlan`；初始 `status=planned` |
| `PUT /api/planting-plans/{planId}` | 与新增相同 | `PlantingPlan` |
| `DELETE /api/planting-plans/{planId}` | 无请求体 | `204`；已关联任务时返回 `409` |

`area > 0`，预计收获日期不得早于种植日期，计划面积不得超过所属地块面积。

## 6. 环境监测

`SensorReading`：`deviceId`、`landId|null`、`recordedAt`、`metric`、`unit`、`value`。单位由指标决定，前端不能修改。

`EnvironmentThreshold`：`landId`、`metric`、`min`、`max`、`enabled`、`creator`、`updatedAt`。

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/sensor-readings` | 必填 `landId`；可选 `metric, startAt, endAt` | `SensorReading[]`，按 `recordedAt` 升序 |
| `GET /api/lands/{landId}/environment-thresholds` | 无 | `EnvironmentThreshold[]` |
| `POST /api/lands/{landId}/environment-thresholds` | `metric, min, max, enabled` | `201 EnvironmentThreshold`；同一地块同一指标只能有一条 |
| `PUT /api/lands/{landId}/environment-thresholds/{metric}` | `min, max, enabled` | `EnvironmentThreshold`；不能修改指标 |
| `DELETE /api/lands/{landId}/environment-thresholds/{metric}` | 无请求体 | `204` |

阈值必须满足 `min < max`。`metric` 不包含当前地块设备无法采集的指标。

## 7. 智能灌溉

`IrrigationConfig`：`landId`、`controllerDeviceId`、`mode`、`enabled`、`triggerMoisture`、`targetMoisture`、`defaultDuration`、`updatedBy`、`updatedAt`。

`IrrigationRecord`：`id`、`landId`、`controllerDeviceId`、`source`、`status`、`startedAt|null`、`endedAt|null`、`plannedDuration`、`duration`、`waterUsage|null`、`triggerReason`、`operator`。

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/lands/{landId}/irrigation-config` | 无 | `IrrigationConfig`；未配置返回 `404` |
| `PUT /api/lands/{landId}/irrigation-config` | `controllerDeviceId, mode, enabled, triggerMoisture, targetMoisture, defaultDuration` | `IrrigationConfig`；不存在时创建，存在时更新 |
| `DELETE /api/lands/{landId}/irrigation-config` | 无请求体 | `204`；存在待执行或执行中的灌溉记录时返回 `409`，历史记录保留 |
| `GET /api/irrigation-records` | 必填 `landId`；可选 `startAt, endAt, status` | `IrrigationRecord[]`，按创建时间倒序 |
| `POST /api/irrigations` | `landId, controllerDeviceId, plannedDuration` | `202 IrrigationRecord`；创建人工灌溉指令 |
| `POST /api/irrigations/{recordId}/stop` | 无请求体 | `202 IrrigationRecord`；只允许停止执行中的记录 |

`triggerMoisture` 和 `targetMoisture` 范围为 `0～100` 且前者小于后者；时长范围为 `1～180` 分钟。控制器必须在线，同一控制器存在待执行或执行中记录时不得重复启动。

## 8. 异常预警

`Alert`：`id`、`landId`、`type`、`severity`、`title`、`description`、`suggestion`、`status`、`occurredAt`、`source`、`handleRecord|null`。

- `source`：`{ deviceId|null, metric|null, value|null, unit|null }`
- `handleRecord`：`{ measure, handledAt, result, remark, operator }`

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/alerts` | 必填 `landId`；可选 `type, severity, status` | `Alert[]`，按发生时间倒序 |
| `POST /api/alerts` | `landId, type, severity, title, description, suggestion, occurredAt, source` | `201 Alert`；初始 `status=pending` |
| `POST /api/alerts/{alertId}/start` | `createTask`；为 `true` 时附带 `taskType, priority, assignee, deadline` | `Alert`；状态改为 `processing`，任务与状态变更在同一事务完成 |
| `POST /api/alerts/{alertId}/resolve` | `measure, handledAt, result, remark?` | `Alert`；状态改为 `resolved` |
| `POST /api/alerts/{alertId}/ignore` | `remark` | `Alert`；状态改为 `ignored` |

只有 `pending` 预警可以开始处理。仍有关联 `pending` 或 `processing` 任务时不能解决预警；`cancelled` 任务不阻塞。

## 9. 农事任务

`FarmTask`：`id`、`landId`、`sourceType`、`sourceId|null`、`taskType`、`title`、`description`、`priority`、`status`、`assignee`、`deadline`、`createdAt`、`completedAt|null`、`result`、`remark`。

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/farm-tasks` | 必填 `landId`；可选 `taskType, priority, status` | `FarmTask[]`，按创建时间倒序 |
| `POST /api/farm-tasks` | `landId, taskType, title, description, priority, assignee, deadline, remark?` | `201 FarmTask`；后端设置 `sourceType=manual, status=pending` |
| `POST /api/farm-tasks/{taskId}/start` | 无请求体 | `FarmTask`；`pending → processing` |
| `POST /api/farm-tasks/{taskId}/complete` | `result` | `FarmTask`；`processing → completed` |
| `POST /api/farm-tasks/{taskId}/cancel` | `reason` | `FarmTask`；`pending/processing → cancelled` |

预警、计划、系统和 AI 产生的任务由对应业务接口创建，前端不能伪造 `sourceType` 或 `sourceId`。

## 10. AI 技术顾问

`AiConversation`：`id`、`landId`、`title`、`status`、`createdAt`、`updatedAt`、`messages`。

`AiMessage`：`id`、`role`、`content`、`createdAt`、`references`、`taskDraft|null`。

- `references` 项：`{ type, sourceId, label, value, unit }`
- `taskDraft`：`{ taskType, title, description, priority, assignee, deadline }`

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/ai/conversations` | 必填 `landId`；可选 `status`，默认 `active` | `AiConversation[]` |
| `POST /api/ai/conversations` | `landId, title` | `201 AiConversation`；同一地块只能有一个进行中对话 |
| `POST /api/ai/conversations/{conversationId}/messages` | `content` | `{ userMessage, assistantMessage }`；后端构建上下文、调用模型并保存两条消息 |
| `POST /api/ai/conversations/{conversationId}/messages/{messageId}/task` | `assignee, deadline` | `201 FarmTask`；从该 AI 消息保存的任务草稿创建 |
| `POST /api/ai/conversations/{conversationId}/close` | 无请求体 | `AiConversation`；仅允许将 `active`（进行中）改为 `closed`（已结束） |

前端只提交问题，不提交上下文、AI 回复、参考数据或任务草稿。同一 AI 消息最多生成一个任务。

## 11. 报告中心

`ReportSummary`：`id`、`landId`、`type`、`title`、`startDate`、`endDate`、`status`、`creator`、`createdAt`、`generatedAt`、`summary`。

`Report` 在 `ReportSummary` 基础上增加只读 `snapshot`：

- `land`：生成时的 `id, name, crop, area`
- `devices`：`total, online, offline, lowBattery`
- `environment[]`：`metric, value, unit, status, recordedAt`
- `alerts`：各状态数量汇总
- `tasks`：各状态数量汇总
- `aiAdvice[]`：`messageId, content, createdAt, references`

| 方法和路径 | 参数或请求体 | 响应与规则 |
| --- | --- | --- |
| `GET /api/reports` | 可选 `landId, type, status, startDate, endDate, keyword` | `ReportSummary[]`，不返回大体积快照 |
| `GET /api/reports/{reportId}` | 无 | `Report` |
| `POST /api/reports` | `landId, type, title, startDate, endDate` | `201 Report`；后端生成摘要和完整快照，作者取当前用户 |
| `POST /api/reports/{reportId}/archive` | 无请求体 | `ReportSummary`；`generated → archived` |

已生成报告是不可变快照。归档只修改报告状态，查看历史报告时不得使用当前实时数据重新计算。
