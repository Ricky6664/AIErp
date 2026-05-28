# P0-013 部署与DevOps基础 - 模块开发指南

## 一、模块概述

- **模块定位**：Docker容器化配置 + Nginx反向代理与静态资源配置 + CI/CD流水线搭建，确保开发/测试/生产环境一致性与自动化部署能力
- **前置依赖**：P0-001（后端框架提供Spring Boot应用）、P0-002（前端框架提供Vue应用）
- **被依赖方**：P0-014（测试基础模块依赖CI/CD集成）、P1-001~P1-010（所有后续业务模块的部署运行环境）、P2级所有模块
- **开发阶段**：阶段一（基础设施）

## 二、本模块涉及的资源清单

### 2.1 数据表清单

无

### 2.2 数据视图清单

无

### 2.3 API接口清单

无

### 2.4 页面清单

无

## 三、子模块清单与执行顺序

| 子模块编号 | 子模块名称 | 是否有独立规范 | 前置依赖 |
|-----------|-----------|:---:|---------|
| P0-013-001 | Docker容器化 | 是 | P0-001、P0-002 |
| P0-013-002 | Nginx反向代理与静态资源 | 是 | P0-013-001 |
| P0-013-003 | CI/CD流水线 | 是 | P0-013-001、P0-013-002 |

## 四、模块级专属约束

1. **Docker Compose编排结构**：docker-compose.yml需包含以下服务：
   - `postgres`：PostgreSQL 16，端口5432，健康检查pg_isready，数据卷`erp_pgdata`
   - `redis`：Redis 7-alpine，端口6379，健康检查redis-cli ping，数据卷`erp_redis`
   - `minio`：MinIO latest，端口9000/9001，健康检查curl /minio/health/live，数据卷`erp_minio`
   - `elasticsearch`：Elasticsearch 8.x（单节点模式），端口9200，健康检查curl localhost:9200/_cluster/health，数据卷`erp_es`
   - `backend`：Spring Boot应用，端口8080，依赖postgres+redis+minio+elasticsearch，健康检查/actuator/health
   - `frontend`：Nginx托管静态资源，端口80，依赖backend
   - `nginx`：反向代理统一入口，端口443(HTTPS)/80(HTTP)，依赖backend+frontend
   - 所有服务使用`erp_network`桥接网络，敏感配置通过`.env`文件注入

2. **多环境变量管理**：
   - `.env.dev`：开发环境（数据库/Redis/MinIO本地端口，DEBUG日志级别）
   - `.env.staging`：预发布环境（独立数据库实例，INFO日志级别）
   - `.env.prod`：生产环境（高可用数据库集群，WARN日志级别，SSL启用）
   - 所有密钥（数据库密码/JWT密钥/API Key/MinIO凭证）禁止硬编码，必须通过`.env`文件或K8s Secret注入

3. **Dockerfile多阶段构建规范**：
   - **后端**：使用`maven:3.9-eclipse-temurin-17`作为构建镜像，`eclipse-temurin:17-jre`作为运行镜像；启用Spring Boot分层jar（layers）；运行用户为非root（`erpuser:1001`）；JVM参数通过ENV变量注入
   - **前端**：使用`node:20-alpine`作为构建镜像，`nginx:1.27-alpine`作为运行镜像；启用gzip压缩；设置CSP安全头；静态资源带content-hash文件名

4. **.dockerignore规范**：必须排除`node_modules/`、`target/`、`.git/`、`logs/`、`.env.local`、`*.log`、`.idea/`、`.vscode/`、`dist/`等目录，避免构建镜像过大

5. **Nginx配置规范**：
   - **upstream配置**：后端服务使用`upstream backend { server backend:8080; }`，支持未来扩展为多节点集群
   - **API反向代理**：`/api/*`路由到backend，WebSocket路径`/ws/*`单独配置Upgrade头
   - **静态资源缓存**：js/css/图片等静态资源配置`Cache-Control: public, max-age=31536000, immutable`，HTML配置`no-cache`
   - **gzip压缩**：启用gzip_min_length 1024，压缩text/css/application/javascript/application/json等类型
   - **安全头**：配置X-Frame-Options、X-Content-Type-Options、X-XSS-Protection、Strict-Transport-Security、Content-Security-Policy
   - **多环境配置**：dev/staging/prod各自独立nginx.conf，通过环境变量选择配置；prod环境必须配置SSL证书（Let's Encrypt或自签名证书）

6. **CI/CD流水线阶段**（GitHub Actions或GitLab CI）：
   - **代码检出**（checkout）：使用actions/checkout@v4
   - **依赖缓存**（cache）：Maven本地仓库+npm缓存，按lock文件hash作为缓存key
   - **编译构建**（build）：后端`mvn clean package -DskipTests`，前端`npm ci && npm run build`
   - **单元测试**（test）：后端`mvn test`+JaCoCo覆盖率报告，前端`npm run test:coverage`
   - **代码质量门禁**（lint）：后端Checkstyle+SpotBugs，前端ESLint+Prettier，任一失败则流水线中断
   - **SonarQube扫描**（占位）：预留SonarQube扫描stage，待SonarQube服务部署后启用
   - **Docker镜像构建**（docker-build）：使用`docker/build-push-action`，标签规则为`{仓库}:{git-sha-short}`和`{仓库}:latest`
   - **镜像推送**（docker-push）：推送到Docker Hub或私有Registry，使用Repository Secrets存储凭证
   - **部署**（deploy）：触发远程deploy.sh脚本，执行拉取镜像→备份数据库→滚动更新→健康检查→失败自动回滚

7. **自动化部署脚本（deploy.sh）规范**：
   - **备份**：部署前自动备份PostgreSQL数据库到`/backup/db/{date}_{time}.sql`，保留最近7天
   - **滚动更新**：使用`docker compose pull && docker compose up -d --remove-orphans`，避免全量停机
   - **健康检查**：部署后循环请求`/actuator/health`最多30次（每次间隔2s），未通过则触发回滚
   - **回滚机制**：保留最近3个版本镜像标签，回滚时使用`docker compose down && docker compose -f docker-compose.rollback.yml up -d`
   - **日志记录**：每次部署记录时间/版本/操作人/结果到`/var/log/erp-deploy.log`

8. **健康检查端点**：后端应用需暴露`/actuator/health`端点，Nginx通过`location /health`代理到该端点，配置`proxy_next_upstream error timeout`实现故障转移。
