package com.erp.module.srm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.srm.dto.SupplierClassDTO;
import com.erp.module.srm.dto.SupplierClassQueryDTO;
import com.erp.module.srm.entity.SupplierClass;
import com.erp.module.srm.mapper.SupplierClassMapper;
import com.erp.module.srm.service.SupplierClassService;
import com.erp.module.srm.vo.SupplierClassVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SupplierClassServiceImpl extends ServiceImpl<SupplierClassMapper, SupplierClass>
        implements SupplierClassService {

    @Override
    @Transactional(readOnly = true)
    public IPage<SupplierClassVO> list(SupplierClassQueryDTO query) {
        LambdaQueryWrapper<SupplierClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getClassName()), SupplierClass::getClassName, query.getClassName());
        wrapper.eq(query.getParentId() != null, SupplierClass::getParentId, query.getParentId());
        wrapper.eq(query.getIsActive() != null, SupplierClass::getIsActive, query.getIsActive());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if ("createTime".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, SupplierClass::getCreateTime);
        } else {
            wrapper.orderBy(true, isAsc, SupplierClass::getSortOrder);
        }

        IPage<SupplierClass> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierClassVO getById(Long id) {
        SupplierClass entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SupplierClassDTO dto) {
        validateClassNameUniqueness(dto.getClassName(), dto.getParentId(), null);
        validateParentExists(dto.getParentId());

        SupplierClass entity = new SupplierClass();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SupplierClassDTO dto) {
        SupplierClass existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        validateClassNameUniqueness(dto.getClassName(), dto.getParentId(), id);
        validateParentExists(dto.getParentId());

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SupplierClass existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        LambdaQueryWrapper<SupplierClass> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SupplierClass::getParentId, id);
        if (count(childWrapper) > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "存在子分类，无法删除");
        }

        removeById(id);
    }

    private void validateClassNameUniqueness(String className, Long parentId, Long excludeId) {
        LambdaQueryWrapper<SupplierClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierClass::getClassName, className);
        if (parentId != null) {
            wrapper.eq(SupplierClass::getParentId, parentId);
        } else {
            wrapper.isNull(SupplierClass::getParentId);
        }
        if (excludeId != null) {
            wrapper.ne(SupplierClass::getId, excludeId);
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

    private SupplierClassVO toVO(SupplierClass entity) {
        SupplierClassVO vo = new SupplierClassVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
