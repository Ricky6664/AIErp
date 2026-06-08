package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.approval.dto.RecordCountersignDTO;
import com.erp.approval.dto.RecordTransferDTO;
import com.erp.approval.dto.RecordUrgeDTO;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 审批记录核心业务Service.
 * 负责审批运行时状态机：审核→驳回→转办→加签→催办.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalRecordCoreService {

    private final ApprovalRecordMapper recordMapper;
    private final ApprovalInstanceMapper instanceMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long transferAction(RecordTransferDTO dto) {
        ApprovalRecordEntity record = recordMapper.selectById(dto.getRecordId());
        if (record == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        ApprovalInstanceEntity instance = validateInstancePending(record.getInstanceId());

        Long fromApproverId = record.getApproverId();
        record.setAction("TRANSFER");
        record.setComment(dto.getComment());
        record.setApproverId(dto.getTargetApproverId());
        record.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        recordMapper.updateById(record);

        log.info("审批转办: recordId={}, fromApprover={}, toApprover={}, operator={}",
                record.getId(), fromApproverId, dto.getTargetApproverId(),
                StpUtil.getLoginIdAsLong());
        return record.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long countersignAction(RecordCountersignDTO dto) {
        ApprovalRecordEntity record = recordMapper.selectById(dto.getRecordId());
        if (record == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        validateInstancePending(record.getInstanceId());

        ApprovalRecordEntity countersignRecord = new ApprovalRecordEntity();
        countersignRecord.setInstanceId(record.getInstanceId());
        countersignRecord.setNodeName(record.getNodeName());
        countersignRecord.setApproverId(dto.getCountersignApproverId());
        countersignRecord.setAction("COUNTERSIGN");
        countersignRecord.setComment(dto.getComment());
        countersignRecord.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        recordMapper.insert(countersignRecord);

        log.info("审批加签: recordId={}, countersignApprover={}, operator={}",
                record.getId(), dto.getCountersignApproverId(), StpUtil.getLoginIdAsLong());
        return countersignRecord.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long urgeAction(RecordUrgeDTO dto) {
        ApprovalRecordEntity record = recordMapper.selectById(dto.getRecordId());
        if (record == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        validateInstancePending(record.getInstanceId());

        ApprovalRecordEntity urgeRecord = new ApprovalRecordEntity();
        urgeRecord.setInstanceId(record.getInstanceId());
        urgeRecord.setNodeName(record.getNodeName());
        urgeRecord.setApproverId(record.getApproverId());
        urgeRecord.setAction("URGE");
        urgeRecord.setComment(dto.getMessage() != null ? dto.getMessage() : "催办提醒");
        urgeRecord.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        recordMapper.insert(urgeRecord);

        log.info("审批催办: recordId={}, targetApprover={}, operator={}",
                record.getId(), record.getApproverId(), StpUtil.getLoginIdAsLong());
        return urgeRecord.getId();
    }

    private ApprovalInstanceEntity validateInstancePending(Long instanceId) {
        ApprovalInstanceEntity instance = instanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (!"PENDING".equals(instance.getStatus())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID);
        }
        return instance;
    }
}
