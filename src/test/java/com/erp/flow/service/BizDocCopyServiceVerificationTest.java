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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BizDocCopyService 验证测试 —— 覆盖复制/回滚/查询/目标类型获取全场景.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BizDocCopyService - 通用单据复制服务验证")
class BizDocCopyServiceVerificationTest {

    @Mock
    private DocRelationMapper docRelationMapper;

    private BizDocRelationCoreService relationCoreService;
    private BizDocCopyService copyService;
    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
        relationCoreService = new BizDocRelationCoreService(docRelationMapper);
        copyService = new BizDocCopyService(relationCoreService, docRelationMapper);
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    // ========== 1. 正常流程-新增 ==========

    @Nested
    @DisplayName("正常流程-新增")
    class CopyDocument {

        @Test
        @DisplayName("传入完整copy DTO, 返回新建关联关系ID")
        void shouldCopyDocumentAndReturnRelationId() {
            BizDocRelationDTO dto = buildCopyDTO();

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(400L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long relationId = copyService.copyDocument(dto);

            assertEquals(400L, relationId);
            ArgumentCaptor<DocRelationEntity> captor = ArgumentCaptor.forClass(DocRelationEntity.class);
            verify(docRelationMapper).insert(captor.capture());
            DocRelationEntity saved = captor.getValue();
            assertEquals("purchase_order", saved.getSourceDocType());
            assertEquals("copy", saved.getRelationType());
            assertEquals(new BigDecimal("15.000000"), saved.getRelationQty());
        }

        @Test
        @DisplayName("copy类型允许关联数量为0")
        void shouldAcceptZeroQtyForCopy() {
            BizDocRelationDTO dto = buildCopyDTO();
            dto.setRelationQty(BigDecimal.ZERO);

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(401L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long relationId = copyService.copyDocument(dto);

            assertEquals(401L, relationId);
            verify(docRelationMapper).insert(any(DocRelationEntity.class));
        }

        @Test
        @DisplayName("带源单明细和目标明细的复制成功")
        void shouldCopyWithDetailIds() {
            BizDocRelationDTO dto = buildCopyDTO();
            dto.setSourceDetailId(100L);
            dto.setTargetDetailId(200L);

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(402L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long relationId = copyService.copyDocument(dto);

            assertEquals(402L, relationId);
            ArgumentCaptor<DocRelationEntity> captor = ArgumentCaptor.forClass(DocRelationEntity.class);
            verify(docRelationMapper).insert(captor.capture());
            assertEquals(100L, captor.getValue().getSourceDetailId());
            assertEquals(200L, captor.getValue().getTargetDetailId());
        }
    }

    // ========== 2. 正常流程-查询 ==========

    @Nested
    @DisplayName("正常流程-查询")
    class QueryCopyRelations {

        @Test
        @DisplayName("按源单据查询已复制关联返回正确数据")
        void shouldReturnCopyRelations() {
            DocRelationEntity r1 = buildEntityFromDTO(buildCopyDTO());
            r1.setId(1L);
            r1.setRelationType("copy");
            DocRelationEntity r2 = buildEntityFromDTO(buildCopyDTO());
            r2.setId(2L);
            r2.setRelationType("copy");
            r2.setTargetDocType("sale_order");
            when(docRelationMapper.selectList(any()))
                    .thenReturn(List.of(r1, r2));

            List<DocRelationEntity> result = copyService.queryCopyRelations("purchase_order", 5L);

            assertNotNull(result);
            assertEquals(2, result.size());
            result.forEach(r -> assertEquals("copy", r.getRelationType()));
        }

        @Test
        @DisplayName("查询无复制记录时返回空列表")
        void shouldReturnEmptyWhenNoCopyRelations() {
            when(docRelationMapper.selectList(any()))
                    .thenReturn(Collections.emptyList());

            List<DocRelationEntity> result = copyService.queryCopyRelations("unknown_type", 999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ========== 3. 正常流程-删除/回滚 ==========

    @Nested
    @DisplayName("正常流程-回滚")
    class RollbackCopy {

        @Test
        @DisplayName("有效copy关联关系回滚成功")
        void shouldRollbackCopySuccessfully() {
            DocRelationEntity relation = buildEntityFromDTO(buildCopyDTO());
            relation.setId(10L);
            relation.setRelationType("copy");
            when(docRelationMapper.selectById(10L)).thenReturn(relation);
            when(docRelationMapper.deleteById(10L)).thenReturn(1);

            assertDoesNotThrow(() -> copyService.rollbackCopy(10L));

            verify(docRelationMapper, times(2)).selectById(10L);
            verify(docRelationMapper).deleteById(10L);
        }
    }

    // ========== 4. 边界条件测试 ==========

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditions {

        @Test
        @DisplayName("非copy类型复制时抛BusinessException")
        void shouldRejectNonCopyRelationType() {
            BizDocRelationDTO dto = buildCopyDTO();
            dto.setRelationType("import");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> copyService.copyDocument(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(docRelationMapper, never()).insert(any());
        }

        @Test
        @DisplayName("push类型复制时抛BusinessException")
        void shouldRejectPushTypeForCopy() {
            BizDocRelationDTO dto = buildCopyDTO();
            dto.setRelationType("push");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> copyService.copyDocument(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(docRelationMapper, never()).insert(any());
        }

        @Test
        @DisplayName("复制回滚非copy类型关联抛DATA_STATUS_INVALID")
        void shouldRejectRollbackNonCopyRelation() {
            DocRelationEntity relation = buildEntityFromDTO(buildCopyDTO());
            relation.setId(10L);
            relation.setRelationType("push");
            when(docRelationMapper.selectById(10L)).thenReturn(relation);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> copyService.rollbackCopy(10L));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(docRelationMapper, never()).deleteById(any());
        }
    }

    // ========== 5. 异常-不存在 ==========

    @Nested
    @DisplayName("异常-不存在")
    class NotFound {

        @Test
        @DisplayName("回滚不存在的关联关系抛DATA_NOT_FOUND")
        void shouldThrowNotFoundOnRollbackNonexistent() {
            when(docRelationMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> copyService.rollbackCopy(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(docRelationMapper, never()).deleteById(any());
        }
    }

    // ========== 辅助方法 ==========

    private BizDocRelationDTO buildCopyDTO() {
        BizDocRelationDTO dto = new BizDocRelationDTO();
        dto.setSourceDocType("purchase_order");
        dto.setSourceDocId(5L);
        dto.setTargetDocType("purchase_order");
        dto.setTargetDocId(6L);
        dto.setRelationType("copy");
        dto.setRelationQty(new BigDecimal("15.000000"));
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
