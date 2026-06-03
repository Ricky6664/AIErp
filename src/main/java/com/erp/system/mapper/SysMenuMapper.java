package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单表 Mapper 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
@Mapper
public interface SysMenuMapper extends BaseMapperX<SysMenu> {

    @Select("SELECT permission_code FROM sys_menu WHERE id IN (SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}) AND permission_code IS NOT NULL AND permission_code != ''")
    List<String> selectPermissionCodesByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT DISTINCT m.permission_code FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id WHERE ur.user_id = #{userId} AND m.permission_code IS NOT NULL AND m.permission_code != ''")
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM sys_menu WHERE parent_id = #{parentId} AND menu_type = 'button' AND is_enabled = true ORDER BY sort_order")
    List<SysMenu> selectButtonsByParentId(@Param("parentId") Long parentId);

    @Select("SELECT DISTINCT m.* FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id WHERE ur.user_id = #{userId} AND m.menu_type = 'button' AND m.is_enabled = true ORDER BY m.sort_order")
    List<SysMenu> selectButtonsByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM sys_menu WHERE menu_type = 'button' AND is_enabled = true AND permission_code = #{permissionCode}")
    SysMenu selectByPermissionCode(@Param("permissionCode") String permissionCode);
}
