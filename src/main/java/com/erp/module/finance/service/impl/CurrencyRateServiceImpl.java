package com.erp.module.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.finance.dto.CurrencyRateCreateDTO;
import com.erp.module.finance.dto.CurrencyRateQueryDTO;
import com.erp.module.finance.dto.CurrencyRateUpdateDTO;
import com.erp.module.finance.entity.CurrencyRateEntity;
import com.erp.module.finance.mapper.CurrencyRateMapper;
import com.erp.module.finance.service.ICurrencyRateService;
import com.erp.module.finance.vo.CurrencyRateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class CurrencyRateServiceImpl extends ServiceImpl<CurrencyRateMapper, CurrencyRateEntity>
        implements ICurrencyRateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "新增", description = "新增币种汇率")
    public CurrencyRateVO create(CurrencyRateCreateDTO dto) {
        validateCurrencyCodeUniqueness(dto.getCurrencyCode(), null);
        validateEffectiveDate(dto.getEffectiveDate());
        CurrencyRateEntity entity = new CurrencyRateEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "修改", description = "修改币种汇率")
    public CurrencyRateVO update(Long id, CurrencyRateUpdateDTO dto) {
        CurrencyRateEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "币种汇率不存在");
        }
        validateCurrencyCodeUniqueness(dto.getCurrencyCode(), id);
        validateEffectiveDate(dto.getEffectiveDate());
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "删除", description = "删除币种汇率")
    public void delete(Long id) {
        CurrencyRateEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "币种汇率不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyRateVO getById(Long id) {
        CurrencyRateEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "币种汇率不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CurrencyRateVO> pageList(CurrencyRateQueryDTO query) {
        LambdaQueryWrapper<CurrencyRateEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(query.getCurrencyCode()),
                CurrencyRateEntity::getCurrencyCode, query.getCurrencyCode());
        wrapper.like(StringUtils.hasText(query.getCurrencyName()),
                CurrencyRateEntity::getCurrencyName, query.getCurrencyName());
        wrapper.eq(query.getRateType() != null,
                CurrencyRateEntity::getRateType, query.getRateType());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "currencyCode" -> CurrencyRateEntity::getCurrencyCode;
                case "currencyName" -> CurrencyRateEntity::getCurrencyName;
                case "exchangeRate" -> CurrencyRateEntity::getExchangeRate;
                case "effectiveDate" -> CurrencyRateEntity::getEffectiveDate;
                case "createTime" -> CurrencyRateEntity::getCreateTime;
                case "updateTime" -> CurrencyRateEntity::getUpdateTime;
                default -> CurrencyRateEntity::getCreateTime;
            });
        } else {
            wrapper.orderByDesc(CurrencyRateEntity::getCreateTime);
        }

        IPage<CurrencyRateEntity> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    private void validateCurrencyCodeUniqueness(String currencyCode, Long excludeId) {
        if (!StringUtils.hasText(currencyCode)) {
            return;
        }
        LambdaQueryWrapper<CurrencyRateEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CurrencyRateEntity::getCurrencyCode, currencyCode);
        if (excludeId != null) {
            wrapper.ne(CurrencyRateEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "币种编码已存在");
        }
    }

    private void validateEffectiveDate(LocalDate effectiveDate) {
        if (effectiveDate == null) {
            return;
        }
        if (effectiveDate.isAfter(LocalDate.now().plusDays(30))) {
            throw new BusinessException(ErrorCode.PARAM_RANGE_ERROR, "汇率日期不得晚于当前日期+30天");
        }
    }

    private CurrencyRateVO toVO(CurrencyRateEntity entity) {
        CurrencyRateVO vo = new CurrencyRateVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
