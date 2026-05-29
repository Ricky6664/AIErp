package com.erp.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.mapper.BaseMapperX;

/**
 * 扩展ServiceImpl, 绑定BaseMapperX与IServiceX.
 *
 * <p>所有业务模块的 ServiceImpl 继承本类以获取增强能力.
 * 集成了 MyBatis-Plus {@link ServiceImpl} 的 CRUD 能力和 {@link IServiceX} 的扩展方法.</p>
 *
 * @param <M> Mapper类型(必须继承BaseMapperX)
 * @param <T> 实体类型
 * @author AI
 * @since 2026-05-29
 */
public class ServiceImplX<M extends BaseMapperX<T>, T> extends ServiceImpl<M, T> implements IServiceX<T> {
}
