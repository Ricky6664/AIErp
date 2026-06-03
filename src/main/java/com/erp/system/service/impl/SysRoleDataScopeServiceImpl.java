package com.erp.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysRoleDataScope;
import com.erp.system.mapper.SysRoleDataScopeMapper;
import com.erp.system.mapper.UserMapper;
import com.erp.system.service.SysRoleDataScopeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 角色数据权限配置 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class SysRoleDataScopeServiceImpl extends ServiceImplX<SysRoleDataScopeMapper, SysRoleDataScope>
        implements SysRoleDataScopeService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<SysRoleDataScope> getByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysRoleDataScope> wrapper = new LambdaQueryWrapper<SysRoleDataScope>()
                .eq(SysRoleDataScope::getRoleId, roleId);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleDataScopes(Long roleId, List<SysRoleDataScope> scopes) {
        if (roleId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "角色ID不能为空");
        }

        LambdaQueryWrapper<SysRoleDataScope> wrapper = new LambdaQueryWrapper<SysRoleDataScope>()
                .eq(SysRoleDataScope::getRoleId, roleId);
        remove(wrapper);

        if (!CollectionUtils.isEmpty(scopes)) {
            for (SysRoleDataScope scope : scopes) {
                scope.setRoleId(roleId);
            }
            saveBatch(scopes);
        }

        kickoutUsersByRoleId(roleId);

        log.info("角色数据权限保存完成: roleId={}, scopeCount={}",
                roleId, scopes != null ? scopes.size() : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        LambdaQueryWrapper<SysRoleDataScope> wrapper = new LambdaQueryWrapper<SysRoleDataScope>()
                .eq(SysRoleDataScope::getRoleId, roleId);
        remove(wrapper);

        log.info("角色数据权限已删除: roleId={}", roleId);
    }

    @Override
    public String getScopeType(Long roleId) {
        if (roleId == null) {
            return null;
        }
        List<SysRoleDataScope> scopes = getByRoleId(roleId);
        if (CollectionUtils.isEmpty(scopes)) {
            return null;
        }
        return scopes.get(0).getScopeType();
    }

    private void kickoutUsersByRoleId(Long roleId) {
        List<Long> userIds = userMapper.selectUserIdsByRoleId(roleId);
        if (userIds != null) {
            for (Long userId : userIds) {
                try {
                    StpUtil.kickout(userId);
                } catch (Exception e) {
                    log.debug("角色数据权限变更踢出用户失败(用户可能未在线): userId={}, roleId={}", userId, roleId);
                }
            }
        }
    }
}
