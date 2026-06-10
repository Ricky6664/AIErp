package com.erp.auth.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.SysUser;
import com.erp.common.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表Mapper.
 *
 * @author AI
 * @since 2026-06-03
 */
@Mapper
public interface SysUserMapper extends BaseMapperX<SysUser> {

    /**
     * 根据用户名查询用户.
     *
     * @param username 用户名
     * @return 用户实体, 不存在返回null
     */
    default SysUser selectByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return selectOne(wrapper);
    }
}
