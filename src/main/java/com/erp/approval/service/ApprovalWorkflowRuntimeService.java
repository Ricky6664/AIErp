package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 审批流程运行时引擎Service.
 * 负责审批状态机编排：提交→审核→驳回→转办→加签→撤回.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalWorkflowRuntimeService {

    private final ApprovalDefinitionMapper definitionMapper;
    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalRecordMapper recordMapper;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long startWorkflow(Long definitionId, Long businessId, String businessType) {
        ApprovalDefinitionEntity definition = definitionMapper.selectById(definitionId);
        if (definition == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (Boolean.FALSE.equals(definition.getEnableFlag())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID);
        }

        ApprovalInstanceEntity instance = new ApprovalInstanceEntity();
        instance.setDefinitionId(definitionId);
        instance.setBusinessType(businessType);
        instance.setBusinessId(businessId);
        instance.setApplicantId(StpUtil.getLoginIdAsLong());
        instance.setStatus("PENDING");
        instanceMapper.insert(instance);

        log.info("工作流启动: instanceId={}, definitionId={}, businessType={}, businessId={}, applicantId={}",
                instance.getId(), definitionId, businessType, businessId, instance.getApplicantId());
        return instance.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void advanceWorkflow(Long instanceId, String action, String comment) {
        ApprovalInstanceEntity instance = validateInstancePending(instanceId);
        ApprovalDefinitionEntity definition = definitionMapper.selectById(instance.getDefinitionId());
        if (definition == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        List<Map<String, Object>> nodes = parseFlowConfig(definition.getFlowConfig());

        ApprovalRecordEntity record = new ApprovalRecordEntity();
        record.setInstanceId(instanceId);
        record.setAction(action);
        record.setComment(comment);
        record.setApproverId(StpUtil.getLoginIdAsLong());
        record.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        switch (action) {
            case "APPROVE":
                handleApprove(instance, nodes, record);
                break;
            case "REJECT":
                handleReject(instance, record);
                break;
            default:
                record.setNodeName(getCurrentNodeName(instance, nodes));
                recordMapper.insert(record);
                break;
        }

        log.info("工作流推进: instanceId={}, action={}, operatorId={}", instanceId, action, StpUtil.getLoginIdAsLong());
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeWorkflow(Long instanceId) {
        ApprovalInstanceEntity instance = validateInstancePending(instanceId);

        ApprovalRecordEntity record = new ApprovalRecordEntity();
        record.setInstanceId(instanceId);
        record.setAction("COMPLETE");
        record.setNodeName("最终完成");
        record.setApproverId(StpUtil.getLoginIdAsLong());
        record.setComment("审批流程完成");
        record.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        recordMapper.insert(record);

        instance.setStatus("APPROVED");
        instanceMapper.updateById(instance);

        log.info("工作流完成: instanceId={}, operatorId={}", instanceId, StpUtil.getLoginIdAsLong());
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejectWorkflow(Long instanceId, String comment) {
        ApprovalInstanceEntity instance = validateInstancePending(instanceId);

        ApprovalRecordEntity record = new ApprovalRecordEntity();
        record.setInstanceId(instanceId);
        record.setAction("REJECT");
        record.setNodeName("驳回");
        record.setApproverId(StpUtil.getLoginIdAsLong());
        record.setComment(comment);
        record.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        recordMapper.insert(record);

        instance.setStatus("REJECTED");
        instanceMapper.updateById(instance);

        log.info("工作流驳回: instanceId={}, operatorId={}", instanceId, StpUtil.getLoginIdAsLong());
    }

    @Transactional(rollbackFor = Exception.class)
    public void withdrawWorkflow(Long instanceId) {
        ApprovalInstanceEntity instance = instanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (!"PENDING".equals(instance.getStatus())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID);
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!currentUserId.equals(instance.getApplicantId())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }

        ApprovalRecordEntity record = new ApprovalRecordEntity();
        record.setInstanceId(instanceId);
        record.setAction("WITHDRAW");
        record.setNodeName("撤回");
        record.setApproverId(currentUserId);
        record.setComment("申请人主动撤回");
        record.setOperateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        recordMapper.insert(record);

        instance.setStatus("WITHDRAWN");
        instanceMapper.updateById(instance);

        log.info("工作流撤回: instanceId={}, applicantId={}", instanceId, currentUserId);
    }

    public ApprovalInstanceEntity getWorkflowState(Long instanceId) {
        ApprovalInstanceEntity instance = instanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return instance;
    }

    private void handleApprove(ApprovalInstanceEntity instance, List<Map<String, Object>> nodes,
                               ApprovalRecordEntity record) {
        Long currentNodeId = instance.getCurrentNodeId();
        int currentNodeIndex = findNodeIndex(nodes, currentNodeId);

        if (currentNodeIndex >= 0 && currentNodeIndex < nodes.size() - 1) {
            Map<String, Object> nextNode = nodes.get(currentNodeIndex + 1);
            Number nextNodeOrder = (Number) nextNode.get("order");
            instance.setCurrentNodeId(nextNodeOrder.longValue());
            record.setNodeName((String) nextNode.getOrDefault("name", "节点" + nextNodeOrder));
        } else {
            instance.setStatus("APPROVED");
            record.setNodeName("审批完成");
        }

        recordMapper.insert(record);
        instanceMapper.updateById(instance);
    }

    private void handleReject(ApprovalInstanceEntity instance, ApprovalRecordEntity record) {
        instance.setStatus("REJECTED");
        record.setNodeName("驳回");
        recordMapper.insert(record);
        instanceMapper.updateById(instance);
    }

    private String getCurrentNodeName(ApprovalInstanceEntity instance, List<Map<String, Object>> nodes) {
        int index = findNodeIndex(nodes, instance.getCurrentNodeId());
        if (index >= 0) {
            return (String) nodes.get(index).getOrDefault("name", "节点" + index);
        }
        return "未知节点";
    }

    private int findNodeIndex(List<Map<String, Object>> nodes, Long nodeOrder) {
        if (nodes == null || nodeOrder == null) {
            return 0;
        }
        for (int i = 0; i < nodes.size(); i++) {
            Number order = (Number) nodes.get(i).get("order");
            if (order != null && order.longValue() == nodeOrder.longValue()) {
                return i;
            }
        }
        return -1;
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

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseFlowConfig(String flowConfig) {
        if (flowConfig == null || flowConfig.isBlank()) {
            return List.of();
        }
        try {
            Map<String, Object> config = objectMapper.readValue(flowConfig,
                    new TypeReference<Map<String, Object>>() {});
            return (List<Map<String, Object>>) config.getOrDefault("nodes", List.of());
        } catch (Exception e) {
            log.warn("解析流程配置JSON失败: {}", e.getMessage());
            return List.of();
        }
    }
}
