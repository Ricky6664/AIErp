package com.erp.engine.audit.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.model.ApprovalFlowConfig;
import com.erp.engine.audit.model.ApprovalNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 审核引擎与审批流程集成Service.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditApprovalIntegrationService {

    private final AuditConfigService auditConfigService;
    private final ObjectMapper objectMapper;

    /**
     * 创建审批实例 (委托给P1-002审批流程模块).
     */
    public Long createApprovalInstance(String docType, Long docId) {
        SysAuditConfigEntity config = auditConfigService.getConfig(docType);
        if (Boolean.FALSE.equals(config.getApprovalEnabled())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "当前单据类型未启用审批流程");
        }

        ApprovalFlowConfig flowConfig = parseFlowConfig(config.getApprovalFlowConfig());

        // TODO: P1-002审批流程模块完成后对接 ApprovalEngineService.createInstance(param)
        log.info("创建审批实例: docType={}, docId={}, flowDefinitionId={}",
                docType, docId, flowConfig.getFlowDefinitionId());
        return null;
    }

    /**
     * 解析审批流程配置JSON.
     */
    public ApprovalFlowConfig parseFlowConfig(String approvalFlowConfigJson) {
        if (approvalFlowConfigJson == null || approvalFlowConfigJson.isBlank()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "审批流程配置为空");
        }
        try {
            ApprovalFlowConfig config = objectMapper.readValue(
                    approvalFlowConfigJson, ApprovalFlowConfig.class);
            if (config.getNodes() == null) {
                config.setNodes(new ArrayList<>());
            }
            List<ApprovalNode> nodes = config.getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getNodeOrder() == null) {
                    nodes.get(i).setNodeOrder(i + 1);
                }
            }
            return config;
        } catch (JsonProcessingException e) {
            log.error("审批流程配置JSON解析失败: {}", approvalFlowConfigJson, e);
            throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR, "审批流程配置格式错误");
        }
    }
}
