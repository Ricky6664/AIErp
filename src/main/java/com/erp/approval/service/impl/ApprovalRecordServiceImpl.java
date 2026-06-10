package com.erp.approval.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.approval.dto.RecordActionDTO;
import com.erp.approval.dto.RecordLogQueryDTO;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.approval.service.IApprovalRecordService;
import com.erp.approval.vo.RecordLogVO;
import com.erp.approval.vo.RecordVO;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public PageResult<RecordLogVO> pageLogList(RecordLogQueryDTO query) {
        int pn = query.getPageNum() != null ? query.getPageNum() : 1;
        int ps = query.getPageSize() != null ? query.getPageSize() : 10;
        int offset = (pn - 1) * ps;

        List<Map<String, Object>> rows = baseMapper.selectLogList(
                query.getDefinitionId(),
                query.getAction(),
                query.getStatus(),
                query.getBusinessType(),
                query.getApplicantId(),
                query.getApproverId(),
                query.getStartTime(),
                query.getEndTime(),
                offset,
                ps);

        long total = baseMapper.selectLogCount(
                query.getDefinitionId(),
                query.getAction(),
                query.getStatus(),
                query.getBusinessType(),
                query.getApplicantId(),
                query.getApproverId(),
                query.getStartTime(),
                query.getEndTime());

        List<RecordLogVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            list.add(toLogVO(row));
        }

        return PageResult.of(list, total, pn, ps);
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

    private RecordLogVO toLogVO(Map<String, Object> row) {
        RecordLogVO vo = new RecordLogVO();
        vo.setRecordId(toLong(row.get("record_id")));
        vo.setInstanceId(toLong(row.get("instance_id")));
        vo.setNodeName((String) row.get("node_name"));
        vo.setApproverId(toLong(row.get("approver_id")));
        vo.setAction((String) row.get("action"));
        vo.setComment((String) row.get("comment"));
        vo.setOperateTime((String) row.get("operate_time"));
        vo.setRecordCreateTime((String) row.get("record_create_time"));
        vo.setDefinitionId(toLong(row.get("definition_id")));
        vo.setBusinessType((String) row.get("business_type"));
        vo.setBusinessId(toLong(row.get("business_id")));
        vo.setApplicantId(toLong(row.get("applicant_id")));
        vo.setInstanceStatus((String) row.get("instance_status"));
        vo.setCurrentNodeId(toLong(row.get("current_node_id")));
        vo.setDefinitionName((String) row.get("definition_name"));
        vo.setDefinitionCode((String) row.get("definition_code"));
        return vo;
    }

    private Long toLong(Object val) {
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return null;
    }
}
