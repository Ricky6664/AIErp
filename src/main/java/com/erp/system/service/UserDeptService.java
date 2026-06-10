package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysUser;

import java.util.List;

/**
 * 用户部门关联 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface UserDeptService extends IServiceX<SysUser> {

    void assignDepts(Long userId, List<Long> deptIds);

    void removeUserDept(Long userId, Long deptId);

    List<Long> getUserDeptIds(Long userId);

    List<Long> getUserIdsByDeptId(Long deptId);

    boolean hasDept(Long userId, Long deptId);

    void setPrimaryDept(Long userId, Long deptId);
}
