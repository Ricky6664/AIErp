package com.erp.framework.tenant;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 多租户数据隔离拦截器 — MyBatis-Plus {@link TenantLineHandler} 实现.
 *
 * <p>责任:
 * <ul>
 *   <li>从当前 Sa-Token 会话中获取租户ID ({@code tenantId})</li>
 *   <li>自动为所有 SQL 注入 {@code tenant_id = ?} 条件</li>
 *   <li>跳过系统元数据表和无租户上下文的场景</li>
 * </ul>
 * </p>
 *
 * <p>注入规则:
 * <ul>
 *   <li>已登录且有 tenantId → 注入 {@code tenant_id = <tenantId>}</li>
 *   <li>未登录或无 tenantId → 跳过注入 (返回 {@link NullValue})</li>
 *   <li>表名在 {@code IGNORE_TABLES} 中 → 跳过注入</li>
 * </ul>
 * </p>
 *
 * <p>使用方式: 由 {@link com.erp.config.MybatisPlusConfig} 注入到
 * {@link com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor} 中.</p>
 *
 * @author AI
 * @since 2026-06-01
 */
@Component
public class TenantInterceptor implements TenantLineHandler {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    /** 多租户列名 — 所有业务表统一使用 tenant_id */
    private static final String TENANT_ID_COLUMN = "tenant_id";

    /**
     * 跳过租户过滤的系统表.
     *
     * <p>以下类型的表不注入 tenant_id 条件:
     * <ul>
     *   <li>tenant_isolation_constraint — 租户隔离约束元数据表 (跨租户共享)</li>
     *   <li>flyway_schema_history — Flyway 迁移版本表 (框架管理)</li>
     * </ul>
     * </p>
     */
    private static final Set<String> IGNORE_TABLES = Set.of(
        "tenant_isolation_constraint",
        "flyway_schema_history"
    );

    @Override
    public Expression getTenantId() {
        try {
            Object tenantId = StpUtil.getSession().get("tenantId");
            if (tenantId != null) {
                return new LongValue(Long.parseLong(tenantId.toString()));
            }
        } catch (Exception e) {
            log.debug("获取租户ID失败, 跳过租户条件注入: {}", e.getMessage());
        }
        return new NullValue();
    }

    @Override
    public String getTenantIdColumn() {
        return TENANT_ID_COLUMN;
    }

    @Override
    public boolean ignoreTable(String tableName) {
        if (tableName == null) {
            return true;
        }
        // 系统表不注入租户条件
        if (IGNORE_TABLES.contains(tableName)) {
            return true;
        }
        // 无有效登录会话或无租户上下文时跳过
        try {
            StpUtil.checkLogin();
            return StpUtil.getSession().get("tenantId") == null;
        } catch (Exception e) {
            return true;
        }
    }
}
