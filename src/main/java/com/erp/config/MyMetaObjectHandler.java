package com.erp.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器.
 *
 * <p>在实体对象的新增和更新操作时, 自动填充审计字段、多租户ID和逻辑删除标记.</p>
 *
 * <p>新增时自动填充({@code @TableField(fill = FieldFill.INSERT)}):
 * <ul>
 *   <li>{@code createTime} - 创建时间({@link LocalDateTime#now()})</li>
 *   <li>{@code updateTime} - 更新时间({@link LocalDateTime#now()})</li>
 *   <li>{@code creatorId} - 创建人(当前登录用户ID, {@code StpUtil.getLoginIdAsLong()})</li>
 *   <li>{@code updaterId} - 更新人(当前登录用户ID)</li>
 *   <li>{@code tenantId} - 租户ID(当前会话租户)</li>
 *   <li>{@code isDeleted} - 逻辑删除标记(false=未删除)</li>
 * </ul>
 * </p>
 *
 * <p>更新时自动填充({@code @TableField(fill = FieldFill.UPDATE)}):
 * <ul>
 *   <li>{@code updateTime} - 更新时间(强制刷新为当前时间)</li>
 *   <li>{@code updaterId} - 更新人(当前登录用户ID)</li>
 * </ul>
 * </p>
 *
 * <p>使用方式: 实体字段需标注 {@code @TableField(fill = FieldFill.INSERT)}
 * 或 {@code @TableField(fill = FieldFill.INSERT_UPDATE)} 或
 * {@code @TableField(fill = FieldFill.UPDATE)} 以启用自动填充.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final Logger log = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    /** 逻辑删除 - 未删除 */
    private static final Boolean NOT_DELETED = false;

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long userId = getCurrentUserId();
        Long tenantId = getTenantId();

        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        // 创建人
        this.strictInsertFill(metaObject, "creatorId", Long.class, userId);
        // 更新人
        this.strictInsertFill(metaObject, "updaterId", Long.class, userId);
        // 租户ID
        this.strictInsertFill(metaObject, "tenantId", Long.class, tenantId);
        // 逻辑删除标记: false=未删除
        this.strictInsertFill(metaObject, "isDeleted", Boolean.class, NOT_DELETED);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 先获取用户ID (避免异常中断后续填充)
        Long userId = getCurrentUserId();
        // 更新时间: 每次更新强制刷新为当前时间
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        // 更新人: 每次更新强制刷新为当前登录用户
        this.setFieldValByName("updaterId", userId, metaObject);
    }

    /**
     * 获取当前会话的租户ID.
     *
     * @return 租户ID, 未登录或无租户上下文时返回 null
     */
    private Long getTenantId() {
        try {
            Object tenantId = StpUtil.getSession().get("tenantId");
            if (tenantId != null) {
                return Long.parseLong(tenantId.toString());
            }
        } catch (Exception e) {
            log.debug("获取租户ID失败, 跳过tenantId自动填充: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前登录用户ID.
     *
     * @return 用户ID, 未登录时返回 null
     */
    private Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            log.debug("获取用户ID失败, 跳过用户相关字段自动填充: {}", e.getMessage());
            return null;
        }
    }
}
