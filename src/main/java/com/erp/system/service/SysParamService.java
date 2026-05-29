package com.erp.system.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 系统参数 Service.
 *
 * <p>提供系统参数的读取、写入、删除与分类查询功能.
 * 读取方法使用 Redis 缓存（永不过期），写入与删除操作主动清除缓存.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysParamService {

    private final JdbcTemplate jdbcTemplate;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // ==================== 读取方法 ====================

    /**
     * 根据分类与键获取参数值，自动按类型转换.
     *
     * @param category 参数分类
     * @param key      参数键
     * @param type     目标类型
     * @param <T>      泛型类型
     * @return 转换后的参数值，不存在时返回 null
     */
    @Cacheable(value = "sys:param", key = "#category + ':' + #key", unless = "#result == null")
    @SuppressWarnings("unchecked")
    public <T> T getValue(String category, String key, Class<T> type) {
        Long tenantId = getCurrentTenantId();
        String sql = "SELECT param_value, value_type FROM sys_param"
                + " WHERE param_category = ? AND param_key = ? AND tenant_id = ? AND is_deleted = FALSE";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, category, key, tenantId);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.get(0);
        String value = (String) row.get("param_value");
        Integer valueType = (Integer) row.get("value_type");
        return convertValue(value, valueType, type);
    }

    /**
     * 获取字符串类型的参数值.
     *
     * @param category 参数分类
     * @param key      参数键
     * @return 参数值，不存在时返回 null
     */
    @Cacheable(value = "sys:param", key = "#category + ':' + #key", unless = "#result == null")
    public String getStr(String category, String key) {
        Long tenantId = getCurrentTenantId();
        String sql = "SELECT param_value FROM sys_param"
                + " WHERE param_category = ? AND param_key = ? AND tenant_id = ? AND is_deleted = FALSE";
        List<String> rows = jdbcTemplate.queryForList(sql, String.class, category, key, tenantId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * 按分类查询参数列表.
     *
     * @param category 参数分类
     * @return 参数列表（Map 形式），按 sort_order 排序
     */
    @Cacheable(value = "sys:param", key = "'list:' + #category", unless = "#result.isEmpty()")
    public List<Map<String, Object>> listByCategory(String category) {
        Long tenantId = getCurrentTenantId();
        String sql = "SELECT param_category, param_key, param_value, value_type, description, sort_order"
                + " FROM sys_param"
                + " WHERE param_category = ? AND tenant_id = ? AND is_deleted = FALSE"
                + " ORDER BY sort_order";
        return jdbcTemplate.queryForList(sql, category, tenantId);
    }

    // ==================== 写入方法 ====================

    /**
     * 设置参数（新增或更新），清除对应缓存.
     *
     * @param category 参数分类
     * @param key      参数键
     * @param value    参数值
     */
    @Caching(evict = {
            @CacheEvict(value = "sys:param", key = "#category + ':' + #key"),
            @CacheEvict(value = "sys:param", key = "'list:' + #category")
    })
    @Transactional(rollbackFor = Exception.class)
    public void setParam(String category, String key, String value) {
        Long tenantId = getCurrentTenantId();
        String existsSql = "SELECT COUNT(*) FROM sys_param"
                + " WHERE param_category = ? AND param_key = ? AND tenant_id = ? AND is_deleted = FALSE";
        Long count = jdbcTemplate.queryForObject(existsSql, Long.class, category, key, tenantId);
        if (count != null && count > 0) {
            String updateSql = "UPDATE sys_param SET param_value = ?, updated_at = NOW()"
                    + " WHERE param_category = ? AND param_key = ? AND tenant_id = ? AND is_deleted = FALSE";
            jdbcTemplate.update(updateSql, value, category, key, tenantId);
        } else {
            Long id = generateId();
            String insertSql = "INSERT INTO sys_param"
                    + " (id, param_category, param_key, param_value, value_type, tenant_id,"
                    + " created_at, updated_at, is_deleted, version)"
                    + " VALUES (?, ?, ?, ?, 1, ?, NOW(), NOW(), FALSE, 0)";
            jdbcTemplate.update(insertSql, id, category, key, value, tenantId);
        }
    }

    /**
     * 删除参数（软删除），系统参数不可删除.
     *
     * @param category 参数分类
     * @param key      参数键
     */
    @Caching(evict = {
            @CacheEvict(value = "sys:param", key = "#category + ':' + #key"),
            @CacheEvict(value = "sys:param", key = "'list:' + #category")
    })
    @Transactional(rollbackFor = Exception.class)
    public void deleteParam(String category, String key) {
        Long tenantId = getCurrentTenantId();
        String checkSql = "SELECT is_system FROM sys_param"
                + " WHERE param_category = ? AND param_key = ? AND tenant_id = ? AND is_deleted = FALSE";
        List<Integer> results = jdbcTemplate.queryForList(checkSql, Integer.class, category, key, tenantId);
        if (!results.isEmpty() && results.get(0) != null && results.get(0) == 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "系统参数不可删除: " + category + "." + key);
        }
        String sql = "UPDATE sys_param SET is_deleted = TRUE, updated_at = NOW()"
                + " WHERE param_category = ? AND param_key = ? AND tenant_id = ? AND is_deleted = FALSE";
        jdbcTemplate.update(sql, category, key, tenantId);
    }

    // ==================== 辅助方法 ====================

    private Long getCurrentTenantId() {
        try {
            Object tenantId = StpUtil.getSession().get("tenantId");
            if (tenantId != null) {
                return Long.parseLong(tenantId.toString());
            }
        } catch (Exception e) {
            log.debug("获取租户ID失败: {}", e.getMessage());
        }
        return 0L;
    }

    private Long generateId() {
        long timestamp = System.currentTimeMillis();
        long sequence = (long) (Math.random() * 4096);
        return (timestamp << 22) | (1L << 17) | (1L << 12) | sequence;
    }

    @SuppressWarnings("unchecked")
    private <T> T convertValue(String value, Integer valueType, Class<T> type) {
        if (value == null) {
            return null;
        }
        if (type == String.class) {
            return (T) value;
        }
        try {
            int vt = valueType != null ? valueType : 1;
            switch (vt) {
                case 2: // NUMBER
                    if (type == Integer.class || type == int.class) {
                        return (T) Integer.valueOf(value);
                    }
                    if (type == Long.class || type == long.class) {
                        return (T) Long.valueOf(value);
                    }
                    if (type == Double.class || type == double.class) {
                        return (T) Double.valueOf(value);
                    }
                    return (T) value;
                case 3: // BOOLEAN
                    boolean b = "true".equalsIgnoreCase(value) || "1".equals(value);
                    if (type == Boolean.class || type == boolean.class) {
                        return (T) Boolean.valueOf(b);
                    }
                    return (T) value;
                case 4: // JSON
                    return OBJECT_MAPPER.readValue(value, type);
                case 5: // DATE
                    if (type == LocalDate.class) {
                        return (T) LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    if (type == LocalDateTime.class) {
                        return (T) LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    return (T) value;
                default: // STRING
                    return (T) value;
            }
        } catch (Exception e) {
            log.warn("类型转换失败: value={}, valueType={}, targetType={}", value, valueType, type, e);
            return null;
        }
    }
}
