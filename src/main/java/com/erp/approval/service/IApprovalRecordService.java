package com.erp.approval.service;

import com.erp.approval.dto.RecordActionDTO;
import com.erp.approval.dto.RecordLogQueryDTO;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.vo.RecordLogVO;
import com.erp.approval.vo.RecordVO;
import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批记录Service接口.
 *
 * @author AI
 */
public interface IApprovalRecordService extends IServiceX<ApprovalRecordEntity> {

    PageResult<RecordVO> pageListByInstance(Long instanceId, Integer pageNum, Integer pageSize);

    PageResult<RecordLogVO> pageLogList(RecordLogQueryDTO query);

    @Transactional(rollbackFor = Exception.class)
    Long recordAction(@Valid RecordActionDTO dto);
}
