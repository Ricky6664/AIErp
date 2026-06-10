package com.erp.approval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.approval.dto.DefinitionCreateDTO;
import com.erp.approval.dto.DefinitionQueryDTO;
import com.erp.approval.dto.DefinitionUpdateDTO;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.service.impl.ApprovalDefinitionServiceImpl;
import com.erp.approval.vo.DefinitionDetailVO;
import com.erp.approval.vo.DefinitionListVO;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ApprovalDefinitionService 单元测试")
class ApprovalDefinitionServiceTest {

    @Mock
    private ApprovalDefinitionMapper mapper;

    private ApprovalDefinitionServiceImpl service;

    private ApprovalDefinitionEntity entity;
    private DefinitionCreateDTO createDTO;
    private DefinitionUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        service = spy(new ApprovalDefinitionServiceImpl());
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        entity = new ApprovalDefinitionEntity();
        entity.setId(1L);
        entity.setDefinitionName("测试审批流程");
        entity.setDefinitionCode("TEST_CODE_001");
        entity.setBusinessType("LEAVE");
        entity.setFlowConfig("{\"nodes\":[]}");
        entity.setEnableFlag(true);
        entity.setVersion(0);

        createDTO = new DefinitionCreateDTO();
        createDTO.setDefinitionName("测试审批流程");
        createDTO.setDefinitionCode("TEST_CODE_001");
        createDTO.setBusinessType("LEAVE");
        createDTO.setFlowConfig("{\"nodes\":[]}");
        createDTO.setEnableFlag(true);

        updateDTO = new DefinitionUpdateDTO();
        updateDTO.setDefinitionName("更新后的审批流程");
        updateDTO.setBusinessType("OVERTIME");
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create - 新增审批定义")
    class CreateTests {

        @Test
        @DisplayName("完整DTO → 返回新建ID，事务提交")
        void shouldReturnNewIdWhenValidDto() {
            when(mapper.selectCount(any())).thenReturn(0L);
            doAnswer(inv -> {
                ApprovalDefinitionEntity e = inv.getArgument(0);
                e.setId(100L);
                return 1;
            }).when(mapper).insert(any(ApprovalDefinitionEntity.class));

            Long id = service.create(createDTO);

            assertNotNull(id);
            assertEquals(100L, id);
            verify(mapper).insert(any(ApprovalDefinitionEntity.class));
        }

        @Test
        @DisplayName("enableFlag为空 → 默认设为true")
        void shouldDefaultEnableFlagToTrueWhenNull() {
            createDTO.setEnableFlag(null);
            when(mapper.selectCount(any())).thenReturn(0L);
            doAnswer(inv -> {
                ApprovalDefinitionEntity e = inv.getArgument(0);
                e.setId(100L);
                return 1;
            }).when(mapper).insert(any(ApprovalDefinitionEntity.class));

            service.create(createDTO);

            verify(mapper).insert(argThat(e -> Boolean.TRUE.equals(e.getEnableFlag())));
        }

        @Test
        @DisplayName("编码重复 → 抛出BusinessException(DATA_ALREADY_EXISTS)")
        void shouldThrowWhenCodeDuplicate() {
            when(mapper.selectCount(any())).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(mapper, never()).insert(any());
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update - 修改审批定义")
    class UpdateTests {

        @Test
        @DisplayName("ID存在 → 数据更新")
        void shouldUpdateWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any())).thenReturn(1);

            service.update(1L, updateDTO);

            verify(mapper).updateById(argThat(e ->
                    "更新后的审批流程".equals(e.getDefinitionName())
                            && "OVERTIME".equals(e.getBusinessType())));
        }

        @Test
        @DisplayName("ID不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenEntityNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.update(999L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(mapper, never()).updateById(any());
        }

        @Test
        @DisplayName("并发冲突 → 乐观锁异常传播（事务回滚）")
        void shouldPropagateOptimisticLockFailure() {
            entity.setVersion(1);
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any()))
                    .thenThrow(new RuntimeException("Optimistic lock conflict"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.update(1L, updateDTO));
            assertTrue(ex.getMessage().contains("Optimistic lock"));
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete - 删除审批定义")
    class DeleteTests {

        @Test
        @DisplayName("ID存在 → 逻辑删除成功")
        void shouldDeleteWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            // Stub removeById to bypass MyBatis-Plus TableInfo requirement in unit test
            doReturn(true).when(service).removeById(1L);

            assertDoesNotThrow(() -> service.delete(1L));
            verify(mapper).selectById(1L);
        }

        @Test
        @DisplayName("ID不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenEntityNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.delete(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(mapper).selectById(999L);
        }

        @Test
        @DisplayName("有下游关联数据 → 外键异常传播（事务回滚）")
        void shouldPropagateForeignKeyViolation() {
            when(mapper.selectById(1L)).thenReturn(entity);
            doThrow(new RuntimeException("Foreign key constraint violation"))
                    .when(service).removeById(1L);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.delete(1L));
            assertTrue(ex.getMessage().contains("Foreign key"));
        }
    }

    // ==================== pageList ====================

    @Nested
    @DisplayName("pageList - 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无条件查询 → 返回分页数据")
        void shouldReturnPageData() {
            IPage<ApprovalDefinitionEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            DefinitionQueryDTO query = new DefinitionQueryDTO();
            PageResult<DefinitionListVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals("测试审批流程", result.getList().get(0).getDefinitionName());
        }

        @Test
        @DisplayName("按名称模糊查询 → 条件生效")
        void shouldFilterByName() {
            IPage<ApprovalDefinitionEntity> page = new Page<>(1, 10, 0);
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            DefinitionQueryDTO query = new DefinitionQueryDTO();
            query.setDefinitionName("测试");
            PageResult<DefinitionListVO> result = service.pageList(query);

            assertNotNull(result);
            verify(mapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("pageNum/pageSize为空 → 使用默认值(1/10)")
        void shouldUseDefaultPagination() {
            IPage<ApprovalDefinitionEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            DefinitionQueryDTO query = new DefinitionQueryDTO();
            service.pageList(query);

            verify(mapper).selectPage(argThat(p ->
                    p.getCurrent() == 1 && p.getSize() == 10), any());
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById - 查询详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 → 返回DefinitionDetailVO")
        void shouldReturnDetailWhenExists() {
            when(mapper.selectById(1L)).thenReturn(entity);

            DefinitionDetailVO vo = service.getById(1L);

            assertNotNull(vo);
            assertEquals("测试审批流程", vo.getDefinitionName());
            assertEquals("TEST_CODE_001", vo.getDefinitionCode());
        }

        @Test
        @DisplayName("ID不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.getById(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }
}
