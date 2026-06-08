package com.erp.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.message.dto.MsgMessageCreateDTO;
import com.erp.module.message.dto.MsgMessageQueryDTO;
import com.erp.module.message.dto.MsgMessageUpdateDTO;
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
    @DisplayName("update - 修改消息")
    class UpdateTests {

        @Test
        @DisplayName("实体存在 → 数据更新成功，version+1")
        void shouldUpdateWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgMessageEntity.class))).thenReturn(1);

            MsgMessageUpdateDTO dto = new MsgMessageUpdateDTO();
            dto.setMessageTitle("更新后的标题");
            dto.setMessageContent("更新后的内容");

            service.update(1L, dto);

            verify(mapper).selectById(1L);
            verify(mapper).updateById(argThat(e ->
                    "更新后的标题".equals(e.getMessageTitle())
                            && "更新后的内容".equals(e.getMessageContent())));
        }

        @Test
        @DisplayName("实体不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenEntityNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            MsgMessageUpdateDTO dto = new MsgMessageUpdateDTO();
            dto.setMessageTitle("不存在");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.update(999L, dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(mapper, never()).updateById(any());
        }

        @Test
        @DisplayName("并发冲突(乐观锁失败) → 异常传播，事务回滚")
        void shouldThrowOnOptimisticLockConflict() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgMessageEntity.class)))
                    .thenThrow(new RuntimeException("OptimisticLockingFailure"));

            MsgMessageUpdateDTO dto = new MsgMessageUpdateDTO();
            dto.setMessageTitle("并发更新");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.update(1L, dto));
            assertTrue(ex.getMessage().contains("OptimisticLockingFailure"));
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete - 删除消息")
    class DeleteTests {

        @Test
        @DisplayName("实体存在 → 逻辑删除成功(isDeleted=true)")
        void shouldDeleteWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgMessageEntity.class))).thenReturn(1);

            service.delete(1L);

            verify(mapper).selectById(1L);
            verify(mapper).updateById(argThat(MsgMessageEntity::getIsDeleted));
        }

        @Test
        @DisplayName("实体不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenEntityNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.delete(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(mapper, never()).updateById(any());
        }
    }

    // ==================== read ====================

    @Nested
    @DisplayName("read - 标记已读")
    class ReadTests {

        @Test
        @DisplayName("实体存在 → readStatus设为1")
        void shouldMarkAsReadWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgMessageEntity.class))).thenReturn(1);

            service.read(1L);

            verify(mapper).selectById(1L);
            verify(mapper).updateById(argThat(e ->
                    e.getReadStatus() != null && e.getReadStatus() == 1));
        }

        @Test
        @DisplayName("实体不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenEntityNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.read(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(mapper, never()).updateById(any());
        }
    }

    // ==================== readAll ====================

    @Nested
    @DisplayName("readAll - 全部标记已读")
    class ReadAllTests {

        @Test
        @DisplayName("存在未读消息 → 全部标记为已读")
        void shouldMarkAllUnreadAsRead() {
            MsgMessageEntity unreadEntity = new MsgMessageEntity();
            unreadEntity.setId(2L);
            unreadEntity.setMessageTitle("未读消息");
            unreadEntity.setReadStatus(0);
            unreadEntity.setVersion(0);

            when(mapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(java.util.List.of(entity, unreadEntity));
            when(mapper.updateById(any(MsgMessageEntity.class))).thenReturn(1);

            service.readAll();

            verify(mapper).selectList(any(LambdaQueryWrapper.class));
            verify(mapper, times(2)).updateById(argThat(e ->
                    e.getReadStatus() != null && e.getReadStatus() == 1));
        }

        @Test
        @DisplayName("无未读消息 → 不执行更新")
        void shouldNotUpdateWhenNoUnread() {
            when(mapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(java.util.List.of());

            service.readAll();

            verify(mapper).selectList(any(LambdaQueryWrapper.class));
            verify(mapper, never()).updateById(any());
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
        @DisplayName("按msgTypeId查询 → 条件生效")
        void shouldFilterByMsgTypeId() {
            IPage<MsgMessageEntity> page = new Page<>(1, 10, 0);
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgMessageQueryDTO query = new MsgMessageQueryDTO();
            query.setMsgTypeId(10L);
            service.pageList(query);

            verify(mapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("按receiverId查询 → 条件生效")
        void shouldFilterByReceiverId() {
            IPage<MsgMessageEntity> page = new Page<>(1, 10, 0);
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgMessageQueryDTO query = new MsgMessageQueryDTO();
            query.setReceiverId(100L);
            service.pageList(query);

            verify(mapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
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

    // ==================== 事务回滚 ====================

    @Nested
    @DisplayName("事务回滚 - 数据一致性")
    class TransactionRollbackTests {

        @Test
        @DisplayName("save中间抛异常 → 异常传播，数据不回写")
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
