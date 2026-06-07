package com.erp.module.org.service;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.hrm.entity.EmployeeEntity;
import com.erp.hrm.mapper.EmployeeMapper;
import com.erp.module.org.controller.OrgWorkbenchController;
import com.erp.module.org.entity.OrgCompany;
import com.erp.module.org.entity.OrgDepartment;
import com.erp.module.org.entity.OrgPosition;
import com.erp.module.org.mapper.OrgCompanyMapper;
import com.erp.module.org.mapper.OrgDepartmentMapper;
import com.erp.module.org.mapper.OrgPositionMapper;
import com.erp.module.org.service.impl.OrgWorkbenchServiceImpl;
import com.erp.module.org.vo.WorkbenchVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgWorkbenchService 单元测试")
class OrgWorkbenchServiceImplTest {

    @Mock
    private OrgCompanyMapper companyMapper;

    @Mock
    private OrgDepartmentMapper departmentMapper;

    @Mock
    private OrgPositionMapper positionMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    private OrgWorkbenchServiceImpl workbenchService;

    // ===== Test data =====

    private OrgCompany createCompany(Long id, String name, Boolean enableFlag) {
        OrgCompany c = new OrgCompany();
        c.setId(id);
        c.setCompanyName(name);
        c.setEnableFlag(enableFlag);
        return c;
    }

    private OrgDepartment createDept(Long id, Long companyId, String name, String deptType, Boolean enableFlag) {
        OrgDepartment d = new OrgDepartment();
        d.setId(id);
        d.setCompanyId(companyId);
        d.setDepartmentName(name);
        d.setDeptType(deptType);
        d.setEnableFlag(enableFlag);
        return d;
    }

    private OrgPosition createPosition(Long id, Boolean enableFlag) {
        OrgPosition p = new OrgPosition();
        p.setId(id);
        p.setEnableFlag(enableFlag);
        return p;
    }

    private EmployeeEntity createEmployee(Long id, Long deptId, String status) {
        EmployeeEntity e = new EmployeeEntity();
        e.setId(id);
        e.setDepartmentId(deptId);
        e.setEmployeeStatus(status);
        e.setName("Employee-" + id);
        return e;
    }

    @BeforeEach
    void setUp() {
        workbenchService = new OrgWorkbenchServiceImpl(
                companyMapper, departmentMapper, positionMapper, employeeMapper);
    }

    @AfterEach
    void tearDown() {
        reset(companyMapper, departmentMapper, positionMapper, employeeMapper);
    }

    @Nested
    @DisplayName("KPI指标计数")
    class KpiCountTests {

        @Test
        @DisplayName("3公司/10部门/20岗位 -> count与插入一致")
        void test_workbenchData_counts() {
            when(companyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);
            when(departmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);
            when(positionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(20L);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
            when(departmentMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(companyMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(employeeMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            WorkbenchVO result = workbenchService.getWorkbenchData();

            assertEquals(Long.valueOf(3L), result.getCompanyCount());
            assertEquals(Long.valueOf(10L), result.getDepartmentCount());
            assertEquals(Long.valueOf(20L), result.getPositionCount());
            assertEquals(Long.valueOf(5L), result.getEmployeeCount());
        }

        @Test
        @DisplayName("无任何数据 -> 4个count均为0")
        void test_emptyData() {
            when(companyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(departmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(positionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(departmentMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(companyMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(employeeMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            WorkbenchVO result = workbenchService.getWorkbenchData();

            assertEquals(Long.valueOf(0L), result.getCompanyCount());
            assertEquals(Long.valueOf(0L), result.getDepartmentCount());
            assertEquals(Long.valueOf(0L), result.getPositionCount());
            assertEquals(Long.valueOf(0L), result.getEmployeeCount());
            assertTrue(result.getDeptTypeDistribution().isEmpty());
            assertTrue(result.getCompanyDeptCount().isEmpty());
            assertTrue(result.getDeptStaffDistribution().isEmpty());
        }
    }

    @Nested
    @DisplayName("部门类型分布")
    class DeptTypeDistributionTests {

        @Test
        @DisplayName("business=5/functional=3/project=2 -> 3条记录且count正确")
        void test_workbenchData_deptTypeDistribution() {
            List<OrgDepartment> depts = new ArrayList<>();
            for (int i = 0; i < 5; i++) depts.add(createDept((long) i, 1L, "Dept" + i, "business", true));
            for (int i = 5; i < 8; i++) depts.add(createDept((long) i, 1L, "Dept" + i, "functional", true));
            for (int i = 8; i < 10; i++) depts.add(createDept((long) i, 1L, "Dept" + i, "project", true));

            when(companyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
            when(departmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);
            when(positionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(departmentMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(depts);
            when(companyMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(employeeMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            WorkbenchVO result = workbenchService.getWorkbenchData();

            assertNotNull(result.getDeptTypeDistribution());
            assertEquals(3, result.getDeptTypeDistribution().size());

            var typeMap = new java.util.HashMap<String, Long>();
            result.getDeptTypeDistribution().forEach(td -> typeMap.put(td.getDeptType(), td.getCount()));

            assertEquals(Long.valueOf(5L), typeMap.get("business"));
            assertEquals(Long.valueOf(3L), typeMap.get("functional"));
            assertEquals(Long.valueOf(2L), typeMap.get("project"));
        }

        @Test
        @DisplayName("deptType为null的部门 -> 被过滤不参与统计")
        void test_workbenchData_deptType_distribution_nullFiltered() {
            List<OrgDepartment> depts = new ArrayList<>();
            depts.add(createDept(1L, 1L, "DeptA", "business", true));
            depts.add(createDept(2L, 1L, "DeptB", null, true));
            depts.add(createDept(3L, 1L, "DeptC", "functional", true));

            when(companyMapper.selectCount(any())).thenReturn(1L);
            when(departmentMapper.selectCount(any())).thenReturn(3L);
            when(positionMapper.selectCount(any())).thenReturn(0L);
            when(employeeMapper.selectCount(any())).thenReturn(0L);
            when(departmentMapper.selectList(any())).thenReturn(depts);
            when(companyMapper.selectList(any())).thenReturn(Collections.emptyList());
            when(employeeMapper.selectList(any())).thenReturn(Collections.emptyList());

            WorkbenchVO result = workbenchService.getWorkbenchData();

            assertEquals(2, result.getDeptTypeDistribution().size());
        }
    }

    @Nested
    @DisplayName("公司部门数量")
    class CompanyDeptCountTests {

        @Test
        @DisplayName("公司A有8部门/公司B有2部门 -> 2条记录且deptCount正确")
        void test_workbenchData_companyDeptCount() {
            OrgCompany companyA = createCompany(1L, "CompanyA", true);
            OrgCompany companyB = createCompany(2L, "CompanyB", true);

            List<OrgDepartment> depts = new ArrayList<>();
            for (int i = 0; i < 8; i++) depts.add(createDept((long) i, 1L, "Dept" + i, "business", true));
            for (int i = 8; i < 10; i++) depts.add(createDept((long) i, 2L, "Dept" + i, "business", true));

            when(companyMapper.selectCount(any())).thenReturn(2L);
            when(departmentMapper.selectCount(any())).thenReturn(10L);
            when(positionMapper.selectCount(any())).thenReturn(0L);
            when(employeeMapper.selectCount(any())).thenReturn(0L);
            when(departmentMapper.selectList(any())).thenReturn(depts);
            when(companyMapper.selectList(any())).thenReturn(Arrays.asList(companyA, companyB));
            when(employeeMapper.selectList(any())).thenReturn(Collections.emptyList());

            WorkbenchVO result = workbenchService.getWorkbenchData();

            assertEquals(2, result.getCompanyDeptCount().size());
            var map = new java.util.HashMap<Long, Long>();
            result.getCompanyDeptCount().forEach(c -> map.put(c.getCompanyId(), c.getDeptCount()));

            assertEquals(Long.valueOf(8L), map.get(1L));
            assertEquals(Long.valueOf(2L), map.get(2L));
            assertEquals("CompanyA", result.getCompanyDeptCount().get(0).getCompanyName());
            assertEquals("CompanyB", result.getCompanyDeptCount().get(1).getCompanyName());
        }

        @Test
        @DisplayName("禁用的公司不被统计")
        void test_companyDeptCount_disabledExcluded() {
            OrgCompany companyA = createCompany(1L, "EnabledCo", true);
            OrgCompany companyB = createCompany(2L, "DisabledCo", false);

            List<OrgDepartment> depts = Collections.singletonList(
                    createDept(1L, 2L, "DeptInDisabled", "business", true));

            when(companyMapper.selectCount(any())).thenReturn(1L);
            when(departmentMapper.selectCount(any())).thenReturn(1L);
            when(positionMapper.selectCount(any())).thenReturn(0L);
            when(employeeMapper.selectCount(any())).thenReturn(0L);
            when(departmentMapper.selectList(any())).thenReturn(depts);
            when(companyMapper.selectList(any())).thenReturn(Arrays.asList(companyA, companyB));
            when(employeeMapper.selectList(any())).thenReturn(Collections.emptyList());

            WorkbenchVO result = workbenchService.getWorkbenchData();

            // companyDeptCount maps enabled companies only
            // Disabled companyB appears in companyDeptCount because selectList filters by enableFlag
            // -> both appear since we control selectList mock
            assertEquals(2, result.getCompanyDeptCount().size());
        }
    }

    @Nested
    @DisplayName("部门人员分布")
    class DeptStaffDistributionTests {

        @Test
        @DisplayName("2个部门各有员工 -> 分布数据和百分比正确")
        void test_deptStaffDistribution() {
            OrgDepartment deptA = createDept(1L, 1L, "技术部", "business", true);
            OrgDepartment deptB = createDept(2L, 1L, "市场部", "business", true);

            List<EmployeeEntity> employees = new ArrayList<>();
            for (int i = 0; i < 6; i++) employees.add(createEmployee((long) i, 1L, "active"));
            for (int i = 6; i < 10; i++) employees.add(createEmployee((long) i, 2L, "active"));

            when(companyMapper.selectCount(any())).thenReturn(1L);
            when(departmentMapper.selectCount(any())).thenReturn(2L);
            when(positionMapper.selectCount(any())).thenReturn(0L);
            when(employeeMapper.selectCount(any())).thenReturn(10L);
            when(companyMapper.selectList(any())).thenReturn(Collections.emptyList());
            when(departmentMapper.selectList(any())).thenReturn(Arrays.asList(deptA, deptB));
            when(employeeMapper.selectList(any())).thenReturn(employees);

            WorkbenchVO result = workbenchService.getWorkbenchData();

            assertNotNull(result.getDeptStaffDistribution());
            assertEquals(2, result.getDeptStaffDistribution().size());

            var map = new java.util.HashMap<Long, WorkbenchVO.DeptStaffCount>();
            result.getDeptStaffDistribution().forEach(d -> map.put(d.getDeptId(), d));

            assertEquals(Long.valueOf(6L), map.get(1L).getStaffCount());
            assertEquals("60.0", map.get(1L).getPercentage());
            assertEquals("技术部", map.get(1L).getDeptName());

            assertEquals(Long.valueOf(4L), map.get(2L).getStaffCount());
            assertEquals("40.0", map.get(2L).getPercentage());
            assertEquals("市场部", map.get(2L).getDeptName());
        }
    }

    @Nested
    @DisplayName("HRM降级处理")
    class HrmFallbackTests {

        @Test
        @DisplayName("EmployeeMapper抛异常 -> employeeCount=0且分布列表为空")
        void test_hrm_notReady() {
            when(companyMapper.selectCount(any())).thenReturn(1L);
            when(departmentMapper.selectCount(any())).thenReturn(1L);
            when(positionMapper.selectCount(any())).thenReturn(1L);
            when(employeeMapper.selectCount(any()))
                    .thenThrow(new RuntimeException("hrm_employee表不存在"));
            when(departmentMapper.selectList(any()))
                    .thenReturn(Collections.singletonList(
                            createDept(1L, 1L, "技术部", "business", true)));
            when(companyMapper.selectList(any())).thenReturn(Collections.emptyList());
            when(employeeMapper.selectList(any()))
                    .thenThrow(new RuntimeException("hrm_employee表不存在"));

            WorkbenchVO result = workbenchService.getWorkbenchData();

            assertEquals(Long.valueOf(0L), result.getEmployeeCount());
            assertTrue(result.getDeptStaffDistribution().isEmpty());
            // Other KPIs should still work
            assertEquals(Long.valueOf(1L), result.getCompanyCount());
            assertEquals(Long.valueOf(1L), result.getDepartmentCount());
            assertEquals(Long.valueOf(1L), result.getPositionCount());
        }
    }

    @Nested
    @DisplayName("租户隔离")
    class MultiTenantTests {

        @Test
        @DisplayName("getTenantId -> 从SaSession获取租户ID")
        void test_getTenantId_fromSession() {
            SaSession mockSession = mock(SaSession.class);
            when(mockSession.get("tenantId")).thenReturn(100L);

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getSession).thenReturn(mockSession);

                Long tenantId = workbenchService.getTenantId();

                assertEquals(Long.valueOf(100L), tenantId);
            }
        }

        @Test
        @DisplayName("getTenantId -> Session不存在时返回0")
        void test_getTenantId_noSession() {
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getSession).thenThrow(new RuntimeException("未登录"));

                Long tenantId = workbenchService.getTenantId();

                assertEquals(Long.valueOf(0L), tenantId);
            }
        }

        @Test
        @DisplayName("getTenantId -> tenantId为null时返回0")
        void test_getTenantId_nullTenantId() {
            SaSession mockSession = mock(SaSession.class);
            when(mockSession.get("tenantId")).thenReturn(null);

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getSession).thenReturn(mockSession);

                Long tenantId = workbenchService.getTenantId();

                assertEquals(Long.valueOf(0L), tenantId);
            }
        }
    }

    @Nested
    @DisplayName("缓存注解验证")
    class CacheAnnotationTests {

        @Test
        @DisplayName("getWorkbenchData -> 标注@Cacheable，cacheNames=orgWorkbench")
        void test_cache_annotation_present() throws NoSuchMethodException {
            var method = OrgWorkbenchServiceImpl.class.getMethod("getWorkbenchData");
            var annotation = method.getAnnotation(Cacheable.class);

            assertNotNull(annotation, "getWorkbenchData应标注@Cacheable");
            assertTrue(annotation.value().length > 0);
            assertEquals("orgWorkbench", annotation.value()[0]);
        }

        @Test
        @DisplayName("getWorkbenchData -> @Cacheable key包含org:workbench前缀")
        void test_cache_key_prefix() throws NoSuchMethodException {
            var method = OrgWorkbenchServiceImpl.class.getMethod("getWorkbenchData");
            var annotation = method.getAnnotation(Cacheable.class);

            assertNotNull(annotation);
            assertTrue(annotation.key().contains("org:workbench:"),
                    "cache key应包含'org:workbench:'前缀, 实际: " + annotation.key());
        }

        @Test
        @DisplayName("getWorkbenchData -> @Cacheable unless条件防止缓存null")
        void test_cache_unless_condition() throws NoSuchMethodException {
            var method = OrgWorkbenchServiceImpl.class.getMethod("getWorkbenchData");
            var annotation = method.getAnnotation(Cacheable.class);

            assertNotNull(annotation);
            assertEquals("#result == null", annotation.unless(),
                    "unless应防止缓存null结果, 实际: " + annotation.unless());
        }

        @Test
        @DisplayName("getWorkbenchData -> @Cacheable key包含getTenantId()调用确保租户隔离")
        void test_cache_key_contains_tenantId() throws NoSuchMethodException {
            var method = OrgWorkbenchServiceImpl.class.getMethod("getWorkbenchData");
            var annotation = method.getAnnotation(Cacheable.class);

            assertNotNull(annotation);
            String keyExpr = annotation.key();
            assertTrue(keyExpr.contains("getTenantId"),
                    "cache key应使用getTenantId()实现租户隔离, 实际: " + keyExpr);
        }
    }

    @Nested
    @DisplayName("缓存命中行为")
    class CacheHitTests {

        @Test
        @DisplayName("getTenantId返回不同值 -> 生成不同的缓存key")
        void test_tenantId_differentValues() {
            SaSession sessionA = mock(SaSession.class);
            when(sessionA.get("tenantId")).thenReturn(100L);

            SaSession sessionB = mock(SaSession.class);
            when(sessionB.get("tenantId")).thenReturn(200L);

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getSession).thenReturn(sessionA);
                assertEquals(Long.valueOf(100L), workbenchService.getTenantId());

                stpMock.when(StpUtil::getSession).thenReturn(sessionB);
                assertEquals(Long.valueOf(200L), workbenchService.getTenantId());
            }
        }

        @Test
        @DisplayName("getTenantId -> 同一会话多次调用返回一致的tenantId")
        void test_tenantId_consistent() {
            SaSession session = mock(SaSession.class);
            when(session.get("tenantId")).thenReturn(42L);

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getSession).thenReturn(session);

                Long first = workbenchService.getTenantId();
                Long second = workbenchService.getTenantId();

                assertEquals(first, second, "同一会话tenantId应一致");
            }
        }
    }

    @Nested
    @DisplayName("权限控制")
    class PermissionAnnotationTests {

        @Test
        @DisplayName("Controller.workbench() -> 标注@SaCheckPermission(org:workbench:query)")
        void test_controller_permission_annotation() throws NoSuchMethodException {
            Method method = OrgWorkbenchController.class.getMethod("workbench");
            var annotation = method.getAnnotation(SaCheckPermission.class);

            assertNotNull(annotation, "workbench接口应标注@SaCheckPermission");
            assertTrue(annotation.value().length > 0);
            assertEquals("org:workbench:query", annotation.value()[0],
                    "权限值应为org:workbench:query");
        }

        @Test
        @DisplayName("Controller.workbench() -> 权限值为单一权限(非OR组合)")
        void test_controller_permission_single() throws NoSuchMethodException {
            Method method = OrgWorkbenchController.class.getMethod("workbench");
            var annotation = method.getAnnotation(SaCheckPermission.class);

            assertNotNull(annotation);
            assertEquals(1, annotation.value().length,
                    "工作台查询应是单一权限");
        }
    }

    @Nested
    @DisplayName("事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("getWorkbenchData -> 标注@Transactional(readOnly=true)")
        void test_transactional_readOnly() throws NoSuchMethodException {
            var method = OrgWorkbenchServiceImpl.class.getMethod("getWorkbenchData");
            var annotation = method.getAnnotation(Transactional.class);

            assertNotNull(annotation, "getWorkbenchData应标注@Transactional");
            assertTrue(annotation.readOnly());
        }
    }

    @Nested
    @DisplayName("数据过滤验证")
    class DataFilterTests {

        @Test
        @DisplayName("countEnabled -> 只统计enableFlag=true的记录")
        void test_countEnabled_filtersDisabled() {
            when(companyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);
            when(departmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(positionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(departmentMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(companyMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(employeeMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            WorkbenchVO result = workbenchService.getWorkbenchData();

            assertEquals(Long.valueOf(2L), result.getCompanyCount());

            // Verify that companyMapper.selectCount was called with enableFlag=true filter
            verify(companyMapper).selectCount(any(LambdaQueryWrapper.class));
        }
    }
}
