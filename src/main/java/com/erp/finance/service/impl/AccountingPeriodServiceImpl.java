package com.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.finance.dto.AccountingPeriodCreateDTO;
import com.erp.finance.dto.AccountingPeriodQueryDTO;
import com.erp.finance.dto.AccountingPeriodUpdateDTO;
import com.erp.finance.entity.AccountingPeriodEntity;
import com.erp.finance.mapper.AccountingPeriodMapper;
import com.erp.finance.service.IAccountingPeriodService;
import com.erp.finance.vo.AccountingPeriodVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AccountingPeriodServiceImpl
        extends ServiceImpl<AccountingPeriodMapper, AccountingPeriodEntity>
        implements IAccountingPeriodService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "新增", description = "新增会计期间")
    public AccountingPeriodVO create(AccountingPeriodCreateDTO dto) {
        validatePeriodOverlap(dto.getFiscalYear(), dto.getStartDate(), dto.getEndDate(), null);
        AccountingPeriodEntity entity = new AccountingPeriodEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "修改", description = "修改会计期间")
    public AccountingPeriodVO update(Long id, AccountingPeriodUpdateDTO dto) {
        AccountingPeriodEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "会计期间不存在");
        }
        validatePeriodOverlap(dto.getFiscalYear(), dto.getStartDate(), dto.getEndDate(), id);
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "删除", description = "删除会计期间")
    public void delete(Long id) {
        AccountingPeriodEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "会计期间不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountingPeriodVO getById(Long id) {
        AccountingPeriodEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "会计期间不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AccountingPeriodVO> pageList(AccountingPeriodQueryDTO query) {
        LambdaQueryWrapper<AccountingPeriodEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(query.getFiscalYear()),
                AccountingPeriodEntity::getFiscalYear, query.getFiscalYear());
        wrapper.eq(StringUtils.hasText(query.getPeriod()),
                AccountingPeriodEntity::getPeriod, query.getPeriod());
        wrapper.eq(StringUtils.hasText(query.getPeriodStatus()),
                AccountingPeriodEntity::getPeriodStatus, query.getPeriodStatus());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "fiscalYear" -> AccountingPeriodEntity::getFiscalYear;
                case "period" -> AccountingPeriodEntity::getPeriod;
                case "startDate" -> AccountingPeriodEntity::getStartDate;
                case "endDate" -> AccountingPeriodEntity::getEndDate;
                case "createTime" -> AccountingPeriodEntity::getCreateTime;
                default -> AccountingPeriodEntity::getFiscalYear;
            });
        } else {
            wrapper.orderByDesc(AccountingPeriodEntity::getFiscalYear);
            wrapper.orderByAsc(AccountingPeriodEntity::getPeriod);
        }

        IPage<AccountingPeriodEntity> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    private void validatePeriodOverlap(String fiscalYear, java.time.LocalDate startDate,
                                        java.time.LocalDate endDate, Long excludeId) {
        if (!StringUtils.hasText(fiscalYear) || startDate == null || endDate == null) {
            return;
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "结束日期不能早于开始日期");
        }
        LambdaQueryWrapper<AccountingPeriodEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountingPeriodEntity::getFiscalYear, fiscalYear);
        if (excludeId != null) {
            wrapper.ne(AccountingPeriodEntity::getId, excludeId);
        }
        wrapper.and(w -> w
                .between(AccountingPeriodEntity::getStartDate, startDate, endDate)
                .or()
                .between(AccountingPeriodEntity::getEndDate, startDate, endDate)
                .or()
                .le(AccountingPeriodEntity::getStartDate, startDate)
                .ge(AccountingPeriodEntity::getEndDate, endDate));
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS,
                    "同一会计年度内期间日期不可重叠");
        }
    }

    private AccountingPeriodVO toVO(AccountingPeriodEntity entity) {
        AccountingPeriodVO vo = new AccountingPeriodVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
