# FarmWise

FarmWise 是一个基于 Spring Boot 与 Vue 3 的智慧农业管理系统，覆盖地块、设备、环境监测、灌溉、预警、农事任务、AI 顾问和报告等业务。

## 目标技术栈

- 前端：Vue 3、Vite、Element Plus、Tailwind CSS
- 后端：Java 21、Spring Boot 4.1、Spring MVC、MyBatis 4、Maven
- 数据：MySQL 8.0、Flyway、Redis
- 认证：Spring Security、JWT、RBAC
- 物联网：EMQX、MQTT、Eclipse Paho
- AI Agent：Spring AI
- 可观测性：Actuator、Micrometer
- 部署：Docker Compose、Nginx、Spring Boot

项目采用前后端分离的模块化单体架构：后端位于 `backend/`，前端位于 `frontend/farmwise-vue/`。后端已完成数据库基础、认证授权、认证接口、当前用户资料、本地头像文件接口和地块基础 CRUD；下一步实现设备接口。架构、选型理由和淘汰项见 [后端架构说明](docs/backend-architecture.md)。

## 开发计划

当前围绕 Java 后端技术栈展示、物联网应用和 AI Agent 顾问三条主线开发。

开发顺序固定为：

1. 前端产品原型
2. mock 数据与交互
3. RESTful 接口契约
4. 后端实现

### 第一阶段：前端原型

按以下顺序补齐功能：

- [x] 环境监测：实时指标、历史趋势和传感器状态。
- [x] 环境阈值管理：由技术顾问按地块和监测指标配置适宜范围，环境监测根据规则动态计算状态，并为异常预警提供判断依据。
- [x] 智能灌溉：自动/手动模式、阈值、设备控制和执行记录。
- [x] 异常预警：环境、设备和病虫害预警及处理记录。
- [x] 农事任务：从预警生成任务并跟踪处理状态。
- [x] AI Agent 顾问：结合地块、设备、环境、灌溉、预警和任务上下文生成建议，展示参考数据，并支持将任务草稿确认为农事任务。
- [x] 报告中心：汇总地块、设备、监测、预警、任务和 AI 建议，已生成报告保持数据快照只读。

地块管理已有基础功能，后续按其他模块的需求补充关联信息。

### 第二阶段：数据与接口

- [x] 确定核心数据结构和 RESTful 接口契约，完整接口见 [接口文档](docs/api-contract.md)。

### 第三阶段：后端能力

- [x] 重新设计 MySQL 数据库，通过 Flyway 管理结构、约束、角色和权限种子数据。
- [x] 完成 Spring Security、JWT、RBAC、统一 401/403 响应和接口权限拦截基础。
- [x] 实现验证码、注册、登录、访问令牌、刷新令牌轮换和退出接口，并使用 Redis 管理验证码与刷新令牌。
- [x] 实现当前用户资料和头像文件接口，并使用可替换存储接口管理本地头像文件。
- [ ] 按接口文档顺序实现地块、设备、监测、灌溉、预警、任务、AI 顾问和报告接口；地块基础 CRUD 已完成，下一步实现设备接口。
- [ ] 使用 Redis 管理设备状态和短期 AI 上下文。
- [ ] 使用 EMQX + MQTT 处理设备数据和控制指令。
- [ ] 使用 Spring AI 实现技术顾问的上下文、工具调用和任务草稿。
- [ ] 使用 Docker Compose 编排 MySQL、Redis、EMQX 和应用服务。

## 开源协议

MIT License
