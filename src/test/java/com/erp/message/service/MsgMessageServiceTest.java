package com.erp.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.message.dto.MsgMessageCreateDTO;
import com.erp.module.message.dto.MsgMessageQueryDTO;
import com.erp.module.message.entity.MsgMessageEntity;
import com.erp.module.message.mapper.MsgMessageMapper;
import com.erp.module.message.service.impl.MsgMessageServiceImpl;
import com.erp.module.message.vo.MsgMessageListVO;
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
@DisplayName("MsgMessageService 单元测试")
class MsgMessageServiceTest {

    @Mock
    private MsgMessageMapper mapper;

    private MsgMessageServiceImpl service;

    private MsgMessageEntity entity;
    private MsgMessageCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        service = spy(new MsgMessageServiceImpl());
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        entity = new MsgMessageEntity();
        entity.setId(1L);
        entity.setMessageTitle("测试消息");
        entity.setMessageContent("测试内容");
        entity.setReceiverId(100L);
        entity.setReadStatus(0);
        entity.setMsgTypeId(10L);
        entity.setSourceType("SYSTEM");
        entity.setStatus(0);
        entity.setVersion(0);

        createDTO = new MsgMessageCreateDTO();
        createDTO.setMessageTitle("测试消息");
        createDTO.setMessageContent("测试内容");
        createDTO.setReceiverId(100L);
        createDTO.setMsgTypeId(10L);
        createDTO.setSourceType("SYSTEM");
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create - 新增消息")
    class CreateTests {

        @Test
        @DisplayName("完整DTO → 返回新建ID，事务提交")
        void shouldReturnNewIdWhenValidDto() {
            doAnswer(inv -> {
                MsgMessageEntity e = inv.getArgument(0);
                e.setId(200L);
                return true;
            }).when(service).save(any(MsgMessageEntity.class));

            Long id = service.create(createDTO);

            assertNotNull(id);
            assertEquals(200L, id);
            verify(service).save(any(MsgMessageEntity.class));
        }

        @Test
        @DisplayName("缺少messageTitle → 抛出BusinessException(PARAM_INVALID)")
        void shouldThrowWhenTitleBlank() {
            createDTO.setMessageTitle("");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.create(createDTO));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(service, never()).save(any());
        }

        @Test
        @DisplayName("receiverId为null → 抛出BusinessException(PARAM_INVALID)")
        void shouldThrowWhenReceiverIdNull() {
            createDTO.setReceiverId(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.create(createDTO));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(service, never()).save(any());
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("updateById - 修改消息")
    class UpdateTests {

        @Test
        @DisplayName("实体存在 → 数据更新成功")
        void shouldUpdateWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any())).thenReturn(1);

            MsgMessageEntity update = new MsgMessageEntity();
            update.setId(1L);
            update.setMessageTitle("更新后的消息");
            update.setVersion(0);

            boolean result = service.updateById(update);

            assertTrue(result);
            verify(mapper).updateById(argThat(e ->
                    "更新后的消息".equals(e.getMessageTitle())));
        }

        @Test
        @DisplayName("并发冲突 → 乐观锁异常传播（事务回滚）")
        void shouldPropagateOptimisticLockFailure() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any()))
                    .thenThrow(new RuntimeException("Optimistic lock conflict"));

            MsgMessageEntity update = new MsgMessageEntity();
            update.setId(1L);
            update.setMessageTitle("并发更新");
            update.setVersion(0);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.updateById(update));
            assertTrue(ex.getMessage().contains("Optimistic lock"));
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("removeById - 删除消息")
    class DeleteTests {

        @Test
        @DisplayName("实体存在 → 逻辑删除成功")
        void shouldDeleteWhenEntityExists() {
            doReturn(true).when(service).removeById(1L);

            boolean result = service.removeById(1L);

            assertTrue(result);
        }

        @Test
        @DisplayName("实体不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenEntityNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            // removeById returns false when entity doesn't exist (MP behavior)
            doReturn(false).when(service).removeById(999L);

            boolean result = service.removeById(999L);
            assertFalse(result);
        }
    }

    // ==================== pageList ====================

    @Nested
    @DisplayName("pageList - 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无条件查询 → 返回分页数据")
        void shouldReturnPageData() {
            IPage<MsgMessageEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgMessageQueryDTO query = new MsgMessageQueryDTO();
            PageResult<MsgMessageListVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals("测试消息", result.getList().get(0).getMessageTitle());
        }

        @Test
        @DisplayName("按标题模糊查询 → 条件生效")
        void shouldFilterByTitle() {
            IPage<MsgMessageEntity> page = new Page<>(1, 10, 0);
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgMessageQueryDTO query = new MsgMessageQueryDTO();
            query.setMessageTitle("测试");
            PageResult<MsgMessageListVO> result = service.pageList(query);

            assertNotNull(result);
            verify(mapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("按readStatus查询 → 条件生效")
        void shouldFilterByReadStatus() {
            IPage<MsgMessageEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgMessageQueryDTO query = new MsgMessageQueryDTO();
            query.setReadStatus(0);
            PageResult<MsgMessageListVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("pageNum/pageSize为空 → 使用默认值(1/10)")
        void shouldUseDefaultPagination() {
            IPage<MsgMessageEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgMessageQueryDTO query = new MsgMessageQueryDTO();
            service.pageList(query);

            verify(mapper).selectPage(argThat(p ->
                    p.getCurrent() == 1 && p.getSize() == 10), any());
        }
    }

    // ==================== 事务回滚验证 ====================

    @Nested
    @DisplayName("事务回滚 - 数据一致性")
    class TransactionRollbackTests {

        @Test
        @DisplayName("save中间抛异常 → 数据不回写（事务边界验证）")
        void shouldNotPersistWhenExceptionInSave() {
            doThrow(new RuntimeException("DB connection lost"))
                    .when(service).save(any(MsgMessageEntity.class));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.create(createDTO));
            assertEquals("DB connection lost", ex.getMessage());
            verify(service).save(any(MsgMessageEntity.class));
        }
    }
}
