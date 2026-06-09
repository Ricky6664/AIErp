package com.erp.flow.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.flow.dto.BizDocRelationDTO;
import com.erp.flow.entity.DocRelationEntity;
import com.erp.flow.mapper.DocRelationMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BizDocPushService 验证测试 —— 覆盖下推/回滚/查询/目标类型获取全场景.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BizDocPushService - 通用单据下推服务验证")
class BizDocPushServiceVerificationTest {

    @Mock
    private DocRelationMapper docRelationMapper;

    private BizDocRelationCoreService relationCoreService;
    private BizDocPushService pushService;
    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
        relationCoreService = new BizDocRelationCoreService(docRelationMapper);
        pushService = new BizDocPushService(relationCoreService, docRelationMapper);
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    // ========== 正常流程 ==========

    @Nested
    @DisplayName("正常流程-下推")
    class PushDocument {

        @Test
        @DisplayName("完整push DTO下推成功, 返回关联关系ID")
        void shouldPushDocumentAndReturnRelationId() {
            BizDocRelationDTO dto = buildPushDTO();

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(500L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long relationId = pushService.pushDocument(dto);

            assertEquals(500L, relationId);
            ArgumentCaptor<DocRelationEntity> captor = ArgumentCaptor.forClass(DocRelationEntity.class);
            verify(docRelationMapper).insert(captor.capture());
            DocRelationEntity saved = captor.getValue();
            assertEquals("sale_order", saved.getSourceDocType());
            assertEquals("push", saved.getRelationType());
            assertEquals(new BigDecimal("5.000000"), saved.getRelationQty());
        }

        @Test
        @DisplayName("源单无已推记录时直接下推成功")
        void shouldPushWhenNoExistingPushRelations() {
            BizDocRelationDTO dto = buildPushDTO();
            when(docRelationMapper.selectList(any())).thenReturn(Collections.emptyList());

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(600L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long relationId = pushService.pushDocument(dto);

            assertEquals(600L, relationId);
            verify(docRelationMapper, times(1)).selectList(any());
            verify(docRelationMapper).insert(any(DocRelationEntity.class));
        }

        @Test
        @DisplayName("源单有已推记录时累计校验通过")
        void shouldPushWhenExistingPushRelationsExist() {
            BizDocRelationDTO dto = buildPushDTO();
            DocRelationEntity existingRelation = buildEntityFromDTO(buildPushDTO());
            existingRelation.setId(1L);
            existingRelation.setRelationQty(new BigDecimal("3.000000"));
            when(docRelationMapper.selectList(any()))
                    .thenReturn(Collections.singletonList(existingRelation));

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(700L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long relationId = pushService.pushDocument(dto);

            assertEquals(700L, relationId);
        }
    }

    @Nested
    @DisplayName("正常流程-回滚")
    class RollbackPush {

        @Test
        @DisplayName("有效push关联关系回滚成功")
        void shouldRollbackPushSuccessfully() {
            DocRelationEntity relation = buildEntityFromDTO(buildPushDTO());
            relation.setId(10L);
            relation.setRelationType("push");
            when(docRelationMapper.selectById(10L)).thenReturn(relation);
            when(docRelationMapper.deleteById(10L)).thenReturn(1);

            assertDoesNotThrow(() -> pushService.rollbackPush(10L));

            verify(docRelationMapper, times(2)).selectById(10L);
            verify(docRelationMapper).deleteById(10L);
        }
    }

    @Nested
    @DisplayName("正常流程-查询已推关联")
    class QueryPushRelations {

        @Test
        @DisplayName("查询已下推关联列表返回正确数据")
        void shouldReturnPushRelations() {
            DocRelationEntity r1 = buildEntityFromDTO(buildPushDTO());
            r1.setId(1L);
            r1.setRelationType("push");
            DocRelationEntity r2 = buildEntityFromDTO(buildPushDTO());
            r2.setId(2L);
            r2.setRelationType("push");
            r2.setTargetDocType("delivery_order");
            when(docRelationMapper.selectList(any()))
                    .thenReturn(Arrays.asList(r1, r2));

            var result = pushService.queryPushRelations("sale_order", 3L);

            assertNotNull(result);
            assertEquals(2, result.size());
            result.forEach(r -> assertEquals("push", r.getRelationType()));
        }

        @Test
        @DisplayName("查询无下推记录时返回空列表")
        void shouldReturnEmptyWhenNoPushRelations() {
            when(docRelationMapper.selectList(any()))
                    .thenReturn(Collections.emptyList());

            var result = pushService.queryPushRelations("unknown_type", 999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ========== 异常场景测试 ==========

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditions {

        @Test
        @DisplayName("非push类型下推时抛BusinessException")
        void shouldRejectNonPushRelationType() {
            BizDocRelationDTO dto = buildPushDTO();
            dto.setRelationType("import");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> pushService.pushDocument(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(docRelationMapper, never()).insert(any());
        }

        @Test
        @DisplayName("下推数量为0时抛BusinessException")
        void shouldRejectZeroPushQty() {
            BizDocRelationDTO dto = buildPushDTO();
            dto.setRelationQty(BigDecimal.ZERO);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> pushService.pushDocument(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("下推数量为负数时抛BusinessException")
        void shouldRejectNegativePushQty() {
            BizDocRelationDTO dto = buildPushDTO();
            dto.setRelationQty(new BigDecimal("-5.000000"));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> pushService.pushDocument(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }
    }

    // ========== 异常场景测试 ==========

    @Nested
    @DisplayName("异常场景测试")
    class ExceptionScenarios {

        @Test
        @DisplayName("回滚不存在的关联关系抛DATA_NOT_FOUND")
        void shouldThrowNotFoundOnRollbackNonexistent() {
            when(docRelationMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> pushService.rollbackPush(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(docRelationMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("回滚非push类型关联关系抛DATA_STATUS_INVALID")
        void shouldRejectRollbackNonPushRelation() {
            DocRelationEntity relation = buildEntityFromDTO(buildPushDTO());
            relation.setId(10L);
            relation.setRelationType("import");
            when(docRelationMapper.selectById(10L)).thenReturn(relation);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> pushService.rollbackPush(10L));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(docRelationMapper, never()).deleteById(any());
        }
    }

    // ========== 辅助方法 ==========

    private BizDocRelationDTO buildPushDTO() {
        BizDocRelationDTO dto = new BizDocRelationDTO();
        dto.setSourceDocType("sale_order");
        dto.setSourceDocId(3L);
        dto.setSourceDetailId(30L);
        dto.setTargetDocType("delivery_order");
        dto.setTargetDocId(4L);
        dto.setTargetDetailId(40L);
        dto.setRelationType("push");
        dto.setRelationQty(new BigDecimal("5.000000"));
        return dto;
    }

    private DocRelationEntity buildEntityFromDTO(BizDocRelationDTO dto) {
        DocRelationEntity entity = new DocRelationEntity();
        entity.setSourceDocType(dto.getSourceDocType());
        entity.setSourceDocId(dto.getSourceDocId());
        entity.setSourceDetailId(dto.getSourceDetailId());
        entity.setTargetDocType(dto.getTargetDocType());
        entity.setTargetDocId(dto.getTargetDocId());
        entity.setTargetDetailId(dto.getTargetDetailId());
        entity.setRelationType(dto.getRelationType());
        entity.setRelationQty(dto.getRelationQty());
        return entity;
    }
}
