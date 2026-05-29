package com.erp.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 全局配置类.
 *
 * <p>注册 MybatisPlusInterceptor 拦截器链和 MetaObjectHandler 自动填充处理器.</p>
 *
 * <p>拦截器注册顺序(不可调整):
 * <ol>
 *   <li>{@link TenantLineInnerInterceptor} - 多租户隔离(必须第一个)</li>
 *   <li>{@link PaginationInnerInterceptor} - 分页(必须在乐观锁之前, 否则分页失效)</li>
 *   <li>{@link OptimisticLockerInnerInterceptor} - 乐观锁</li>
 * </ol>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Configuration
public class MybatisPlusConfig {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlusConfig.class);

    /**
     * 注册 MybatisPlusInterceptor 拦截器链.
     *
     * <p>按顺序添加 3 个 InnerInterceptor:
     * <ol>
     *   <li>多租户拦截器 - 自动注入 tenant_id 条件</li>
     *   <li>分页拦截器 - overflow=true, 页码溢出时返回首页</li>
     *   <li>乐观锁拦截器 - 自动递增 version 字段</li>
     * </ol>
     * </p>
     *
     * @return MybatisPlusInterceptor 拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 多租户拦截器(必须第一个注册)
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
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
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 未登录或无租户上下文时, 跳过租户条件注入
                try {
                    StpUtil.checkLogin();
                    return StpUtil.getSession().get("tenantId") == null;
                } catch (Exception e) {
                    return true;
                }
            }
        }));

        // 2. 分页拦截器(必须在乐观锁之前, overflow=true: 页码溢出时返回首页)
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        paginationInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 3. 乐观锁拦截器
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }

    /**
     * 注册 MetaObjectHandler 自动填充处理器.
     *
     * <p>新增时自动填充: createdAt, updatedAt, createdBy, updatedBy, version.
     * 更新时自动填充: updatedAt, updatedBy, version(自动递增由乐观锁插件处理).</p>
     *
     * @return MetaObjectHandler 自动填充处理器实例
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new ErpMetaObjectHandler();
    }

    /**
     * ERP 系统 MetaObjectHandler 实现.
     *
     * <p>在实体对象的新增和更新操作时, 自动填充审计字段和版本号.</p>
     *
     * @author AI
     * @since 2026-05-29
     */
    static class ErpMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            LocalDateTime now = LocalDateTime.now();
            Long userId = getCurrentUserId();

            this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
            this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
            this.strictInsertFill(metaObject, "createdBy", Long.class, userId);
            this.strictInsertFill(metaObject, "updatedBy", Long.class, userId);
            this.strictInsertFill(metaObject, "version", Integer.class, 0);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            Long userId = getCurrentUserId();

            // updatedAt 每次更新强制刷新为当前时间
            this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
            this.strictUpdateFill(metaObject, "updatedBy", Long.class, userId);
        }

        /**
         * 获取当前登录用户 ID.
         *
         * @return 用户 ID, 未登录时返回 null
         */
        private Long getCurrentUserId() {
            try {
                Object userId = StpUtil.getLoginIdAsLong();
                return userId != null ? Long.parseLong(userId.toString()) : null;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
