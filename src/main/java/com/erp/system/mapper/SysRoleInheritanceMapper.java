package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysRoleInheritance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色继承关系 Mapper.
 *
 * @author AI
 * @since 2026-06-03
 */
@Mapper
public interface SysRoleInheritanceMapper extends BaseMapperX<SysRoleInheritance> {

    @Select("SELECT parent_role_id FROM sys_role_inheritance WHERE child_role_id = #{roleId} AND is_deleted = false")
    List<Long> selectParentRoleIds(@Param("roleId") Long roleId);

    @Select("SELECT child_role_id FROM sys_role_inheritance WHERE parent_role_id = #{roleId} AND is_deleted = false")
    List<Long> selectChildRoleIds(@Param("roleId") Long roleId);
}
