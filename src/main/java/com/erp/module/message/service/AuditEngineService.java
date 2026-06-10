package com.erp.module.message.service;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 审批引擎路由服务 - 根据business_type策略模式路由到对应审批处理器.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
public class AuditEngineService {

    private final Map<String, BusinessAuditHandler> handlerMap;

    public void approve(String businessType, Long businessId, String opinion) {
        BusinessAuditHandler handler = handlerMap.get(businessType + "AuditHandler");
        if (handler == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR,
                    "未找到业务审批处理器: " + businessType);
        }
        handler.approve(businessId, opinion);
    }

    public void reject(String businessType, Long businessId, String reason) {
        BusinessAuditHandler handler = handlerMap.get(businessType + "AuditHandler");
        if (handler == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR,
                    "未找到业务审批处理器: " + businessType);
        }
        handler.reject(businessId, reason);
    }
}
