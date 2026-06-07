package com.erp.hrm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.dto.RecruitmentCreateDTO;
import com.erp.hrm.dto.RecruitmentQueryDTO;
import com.erp.hrm.dto.RecruitmentUpdateDTO;
import com.erp.hrm.entity.RecruitmentEntity;
import com.erp.hrm.mapper.RecruitmentMapper;
import com.erp.hrm.service.impl.RecruitmentServiceImpl;
import com.erp.hrm.vo.RecruitmentVO;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecruitmentService 单元测试")
class RecruitmentServiceTest {

    @Mock
    private RecruitmentMapper recruitmentMapper;

    private RecruitmentServiceImpl recruitmentService;

    private RecruitmentCreateDTO createDTO;
    private RecruitmentUpdateDTO updateDTO;
    private RecruitmentEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        recruitmentService = spy(new RecruitmentServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(recruitmentService, recruitmentMapper);

        createDTO = new RecruitmentCreateDTO();
        createDTO.setPositionId(1L);
        createDTO.setDepartmentId(10L);
        createDTO.setRecruitNum(3);
        createDTO.setSalaryRange("10k-15k");
        createDTO.setRequirement("3年Java开发经验");
        createDTO.setDeadline(LocalDate.now().plusDays(30));

        updateDTO = new RecruitmentUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setPositionId(2L);
        updateDTO.setDepartmentId(20L);
        updateDTO.setRecruitNum(5);
        updateDTO.setSalaryRange("15k-20k");
        updateDTO.setRequirement("5年Java开发经验");
        updateDTO.setDeadline(LocalDate.now().plusDays(60));
        updateDTO.setRecruitStatus("recruiting");

        existingEntity = new RecruitmentEntity();
        existingEntity.setId(1L);
        existingEntity.setPositionId(1L);
        existingEntity.setDepartmentId(10L);
        existingEntity.setRecruitNum(3);
        existingEntity.setSalaryRange("10k-15k");
        existingEntity.setRequirement("3年Java开发经验");
        existingEntity.setRecruitStatus("recruiting");
        existingEntity.setDeadline(LocalDate.now().plusDays(30));
    }

    @AfterEach
    void tearDown() {
        reset(recruitmentMapper);
    }

    @Nested
    @DisplayName("create 新增招聘")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateRecruitmentSuccessfully() {
            when(recruitmentMapper.insert(any(RecruitmentEntity.class))).thenReturn(1);

            RecruitmentVO result = recruitmentService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getRecruitNum(), result.getRecruitNum());
            assertEquals(createDTO.getSalaryRange(), result.getSalaryRange());

            ArgumentCaptor<RecruitmentEntity> captor = ArgumentCaptor.forClass(RecruitmentEntity.class);
            verify(recruitmentMapper).insert(captor.capture());
            assertEquals(1L, captor.getValue().getPositionId());
            assertEquals(10L, captor.getValue().getDepartmentId());
            assertEquals("recruiting", captor.getValue().getRecruitStatus());
        }

        @Test
        @DisplayName("截止日期早于当前日期 -> 抛出BusinessException PARAM_RANGE_ERROR")
        void shouldThrowExceptionWhenDeadlineInPast() {
            createDTO.setDeadline(LocalDate.now().minusDays(1));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> recruitmentService.create(createDTO));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
            verify(recruitmentMapper, never()).insert(any());
        }

        @Test
        @DisplayName("截止日期为null -> 跳过校验,正常创建")
        void shouldSkipDeadlineValidationWhenNull() {
            createDTO.setDeadline(null);
            when(recruitmentMapper.insert(any(RecruitmentEntity.class))).thenReturn(1);

            RecruitmentVO result = recruitmentService.create(createDTO);

            assertNotNull(result);
            verify(recruitmentMapper).insert(any());
        }

        @Test
        @DisplayName("截止日期为今天 -> 正常创建")
        void shouldAllowDeadlineToday() {
            createDTO.setDeadline(LocalDate.now());
            when(recruitmentMapper.insert(any(RecruitmentEntity.class))).thenReturn(1);

            assertDoesNotThrow(() -> recruitmentService.create(createDTO));
            verify(recruitmentMapper).insert(any());
        }

        @Test
        @DisplayName("create参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnCreateParam() throws NoSuchMethodException {
            var method = IRecruitmentService.class.getMethod("create", RecruitmentCreateDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0);
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid);
        }

        @Test
        @DisplayName("可选字段为null -> 正常创建")
        void shouldCreateWithNullOptionalFields() {
            createDTO.setSalaryRange(null);
            createDTO.setRequirement(null);
            createDTO.setDeadline(null);
            when(recruitmentMapper.insert(any())).thenReturn(1);

            RecruitmentVO result = recruitmentService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getSalaryRange());
        }
    }

    @Nested
    @DisplayName("update 更新招聘")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateRecruitmentSuccessfully() {
            when(recruitmentMapper.selectById(1L)).thenReturn(existingEntity);
            when(recruitmentMapper.updateById(any(RecruitmentEntity.class))).thenReturn(1);

            RecruitmentVO result = recruitmentService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getRecruitNum(), result.getRecruitNum());
            assertEquals(updateDTO.getSalaryRange(), result.getSalaryRange());
            assertEquals(updateDTO.getRecruitNum(), result.getRecruitNum());

            ArgumentCaptor<RecruitmentEntity> captor = ArgumentCaptor.forClass(RecruitmentEntity.class);
            verify(recruitmentMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals(2L, captor.getValue().getPositionId());
            assertEquals(20L, captor.getValue().getDepartmentId());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(recruitmentMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> recruitmentService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(recruitmentMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("已完成状态的招聘 -> 抛出BusinessException DATA_STATUS_INVALID")
        void shouldThrowExceptionWhenStatusIsCompleted() {
            existingEntity.setRecruitStatus("completed");
            when(recruitmentMapper.selectById(1L)).thenReturn(existingEntity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> recruitmentService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(recruitmentMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("已取消状态的招聘 -> 抛出BusinessException DATA_STATUS_INVALID")
        void shouldThrowExceptionWhenStatusIsCancelled() {
            existingEntity.setRecruitStatus("cancelled");
            when(recruitmentMapper.selectById(1L)).thenReturn(existingEntity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> recruitmentService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(recruitmentMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("截止日期早于当前日期 -> 抛出BusinessException PARAM_RANGE_ERROR")
        void shouldThrowExceptionWhenDeadlineInPast() {
            updateDTO.setDeadline(LocalDate.now().minusDays(1));
            when(recruitmentMapper.selectById(1L)).thenReturn(existingEntity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> recruitmentService.update(1L, updateDTO));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
            verify(recruitmentMapper, never()).updateById(any());
        }
    }

    @Nested
    @DisplayName("delete 删除招聘")
    class DeleteTests {

        @Test
        @DisplayName("无关联数据 -> 软删除成功")
        void shouldSoftDeleteSuccessfully() {
            when(recruitmentMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(recruitmentService).removeById(1L);

            assertDoesNotThrow(() -> recruitmentService.delete(1L));
            verify(recruitmentService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(recruitmentMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> recruitmentService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(recruitmentMapper, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("getById 查询招聘详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(recruitmentMapper.selectById(1L)).thenReturn(existingEntity);

            RecruitmentVO result = recruitmentService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getRecruitNum(), result.getRecruitNum());
            assertEquals(existingEntity.getSalaryRange(), result.getSalaryRange());
            assertEquals(existingEntity.getRecruitStatus(), result.getRecruitStatus());
            assertEquals(existingEntity.getRecruitNum(), result.getRecruitNum());
            assertEquals(existingEntity.getRecruitStatus(), result.getRecruitStatus());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(recruitmentMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> recruitmentService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            RecruitmentQueryDTO query = new RecruitmentQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<RecruitmentEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(recruitmentMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<RecruitmentEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<RecruitmentVO> result = recruitmentService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getRecruitNum(), result.getList().get(0).getRecruitNum());
        }

        @Test
        @DisplayName("按部门ID筛选 -> 返回匹配结果")
        void shouldFilterByDepartmentId() {
            RecruitmentQueryDTO query = new RecruitmentQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setDepartmentId(10L);

            IPage<RecruitmentEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(recruitmentMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<RecruitmentEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<RecruitmentVO> result = recruitmentService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按招聘状态筛选 -> 返回匹配结果")
        void shouldFilterByRecruitStatus() {
            RecruitmentQueryDTO query = new RecruitmentQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setRecruitStatus("recruiting");

            IPage<RecruitmentEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(recruitmentMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<RecruitmentEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<RecruitmentVO> result = recruitmentService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            RecruitmentQueryDTO query = new RecruitmentQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setDepartmentId(10L);
            query.setPositionId(1L);
            query.setRecruitStatus("recruiting");
            query.setKeyword("Java");

            IPage<RecruitmentEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(recruitmentMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<RecruitmentEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<RecruitmentVO> result = recruitmentService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            RecruitmentQueryDTO query = new RecruitmentQueryDTO();

            IPage<RecruitmentEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(recruitmentMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<RecruitmentEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<RecruitmentVO> result = recruitmentService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("默认分页参数(pageNum=null, pageSize=null) -> 使用默认值")
        void shouldUseDefaultPaginationWhenNull() {
            RecruitmentQueryDTO query = new RecruitmentQueryDTO();

            IPage<RecruitmentEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(recruitmentMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<RecruitmentEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> recruitmentService.pageList(query));
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = RecruitmentServiceImpl.class.getMethod("create", RecruitmentCreateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class));
        }

        @Test
        @DisplayName("update标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnUpdate() throws NoSuchMethodException {
            var method = RecruitmentServiceImpl.class.getMethod("update", Long.class, RecruitmentUpdateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class));
        }

        @Test
        @DisplayName("delete标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnDelete() throws NoSuchMethodException {
            var method = RecruitmentServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class));
        }

        @Test
        @DisplayName("getById标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = RecruitmentServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.readOnly());
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = RecruitmentServiceImpl.class.getMethod("pageList", RecruitmentQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.readOnly());
        }
    }

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("招聘人数为1(最小值) -> 正常创建")
        void shouldCreateWithMinimumRecruitNum() {
            createDTO.setRecruitNum(1);
            when(recruitmentMapper.insert(any())).thenReturn(1);

            RecruitmentVO result = recruitmentService.create(createDTO);

            assertNotNull(result);
            assertEquals(1, result.getRecruitNum());
        }

        @Test
        @DisplayName("薪资范围超长文本 -> 正常创建")
        void shouldCreateWithLongSalaryRange() {
            createDTO.setSalaryRange("A".repeat(50));
            when(recruitmentMapper.insert(any())).thenReturn(1);

            RecruitmentVO result = recruitmentService.create(createDTO);

            assertNotNull(result);
            assertEquals(50, result.getSalaryRange().length());
        }

        @Test
        @DisplayName("截止日期远期(30天后) -> 正常创建")
        void shouldCreateWithFutureDeadline() {
            createDTO.setDeadline(LocalDate.now().plusDays(30));
            when(recruitmentMapper.insert(any())).thenReturn(1);

            assertDoesNotThrow(() -> recruitmentService.create(createDTO));
            verify(recruitmentMapper).insert(any());
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(recruitmentMapper.selectById(1L)).thenReturn(existingEntity);

            RecruitmentVO result = recruitmentService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getRecruitNum(), result.getRecruitNum());
            assertEquals(existingEntity.getSalaryRange(), result.getSalaryRange());
            assertEquals(existingEntity.getRecruitStatus(), result.getRecruitStatus());
            assertEquals(existingEntity.getDeadline(), result.getDeadline());
        }
    }
}
