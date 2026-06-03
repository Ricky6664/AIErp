package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysRole;
import com.erp.system.mapper.SysRoleMapper;
import com.erp.system.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色管理 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImplX<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public boolean isRoleCodeUnique(String roleCode, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        return count(wrapper) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long roleId, Boolean enabled) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色不存在: id=" + roleId);
        }

        role.setIsEnabled(enabled);
        updateById(role);

        log.info("角色状态更新: roleId={}, enabled={}", roleId, enabled);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleWithCleanup(Long roleId) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色不存在: id=" + roleId);
        }

        baseMapper.deleteUserRoleAssociations(roleId);
        baseMapper.deleteRoleMenuAssociations(roleId);
        baseMapper.deleteRoleDataAssociations(roleId);
        baseMapper.deleteRoleFieldAssociations(roleId);

        removeById(roleId);

        log.info("角色已删除(含关联清理): roleId={}", roleId);
    }
}
