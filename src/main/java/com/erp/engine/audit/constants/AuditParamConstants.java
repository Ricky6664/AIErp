package com.erp.engine.audit.constants;

/**
 * 审核系统参数常量.
 *
 * @author AI
 */
public final class AuditParamConstants {

    private AuditParamConstants() {
    }

    /** 审核配置Redis缓存Key前缀 */
    public static final String CONFIG_CACHE_PREFIX = "audit:config:";

    /** 审核配置Redis缓存TTL(小时) */
    public static final long CONFIG_CACHE_TTL_HOURS = 2;

    /** 系统参数: 默认自动确认开关 */
    public static final String PARAM_DEFAULT_AUTO_CONFIRM = "audit.default.auto_confirm";

    /** 系统参数: 默认审批开关 */
    public static final String PARAM_DEFAULT_APPROVAL_ENABLED = "audit.default.approval_enabled";

    /** 系统参数: 审批超时时间(小时) */
    public static final String PARAM_APPROVAL_TIMEOUT_HOURS = "audit.approval.timeout_hours";

    /** 系统参数: 最大审核层级 */
    public static final String PARAM_MAX_AUDIT_LEVEL = "audit.max.audit_level";
}
