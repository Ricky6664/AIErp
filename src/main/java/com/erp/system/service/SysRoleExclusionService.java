package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysRoleExclusion;

import java.util.List;

/**
 * 角色互斥关系 Service.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface SysRoleExclusionService extends IServiceX<SysRoleExclusion> {

    void addExclusion(Long roleA, Long roleB);

    void removeExclusion(Long roleA, Long roleB);

    List<Long> getExclusiveRoleIds(Long roleId);

    boolean isExclusive(Long roleA, Long roleB);
}
