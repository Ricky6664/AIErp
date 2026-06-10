package com.erp.approval.service;

import com.erp.approval.dto.MyApprovalQueryDTO;
import com.erp.approval.vo.MyApprovalVO;
import com.erp.common.result.PageResult;

/**
 * 我的审批Service接口.
 *
 * @author AI
 */
public interface IApprovalMyService {

    PageResult<MyApprovalVO> pageList(MyApprovalQueryDTO query);
}
