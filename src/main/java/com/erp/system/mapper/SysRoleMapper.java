package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    @Insert("INSERT INTO sys_role_menu (role_id, menu_id) VALUES (#{roleId}, #{menuId})")
    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    @Insert("INSERT INTO sys_role_menu (role_id, menu_id, permission_type) VALUES (#{roleId}, #{menuId}, #{permissionType})")
    int insertRoleMenuWithPermission(@Param("roleId") Long roleId, @Param("menuId") Long menuId, @Param("permissionType") String permissionType);

    @Delete("<script>DELETE FROM sys_role_menu WHERE role_id = #{roleId} AND menu_id IN <foreach collection='menuIds' item='menuId' open='(' separator=',' close=')'>#{menuId}</foreach></script>")
    int deleteRoleMenuByMenuIds(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    @Select("SELECT COUNT(*) FROM sys_role_menu WHERE role_id = #{roleId} AND menu_id = #{menuId} AND permission_type = #{permissionType}")
    int countRoleMenuPermission(@Param("roleId") Long roleId, @Param("menuId") Long menuId, @Param("permissionType") String permissionType);
}
