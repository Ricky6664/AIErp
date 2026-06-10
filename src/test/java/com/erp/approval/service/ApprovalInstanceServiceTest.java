package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.approval.dto.InstanceCreateDTO;
import com.erp.approval.dto.InstanceQueryDTO;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.service.impl.ApprovalInstanceServiceImpl;
import com.erp.approval.vo.InstanceVO;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ApprovalInstanceService 单元测试")
class ApprovalInstanceServiceTest {

    @Mock
    private ApprovalInstanceMapper instanceMapper;

    @Mock
    private ApprovalDefinitionMapper definitionMapper;

    private ApprovalInstanceServiceImpl service;

    private ApprovalInstanceEntity entity;
    private ApprovalDefinitionEntity definition;
    private InstanceCreateDTO createDTO;
    private MockedStatic<StpUtil> stpMock;

    @BeforeEach
    void setUp() {
        service = spy(new ApprovalInstanceServiceImpl(definitionMapper));
        ReflectionTestUtils.setField(service, "baseMapper", instanceMapper);

        stpMock = mockStatic(StpUtil.class);
        stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

        entity = new ApprovalInstanceEntity();
        entity.setId(1L);
        entity.setDefinitionId(10L);
        entity.setBusinessType("LEAVE");
        entity.setBusinessId(100L);
        entity.setApplicantId(1L);
        entity.setCurrentNodeId(5L);
        entity.setStatus("PENDING");
        entity.setVersion(0);

        definition = new ApprovalDefinitionEntity();
        definition.setId(10L);
        definition.setDefinitionName("请假审批");
        definition.setBusinessType("LEAVE");
        definition.setEnableFlag(true);

        createDTO = new InstanceCreateDTO();
        createDTO.setDefinitionId(10L);
        createDTO.setBusinessType("LEAVE");
        createDTO.setBusinessId(100L);
    }

    @AfterEach
    void tearDown() {
        stpMock.close();
    }

    // ==================== submit ====================

    @Nested
    @DisplayName("submit - 提交审批实例")
    class SubmitTests {

        @Test
        @DisplayName("完整DTO → 返回新建ID")
        void shouldReturnNewIdWhenValidDto() {
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(instanceMapper.selectCount(any())).thenReturn(0L);
            doAnswer(inv -> {
                ApprovalInstanceEntity e = inv.getArgument(0);
                e.setId(200L);
                return 1;
            }).when(instanceMapper).insert(any(ApprovalInstanceEntity.class));

            Long id = service.submit(createDTO);

            assertNotNull(id);
            assertEquals(200L, id);
            verify(instanceMapper).insert(any(ApprovalInstanceEntity.class));
        }

        @Test
        @DisplayName("审批定义不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenDefinitionNotFound() {
            when(definitionMapper.selectById(10L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.submit(createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(instanceMapper, never()).insert(any());
        }

        @Test
        @DisplayName("审批定义已停用 → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenDefinitionDisabled() {
            definition.setEnableFlag(false);
            when(definitionMapper.selectById(10L)).thenReturn(definition);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.submit(createDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(instanceMapper, never()).insert(any());
        }

        @Test
        @DisplayName("同一业务已有审批中的实例 → 抛出BusinessException(DATA_ALREADY_EXISTS)")
        void shouldThrowWhenDuplicatePending() {
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(instanceMapper.selectCount(any())).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.submit(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(instanceMapper, never()).insert(any());
        }

        @Test
        @DisplayName("新增实例时状态设为PENDING")
        void shouldSetStatusToPending() {
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(instanceMapper.selectCount(any())).thenReturn(0L);
            doAnswer(inv -> {
                ApprovalInstanceEntity e = inv.getArgument(0);
                e.setId(200L);
                return 1;
            }).when(instanceMapper).insert(any(ApprovalInstanceEntity.class));

            service.submit(createDTO);

            verify(instanceMapper).insert(argThat(e ->
                    "PENDING".equals(e.getStatus())));
        }
    }

    // ==================== pageList ====================

    @Nested
    @DisplayName("pageList - 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无条件查询 → 返回分页数据")
        void shouldReturnPageData() {
            IPage<ApprovalInstanceEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            InstanceQueryDTO query = new InstanceQueryDTO();
            PageResult<InstanceVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("按状态过滤 → 条件生效")
        void shouldFilterByStatus() {
            IPage<ApprovalInstanceEntity> page = new Page<>(1, 10, 0);
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            InstanceQueryDTO query = new InstanceQueryDTO();
            query.setStatus("APPROVED");
            service.pageList(query);

            verify(instanceMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("pageNum/pageSize为空 → 使用默认值(1/10)")
        void shouldUseDefaultPagination() {
            IPage<ApprovalInstanceEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            InstanceQueryDTO query = new InstanceQueryDTO();
            service.pageList(query);

            verify(instanceMapper).selectPage(argThat(p ->
                    p.getCurrent() == 1 && p.getSize() == 10), any());
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById - 查询详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 → 返回InstanceVO")
        void shouldReturnVOWhenExists() {
            when(instanceMapper.selectById(1L)).thenReturn(entity);

            InstanceVO vo = service.getById(1L);

            assertNotNull(vo);
        }

        @Test
        @DisplayName("ID不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenNotFound() {
            when(instanceMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.getById(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== withdraw ====================

    @Nested
    @DisplayName("withdraw - 撤回审批")
    class WithdrawTests {

        @Test
        @DisplayName("待审批状态且本人提交 → 撤回成功，状态变更为WITHDRAWN")
        void shouldWithdrawWhenPendingAndOwn() {
            when(instanceMapper.selectById(1L)).thenReturn(entity);
            when(instanceMapper.updateById(any())).thenReturn(1);

            service.withdraw(1L);

            verify(instanceMapper).updateById(argThat(e ->
                    "WITHDRAWN".equals(e.getStatus())));
        }

        @Test
        @DisplayName("实例不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenInstanceNotFound() {
            when(instanceMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.withdraw(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(instanceMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("状态不是PENDING → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenNotPending() {
            entity.setStatus("APPROVED");
            when(instanceMapper.selectById(1L)).thenReturn(entity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.withdraw(1L));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(instanceMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("非本人提交 → 抛出BusinessException(BUSINESS_ERROR)")
        void shouldThrowWhenNotOwnApplicant() {
            entity.setApplicantId(2L);
            when(instanceMapper.selectById(1L)).thenReturn(entity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.withdraw(1L));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(instanceMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("并发修改冲突 → 乐观锁异常传播")
        void shouldPropagateOptimisticLockFailure() {
            entity.setVersion(1);
            when(instanceMapper.selectById(1L)).thenReturn(entity);
            when(instanceMapper.updateById(any()))
                    .thenThrow(new RuntimeException("Optimistic lock conflict"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.withdraw(1L));
            assertTrue(ex.getMessage().contains("Optimistic lock"));
        }
    }

    // ==================== 边界条件 ====================

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("submit DTO definitionId为null → 参数校验失败（Bean Validation层）")
        void shouldRejectNullDefinitionId() {
            createDTO.setDefinitionId(null);
            // Bean Validation 由 Controller 层触发，Service 层直接接收
            // 此处验证 Service 会正常传递 null 到 mapper
            when(definitionMapper.selectById(null)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.submit(createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("查询不存在的ID → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldReturnErrorForNonExistentId() {
            when(instanceMapper.selectById(Long.MAX_VALUE)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.getById(Long.MAX_VALUE));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("pageList空结果 → 返回空列表非null")
        void shouldReturnEmptyListWhenNoData() {
            IPage<ApprovalInstanceEntity> page = new Page<>(1, 10, 0);
            page.setRecords(java.util.Collections.emptyList());
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            InstanceQueryDTO query = new InstanceQueryDTO();
            PageResult<InstanceVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }
    }
}
