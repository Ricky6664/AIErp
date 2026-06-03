package com.erp.system.service.impl;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysRoleInheritance;
import com.erp.system.mapper.SysRoleInheritanceMapper;
import com.erp.system.service.SysRoleInheritanceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色继承关系 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class SysRoleInheritanceServiceImpl extends ServiceImplX<SysRoleInheritanceMapper, SysRoleInheritance>
        implements SysRoleInheritanceService {

    @Resource
    private SysRoleInheritanceMapper inheritanceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInheritance(Long parentRoleId, Long childRoleId) {
        if (parentRoleId == null || childRoleId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "父角色ID和子角色ID不能为空");
        }
        if (parentRoleId.equals(childRoleId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "父角色和子角色不能相同");
        }
        SysRoleInheritance entity = new SysRoleInheritance();
        entity.setParentRoleId(parentRoleId);
        entity.setChildRoleId(childRoleId);
        save(entity);
        log.info("角色继承关系已创建: parentRoleId={}, childRoleId={}", parentRoleId, childRoleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeInheritance(Long parentRoleId, Long childRoleId) {
        if (parentRoleId == null || childRoleId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "父角色ID和子角色ID不能为空");
        }
        inheritanceMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRoleInheritance>()
                .eq(SysRoleInheritance::getParentRoleId, parentRoleId)
                .eq(SysRoleInheritance::getChildRoleId, childRoleId));
        log.info("角色继承关系已删除: parentRoleId={}, childRoleId={}", parentRoleId, childRoleId);
    }

    @Override
    public List<Long> getParentRoleIds(Long roleId) {
        return inheritanceMapper.selectParentRoleIds(roleId);
    }

    @Override
    public List<Long> getChildRoleIds(Long roleId) {
        return inheritanceMapper.selectChildRoleIds(roleId);
    }
}
