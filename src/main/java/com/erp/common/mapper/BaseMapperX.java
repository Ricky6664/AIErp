package com.erp.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.Db;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 扩展Mapper基类, 提供通用批量操作和分页查询方法.
 *
 * <p>所有业务模块的 Mapper 接口继承本类以获取增强能力.
 * 批量操作方法基于 MyBatis-Plus {@link Db} 工具类实现.</p>
 *
 * @param <T> 实体类型
 * @author AI
 * @since 2026-05-29
 */
public interface BaseMapperX<T> extends BaseMapper<T> {

    /**
     * 条件分页查询.
     *
     * @param page    分页对象
     * @param wrapper 查询条件包装器
     * @return 分页结果
     */
    default IPage<T> selectPageByCondition(IPage<T> page, LambdaQueryWrapper<T> wrapper) {
        return selectPage(page, wrapper);
    }

    /**
     * 根据主键ID查询单条记录.
     *
     * @param id 主键ID
     * @return 实体对象, 不存在时返回 null
     */
    default T selectOneById(Serializable id) {
        return selectById(id);
    }

    /**
     * 批量插入(分批执行).
     *
     * <p>基于 MyBatis-Plus {@link Db#saveBatch(Collection, int)} 实现,
     * 自动按 batchSize 分批提交, 减少数据库交互次数.</p>
     *
     * @param list      数据列表
     * @param batchSize 每批数量
     * @return true 全部插入成功
     */
    default boolean insertBatch(List<T> list, int batchSize) {
        return Db.saveBatch(list, batchSize);
    }

    /**
     * 批量根据ID更新(分批执行).
     *
     * <p>基于 MyBatis-Plus {@link Db#updateBatchById(Collection, int)} 实现,
     * 自动按 batchSize 分批提交.</p>
     *
     * @param list      数据列表(实体必须包含主键值)
     * @param batchSize 每批数量
     * @return true 全部更新成功
     */
    default boolean updateBatchById(List<T> list, int batchSize) {
        return Db.updateBatchById(list, batchSize);
    }

    /**
     * 根据ID集合批量删除.
     *
     * @param ids 主键ID集合
     * @return 删除的记录数
     */
    default int deleteByIds(Collection<? extends Serializable> ids) {
        return deleteBatchIds(ids);
    }
}
