package com.erp.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.product.dto.ProductDTO;
import com.erp.module.product.dto.ProductQueryDTO;
import com.erp.module.product.entity.Product;
import com.erp.module.product.entity.ProductClass;
import com.erp.module.product.mapper.ProductClassMapper;
import com.erp.module.product.mapper.ProductMapper;
import com.erp.module.product.service.ProductService;
import com.erp.module.product.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {

    private static final Set<String> VALID_AUDIT_STATUS = Set.of("草稿", "待审核", "已审核", "已驳回");

    @Autowired
    private ProductClassMapper productClassMapper;

    @Override
    @Transactional(readOnly = true)
    public IPage<ProductVO> list(ProductQueryDTO query) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getProductCode()), Product::getProductCode, query.getProductCode());
        wrapper.like(StringUtils.hasText(query.getProductName()), Product::getProductName, query.getProductName());
        wrapper.like(StringUtils.hasText(query.getBrand()), Product::getBrand, query.getBrand());
        wrapper.eq(query.getClassId() != null, Product::getClassId, query.getClassId());
        wrapper.eq(StringUtils.hasText(query.getAuditStatus()), Product::getAuditStatus, query.getAuditStatus());
        wrapper.eq(query.getIsActive() != null, Product::getIsActive, query.getIsActive());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if ("productCode".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, Product::getProductCode);
        } else if ("productName".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, Product::getProductName);
        } else {
            wrapper.orderBy(false, true, Product::getCreateTime);
        }

        IPage<Product> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVO getById(Long id) {
        Product entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ProductDTO dto) {
        validateProductCodeUniqueness(dto.getProductCode(), null);
        validateClassExists(dto.getClassId());
        if (StringUtils.hasText(dto.getAuditStatus())) {
            validateAuditStatus(dto.getAuditStatus());
        }

        Product entity = new Product();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProductDTO dto) {
        Product existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        validateProductCodeUniqueness(dto.getProductCode(), id);
        validateClassExists(dto.getClassId());
        if (StringUtils.hasText(dto.getAuditStatus())) {
            validateAuditStatusTransition(existing.getAuditStatus(), dto.getAuditStatus());
        }

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Product existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        removeById(id);
    }

    private void validateProductCodeUniqueness(String productCode, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getProductCode, productCode);
        if (excludeId != null) {
            wrapper.ne(Product::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "商品编码已存在: " + productCode);
        }
    }

    private void validateClassExists(Long classId) {
        if (classId == null) {
            return;
        }
        ProductClass productClass = productClassMapper.selectById(classId);
        if (productClass == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "商品分类不存在, classId: " + classId);
        }
    }

    private void validateAuditStatus(String auditStatus) {
        if (!VALID_AUDIT_STATUS.contains(auditStatus)) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID,
                    "审核状态无效, 允许值: 草稿/待审核/已审核/已驳回");
        }
    }

    private void validateAuditStatusTransition(String currentStatus, String newStatus) {
        validateAuditStatus(newStatus);

        if (currentStatus == null) {
            return;
        }

        if (currentStatus.equals(newStatus)) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case "草稿" -> "待审核".equals(newStatus) || "已驳回".equals(newStatus) || "草稿".equals(newStatus);
            case "待审核" -> "已审核".equals(newStatus) || "已驳回".equals(newStatus);
            case "已审核" -> "已驳回".equals(newStatus);
            case "已驳回" -> "草稿".equals(newStatus) || "待审核".equals(newStatus);
            default -> true;
        };

        if (!valid) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID,
                    String.format("审核状态不允许从[%s]变更为[%s]", currentStatus, newStatus));
        }
    }

    private ProductVO toVO(Product entity) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
