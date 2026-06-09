package com.erp.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.message.dto.MsgTemplateCreateDTO;
import com.erp.module.message.dto.MsgTemplateQueryDTO;
import com.erp.module.message.dto.MsgTemplateUpdateDTO;
import com.erp.module.message.entity.MsgTemplateEntity;
import com.erp.module.message.mapper.MsgTemplateMapper;
import com.erp.module.message.service.impl.MsgTemplateServiceImpl;
import com.erp.module.message.vo.MsgTemplateListVO;
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
@DisplayName("MsgTemplateService 单元测试")
class MsgTemplateServiceTest {

    @Mock
    private MsgTemplateMapper mapper;

    private MsgTemplateServiceImpl service;

    private MsgTemplateEntity entity;
    private MsgTemplateCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        service = spy(new MsgTemplateServiceImpl());
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        entity = new MsgTemplateEntity();
        entity.setId(1L);
        entity.setTemplateCode("MSG_001");
        entity.setTitleTemplate("新消息通知");
        entity.setContentTemplate("您有一条来自${sender}的新消息");
        entity.setIsEnabled(true);
        entity.setVersion(0);

        createDTO = new MsgTemplateCreateDTO();
        createDTO.setTemplateCode("MSG_001");
        createDTO.setTitleTemplate("新消息通知");
        createDTO.setContentTemplate("您有一条来自${sender}的新消息");
        createDTO.setIsEnabled(true);
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create - 新增模板")
    class CreateTests {

        @Test
        @DisplayName("完整DTO → 返回新建ID")
        void shouldReturnNewIdWhenValidDto() {
            doAnswer(inv -> {
                MsgTemplateEntity e = inv.getArgument(0);
                e.setId(200L);
                return true;
            }).when(service).save(any(MsgTemplateEntity.class));

            Long id = service.create(createDTO);

            assertNotNull(id);
            assertEquals(200L, id);
            verify(service).save(any(MsgTemplateEntity.class));
        }

        @Test
        @DisplayName("缺少templateCode → 抛出BusinessException(PARAM_INVALID)")
        void shouldThrowWhenTemplateCodeBlank() {
            createDTO.setTemplateCode("");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.create(createDTO));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(service, never()).save(any());
        }

        @Test
        @DisplayName("templateCode为null → 抛出BusinessException(PARAM_INVALID)")
        void shouldThrowWhenTemplateCodeNull() {
            createDTO.setTemplateCode(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.create(createDTO));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(service, never()).save(any());
        }

        @Test
        @DisplayName("titleTemplate为空(非必填) → 成功保存")
        void shouldSucceedWhenTitleTemplateEmpty() {
            createDTO.setTitleTemplate(null);
            doAnswer(inv -> {
                MsgTemplateEntity e = inv.getArgument(0);
                e.setId(300L);
                return true;
            }).when(service).save(any(MsgTemplateEntity.class));

            Long id = service.create(createDTO);
            assertNotNull(id);
            verify(service).save(any(MsgTemplateEntity.class));
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update - 修改模板")
    class UpdateTests {

        @Test
        @DisplayName("实体存在 → 数据更新成功")
        void shouldUpdateWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgTemplateEntity.class))).thenReturn(1);

            MsgTemplateUpdateDTO dto = new MsgTemplateUpdateDTO();
            dto.setTitleTemplate("更新后的标题模板");
            dto.setContentTemplate("更新后的内容模板");
            dto.setIsEnabled(false);

            service.update(1L, dto);

            verify(mapper).selectById(1L);
            verify(mapper).updateById(argThat(e ->
                    "更新后的标题模板".equals(e.getTitleTemplate())
                            && "更新后的内容模板".equals(e.getContentTemplate())
                            && Boolean.FALSE.equals(e.getIsEnabled())));
        }

        @Test
        @DisplayName("实体不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenEntityNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            MsgTemplateUpdateDTO dto = new MsgTemplateUpdateDTO();
            dto.setTitleTemplate("不存在");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.update(999L, dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(mapper, never()).updateById(any());
        }

        @Test
        @DisplayName("并发冲突(乐观锁失败) → 异常传播")
        void shouldThrowOnOptimisticLockConflict() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgTemplateEntity.class)))
                    .thenThrow(new RuntimeException("OptimisticLockingFailure"));

            MsgTemplateUpdateDTO dto = new MsgTemplateUpdateDTO();
            dto.setTitleTemplate("并发更新");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.update(1L, dto));
            assertTrue(ex.getMessage().contains("OptimisticLockingFailure"));
        }

        @Test
        @DisplayName("部分更新(仅titleTemplate) → 只更新指定字段")
        void shouldPartialUpdate() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgTemplateEntity.class))).thenReturn(1);

            MsgTemplateUpdateDTO dto = new MsgTemplateUpdateDTO();
            dto.setTitleTemplate("仅更新标题");

            service.update(1L, dto);

            verify(mapper).updateById(argThat(e ->
                    "仅更新标题".equals(e.getTitleTemplate())));
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete - 删除模板")
    class DeleteTests {

        @Test
        @DisplayName("实体存在 → 逻辑删除成功(isDeleted=true)")
        void shouldDeleteWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgTemplateEntity.class))).thenReturn(1);

            service.delete(1L);

            verify(mapper).selectById(1L);
            verify(mapper).updateById(argThat(MsgTemplateEntity::getIsDeleted));
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

    // ==================== pageList ====================

    @Nested
    @DisplayName("pageList - 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无条件查询 → 返回分页数据")
        void shouldReturnPageData() {
            IPage<MsgTemplateEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgTemplateQueryDTO query = new MsgTemplateQueryDTO();
            PageResult<MsgTemplateListVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals("MSG_001", result.getList().get(0).getTemplateCode());
        }

        @Test
        @DisplayName("按templateCode模糊查询 → 条件生效")
        void shouldFilterByTemplateCode() {
            IPage<MsgTemplateEntity> page = new Page<>(1, 10, 0);
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgTemplateQueryDTO query = new MsgTemplateQueryDTO();
            query.setTemplateCode("MSG");
            PageResult<MsgTemplateListVO> result = service.pageList(query);

            assertNotNull(result);
            verify(mapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("按isEnabled查询 → 条件生效")
        void shouldFilterByIsEnabled() {
            IPage<MsgTemplateEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgTemplateQueryDTO query = new MsgTemplateQueryDTO();
            query.setIsEnabled(true);
            PageResult<MsgTemplateListVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按titleTemplate模糊查询 → 条件生效")
        void shouldFilterByTitleTemplate() {
            IPage<MsgTemplateEntity> page = new Page<>(1, 10, 0);
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgTemplateQueryDTO query = new MsgTemplateQueryDTO();
            query.setTitleTemplate("通知");
            service.pageList(query);

            verify(mapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("pageNum/pageSize为空 → 使用默认值(1/10)")
        void shouldUseDefaultPagination() {
            IPage<MsgTemplateEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgTemplateQueryDTO query = new MsgTemplateQueryDTO();
            service.pageList(query);

            verify(mapper).selectPage(argThat(p ->
                    p.getCurrent() == 1 && p.getSize() == 10), any());
        }

        @Test
        @DisplayName("空结果 → total=0, list为空")
        void shouldReturnEmptyWhenNoData() {
            IPage<MsgTemplateEntity> page = new Page<>(1, 10, 0);
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgTemplateQueryDTO query = new MsgTemplateQueryDTO();
            PageResult<MsgTemplateListVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }
    }

    // ==================== 事务回滚 ====================

    @Nested
    @DisplayName("事务回滚 - 数据一致性")
    class TransactionRollbackTests {

        @Test
        @DisplayName("save中间抛异常 → 异常传播")
        void shouldNotPersistWhenExceptionInSave() {
            doThrow(new RuntimeException("DB connection lost"))
                    .when(service).save(any(MsgTemplateEntity.class));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.create(createDTO));
            assertEquals("DB connection lost", ex.getMessage());
            verify(service).save(any(MsgTemplateEntity.class));
        }
    }
}
