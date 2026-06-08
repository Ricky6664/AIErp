package com.erp.engine.audit;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.callback.ApprovalCallbackImpl;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.event.AuditApprovedEvent;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import com.erp.engine.audit.model.ApprovalFlowConfig;
import com.erp.engine.audit.model.ApprovalNode;
import com.erp.engine.audit.service.AuditApprovalIntegrationService;
import com.erp.engine.audit.service.AuditConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 审批流程集成测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("审批流程集成测试")
class ApprovalIntegrationTest {

    private static final String DOC_TYPE = "PURCHASE_ORDER";
    private static final Long DOC_ID = 1001L;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // -- AuditApprovalIntegrationService 测试 --

    @Nested
    @DisplayName("AuditApprovalIntegrationService")
    class AuditApprovalIntegrationServiceTest {

        @Mock
        private AuditConfigService auditConfigService;

        @InjectMocks
        private AuditApprovalIntegrationService integrationService;

        @BeforeEach
        void setUp() {
            integrationService = new AuditApprovalIntegrationService(
                    auditConfigService, new ObjectMapper());
        }

        @Test
        @DisplayName("approvalEnabled=false时抛出异常")
        void shouldThrowWhenApprovalDisabled() {
            SysAuditConfigEntity config = new SysAuditConfigEntity();
            config.setApprovalEnabled(false);
            when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> integrationService.createApprovalInstance(DOC_TYPE, DOC_ID));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("approvalFlowConfig正确解析")
        void shouldParseFlowConfig() throws Exception {
            String json = objectMapper.writeValueAsString(createFlowConfig());
            ApprovalFlowConfig result = integrationService.parseFlowConfig(json);

            assertNotNull(result);
            assertEquals("FLOW_DEF_001", result.getFlowDefinitionId());
            assertEquals(2, result.getNodes().size());
            assertEquals("部门经理审批", result.getNodes().get(0).getNodeName());
            assertEquals(Integer.valueOf(1), result.getNodes().get(0).getNodeOrder());
        }

        @Test
        @DisplayName("approvalFlowConfig为空时抛出异常")
        void shouldThrowWhenFlowConfigEmpty() {
            assertThrows(BusinessException.class,
                    () -> integrationService.parseFlowConfig(null));
            assertThrows(BusinessException.class,
                    () -> integrationService.parseFlowConfig("  "));
        }

        @Test
        @DisplayName("approvalFlowConfig格式错误时抛出异常")
        void shouldThrowWhenFlowConfigInvalid() {
            assertThrows(BusinessException.class,
                    () -> integrationService.parseFlowConfig("{invalid json}"));
        }

        @Test
        @DisplayName("approvalFlowConfig缺少nodes时自动补充空列表")
        void shouldFillEmptyNodesWhenMissing() {
            ApprovalFlowConfig result = integrationService.parseFlowConfig(
                    "{\"flowDefinitionId\":\"FLOW_001\"}");

            assertNotNull(result);
            assertNotNull(result.getNodes());
            assertTrue(result.getNodes().isEmpty());
        }

        @Test
        @DisplayName("nodeOrder为null时自动按顺序填充")
        void shouldFillNodeOrderWhenNull() {
            ApprovalFlowConfig result = integrationService.parseFlowConfig(
                    "{\"flowDefinitionId\":\"FLOW_001\",\"nodes\":[{\"nodeName\":\"A\"},{\"nodeName\":\"B\"}]}");

            assertEquals(Integer.valueOf(1), result.getNodes().get(0).getNodeOrder());
            assertEquals(Integer.valueOf(2), result.getNodes().get(1).getNodeOrder());
        }

        private ApprovalFlowConfig createFlowConfig() {
            ApprovalNode node1 = new ApprovalNode();
            node1.setNodeName("部门经理审批");
            node1.setApproverRole("DEPT_MANAGER");
            node1.setNodeOrder(1);
            node1.setRequired(true);

            ApprovalNode node2 = new ApprovalNode();
            node2.setNodeName("总经理审批");
            node2.setApproverRole("GM");
            node2.setNodeOrder(2);
            node2.setRequired(true);

            ApprovalFlowConfig config = new ApprovalFlowConfig();
            config.setFlowDefinitionId("FLOW_DEF_001");
            config.setNodes(List.of(node1, node2));
            return config;
        }
    }

    // -- ApprovalCallbackImpl 测试 --

    @Nested
    @DisplayName("ApprovalCallbackImpl")
    class ApprovalCallbackImplTest {

        @Mock
        private DocumentStatusMapper documentStatusMapper;

        @Mock
        private AuditLogMapper auditLogMapper;

        @Mock
        private ApplicationEventPublisher eventPublisher;

        @InjectMocks
        private ApprovalCallbackImpl callback;

        @Test
        @DisplayName("approved回调正确将Submitted(1)->Approved(2)")
        void shouldApproveCallbackSuccessfully() {
            DocumentStatusEntity docStatus = new DocumentStatusEntity();
            docStatus.setStatus(1);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(docStatus);
            when(documentStatusMapper.updateStatus(DOC_TYPE, DOC_ID, 2))
                    .thenReturn(1);

            callback.approved(DOC_TYPE, DOC_ID, "同意");

            verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 2);
            verify(auditLogMapper).insert(any(SysAuditLogEntity.class));

            ArgumentCaptor<AuditApprovedEvent> eventCaptor =
                    ArgumentCaptor.forClass(AuditApprovedEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertEquals(DOC_TYPE, eventCaptor.getValue().getDocType());
            assertEquals(DOC_ID, eventCaptor.getValue().getDocId());
        }

        @Test
        @DisplayName("rejected回调正确将Submitted(1)->Rejected(3)")
        void shouldRejectCallbackSuccessfully() {
            DocumentStatusEntity docStatus = new DocumentStatusEntity();
            docStatus.setStatus(1);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(docStatus);
            when(documentStatusMapper.updateStatus(DOC_TYPE, DOC_ID, 3))
                    .thenReturn(1);

            callback.rejected(DOC_TYPE, DOC_ID, "不符合要求");

            verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 3);
            verify(auditLogMapper).insert(any(SysAuditLogEntity.class));
        }

        @Test
        @DisplayName("重复回调幂等处理（非Submitted状态忽略）")
        void shouldIgnoreDuplicateCallback() {
            DocumentStatusEntity docStatus = new DocumentStatusEntity();
            docStatus.setStatus(2); // already approved
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(docStatus);

            callback.approved(DOC_TYPE, DOC_ID, "重复回调");

            verify(documentStatusMapper, never()).updateStatus(anyString(), anyLong(), anyInt());
            verify(auditLogMapper, never()).insert(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("单据不存在时回调忽略")
        void shouldIgnoreCallbackWhenDocNotFound() {
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(null);

            callback.approved(DOC_TYPE, DOC_ID, "同意");

            verify(documentStatusMapper, never()).updateStatus(anyString(), anyLong(), anyInt());
        }

        @Test
        @DisplayName("驳回回调幂等处理（非Submitted状态忽略）")
        void shouldIgnoreDuplicateRejectCallback() {
            DocumentStatusEntity docStatus = new DocumentStatusEntity();
            docStatus.setStatus(3); // already rejected
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(docStatus);

            callback.rejected(DOC_TYPE, DOC_ID, "重复驳回");

            verify(documentStatusMapper, never()).updateStatus(anyString(), anyLong(), anyInt());
            verify(auditLogMapper, never()).insert(any());
        }
    }
}
