# ===== 阶段1：构建 =====
FROM node:20-alpine AS builder
WORKDIR /build

ENV NODE_OPTIONS="--max-old-space-size=2048"

# 先COPY package.json和lock文件，利用Docker层缓存加速依赖安装
COPY package.json .
COPY pnpm-lock.yaml .

# 安装pnpm并使用pnpm install（与项目一致）
RUN corepack enable && corepack prepare pnpm@latest --activate
RUN pnpm install --frozen-lockfile

# 再COPY前端源码并构建
COPY . .
RUN pnpm run build

# ===== 阶段2：运行 =====
FROM nginx:1.27-alpine

LABEL maintainer="erp-team"
LABEL description="ERP Frontend Service"

# 删除Nginx默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 从构建阶段COPY dist产物
COPY --from=builder /build/dist /usr/share/nginx/html

# COPY自定义nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 健康检查静态文件
RUN echo "OK" > /usr/share/nginx/html/healthz

# 安装wget用于HEALTHCHECK
RUN apk add --no-cache wget

# 安全：关闭版本暴露
RUN sed -i 's/# server_tokens off;/server_tokens off;/' /etc/nginx/nginx.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -q --spider http://localhost:80/healthz || exit 1

STOPSIGNAL SIGQUIT
CMD ["nginx", "-g", "daemon off;"]
