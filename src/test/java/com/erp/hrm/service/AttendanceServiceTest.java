package com.erp.hrm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.dto.AttendanceCreateDTO;
import com.erp.hrm.dto.AttendanceQueryDTO;
import com.erp.hrm.dto.AttendanceUpdateDTO;
import com.erp.hrm.entity.AttendanceEntity;
import com.erp.hrm.mapper.AttendanceMapper;
import com.erp.hrm.service.impl.AttendanceServiceImpl;
import com.erp.hrm.vo.AttendanceVO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService 单元测试")
class AttendanceServiceTest {

    @Mock
    private AttendanceMapper attendanceMapper;

    private AttendanceServiceImpl attendanceService;

    private AttendanceCreateDTO createDTO;
    private AttendanceUpdateDTO updateDTO;
    private AttendanceEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        attendanceService = spy(new AttendanceServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(attendanceService, attendanceMapper);

        createDTO = new AttendanceCreateDTO();
        createDTO.setEmployeeId(1L);
        createDTO.setAttendanceDate(LocalDate.of(2026, 6, 8));
        createDTO.setCheckInTime(LocalDateTime.of(2026, 6, 8, 9, 0));
        createDTO.setCheckOutTime(LocalDateTime.of(2026, 6, 8, 18, 0));
        createDTO.setWorkHours(new BigDecimal("8.0"));
        createDTO.setAttendanceType("normal");
        createDTO.setOvertimeHours(BigDecimal.ZERO);

        updateDTO = new AttendanceUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setEmployeeId(1L);
        updateDTO.setAttendanceDate(LocalDate.of(2026, 6, 9));
        updateDTO.setCheckInTime(LocalDateTime.of(2026, 6, 9, 8, 30));
        updateDTO.setCheckOutTime(LocalDateTime.of(2026, 6, 9, 17, 30));
        updateDTO.setWorkHours(new BigDecimal("8.0"));
        updateDTO.setAttendanceType("late");
        updateDTO.setOvertimeHours(new BigDecimal("1.5"));

        existingEntity = new AttendanceEntity();
        existingEntity.setId(1L);
        existingEntity.setEmployeeId(1L);
        existingEntity.setAttendanceDate(LocalDate.of(2026, 6, 8));
        existingEntity.setCheckInTime(LocalDateTime.of(2026, 6, 8, 9, 0));
        existingEntity.setCheckOutTime(LocalDateTime.of(2026, 6, 8, 18, 0));
        existingEntity.setWorkHours(new BigDecimal("8.0"));
        existingEntity.setAttendanceType("normal");
        existingEntity.setOvertimeHours(BigDecimal.ZERO);
    }

    @AfterEach
    void tearDown() {
        reset(attendanceMapper, attendanceService);
    }

    // ==================== 5.1 create正常数据 ====================

    @Nested
    @DisplayName("create 新增考勤记录")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateAttendanceSuccessfully() {
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.insert(any(AttendanceEntity.class))).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getEmployeeId(), result.getEmployeeId());
            assertEquals(createDTO.getAttendanceDate(), result.getAttendanceDate());
            assertEquals(createDTO.getCheckInTime(), result.getCheckInTime());
            assertEquals(createDTO.getCheckOutTime(), result.getCheckOutTime());
            assertEquals(createDTO.getWorkHours(), result.getWorkHours());
            assertEquals(createDTO.getAttendanceType(), result.getAttendanceType());
            assertEquals(createDTO.getOvertimeHours(), result.getOvertimeHours());

            ArgumentCaptor<AttendanceEntity> captor = ArgumentCaptor.forClass(AttendanceEntity.class);
            verify(attendanceMapper).insert(captor.capture());
            assertEquals(1L, captor.getValue().getEmployeeId());
            assertEquals(LocalDate.of(2026, 6, 8), captor.getValue().getAttendanceDate());
        }

        // ==================== 5.2 create编码重复(员工+日期唯一性) ====================

        @Test
        @DisplayName("员工+日期重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenAttendanceDateDuplicate() {
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> attendanceService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(attendanceMapper, never()).insert(any());
        }

        @Test
        @DisplayName("employeeId为null -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenEmployeeIdIsNull() {
            createDTO.setEmployeeId(null);
            when(attendanceMapper.insert(any(AttendanceEntity.class))).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getEmployeeId());
            verify(attendanceMapper).insert(any());
        }

        @Test
        @DisplayName("attendanceDate为null -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenAttendanceDateIsNull() {
            createDTO.setAttendanceDate(null);
            when(attendanceMapper.insert(any(AttendanceEntity.class))).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getAttendanceDate());
            verify(attendanceMapper).insert(any());
        }

        // ==================== 5.3 create必填字段缺失(注解验证) ====================

        @Test
        @DisplayName("create参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnCreateParam() throws NoSuchMethodException {
            var method = IAttendanceService.class.getMethod("create", AttendanceCreateDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0, "create应有参数");
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid, "create参数应标注@Valid");
        }

        @Test
        @DisplayName("employeeId标注@NotNull -> 必填约束存在")
        void shouldHaveNotNullOnEmployeeId() throws NoSuchFieldException {
            var field = AttendanceCreateDTO.class.getDeclaredField("employeeId");
            var annotation = field.getAnnotation(jakarta.validation.constraints.NotNull.class);
            assertNotNull(annotation, "employeeId应标注@NotNull");
        }

        @Test
        @DisplayName("attendanceDate标注@NotNull -> 必填约束存在")
        void shouldHaveNotNullOnAttendanceDate() throws NoSuchFieldException {
            var field = AttendanceCreateDTO.class.getDeclaredField("attendanceDate");
            var annotation = field.getAnnotation(jakarta.validation.constraints.NotNull.class);
            assertNotNull(annotation, "attendanceDate应标注@NotNull");
        }
    }

    // ==================== 5.4 update正常数据 ====================

    @Nested
    @DisplayName("update 更新考勤记录")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateAttendanceSuccessfully() {
            when(attendanceMapper.selectById(1L)).thenReturn(existingEntity);
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.updateById(any(AttendanceEntity.class))).thenReturn(1);

            AttendanceVO result = attendanceService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getEmployeeId(), result.getEmployeeId());
            assertEquals(updateDTO.getAttendanceDate(), result.getAttendanceDate());
            assertEquals(updateDTO.getCheckInTime(), result.getCheckInTime());
            assertEquals(updateDTO.getAttendanceType(), result.getAttendanceType());

            ArgumentCaptor<AttendanceEntity> captor = ArgumentCaptor.forClass(AttendanceEntity.class);
            verify(attendanceMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals(LocalDate.of(2026, 6, 9), captor.getValue().getAttendanceDate());
            assertEquals("late", captor.getValue().getAttendanceType());
        }

        // ==================== 5.5 update不存在ID ====================

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(attendanceMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> attendanceService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(attendanceMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("员工+日期重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenAttendanceDateDuplicateExcludingSelf() {
            when(attendanceMapper.selectById(1L)).thenReturn(existingEntity);
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> attendanceService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(attendanceMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时日期不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameDateWhenUpdatingSelf() {
            updateDTO.setAttendanceDate(LocalDate.of(2026, 6, 8));
            when(attendanceMapper.selectById(1L)).thenReturn(existingEntity);
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.updateById(any())).thenReturn(1);

            AttendanceVO result = attendanceService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(LocalDate.of(2026, 6, 8), result.getAttendanceDate());
            verify(attendanceMapper).updateById(any());
        }
    }

    // ==================== 5.6 & 5.7 delete ====================

    @Nested
    @DisplayName("delete 删除考勤记录")
    class DeleteTests {

        @Test
        @DisplayName("无关联数据 -> 软删除成功(is_deleted=true)")
        void shouldSoftDeleteSuccessfully() {
            when(attendanceMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(attendanceService).removeById(1L);

            assertDoesNotThrow(() -> attendanceService.delete(1L));
            verify(attendanceService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(attendanceMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> attendanceService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(attendanceMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("关联数据校验: 当前实现仅检查存在性 -> 不检查关联数据(已知限制)")
        void shouldNoteThatDeleteDoesNotCheckRelatedData() {
            when(attendanceMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(attendanceService).removeById(1L);

            assertDoesNotThrow(() -> attendanceService.delete(1L));
        }
    }

    // ==================== 5.8 pageList ====================

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<AttendanceEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AttendanceVO> result = attendanceService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getEmployeeId(), result.getList().get(0).getEmployeeId());
            assertEquals(existingEntity.getAttendanceDate(), result.getList().get(0).getAttendanceDate());
        }

        @Test
        @DisplayName("按员工ID筛选 -> 返回匹配结果")
        void shouldFilterByEmployeeId() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setEmployeeId(1L);

            IPage<AttendanceEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AttendanceVO> result = attendanceService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按考勤日期筛选 -> 返回匹配结果")
        void shouldFilterByAttendanceDate() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAttendanceDate(LocalDate.of(2026, 6, 8));

            IPage<AttendanceEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AttendanceVO> result = attendanceService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按考勤类型筛选 -> 返回匹配结果")
        void shouldFilterByAttendanceType() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAttendanceType("normal");

            IPage<AttendanceEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AttendanceVO> result = attendanceService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setEmployeeId(1L);
            query.setAttendanceDate(LocalDate.of(2026, 6, 8));
            query.setAttendanceType("normal");

            IPage<AttendanceEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AttendanceVO> result = attendanceService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();

            IPage<AttendanceEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<AttendanceVO> result = attendanceService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("默认分页参数(pageNum=null, pageSize=null) -> 使用默认值")
        void shouldUseDefaultPaginationWhenNull() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();

            IPage<AttendanceEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> attendanceService.pageList(query));
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById 查询考勤详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(attendanceMapper.selectById(1L)).thenReturn(existingEntity);

            AttendanceVO result = attendanceService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getEmployeeId(), result.getEmployeeId());
            assertEquals(existingEntity.getAttendanceDate(), result.getAttendanceDate());
            assertEquals(existingEntity.getCheckInTime(), result.getCheckInTime());
            assertEquals(existingEntity.getCheckOutTime(), result.getCheckOutTime());
            assertEquals(existingEntity.getWorkHours(), result.getWorkHours());
            assertEquals(existingEntity.getAttendanceType(), result.getAttendanceType());
            assertEquals(existingEntity.getOvertimeHours(), result.getOvertimeHours());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(attendanceMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> attendanceService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== 5.9 事务回滚验证 ====================

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = AttendanceServiceImpl.class.getMethod("create", AttendanceCreateDTO.class);
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
            var method = AttendanceServiceImpl.class.getMethod("update", Long.class, AttendanceUpdateDTO.class);
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
            var method = AttendanceServiceImpl.class.getMethod("delete", Long.class);
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
            var method = AttendanceServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = AttendanceServiceImpl.class.getMethod("pageList", AttendanceQueryDTO.class);
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
        @DisplayName("create 所有可选字段为null -> 正常创建")
        void shouldCreateWithNullOptionalFields() {
            createDTO.setCheckInTime(null);
            createDTO.setCheckOutTime(null);
            createDTO.setWorkHours(null);
            createDTO.setAttendanceType(null);
            createDTO.setOvertimeHours(null);
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.insert(any())).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getCheckInTime());
            assertNull(result.getCheckOutTime());
            assertNull(result.getWorkHours());
            assertNull(result.getAttendanceType());
            assertNull(result.getOvertimeHours());
        }

        @Test
        @DisplayName("超大工时值 -> 正常创建")
        void shouldCreateWithLargeWorkHours() {
            createDTO.setWorkHours(new BigDecimal("24.0"));
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.insert(any())).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("24.0"), result.getWorkHours());
        }

        @Test
        @DisplayName("加班时长为小数 -> 正常创建")
        void shouldCreateWithFractionalOvertime() {
            createDTO.setOvertimeHours(new BigDecimal("3.75"));
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.insert(any())).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("3.75"), result.getOvertimeHours());
        }

        @Test
        @DisplayName("考勤类型为late(迟到) -> 正常创建")
        void shouldCreateWithLateType() {
            createDTO.setAttendanceType("late");
            createDTO.setCheckInTime(LocalDateTime.of(2026, 6, 8, 10, 30));
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.insert(any())).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertNotNull(result);
            assertEquals("late", result.getAttendanceType());
        }

        @Test
        @DisplayName("考勤类型为overtime(加班) -> 正常创建")
        void shouldCreateWithOvertimeType() {
            createDTO.setAttendanceType("overtime");
            createDTO.setOvertimeHours(new BigDecimal("4.0"));
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.insert(any())).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertNotNull(result);
            assertEquals("overtime", result.getAttendanceType());
        }

        @Test
        @DisplayName("attendanceType为空字符串 -> 查询时不添加该条件(pageList)")
        void shouldSkipEmptyAttendanceTypeInPageList() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();
            query.setAttendanceType("");

            IPage<AttendanceEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> attendanceService.pageList(query));
        }

        @Test
        @DisplayName("attendanceType为null -> 查询时不添加该条件(pageList)")
        void shouldSkipNullAttendanceTypeInPageList() {
            AttendanceQueryDTO query = new AttendanceQueryDTO();

            IPage<AttendanceEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(attendanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AttendanceEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> attendanceService.pageList(query));
        }
    }

    // ==================== toVO 实体转换 ====================

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(attendanceMapper.selectById(1L)).thenReturn(existingEntity);

            AttendanceVO result = attendanceService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getEmployeeId(), result.getEmployeeId());
            assertEquals(existingEntity.getAttendanceDate(), result.getAttendanceDate());
            assertEquals(existingEntity.getCheckInTime(), result.getCheckInTime());
            assertEquals(existingEntity.getCheckOutTime(), result.getCheckOutTime());
            assertEquals(existingEntity.getWorkHours(), result.getWorkHours());
            assertEquals(existingEntity.getAttendanceType(), result.getAttendanceType());
            assertEquals(existingEntity.getOvertimeHours(), result.getOvertimeHours());
        }

        @Test
        @DisplayName("create后VO字段与entity一致")
        void shouldHaveConsistentFieldsAfterCreate() {
            when(attendanceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(attendanceMapper.insert(any(AttendanceEntity.class))).thenReturn(1);

            AttendanceVO result = attendanceService.create(createDTO);

            assertEquals(createDTO.getEmployeeId(), result.getEmployeeId());
            assertEquals(createDTO.getAttendanceDate(), result.getAttendanceDate());
            assertEquals(createDTO.getAttendanceType(), result.getAttendanceType());
            assertEquals(createDTO.getOvertimeHours(), result.getOvertimeHours());
        }
    }
}
