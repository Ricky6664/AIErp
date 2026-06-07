package com.erp.hrm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.dto.SalaryCreateDTO;
import com.erp.hrm.dto.SalaryQueryDTO;
import com.erp.hrm.dto.SalaryUpdateDTO;
import com.erp.hrm.entity.SalaryEntity;
import com.erp.hrm.mapper.SalaryMapper;
import com.erp.hrm.service.impl.SalaryServiceImpl;
import com.erp.hrm.vo.SalaryVO;
import jakarta.validation.Valid;
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
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryService 单元测试")
class SalaryServiceTest {

    @Mock
    private SalaryMapper salaryMapper;

    private SalaryServiceImpl salaryService;

    private SalaryCreateDTO createDTO;
    private SalaryUpdateDTO updateDTO;
    private SalaryEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        salaryService = spy(new SalaryServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(salaryService, salaryMapper);

        createDTO = new SalaryCreateDTO();
        createDTO.setEmployeeId(1L);
        createDTO.setBaseSalary(new BigDecimal("10000.00"));
        createDTO.setAllowance(new BigDecimal("2000.00"));
        createDTO.setDeduction(new BigDecimal("500.00"));
        createDTO.setSalaryMonth("2026-06");

        updateDTO = new SalaryUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setBaseSalary(new BigDecimal("12000.00"));
        updateDTO.setAllowance(new BigDecimal("2500.00"));
        updateDTO.setDeduction(new BigDecimal("600.00"));

        existingEntity = new SalaryEntity();
        existingEntity.setId(1L);
        existingEntity.setEmployeeId(1L);
        existingEntity.setBaseSalary(new BigDecimal("10000.00"));
        existingEntity.setAllowance(new BigDecimal("2000.00"));
        existingEntity.setDeduction(new BigDecimal("500.00"));
        existingEntity.setNetSalary(new BigDecimal("11500.00"));
        existingEntity.setSalaryMonth("2026-06");
        existingEntity.setEnableFlag(true);
    }

    @AfterEach
    void tearDown() {
        reset(salaryMapper);
    }

    // ==================== 5.1 create正常数据 ====================

    @Nested
    @DisplayName("create 新增薪资记录")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateSalarySuccessfully() {
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getEmployeeId(), result.getEmployeeId());
            assertEquals(createDTO.getBaseSalary(), result.getBaseSalary());
            assertEquals(createDTO.getAllowance(), result.getAllowance());
            assertEquals(createDTO.getDeduction(), result.getDeduction());
            assertEquals(new BigDecimal("11500.00"), result.getNetSalary());
            assertEquals(createDTO.getSalaryMonth(), result.getSalaryMonth());

            ArgumentCaptor<SalaryEntity> captor = ArgumentCaptor.forClass(SalaryEntity.class);
            verify(salaryMapper).insert(captor.capture());
            assertEquals(Long.valueOf(1L), captor.getValue().getEmployeeId());
            assertEquals(new BigDecimal("11500.00"), captor.getValue().getNetSalary());
        }

        // ==================== 5.2 create编码重复 ====================

        @Test
        @DisplayName("员工+月份重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenEmployeeMonthDuplicate() {
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> salaryService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(salaryMapper, never()).insert(any());
        }

        @Test
        @DisplayName("员工ID为null -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenEmployeeIdIsNull() {
            createDTO.setEmployeeId(null);
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getEmployeeId());
            verify(salaryMapper).insert(any());
        }

        @Test
        @DisplayName("薪资月份为空字符串 -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenSalaryMonthIsEmpty() {
            createDTO.setSalaryMonth("");
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertEquals("", result.getSalaryMonth());
            verify(salaryMapper).insert(any());
        }

        // ==================== 5.3 create必填字段缺失(注解验证) ====================

        @Test
        @DisplayName("create参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnCreateParam() throws NoSuchMethodException {
            var method = ISalaryService.class.getMethod("create", SalaryCreateDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0, "create应有参数");
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid, "create参数应标注@Valid");
        }

        @Test
        @DisplayName("基本工资为0 -> 边界值正常创建")
        void shouldCreateWithZeroBaseSalary() {
            createDTO.setBaseSalary(BigDecimal.ZERO);
            createDTO.setAllowance(BigDecimal.ZERO);
            createDTO.setDeduction(BigDecimal.ZERO);
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertEquals(0, BigDecimal.ZERO.compareTo(result.getNetSalary()));
        }

        @Test
        @DisplayName("净薪资计算：baseSalary+allowance-deduction")
        void shouldCalculateNetSalaryCorrectly() {
            createDTO.setBaseSalary(new BigDecimal("8000.00"));
            createDTO.setAllowance(new BigDecimal("1500.00"));
            createDTO.setDeduction(new BigDecimal("300.00"));
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("9200.00"), result.getNetSalary());
        }

        @Test
        @DisplayName("净薪资计算：null字段视为0")
        void shouldTreatNullFieldsAsZeroInNetSalaryCalc() {
            createDTO.setBaseSalary(null);
            createDTO.setAllowance(null);
            createDTO.setDeduction(null);
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.getNetSalary());
        }
    }

    // ==================== 5.4 update正常数据 ====================

    @Nested
    @DisplayName("update 更新薪资记录")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateSalarySuccessfully() {
            when(salaryMapper.selectById(1L)).thenReturn(existingEntity);
            when(salaryMapper.updateById(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getBaseSalary(), result.getBaseSalary());
            assertEquals(updateDTO.getAllowance(), result.getAllowance());
            assertEquals(updateDTO.getDeduction(), result.getDeduction());
            assertEquals(new BigDecimal("13900.00"), result.getNetSalary());

            ArgumentCaptor<SalaryEntity> captor = ArgumentCaptor.forClass(SalaryEntity.class);
            verify(salaryMapper).updateById(captor.capture());
            assertEquals(Long.valueOf(1L), captor.getValue().getId());
            assertEquals(new BigDecimal("12000.00"), captor.getValue().getBaseSalary());
        }

        // ==================== 5.5 update不存在ID ====================

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(salaryMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> salaryService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(salaryMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时净薪资重新计算")
        void shouldRecalculateNetSalaryOnUpdate() {
            when(salaryMapper.selectById(1L)).thenReturn(existingEntity);
            when(salaryMapper.updateById(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("13900.00"), result.getNetSalary());
        }
    }

    // ==================== 5.6 & 5.7 delete ====================

    @Nested
    @DisplayName("delete 删除薪资记录")
    class DeleteTests {

        @Test
        @DisplayName("无关联数据 -> 软删除成功")
        void shouldSoftDeleteSuccessfully() {
            when(salaryMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(salaryService).removeById(1L);

            assertDoesNotThrow(() -> salaryService.delete(1L));
            verify(salaryService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(salaryMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> salaryService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(salaryMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("关联校验: 当前实现仅检查存在性 -> 不检查关联数据(已知限制)")
        void shouldNoteThatDeleteDoesNotCheckRelatedData() {
            when(salaryMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(salaryService).removeById(1L);

            assertDoesNotThrow(() -> salaryService.delete(1L));
        }
    }

    // ==================== 5.8 pageList ====================

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            SalaryQueryDTO query = new SalaryQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<SalaryEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(salaryMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<SalaryEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<SalaryVO> result = salaryService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getEmployeeId(), result.getList().get(0).getEmployeeId());
        }

        @Test
        @DisplayName("按员工ID筛选 -> 返回匹配结果")
        void shouldFilterByEmployeeId() {
            SalaryQueryDTO query = new SalaryQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setEmployeeId(1L);

            IPage<SalaryEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(salaryMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<SalaryEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<SalaryVO> result = salaryService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按薪资月份筛选 -> 返回匹配结果")
        void shouldFilterBySalaryMonth() {
            SalaryQueryDTO query = new SalaryQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setSalaryMonth("2026-06");

            IPage<SalaryEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(salaryMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<SalaryEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<SalaryVO> result = salaryService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            SalaryQueryDTO query = new SalaryQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setEmployeeId(1L);
            query.setSalaryMonth("2026-06");

            IPage<SalaryEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(salaryMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<SalaryEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<SalaryVO> result = salaryService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            SalaryQueryDTO query = new SalaryQueryDTO();

            IPage<SalaryEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(salaryMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<SalaryEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<SalaryVO> result = salaryService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("默认分页参数(pageNum=null, pageSize=null) -> 使用默认值")
        void shouldUseDefaultPaginationWhenNull() {
            SalaryQueryDTO query = new SalaryQueryDTO();

            IPage<SalaryEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(salaryMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<SalaryEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> salaryService.pageList(query));
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById 查询薪资详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(salaryMapper.selectById(1L)).thenReturn(existingEntity);

            SalaryVO result = salaryService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getEmployeeId(), result.getEmployeeId());
            assertEquals(existingEntity.getBaseSalary(), result.getBaseSalary());
            assertEquals(existingEntity.getAllowance(), result.getAllowance());
            assertEquals(existingEntity.getDeduction(), result.getDeduction());
            assertEquals(existingEntity.getNetSalary(), result.getNetSalary());
            assertEquals(existingEntity.getSalaryMonth(), result.getSalaryMonth());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(salaryMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> salaryService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== 5.9 事务回滚 ====================

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = SalaryServiceImpl.class.getMethod("create", SalaryCreateDTO.class);
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
            var method = SalaryServiceImpl.class.getMethod("update", Long.class, SalaryUpdateDTO.class);
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
            var method = SalaryServiceImpl.class.getMethod("delete", Long.class);
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
            var method = SalaryServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = SalaryServiceImpl.class.getMethod("pageList", SalaryQueryDTO.class);
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
        @DisplayName("高精度薪资(4位小数) -> 正常创建")
        void shouldCreateWithHighPrecisionSalary() {
            createDTO.setBaseSalary(new BigDecimal("12345.6789"));
            createDTO.setAllowance(new BigDecimal("2345.6789"));
            createDTO.setDeduction(new BigDecimal("345.6789"));
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("14345.6789"), result.getNetSalary());
        }

        @Test
        @DisplayName("超大金额薪资 -> 正常创建")
        void shouldCreateWithLargeSalary() {
            createDTO.setBaseSalary(new BigDecimal("99999999.99"));
            createDTO.setAllowance(BigDecimal.ZERO);
            createDTO.setDeduction(BigDecimal.ZERO);
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("99999999.99"), result.getNetSalary());
        }

        @Test
        @DisplayName("扣款大于薪资+津贴 -> 净薪资为负")
        void shouldAllowNegativeNetSalary() {
            createDTO.setBaseSalary(new BigDecimal("1000.00"));
            createDTO.setAllowance(BigDecimal.ZERO);
            createDTO.setDeduction(new BigDecimal("2000.00"));
            when(salaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(salaryMapper.insert(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.create(createDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("-1000.00"), result.getNetSalary());
        }

        @Test
        @DisplayName("update时可选字段为null -> 正常更新")
        void shouldUpdateWithNullOptionalFields() {
            updateDTO.setAllowance(null);
            updateDTO.setDeduction(null);
            when(salaryMapper.selectById(1L)).thenReturn(existingEntity);
            when(salaryMapper.updateById(any(SalaryEntity.class))).thenReturn(1);

            SalaryVO result = salaryService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("12000.00"), result.getNetSalary());
        }
    }

    // ==================== toVO 实体转换 ====================

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(salaryMapper.selectById(1L)).thenReturn(existingEntity);

            SalaryVO result = salaryService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getEmployeeId(), result.getEmployeeId());
            assertEquals(existingEntity.getBaseSalary(), result.getBaseSalary());
            assertEquals(existingEntity.getAllowance(), result.getAllowance());
            assertEquals(existingEntity.getDeduction(), result.getDeduction());
            assertEquals(existingEntity.getNetSalary(), result.getNetSalary());
            assertEquals(existingEntity.getSalaryMonth(), result.getSalaryMonth());
        }
    }
}
