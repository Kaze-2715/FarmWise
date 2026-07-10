# FarmWise

FarmWise 是一个基于 Spring Boot 与 Vue 3 的智慧农业管理系统，覆盖地块、设备、环境监测、灌溉、预警、农事任务、AI 顾问和报告等业务。

## 技术栈

- 前端：Vue 3、Vite、Element Plus、Tailwind CSS
- 后端：Java 21、Spring Boot 3.3、MyBatis、Maven
- 数据库：MySQL 8.0
- 认证：Session + RBAC
- 部署：Nginx + Spring Boot

项目采用前后端分离结构：后端位于 `backend/`，前端位于 `frontend/farmwise-vue/`。

## 开发计划

当前围绕 Java 后端技术栈展示、物联网应用和 AI Agent 顾问三条主线开发。

开发顺序固定为：

1. 前端产品原型
2. mock 数据与交互
3. RESTful 接口契约
4. 后端实现

### 第一阶段：前端原型

按以下顺序补齐功能：

1. 环境监测：实时指标、历史趋势和传感器状态。
2. 环境阈值管理：由技术顾问按地块和监测指标配置适宜范围，环境监测根据规则动态计算状态，并为异常预警提供判断依据。
3. 智能灌溉：自动/手动模式、阈值、设备控制和执行记录。
4. 异常预警：环境、设备和病虫害预警及处理记录。
5. 农事任务：从预警生成任务并跟踪处理状态。
6. AI Agent 顾问：结合地块、作物、传感器和预警上下文生成建议。
7. 报告中心：汇总地块、设备、监测、预警、任务和 AI 建议。

地块管理已有基础功能，后续按其他模块的需求补充关联信息。

### 第二阶段：数据与接口

前端交互稳定后，确定 `land`、`device`、`sensorReading`、`environmentThreshold`、`irrigationRule`、`alert`、`farmTask`、`aiConversation` 和 `report` 等核心数据结构，再设计接口。

### 第三阶段：后端能力

- 使用 Spring Boot + MyBatis 实现业务接口。
- 使用 JWT + Spring Security + RBAC 重构认证授权。
- 使用 Redis 管理验证码、设备状态和短期 AI 上下文。
- 使用 MQ 和 MQTT 处理设备数据、预警及控制指令。
- 根据数据特点引入时序数据库或 MongoDB。
- 使用 Docker Compose 编排项目依赖。

## 开源协议

MIT License
