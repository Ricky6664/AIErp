package com.erp.approval.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.approval.dto.InstanceCreateDTO;
import com.erp.approval.dto.InstanceQueryDTO;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.service.IApprovalInstanceService;
import com.erp.approval.vo.InstanceVO;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 审批实例Service实现.
 *
 * @author AI
 */
@Service
public class ApprovalInstanceServiceImpl
        extends ServiceImpl<ApprovalInstanceMapper, ApprovalInstanceEntity>
        implements IApprovalInstanceService {

    private final ApprovalDefinitionMapper definitionMapper;

    public ApprovalInstanceServiceImpl(ApprovalDefinitionMapper definitionMapper) {
        this.definitionMapper = definitionMapper;
    }

    @Override
    public PageResult<InstanceVO> pageList(InstanceQueryDTO query) {
        LambdaQueryWrapper<ApprovalInstanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(query.getBusinessType()),
                ApprovalInstanceEntity::getBusinessType, query.getBusinessType());
        wrapper.eq(StringUtils.hasText(query.getStatus()),
                ApprovalInstanceEntity::getStatus, query.getStatus());
        wrapper.eq(query.getApplicantId() != null,
                ApprovalInstanceEntity::getApplicantId, query.getApplicantId());
        wrapper.orderByDesc(ApprovalInstanceEntity::getCreateTime);

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        IPage<ApprovalInstanceEntity> page = page(new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(page).convert(this::toVO);
    }

    @Override
    public InstanceVO getById(Long id) {
        ApprovalInstanceEntity entity = getEntityOrThrow(id);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(InstanceCreateDTO dto) {
        ApprovalDefinitionEntity definition = definitionMapper.selectById(dto.getDefinitionId());
        if (definition == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (Boolean.FALSE.equals(definition.getEnableFlag())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID);
        }

        LambdaQueryWrapper<ApprovalInstanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstanceEntity::getBusinessType, dto.getBusinessType());
        wrapper.eq(ApprovalInstanceEntity::getBusinessId, dto.getBusinessId());
        wrapper.eq(ApprovalInstanceEntity::getStatus, "PENDING");
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS);
        }

        ApprovalInstanceEntity entity = new ApprovalInstanceEntity();
        entity.setDefinitionId(dto.getDefinitionId());
        entity.setBusinessType(dto.getBusinessType());
        entity.setBusinessId(dto.getBusinessId());
        entity.setApplicantId(StpUtil.getLoginIdAsLong());
        entity.setStatus("PENDING");
        save(entity);

        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(Long id) {
        ApprovalInstanceEntity entity = getEntityOrThrow(id);

        if (!"PENDING".equals(entity.getStatus())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID);
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!currentUserId.equals(entity.getApplicantId())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        entity.setStatus("WITHDRAWN");
        updateById(entity);
    }

    private ApprovalInstanceEntity getEntityOrThrow(Long id) {
        ApprovalInstanceEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return entity;
    }

    private InstanceVO toVO(ApprovalInstanceEntity entity) {
        InstanceVO vo = new InstanceVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
