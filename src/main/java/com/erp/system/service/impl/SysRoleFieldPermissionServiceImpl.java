package com.erp.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysRoleFieldPermission;
import com.erp.system.mapper.SysRoleFieldPermissionMapper;
import com.erp.system.mapper.UserMapper;
import com.erp.system.service.SysRoleFieldPermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 角色字段权限配置 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class SysRoleFieldPermissionServiceImpl
        extends ServiceImplX<SysRoleFieldPermissionMapper, SysRoleFieldPermission>
        implements SysRoleFieldPermissionService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<SysRoleFieldPermission> getByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysRoleFieldPermission> wrapper = new LambdaQueryWrapper<SysRoleFieldPermission>()
                .eq(SysRoleFieldPermission::getRoleId, roleId);
        return list(wrapper);
    }

    @Override
    public List<SysRoleFieldPermission> getByRoleIdAndTable(Long roleId, String tableName) {
        if (roleId == null || tableName == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysRoleFieldPermission> wrapper = new LambdaQueryWrapper<SysRoleFieldPermission>()
                .eq(SysRoleFieldPermission::getRoleId, roleId)
                .eq(SysRoleFieldPermission::getTableName, tableName);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleFieldPermissions(Long roleId, List<SysRoleFieldPermission> permissions) {
        if (roleId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "角色ID不能为空");
        }

        LambdaQueryWrapper<SysRoleFieldPermission> wrapper = new LambdaQueryWrapper<SysRoleFieldPermission>()
                .eq(SysRoleFieldPermission::getRoleId, roleId);
        remove(wrapper);

        if (!CollectionUtils.isEmpty(permissions)) {
            for (SysRoleFieldPermission perm : permissions) {
                perm.setRoleId(roleId);
            }
            saveBatch(permissions);
        }

        kickoutUsersByRoleId(roleId);

        log.info("角色字段权限保存完成: roleId={}, permissionCount={}",
                roleId, permissions != null ? permissions.size() : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        LambdaQueryWrapper<SysRoleFieldPermission> wrapper = new LambdaQueryWrapper<SysRoleFieldPermission>()
                .eq(SysRoleFieldPermission::getRoleId, roleId);
        remove(wrapper);

        log.info("角色字段权限已删除: roleId={}", roleId);
    }

    @Override
    public String getPermissionType(Long roleId, String tableName, String fieldName) {
        if (roleId == null || tableName == null || fieldName == null) {
            return null;
        }
        LambdaQueryWrapper<SysRoleFieldPermission> wrapper = new LambdaQueryWrapper<SysRoleFieldPermission>()
                .eq(SysRoleFieldPermission::getRoleId, roleId)
                .eq(SysRoleFieldPermission::getTableName, tableName)
                .eq(SysRoleFieldPermission::getFieldName, fieldName);
        SysRoleFieldPermission perm = getOne(wrapper);
        return perm != null ? perm.getPermissionType() : null;
    }

    private void kickoutUsersByRoleId(Long roleId) {
        List<Long> userIds = userMapper.selectUserIdsByRoleId(roleId);
        if (userIds != null) {
            for (Long userId : userIds) {
                try {
                    StpUtil.kickout(userId);
                } catch (Exception e) {
                    log.debug("角色字段权限变更踢出用户失败(用户可能未在线): userId={}, roleId={}", userId, roleId);
                }
            }
        }
    }
}
