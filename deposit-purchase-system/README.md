# 存款产品购买系统

湖南三湘银行手机银行「存款产品购买」模块，提供服务编排画布、实时流程发布与终端购买闭环。

## 目录结构

```
deposit-purchase-system/
├─ db/                     # 初始化脚本
├─ backend/                # SpringBoot3 服务
└─ frontend/               # Vue3 前端
```

## 快速开始

### 1. 准备依赖

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8
- Redis 7
- RocketMQ 5（示例使用本地事务，不启动亦可编译）

### 2. 初始化数据库

```bash
mysql -uroot -proot < db/deposit_product.sql
mysql -uroot -proot < db/orchestration_flow.sql
mysql -uroot -proot < db/atomic_service.sql
```

### 3. 启动后端

```bash
cd backend
mvn clean package
java -jar target/deposit-purchase-system-1.0.0.jar
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 http://localhost:5173 ，完成「列表→详情→购买→结果」流程。服务编排页面可保存及发布流程，发布后后端 Redis 缓存即时刷新。

## 压测建议

使用 Apache JMeter/wrk 对 `/purchase` 接口压测，示例命令：

```bash
wrk -t8 -c200 -d60s --latency -s scripts/purchase.lua http://localhost:8080/purchase
```

Lua 脚本需在请求头中附加 JWT 与签名信息。后端库存使用 Redis Lua 脚本保障原子扣减，幂等表避免重复扣款。

## 灰度发布方案

1. 编排流程发布时写入 Redis，并按照渠道维度缓存副本。
2. 前端下发带有 `flowCode` 的策略标签，可按客户号/风险等级选择流程模板。
3. 灰度期间同时保留旧模板，通过 RocketMQ 事务消息同步购买事件，便于快速回滚。
4. 完成验证后切换默认模板并下线旧模板。

## 关键特性

- 10 个原子服务（证件审查、风控、库存锁定、预扣款等）均为 Spring Bean，可被编排引擎动态调用。
- 编排引擎使用 JSON DAG，支持同步/异步节点、指数退避重试、补偿逻辑。
- 幂等表 `idempotent_record`、库存 Redis Lua、防超卖分桶、JWT+RSA256 签名校验。
- 前端基于 Vue3 + ArcoDesign + VueFlow 实现拖拽式流程设计器。

## 监控与运维

- RocketMQ 半消息在本地事务提交后确认，异常自动回滚。
- 可扩展接入 SkyWalking/Zipkin 进行链路追踪。
- 建议结合 Prometheus + Grafana 对 TPS、库存命中率、风控拦截率进行实时监控。
