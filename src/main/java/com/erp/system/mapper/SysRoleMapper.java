package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色表 Mapper 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
@Mapper
public interface SysRoleMapper extends BaseMapperX<SysRole> {

    @Delete("DELETE FROM sys_user_role WHERE role_id = #{roleId}")
    int deleteUserRoleAssociations(@Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    int deleteRoleMenuAssociations(@Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_role_data WHERE role_id = #{roleId}")
    int deleteRoleDataAssociations(@Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_role_field WHERE role_id = #{roleId}")
    int deleteRoleFieldAssociations(@Param("roleId") Long roleId);
}
