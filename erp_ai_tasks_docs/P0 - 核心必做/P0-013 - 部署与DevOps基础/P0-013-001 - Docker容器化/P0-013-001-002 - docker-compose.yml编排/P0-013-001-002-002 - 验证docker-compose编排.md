# P0-013-001-002-002 验证docker-compose编排

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-013-001-002-002 |
| 任务名称 | 验证docker-compose编排 |
| 所属模块 | P0-013 |
| 优先级 | P0 |
| 任务类型 | 验证/测试 |

## 二、任务目标

docker compose up+所有服务启动+健康检查通过+服务间通信正常

## 三、前置依赖

### 3.1 前置任务

- P0-013-001-002 docker-compose.yml编排（父任务）
- P0-013-001-002-001 编写docker-compose服务编排（前序兄弟任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用

| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-部署架构规范 | Docker Compose编排与验证标准 |
| 全局规范-测试开发规范 | 验证方法与测试报告规范 |

## 五、详细开发规格

> **本任务模块上下文**（来源：P0-013模块开发指南）
> - 验证范围：docker-compose.yml中定义的7个服务（postgres/redis/minio/elasticsearch/backend/frontend/nginx）
> - 验证目标：所有服务正常启动、健康检查通过、服务间通信正常、端到端可访问

### 5.1 验证清单（按任务目标逐项验证）

1. **YAML语法校验**
   - 执行 `docker compose config`，无错误输出
   - 执行 `docker compose config --services`，列出全部7个服务

2. **镜像构建与拉取**
   - 执行 `docker compose build`，backend与frontend镜像构建成功
   - 执行 `docker compose pull`，postgres/redis/minio/elasticsearch/nginx官方镜像拉取成功

3. **服务启动**
   - 执行 `docker compose up -d`，所有容器启动无错误
   - 执行 `docker compose ps`，所有服务状态为 `Up` 且 `healthy`
   - 启动顺序正确：基础设施服务先启动，backend次之，frontend与nginx最后

4. **健康检查验证**
   - postgres：`docker exec <postgres> pg_isready -U erp` 返回 `accepting connections`
   - redis：`docker exec <redis> redis-cli ping` 返回 `PONG`
   - minio：`curl http://localhost:9000/minio/health/live` 返回200
   - elasticsearch：`curl http://localhost:9200/_cluster/health` 返回 `status: green/yellow`
   - backend：`curl http://localhost:8080/actuator/health` 返回 `{"status":"UP"}`
   - frontend：`curl http://localhost:3000/` 返回200
   - nginx：`curl http://localhost/health` 返回200

5. **服务间通信验证**
   - backend → postgres：`docker exec backend sh -c "nc -zv postgres 5432"` 成功
   - backend → redis：`docker exec backend sh -c "nc -zv redis 6379"` 成功
   - backend → minio：`docker exec backend sh -c "nc -zv minio 9000"` 成功
   - backend → elasticsearch：`docker exec backend sh -c "nc -zv elasticsearch 9200"` 成功
   - nginx → backend：`docker exec nginx sh -c "nc -zv backend 8080"` 成功
   - nginx → frontend：`docker exec nginx sh -c "nc -zv frontend 80"` 成功

6. **数据卷持久化验证**
   - 在postgres中创建测试表并插入数据
   - 执行 `docker compose down` 后再 `docker compose up -d`
   - 验证测试表与数据仍然存在

7. **端到端访问验证**
   - 浏览器访问 `http://localhost` 正常打开前端页面
   - 前端API请求通过nginx代理到backend，返回正常数据
   - WebSocket连接（如 `/ws/*`）建立成功

8. **故障恢复验证**
   - 执行 `docker compose stop backend` 停止backend
   - 执行 `docker compose start backend` 重启
   - 验证backend重新变为healthy，nginx代理恢复

### 5.2 测试方法

1. **自动化脚本**：编写 `scripts/verify-compose.sh`，循环检查上述所有验证项，输出PASS/FAIL报告
2. **手动验证**：对UI相关验证项（端到端访问、WebSocket）进行人工验证
3. **日志检查**：`docker compose logs --tail=50 <service>` 检查是否有ERROR日志

### 5.3 结果记录

通过项 + 未通过项 + 修复方案，记录在 `docs/verification/docker-compose-report.md`：

```markdown
## docker-compose编排验证报告

| 验证项 | 结果 | 备注 |
|--------|------|------|
| YAML语法校验 | PASS/FAIL | |
| 镜像构建 | PASS/FAIL | |
| 服务启动 | PASS/FAIL | |
| 健康检查 | PASS/FAIL | 哪个服务失败 |
| 服务间通信 | PASS/FAIL | |
| 数据卷持久化 | PASS/FAIL | |
| 端到端访问 | PASS/FAIL | |
| 故障恢复 | PASS/FAIL | |
```

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | `scripts/verify-compose.sh` | 自动化验证脚本 |
| 2 | `docs/verification/docker-compose-report.md` | 验证报告（PASS/FAIL+备注） |
| 3 | 问题清单与修复方案 | 发现的任何问题和修复记录 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | 7个服务全部启动且状态为 healthy | 执行 `docker compose ps` |
| 2 | 所有健康检查端点返回200/UP | 自动化脚本或手动curl |
| 3 | 服务间网络通信全部成功 | 自动化脚本或手动nc测试 |
| 4 | 数据卷持久化验证通过 | down+up后数据仍在 |
| 5 | 端到端访问正常 | 浏览器访问+API请求+WebSocket |
| 6 | 验证报告已记录 | 检查 `docker-compose-report.md` |
| 7 | 发现的问题已修复或标记到问题清单 | 检查问题清单文档 |

## 八、易错警示

> ⚠️ 验证前确保本地80/443/5432/6379/8080/9000/9200端口未被其他进程占用，使用 `lsof -i :<port>` 检查

> ⚠️ 验证失败时优先检查 `docker compose logs <service>` 输出，常见错误：环境变量未设置、端口冲突、磁盘空间不足

> ⚠️ Elasticsearch启动需要系统 `vm.max_map_count >= 262144`，否则容器会启动失败，使用 `sysctl -w vm.max_map_count=262144` 调整

> ⚠️ backend服务启动时间较长（60s+），不要在健康检查start-period内判定为失败

> ⚠️ 验证完清理环境时使用 `docker compose down -v` 会删除数据卷，谨慎使用；普通 `docker compose down` 保留数据卷

> ⚠️ 不要在验证报告中只记录PASS项，FAIL项和修复方案同样重要
