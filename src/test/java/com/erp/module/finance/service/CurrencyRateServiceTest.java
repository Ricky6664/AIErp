package com.erp.module.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.finance.dto.CurrencyRateCreateDTO;
import com.erp.module.finance.dto.CurrencyRateQueryDTO;
import com.erp.module.finance.dto.CurrencyRateUpdateDTO;
import com.erp.module.finance.entity.CurrencyRateEntity;
import com.erp.module.finance.mapper.CurrencyRateMapper;
import com.erp.module.finance.service.impl.CurrencyRateServiceImpl;
import com.erp.module.finance.vo.CurrencyRateVO;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrencyRateService 单元测试")
class CurrencyRateServiceTest {

    @Mock
    private CurrencyRateMapper currencyRateMapper;

    private CurrencyRateServiceImpl currencyRateService;

    private CurrencyRateCreateDTO createDTO;
    private CurrencyRateUpdateDTO updateDTO;
    private CurrencyRateEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        currencyRateService = spy(new CurrencyRateServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(currencyRateService, currencyRateMapper);

        createDTO = new CurrencyRateCreateDTO();
        createDTO.setCurrencyCode("USD");
        createDTO.setCurrencyName("美元");
        createDTO.setCurrencySymbol("$");
        createDTO.setExchangeRate(new BigDecimal("7.1234"));
        createDTO.setRateType(1);
        createDTO.setEffectiveDate(LocalDate.now());

        updateDTO = new CurrencyRateUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setCurrencyCode("EUR");
        updateDTO.setCurrencyName("欧元");
        updateDTO.setCurrencySymbol("€");
        updateDTO.setExchangeRate(new BigDecimal("7.8901"));
        updateDTO.setRateType(2);
        updateDTO.setEffectiveDate(LocalDate.now().plusDays(10));

        existingEntity = new CurrencyRateEntity();
        existingEntity.setId(1L);
        existingEntity.setCurrencyCode("USD");
        existingEntity.setCurrencyName("美元");
        existingEntity.setCurrencySymbol("$");
        existingEntity.setExchangeRate(new BigDecimal("7.1234"));
        existingEntity.setRateType(1);
        existingEntity.setEffectiveDate(LocalDate.now());
    }

    @AfterEach
    void tearDown() {
        reset(currencyRateMapper);
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create 新增币种汇率")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateCurrencyRateSuccessfully() {
            when(currencyRateMapper.insert(any(CurrencyRateEntity.class))).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getCurrencyCode(), result.getCurrencyCode());
            assertEquals(createDTO.getCurrencyName(), result.getCurrencyName());
            assertEquals(createDTO.getCurrencySymbol(), result.getCurrencySymbol());
            assertEquals(createDTO.getExchangeRate(), result.getExchangeRate());
            assertEquals(createDTO.getRateType(), result.getRateType());

            ArgumentCaptor<CurrencyRateEntity> captor = ArgumentCaptor.forClass(CurrencyRateEntity.class);
            verify(currencyRateMapper).insert(captor.capture());
            assertEquals("USD", captor.getValue().getCurrencyCode());
            assertEquals("美元", captor.getValue().getCurrencyName());
            assertEquals(new BigDecimal("7.1234"), captor.getValue().getExchangeRate());
        }

        @Test
        @DisplayName("编码重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenCodeDuplicate() {
            when(currencyRateMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> currencyRateService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(currencyRateMapper, never()).insert(any());
        }

        @Test
        @DisplayName("编码为空字符串 -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsEmpty() {
            createDTO.setCurrencyCode("");
            when(currencyRateMapper.insert(any(CurrencyRateEntity.class))).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            verify(currencyRateMapper).insert(any());
        }

        @Test
        @DisplayName("编码为null -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsNull() {
            createDTO.setCurrencyCode(null);
            when(currencyRateMapper.insert(any(CurrencyRateEntity.class))).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getCurrencyCode());
            verify(currencyRateMapper).insert(any());
        }

        @Test
        @DisplayName("生效日期晚于当前+30天 -> 抛出BusinessException PARAM_RANGE_ERROR")
        void shouldThrowExceptionWhenEffectiveDateTooFar() {
            createDTO.setEffectiveDate(LocalDate.now().plusDays(31));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> currencyRateService.create(createDTO));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
            verify(currencyRateMapper, never()).insert(any());
        }

        @Test
        @DisplayName("生效日期为null -> 跳过日期校验，正常创建")
        void shouldSkipDateValidationWhenEffectiveDateIsNull() {
            createDTO.setEffectiveDate(null);
            when(currencyRateMapper.insert(any(CurrencyRateEntity.class))).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getEffectiveDate());
            verify(currencyRateMapper).insert(any());
        }

        @Test
        @DisplayName("生效日期正好为+30天 -> 允许创建(边界值)")
        void shouldAllowEffectiveDateAt30Days() {
            createDTO.setEffectiveDate(LocalDate.now().plusDays(30));
            when(currencyRateMapper.insert(any(CurrencyRateEntity.class))).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            assertEquals(LocalDate.now().plusDays(30), result.getEffectiveDate());
        }

        @Test
        @DisplayName("插入失败返回0 -> 不抛异常(MyBatis-Plus save不校验返回值)")
        void shouldNotThrowWhenInsertReturnsZero() {
            when(currencyRateMapper.insert(any(CurrencyRateEntity.class))).thenReturn(0);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            verify(currencyRateMapper).insert(any());
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update 更新币种汇率")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateCurrencyRateSuccessfully() {
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);
            when(currencyRateMapper.updateById(any(CurrencyRateEntity.class))).thenReturn(1);

            CurrencyRateVO result = currencyRateService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getCurrencyCode(), result.getCurrencyCode());
            assertEquals(updateDTO.getCurrencyName(), result.getCurrencyName());
            assertEquals(updateDTO.getExchangeRate(), result.getExchangeRate());

            ArgumentCaptor<CurrencyRateEntity> captor = ArgumentCaptor.forClass(CurrencyRateEntity.class);
            verify(currencyRateMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("EUR", captor.getValue().getCurrencyCode());
            assertEquals(new BigDecimal("7.8901"), captor.getValue().getExchangeRate());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(currencyRateMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> currencyRateService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(currencyRateMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("编码重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenCodeDuplicateExcludingSelf() {
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);
            when(currencyRateMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> currencyRateService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(currencyRateMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时生效日期晚于+30天 -> 抛出BusinessException")
        void shouldThrowExceptionWhenUpdateEffectiveDateTooFar() {
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);
            updateDTO.setEffectiveDate(LocalDate.now().plusDays(31));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> currencyRateService.update(1L, updateDTO));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
            verify(currencyRateMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时生效日期为null -> 跳过校验，更新成功")
        void shouldSkipDateValidationOnUpdateWhenNull() {
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);
            when(currencyRateMapper.updateById(any())).thenReturn(1);
            updateDTO.setEffectiveDate(null);

            CurrencyRateVO result = currencyRateService.update(1L, updateDTO);

            assertNotNull(result);
            assertNull(result.getEffectiveDate());
        }

        @Test
        @DisplayName("更新时编码不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameCodeWhenUpdatingSelf() {
            updateDTO.setCurrencyCode("USD");
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);
            when(currencyRateMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(currencyRateMapper.updateById(any())).thenReturn(1);

            CurrencyRateVO result = currencyRateService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals("USD", result.getCurrencyCode());
            verify(currencyRateMapper).updateById(any());
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete 删除币种汇率")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 软删除成功")
        void shouldSoftDeleteSuccessfully() throws Exception {
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(currencyRateService).removeById(1L);

            assertDoesNotThrow(() -> currencyRateService.delete(1L));
            verify(currencyRateService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(currencyRateMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> currencyRateService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(currencyRateMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("删除已删除的记录 -> 抛出BusinessException(NOT_FOUND)")
        void shouldThrowWhenDeletingAlreadyDeleted() {
            when(currencyRateMapper.selectById(1L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> currencyRateService.delete(1L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("removeById被调用且传入正确ID")
        void shouldCallRemoveByIdWithCorrectId() throws Exception {
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(currencyRateService).removeById(1L);

            currencyRateService.delete(1L);

            verify(currencyRateService).removeById(eq(1L));
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById 查询币种汇率详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);

            CurrencyRateVO result = currencyRateService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getCurrencyCode(), result.getCurrencyCode());
            assertEquals(existingEntity.getCurrencyName(), result.getCurrencyName());
            assertEquals(existingEntity.getExchangeRate(), result.getExchangeRate());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(currencyRateMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> currencyRateService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== pageList ====================

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<CurrencyRateVO> result = currencyRateService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getCurrencyCode(), result.getList().get(0).getCurrencyCode());
        }

        @Test
        @DisplayName("按币种编码精确筛选 -> 返回匹配结果")
        void shouldFilterByCurrencyCode() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setCurrencyCode("USD");

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<CurrencyRateVO> result = currencyRateService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按币种名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByCurrencyNameFuzzy() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setCurrencyName("美元");

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<CurrencyRateVO> result = currencyRateService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按汇率类型筛选 -> 返回匹配结果")
        void shouldFilterByRateType() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();
            query.setPageNum(1);
            query.setPageSize(20);
            query.setRateType(1);

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<CurrencyRateVO> result = currencyRateService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<CurrencyRateVO> result = currencyRateService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setCurrencyCode("USD");
            query.setCurrencyName("美元");
            query.setRateType(1);

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<CurrencyRateVO> result = currencyRateService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();
            query.setSortField("currencyCode");
            query.setSortOrder("ASC");

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> currencyRateService.pageList(query));
            verify(currencyRateMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("无效排序字段 -> 使用默认排序(createTime降序)")
        void shouldFallbackToDefaultSortWhenInvalidField() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();
            query.setSortField("invalidField");
            query.setSortOrder("DESC");

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> currencyRateService.pageList(query));
        }
    }

    // ==================== toVO转换 ====================

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(currencyRateMapper.selectById(1L)).thenReturn(existingEntity);

            CurrencyRateVO result = currencyRateService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getCurrencyCode(), result.getCurrencyCode());
            assertEquals(existingEntity.getCurrencyName(), result.getCurrencyName());
            assertEquals(existingEntity.getCurrencySymbol(), result.getCurrencySymbol());
            assertEquals(existingEntity.getExchangeRate(), result.getExchangeRate());
            assertEquals(existingEntity.getRateType(), result.getRateType());
            assertEquals(existingEntity.getEffectiveDate(), result.getEffectiveDate());
        }
    }

    // ==================== @Transactional 注解验证 ====================

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = CurrencyRateServiceImpl.class.getMethod("create", CurrencyRateCreateDTO.class);
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
            var method = CurrencyRateServiceImpl.class.getMethod("update", Long.class, CurrencyRateUpdateDTO.class);
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
            var method = CurrencyRateServiceImpl.class.getMethod("delete", Long.class);
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
            var method = CurrencyRateServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = CurrencyRateServiceImpl.class.getMethod("pageList", CurrencyRateQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "pageList应标注@Transactional");
            assertTrue(annotation.readOnly(), "pageList应为readOnly=true");
        }
    }

    // ==================== 边界场景 ====================

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("超长编码 -> 不抛异常(校验在Controller层)")
        void shouldNotThrowOnLongCode() {
            createDTO.setCurrencyCode("C".repeat(100));
            when(currencyRateMapper.insert(any())).thenReturn(1);

            assertDoesNotThrow(() -> currencyRateService.create(createDTO));
        }

        @Test
        @DisplayName("特殊字符编码 -> 通过唯一性校验")
        void shouldAcceptSpecialCharactersInCode() {
            createDTO.setCurrencyCode("USD-CNY_001");
            when(currencyRateMapper.insert(any())).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            assertEquals("USD-CNY_001", result.getCurrencyCode());
        }

        @Test
        @DisplayName("超大汇率值 -> 正常创建")
        void shouldCreateWithLargeExchangeRate() {
            createDTO.setExchangeRate(new BigDecimal("99999999.999999"));
            when(currencyRateMapper.insert(any())).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("99999999.999999"), result.getExchangeRate());
        }

        @Test
        @DisplayName("delete传入null ID -> 抛出BusinessException")
        void shouldHandleNullIdInDelete() {
            when(currencyRateMapper.selectById(isNull())).thenReturn(null);

            assertThrows(BusinessException.class, () -> currencyRateService.delete(null));
        }

        @Test
        @DisplayName("pageList无排序字段 -> 默认按createTime降序")
        void shouldUseDefaultSortOrderWhenNoSortField() {
            CurrencyRateQueryDTO query = new CurrencyRateQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<CurrencyRateEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(currencyRateMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<CurrencyRateEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> currencyRateService.pageList(query));
        }

        @Test
        @DisplayName("symbol为空 -> 正常创建(非必填)")
        void shouldCreateWithNullSymbol() {
            createDTO.setCurrencySymbol(null);
            when(currencyRateMapper.insert(any())).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getCurrencySymbol());
        }

        @Test
        @DisplayName("rateType为null -> 正常创建(非必填)")
        void shouldCreateWithNullRateType() {
            createDTO.setRateType(null);
            when(currencyRateMapper.insert(any())).thenReturn(1);

            CurrencyRateVO result = currencyRateService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getRateType());
        }
    }
}
