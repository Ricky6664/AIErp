package com.erp.module.org.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.entity.EmployeeEntity;
import com.erp.hrm.mapper.EmployeeMapper;
import com.erp.module.org.dto.PositionCreateDTO;
import com.erp.module.org.dto.PositionQueryDTO;
import com.erp.module.org.dto.PositionUpdateDTO;
import com.erp.module.org.entity.OrgPosition;
import com.erp.module.org.mapper.OrgPositionMapper;
import com.erp.module.org.service.impl.OrgPositionServiceImpl;
import com.erp.module.org.vo.PositionDetailVO;
import com.erp.module.org.vo.PositionListVO;
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
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgPositionService 单元测试")
class OrgPositionServiceImplTest {

    @Mock
    private OrgPositionMapper orgPositionMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    private OrgPositionServiceImpl positionService;

    private PositionCreateDTO createDTO;
    private PositionUpdateDTO updateDTO;
    private OrgPosition existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        positionService = spy(new OrgPositionServiceImpl());

        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(positionService, orgPositionMapper);

        Field employeeMapperField = OrgPositionServiceImpl.class.getDeclaredField("employeeMapper");
        employeeMapperField.setAccessible(true);
        employeeMapperField.set(positionService, employeeMapper);

        createDTO = new PositionCreateDTO();
        createDTO.setPositionName("Java开发工程师");
        createDTO.setDepartmentId(101L);
        createDTO.setSortNo(1);
        createDTO.setEnableFlag(true);

        updateDTO = new PositionUpdateDTO();
        updateDTO.setPositionName("高级Java开发工程师");
        updateDTO.setDepartmentId(101L);
        updateDTO.setSortNo(2);
        updateDTO.setEnableFlag(true);

        existingEntity = new OrgPosition();
        existingEntity.setId(1L);
        existingEntity.setPositionName("Java开发工程师");
        existingEntity.setPositionCode("POS001");
        existingEntity.setDepartmentId(101L);
        existingEntity.setSortNo(1);
        existingEntity.setEnableFlag(true);
    }

    @AfterEach
    void tearDown() {
        reset(orgPositionMapper, employeeMapper, positionService);
    }

    @Nested
    @DisplayName("create 新增岗位")
    class CreateTests {

        @Test
        @DisplayName("正常新增 -> 返回ID，数据已持久化")
        void test_create_success() {
            doAnswer(inv -> {
                OrgPosition entity = inv.getArgument(0);
                entity.setId(1L);
                return true;
            }).when(positionService).save(any(OrgPosition.class));

            Long id = positionService.create(createDTO);

            assertNotNull(id);
            assertEquals(Long.valueOf(1L), id);
            ArgumentCaptor<OrgPosition> captor = ArgumentCaptor.forClass(OrgPosition.class);
            verify(positionService).save(captor.capture());
            OrgPosition saved = captor.getValue();
            assertEquals("Java开发工程师", saved.getPositionName());
            assertEquals(Long.valueOf(101L), saved.getDepartmentId());
            assertNotNull(saved.getPositionCode());
            assertTrue(saved.getPositionCode().startsWith("POS"));
        }

        @Test
        @DisplayName("部门ID为null -> 抛出BusinessException")
        void test_create_deptNotFound() {
            createDTO.setDepartmentId(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> positionService.create(createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(positionService, never()).save(any());
        }

        @Test
        @DisplayName("同部门下重复岗位名称 -> 抛出BusinessException")
        void test_create_duplicateName() {
            when(orgPositionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> positionService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(positionService, never()).save(any());
        }

        @Test
        @DisplayName("不同部门下同名岗位 -> 创建成功")
        void test_create_differentDept_sameName() {
            createDTO.setDepartmentId(102L);
            doAnswer(inv -> {
                OrgPosition entity = inv.getArgument(0);
                entity.setId(2L);
                return true;
            }).when(positionService).save(any(OrgPosition.class));
            when(orgPositionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            Long id = positionService.create(createDTO);

            assertNotNull(id);
            assertEquals(Long.valueOf(2L), id);
            verify(positionService).save(any(OrgPosition.class));
        }

        @Test
        @DisplayName("岗位名称为空字符串 -> 跳过唯一性校验，正常创建")
        void test_create_blankName() {
            createDTO.setPositionName("");
            doAnswer(inv -> {
                OrgPosition entity = inv.getArgument(0);
                entity.setId(3L);
                return true;
            }).when(positionService).save(any(OrgPosition.class));

            Long id = positionService.create(createDTO);

            assertNotNull(id);
            verify(positionService).save(any(OrgPosition.class));
        }
    }

    @Nested
    @DisplayName("update 修改岗位")
    class UpdateTests {

        @Test
        @DisplayName("正常修改 -> 数据已更新")
        void test_update_success() {
            when(orgPositionMapper.selectById(1L)).thenReturn(existingEntity);
            when(orgPositionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(orgPositionMapper.updateById(any(OrgPosition.class))).thenReturn(1);

            assertDoesNotThrow(() -> positionService.update(1L, updateDTO));

            ArgumentCaptor<OrgPosition> captor = ArgumentCaptor.forClass(OrgPosition.class);
            verify(orgPositionMapper).updateById(captor.capture());
            assertEquals("高级Java开发工程师", captor.getValue().getPositionName());
            assertEquals(Integer.valueOf(2), captor.getValue().getSortNo());
        }

        @Test
        @DisplayName("同部门下改为已存在的名称 -> 抛出BusinessException")
        void test_update_duplicateNameExcludeSelf() {
            when(orgPositionMapper.selectById(1L)).thenReturn(existingEntity);
            when(orgPositionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> positionService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("变更部门 -> 在新部门下做名称唯一性校验")
        void test_update_changeDept() {
            updateDTO.setDepartmentId(102L);
            when(orgPositionMapper.selectById(1L)).thenReturn(existingEntity);
            when(orgPositionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(orgPositionMapper.updateById(any(OrgPosition.class))).thenReturn(1);

            assertDoesNotThrow(() -> positionService.update(1L, updateDTO));

            ArgumentCaptor<OrgPosition> captor = ArgumentCaptor.forClass(OrgPosition.class);
            verify(orgPositionMapper).updateById(captor.capture());
            assertEquals(Long.valueOf(102L), captor.getValue().getDepartmentId());
        }

        @Test
        @DisplayName("修改不存在的岗位 -> 抛出BusinessException")
        void test_update_notFound() {
            when(orgPositionMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> positionService.update(999L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("delete 删除岗位")
    class DeleteTests {

        @Test
        @DisplayName("正常删除无关联员工 -> 删除成功")
        void test_delete_success() {
            when(orgPositionMapper.selectById(1L)).thenReturn(existingEntity);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(positionService).removeById(1L);

            assertDoesNotThrow(() -> positionService.delete(1L));

            verify(positionService).removeById(1L);
        }

        @Test
        @DisplayName("删除有关联员工的岗位 -> 抛出BusinessException")
        void test_delete_hasEmployee() {
            when(orgPositionMapper.selectById(1L)).thenReturn(existingEntity);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> positionService.delete(1L));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(positionService, never()).removeById(anyLong());
        }

        @Test
        @DisplayName("删除不存在的岗位 -> 抛出BusinessException")
        void test_delete_notFound() {
            when(orgPositionMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> positionService.delete(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("page 分页查询")
    class PageTests {

        @Test
        @DisplayName("按departmentId筛选 -> 返回指定部门岗位")
        void test_page_deptFilter() {
            PositionQueryDTO query = new PositionQueryDTO();
            query.setDepartmentId(101L);

            Page<OrgPosition> page = new Page<>(1, 20);
            page.setRecords(Arrays.asList(existingEntity));
            page.setTotal(1);

            when(orgPositionMapper.selectPage(any(), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<PositionListVO> result = positionService.page(query);

            assertEquals(1, result.getTotal());
            assertNotNull(result.getList());
        }

        @Test
        @DisplayName("keyword模糊搜索 -> 返回匹配岗位")
        void test_page_keywordSearch() {
            PositionQueryDTO query = new PositionQueryDTO();
            query.setKeyword("Java");

            Page<OrgPosition> page = new Page<>(1, 20);
            page.setRecords(Arrays.asList(existingEntity));
            page.setTotal(1);

            when(orgPositionMapper.selectPage(any(), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<PositionListVO> result = positionService.page(query);

            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("组合筛选 -> 返回同时满足条件的岗位")
        void test_page_combinedFilter() {
            PositionQueryDTO query = new PositionQueryDTO();
            query.setDepartmentId(101L);
            query.setKeyword("Java");
            query.setEnableFlag(true);

            Page<OrgPosition> page = new Page<>(1, 20);
            page.setRecords(Arrays.asList(existingEntity));
            page.setTotal(1);

            when(orgPositionMapper.selectPage(any(), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<PositionListVO> result = positionService.page(query);

            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无筛选条件 -> 返回全部岗位")
        void test_page_noFilter() {
            PositionQueryDTO query = new PositionQueryDTO();

            Page<OrgPosition> page = new Page<>(1, 20);
            page.setRecords(Collections.emptyList());
            page.setTotal(0);

            when(orgPositionMapper.selectPage(any(), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<PositionListVO> result = positionService.page(query);

            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }
    }

    @Nested
    @DisplayName("getById 查询岗位详情")
    class GetByIdTests {

        @Test
        @DisplayName("查询存在的岗位 -> 返回PositionDetailVO")
        void test_getById_success() {
            when(orgPositionMapper.selectById(1L)).thenReturn(existingEntity);

            PositionDetailVO result = positionService.getById(1L);

            assertNotNull(result);
            assertEquals("Java开发工程师", result.getPositionName());
            assertEquals("POS001", result.getPositionCode());
            assertEquals(Long.valueOf(101L), result.getDepartmentId());
        }

        @Test
        @DisplayName("查询不存在的岗位 -> 抛出BusinessException")
        void test_getById_notFound() {
            when(orgPositionMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> positionService.getById(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_create_hasTransactional() throws NoSuchMethodException {
            var method = OrgPositionServiceImpl.class.getMethod("create", PositionCreateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "create方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("update方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_update_hasTransactional() throws NoSuchMethodException {
            var method = OrgPositionServiceImpl.class.getMethod("update", Long.class,
                    PositionUpdateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "update方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("delete方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_delete_hasTransactional() throws NoSuchMethodException {
            var method = OrgPositionServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "delete方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }
    }
}
