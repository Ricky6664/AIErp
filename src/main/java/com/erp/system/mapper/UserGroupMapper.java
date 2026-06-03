package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysUserGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户组 Mapper 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
@Mapper
public interface UserGroupMapper extends BaseMapperX<SysUserGroup> {

    int deleteGroupMembers(@Param("groupId") Long groupId);

    int insertGroupMembers(@Param("groupId") Long groupId, @Param("userIds") List<Long> userIds);

    int deleteGroupMember(@Param("groupId") Long groupId, @Param("userId") Long userId);

    List<Long> selectUserIdsByGroupId(@Param("groupId") Long groupId);

    List<Long> selectGroupIdsByUserId(@Param("userId") Long userId);

    int countGroupMember(@Param("groupId") Long groupId, @Param("userId") Long userId);
}
