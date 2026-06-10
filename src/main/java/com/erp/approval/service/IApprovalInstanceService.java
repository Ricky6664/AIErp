package com.erp.approval.service;

import com.erp.approval.dto.InstanceCreateDTO;
import com.erp.approval.dto.InstanceQueryDTO;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.vo.InstanceVO;
import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批实例Service接口.
 *
 * @author AI
 */
public interface IApprovalInstanceService extends IServiceX<ApprovalInstanceEntity> {

    PageResult<InstanceVO> pageList(InstanceQueryDTO query);

    InstanceVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    Long submit(@Valid InstanceCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void withdraw(Long id);
}
