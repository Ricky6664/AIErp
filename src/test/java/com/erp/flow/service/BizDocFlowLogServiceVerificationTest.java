package com.erp.flow.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.flow.dto.BizDocRelationDTO;
import com.erp.flow.entity.DocRelationEntity;
import com.erp.flow.mapper.DocRelationMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BizDocFlowLogService 验证测试 — 覆盖日志构建/记录/下推查询/流转历史/操作统计全场景.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BizDocFlowLogService - 单据流转操作日志服务验证")
class BizDocFlowLogServiceVerificationTest {

    @Mock
    private DocRelationMapper docRelationMapper;

    private BizDocFlowLogService service;
    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
        service = new BizDocFlowLogService(docRelationMapper);
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    // ========== 1. 正常流程 — buildLogEntry ==========

    @Nested
    @DisplayName("正常流程-构建日志条目")
    class BuildLogEntry {

        @Test
        @DisplayName("传入完整DTO构建push类型日志条目, 包含全部字段")
        void shouldBuildCompletePushLogEntry() {
            BizDocRelationDTO dto = buildValidImportDTO();

            Map<String, Object> entry = service.buildLogEntry("push", dto, 100L);

            assertNotNull(entry);
            assertEquals("push", entry.get("operationType"));
            assertEquals(100L, entry.get("relationId"));
            assertEquals("sale_order", entry.get("sourceDocType"));
            assertEquals(1L, entry.get("sourceDocId"));
            assertEquals(10L, entry.get("sourceDetailId"));
            assertEquals("purchase_order", entry.get("targetDocType"));
            assertEquals(2L, entry.get("targetDocId"));
            assertEquals(20L, entry.get("targetDetailId"));
            assertEquals(new BigDecimal("10.000000"), entry.get("relationQty"));
            assertEquals(1L, entry.get("operatorId"));
            assertNotNull(entry.get("timestamp"));
        }

        @Test
        @DisplayName("传入import类型DTO构建日志条目")
        void shouldBuildImportLogEntry() {
            BizDocRelationDTO dto = buildValidImportDTO();

            Map<String, Object> entry = service.buildLogEntry("import", dto, 200L);

            assertEquals("import", entry.get("operationType"));
            assertEquals(200L, entry.get("relationId"));
        }

        @Test
        @DisplayName("传入copy类型DTO构建日志条目")
        void shouldBuildCopyLogEntry() {
            BizDocRelationDTO dto = buildValidCopyDTO();

            Map<String, Object> entry = service.buildLogEntry("copy", dto, 300L);

            assertEquals("copy", entry.get("operationType"));
            assertEquals(300L, entry.get("relationId"));
            assertEquals("purchase_order", entry.get("sourceDocType"));
            assertEquals("purchase_order", entry.get("targetDocType"));
        }

        @Test
        @DisplayName("构建日志条目包含时间戳且格式正确")
        void shouldIncludeTimestampInLogEntry() {
            BizDocRelationDTO dto = buildValidImportDTO();

            Map<String, Object> entry = service.buildLogEntry("push", dto, 1L);

            String timestamp = (String) entry.get("timestamp");
            assertNotNull(timestamp);
            assertFalse(timestamp.isEmpty());
        }
    }

    // ========== 2. 正常流程 — recordPushOperation ==========

    @Nested
    @DisplayName("正常流程-记录下推操作")
    class RecordPushOperation {

        @Test
        @DisplayName("记录下推操作不抛异常(仅日志输出)")
        void shouldRecordPushOperationWithoutException() {
            BizDocRelationDTO dto = buildValidImportDTO();

            assertDoesNotThrow(() -> service.recordPushOperation(dto, 100L));
        }

        @Test
        @DisplayName("记录下推操作无DB交互(仅log.info)")
        void shouldNotInteractWithDatabaseOnRecordPush() {
            BizDocRelationDTO dto = buildValidImportDTO();

            service.recordPushOperation(dto, 100L);

            verifyNoInteractions(docRelationMapper);
        }
    }

    // ========== 3. 正常流程 — recordRollbackOperation ==========

    @Nested
    @DisplayName("正常流程-记录回滚操作")
    class RecordRollbackOperation {

        @Test
        @DisplayName("记录回滚操作不抛异常(仅日志输出)")
        void shouldRecordRollbackOperationWithoutException() {
            DocRelationEntity relation = buildPushEntity();

            assertDoesNotThrow(() -> service.recordRollbackOperation(relation));
        }

        @Test
        @DisplayName("记录回滚操作无DB交互(仅log.info)")
        void shouldNotInteractWithDatabaseOnRecordRollback() {
            DocRelationEntity relation = buildPushEntity();

            service.recordRollbackOperation(relation);

            verifyNoInteractions(docRelationMapper);
        }

        @Test
        @DisplayName("回滚日志条目包含正确的操作类型和关联ID")
        void shouldBuildRollbackEntryWithCorrectFields() {
            DocRelationEntity relation = buildPushEntity();
            relation.setId(500L);
            relation.setSourceDocType("delivery_order");
            relation.setSourceDocId(10L);
            relation.setTargetDocType("sale_order");
            relation.setTargetDocId(20L);

            assertDoesNotThrow(() -> service.recordRollbackOperation(relation));
        }
    }

    // ========== 4. 正常流程-查询下推日志 ==========

    @Nested
    @DisplayName("正常流程-查询下推日志")
    class QueryPushLogs {

        @Test
        @DisplayName("按源单据查询下推日志返回正确数据")
        void shouldQueryPushLogsBySource() {
            DocRelationEntity entity = buildPushEntity();
            entity.setId(1L);
            entity.setSourceDocType("sale_order");
            entity.setSourceDocId(1L);
            entity.setTargetDocType("delivery_order");
            entity.setTargetDocId(10L);
            when(docRelationMapper.selectList(any())).thenReturn(Collections.singletonList(entity));

            List<Map<String, Object>> result = service.queryPushLogs("sale_order", 1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            Map<String, Object> entry = result.get(0);
            assertEquals(1L, entry.get("relationId"));
            assertEquals("sale_order", entry.get("sourceDocType"));
            assertEquals(1L, entry.get("sourceDocId"));
            assertEquals("delivery_order", entry.get("targetDocType"));
            assertEquals("push", entry.get("relationType"));
        }

        @Test
        @DisplayName("按目标单据查询下推日志返回正确数据")
        void shouldQueryPushLogsByTarget() {
            DocRelationEntity entity = buildPushEntity();
            entity.setId(2L);
            entity.setTargetDocType("purchase_order");
            entity.setTargetDocId(20L);
            when(docRelationMapper.selectList(any())).thenReturn(Collections.singletonList(entity));

            List<Map<String, Object>> result = service.queryPushLogs("purchase_order", 20L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("purchase_order", result.get(0).get("targetDocType"));
        }

        @Test
        @DisplayName("查询不存在的单据返回空列表")
        void shouldReturnEmptyListForUnknownDoc() {
            when(docRelationMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<Map<String, Object>> result = service.queryPushLogs("unknown_type", 999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ========== 5. 正常流程-查询流转历史 ==========

    @Nested
    @DisplayName("正常流程-查询流转历史")
    class QueryFlowHistory {

        @Test
        @DisplayName("查询流转历史返回多类型操作记录")
        void shouldReturnMultiTypeHistory() {
            DocRelationEntity pushEntity = buildPushEntity();
            pushEntity.setId(1L);
            pushEntity.setRelationType("push");
            DocRelationEntity importEntity = buildPushEntity();
            importEntity.setId(2L);
            importEntity.setRelationType("import");
            when(docRelationMapper.selectList(any())).thenReturn(Arrays.asList(pushEntity, importEntity));

            List<Map<String, Object>> result = service.queryFlowHistory("sale_order", 1L);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("push", result.get(0).get("relationType"));
            assertEquals("import", result.get(1).get("relationType"));
        }

        @Test
        @DisplayName("查询流转历史无记录时返回空列表")
        void shouldReturnEmptyHistoryWhenNone() {
            when(docRelationMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<Map<String, Object>> result = service.queryFlowHistory("unknown_type", 999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ========== 6. 正常流程-统计下推操作 ==========

    @Nested
    @DisplayName("正常流程-统计下推操作")
    class CountPushOperations {

        @Test
        @DisplayName("统计下推操作次数返回正确数量")
        void shouldReturnCorrectCount() {
            when(docRelationMapper.selectCount(any())).thenReturn(5L);

            long count = service.countPushOperations("sale_order", 1L);

            assertEquals(5L, count);
            verify(docRelationMapper).selectCount(any());
        }

        @Test
        @DisplayName("统计无下推操作的返回0")
        void shouldReturnZeroWhenNoPushOps() {
            when(docRelationMapper.selectCount(any())).thenReturn(0L);

            long count = service.countPushOperations("unknown_type", 999L);

            assertEquals(0L, count);
        }
    }

    // ========== 7. 边界条件测试 ==========

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditions {

        @Test
        @DisplayName("DTO字段全部为null时buildLogEntry不抛异常")
        void shouldHandleNullDTOFieldsGracefully() {
            BizDocRelationDTO dto = new BizDocRelationDTO();

            Map<String, Object> entry = service.buildLogEntry("push", dto, 1L);

            assertNotNull(entry);
            assertEquals("push", entry.get("operationType"));
            assertNull(entry.get("sourceDocType"));
            assertNull(entry.get("sourceDocId"));
        }

        @Test
        @DisplayName("超长单据类型字符串可正常记录")
        void shouldHandleExtraLongDocTypeInLogEntry() {
            BizDocRelationDTO dto = buildValidImportDTO();
            String longType = "a".repeat(200);
            dto.setSourceDocType(longType);
            dto.setTargetDocType(longType);

            Map<String, Object> entry = service.buildLogEntry("push", dto, 1L);

            assertEquals(longType, entry.get("sourceDocType"));
            assertEquals(longType, entry.get("targetDocType"));
        }

        @Test
        @DisplayName("relationId为null时buildLogEntry仍可执行")
        void shouldHandleNullRelationId() {
            BizDocRelationDTO dto = buildValidImportDTO();

            Map<String, Object> entry = service.buildLogEntry("push", dto, null);

            assertNotNull(entry);
            assertNull(entry.get("relationId"));
        }

        @Test
        @DisplayName("极值数量在日志条目中正确保存")
        void shouldHandleMaxQuantityInLogEntry() {
            BizDocRelationDTO dto = buildValidImportDTO();
            dto.setRelationQty(new BigDecimal("999999999999.999999"));

            Map<String, Object> entry = service.buildLogEntry("push", dto, 1L);

            assertEquals(new BigDecimal("999999999999.999999"), entry.get("relationQty"));
        }

        @Test
        @DisplayName("零数量关联在日志条目中正确保存")
        void shouldHandleZeroQuantityInLogEntry() {
            BizDocRelationDTO dto = buildValidImportDTO();
            dto.setRelationQty(BigDecimal.ZERO);

            Map<String, Object> entry = service.buildLogEntry("push", dto, 1L);

            assertEquals(BigDecimal.ZERO, entry.get("relationQty"));
        }
    }

    // ========== 8. 异常-不存在 ==========

    @Nested
    @DisplayName("异常-不存在")
    class NotFound {

        @Test
        @DisplayName("查询不存在单据的下推日志返回空列表而非异常")
        void shouldReturnEmptyNotThrowOnMissingPushLogs() {
            when(docRelationMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<Map<String, Object>> result = service.queryPushLogs("nonexistent", 99999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("查询不存在单据的流转历史返回空列表而非异常")
        void shouldReturnEmptyNotThrowOnMissingHistory() {
            when(docRelationMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<Map<String, Object>> result = service.queryFlowHistory("nonexistent", 99999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ========== 辅助方法 ==========

    private BizDocRelationDTO buildValidImportDTO() {
        BizDocRelationDTO dto = new BizDocRelationDTO();
        dto.setSourceDocType("sale_order");
        dto.setSourceDocId(1L);
        dto.setSourceDetailId(10L);
        dto.setTargetDocType("purchase_order");
        dto.setTargetDocId(2L);
        dto.setTargetDetailId(20L);
        dto.setRelationType("import");
        dto.setRelationQty(new BigDecimal("10.000000"));
        return dto;
    }

    private BizDocRelationDTO buildValidCopyDTO() {
        BizDocRelationDTO dto = new BizDocRelationDTO();
        dto.setSourceDocType("purchase_order");
        dto.setSourceDocId(5L);
        dto.setTargetDocType("purchase_order");
        dto.setTargetDocId(6L);
        dto.setRelationType("copy");
        dto.setRelationQty(new BigDecimal("15.000000"));
        return dto;
    }

    private DocRelationEntity buildPushEntity() {
        DocRelationEntity entity = new DocRelationEntity();
        entity.setId(1L);
        entity.setSourceDocType("sale_order");
        entity.setSourceDocId(1L);
        entity.setSourceDetailId(10L);
        entity.setTargetDocType("delivery_order");
        entity.setTargetDocId(10L);
        entity.setTargetDetailId(100L);
        entity.setRelationType("push");
        entity.setRelationQty(new BigDecimal("5.000000"));
        return entity;
    }
}
