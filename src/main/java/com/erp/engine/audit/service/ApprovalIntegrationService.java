package com.erp.engine.audit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 审批流程集成服务 (P1-002集成点).
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalIntegrationService {

    /**
     * 创建审批实例 (委托给P1-002审批流程模块).
     */
    public void createApprovalInstance(String docType, Long docId) {
        // TODO: P1-002审批流程模块完成后对接
        log.info("创建审批实例: docType={}, docId={}", docType, docId);
    }
}
