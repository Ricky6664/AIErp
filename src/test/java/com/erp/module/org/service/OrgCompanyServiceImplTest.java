package com.erp.module.org.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.org.dto.CompanyCreateDTO;
import com.erp.module.org.dto.CompanyQueryDTO;
import com.erp.module.org.dto.CompanyUpdateDTO;
import com.erp.module.org.entity.OrgCompany;
import com.erp.module.org.entity.OrgDepartment;
import com.erp.module.org.mapper.OrgCompanyMapper;
import com.erp.module.org.mapper.OrgDepartmentMapper;
import com.erp.module.org.service.impl.OrgCompanyServiceImpl;
import com.erp.module.org.vo.CompanyDetailVO;
import com.erp.module.org.vo.CompanyListVO;
import jakarta.validation.Validation;
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
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgCompanyService 单元测试")
class OrgCompanyServiceImplTest {

    @Mock
    private OrgCompanyMapper orgCompanyMapper;

    @Mock
    private OrgDepartmentMapper orgDepartmentMapper;

    private OrgCompanyServiceImpl companyService;

    private CompanyCreateDTO createDTO;
    private CompanyUpdateDTO updateDTO;
    private OrgCompany existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        companyService = spy(new OrgCompanyServiceImpl());

        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(companyService, orgCompanyMapper);

        Field deptMapperField = OrgCompanyServiceImpl.class.getDeclaredField("departmentMapper");
        deptMapperField.setAccessible(true);
        deptMapperField.set(companyService, orgDepartmentMapper);

        createDTO = new CompanyCreateDTO();
        createDTO.setCompanyName("测试公司");
        createDTO.setCompanyShortName("测试");
        createDTO.setCreditCode("123456789012345678");
        createDTO.setLegalPerson("张三");
        createDTO.setRegisteredCapital(new BigDecimal("1000000"));
        createDTO.setAddress("测试地址");
        createDTO.setPhone("13800138000");

        updateDTO = new CompanyUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setCompanyName("新公司名称");
        updateDTO.setCompanyShortName("新简称");
        updateDTO.setCreditCode("876543210987654321");

        existingEntity = new OrgCompany();
        existingEntity.setId(1L);
        existingEntity.setCompanyName("测试公司");
        existingEntity.setCompanyShortName("测试");
        existingEntity.setCreditCode("123456789012345678");
        existingEntity.setLegalPerson("张三");
        existingEntity.setEnableFlag(true);
        existingEntity.setVersion(1);
    }

    @AfterEach
    void tearDown() {
        reset(orgCompanyMapper, orgDepartmentMapper, companyService);
    }

    @Nested
    @DisplayName("create 新增公司")
    class CreateTests {

        @Test
        @DisplayName("正常新增 -> 返回ID，save被调用")
        void test_create_success() {
            when(orgCompanyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L, 0L);
            doAnswer(inv -> {
                OrgCompany entity = inv.getArgument(0);
                entity.setId(1L);
                return true;
            }).when(companyService).save(any(OrgCompany.class));

            Long id = companyService.create(createDTO);

            assertNotNull(id);
            assertEquals(Long.valueOf(1L), id);
            ArgumentCaptor<OrgCompany> captor = ArgumentCaptor.forClass(OrgCompany.class);
            verify(companyService).save(captor.capture());
            OrgCompany saved = captor.getValue();
            assertEquals("测试公司", saved.getCompanyName());
            assertTrue(saved.getEnableFlag());
        }

        @Test
        @DisplayName("同租户下新增同名公司 -> 抛出BusinessException，message包含公司名称已存在")
        void test_create_duplicateName() {
            when(orgCompanyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> companyService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(companyService, never()).save(any());
        }

        @Test
        @DisplayName("信用代码格式不合法(少于18位) -> 抛出BusinessException")
        void test_create_invalidCreditCode() {
            createDTO.setCreditCode("12345");
            when(orgCompanyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> companyService.create(createDTO));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
            assertTrue(ex.getMessage().contains("格式不正确"));
            verify(companyService, never()).save(any());
        }

        @Test
        @DisplayName("信用代码与已有公司重复 -> 抛出BusinessException")
        void test_create_duplicateCreditCode() {
            when(orgCompanyMapper.selectCount(any(LambdaQueryWrapper.class)))
                    .thenReturn(0L)  // 名称校验通过
                    .thenReturn(1L); // 信用代码校验发现重复

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> companyService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(companyService, never()).save(any());
        }

        @Test
        @DisplayName("注册资本为负数 -> DTO校验注解捕获约束违规")
        void test_create_negativeCapital() {
            createDTO.setRegisteredCapital(new BigDecimal("-1"));
            var validator = Validation.buildDefaultValidatorFactory().getValidator();
            var violations = validator.validate(createDTO);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v ->
                    v.getMessage().contains("注册资本不能为负数")));
        }
    }

    @Nested
    @DisplayName("update 修改公司")
    class UpdateTests {

        @Test
        @DisplayName("正常修改 -> updateById被调用，新值已传递")
        void test_update_success() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingEntity);
            when(orgCompanyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L, 0L);
            when(orgCompanyMapper.updateById(any(OrgCompany.class))).thenReturn(1);

            assertDoesNotThrow(() -> companyService.update(1L, updateDTO));

            ArgumentCaptor<OrgCompany> captor = ArgumentCaptor.forClass(OrgCompany.class);
            verify(orgCompanyMapper).updateById(captor.capture());
            assertEquals("新公司名称", captor.getValue().getCompanyName());
        }

        @Test
        @DisplayName("修改为其他公司已存在的名称 -> 抛出BusinessException")
        void test_update_duplicateNameExcludeSelf() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingEntity);
            when(orgCompanyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> companyService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(orgCompanyMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("修改不存在的公司ID -> 抛出BusinessException")
        void test_update_notFound() {
            when(orgCompanyMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> companyService.update(999L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(orgCompanyMapper, never()).updateById(any());
        }
    }

    @Nested
    @DisplayName("delete 删除公司")
    class DeleteTests {

        @Test
        @DisplayName("删除无关联部门的公司 -> 删除成功")
        void test_delete_success() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingEntity);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(companyService).removeById(1L);

            assertDoesNotThrow(() -> companyService.delete(1L));

            verify(companyService).removeById(1L);
        }

        @Test
        @DisplayName("删除存在关联部门的公司 -> 抛出BusinessException")
        void test_delete_hasDepartment() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingEntity);
            when(orgDepartmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> companyService.delete(1L));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(companyService, never()).removeById(anyLong());
        }

        @Test
        @DisplayName("删除不存在的公司ID -> 抛出BusinessException")
        void test_delete_notFound() {
            when(orgCompanyMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> companyService.delete(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(companyService, never()).removeById(anyLong());
        }
    }

    @Nested
    @DisplayName("page 分页查询")
    class PageTests {

        @Test
        @DisplayName("keyword模糊搜索 -> 返回匹配公司名称或简称的结果")
        void test_page_keywordSearch() {
            CompanyQueryDTO query = new CompanyQueryDTO();
            query.setKeyword("测试");

            Page<OrgCompany> page = new Page<>(1, 20);
            page.setRecords(Arrays.asList(existingEntity));
            page.setTotal(1);

            when(orgCompanyMapper.selectPage(any(), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<CompanyListVO> result = companyService.page(query);

            assertEquals(1, result.getTotal());
            assertNotNull(result.getList());
            assertEquals("测试公司", result.getList().get(0).getCompanyName());
        }

        @Test
        @DisplayName("enabled筛选 -> 返回enableFlag=true的结果")
        void test_page_enabledFilter() {
            CompanyQueryDTO query = new CompanyQueryDTO();
            query.setEnabled(true);

            Page<OrgCompany> page = new Page<>(1, 20);
            page.setRecords(Arrays.asList(existingEntity));
            page.setTotal(1);

            when(orgCompanyMapper.selectPage(any(), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<CompanyListVO> result = companyService.page(query);

            assertEquals(1, result.getTotal());
            assertTrue(result.getList().get(0).getEnableFlag());
        }
    }

    @Nested
    @DisplayName("getById 查询公司详情")
    class GetByIdTests {

        @Test
        @DisplayName("查询存在的公司 -> 返回CompanyDetailVO")
        void test_getById_success() {
            when(orgCompanyMapper.selectById(1L)).thenReturn(existingEntity);

            CompanyDetailVO result = companyService.getById(1L);

            assertNotNull(result);
            assertEquals("测试公司", result.getCompanyName());
            assertEquals("123456789012345678", result.getCreditCode());
        }

        @Test
        @DisplayName("查询不存在的公司 -> 抛出BusinessException")
        void test_getById_notFound() {
            when(orgCompanyMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> companyService.getById(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("事务回滚验证")
    class TransactionRollbackTests {

        @Test
        @DisplayName("create过程中save抛异常 -> 异常传播，不返回ID")
        void test_transaction_rollback() {
            when(orgCompanyMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L, 0L);
            doThrow(new RuntimeException("DB error")).when(companyService).save(any(OrgCompany.class));

            assertThrows(RuntimeException.class, () -> companyService.create(createDTO));
            verify(companyService).save(any(OrgCompany.class));
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("page方法 -> 标注@Transactional(readOnly=true)")
        void test_page_hasTransactional() throws NoSuchMethodException {
            var method = OrgCompanyServiceImpl.class.getMethod("page", CompanyQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "page方法应标注@Transactional");
            assertTrue(annotation.readOnly());
        }

        @Test
        @DisplayName("create方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_create_hasTransactional() throws NoSuchMethodException {
            var method = OrgCompanyServiceImpl.class.getMethod("create", CompanyCreateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "create方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("update方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_update_hasTransactional() throws NoSuchMethodException {
            var method = OrgCompanyServiceImpl.class.getMethod("update", Long.class,
                    CompanyUpdateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "update方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("delete方法 -> 标注@Transactional(rollbackFor=Exception.class)")
        void test_delete_hasTransactional() throws NoSuchMethodException {
            var method = OrgCompanyServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);
            assertNotNull(annotation, "delete方法应标注@Transactional");
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }
    }
}
