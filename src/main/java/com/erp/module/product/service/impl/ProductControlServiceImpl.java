package com.erp.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.product.dto.ProductControlDTO;
import com.erp.module.product.dto.ProductControlQueryDTO;
import com.erp.module.product.entity.ProductControl;
import com.erp.module.product.mapper.ProductControlMapper;
import com.erp.module.product.service.ProductControlService;
import com.erp.module.product.vo.ProductControlVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductControlServiceImpl extends ServiceImpl<ProductControlMapper, ProductControl>
        implements ProductControlService {

    @Override
    @Transactional(readOnly = true)
    public IPage<ProductControlVO> list(ProductControlQueryDTO query) {
        LambdaQueryWrapper<ProductControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getProductId() != null, ProductControl::getProductId, query.getProductId());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if ("createTime".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, ProductControl::getCreateTime);
        } else {
            wrapper.orderBy(true, isAsc, ProductControl::getId);
        }

        IPage<ProductControl> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductControlVO getById(Long id) {
        ProductControl entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ProductControlDTO dto) {
        validateProductControlUniqueness(dto.getProductId(), null);
        validateMutualExclusion(dto.getBatchManageFlag(), dto.getSerialManageFlag());

        ProductControl entity = new ProductControl();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProductControlDTO dto) {
        ProductControl existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        validateProductControlUniqueness(dto.getProductId(), id);
        validateMutualExclusion(dto.getBatchManageFlag(), dto.getSerialManageFlag());

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProductControl existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        removeById(id);
    }

    private void validateProductControlUniqueness(Long productId, Long excludeId) {
        LambdaQueryWrapper<ProductControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductControl::getProductId, productId);
        if (excludeId != null) {
            wrapper.ne(ProductControl::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该商品已存在控制策略记录");
        }
    }

    private void validateMutualExclusion(Boolean batchManageFlag, Boolean serialManageFlag) {
        if (Boolean.TRUE.equals(batchManageFlag) && Boolean.TRUE.equals(serialManageFlag)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "序列号管理与批次管理不可同时开启");
        }
    }

    private ProductControlVO toVO(ProductControl entity) {
        ProductControlVO vo = new ProductControlVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setCreateBy(entity.getCreatorId());
        vo.setUpdateBy(entity.getUpdaterId());
        return vo;
    }
}
