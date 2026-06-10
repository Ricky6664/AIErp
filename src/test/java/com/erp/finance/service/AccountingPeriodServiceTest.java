package com.erp.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.finance.dto.AccountingPeriodCreateDTO;
import com.erp.finance.dto.AccountingPeriodQueryDTO;
import com.erp.finance.dto.AccountingPeriodUpdateDTO;
import com.erp.finance.entity.AccountingPeriodEntity;
import com.erp.finance.mapper.AccountingPeriodMapper;
import com.erp.finance.service.impl.AccountingPeriodServiceImpl;
import com.erp.finance.vo.AccountingPeriodVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountingPeriodService 单元测试")
class AccountingPeriodServiceTest {

    @Mock
    private AccountingPeriodMapper accountingPeriodMapper;

    private AccountingPeriodServiceImpl accountingPeriodService;

    private AccountingPeriodCreateDTO createDTO;
    private AccountingPeriodUpdateDTO updateDTO;
    private AccountingPeriodEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        accountingPeriodService = spy(new AccountingPeriodServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(accountingPeriodService, accountingPeriodMapper);

        createDTO = new AccountingPeriodCreateDTO();
        createDTO.setFiscalYear("2026");
        createDTO.setPeriod("01");
        createDTO.setStartDate(LocalDate.of(2026, 1, 1));
        createDTO.setEndDate(LocalDate.of(2026, 1, 31));
        createDTO.setPeriodStatus("OPEN");
        createDTO.setIsYearEnd(false);

        updateDTO = new AccountingPeriodUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setFiscalYear("2026");
        updateDTO.setPeriod("02");
        updateDTO.setStartDate(LocalDate.of(2026, 2, 1));
        updateDTO.setEndDate(LocalDate.of(2026, 2, 28));
        updateDTO.setPeriodStatus("OPEN");
        updateDTO.setIsYearEnd(false);

        existingEntity = new AccountingPeriodEntity();
        existingEntity.setId(1L);
        existingEntity.setFiscalYear("2026");
        existingEntity.setPeriod("01");
        existingEntity.setStartDate(LocalDate.of(2026, 1, 1));
        existingEntity.setEndDate(LocalDate.of(2026, 1, 31));
        existingEntity.setPeriodStatus("OPEN");
        existingEntity.setIsYearEnd(false);
    }

    @AfterEach
    void tearDown() {
        reset(accountingPeriodMapper);
    }

    @Nested
    @DisplayName("create 新增会计期间")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateAccountingPeriodSuccessfully() {
            when(accountingPeriodMapper.insert(any(AccountingPeriodEntity.class))).thenReturn(1);
            when(accountingPeriodMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            AccountingPeriodVO result = accountingPeriodService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getFiscalYear(), result.getFiscalYear());
            assertEquals(createDTO.getPeriod(), result.getPeriod());
            assertEquals(createDTO.getStartDate(), result.getStartDate());
            assertEquals(createDTO.getEndDate(), result.getEndDate());
            assertEquals(createDTO.getPeriodStatus(), result.getPeriodStatus());

            ArgumentCaptor<AccountingPeriodEntity> captor = ArgumentCaptor.forClass(AccountingPeriodEntity.class);
            verify(accountingPeriodMapper).insert(captor.capture());
            assertEquals("2026", captor.getValue().getFiscalYear());
            assertEquals("01", captor.getValue().getPeriod());
        }

        @Test
        @DisplayName("期间重叠 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenPeriodOverlaps() {
            when(accountingPeriodMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountingPeriodService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(accountingPeriodMapper, never()).insert(any());
        }

        @Test
        @DisplayName("结束日期早于开始日期 -> 抛出BusinessException")
        void shouldThrowExceptionWhenEndDateBeforeStartDate() {
            createDTO.setEndDate(LocalDate.of(2025, 12, 31));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountingPeriodService.create(createDTO));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(accountingPeriodMapper, never()).insert(any());
        }

        @Test
        @DisplayName("fiscalYear为空 -> 跳过重叠校验正常创建")
        void shouldSkipOverlapCheckWhenFiscalYearEmpty() {
            createDTO.setFiscalYear("");
            when(accountingPeriodMapper.insert(any())).thenReturn(1);

            AccountingPeriodVO result = accountingPeriodService.create(createDTO);

            assertNotNull(result);
            verify(accountingPeriodMapper).insert(any());
        }

        @Test
        @DisplayName("fiscalYear为null -> 跳过重叠校验正常创建")
        void shouldSkipOverlapCheckWhenFiscalYearNull() {
            createDTO.setFiscalYear(null);
            when(accountingPeriodMapper.insert(any())).thenReturn(1);

            AccountingPeriodVO result = accountingPeriodService.create(createDTO);

            assertNotNull(result);
            verify(accountingPeriodMapper).insert(any());
        }

        @Test
        @DisplayName("isYearEnd=true -> 正常创建")
        void shouldCreateWithYearEndTrue() {
            createDTO.setIsYearEnd(true);
            when(accountingPeriodMapper.insert(any())).thenReturn(1);
            when(accountingPeriodMapper.selectCount(any())).thenReturn(0L);

            AccountingPeriodVO result = accountingPeriodService.create(createDTO);

            assertNotNull(result);
            assertTrue(result.getIsYearEnd());
        }
    }

    @Nested
    @DisplayName("update 更新会计期间")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateAccountingPeriodSuccessfully() {
            when(accountingPeriodMapper.selectById(1L)).thenReturn(existingEntity);
            when(accountingPeriodMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(accountingPeriodMapper.updateById(any(AccountingPeriodEntity.class))).thenReturn(1);

            AccountingPeriodVO result = accountingPeriodService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getPeriod(), result.getPeriod());
            assertEquals(updateDTO.getStartDate(), result.getStartDate());
            assertEquals(updateDTO.getEndDate(), result.getEndDate());

            ArgumentCaptor<AccountingPeriodEntity> captor = ArgumentCaptor.forClass(AccountingPeriodEntity.class);
            verify(accountingPeriodMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("02", captor.getValue().getPeriod());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(accountingPeriodMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountingPeriodService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(accountingPeriodMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时期间重叠排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenOverlapExcludingSelf() {
            when(accountingPeriodMapper.selectById(1L)).thenReturn(existingEntity);
            when(accountingPeriodMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountingPeriodService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(accountingPeriodMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新结束日期早于开始日期 -> 抛出BusinessException")
        void shouldThrowWhenEndDateBeforeStartOnUpdate() {
            updateDTO.setEndDate(LocalDate.of(2025, 12, 31));
            when(accountingPeriodMapper.selectById(1L)).thenReturn(existingEntity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountingPeriodService.update(1L, updateDTO));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("期间不变 -> 重叠校验排除自身通过")
        void shouldAllowSamePeriodWhenUpdatingSelf() {
            updateDTO.setFiscalYear("2026");
            updateDTO.setPeriod("01");
            updateDTO.setStartDate(LocalDate.of(2026, 1, 1));
            updateDTO.setEndDate(LocalDate.of(2026, 1, 31));
            when(accountingPeriodMapper.selectById(1L)).thenReturn(existingEntity);
            when(accountingPeriodMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(accountingPeriodMapper.updateById(any())).thenReturn(1);

            AccountingPeriodVO result = accountingPeriodService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals("01", result.getPeriod());
            verify(accountingPeriodMapper).updateById(any());
        }
    }

    @Nested
    @DisplayName("delete 删除会计期间")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 删除成功")
        void shouldDeleteSuccessfully() {
            when(accountingPeriodMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(accountingPeriodService).removeById(1L);

            assertDoesNotThrow(() -> accountingPeriodService.delete(1L));
            verify(accountingPeriodService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(accountingPeriodMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountingPeriodService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(accountingPeriodMapper, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("getById 查询会计期间详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(accountingPeriodMapper.selectById(1L)).thenReturn(existingEntity);

            AccountingPeriodVO result = accountingPeriodService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getFiscalYear(), result.getFiscalYear());
            assertEquals(existingEntity.getPeriod(), result.getPeriod());
            assertEquals(existingEntity.getStartDate(), result.getStartDate());
            assertEquals(existingEntity.getEndDate(), result.getEndDate());
            assertEquals(existingEntity.getPeriodStatus(), result.getPeriodStatus());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(accountingPeriodMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountingPeriodService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountingPeriodVO> result = accountingPeriodService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getPeriod(), result.getList().get(0).getPeriod());
        }

        @Test
        @DisplayName("按会计年度筛选 -> 返回匹配结果")
        void shouldFilterByFiscalYear() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setFiscalYear("2026");

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountingPeriodVO> result = accountingPeriodService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按会计期间筛选 -> 返回匹配结果")
        void shouldFilterByPeriod() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setPeriod("01");

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountingPeriodVO> result = accountingPeriodService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按期间状态筛选 -> 返回匹配结果")
        void shouldFilterByPeriodStatus() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setPeriodStatus("OPEN");

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountingPeriodVO> result = accountingPeriodService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setFiscalYear("2026");
            query.setPeriod("01");
            query.setPeriodStatus("OPEN");

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountingPeriodVO> result = accountingPeriodService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<AccountingPeriodVO> result = accountingPeriodService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setSortField("fiscalYear");
            query.setSortOrder("ASC");

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> accountingPeriodService.pageList(query));
            verify(accountingPeriodMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("无效排序字段 -> 使用默认排序(fiscalYear降序+period升序)")
        void shouldFallbackToDefaultSortWhenInvalidField() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setSortField("invalidField");
            query.setSortOrder("DESC");

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> accountingPeriodService.pageList(query));
        }

        @Test
        @DisplayName("无排序字段 -> 默认按fiscalYear降序period升序")
        void shouldUseDefaultSortOrderWhenNoSortField() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> accountingPeriodService.pageList(query));
        }

        @Test
        @DisplayName("按startDate排序 -> 正确排序")
        void shouldSortByStartDate() {
            AccountingPeriodQueryDTO query = new AccountingPeriodQueryDTO();
            query.setSortField("startDate");
            query.setSortOrder("DESC");

            IPage<AccountingPeriodEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountingPeriodMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<AccountingPeriodEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> accountingPeriodService.pageList(query));
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(accountingPeriodMapper.selectById(1L)).thenReturn(existingEntity);

            AccountingPeriodVO result = accountingPeriodService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getFiscalYear(), result.getFiscalYear());
            assertEquals(existingEntity.getPeriod(), result.getPeriod());
            assertEquals(existingEntity.getStartDate(), result.getStartDate());
            assertEquals(existingEntity.getEndDate(), result.getEndDate());
            assertEquals(existingEntity.getPeriodStatus(), result.getPeriodStatus());
            assertEquals(existingEntity.getIsYearEnd(), result.getIsYearEnd());
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = AccountingPeriodServiceImpl.class.getMethod("create", AccountingPeriodCreateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "create应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("update标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnUpdate() throws NoSuchMethodException {
            var method = AccountingPeriodServiceImpl.class.getMethod("update", Long.class,
                    AccountingPeriodUpdateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "update应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("delete标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnDelete() throws NoSuchMethodException {
            var method = AccountingPeriodServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "delete应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("getById标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = AccountingPeriodServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = AccountingPeriodServiceImpl.class.getMethod("pageList", AccountingPeriodQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "pageList应标注@Transactional");
            assertTrue(annotation.readOnly(), "pageList应为readOnly=true");
        }
    }

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("超长fiscalYear -> 正常创建")
        void shouldCreateWithLongFiscalYear() {
            createDTO.setFiscalYear("F".repeat(20));
            when(accountingPeriodMapper.insert(any())).thenReturn(1);
            when(accountingPeriodMapper.selectCount(any())).thenReturn(0L);

            AccountingPeriodVO result = accountingPeriodService.create(createDTO);

            assertNotNull(result);
        }

        @Test
        @DisplayName("跨年期间 -> 正常创建(不重叠)")
        void shouldCreateCrossYearPeriod() {
            createDTO.setStartDate(LocalDate.of(2025, 12, 1));
            createDTO.setEndDate(LocalDate.of(2026, 1, 31));
            when(accountingPeriodMapper.insert(any())).thenReturn(1);
            when(accountingPeriodMapper.selectCount(any())).thenReturn(0L);

            AccountingPeriodVO result = accountingPeriodService.create(createDTO);

            assertNotNull(result);
            assertEquals(LocalDate.of(2025, 12, 1), result.getStartDate());
            assertEquals(LocalDate.of(2026, 1, 31), result.getEndDate());
        }

        @Test
        @DisplayName("startDate为null -> 跳过重叠校验正常创建")
        void shouldSkipValidationWhenStartDateNull() {
            createDTO.setStartDate(null);
            when(accountingPeriodMapper.insert(any())).thenReturn(1);

            AccountingPeriodVO result = accountingPeriodService.create(createDTO);

            assertNotNull(result);
            verify(accountingPeriodMapper).insert(any());
        }

        @Test
        @DisplayName("update时传入同一天的单日期间 -> 重叠校验通过")
        void shouldAllowSingleDayPeriod() {
            updateDTO.setStartDate(LocalDate.of(2026, 3, 15));
            updateDTO.setEndDate(LocalDate.of(2026, 3, 15));
            when(accountingPeriodMapper.selectById(1L)).thenReturn(existingEntity);
            when(accountingPeriodMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(accountingPeriodMapper.updateById(any())).thenReturn(1);

            AccountingPeriodVO result = accountingPeriodService.update(1L, updateDTO);

            assertNotNull(result);
        }
    }
}
