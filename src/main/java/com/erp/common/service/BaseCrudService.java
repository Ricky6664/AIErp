package com.erp.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.mapper.BaseMapperX;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用CRUD Service抽象基类.
 *
 * <p>封装标准CRUD操作：新增、修改(按ID)、删除(按ID)、按ID查询、分页列表查询.
 * 子类通过实现转换方法对接MapStruct，通过覆写校验方法注入业务规则.</p>
 *
 * @param <E> 实体类型
 * @param <C> 创建DTO类型
 * @param <U> 更新DTO类型
 * @param <V> VO类型
 * @author AI
 * @since 2026-05-29
 */
public abstract class BaseCrudService<E, C, U, V> extends ServiceImplX<BaseMapperX<E>, E> {

    // ========== 模板方法（子类覆写） ==========

    /**
     * 创建前校验（模板方法，默认空实现，子类按需覆写）.
     *
     * @param dto 创建DTO
     */
    protected void validateCreate(C dto) {
    }

    /**
     * 更新前校验（模板方法，默认空实现，子类按需覆写）.
     *
     * @param dto 更新DTO
     */
    protected void validateUpdate(U dto) {
    }

    // ========== 抽象转换方法（子类实现，通常委托MapStruct Converter） ==========

    /**
     * 创建DTO → Entity.
     *
     * @param dto 创建DTO
     * @return 实体对象
     */
    protected abstract E toEntity(C dto);

    /**
     * 更新DTO合并到已有Entity（MapStruct @MappingTarget 对应）.
     *
     * @param dto    更新DTO
     * @param entity 已有实体（从数据库查询得到）
     */
    protected abstract void updateEntity(U dto, E entity);

    /**
     * Entity → VO.
     *
     * @param entity 实体对象
     * @return VO对象
     */
    protected abstract V toVO(E entity);

    // ========== 通用CRUD方法 ==========

    /**
     * 新增.
     *
     * @param dto 创建DTO
     * @return VO（含生成的主键ID）
     */
    @Transactional(rollbackFor = Exception.class)
    public V create(C dto) {
        validateCreate(dto);
        E entity = toEntity(dto);
        save(entity);
        return toVO(entity);
    }

    /**
     * 按ID修改.
     *
     * @param id  主键ID
     * @param dto 更新DTO
     * @return VO
     * @throws BusinessException 数据不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public V update(Long id, U dto) {
        validateUpdate(dto);
        E entity = getBaseMapper().selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        updateEntity(dto, entity);
        updateById(entity);
        return toVO(entity);
    }

    /**
     * 按ID删除（软删除，MyBatis-Plus @TableLogic 自动拦截）.
     *
     * @param id 主键ID
     * @return true-删除成功 / false-记录不存在
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return removeById(id);
    }

    /**
     * 按ID查询.
     *
     * @param id 主键ID
     * @return VO，不存在返回null（软删除记录自动过滤）
     */
    public V getById(Long id) {
        E entity = getBaseMapper().selectById(id);
        return entity != null ? toVO(entity) : null;
    }

    /**
     * 分页列表查询（无条件，全表分页）.
     *
     * @param query 分页参数
     * @return 分页结果（VO列表）
     */
    public PageResult<V> pageList(PageQuery query) {
        IPage<E> page = page(query.toPage());
        List<V> voList = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }
}
