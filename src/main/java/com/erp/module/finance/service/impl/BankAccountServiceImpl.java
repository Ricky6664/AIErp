package com.erp.module.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.finance.dto.BankAccountCreateDTO;
import com.erp.module.finance.dto.BankAccountQueryDTO;
import com.erp.module.finance.dto.BankAccountUpdateDTO;
import com.erp.module.finance.entity.BankAccountEntity;
import com.erp.module.finance.mapper.BankAccountMapper;
import com.erp.module.finance.service.IBankAccountService;
import com.erp.module.finance.vo.BankAccountVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BankAccountServiceImpl extends ServiceImpl<BankAccountMapper, BankAccountEntity>
        implements IBankAccountService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "新增", description = "新增银行账户")
    public BankAccountVO create(BankAccountCreateDTO dto) {
        validateBankAccountNoUniqueness(dto.getBankAccountNo(), null);
        BankAccountEntity entity = new BankAccountEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "修改", description = "修改银行账户")
    public BankAccountVO update(Long id, BankAccountUpdateDTO dto) {
        BankAccountEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "银行账户不存在");
        }
        validateBankAccountNoUniqueness(dto.getBankAccountNo(), id);
        validateStatusTransition(existing.getStatus(), dto.getStatus());
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "删除", description = "删除银行账户")
    public void delete(Long id) {
        BankAccountEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "银行账户不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountVO getById(Long id) {
        BankAccountEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "银行账户不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<BankAccountVO> pageList(BankAccountQueryDTO query) {
        LambdaQueryWrapper<BankAccountEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getAccountName()),
                BankAccountEntity::getAccountName, query.getAccountName());
        wrapper.like(StringUtils.hasText(query.getBankAccountNo()),
                BankAccountEntity::getBankAccountNo, query.getBankAccountNo());
        wrapper.eq(StringUtils.hasText(query.getBankName()),
                BankAccountEntity::getBankName, query.getBankName());
        wrapper.eq(query.getCurrencyId() != null,
                BankAccountEntity::getCurrencyId, query.getCurrencyId());
        wrapper.eq(StringUtils.hasText(query.getAccountType()),
                BankAccountEntity::getAccountType, query.getAccountType());
        wrapper.eq(query.getStatus() != null,
                BankAccountEntity::getStatus, query.getStatus());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "accountName" -> BankAccountEntity::getAccountName;
                case "bankAccountNo" -> BankAccountEntity::getBankAccountNo;
                case "bankName" -> BankAccountEntity::getBankName;
                case "createTime" -> BankAccountEntity::getCreateTime;
                case "updateTime" -> BankAccountEntity::getUpdateTime;
                default -> BankAccountEntity::getCreateTime;
            });
        } else {
            wrapper.orderByDesc(BankAccountEntity::getCreateTime);
        }

        IPage<BankAccountEntity> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    private void validateBankAccountNoUniqueness(String bankAccountNo, Long excludeId) {
        if (!StringUtils.hasText(bankAccountNo)) {
            return;
        }
        LambdaQueryWrapper<BankAccountEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BankAccountEntity::getBankAccountNo, bankAccountNo);
        if (excludeId != null) {
            wrapper.ne(BankAccountEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "银行账号已存在");
        }
    }

    private void validateStatusTransition(Integer oldStatus, Integer newStatus) {
        if (newStatus == null || newStatus.equals(oldStatus)) {
            return;
        }
        if (oldStatus != null && oldStatus == 0 && newStatus == 1) {
            return;
        }
        if (oldStatus != null && oldStatus == 1 && newStatus == 0) {
            return;
        }
        throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "银行账户状态变更不合法");
    }

    private BankAccountVO toVO(BankAccountEntity entity) {
        BankAccountVO vo = new BankAccountVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
