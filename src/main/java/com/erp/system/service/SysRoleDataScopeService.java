package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysRoleDataScope;

import java.util.List;

/**
 * 角色数据权限配置 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface SysRoleDataScopeService extends IServiceX<SysRoleDataScope> {

    List<SysRoleDataScope> getByRoleId(Long roleId);

    void saveRoleDataScopes(Long roleId, List<SysRoleDataScope> scopes);

    void deleteByRoleId(Long roleId);

    String getScopeType(Long roleId);
}
