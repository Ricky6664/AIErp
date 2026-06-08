package com.erp.approval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.approval.dto.DefinitionCreateDTO;
import com.erp.approval.dto.DefinitionQueryDTO;
import com.erp.approval.dto.DefinitionUpdateDTO;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.service.IApprovalDefinitionService;
import com.erp.approval.vo.DefinitionDetailVO;
import com.erp.approval.vo.DefinitionListVO;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 审批定义Service实现.
 *
 * @author AI
 */
@Service
public class ApprovalDefinitionServiceImpl
        extends ServiceImpl<ApprovalDefinitionMapper, ApprovalDefinitionEntity>
        implements IApprovalDefinitionService {

    @Override
    public PageResult<DefinitionListVO> pageList(DefinitionQueryDTO query) {
        LambdaQueryWrapper<ApprovalDefinitionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getDefinitionName()),
                ApprovalDefinitionEntity::getDefinitionName, query.getDefinitionName());
        wrapper.eq(StringUtils.hasText(query.getBusinessType()),
                ApprovalDefinitionEntity::getBusinessType, query.getBusinessType());
        wrapper.eq(query.getEnableFlag() != null,
                ApprovalDefinitionEntity::getEnableFlag, query.getEnableFlag());
        wrapper.orderByDesc(ApprovalDefinitionEntity::getCreateTime);

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        IPage<ApprovalDefinitionEntity> page = page(new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(page).convert(this::toListVO);
    }

    @Override
    public DefinitionDetailVO getById(Long id) {
        ApprovalDefinitionEntity entity = getEntityOrThrow(id);
        return toDetailVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(DefinitionCreateDTO dto) {
        checkCodeDuplicate(dto.getDefinitionCode(), null);

        ApprovalDefinitionEntity entity = new ApprovalDefinitionEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setEnableFlag(dto.getEnableFlag() != null ? dto.getEnableFlag() : true);
        save(entity);

        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, DefinitionUpdateDTO dto) {
        ApprovalDefinitionEntity entity = getEntityOrThrow(id);

        if (StringUtils.hasText(dto.getDefinitionName())) {
            entity.setDefinitionName(dto.getDefinitionName());
        }
        if (dto.getBusinessType() != null) {
            entity.setBusinessType(dto.getBusinessType());
        }
        if (dto.getFlowConfig() != null) {
            entity.setFlowConfig(dto.getFlowConfig());
        }
        if (dto.getEnableFlag() != null) {
            entity.setEnableFlag(dto.getEnableFlag());
        }

        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getEntityOrThrow(id);
        removeById(id);
    }

    private ApprovalDefinitionEntity getEntityOrThrow(Long id) {
        ApprovalDefinitionEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return entity;
    }

    private void checkCodeDuplicate(String code, Long excludeId) {
        LambdaQueryWrapper<ApprovalDefinitionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalDefinitionEntity::getDefinitionCode, code);
        if (excludeId != null) {
            wrapper.ne(ApprovalDefinitionEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS);
        }
    }

    private DefinitionListVO toListVO(ApprovalDefinitionEntity entity) {
        DefinitionListVO vo = new DefinitionListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private DefinitionDetailVO toDetailVO(ApprovalDefinitionEntity entity) {
        DefinitionDetailVO vo = new DefinitionDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
