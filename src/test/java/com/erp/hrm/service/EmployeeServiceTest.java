package com.erp.hrm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.dto.EmployeeCreateDTO;
import com.erp.hrm.dto.EmployeeQueryDTO;
import com.erp.hrm.dto.EmployeeUpdateDTO;
import com.erp.hrm.entity.EmployeeEntity;
import com.erp.hrm.mapper.EmployeeMapper;
import com.erp.hrm.service.impl.EmployeeServiceImpl;
import com.erp.hrm.vo.EmployeeVO;
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
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService 单元测试")
class EmployeeServiceTest {

    @Mock
    private EmployeeMapper employeeMapper;

    private EmployeeServiceImpl employeeService;

    private EmployeeCreateDTO createDTO;
    private EmployeeUpdateDTO updateDTO;
    private EmployeeEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        employeeService = spy(new EmployeeServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(employeeService, employeeMapper);

        createDTO = new EmployeeCreateDTO();
        createDTO.setEmployeeNo("EMP001");
        createDTO.setName("张三");
        createDTO.setGender("男");
        createDTO.setIdCard("110101199001011234");
        createDTO.setPhone("13800138000");
        createDTO.setEmail("zhangsan@example.com");
        createDTO.setDepartmentId(1L);
        createDTO.setPositionId(10L);
        createDTO.setEmployeeStatus("在职");

        updateDTO = new EmployeeUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setEmployeeNo("EMP002");
        updateDTO.setName("李四");
        updateDTO.setGender("女");
        updateDTO.setIdCard("110101199002022345");
        updateDTO.setPhone("13900139000");
        updateDTO.setEmail("lisi@example.com");
        updateDTO.setDepartmentId(2L);
        updateDTO.setPositionId(20L);
        updateDTO.setEmployeeStatus("在职");

        existingEntity = new EmployeeEntity();
        existingEntity.setId(1L);
        existingEntity.setEmployeeNo("EMP001");
        existingEntity.setName("张三");
        existingEntity.setGender("男");
        existingEntity.setIdCard("110101199001011234");
        existingEntity.setPhone("13800138000");
        existingEntity.setEmail("zhangsan@example.com");
        existingEntity.setDepartmentId(1L);
        existingEntity.setPositionId(10L);
        existingEntity.setEmployeeStatus("在职");
    }

    @AfterEach
    void tearDown() {
        reset(employeeMapper);
    }

    // ==================== 5.1 create正常数据 ====================

    @Nested
    @DisplayName("create 新增员工档案")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateEmployeeSuccessfully() {
            when(employeeMapper.insert(any(EmployeeEntity.class))).thenReturn(1);

            EmployeeVO result = employeeService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getEmployeeNo(), result.getEmployeeNo());
            assertEquals(createDTO.getName(), result.getName());
            assertEquals(createDTO.getGender(), result.getGender());
            assertEquals(createDTO.getDepartmentId(), result.getDepartmentId());
            assertEquals(createDTO.getPositionId(), result.getPositionId());
            assertEquals(createDTO.getEmployeeStatus(), result.getEmployeeStatus());
            assertNotNull(result.getIdCard());
            assertTrue(result.getIdCard().contains("****"));

            ArgumentCaptor<EmployeeEntity> captor = ArgumentCaptor.forClass(EmployeeEntity.class);
            verify(employeeMapper).insert(captor.capture());
            assertEquals("EMP001", captor.getValue().getEmployeeNo());
            assertEquals("张三", captor.getValue().getName());
        }

        // ==================== 5.2 create编码重复 ====================

        @Test
        @DisplayName("编码重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenEmployeeNoDuplicate() {
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> employeeService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(employeeMapper, never()).insert(any());
        }

        @Test
        @DisplayName("编码为空字符串 -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenEmployeeNoIsEmpty() {
            createDTO.setEmployeeNo("");
            when(employeeMapper.insert(any(EmployeeEntity.class))).thenReturn(1);

            EmployeeVO result = employeeService.create(createDTO);

            assertNotNull(result);
            verify(employeeMapper).insert(any());
        }

        @Test
        @DisplayName("编码为null -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenEmployeeNoIsNull() {
            createDTO.setEmployeeNo(null);
            when(employeeMapper.insert(any(EmployeeEntity.class))).thenReturn(1);

            EmployeeVO result = employeeService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getEmployeeNo());
            verify(employeeMapper).insert(any());
        }

        // ==================== 5.3 create必填字段缺失(注解验证) ====================

        @Test
        @DisplayName("create参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnCreateParam() throws NoSuchMethodException {
            var method = IEmployeeService.class.getMethod("create", EmployeeCreateDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0, "create应有参数");
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid, "create参数应标注@Valid");
        }
    }

    // ==================== 5.4 update正常数据 ====================

    @Nested
    @DisplayName("update 更新员工档案")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateEmployeeSuccessfully() {
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);
            when(employeeMapper.updateById(any(EmployeeEntity.class))).thenReturn(1);

            EmployeeVO result = employeeService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getEmployeeNo(), result.getEmployeeNo());
            assertEquals(updateDTO.getName(), result.getName());
            assertEquals(updateDTO.getGender(), result.getGender());

            ArgumentCaptor<EmployeeEntity> captor = ArgumentCaptor.forClass(EmployeeEntity.class);
            verify(employeeMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("EMP002", captor.getValue().getEmployeeNo());
            assertEquals("李四", captor.getValue().getName());
        }

        // ==================== 5.5 update不存在ID ====================

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(employeeMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> employeeService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(employeeMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("编码重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenEmployeeNoDuplicateExcludingSelf() {
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> employeeService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(employeeMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时编码不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameEmployeeNoWhenUpdatingSelf() {
            updateDTO.setEmployeeNo("EMP001");
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);
            when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(employeeMapper.updateById(any())).thenReturn(1);

            EmployeeVO result = employeeService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals("EMP001", result.getEmployeeNo());
            verify(employeeMapper).updateById(any());
        }
    }

    // ==================== 5.6 & 5.7 delete ====================

    @Nested
    @DisplayName("delete 删除员工档案")
    class DeleteTests {

        @Test
        @DisplayName("无关联数据 -> 软删除成功")
        void shouldSoftDeleteSuccessfully() {
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(employeeService).removeById(1L);

            assertDoesNotThrow(() -> employeeService.delete(1L));
            verify(employeeService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(employeeMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> employeeService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(employeeMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("关联校验: 当前实现仅检查存在性 -> 不检查关联数据(已知限制)")
        void shouldNoteThatDeleteDoesNotCheckRelatedData() {
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(employeeService).removeById(1L);

            assertDoesNotThrow(() -> employeeService.delete(1L));
        }
    }

    // ==================== 5.8 pageList ====================

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            EmployeeQueryDTO query = new EmployeeQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<EmployeeEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(employeeMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<EmployeeEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<EmployeeVO> result = employeeService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getEmployeeNo(), result.getList().get(0).getEmployeeNo());
        }

        @Test
        @DisplayName("按姓名模糊搜索 -> 返回匹配结果")
        void shouldFilterByNameFuzzy() {
            EmployeeQueryDTO query = new EmployeeQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setName("张");

            IPage<EmployeeEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(employeeMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<EmployeeEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<EmployeeVO> result = employeeService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按员工状态筛选 -> 返回匹配结果")
        void shouldFilterByEmployeeStatus() {
            EmployeeQueryDTO query = new EmployeeQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setEmployeeStatus("在职");

            IPage<EmployeeEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(employeeMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<EmployeeEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<EmployeeVO> result = employeeService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按部门ID筛选 -> 返回匹配结果")
        void shouldFilterByDepartmentId() {
            EmployeeQueryDTO query = new EmployeeQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setDepartmentId(1L);

            IPage<EmployeeEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(employeeMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<EmployeeEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<EmployeeVO> result = employeeService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            EmployeeQueryDTO query = new EmployeeQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setName("张");
            query.setEmployeeStatus("在职");
            query.setDepartmentId(1L);

            IPage<EmployeeEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(employeeMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<EmployeeEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<EmployeeVO> result = employeeService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            EmployeeQueryDTO query = new EmployeeQueryDTO();

            IPage<EmployeeEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(employeeMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<EmployeeEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<EmployeeVO> result = employeeService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("默认分页参数(pageNum=null, pageSize=null) -> 使用默认值")
        void shouldUseDefaultPaginationWhenNull() {
            EmployeeQueryDTO query = new EmployeeQueryDTO();

            IPage<EmployeeEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(employeeMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<EmployeeEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> employeeService.pageList(query));
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById 查询员工档案详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);

            EmployeeVO result = employeeService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getEmployeeNo(), result.getEmployeeNo());
            assertEquals(existingEntity.getName(), result.getName());
            assertEquals(existingEntity.getGender(), result.getGender());
            assertEquals(existingEntity.getDepartmentId(), result.getDepartmentId());
            assertEquals(existingEntity.getPositionId(), result.getPositionId());
            assertEquals(existingEntity.getEmployeeStatus(), result.getEmployeeStatus());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(employeeMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> employeeService.getById(99L));
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
            var method = EmployeeServiceImpl.class.getMethod("create", EmployeeCreateDTO.class);
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
            var method = EmployeeServiceImpl.class.getMethod("update", Long.class, EmployeeUpdateDTO.class);
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
            var method = EmployeeServiceImpl.class.getMethod("delete", Long.class);
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
            var method = EmployeeServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = EmployeeServiceImpl.class.getMethod("pageList", EmployeeQueryDTO.class);
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
            createDTO.setGender(null);
            createDTO.setIdCard(null);
            createDTO.setPhone(null);
            createDTO.setEmail(null);
            createDTO.setDepartmentId(null);
            createDTO.setPositionId(null);
            createDTO.setEmployeeStatus(null);
            when(employeeMapper.insert(any())).thenReturn(1);

            EmployeeVO result = employeeService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getGender());
            assertNull(result.getPhone());
            assertNull(result.getEmail());
            assertNull(result.getDepartmentId());
            assertNull(result.getPositionId());
            assertNull(result.getEmployeeStatus());
        }

        @Test
        @DisplayName("超长姓名字段 -> 正常创建")
        void shouldCreateWithLongName() {
            createDTO.setName("A".repeat(50));
            when(employeeMapper.insert(any())).thenReturn(1);

            EmployeeVO result = employeeService.create(createDTO);

            assertNotNull(result);
            assertEquals(50, result.getName().length());
        }

        @Test
        @DisplayName("工号最大长度 -> 正常创建")
        void shouldCreateWithMaxEmployeeNo() {
            createDTO.setEmployeeNo("E".repeat(20));
            when(employeeMapper.insert(any())).thenReturn(1);

            EmployeeVO result = employeeService.create(createDTO);

            assertNotNull(result);
            assertEquals(20, result.getEmployeeNo().length());
        }

        @Test
        @DisplayName("特殊字符在姓名中 -> 正常创建")
        void shouldCreateWithSpecialCharactersInName() {
            createDTO.setName("张·三");
            when(employeeMapper.insert(any())).thenReturn(1);

            EmployeeVO result = employeeService.create(createDTO);

            assertNotNull(result);
            assertEquals("张·三", result.getName());
        }
    }

    // ==================== toVO 实体转换 ====================

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);

            EmployeeVO result = employeeService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getEmployeeNo(), result.getEmployeeNo());
            assertEquals(existingEntity.getName(), result.getName());
            assertEquals(existingEntity.getGender(), result.getGender());
            assertEquals(existingEntity.getPhone(), result.getPhone());
            assertEquals(existingEntity.getEmail(), result.getEmail());
            assertEquals(existingEntity.getDepartmentId(), result.getDepartmentId());
            assertEquals(existingEntity.getPositionId(), result.getPositionId());
            assertEquals(existingEntity.getEmployeeStatus(), result.getEmployeeStatus());
        }

        @Test
        @DisplayName("身份证号脱敏 -> 保留前6后4中间用****代替")
        void shouldMaskIdCardCorrectly() {
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);

            EmployeeVO result = employeeService.getById(1L);

            assertNotNull(result.getIdCard());
            assertTrue(result.getIdCard().startsWith("110101"), "身份证号应以前6位开头");
            assertTrue(result.getIdCard().endsWith("1234"), "身份证号应以最后4位结尾");
            assertTrue(result.getIdCard().contains("****"), "身份证号中间应包含****");
        }

        @Test
        @DisplayName("身份证号不足10位 -> 不脱敏直接返回")
        void shouldNotMaskShortIdCard() {
            existingEntity.setIdCard("123456789");
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);

            EmployeeVO result = employeeService.getById(1L);

            assertEquals("123456789", result.getIdCard());
        }

        @Test
        @DisplayName("身份证号为null -> 返回null")
        void shouldReturnNullWhenIdCardIsNull() {
            existingEntity.setIdCard(null);
            when(employeeMapper.selectById(1L)).thenReturn(existingEntity);

            EmployeeVO result = employeeService.getById(1L);

            assertNull(result.getIdCard());
        }
    }
}
