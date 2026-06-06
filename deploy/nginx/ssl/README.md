# SSL 证书管理说明

> **目录**: `deploy/nginx/ssl/`
> **用途**: 存放 Nginx SSL/TLS 证书文件（仅生产/预发布环境使用）
> **安全策略**: 本目录下所有 `.crt`、`.key`、`.pem` 文件均加入 `.gitignore`，禁止提交到版本控制

## 证书文件清单（不入库，通过 Secret 管理或运维手动部署）

| 环境 | 证书文件 | 私钥文件 | 颁发方式 |
|------|---------|---------|---------|
| staging | `staging.erp.example.com.crt` | `staging.erp.example.com.key` | Let's Encrypt 或自签名 |
| prod | `erp.example.com.crt` | `erp.example.com.key` | Let's Encrypt（推荐）|

## Let's Encrypt 证书申请与续期（生产推荐）

### 申请证书

```bash
certbot certonly --nginx -d erp.example.com
```

证书自动存放于 `/etc/letsencrypt/live/erp.example.com/`，复制到本目录：

```bash
cp /etc/letsencrypt/live/erp.example.com/fullchain.pem deploy/nginx/ssl/erp.example.com.crt
cp /etc/letsencrypt/live/erp.example.com/privkey.pem   deploy/nginx/ssl/erp.example.com.key
chmod 600 deploy/nginx/ssl/erp.example.com.key
```

### 自动续期

Let's Encrypt 证书有效期 90 天，必须配置自动续期：

```bash
# 添加 cron 任务（每天凌晨 2 点检查续期）
0 2 * * * certbot renew --quiet --post-hook "cp /etc/letsencrypt/live/erp.example.com/fullchain.pem /path/to/deploy/nginx/ssl/erp.example.com.crt && cp /etc/letsencrypt/live/erp.example.com/privkey.pem /path/to/deploy/nginx/ssl/erp.example.com.key && docker compose -f /path/to/docker-compose.yml restart nginx"
```

## 自签名证书（仅开发/测试环境）

```bash
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout deploy/nginx/ssl/staging.erp.example.com.key \
  -out deploy/nginx/ssl/staging.erp.example.com.crt \
  -subj "/C=CN/ST=Shanghai/L=Shanghai/O=ERP/CN=staging.erp.example.com"
```

## Docker Compose 挂载

生产环境启动时挂载 SSL 目录：

```bash
ENV=prod docker compose --env-file .env.prod up -d
```

docker-compose.yml 中 nginx 服务的卷配置应包含：

```yaml
volumes:
  - ./deploy/nginx/nginx.conf:/etc/nginx/nginx.conf:ro
  - ./deploy/nginx/nginx.${ENV:-dev}.conf:/etc/nginx/conf.d/default.conf:ro
  - ./deploy/nginx/ssl:/etc/nginx/ssl:ro
```

## 安全注意事项

- 私钥文件（`.key`）权限必须设为 `600`（仅所有者可读写）
- 证书文件禁止提交到 Git，已在 `.gitignore` 中排除
- 生产环境证书建议通过 CI/CD Secret 变量或 Vault 注入，而非手动复制
- 定期检查证书到期时间：`openssl x509 -in deploy/nginx/ssl/erp.example.com.crt -noout -enddate`
- SSL Labs 评分检测：https://www.ssllabs.com/ssltest/analyze.html?d=erp.example.com
