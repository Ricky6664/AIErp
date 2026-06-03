package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysUser;
import com.erp.system.vo.UserWorkbenchVO;

import java.util.List;

/**
 * 用户管理 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface UserService extends IServiceX<SysUser> {

    void assignRoles(Long userId, List<Long> roleIds);

    void resetPassword(Long userId, String newPassword);

    void updateStatus(Long userId, String status);

    void unlockUser(Long userId);

    List<String> getRoleNames(Long userId);

    void changePassword(Long userId, String oldPassword, String newPassword);

    boolean isUsernameUnique(String username, Long excludeId);

    String resetPasswordAndReturn(Long userId);

    void deleteUserWithCleanup(Long userId);

    UserWorkbenchVO getWorkbenchData();
}
