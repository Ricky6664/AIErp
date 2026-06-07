# ERP AI 环境变量说明文档

> 生成时间：2026-06-08
> 对应任务：P0-013-001-003

## 一、概述

本项目使用 `.env` 文件管理多环境配置，变量按功能模块分组，统一使用大写蛇形命名（UPPER_SNAKE_CASE）。

所有敏感信息（密码、密钥）通过环境变量外部化，不硬编码在代码或配置文件中。

## 二、环境文件

| 文件 | 用途 | Git |
|------|------|:---:|
| `.env.example` | 变量模板，含所有变量及注释说明 | ✅ 提交 |
| `.env.dev` | 本地开发环境 | ❌ 不入库 |
| `.env.staging` | 预发布/验收环境 | ❌ 不入库 |
| `.env.prod` | 生产环境 | ❌ 不入库 |

## 三、变量清单

### 3.1 通用配置

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `COMPOSE_PROJECT_NAME` | string | `erp` | Docker Compose 项目名称 |
| `ENV` | string | `dev` | 当前环境标识：dev / staging / prod |
| `TZ` | string | `Asia/Shanghai` | 容器时区 |
| `TAG` | string | `latest` | 镜像标签 |

### 3.2 Nginx 端口

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `NGINX_HTTP_PORT` | int | `80` | HTTP 监听端口 |
| `NGINX_HTTPS_PORT` | int | `443` | HTTPS 监听端口 |

### 3.3 PostgreSQL

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `DB_HOST` | string | `postgres` | 数据库主机名（容器内服务名） |
| `DB_PORT` | int | `5432` | 数据库端口 |
| `DB_NAME` | string | `erp` | 数据库名称 |
| `DB_USER` | string | `erp_user` | 数据库用户名 |
| `DB_PASSWORD` | string | — | 数据库密码（必填，≥16字符强密码） |
| `DB_POOL_SIZE` | int | `20` | 连接池大小 |

### 3.4 Redis

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `REDIS_HOST` | string | `redis` | Redis 主机名 |
| `REDIS_PORT` | int | `6379` | Redis 端口 |
| `REDIS_PASSWORD` | string | — | Redis 密码（必填） |

### 3.5 MinIO 对象存储

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `MINIO_ENDPOINT` | string | `http://minio:9000` | MinIO 服务端点 |
| `MINIO_ACCESS_KEY` | string | `erp_minio` | MinIO Access Key |
| `MINIO_SECRET_KEY` | string | — | MinIO Secret Key（必填） |
| `MINIO_BUCKET` | string | `erp-files` | 默认存储桶名称 |

### 3.6 Elasticsearch

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `ES_HOST` | string | `elasticsearch` | Elasticsearch 主机名 |
| `ES_PORT` | int | `9200` | Elasticsearch 端口 |
| `ES_USERNAME` | string | `elastic` | ES 用户名（生产环境启用 xpack 后生效） |
| `ES_PASSWORD` | string | — | ES 密码（生产环境必填） |

### 3.7 后端应用 (Spring Boot)

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `SPRING_PROFILES_ACTIVE` | string | `prod` | Spring 激活的配置文件：dev / staging / prod |
| `JAVA_OPTS` | string | `-Xms512m -Xmx1024m -XX:+UseG1GC` | JVM 启动参数 |
| `JWT_SECRET` | string | — | JWT 签名密钥（必填，256位随机字符串） |
| `JWT_EXPIRATION` | int | `86400` | JWT Token 过期时间（秒，默认24小时） |

### 3.8 日志级别

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `LOGGING_LEVEL_ROOT` | string | `INFO` | 根日志级别 |
| `LOGGING_LEVEL_COM_ERP` | string | `INFO` | ERP 业务包日志级别 |

### 3.9 前端应用

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `VITE_API_BASE_URL` | string | `/api` | 前端 API 请求基础路径 |
| `VITE_APP_TITLE` | string | `ERP管理系统` | 前端页面标题 |

### 3.10 外部服务（可选）

| 变量 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `SMTP_HOST` | string | — | SMTP 邮件服务器地址 |
| `SMTP_PORT` | int | `587` | SMTP 端口 |
| `SMTP_USER` | string | — | SMTP 用户名 |
| `SMTP_PASSWORD` | string | — | SMTP 密码 |
| `SMS_API_KEY` | string | — | 短信服务 API Key |

## 四、环境差异对照

| 配置项 | dev | staging | prod |
|--------|-----|---------|------|
| `ENV` | dev | staging | prod |
| `SPRING_PROFILES_ACTIVE` | dev | staging | prod |
| `JAVA_OPTS` | `-Xms256m -Xmx512m` + jdwp | `-Xms512m -Xmx1024m` + G1GC | `-Xms1024m -Xmx2048m` + G1GC |
| `DB_PASSWORD` | 弱密码可接受 | 强密码 | 强密码 + 定期轮换 |
| `VITE_API_BASE_URL` | `http://localhost:8080` | `/api` | `/api` |
| `LOGGING_LEVEL_ROOT` | INFO | INFO | WARN |
| `LOGGING_LEVEL_COM_ERP` | DEBUG | INFO | INFO |
| `ES xpack security` | 关闭 | 可选 | 强制启用 |
| SSL | 关闭 | 可选 | 强制启用 |

## 五、密钥生成命令

```bash
# 数据库密码 / Redis 密码 / MinIO Secret Key / ES 密码
openssl rand -base64 32

# JWT 签名密钥（256位）
openssl rand -hex 32
```

## 六、使用方式

```bash
# 开发环境
docker compose --env-file .env.dev up -d

# 预发布环境
docker compose --env-file .env.staging up -d

# 生产环境
docker compose --env-file .env.prod up -d
```

## 七、安全注意事项

1. **绝对禁止**将 `.env.dev` / `.env.staging` / `.env.prod` 提交到 Git
2. 仓库中仅保留 `.env.example`（使用 `<replace-me>` 占位符）
3. 生产环境所有密码必须使用 `openssl rand` 生成强密码（≥16字符）
4. 生产环境 `.env.prod` 在服务器上应设置 `chmod 600`（仅属主可读）
5. 生产环境推荐使用 K8s Secret / HashiCorp Vault 管理密钥
6. JWT_SECRET 泄露可导致所有 Token 被伪造，必须妥善保管
7. 不同环境的数据库必须物理隔离，禁止 dev/staging 连接 prod 数据库
8. 环境变量变更后必须重启对应服务：`docker compose up -d --force-recreate`
