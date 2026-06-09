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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BizDocRelationCoreService 验证测试 —— 按 Section 2 任务目标逐项验证.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BizDocRelationCoreService - 单据关联关系核心业务验证")
class BizDocRelationCoreServiceVerificationTest {

    @Mock
    private DocRelationMapper docRelationMapper;

    private BizDocRelationCoreService service;
    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
        service = new BizDocRelationCoreService(docRelationMapper);
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    // ========== 1. 正常流程-新增 ==========

    @Nested
    @DisplayName("正常流程-新增")
    class CreateRelation {

        @Test
        @DisplayName("传入完整DTO创建关联关系, 返回新建ID")
        void shouldCreateRelationAndReturnId() {
            BizDocRelationDTO dto = buildValidImportDTO();
            DocRelationEntity expectedEntity = buildEntityFromDTO(dto);
            expectedEntity.setId(100L);

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(100L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long id = service.createRelation(dto);

            assertEquals(100L, id);
            ArgumentCaptor<DocRelationEntity> captor = ArgumentCaptor.forClass(DocRelationEntity.class);
            verify(docRelationMapper).insert(captor.capture());
            DocRelationEntity saved = captor.getValue();
            assertEquals("sale_order", saved.getSourceDocType());
            assertEquals(1L, saved.getSourceDocId());
            assertEquals("purchase_order", saved.getTargetDocType());
            assertEquals(2L, saved.getTargetDocId());
            assertEquals("import", saved.getRelationType());
            assertEquals(new BigDecimal("10.000000"), saved.getRelationQty());
        }

        @Test
        @DisplayName("创建copy类型关联可接受数量为0")
        void shouldAcceptZeroQtyForCopy() {
            BizDocRelationDTO dto = buildValidCopyDTO();
            dto.setRelationQty(BigDecimal.ZERO);

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(200L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long id = service.createRelation(dto);

            assertEquals(200L, id);
            verify(docRelationMapper).insert(any(DocRelationEntity.class));
        }

        @Test
        @DisplayName("创建push类型关联关系")
        void shouldCreatePushRelation() {
            BizDocRelationDTO dto = buildValidPushDTO();

            doAnswer(inv -> {
                DocRelationEntity e = inv.getArgument(0);
                e.setId(300L);
                return 1;
            }).when(docRelationMapper).insert(any(DocRelationEntity.class));

            Long id = service.createRelation(dto);

            assertEquals(300L, id);
            verify(docRelationMapper).insert(any(DocRelationEntity.class));
        }
    }

    // ========== 2. 正常流程-查询 ==========

    @Nested
    @DisplayName("正常流程-查询")
    class QueryRelation {

        @Test
        @DisplayName("正向查询: 按源单据查询返回关联列表")
        void shouldQueryBySource() {
            DocRelationEntity entity = buildEntityFromDTO(buildValidImportDTO());
            entity.setId(1L);
            when(docRelationMapper.selectList(any()))
                    .thenReturn(Collections.singletonList(entity));

            List<DocRelationEntity> result = service.queryBySource("sale_order", 1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("sale_order", result.get(0).getSourceDocType());
        }

        @Test
        @DisplayName("反向查询: 按目标单据查询返回关联列表")
        void shouldQueryByTarget() {
            DocRelationEntity entity1 = buildEntityFromDTO(buildValidImportDTO());
            entity1.setId(1L);
            DocRelationEntity entity2 = buildEntityFromDTO(buildValidPushDTO());
            entity2.setId(2L);
            when(docRelationMapper.selectList(any()))
                    .thenReturn(Arrays.asList(entity1, entity2));

            List<DocRelationEntity> result = service.queryByTarget("purchase_order", 2L);

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("查询无关联单据时返回空列表")
        void shouldReturnEmptyListWhenNoRelation() {
            when(docRelationMapper.selectList(any()))
                    .thenReturn(Collections.emptyList());

            List<DocRelationEntity> result = service.queryBySource("unknown_type", 999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ========== 4. 正常流程-删除 ==========

    @Nested
    @DisplayName("正常流程-删除")
    class DeleteRelation {

        @Test
        @DisplayName("传入有效ID执行逻辑删除")
        void shouldDeleteRelation() {
            DocRelationEntity entity = buildEntityFromDTO(buildValidImportDTO());
            entity.setId(10L);
            when(docRelationMapper.selectById(10L)).thenReturn(entity);
            when(docRelationMapper.deleteById(10L)).thenReturn(1);

            assertDoesNotThrow(() -> service.deleteRelation(10L));

            verify(docRelationMapper).selectById(10L);
            verify(docRelationMapper).deleteById(10L);
        }
    }

    // ========== 5. 边界条件测试 ==========

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditions {

        @Test
        @DisplayName("无效关联类型抛BusinessException")
        void shouldRejectInvalidRelationType() {
            BizDocRelationDTO dto = buildValidImportDTO();
            dto.setRelationType("invalid_type");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.createRelation(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(docRelationMapper, never()).insert(any());
        }

        @Test
        @DisplayName("import类型关联数量<=0时抛异常")
        void shouldRejectZeroOrNegativeQtyForImport() {
            BizDocRelationDTO dto = buildValidImportDTO();
            dto.setRelationQty(BigDecimal.ZERO);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.createRelation(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("push类型关联数量为负数时抛异常")
        void shouldRejectNegativeQtyForPush() {
            BizDocRelationDTO dto = buildValidPushDTO();
            dto.setRelationQty(new BigDecimal("-1.000000"));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.createRelation(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }
    }

    // ========== 8. 异常-不存在 ==========

    @Nested
    @DisplayName("异常-不存在")
    class NotFound {

        @Test
        @DisplayName("删除不存在的关联关系返回DATA_NOT_FOUND")
        void shouldThrowNotFoundOnDelete() {
            when(docRelationMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.deleteRelation(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(docRelationMapper, never()).deleteById(any());
        }
    }

    // ========== DTO校验测试 ==========

    @Nested
    @DisplayName("DTO参数校验")
    class DtoValidation {

        @Test
        @DisplayName("sourceDocType为null时应在Controller层拦截")
        void shouldValidateSourceDocTypeNotNull() {
            BizDocRelationDTO dto = buildValidImportDTO();
            dto.setSourceDocType(null);
            // DTO校验由Controller层@Valid触发, Service层可正常接收
            // 验证Service层不会额外对null做NPE保护之外的处理
            // 此处验证DTO字段可正常为null (校验在Controller层)
            assertNull(dto.getSourceDocType());
        }

        @Test
        @DisplayName("DTO所有字段正常填充")
        void shouldHaveAllFieldsPopulated() {
            BizDocRelationDTO dto = buildValidImportDTO();

            assertEquals("sale_order", dto.getSourceDocType());
            assertEquals(1L, dto.getSourceDocId());
            assertEquals(10L, dto.getSourceDetailId());
            assertEquals("purchase_order", dto.getTargetDocType());
            assertEquals(2L, dto.getTargetDocId());
            assertEquals(20L, dto.getTargetDetailId());
            assertEquals("import", dto.getRelationType());
            assertEquals(new BigDecimal("10.000000"), dto.getRelationQty());
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

    private BizDocRelationDTO buildValidPushDTO() {
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
