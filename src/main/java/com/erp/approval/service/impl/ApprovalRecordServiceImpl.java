package com.erp.approval.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.approval.dto.RecordActionDTO;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.approval.service.IApprovalRecordService;
import com.erp.approval.vo.RecordVO;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 审批记录Service实现.
 *
 * @author AI
 */
@Service
public class ApprovalRecordServiceImpl
        extends ServiceImpl<ApprovalRecordMapper, ApprovalRecordEntity>
        implements IApprovalRecordService {

    private final ApprovalInstanceMapper instanceMapper;

    public ApprovalRecordServiceImpl(ApprovalInstanceMapper instanceMapper) {
        this.instanceMapper = instanceMapper;
    }

    @Override
    public PageResult<RecordVO> pageListByInstance(Long instanceId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ApprovalRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalRecordEntity::getInstanceId, instanceId);
        wrapper.orderByDesc(ApprovalRecordEntity::getCreateTime);

        int pn = pageNum != null ? pageNum : 1;
        int ps = pageSize != null ? pageSize : 10;
        IPage<ApprovalRecordEntity> page = page(new Page<>(pn, ps), wrapper);

        return PageResult.of(page).convert(this::toVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long recordAction(RecordActionDTO dto) {
        ApprovalInstanceEntity instance = instanceMapper.selectById(dto.getInstanceId());
        if (instance == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        if (!"PENDING".equals(instance.getStatus())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID);
        }

        String action = dto.getAction();
        if ("APPROVE".equals(action)) {
            instance.setStatus("APPROVED");
        } else if ("REJECT".equals(action)) {
            instance.setStatus("REJECTED");
        } else {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        ApprovalRecordEntity record = new ApprovalRecordEntity();
        record.setInstanceId(dto.getInstanceId());
        record.setAction(action);
        record.setComment(dto.getComment());
        record.setApproverId(StpUtil.getLoginIdAsLong());
        record.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        save(record);

        instanceMapper.updateById(instance);

        return record.getId();
    }

    private RecordVO toVO(ApprovalRecordEntity entity) {
        RecordVO vo = new RecordVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
