# FarmWise 后端架构说明

## 架构目标

后端重构采用前后端分离的模块化单体。业务模块在同一个 Spring Boot 应用中独立组织，共享本地事务；当前不引入微服务、服务注册、配置中心和分布式事务。

```text
com.agri.farmwise
├── common
├── security
├── land
├── device
├── monitoring
├── irrigation
├── alert
├── task
├── ai
└── report
```

每个业务模块内部管理自己的 `controller`、`service`、`mapper`、`model` 和 `dto`。

## 技术选型

| 技术 | 用途 | 选择原因 |
| --- | --- | --- |
| Java 21 | 开发语言 | LTS 版本，可使用 record、模式匹配等现代语言能力 |
| Spring Boot 4.1 | 应用基础 | 统一依赖和配置，支持 Java 21，适合重新搭建后端骨架 |
| Spring MVC | REST API | 与阻塞式 MyBatis/JDBC 一致，事务和调试简单 |
| MyBatis 4 | 数据访问 | SQL 可见且可控，适合监测、任务和报告等明确查询 |
| MySQL 8.0 | 长期业务数据 | 保存业务事实、关联关系、历史记录和 JSON 快照 |
| Flyway | 数据库迁移 | 版本化管理建表、索引、约束和种子数据 |
| Spring Security + JWT | 认证授权 | 统一认证上下文和 RBAC，避免业务代码手动读取 Session |
| Redis | 短期状态 | 保存验证码、刷新令牌、限流、设备状态和短期 AI 上下文 |
| EMQX + Eclipse Paho | 设备通信 | MQTT 适合设备上报、心跳、离线检测和控制指令 |
| Spring AI | AI Agent | 提供 ChatClient、结构化输出、对话记忆和工具调用抽象 |
| Actuator + Micrometer | 可观测性 | 观察 HTTP、JVM、数据库、Redis、MQTT 和 AI 调用状态 |
| Maven | 构建 | 延续现有工程习惯并使用 Spring Boot BOM 管理版本 |

Spring Boot 4.1 支持 Java 17 至 26；MyBatis Starter 4 对应 Spring Boot 4.x。参考 [Spring Boot 系统要求](https://docs.spring.io/spring-boot/system-requirements.html) 和 [MyBatis Starter 兼容表](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)。

## 数据边界

- MySQL 保存地块、设备档案、监测历史、灌溉记录、预警、任务、完整 AI 对话和报告快照。
- Redis 只保存带过期时间的短期状态，不作为业务事实来源。
- 设备通过 MQTT 与 EMQX 通信；浏览器只调用 REST API，不直接连接设备。
- AI 完整聊天历史保存在 MySQL，Spring AI Chat Memory 只负责模型需要的短期上下文。
- 报告快照和 AI 参考数据使用 MySQL JSON 字段，生成后保持只读。
- 数据库时间统一保存 UTC，面向用户时转换为北京时间。

## 数据库原则

- 在新的 `farmwise_v2` 数据库中重建结构，不直接修改当前远程数据库。
- 所有结构变化通过 Flyway SQL 迁移，不使用 JPA `ddl-auto`。
- 普通业务 ID 使用 UUID 字符串；枚举使用 `VARCHAR` 代码，不使用 MySQL `ENUM`。
- 面积、经纬度和传感器数值使用 `DECIMAL`，避免浮点误差。
- `sensor_readings` 同时记录 `device_id` 和采集时的 `land_id`，保留历史绑定关系。
- 地块或设备存在历史业务数据时，通过业务校验和外键共同阻止删除。
- 优先为地块、状态、指标和时间等组合查询建立索引，暂不引入时序数据库或 MongoDB。

## 不保留的技术

| 技术 | 不保留原因 |
| --- | --- |
| Spring Data JPA | 避免与 MyBatis 并存两套映射和数据访问风格 |
| XML Mapper | 当前 SQL 使用 MyBatis 注解表达，保持实现位置集中 |
| Spring Cloud、OpenFeign | 当前是单体应用，没有服务发现和服务间调用需求 |
| WebFlux | MyBatis/JDBC 是阻塞式，响应式链路不会带来实际收益 |
| 自定义 Session 权限体系 | 改由 Spring Security 管理认证上下文和授权；`@RequiredPermission` 可保留为业务注解 |
| RabbitMQ、Kafka | MQTT 已覆盖当前设备通信，内部事件规模尚不需要第二套消息中间件 |
| 时序数据库、MongoDB | 当前数据规模和查询可由 MySQL 8 完成，避免过早增加运维负担 |
| 未使用的 POI、iText | 报告导出实现前不保留重型依赖，届时再按格式需求选择 |

## 保留但收敛的能力

- 保留邮件组件，用于验证码等已有邮件业务。
- 保留审计和日志异步执行器，不使用公共线程池执行后台任务。
- Lombok 只用于减少实体样板代码；请求和响应 DTO 优先使用 Java `record`。
- 保留 `@RequiredPermission` 的业务表达，但底层授权统一接入 Spring Security。

## 实施顺序

1. 设计 ER 关系、字段、索引和删除约束。
2. 清理 `pom.xml`，建立 Spring Boot 4.1 模块化骨架。
3. 引入 Flyway，创建 `farmwise_v2` 第一版结构和演示数据。
4. 完成 Spring Security、JWT 和 RBAC 基础能力。
5. 按 [接口文档](api-contract.md) 顺序实现地块到报告模块。
6. 接入 Redis、EMQX/MQTT 和 Spring AI。
7. 最后补充 Docker Compose 与可观测性配置。
