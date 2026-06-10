package com.erp.module.srm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.srm.dto.SupplierDTO;
import com.erp.module.srm.dto.SupplierQueryDTO;
import com.erp.module.srm.entity.Supplier;
import com.erp.module.srm.mapper.SupplierMapper;
import com.erp.module.srm.service.SupplierService;
import com.erp.module.srm.vo.SupplierVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier>
        implements SupplierService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    @Override
    @Transactional(readOnly = true)
    public IPage<SupplierVO> list(SupplierQueryDTO query) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getSupplierCode()),
                Supplier::getSupplierCode, query.getSupplierCode());
        wrapper.like(StringUtils.hasText(query.getSupplierName()),
                Supplier::getSupplierName, query.getSupplierName());
        wrapper.like(StringUtils.hasText(query.getShortName()),
                Supplier::getShortName, query.getShortName());
        wrapper.eq(query.getClassId() != null,
                Supplier::getClassId, query.getClassId());
        wrapper.eq(StringUtils.hasText(query.getAuditStatus()),
                Supplier::getAuditStatus, query.getAuditStatus());
        wrapper.eq(query.getIsActive() != null,
                Supplier::getIsActive, query.getIsActive());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "supplierCode" -> Supplier::getSupplierCode;
                case "supplierName" -> Supplier::getSupplierName;
                case "createTime" -> Supplier::getCreateTime;
                case "updateTime" -> Supplier::getUpdateTime;
                default -> Supplier::getCreateTime;
            });
        } else {
            wrapper.orderByDesc(Supplier::getCreateTime);
        }

        IPage<Supplier> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierVO getById(Long id) {
        Supplier entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SupplierDTO dto) {
        validateSupplierNameUniqueness(dto.getSupplierName(), dto.getCompanyId(), null);
        validateEmail(dto.getEmail());

        Supplier entity = new Supplier();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SupplierDTO dto) {
        Supplier existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        validateSupplierNameUniqueness(dto.getSupplierName(), dto.getCompanyId(), id);
        validateEmail(dto.getEmail());
        validateAuditStatusTransition(existing.getAuditStatus(), dto.getAuditStatus());

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Supplier existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        removeById(id);
    }

    private void validateSupplierNameUniqueness(String supplierName, Long companyId, Long excludeId) {
        if (!StringUtils.hasText(supplierName)) {
            return;
        }
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getSupplierName, supplierName);
        if (companyId != null) {
            wrapper.eq(Supplier::getCompanyId, companyId);
        }
        if (excludeId != null) {
            wrapper.ne(Supplier::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "同一公司主体下供应商名称已存在");
        }
    }

    private void validateEmail(String email) {
        if (StringUtils.hasText(email) && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR, "邮箱格式不正确");
        }
    }

    private void validateAuditStatusTransition(String oldStatus, String newStatus) {
        if (!StringUtils.hasText(newStatus) || newStatus.equals(oldStatus)) {
            return;
        }
        if ("APPROVED".equals(oldStatus) && "DRAFT".equals(newStatus)) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "已审核通过的供应商不能退回草稿状态");
        }
    }

    private SupplierVO toVO(Supplier entity) {
        SupplierVO vo = new SupplierVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
