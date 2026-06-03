package com.erp.system.service.impl;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysRoleExclusion;
import com.erp.system.mapper.SysRoleExclusionMapper;
import com.erp.system.service.SysRoleExclusionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 角色互斥关系 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class SysRoleExclusionServiceImpl extends ServiceImplX<SysRoleExclusionMapper, SysRoleExclusion>
        implements SysRoleExclusionService {

    @Resource
    private SysRoleExclusionMapper exclusionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addExclusion(Long roleA, Long roleB) {
        if (roleA == null || roleB == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "角色A和角色B的ID不能为空");
        }
        if (roleA.equals(roleB)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "不能将同一角色设为互斥");
        }
        if (isExclusive(roleA, roleB)) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该互斥关系已存在");
        }
        SysRoleExclusion entity = new SysRoleExclusion();
        entity.setRoleA(roleA);
        entity.setRoleB(roleB);
        save(entity);
        log.info("角色互斥关系已创建: roleA={}, roleB={}", roleA, roleB);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeExclusion(Long roleA, Long roleB) {
        if (roleA == null || roleB == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "角色A和角色B的ID不能为空");
        }
        exclusionMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRoleExclusion>()
                .and(w -> w
                        .eq(SysRoleExclusion::getRoleA, roleA).eq(SysRoleExclusion::getRoleB, roleB)
                        .or()
                        .eq(SysRoleExclusion::getRoleA, roleB).eq(SysRoleExclusion::getRoleB, roleA)));
        log.info("角色互斥关系已删除: roleA={}, roleB={}", roleA, roleB);
    }

    @Override
    public List<Long> getExclusiveRoleIds(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return exclusionMapper.selectExclusiveRoleIds(roleId);
    }

    @Override
    public boolean isExclusive(Long roleA, Long roleB) {
        if (roleA == null || roleB == null) {
            return false;
        }
        return exclusionMapper.countExclusion(roleA, roleB) > 0;
    }
}
