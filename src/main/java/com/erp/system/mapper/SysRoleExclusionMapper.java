package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysRoleExclusion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色互斥关系 Mapper.
 *
 * @author AI
 * @since 2026-06-03
 */
@Mapper
public interface SysRoleExclusionMapper extends BaseMapperX<SysRoleExclusion> {

    @Select("SELECT role_b FROM sys_role_exclusion WHERE role_a = #{roleId} AND is_deleted = false "
            + "UNION ALL "
            + "SELECT role_a FROM sys_role_exclusion WHERE role_b = #{roleId} AND is_deleted = false")
    List<Long> selectExclusiveRoleIds(@Param("roleId") Long roleId);

    @Select("SELECT COUNT(*) FROM sys_role_exclusion WHERE is_deleted = false "
            + "AND ((role_a = #{roleA} AND role_b = #{roleB}) OR (role_a = #{roleB} AND role_b = #{roleA}))")
    int countExclusion(@Param("roleA") Long roleA, @Param("roleB") Long roleB);
}
