package com.erp.module.org.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.mapper.EmployeeMapper;
import com.erp.module.org.dto.DeptCreateDTO;
import com.erp.module.org.dto.DeptQueryDTO;
import com.erp.module.org.dto.DeptUpdateDTO;
import com.erp.module.org.entity.OrgCompany;
import com.erp.module.org.entity.OrgDepartment;
import com.erp.module.org.mapper.OrgCompanyMapper;
import com.erp.module.org.mapper.OrgDepartmentMapper;
import com.erp.module.org.mapper.OrgPositionMapper;
import com.erp.module.org.service.impl.OrgDepartmentServiceImpl;
import com.erp.module.org.vo.DeptDetailVO;
import com.erp.module.org.vo.DeptListVO;
import com.erp.module.org.vo.DeptTreeVO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgDepartmentService 单元测试")
class OrgDepartmentServiceImplTest {

    @Mock
    private OrgDepartmentMapper orgDepartmentMapper;

    @Mock
    private OrgCompanyMapper orgCompanyMapper;

    @Mock
    private OrgPositionMapper orgPositionMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    private OrgDepartmentServiceImpl departmentService;

    private DeptCreateDTO createDTO;
    private DeptUpdateDTO updateDTO;
    private OrgDepartment existingDept;
    private OrgCompany existingCompany;
    private OrgDepartment childDeptA;
    private OrgDepartment childDeptB;
    private OrgDepartment childDeptC;

    @BeforeEach
    void setUp() throws Exception {
        departmentService = spy(new OrgDepartmentServiceImpl());

        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(departmentService, orgDepartmentMapper);

        Field companyMapperField = OrgDepartmentServiceImpl.class.getDeclaredField("companyMapper");
        companyMapperField.setAccessible(true);
        companyMapperField.set(departmentService, orgCompanyMapper);

        Field positionMapperField = OrgDepartmentServiceImpl.class.getDeclaredField("positionMapper");
        positionMapperField.setAccessible(true);
        positionMapperField.set(departmentService, orgPositionMapper);

        Field employeeMapperField = OrgDepartmentServiceImpl.class.getDeclaredField("employeeMapper");
        employeeMapperField.setAccessible(true);
        employeeMapperField.set(departmentService, employeeMapper);

        createDTO = new DeptCreateDTO();
        createDTO.setDeptName("技术部");
        createDTO.setCompanyId(1L);
        createDTO.setParentId(0L);
        createDTO.setDeptType("business");
        createDTO.setSortNo(1);

        updateDTO = new DeptUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setDeptName("研发中心");
        updateDTO.setCompanyId(1L);
        updateDTO.setParentId(0L);
        updateDTO.setDeptType("business");
        updateDTO.setSortNo(2);

        existingDept = new OrgDepartment();
        existingDept.setId(1L);
        existingDept.setDepartmentName("技术部");
        existingDept.setDeptCode("DEPT001");
        existingDept.setCompanyId(1L);
        existingDept.setParentId(0L);
        existingDept.setEnableFlag(true);
        existingDept.setSortNo(1);

        existingCompany = new OrgCompany();
        existingCompany.setId(1L);
        existingCompany.setCompanyName("测试总公司");

        childDeptA = new OrgDepartment();
        childDeptA.setId(1L);
        childDeptA.setDepartmentName("技术部");
        childDeptA.setDeptCode("DEPT001");
        childDeptA.setCompanyId(1L);
        childDeptA.setParentId(0L);
        childDeptA.setEnableFlag(true);
        childDeptA.setSortNo(1);

        childDeptB = new OrgDepartment();
        childDeptB.setId(2L);
        childDeptB.setDepartmentName("研发中心");
        childDeptB.setDeptCode("DEPT002");
        childDeptB.setCompanyId(1L);
        childDeptB.setParentId(1L);
        childDeptB.setEnableFlag(true);
        childDeptB.setSortNo(1);

        childDeptC = new OrgDepartment();
        childDeptC.setId(3L);
        childDeptC.setDepartmentName("前端组");
        childDeptC.setDeptCode("DEPT003");
        childDeptC.setCompanyId(1L);
        childDeptC.setParentId(2L);
        childDeptC.setEnableFlag(true);
        childDeptC.setSortNo(1);
    }

    @AfterEach
    void tearDown() {
        reset(orgDepartmentMapper, orgCompanyMapper, orgPositionMapper, employeeMapper, departmentService);
    }

    @Nested
    @DisplayName("create 新增部门")
    class CreateTests {

        @Test
        @DisplayName("正常新增 -> 返回ID，save被调用")
        void test_create_success() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingCompany);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doAnswer(inv -> {
                OrgDepartment entity = inv.getArgument(0);
                entity.setId(10L);
                return true;
            }).when(departmentService).save(any(OrgDepartment.class));

            Long id = departmentService.create(createDTO);

            assertNotNull(id);
            assertEquals(Long.valueOf(10L), id);
            ArgumentCaptor<OrgDepartment> captor = ArgumentCaptor.forClass(OrgDepartment.class);
            verify(departmentService).save(captor.capture());
            OrgDepartment saved = captor.getValue();
            assertEquals("技术部", saved.getDepartmentName());
            assertTrue(saved.getEnableFlag());
        }

        @Test
        @DisplayName("companyId不存在 -> 抛出BusinessException")
        void test_create_companyNotFound() {
            when(orgCompanyMapper.selectById(999L)).thenReturn(null);
            createDTO.setCompanyId(999L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> departmentService.create(createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(departmentService, never()).save(any());
        }

        @Test
        @DisplayName("同公司同上级下新增同名部门 -> 抛出BusinessException")
        void test_create_duplicateName() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingCompany);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> departmentService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(departmentService, never()).save(any());
        }

        @Test
        @DisplayName("不同上级下同名部门 -> 创建成功")
        void test_create_differentParent_sameName() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingCompany);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doAnswer(inv -> {
                OrgDepartment entity = inv.getArgument(0);
                entity.setId(10L);
                return true;
            }).when(departmentService).save(any(OrgDepartment.class));

            createDTO.setParentId(5L);

            Long id = departmentService.create(createDTO);

            assertNotNull(id);
            verify(departmentService).save(any(OrgDepartment.class));
        }

        @Test
        @DisplayName("parentId为0创建顶级部门 -> 创建成功")
        void test_create_topLevel() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingCompany);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doAnswer(inv -> {
                OrgDepartment entity = inv.getArgument(0);
                entity.setId(10L);
                return true;
            }).when(departmentService).save(any(OrgDepartment.class));

            createDTO.setParentId(0L);

            Long id = departmentService.create(createDTO);

            assertNotNull(id);
            ArgumentCaptor<OrgDepartment> captor = ArgumentCaptor.forClass(OrgDepartment.class);
            verify(departmentService).save(captor.capture());
            assertEquals(Long.valueOf(0L), captor.getValue().getParentId());
        }
    }

    @Nested
    @DisplayName("update 修改部门")
    class UpdateTests {

        @Test
        @DisplayName("正常修改 -> updateById被调用")
        void test_update_success() {
            when(orgDepartmentMapper.selectById(1L)).thenReturn(existingDept);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(orgDepartmentMapper.updateById(any(OrgDepartment.class))).thenReturn(1);

            assertDoesNotThrow(() -> departmentService.update(1L, updateDTO));

            ArgumentCaptor<OrgDepartment> captor = ArgumentCaptor.forClass(OrgDepartment.class);
            verify(orgDepartmentMapper).updateById(captor.capture());
            assertEquals("研发中心", captor.getValue().getDepartmentName());
        }

        @Test
        @DisplayName("parentId设为自身ID -> 抛出BusinessException")
        void test_update_circularRef_self() {
            when(orgDepartmentMapper.selectById(1L)).thenReturn(existingDept);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            updateDTO.setParentId(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> departmentService.update(1L, updateDTO));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(orgDepartmentMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("parentId设为子孙部门ID(A的parent设为C，A→B→C) -> 抛出BusinessException")
        void test_update_circularRef_descendant() {
            OrgDepartment deptA = new OrgDepartment();
            deptA.setId(1L);
            deptA.setDepartmentName("技术部");
            deptA.setCompanyId(1L);
            deptA.setParentId(0L);
            deptA.setEnableFlag(true);

            when(orgDepartmentMapper.selectById(1L)).thenReturn(deptA);
            when(orgDepartmentMapper.selectById(3L)).thenReturn(childDeptC);
            when(orgDepartmentMapper.selectById(2L)).thenReturn(childDeptB);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            updateDTO.setParentId(3L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> departmentService.update(1L, updateDTO));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
            verify(orgDepartmentMapper, never()).updateById(any());
        }
    }

    @Nested
    @DisplayName("delete 删除部门")
    class DeleteTests {

        @Test
        @DisplayName("删除无关联数据的部门 -> 删除成功")
        void test_delete_success() {
            when(orgDepartmentMapper.selectById(1L)).thenReturn(existingDept);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(orgPositionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(departmentService).removeById(1L);

            assertDoesNotThrow(() -> departmentService.delete(1L));

            verify(departmentService).removeById(1L);
        }

        @Test
        @DisplayName("删除有子部门的部门 -> 抛出BusinessException")
        void test_delete_hasChildDept() {
            when(orgDepartmentMapper.selectById(1L)).thenReturn(existingDept);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> departmentService.delete(1L));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(departmentService, never()).removeById(anyLong());
        }

        @Test
        @DisplayName("删除有关联岗位的部门 -> 抛出BusinessException")
        void test_delete_hasPosition() {
            when(orgDepartmentMapper.selectById(1L)).thenReturn(existingDept);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(orgPositionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> departmentService.delete(1L));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(departmentService, never()).removeById(anyLong());
        }

        @Test
        @DisplayName("删除有关联员工的部门 -> 抛出BusinessException")
        void test_delete_hasEmployee() {
            when(orgDepartmentMapper.selectById(1L)).thenReturn(existingDept);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(orgPositionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> departmentService.delete(1L));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(departmentService, never()).removeById(anyLong());
        }
    }

    @Nested
    @DisplayName("tree 部门树查询")
    class TreeTests {

        @Test
        @DisplayName("3级部门树 -> children嵌套正确")
        void test_tree_multiLevel() {
            when(orgDepartmentMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(childDeptA, childDeptB, childDeptC));

            List<DeptTreeVO> result = departmentService.tree(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            DeptTreeVO root = result.get(0);
            assertEquals("技术部", root.getDeptName());
            assertNotNull(root.getChildren());
            assertEquals(1, root.getChildren().size());
            DeptTreeVO level2 = root.getChildren().get(0);
            assertEquals("研发中心", level2.getDeptName());
            assertNotNull(level2.getChildren());
            assertEquals(1, level2.getChildren().size());
            DeptTreeVO level3 = level2.getChildren().get(0);
            assertEquals("前端组", level3.getDeptName());
            assertEquals(Long.valueOf(2L), level3.getParentId());
        }

        @Test
        @DisplayName("无部门的公司查询树 -> 返回空列表")
        void test_tree_emptyCompany() {
            when(orgDepartmentMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            List<DeptTreeVO> result = departmentService.tree(999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("page 分页查询")
    class PageTests {

        @Test
        @DisplayName("按companyId筛选 -> 返回结果全部属于指定公司")
        void test_page_companyFilter() {
            DeptQueryDTO query = new DeptQueryDTO();
            query.setCompanyId(1L);

            Page<OrgDepartment> page = new Page<>(1, 20);
            page.setRecords(Arrays.asList(existingDept));
            page.setTotal(1);

            when(orgDepartmentMapper.selectPage(any(), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<DeptListVO> result = departmentService.page(query);

            assertEquals(1, result.getTotal());
            assertNotNull(result.getList());
            assertEquals("技术部", result.getList().get(0).getDeptName());
        }
    }

    @Nested
    @DisplayName("getById 查询部门详情")
    class GetByIdTests {

        @Test
        @DisplayName("查询存在的部门 -> 返回DeptDetailVO")
        void test_getById_success() {
            when(orgDepartmentMapper.selectById(1L)).thenReturn(existingDept);

            DeptDetailVO result = departmentService.getById(1L);

            assertNotNull(result);
            assertEquals("技术部", result.getDeptName());
            assertEquals("DEPT001", result.getDeptCode());
        }

        @Test
        @DisplayName("查询不存在的部门 -> 抛出BusinessException")
        void test_getById_notFound() {
            when(orgDepartmentMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> departmentService.getById(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("page方法 -> 标注@Transactional(readOnly=true)")
        void test_page_hasTransactional() throws NoSuchMethodException {
            var method = OrgDepartmentServiceImpl.class.getMethod("page", DeptQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "page方法应标注@Transactional");
            assertTrue(annotation.readOnly());
        }

        @Test
        @DisplayName("tree方法 -> 标注@Transactional(readOnly=true)")
        void test_tree_hasTransactional() throws NoSuchMethodException {
            var method = OrgDepartmentServiceImpl.class.getMethod("tree", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "tree方法应标注@Transactional");
            assertTrue(annotation.readOnly());
        }

        @Test
        @DisplayName("create方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_create_hasTransactional() throws NoSuchMethodException {
            var method = OrgDepartmentServiceImpl.class.getMethod("create", DeptCreateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "create方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("update方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_update_hasTransactional() throws NoSuchMethodException {
            var method = OrgDepartmentServiceImpl.class.getMethod("update", Long.class,
                    DeptUpdateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "update方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("delete方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_delete_hasTransactional() throws NoSuchMethodException {
            var method = OrgDepartmentServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "delete方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }
    }
}
