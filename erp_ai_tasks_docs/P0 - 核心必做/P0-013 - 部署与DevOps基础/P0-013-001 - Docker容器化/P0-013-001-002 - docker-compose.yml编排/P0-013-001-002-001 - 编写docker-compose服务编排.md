# P0-013-001-002-001 编写docker-compose服务编排

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-013-001-002-001 |
| 任务名称 | 编写docker-compose服务编排 |
| 所属模块 | P0-013 |
| 优先级 | P0 |
| 任务类型 | 配置/基础设施 |

## 二、任务目标

各服务+健康检查+网络+卷挂载+环境变量+.env文件

## 三、前置依赖

### 3.1 前置任务

- P0-013-001-002 docker-compose.yml编排（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用

| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-部署架构规范 | Docker Compose编排标准与服务定义规范 |
| 全局规范-项目架构与开发约束 | 技术栈版本约束（PG/Redis/MinIO/ES版本） |
| 全局规范-安全开发规范 | 密钥管理、网络隔离与非root用户 |

## 五、详细开发规格

> **本任务模块上下文**
> - 涉及数据表：无独立业务表(DevOps基础设施)
> - 涉及产出：完整的Docker Compose编排文件，定义7个服务 + 网络 + 卷

### 5.1 服务定义

`docker-compose.yml` 必须包含以下7个服务，按依赖关系组织：

1. **postgres**（PostgreSQL 16）
   - 镜像：`postgres:16-alpine`
   - 端口：`5432:5432`
   - 环境变量：`POSTGRES_DB=erp`、`POSTGRES_USER`、`POSTGRES_PASSWORD`（来自.env）
   - 健康检查：`pg_isready -U $POSTGRES_USER -d $POSTGRES_DB`，间隔10s
   - 数据卷：`erp_pgdata:/var/lib/postgresql/data`
   - 启动参数：`-c shared_buffers=256MB -c max_connections=200`

2. **redis**（Redis 7-alpine）
   - 镜像：`redis:7-alpine`
   - 端口：`6379:6379`
   - 健康检查：`redis-cli ping`，间隔10s
   - 数据卷：`erp_redis:/data`
   - 启动命令：`redis-server --appendonly yes --requirepass $REDIS_PASSWORD`

3. **minio**（MinIO latest）
   - 镜像：`minio/minio:latest`
   - 端口：`9000:9000`（API）、`9001:9001`（Console）
   - 环境变量：`MINIO_ROOT_USER`、`MINIO_ROOT_PASSWORD`（来自.env）
   - 健康检查：`curl -f http://localhost:9000/minio/health/live`，间隔30s
   - 数据卷：`erp_minio:/data`
   - 启动命令：`minio server /data --console-address ":9001"`

4. **elasticsearch**（Elasticsearch 8.x 单节点）
   - 镜像：`elasticsearch:8.13.0`
   - 端口：`9200:9200`
   - 环境变量：`discovery.type=single-node`、`ES_JAVA_OPTS=-Xms512m -Xmx512m`、`xpack.security.enabled=false`
   - 健康检查：`curl -f http://localhost:9200/_cluster/health`，间隔30s
   - 数据卷：`erp_es:/usr/share/elasticsearch/data`

5. **backend**（Spring Boot应用）
   - 构建：`context: ., dockerfile: deploy/backend/Dockerfile`
   - 镜像：`erp-backend:${TAG:-latest}`
   - 端口：`8080:8080`
   - 环境变量：通过 `env_file: .env` 注入，覆盖 `SPRING_PROFILES_ACTIVE`、`JAVA_OPTS` 等
   - 健康检查：`wget -q --spider http://localhost:8080/actuator/health`，间隔30s，start-period 60s
   - 依赖：`depends_on: postgres/service_healthy, redis/service_healthy, minio/service_healthy, elasticsearch/service_healthy`

6. **frontend**（Nginx托管静态资源）
   - 构建：`context: ., dockerfile: deploy/frontend/Dockerfile`
   - 镜像：`erp-frontend:${TAG:-latest}`
   - 端口：`3000:80`（内部端口，不对外暴露）
   - 健康检查：`wget -q --spider http://localhost:80/`，间隔30s
   - 依赖：`depends_on: backend/service_healthy`

7. **nginx**（反向代理统一入口）
   - 镜像：`nginx:1.27-alpine`
   - 端口：`80:80`（HTTP）、`443:443`（HTTPS，prod环境）
   - 配置卷：`./deploy/nginx/nginx.${ENV}.conf:/etc/nginx/nginx.conf:ro`
   - 健康检查：`wget -q --spider http://localhost:80/health`，间隔30s
   - 依赖：`depends_on: backend/service_healthy, frontend/service_healthy`

### 5.2 网络配置

1. 定义桥接网络 `erp_network`：`driver: bridge`
2. 所有7个服务加入同一网络，可通过服务名互相访问（如backend访问 `postgres:5432`）
3. 对外仅暴露nginx的80/443端口，其他服务端口仅内部可达

### 5.3 卷挂载

定义命名卷（named volumes）持久化数据：

1. `erp_pgdata`：PostgreSQL数据目录
2. `erp_redis`：Redis持久化数据（AOF）
3. `erp_minio`：MinIO对象存储数据
4. `erp_es`：Elasticsearch索引数据

```yaml
volumes:
  erp_pgdata:
  erp_redis:
  erp_minio:
  erp_es:
```

### 5.4 环境变量与.env文件

1. 使用 `env_file: .env` 将变量注入到各服务
2. 敏感变量（密码、密钥）在docker-compose.yml中使用 `${VAR_NAME}` 引用，不直接写死
3. 示例变量：
   ```yaml
   environment:
     POSTGRES_PASSWORD: ${DB_PASSWORD}
     REDIS_PASSWORD: ${REDIS_PASSWORD}
     MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY}
   ```

### 5.5 健康检查与依赖顺序

1. 所有服务必须配置 `healthcheck`，使用 `test` + `interval` + `timeout` + `retries`
2. 使用 `depends_on` 的 `condition: service_healthy` 确保依赖服务就绪后再启动
3. 启动顺序：postgres/redis/minio/elasticsearch（并行）→ backend → frontend → nginx

### 5.6 重启策略

1. 所有服务配置 `restart: unless-stopped`
2. 生产环境可升级为 `restart: always`

### 5.7 资源限制（可选）

1. 为每个服务配置 `deploy.resources.limits` 限制CPU与内存：
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '2.0'
         memory: 2G
   ```

### 5.8 验证

1. `docker compose config` 校验YAML语法与配置正确性
2. `docker compose up -d` 启动所有服务
3. `docker compose ps` 查看所有服务状态为 `healthy`
4. `docker compose logs` 检查无错误日志
5. 通过浏览器访问 `http://localhost` 可正常打开前端页面
6. 通过 `curl http://localhost/actuator/health` 可访问后端健康检查

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | `docker-compose.yml`（项目根目录） | 主编排文件，定义7个服务+网络+卷 |
| 2 | `docker-compose.override.yml`（可选） | 开发环境覆盖（挂载源码目录、开启调试端口） |
| 3 | `docker-compose.prod.yml`（可选） | 生产环境覆盖（资源限制、日志驱动） |
| 4 | `.env`（或 `.env.example`） | 环境变量模板，敏感值由实际环境填充 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | `docker compose config` 校验通过无错误 | 执行 `docker compose config` |
| 2 | 7个服务全部定义完整（postgres/redis/minio/elasticsearch/backend/frontend/nginx） | 检查 `docker compose config --services` |
| 3 | 所有服务配置健康检查 | grep `healthcheck` in docker-compose.yml |
| 4 | `docker compose up -d` 启动成功，所有服务状态为 healthy | 执行 `docker compose ps` |
| 5 | 服务间网络互通（backend可访问postgres:5432） | 执行 `docker exec backend ping postgres` |
| 6 | 敏感信息全部通过 `${VAR}` 引用，未硬编码 | grep 检查docker-compose.yml |
| 7 | 数据卷正确挂载，容器重启后数据不丢失 | 重启postgres容器后验证数据存在 |
| 8 | 依赖顺序正确（backend启动时postgres已healthy） | 观察 `docker compose logs backend` 启动时机 |

## 八、易错警示

> ⚠️ 不要在docker-compose.yml中硬编码密码或密钥，必须使用 `${VAR}` 引用.env文件

> ⚠️ 必须使用 `condition: service_healthy` 而不是简单的 `depends_on`，否则依赖服务未就绪时backend会启动失败

> ⚠️ Elasticsearch需要设置 `discovery.type=single-node`，否则多节点发现机制会导致启动失败

> ⚠️ Elasticsearch默认开启xpack security，开发环境需设置 `xpack.security.enabled=false` 或配置密码

> ⚠️ MinIO的API端口（9000）和Console端口（9001）不要混淆，前端访问的是9001

> ⚠️ postgres镜像的初始化脚本仅在数据卷首次创建时执行，修改POSTGRES_DB/USER后需要清空 `erp_pgdata` 卷才生效

> ⚠️ 不要对外暴露postgres/redis/minio/elasticsearch端口到0.0.0.0，生产环境应绑定127.0.0.1或通过防火墙限制

> ⚠️ `restart: unless-stopped` 在开发调试时可能造成容器无限重启，调试时可临时改为 `restart: "no"`

> ⚠️ 命名卷（named volumes）与绑定挂载（bind mounts）不要混用，命名卷更适合跨平台与数据持久化
