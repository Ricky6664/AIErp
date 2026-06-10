package com.erp.module.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.finance.dto.AccountCreateDTO;
import com.erp.module.finance.dto.AccountQueryDTO;
import com.erp.module.finance.dto.AccountUpdateDTO;
import com.erp.module.finance.entity.AccountEntity;
import com.erp.module.finance.mapper.AccountMapper;
import com.erp.module.finance.service.IAccountService;
import com.erp.module.finance.vo.AccountVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountEntity>
        implements IAccountService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "新增", description = "新增会计科目")
    public AccountVO create(AccountCreateDTO dto) {
        validateAccountCodeUniqueness(dto.getAccountCode(), null);
        if (dto.getParentId() != null) {
            AccountEntity parent = super.getById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "上级会计科目不存在");
            }
            if (Boolean.TRUE.equals(parent.getIsLeaf())) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "上级科目已是末级科目, 无法添加子科目");
            }
        }
        AccountEntity entity = new AccountEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        if (dto.getParentId() != null) {
            lambdaUpdate().set(AccountEntity::getIsLeaf, false)
                    .eq(AccountEntity::getId, dto.getParentId())
                    .update();
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "修改", description = "修改会计科目")
    public AccountVO update(Long id, AccountUpdateDTO dto) {
        AccountEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "会计科目不存在");
        }
        validateAccountCodeUniqueness(dto.getAccountCode(), id);
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "删除", description = "删除会计科目")
    public void delete(Long id) {
        AccountEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "会计科目不存在");
        }
        Long childrenCount = lambdaQuery()
                .eq(AccountEntity::getParentId, id)
                .count();
        if (childrenCount > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该科目下存在子科目, 请先删除子科目");
        }
        Long parentId = existing.getParentId();
        removeById(id);
        if (parentId != null) {
            Long remainingSiblings = lambdaQuery()
                    .eq(AccountEntity::getParentId, parentId)
                    .count();
            if (remainingSiblings == 0) {
                lambdaUpdate().set(AccountEntity::getIsLeaf, true)
                        .eq(AccountEntity::getId, parentId)
                        .update();
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountVO getById(Long id) {
        AccountEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "会计科目不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AccountVO> pageList(AccountQueryDTO query) {
        LambdaQueryWrapper<AccountEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getAccountCode()),
                AccountEntity::getAccountCode, query.getAccountCode());
        wrapper.like(StringUtils.hasText(query.getAccountName()),
                AccountEntity::getAccountName, query.getAccountName());
        wrapper.eq(query.getParentId() != null,
                AccountEntity::getParentId, query.getParentId());
        wrapper.eq(query.getAccountType() != null,
                AccountEntity::getAccountType, query.getAccountType());
        wrapper.eq(StringUtils.hasText(query.getCategory()),
                AccountEntity::getCategory, query.getCategory());
        wrapper.eq(query.getIsLeaf() != null,
                AccountEntity::getIsLeaf, query.getIsLeaf());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "accountCode" -> AccountEntity::getAccountCode;
                case "accountName" -> AccountEntity::getAccountName;
                case "accountType" -> AccountEntity::getAccountType;
                case "createTime" -> AccountEntity::getCreateTime;
                case "updateTime" -> AccountEntity::getUpdateTime;
                default -> AccountEntity::getCreateTime;
            });
        } else {
            wrapper.orderByDesc(AccountEntity::getCreateTime);
        }

        IPage<AccountEntity> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    private void validateAccountCodeUniqueness(String accountCode, Long excludeId) {
        if (!StringUtils.hasText(accountCode)) {
            return;
        }
        LambdaQueryWrapper<AccountEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountEntity::getAccountCode, accountCode);
        if (excludeId != null) {
            wrapper.ne(AccountEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "会计科目编码已存在");
        }
    }

    private AccountVO toVO(AccountEntity entity) {
        AccountVO vo = new AccountVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
