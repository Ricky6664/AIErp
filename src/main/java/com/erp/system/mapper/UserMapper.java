package com.erp.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysUser;
import com.erp.system.vo.SysUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 Mapper 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
@Mapper
public interface UserMapper extends BaseMapperX<SysUser> {

    /**
     * 用户列表分页查询, LEFT JOIN hrm_employee 获取员工姓名.
     */
    IPage<SysUserVO.ListVO> selectUserPage(IPage<SysUserVO.ListVO> page,
                                           @Param("keyword") String keyword,
                                           @Param("status") String status);

    /**
     * 用户名唯一性校验, 排除指定ID.
     */
    int countByUsername(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 查询用户最近N条密码历史, 按时间倒序.
     */
    List<String> selectPasswordHistory(@Param("userId") Long userId, @Param("historyCount") int historyCount);

    /**
     * 按部门查询用户列表, 通过 sys_user_dept 关联表.
     */
    List<SysUserVO.ListVO> selectUsersByDeptId(@Param("deptId") Long deptId);

    /**
     * 查询用户角色名称列表, LEFT JOIN sys_user_role + sys_role.
     */
    List<String> selectRoleNamesByUserId(@Param("userId") Long userId);

    /**
     * 删除用户所有角色关联.
     */
    int deleteUserRoles(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联.
     */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 插入密码历史记录.
     */
    int insertPasswordHistory(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

    /**
     * 删除单个用户角色关联.
     */
    int deleteUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 查询用户所有角色ID列表.
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询拥有指定角色的用户ID列表.
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色.
     */
    int countUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户所有部门关联.
     */
    int deleteUserDepts(@Param("userId") Long userId);

    /**
     * 批量插入用户部门关联.
     */
    int insertUserDepts(@Param("userId") Long userId, @Param("deptIds") List<Long> deptIds);

    /**
     * 删除单个用户部门关联.
     */
    int deleteUserDept(@Param("userId") Long userId, @Param("deptId") Long deptId);

    /**
     * 查询用户所有部门ID列表.
     */
    List<Long> selectDeptIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询拥有指定部门的用户ID列表.
     */
    List<Long> selectUserIdsByDeptId(@Param("deptId") Long deptId);

    /**
     * 检查用户是否属于指定部门.
     */
    int countUserDept(@Param("userId") Long userId, @Param("deptId") Long deptId);

    /**
     * 设置用户主部门(清除其他主部门标记).
     */
    int setPrimaryDept(@Param("userId") Long userId, @Param("deptId") Long deptId);
}
