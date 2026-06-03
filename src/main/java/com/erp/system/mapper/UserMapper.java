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
}
