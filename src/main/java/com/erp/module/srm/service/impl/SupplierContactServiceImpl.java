package com.erp.module.srm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.srm.dto.SupplierContactDTO;
import com.erp.module.srm.dto.SupplierContactQueryDTO;
import com.erp.module.srm.entity.SupplierContact;
import com.erp.module.srm.mapper.SupplierContactMapper;
import com.erp.module.srm.service.SupplierContactService;
import com.erp.module.srm.vo.SupplierContactVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SupplierContactServiceImpl extends ServiceImpl<SupplierContactMapper, SupplierContact>
        implements SupplierContactService {

    @Override
    @Transactional(readOnly = true)
    public IPage<SupplierContactVO> list(SupplierContactQueryDTO query) {
        LambdaQueryWrapper<SupplierContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getSupplierId() != null, SupplierContact::getSupplierId, query.getSupplierId());
        wrapper.like(StringUtils.hasText(query.getContactName()), SupplierContact::getContactName, query.getContactName());
        wrapper.eq(StringUtils.hasText(query.getGender()), SupplierContact::getGender, query.getGender());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        wrapper.orderBy(true, isAsc, SupplierContact::getCreateTime);

        IPage<SupplierContact> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierContactVO getById(Long id) {
        SupplierContact entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SupplierContactDTO dto) {
        SupplierContact entity = new SupplierContact();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SupplierContactDTO dto) {
        SupplierContact existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SupplierContact existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        removeById(id);
    }

    private SupplierContactVO toVO(SupplierContact entity) {
        SupplierContactVO vo = new SupplierContactVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
