package com.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.finance.dto.VoucherWordCreateDTO;
import com.erp.finance.dto.VoucherWordQueryDTO;
import com.erp.finance.dto.VoucherWordUpdateDTO;
import com.erp.finance.entity.VoucherWordEntity;
import com.erp.finance.mapper.VoucherWordMapper;
import com.erp.finance.service.IVoucherWordService;
import com.erp.finance.vo.VoucherWordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class VoucherWordServiceImpl extends ServiceImpl<VoucherWordMapper, VoucherWordEntity>
        implements IVoucherWordService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "新增", description = "新增凭证字")
    public VoucherWordVO create(VoucherWordCreateDTO dto) {
        validateWordCodeUniqueness(dto.getWordCode(), null);
        VoucherWordEntity entity = new VoucherWordEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "修改", description = "修改凭证字")
    public VoucherWordVO update(Long id, VoucherWordUpdateDTO dto) {
        VoucherWordEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "凭证字不存在");
        }
        validateWordCodeUniqueness(dto.getWordCode(), id);
        validateStatusTransition(existing.getStatus(), dto.getStatus());
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "删除", description = "删除凭证字")
    public void delete(Long id) {
        VoucherWordEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "凭证字不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "财务基础设置", action = "切换状态", description = "切换凭证字启用状态")
    public void updateStatus(Long id, Integer status) {
        VoucherWordEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "凭证字不存在");
        }
        validateStatusTransition(existing.getStatus(), status);
        existing.setStatus(status);
        updateById(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherWordVO getById(Long id) {
        VoucherWordEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "凭证字不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<VoucherWordVO> pageList(VoucherWordQueryDTO query) {
        LambdaQueryWrapper<VoucherWordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getWordName()),
                VoucherWordEntity::getWordName, query.getWordName());
        wrapper.like(StringUtils.hasText(query.getWordCode()),
                VoucherWordEntity::getWordCode, query.getWordCode());
        wrapper.eq(query.getStatus() != null,
                VoucherWordEntity::getStatus, query.getStatus());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "wordName" -> VoucherWordEntity::getWordName;
                case "wordCode" -> VoucherWordEntity::getWordCode;
                case "sortOrder" -> VoucherWordEntity::getSortOrder;
                case "createTime" -> VoucherWordEntity::getCreateTime;
                case "updateTime" -> VoucherWordEntity::getUpdateTime;
                default -> VoucherWordEntity::getCreateTime;
            });
        } else {
            wrapper.orderByAsc(VoucherWordEntity::getSortOrder);
        }

        IPage<VoucherWordEntity> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    private void validateWordCodeUniqueness(String wordCode, Long excludeId) {
        if (!StringUtils.hasText(wordCode)) {
            return;
        }
        LambdaQueryWrapper<VoucherWordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoucherWordEntity::getWordCode, wordCode);
        if (excludeId != null) {
            wrapper.ne(VoucherWordEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "凭证字编码已存在");
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
        throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "凭证字状态变更不合法");
    }

    private VoucherWordVO toVO(VoucherWordEntity entity) {
        VoucherWordVO vo = new VoucherWordVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
