package com.erp.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.product.dto.ProductClassDTO;
import com.erp.module.product.dto.ProductClassQueryDTO;
import com.erp.module.product.entity.ProductClass;
import com.erp.module.product.mapper.ProductClassMapper;
import com.erp.module.product.service.ProductClassService;
import com.erp.module.product.vo.ProductClassVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProductClassServiceImpl extends ServiceImpl<ProductClassMapper, ProductClass>
        implements ProductClassService {

    private static final int SORT_ORDER_MIN = 0;
    private static final int SORT_ORDER_MAX = 9999;

    @Override
    @Transactional(readOnly = true)
    public IPage<ProductClassVO> list(ProductClassQueryDTO query) {
        LambdaQueryWrapper<ProductClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getClassName()), ProductClass::getClassName, query.getClassName());
        wrapper.eq(query.getParentId() != null, ProductClass::getParentId, query.getParentId());
        wrapper.eq(query.getIsActive() != null, ProductClass::getIsActive, query.getIsActive());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if ("createTime".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, ProductClass::getCreateTime);
        } else {
            wrapper.orderBy(true, isAsc, ProductClass::getSortOrder);
        }

        IPage<ProductClass> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductClassVO getById(Long id) {
        ProductClass entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ProductClassDTO dto) {
        validateSortOrder(dto.getSortOrder());
        validateClassNameUniqueness(dto.getClassName(), dto.getParentId(), null);
        validateParentExists(dto.getParentId());

        ProductClass entity = new ProductClass();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProductClassDTO dto) {
        ProductClass existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        validateSortOrder(dto.getSortOrder());
        validateClassNameUniqueness(dto.getClassName(), dto.getParentId(), id);
        validateParentExists(dto.getParentId());

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProductClass existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        LambdaQueryWrapper<ProductClass> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(ProductClass::getParentId, id);
        if (count(childWrapper) > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "存在子分类，无法删除");
        }

        removeById(id);
    }

    private void validateSortOrder(Integer sortOrder) {
        if (sortOrder != null && (sortOrder < SORT_ORDER_MIN || sortOrder > SORT_ORDER_MAX)) {
            throw new BusinessException(ErrorCode.PARAM_RANGE_ERROR,
                    String.format("排序号范围%d~%d", SORT_ORDER_MIN, SORT_ORDER_MAX));
        }
    }

    private void validateClassNameUniqueness(String className, Long parentId, Long excludeId) {
        LambdaQueryWrapper<ProductClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductClass::getClassName, className);
        if (parentId != null) {
            wrapper.eq(ProductClass::getParentId, parentId);
        } else {
            wrapper.isNull(ProductClass::getParentId);
        }
        if (excludeId != null) {
            wrapper.ne(ProductClass::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "同级分类名称已存在");
        }
    }

    private void validateParentExists(Long parentId) {
        if (parentId != null && parentId > 0) {
            if (super.getById(parentId) == null) {
                throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "上级分类不存在");
            }
        }
    }

    private ProductClassVO toVO(ProductClass entity) {
        ProductClassVO vo = new ProductClassVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
