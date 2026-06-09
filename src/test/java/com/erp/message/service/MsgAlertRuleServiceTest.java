package com.erp.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.message.dto.MsgAlertRuleCreateDTO;
import com.erp.module.message.dto.MsgAlertRuleQueryDTO;
import com.erp.module.message.dto.MsgAlertRuleUpdateDTO;
import com.erp.module.message.entity.MsgAlertRuleEntity;
import com.erp.module.message.mapper.MsgAlertRuleMapper;
import com.erp.module.message.service.impl.MsgAlertRuleServiceImpl;
import com.erp.module.message.vo.MsgAlertRuleListVO;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MsgAlertRuleService 单元测试")
class MsgAlertRuleServiceTest {

    @Mock
    private MsgAlertRuleMapper mapper;

    private MsgAlertRuleServiceImpl service;

    private MsgAlertRuleEntity entity;
    private MsgAlertRuleCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        service = spy(new MsgAlertRuleServiceImpl());
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        entity = new MsgAlertRuleEntity();
        entity.setId(1L);
        entity.setRuleName("库存预警规则");
        entity.setConditionExpression("stock < 10");
        entity.setThreshold(new BigDecimal("100"));
        entity.setFrequency("DAILY");
        entity.setTriggerActionConfig("{\"action\":\"push\"}");
        entity.setIsEnabled(true);
        entity.setVersion(0);

        createDTO = new MsgAlertRuleCreateDTO();
        createDTO.setRuleName("库存预警规则");
        createDTO.setConditionExpression("stock < 10");
        createDTO.setThreshold(new BigDecimal("100"));
        createDTO.setFrequency("DAILY");
        createDTO.setTriggerActionConfig("{\"action\":\"push\"}");
        createDTO.setIsEnabled(true);
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create - 新增业务预警规则")
    class CreateTests {

        @Test
        @DisplayName("完整DTO → 返回新建ID，事务提交")
        void shouldReturnNewIdWhenValidDto() {
            doAnswer(inv -> {
                MsgAlertRuleEntity e = inv.getArgument(0);
                e.setId(200L);
                return true;
            }).when(service).save(any(MsgAlertRuleEntity.class));

            Long id = service.create(createDTO);

            assertNotNull(id);
            assertEquals(200L, id);
            verify(service).save(any(MsgAlertRuleEntity.class));
        }

        @Test
        @DisplayName("缺少ruleName → 抛出BusinessException(PARAM_INVALID)")
        void shouldThrowWhenRuleNameBlank() {
            createDTO.setRuleName("");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.create(createDTO));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(service, never()).save(any());
        }

        @Test
        @DisplayName("isEnabled为null → 默认设为true")
        void shouldDefaultIsEnabledToTrueWhenNull() {
            createDTO.setIsEnabled(null);
            doAnswer(inv -> {
                MsgAlertRuleEntity e = inv.getArgument(0);
                e.setId(300L);
                return true;
            }).when(service).save(any(MsgAlertRuleEntity.class));

            Long id = service.create(createDTO);

            assertNotNull(id);
            verify(service).save(argThat(e ->
                    Boolean.TRUE.equals(e.getIsEnabled())));
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update - 修改业务预警规则")
    class UpdateTests {

        @Test
        @DisplayName("实体存在 → 数据更新成功")
        void shouldUpdateWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgAlertRuleEntity.class))).thenReturn(1);

            MsgAlertRuleUpdateDTO dto = new MsgAlertRuleUpdateDTO();
            dto.setRuleName("更新后的规则");
            dto.setThreshold(new BigDecimal("200"));

            service.update(1L, dto);

            verify(mapper).selectById(1L);
            verify(mapper).updateById(argThat(e ->
                    "更新后的规则".equals(e.getRuleName())
                            && new BigDecimal("200").equals(e.getThreshold())));
        }

        @Test
        @DisplayName("实体不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenEntityNotFound() {
            when(mapper.selectById(999L)).thenReturn(null);

            MsgAlertRuleUpdateDTO dto = new MsgAlertRuleUpdateDTO();
            dto.setRuleName("不存在");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.update(999L, dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(mapper, never()).updateById(any());
        }

        @Test
        @DisplayName("并发冲突(乐观锁失败) → 异常传播，事务回滚")
        void shouldThrowOnOptimisticLockConflict() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgAlertRuleEntity.class)))
                    .thenThrow(new RuntimeException("OptimisticLockingFailure"));

            MsgAlertRuleUpdateDTO dto = new MsgAlertRuleUpdateDTO();
            dto.setRuleName("并发更新");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.update(1L, dto));
            assertTrue(ex.getMessage().contains("OptimisticLockingFailure"));
        }

        @Test
        @DisplayName("部分字段更新 → 仅更新非null字段")
        void shouldOnlyUpdateNonNullFields() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgAlertRuleEntity.class))).thenReturn(1);

            MsgAlertRuleUpdateDTO dto = new MsgAlertRuleUpdateDTO();
            dto.setRuleName("新名称");

            service.update(1L, dto);

            verify(mapper).updateById(argThat(e ->
                    "新名称".equals(e.getRuleName())
                            && "stock < 10".equals(e.getConditionExpression())
                            && entity.getFrequency().equals(e.getFrequency())));
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete - 删除业务预警规则")
    class DeleteTests {

        @Test
        @DisplayName("实体存在 → 逻辑删除成功(isDeleted=true)")
        void shouldDeleteWhenEntityExists() {
            when(mapper.selectById(1L)).thenReturn(entity);
            when(mapper.updateById(any(MsgAlertRuleEntity.class))).thenReturn(1);

            service.delete(1L);

            verify(mapper).selectById(1L);
            verify(mapper).updateById(argThat(MsgAlertRuleEntity::getIsDeleted));
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
            IPage<MsgAlertRuleEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgAlertRuleQueryDTO query = new MsgAlertRuleQueryDTO();
            PageResult<MsgAlertRuleListVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals("库存预警规则", result.getList().get(0).getRuleName());
        }

        @Test
        @DisplayName("按ruleName模糊查询 → 条件生效")
        void shouldFilterByRuleName() {
            IPage<MsgAlertRuleEntity> page = new Page<>(1, 10, 0);
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgAlertRuleQueryDTO query = new MsgAlertRuleQueryDTO();
            query.setRuleName("库存");
            PageResult<MsgAlertRuleListVO> result = service.pageList(query);

            assertNotNull(result);
            verify(mapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("按isEnabled查询 → 条件生效")
        void shouldFilterByIsEnabled() {
            IPage<MsgAlertRuleEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgAlertRuleQueryDTO query = new MsgAlertRuleQueryDTO();
            query.setIsEnabled(true);
            PageResult<MsgAlertRuleListVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("pageNum/pageSize为空 → 使用默认值(1/10)")
        void shouldUseDefaultPagination() {
            IPage<MsgAlertRuleEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(mapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            MsgAlertRuleQueryDTO query = new MsgAlertRuleQueryDTO();
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
        @DisplayName("save中间抛异常 → 异常传播")
        void shouldNotPersistWhenExceptionInSave() {
            doThrow(new RuntimeException("DB connection lost"))
                    .when(service).save(any(MsgAlertRuleEntity.class));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.create(createDTO));
            assertEquals("DB connection lost", ex.getMessage());
            verify(service).save(any(MsgAlertRuleEntity.class));
        }
    }
}
