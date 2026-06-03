package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysRoleInheritance;

import java.util.List;

/**
 * 角色继承关系 Service.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface SysRoleInheritanceService extends IServiceX<SysRoleInheritance> {

    void addInheritance(Long parentRoleId, Long childRoleId);

    void removeInheritance(Long parentRoleId, Long childRoleId);

    List<Long> getParentRoleIds(Long roleId);

    List<Long> getChildRoleIds(Long roleId);
}
