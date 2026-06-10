package com.erp.common.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;

import java.io.Serializable;
import java.util.List;

/**
 * 扩展Service接口, 提供通用批量操作和分页查询方法.
 *
 * <p>所有业务模块的 Service 接口继承本类以获取增强能力.
 * 基于 MyBatis-Plus {@link IService} 扩展, 方法均有默认实现.</p>
 *
 * @param <T> 实体类型
 * @author AI
 * @since 2026-05-29
 */
public interface IServiceX<T> extends IService<T> {

    /**
     * 批量插入.
     *
     * @param list 数据列表
     * @return true 全部插入成功
     */
    default boolean createBatch(List<T> list) {
        return saveBatch(list);
    }

    /**
     * 批量根据ID更新.
     *
     * @param list 数据列表(实体必须包含主键值)
     * @return true 全部更新成功
     */
    default boolean updateBatch(List<T> list) {
        return updateBatchById(list);
    }

    /**
     * 条件分页查询.
     *
     * @param query   分页参数
     * @param wrapper 查询条件包装器
     * @return 分页结果
     */
    default PageResult<T> pageList(PageQuery query, LambdaQueryWrapper<T> wrapper) {
        return PageResult.of(page(query.toPage(), wrapper));
    }

    /**
     * 根据条件查询单条记录, 不存在时抛异常.
     *
     * @param wrapper 查询条件包装器
     * @return 实体对象(保证非null)
     * @throws BusinessException 记录不存在时抛出 DATA_NOT_FOUND
     */
    default T getOneOrThrow(LambdaQueryWrapper<T> wrapper) {
        T entity = getOne(wrapper);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 根据主键ID判断记录是否存在.
     *
     * @param id 主键ID
     * @return true 存在
     */
    default boolean existsById(Serializable id) {
        return getById(id) != null;
    }

    /**
     * 根据主键ID检查记录是否存在, 不存在时抛异常.
     *
     * @param id        主键ID
     * @param errorCode 不存在时使用的错误码
     * @throws BusinessException 记录不存在时抛出
     */
    default void checkExists(Serializable id, ErrorCode errorCode) {
        if (!existsById(id)) {
            throw new BusinessException(errorCode);
        }
    }
}
