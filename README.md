# FarmWise

FarmWise 是一个基于 Spring Boot 与 Vue 3 的智慧农业管理系统，覆盖地块、设备、环境监测、灌溉、预警、农事任务、AI 顾问和报告等业务。

> 项目第一阶段已于 2026 年 8 月完成。当前进入发布与第二阶段持续优化，开发机上的 Docker Compose 编排已经跑通，服务器切换仍在进行。

## 目标技术栈

- 前端：Vue 3、Vite、Element Plus、Tailwind CSS
- 后端：Java 21、Spring Boot 4.1、Spring MVC、MyBatis 4、Maven
- 数据：MySQL 8.0、Flyway、Redis
- 认证：Spring Security、JWT、RBAC
- 物联网：EMQX、MQTT、Eclipse Paho
- AI Agent：Spring AI
- 可观测性：Actuator、Micrometer
- 部署：Docker Compose、Nginx、Spring Boot

## 第二阶段开发计划

- [x] 使用 Docker Compose 编排前端、后端、Redis、EMQX 和虚拟设备模拟器；MySQL 继续使用外部共享实例。
- [x] 完成服务器上的 Docker Compose 与 Nginx 生产部署切换。
- [ ] 完成服务器 HTTPS 配置。
- [ ] 增加低频预警补偿扫描，覆盖事件丢失和异步判定失败。
- [ ] 建立用户操作审计日志与敏感信息脱敏规则。
- [ ] 根据实际热点引入环境阈值和最新传感器状态缓存。
- [ ] 为高增长列表和 AI 消息增加统一分页、游标查询和上下文摘要。
- [ ] 补充自动化测试、日志、指标、告警、备份与恢复流程。

## 开源协议

MIT License
