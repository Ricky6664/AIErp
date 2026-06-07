#!/usr/bin/env bash
# ============================================================
# verify-compose.sh — docker-compose编排自动化验证脚本
# 任务: P0-013-001-002-002
# ============================================================
set -euo pipefail

# ---- 颜色定义 ----
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

PASS=0
FAIL=0
RESULTS=()

# ---- 辅助函数 ----
log_pass() { echo -e "${GREEN}[PASS]${NC} $1"; PASS=$((PASS+1)); RESULTS+=("| $1 | PASS | |"); }
log_fail() { echo -e "${RED}[FAIL]${NC} $1 — $2"; FAIL=$((FAIL+1)); RESULTS+=("| $1 | FAIL | $2 |"); }
log_info() { echo -e "${YELLOW}[INFO]${NC} $1"; }

check_cmd() { command -v "$1" >/dev/null 2>&1 || { log_fail "依赖检查" "$1 未安装"; exit 1; }; }

# ---- 前置检查 ----
echo "============================================"
echo " docker-compose 编排验证脚本"
echo " 开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================"
echo ""

log_info "前置依赖检查..."
check_cmd docker
check_cmd curl
check_cmd nc
log_pass "前置依赖检查"
echo ""

# ---- 1. YAML语法校验 ----
echo "--- 1. YAML语法校验 ---"
if docker compose -f docker-compose.yml config >/dev/null 2>&1; then
  log_pass "docker-compose.yml YAML语法校验"
else
  log_fail "docker-compose.yml YAML语法校验" "docker compose config 返回非0"
fi

SERVICES=$(docker compose config --services 2>/dev/null)
SERVICE_COUNT=$(echo "$SERVICES" | wc -l)
log_info "检测到 ${SERVICE_COUNT} 个服务: $(echo "$SERVICES" | tr '\n' ' ')"

EXPECTED_SERVICES=("postgres" "redis" "minio" "elasticsearch" "backend" "frontend" "nginx")
for svc in "${EXPECTED_SERVICES[@]}"; do
  if echo "$SERVICES" | grep -q "^${svc}$"; then
    log_pass "服务 ${svc} 已定义"
  else
    log_fail "服务 ${svc} 已定义" "docker-compose.yml 中未找到 ${svc} 服务"
  fi
done
echo ""

# ---- 2. 镜像构建与拉取 ----
echo "--- 2. 镜像构建与拉取 ---"
log_info "构建 backend 镜像..."
if docker compose build backend 2>&1 | tail -5; then
  log_pass "backend 镜像构建"
else
  log_fail "backend 镜像构建" "构建失败，检查 Dockerfile 和构建日志"
fi

log_info "构建 frontend 镜像..."
if docker compose build frontend 2>&1 | tail -5; then
  log_pass "frontend 镜像构建"
else
  log_fail "frontend 镜像构建" "构建失败，检查前端 Dockerfile 和构建日志"
fi

INFRA_IMAGES=("postgres" "redis" "minio" "elasticsearch" "nginx")
for img in "${INFRA_IMAGES[@]}"; do
  if docker compose pull "${img}" 2>&1 | tail -3; then
    log_pass "${img} 镜像拉取"
  else
    log_fail "${img} 镜像拉取" "拉取失败，检查网络连接"
  fi
done
echo ""

# ---- 3. 服务启动 ----
echo "--- 3. 服务启动 ---"
log_info "启动所有服务 (docker compose up -d)..."
if docker compose up -d 2>&1; then
  log_pass "docker compose up -d 命令执行成功"
else
  log_fail "docker compose up -d" "启动失败，检查 docker compose logs"
  exit 1
fi

log_info "等待服务启动 (90s)..."
sleep 90

RUNNING=$(docker compose ps --format json 2>/dev/null | wc -l)
log_info "运行中容器数: ${RUNNING}"

for svc in "${EXPECTED_SERVICES[@]}"; do
  STATUS=$(docker compose ps --format json 2>/dev/null | grep "\"Name\":\"erp-${svc}\"" | grep -o '"Health":"[^"]*"' | cut -d'"' -f4 || echo "unknown")
  if [ "$STATUS" = "healthy" ]; then
    log_pass "${svc} 状态 healthy"
  elif [ "$STATUS" = "unknown" ]; then
    log_fail "${svc} 容器状态" "容器未运行或不存在"
  else
    log_fail "${svc} 状态" "当前状态: ${STATUS}，预期 healthy"
  fi
done
echo ""

# ---- 4. 健康检查验证 ----
echo "--- 4. 健康检查验证 ---"
# postgres
if docker exec erp-postgres pg_isready -U "${POSTGRES_USER:-erp_user}" 2>/dev/null | grep -q "accepting connections"; then
  log_pass "postgres 健康检查 (pg_isready)"
else
  log_fail "postgres 健康检查" "pg_isready 返回非 accepting connections"
fi

# redis
PONG=$(docker exec erp-redis redis-cli -a "${REDIS_PASSWORD}" --no-auth-warning ping 2>/dev/null || echo "")
if [ "$PONG" = "PONG" ]; then
  log_pass "redis 健康检查 (PING→PONG)"
else
  log_fail "redis 健康检查" "redis-cli ping 未返回 PONG"
fi

# minio
MINIO_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9000/minio/health/live 2>/dev/null || echo "000")
if [ "$MINIO_STATUS" = "200" ]; then
  log_pass "minio 健康检查 (HTTP ${MINIO_STATUS})"
else
  log_fail "minio 健康检查" "HTTP ${MINIO_STATUS}，预期 200"
fi

# elasticsearch
ES_STATUS=$(curl -s http://localhost:9200/_cluster/health 2>/dev/null | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "unreachable")
if [ "$ES_STATUS" = "green" ] || [ "$ES_STATUS" = "yellow" ]; then
  log_pass "elasticsearch 健康检查 (status: ${ES_STATUS})"
else
  log_fail "elasticsearch 健康检查" "状态: ${ES_STATUS}，预期 green/yellow"
fi

# backend
BACKEND_RESP=$(curl -s http://localhost:8080/actuator/health 2>/dev/null || echo '{"status":"DOWN"}')
if echo "$BACKEND_RESP" | grep -q '"status":"UP"'; then
  log_pass "backend 健康检查 (actuator/health UP)"
else
  log_fail "backend 健康检查" "actuator/health 未返回 UP"
fi

# frontend
FRONTEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000/ 2>/dev/null || echo "000")
if [ "$FRONTEND_STATUS" = "200" ]; then
  log_pass "frontend 健康检查 (HTTP ${FRONTEND_STATUS})"
else
  log_fail "frontend 健康检查" "HTTP ${FRONTEND_STATUS}，预期 200"
fi

# nginx
NGINX_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:80/health 2>/dev/null || echo "000")
if [ "$NGINX_HEALTH" = "200" ]; then
  log_pass "nginx 健康检查 (/health HTTP ${NGINX_HEALTH})"
else
  log_fail "nginx 健康检查" "/health HTTP ${NGINX_HEALTH}，预期 200"
fi
echo ""

# ---- 5. 服务间通信验证 ----
echo "--- 5. 服务间通信验证 ---"
check_conn() {
  local from=$1 to=$2 port=$3
  if docker exec "erp-${from}" sh -c "nc -zv ${to} ${port}" 2>&1 | grep -q "succeeded\|open\|Connected"; then
    log_pass "${from} → ${to}:${port} 通信"
  else
    log_fail "${from} → ${to}:${port} 通信" "网络不可达"
  fi
}

check_conn "backend" "postgres" "5432"
check_conn "backend" "redis" "6379"
check_conn "backend" "minio" "9000"
check_conn "backend" "elasticsearch" "9200"
check_conn "nginx" "backend" "8080"
check_conn "nginx" "frontend" "80"
echo ""

# ---- 6. 数据卷持久化验证 ----
echo "--- 6. 数据卷持久化验证 ---"
log_info "在 postgres 中创建测试表..."
docker exec erp-postgres psql -U "${POSTGRES_USER:-erp_user}" -d "${POSTGRES_DB:-erp}" \
  -c "CREATE TABLE IF NOT EXISTS _verify_test (id SERIAL PRIMARY KEY, msg TEXT); INSERT INTO _verify_test (msg) VALUES ('persistence-check-$(date +%s)');" 2>/dev/null

INSERT_COUNT=$(docker exec erp-postgres psql -U "${POSTGRES_USER:-erp_user}" -d "${POSTGRES_DB:-erp}" \
  -t -c "SELECT COUNT(*) FROM _verify_test;" 2>/dev/null | tr -d ' ')

log_info "停止所有服务 (docker compose down)..."
docker compose down 2>/dev/null

log_info "重新启动服务 (docker compose up -d)..."
docker compose up -d 2>/dev/null
sleep 90

RESTORE_COUNT=$(docker exec erp-postgres psql -U "${POSTGRES_USER:-erp_user}" -d "${POSTGRES_DB:-erp}" \
  -t -c "SELECT COUNT(*) FROM _verify_test;" 2>/dev/null | tr -d ' ')

if [ "${INSERT_COUNT:-0}" = "${RESTORE_COUNT:-0}" ]; then
  log_pass "数据卷持久化 (down前${INSERT_COUNT}行 → up后${RESTORE_COUNT}行)"
else
  log_fail "数据卷持久化" "数据不一致: 前${INSERT_COUNT} 后${RESTORE_COUNT}"
fi

# 清理测试表
docker exec erp-postgres psql -U "${POSTGRES_USER:-erp_user}" -d "${POSTGRES_DB:-erp}" \
  -c "DROP TABLE IF EXISTS _verify_test;" 2>/dev/null
echo ""

# ---- 7. 端到端访问验证 ----
echo "--- 7. 端到端访问验证 ---"
E2E_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:80/ 2>/dev/null || echo "000")
if [ "$E2E_STATUS" = "200" ]; then
  log_pass "端到端访问 - 前端页面 (HTTP ${E2E_STATUS})"
else
  log_fail "端到端访问 - 前端页面" "HTTP ${E2E_STATUS}，预期 200"
fi

API_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:80/api/ 2>/dev/null || echo "000")
if [ "$API_STATUS" != "000" ] && [ "$API_STATUS" != "502" ] && [ "$API_STATUS" != "503" ]; then
  log_pass "端到端访问 - Nginx→Backend API代理 (HTTP ${API_STATUS})"
else
  log_fail "端到端访问 - API代理" "HTTP ${API_STATUS}，预期非5xx"
fi
echo ""

# ---- 8. 故障恢复验证 ----
echo "--- 8. 故障恢复验证 ---"
log_info "停止 backend 服务..."
docker compose stop backend 2>/dev/null
sleep 5

BACKEND_STOPPED=$(docker compose ps --format json 2>/dev/null | grep "erp-backend" | grep -o '"State":"[^"]*"' | cut -d'"' -f4 || echo "unknown")
log_info "backend 状态: ${BACKEND_STOPPED}"

log_info "重新启动 backend..."
docker compose start backend 2>/dev/null
sleep 70

BACKEND_RESTART=$(docker compose ps --format json 2>/dev/null | grep "erp-backend" | grep -o '"Health":"[^"]*"' | cut -d'"' -f4 || echo "unknown")
if [ "$BACKEND_RESTART" = "healthy" ]; then
  log_pass "故障恢复验证 - backend 重新变为 healthy"

  NGINX_AFTER=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:80/health 2>/dev/null || echo "000")
  if [ "$NGINX_AFTER" = "200" ]; then
    log_pass "故障恢复验证 - nginx 代理已恢复"
  else
    log_fail "故障恢复验证 - nginx 代理" "HTTP ${NGINX_AFTER}，预期 200"
  fi
else
  log_fail "故障恢复验证" "backend 重启后状态: ${BACKEND_RESTART}"
fi
echo ""

# ---- 9. 日志错误检查 ----
echo "--- 9. 日志错误检查 ---"
for svc in "${EXPECTED_SERVICES[@]}"; do
  ERROR_COUNT=$(docker compose logs --tail=50 "erp-${svc}" 2>/dev/null | grep -ci "error\|ERROR\|fatal\|FATAL" || echo "0")
  if [ "${ERROR_COUNT:-0}" -eq 0 ]; then
    log_pass "${svc} 日志无错误"
  else
    log_fail "${svc} 日志检查" "最近50行中发现 ${ERROR_COUNT} 条 ERROR/FATAL"
  fi
done
echo ""

# ---- 汇总报告 ----
echo "============================================"
echo " 验证结果汇总"
echo "============================================"
echo -e "  ${GREEN}PASS: ${PASS}${NC}"
echo -e "  ${RED}FAIL: ${FAIL}${NC}"
echo "  完成时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================"

# 输出 Markdown 表格供报告引用
echo ""
echo "<!-- VERIFY_TABLE_START -->"
echo "| 验证项 | 结果 | 备注 |"
echo "|--------|------|------|"
for row in "${RESULTS[@]}"; do
  echo "$row"
done
echo "<!-- VERIFY_TABLE_END -->"

if [ "$FAIL" -gt 0 ]; then
  exit 1
fi
exit 0
