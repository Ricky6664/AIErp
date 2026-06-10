package com.erp.config;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Sa-Token StpInterface 实现.
 *
 * <p>为 Sa-Token 提供权限数据和角色数据的加载逻辑.
 * 采用 Redis 缓存优先策略, 缓存未命中时从数据库查询并回写缓存.</p>
 *
 * <p>缓存策略:
 * <ul>
 *   <li>权限缓存 Key: satoken:permission:{loginId}</li>
 *   <li>角色缓存 Key: satoken:role:{loginId}</li>
 *   <li>缓存过期时间: 5 分钟</li>
 *   <li>登录/角色变更时调用 {@link #clearCache(Object)} 主动清除</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String PERMISSION_CACHE_KEY = "satoken:permission:";
    private static final String ROLE_CACHE_KEY = "satoken:role:";
    private static final long CACHE_TTL_MINUTES = 5;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String key = PERMISSION_CACHE_KEY + loginId;
        List<String> cached = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (cached != null && !cached.isEmpty()) {
            log.debug("命中权限缓存: loginId={}", loginId);
            return cached;
        }
        List<String> permissions = loadPermissionsFromDb(loginId, loginType);
        if (!permissions.isEmpty()) {
            stringRedisTemplate.opsForList().rightPushAll(key, permissions);
            stringRedisTemplate.expire(key, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        log.debug("权限数据已加载: loginId={}, count={}", loginId, permissions.size());
        return permissions;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String key = ROLE_CACHE_KEY + loginId;
        List<String> cached = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (cached != null && !cached.isEmpty()) {
            log.debug("命中角色缓存: loginId={}", loginId);
            return cached;
        }
        List<String> roles = loadRolesFromDb(loginId, loginType);
        if (!roles.isEmpty()) {
            stringRedisTemplate.opsForList().rightPushAll(key, roles);
            stringRedisTemplate.expire(key, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        log.debug("角色数据已加载: loginId={}, count={}", loginId, roles.size());
        return roles;
    }

    /**
     * 清除指定用户的权限和角色缓存.
     *
     * <p>在用户登录或角色变更时调用, 确保下次权限校验时加载最新数据.</p>
     *
     * @param loginId 用户登录标识
     */
    public void clearCache(Object loginId) {
        stringRedisTemplate.delete(PERMISSION_CACHE_KEY + loginId);
        stringRedisTemplate.delete(ROLE_CACHE_KEY + loginId);
        log.debug("已清除用户缓存: loginId={}", loginId);
    }

    /**
     * 从数据库加载用户权限标识列表.
     *
     * <p>待 P0-004 (认证与权限基础开发) 创建 sys_permission / sys_role_permission / sys_user_role 表后接入.</p>
     */
    private List<String> loadPermissionsFromDb(Object loginId, String loginType) {
        // TODO P0-004: 接入权限表查询
        // SELECT DISTINCT p.permission_code
        //   FROM sys_permission p
        //   JOIN sys_role_permission rp ON p.id = rp.permission_id
        //   JOIN sys_user_role ur ON rp.role_id = ur.role_id
        //  WHERE ur.user_id = ? AND p.is_deleted = 0
        return new ArrayList<>();
    }

    /**
     * 从数据库加载用户角色标识列表.
     *
     * <p>待 P0-004 (认证与权限基础开发) 创建 sys_role / sys_user_role 表后接入.</p>
     */
    private List<String> loadRolesFromDb(Object loginId, String loginType) {
        // TODO P0-004: 接入角色表查询
        // SELECT DISTINCT r.role_code
        //   FROM sys_role r
        //   JOIN sys_user_role ur ON r.id = ur.role_id
        //  WHERE ur.user_id = ? AND r.is_deleted = 0
        return new ArrayList<>();
    }
}
