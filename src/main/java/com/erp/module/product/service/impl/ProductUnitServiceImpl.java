package com.erp.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.product.dto.ProductUnitDTO;
import com.erp.module.product.dto.ProductUnitQueryDTO;
import com.erp.module.product.entity.ProductUnit;
import com.erp.module.product.mapper.ProductUnitMapper;
import com.erp.module.product.service.ProductUnitService;
import com.erp.module.product.vo.ProductUnitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit>
        implements ProductUnitService {

    @Override
    @Transactional(readOnly = true)
    public IPage<ProductUnitVO> list(ProductUnitQueryDTO query) {
        LambdaQueryWrapper<ProductUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getProductId() != null, ProductUnit::getProductId, query.getProductId());
        wrapper.eq(query.getUnitId() != null, ProductUnit::getUnitId, query.getUnitId());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if ("id".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, ProductUnit::getId);
        } else {
            wrapper.orderBy(true, isAsc, ProductUnit::getCreateTime);
        }

        IPage<ProductUnit> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductUnitVO getById(Long id) {
        ProductUnit entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ProductUnitDTO dto) {
        validateConversionRate(dto.getConversionRate());
        validateUnitUniqueness(dto.getProductId(), dto.getUnitId(), null);
        if (Boolean.TRUE.equals(dto.getIsBaseUnit())) {
            validateBaseUnitUnique(dto.getProductId(), null);
        }

        ProductUnit entity = new ProductUnit();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProductUnitDTO dto) {
        ProductUnit existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        validateConversionRate(dto.getConversionRate());
        validateUnitUniqueness(dto.getProductId(), dto.getUnitId(), id);
        if (Boolean.TRUE.equals(dto.getIsBaseUnit())) {
            validateBaseUnitUnique(dto.getProductId(), id);
        }

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProductUnit existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        removeById(id);
    }

    private void validateConversionRate(BigDecimal conversionRate) {
        if (conversionRate != null && conversionRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_RANGE_ERROR, "转换比例必须大于0");
        }
    }

    private void validateUnitUniqueness(Long productId, Long unitId, Long excludeId) {
        LambdaQueryWrapper<ProductUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductUnit::getProductId, productId);
        wrapper.eq(ProductUnit::getUnitId, unitId);
        if (excludeId != null) {
            wrapper.ne(ProductUnit::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "同一商品下单位不可重复");
        }
    }

    private void validateBaseUnitUnique(Long productId, Long excludeId) {
        LambdaQueryWrapper<ProductUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductUnit::getProductId, productId);
        wrapper.eq(ProductUnit::getIsBaseUnit, true);
        if (excludeId != null) {
            wrapper.ne(ProductUnit::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该商品已存在基础单位，基础单位有且仅有一个");
        }
    }

    private ProductUnitVO toVO(ProductUnit entity) {
        ProductUnitVO vo = new ProductUnitVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setCreateBy(entity.getCreatorId());
        vo.setUpdateBy(entity.getUpdaterId());
        return vo;
    }
}
