package com.erp.sale.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.service.AuditEngineService;
import com.erp.sale.dto.SaleQuotationCreateDTO;
import com.erp.sale.dto.SaleQuotationDetailCreateDTO;
import com.erp.sale.dto.SaleQuotationQueryDTO;
import com.erp.sale.dto.SaleQuotationUpdateDTO;
import com.erp.sale.entity.SaleQuotationEntity;
import com.erp.sale.mapper.SaleQuotationMapper;
import com.erp.sale.service.ISaleOrderService;
import com.erp.sale.service.ISaleQuotationService;
import com.erp.sale.vo.SaleQuotationDetailVO;
import com.erp.sale.vo.SaleQuotationListVO;
import com.erp.system.codegen.CodeGenerateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * 报价单Service实现.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
public class SaleQuotationServiceImpl extends ServiceImplX<SaleQuotationMapper, SaleQuotationEntity>
        implements ISaleQuotationService, ISaleOrderService {

    private final SaleQuotationMapper saleMapper;
    private final CodeGenerateService codeGenerateService;
    private final AuditEngineService auditEngineService;

    private static final String DOC_TYPE = "SALE_QUOTATION";

    /** 状态流转配置：fromStatus → 允许的toStatus集合 */
    private static final Map<Integer, Set<Integer>> ALLOWED_TRANSITIONS = Map.of(
        0, Set.of(1),        // 草稿 → 待审核
        1, Set.of(2, 4),     // 待审核 → 已审核/已作废
        2, Set.of(0, 4)      // 已审核 → 反审(回草稿)/已作废
    );

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
        validateCreate(dto);
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
        validateUpdate(dto);
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
        validateStatusTransition(existing.getStatus(), 1);
        AuditSubmitDTO submitDTO = new AuditSubmitDTO();
        submitDTO.setDocType(DOC_TYPE);
        submitDTO.setDocId(id);
        submitDTO.setSubmitRemark(opinion);
        auditEngineService.submit(submitDTO);
        existing.setStatus(1);
        updateById(existing);
        return true;
    }

    /** 新增校验：先非空→再唯一性→再金额计算 */
    private void validateCreate(SaleQuotationCreateDTO dto) {
        if (CollUtil.isEmpty(dto.getDetails())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "明细行不能为空");
        }
        validateNameUniqueness(dto.getSaleName(), null);
        validateAmount(dto);
    }

    /** 修改校验：先非空→再唯一性→再金额计算 */
    private void validateUpdate(SaleQuotationUpdateDTO dto) {
        if (CollUtil.isEmpty(dto.getDetails())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "明细行不能为空");
        }
        validateNameUniqueness(dto.getSaleName(), dto.getId());
        validateAmount(dto);
    }

    /** 状态流转校验：基于状态机配置，禁止硬编码if/else */
    private void validateStatusTransition(Integer fromStatus, Integer toStatus) {
        Set<Integer> allowed = ALLOWED_TRANSITIONS.get(fromStatus);
        if (allowed == null || !allowed.contains(toStatus)) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID,
                "状态流转不允许: " + fromStatus + " → " + toStatus);
        }
    }

    /** 金额计算校验：逐行校验数量/单价/税率/折扣 */
    private void validateAmount(SaleQuotationCreateDTO dto) {
        for (int i = 0; i < dto.getDetails().size(); i++) {
            SaleQuotationDetailCreateDTO detail = dto.getDetails().get(i);
            if (detail.getQuantity() == null || detail.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "第" + (i + 1) + "行数量必须大于0");
            }
            if (detail.getUnitPrice() == null || detail.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "第" + (i + 1) + "行单价不能为负");
            }
            if (detail.getTaxRate() != null
                    && (detail.getTaxRate().compareTo(BigDecimal.ZERO) < 0
                        || detail.getTaxRate().compareTo(new BigDecimal("100")) > 0)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "第" + (i + 1) + "行税率须在0-100之间");
            }
            if (detail.getDiscount() != null
                    && (detail.getDiscount().compareTo(BigDecimal.ZERO) < 0
                        || detail.getDiscount().compareTo(new BigDecimal("100")) > 0)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "第" + (i + 1) + "行折扣须在0-100之间");
            }
        }
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
