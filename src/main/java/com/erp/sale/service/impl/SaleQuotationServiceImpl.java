package com.erp.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.service.AuditEngineService;
import com.erp.sale.dto.SaleQuotationCreateDTO;
import com.erp.sale.dto.SaleQuotationQueryDTO;
import com.erp.sale.dto.SaleQuotationUpdateDTO;
import com.erp.sale.entity.SaleQuotationEntity;
import com.erp.sale.mapper.SaleQuotationMapper;
import com.erp.sale.service.ISaleQuotationService;
import com.erp.sale.vo.SaleQuotationDetailVO;
import com.erp.sale.vo.SaleQuotationListVO;
import com.erp.system.codegen.CodeGenerateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 报价单Service实现.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
public class SaleQuotationServiceImpl extends ServiceImplX<SaleQuotationMapper, SaleQuotationEntity>
        implements ISaleQuotationService {

    private final SaleQuotationMapper saleMapper;
    private final CodeGenerateService codeGenerateService;
    private final AuditEngineService auditEngineService;

    private static final String DOC_TYPE = "SALE_QUOTATION";

    @Override
    @Transactional(readOnly = true)
    public Page<SaleQuotationListVO> pageList(SaleQuotationQueryDTO query) {
        Page<SaleQuotationListVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        saleMapper.selectPageList(page, query);
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public SaleQuotationDetailVO getDetail(Long id) {
        SaleQuotationDetailVO detail = saleMapper.selectDetailById(id);
        if (detail == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "报价单不存在, id: " + id);
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SaleQuotationCreateDTO dto) {
        validateNameUniqueness(dto.getSaleName(), null);
        if (dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "明细行不能为空");
        }
        String code = codeGenerateService.generate("sale");
        SaleQuotationEntity entity = BeanUtil.copyProperties(dto, SaleQuotationEntity.class);
        entity.setSaleNo(code);
        entity.setStatus(0);
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(SaleQuotationUpdateDTO dto) {
        SaleQuotationEntity existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "报价单不存在, id: " + dto.getId());
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "仅草稿状态可修改");
        }
        validateNameUniqueness(dto.getSaleName(), dto.getId());
        BeanUtil.copyProperties(dto, existing);
        updateById(existing);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        SaleQuotationEntity existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "报价单不存在, id: " + id);
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "仅草稿状态可删除");
        }
        removeById(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitAudit(Long id, String opinion) {
        SaleQuotationEntity existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "报价单不存在, id: " + id);
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "仅草稿状态可提交审核");
        }
        AuditSubmitDTO submitDTO = new AuditSubmitDTO();
        submitDTO.setDocType(DOC_TYPE);
        submitDTO.setDocId(id);
        submitDTO.setSubmitRemark(opinion);
        auditEngineService.submit(submitDTO);
        existing.setStatus(1);
        updateById(existing);
        return true;
    }

    private void validateNameUniqueness(String name, Long excludeId) {
        LambdaQueryWrapper<SaleQuotationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaleQuotationEntity::getSaleName, name);
        if (excludeId != null) {
            wrapper.ne(SaleQuotationEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "报价单名称已存在: " + name);
        }
    }
}
