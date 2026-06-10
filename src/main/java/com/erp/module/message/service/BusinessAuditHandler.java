package com.erp.module.message.service;

/**
 * 业务审批处理器接口 - 策略模式，按business_type路由到各业务模块.
 *
 * @author AI
 */
public interface BusinessAuditHandler {

    void approve(Long businessId, String opinion);

    void reject(Long businessId, String reason);
}
