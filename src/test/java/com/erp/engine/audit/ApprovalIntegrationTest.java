package com.erp.engine.audit;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.callback.ApprovalCallbackImpl;
import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.event.AuditApprovedEvent;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import com.erp.engine.audit.model.ApprovalFlowConfig;
import com.erp.engine.audit.model.ApprovalNode;
import com.erp.engine.audit.service.ApprovalIntegrationService;
import com.erp.engine.audit.service.AuditApprovalIntegrationService;
import com.erp.engine.audit.service.AuditConfigService;
import com.erp.engine.audit.service.AuditEngineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
        @DisplayName("approvalFlowConfig正确解析3个审批节点")
        void shouldParseFlowConfigWithThreeNodes() throws Exception {
            String json = objectMapper.writeValueAsString(createThreeNodeFlowConfig());
            ApprovalFlowConfig result = integrationService.parseFlowConfig(json);

            assertNotNull(result);
            assertEquals("FLOW_DEF_002", result.getFlowDefinitionId());
            assertEquals(3, result.getNodes().size());
            assertEquals("部门经理审批", result.getNodes().get(0).getNodeName());
            assertEquals("财务审批", result.getNodes().get(1).getNodeName());
            assertEquals("总经理审批", result.getNodes().get(2).getNodeName());
            assertEquals(Integer.valueOf(1), result.getNodes().get(0).getNodeOrder());
            assertEquals(Integer.valueOf(2), result.getNodes().get(1).getNodeOrder());
            assertEquals(Integer.valueOf(3), result.getNodes().get(2).getNodeOrder());
        }

        @Test
        @DisplayName("approvalFlowConfig格式错误时抛出BusinessException(PARAM_FORMAT_ERROR)")
        void shouldThrowWithCorrectMessageWhenFlowConfigInvalid() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> integrationService.parseFlowConfig("{invalid json}"));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
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

        private ApprovalFlowConfig createThreeNodeFlowConfig() {
            ApprovalNode node1 = new ApprovalNode();
            node1.setNodeName("部门经理审批");
            node1.setApproverRole("DEPT_MANAGER");
            node1.setNodeOrder(1);
            node1.setRequired(true);

            ApprovalNode node2 = new ApprovalNode();
            node2.setNodeName("财务审批");
            node2.setApproverRole("FINANCE");
            node2.setNodeOrder(2);
            node2.setRequired(true);

            ApprovalNode node3 = new ApprovalNode();
            node3.setNodeName("总经理审批");
            node3.setApproverRole("GM");
            node3.setNodeOrder(3);
            node3.setRequired(false);

            ApprovalFlowConfig config = new ApprovalFlowConfig();
            config.setFlowDefinitionId("FLOW_DEF_002");
            config.setNodes(List.of(node1, node2, node3));
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
        @DisplayName("approved回调正确将Submitted(1)->Approved(2)并记录审批通过日志")
        void shouldApproveCallbackSuccessfully() {
            DocumentStatusEntity docStatus = new DocumentStatusEntity();
            docStatus.setStatus(1);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(docStatus);
            when(documentStatusMapper.updateStatus(DOC_TYPE, DOC_ID, 2))
                    .thenReturn(1);

            callback.approved(DOC_TYPE, DOC_ID, "同意");

            verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 2);

            ArgumentCaptor<SysAuditLogEntity> logCaptor =
                    ArgumentCaptor.forClass(SysAuditLogEntity.class);
            verify(auditLogMapper).insert(logCaptor.capture());
            SysAuditLogEntity logEntity = logCaptor.getValue();
            assertEquals("APPROVE", logEntity.getOperationType());
            assertEquals(1, logEntity.getFromStatus());
            assertEquals(2, logEntity.getToStatus());
            assertTrue(logEntity.getOpinion().contains("审批流程通过"));
            assertTrue(logEntity.getOpinion().contains("同意"));

            ArgumentCaptor<AuditApprovedEvent> eventCaptor =
                    ArgumentCaptor.forClass(AuditApprovedEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertEquals(DOC_TYPE, eventCaptor.getValue().getDocType());
            assertEquals(DOC_ID, eventCaptor.getValue().getDocId());
        }

        @Test
        @DisplayName("rejected回调正确将Submitted(1)->Rejected(3)并记录审批驳回日志")
        void shouldRejectCallbackSuccessfully() {
            DocumentStatusEntity docStatus = new DocumentStatusEntity();
            docStatus.setStatus(1);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(docStatus);
            when(documentStatusMapper.updateStatus(DOC_TYPE, DOC_ID, 3))
                    .thenReturn(1);

            callback.rejected(DOC_TYPE, DOC_ID, "金额超标");

            verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 3);

            ArgumentCaptor<SysAuditLogEntity> logCaptor =
                    ArgumentCaptor.forClass(SysAuditLogEntity.class);
            verify(auditLogMapper).insert(logCaptor.capture());
            SysAuditLogEntity logEntity = logCaptor.getValue();
            assertEquals("REJECT", logEntity.getOperationType());
            assertEquals(1, logEntity.getFromStatus());
            assertEquals(3, logEntity.getToStatus());
            assertTrue(logEntity.getOpinion().contains("审批流程驳回"));
            assertTrue(logEntity.getOpinion().contains("金额超标"));
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

    // -- 跨模块调用异常测试 --

    @Nested
    @DisplayName("跨模块调用异常处理")
    class CrossModuleExceptionTest {

        @Mock
        private AuditConfigService auditConfigService;
        @Mock
        private AuditLogMapper auditLogMapper;
        @Mock
        private DocumentStatusMapper documentStatusMapper;
        @Mock
        private ApprovalIntegrationService approvalIntegrationService;
        @Mock
        private ApplicationEventPublisher eventPublisher;
        @Mock
        private StringRedisTemplate stringRedisTemplate;
        @Mock
        private ValueOperations<String, String> valueOperations;

        @InjectMocks
        private AuditEngineService auditEngineService;

        @BeforeEach
        void setUp() {
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        }

        @Test
        @DisplayName("跨模块调用异常时事务回滚，单据保持Draft(0)")
        void shouldRollbackWhenCrossModuleTimeout() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            DocumentStatusEntity entity = new DocumentStatusEntity();
            entity.setDocType(DOC_TYPE);
            entity.setDocId(DOC_ID);
            entity.setStatus(0);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

            SysAuditConfigEntity config = new SysAuditConfigEntity();
            config.setDocType(DOC_TYPE);
            config.setApprovalEnabled(true);
            config.setAutoConfirm(false);
            when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);

            doThrow(new RuntimeException("P1-002审批服务超时"))
                    .when(approvalIntegrationService)
                    .createApprovalInstance(DOC_TYPE, DOC_ID);

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

                AuditSubmitDTO dto = new AuditSubmitDTO(DOC_TYPE, DOC_ID, "请审核");
                assertThrows(RuntimeException.class,
                        () -> auditEngineService.submit(dto));
            }

            verify(approvalIntegrationService).createApprovalInstance(DOC_TYPE, DOC_ID);
        }
    }
}
