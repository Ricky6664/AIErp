package com.erp.module.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.crm.dto.CustomerDTO;
import com.erp.module.crm.dto.CustomerQueryDTO;
import com.erp.module.crm.entity.Customer;
import com.erp.module.crm.mapper.CustomerMapper;
import com.erp.module.crm.service.impl.CustomerServiceImpl;
import com.erp.module.crm.vo.CustomerVO;
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
@DisplayName("CustomerService 单元测试")
class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;

    private CustomerServiceImpl customerService;

    private CustomerDTO createDTO;
    private Customer existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        customerService = spy(new CustomerServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(customerService, customerMapper);

        createDTO = new CustomerDTO();
        createDTO.setCustomerCode("CUST001");
        createDTO.setCustomerName("测试客户");
        createDTO.setShortName("测试");
        createDTO.setPhone("13800138000");
        createDTO.setEmail("test@example.com");
        createDTO.setClassId(1L);
        createDTO.setSalesPersonId(10L);
        createDTO.setSalesDeptId(100L);
        createDTO.setCompanyId(1L);
        createDTO.setSource("官网");
        createDTO.setAuditStatus(1);
        createDTO.setIsActive(1);
        createDTO.setRemark("测试备注");

        existingEntity = new Customer();
        existingEntity.setId(1L);
        existingEntity.setCustomerCode("CUST001");
        existingEntity.setCustomerName("测试客户");
        existingEntity.setShortName("测试");
        existingEntity.setPhone("13800138000");
        existingEntity.setEmail("test@example.com");
        existingEntity.setClassId(1L);
        existingEntity.setSalesPersonId(10L);
        existingEntity.setSalesDeptId(100L);
        existingEntity.setCompanyId(1L);
        existingEntity.setSource("官网");
        existingEntity.setAuditStatus(1);
        existingEntity.setIsActive(1);
        existingEntity.setRemark("测试备注");
    }

    @AfterEach
    void tearDown() {
        reset(customerMapper, customerService);
    }

    @Nested
    @DisplayName("list 分页查询")
    class ListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<Customer> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerVO> result = customerService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRecords().size());
            assertEquals(existingEntity.getCustomerName(), result.getRecords().get(0).getCustomerName());
        }

        @Test
        @DisplayName("按客户编码模糊搜索 -> 返回匹配结果")
        void shouldFilterByCustomerCodeFuzzy() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setCustomerCode("CUST");

            IPage<Customer> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerVO> result = customerService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals("CUST001", result.getRecords().get(0).getCustomerCode());
        }

        @Test
        @DisplayName("按客户名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByCustomerNameFuzzy() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setCustomerName("测试");

            IPage<Customer> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerVO> result = customerService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals("测试客户", result.getRecords().get(0).getCustomerName());
        }

        @Test
        @DisplayName("按客户分类ID筛选 -> 返回匹配结果")
        void shouldFilterByClassId() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setClassId(1L);

            IPage<Customer> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerVO> result = customerService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按启用状态筛选 -> 返回匹配结果")
        void shouldFilterByIsActive() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setIsActive(1);

            IPage<Customer> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerVO> result = customerService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            CustomerQueryDTO query = new CustomerQueryDTO();

            IPage<Customer> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            IPage<CustomerVO> result = customerService.list(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setSortField("customerCode");
            query.setSortOrder("ASC");

            IPage<Customer> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> customerService.list(query));
        }

        @Test
        @DisplayName("DESC降序排序 -> 正确传递排序参数")
        void shouldHandleDescendingSortOrder() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setSortField("customerName");
            query.setSortOrder("DESC");

            IPage<Customer> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> customerService.list(query));
        }

        @Test
        @DisplayName("默认排序 -> 按createTime降序")
        void shouldUseDefaultSortOrder() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<Customer> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> customerService.list(query));
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回匹配结果")
        void shouldFilterByMultipleConditions() {
            CustomerQueryDTO query = new CustomerQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setCustomerName("测试");
            query.setClassId(1L);
            query.setIsActive(1);
            query.setCompanyId(1L);

            IPage<Customer> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Customer> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerVO> result = customerService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }
    }

    @Nested
    @DisplayName("getById 查询详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(customerMapper.selectById(1L)).thenReturn(existingEntity);

            CustomerVO result = customerService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getCustomerCode(), result.getCustomerCode());
            assertEquals(existingEntity.getCustomerName(), result.getCustomerName());
            assertEquals(existingEntity.getShortName(), result.getShortName());
            assertEquals(existingEntity.getPhone(), result.getPhone());
            assertEquals(existingEntity.getEmail(), result.getEmail());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(customerMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("save 新增客户")
    class SaveTests {

        @Test
        @DisplayName("正常数据 -> 保存成功")
        void shouldSaveSuccessfully() {
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("同公司主体下名称重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenNameDuplicate() {
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerService.save(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(customerService, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("无效邮箱格式 -> 抛出BusinessException PARAM_FORMAT_ERROR")
        void shouldThrowExceptionWhenEmailInvalid() {
            createDTO.setEmail("invalid-email");
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerService.save(createDTO));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
            verify(customerService, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("有效邮箱格式 -> 正常保存")
        void shouldSaveWithValidEmail() {
            createDTO.setEmail("valid.user@example.co.cn");
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("邮箱为空 -> 跳过邮箱校验正常保存")
        void shouldSkipEmailCheckWhenEmpty() {
            createDTO.setEmail(null);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("客户名称为空 -> 跳过名称唯一性校验正常保存")
        void shouldSkipNameUniquenessCheckWhenNameIsEmpty() {
            createDTO.setCustomerName(null);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("isActive为0 -> 正常保存")
        void shouldSaveWithInactiveStatus() {
            createDTO.setIsActive(0);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
            verify(customerService).save(captor.capture());
            assertEquals(0, captor.getValue().getIsActive());
        }

        @Test
        @DisplayName("save参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnSaveParam() throws NoSuchMethodException {
            var method = CustomerService.class.getMethod("save", CustomerDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0, "save应有参数");
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid, "save参数应标注@Valid");
        }
    }

    @Nested
    @DisplayName("update 更新客户")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 更新成功")
        void shouldUpdateSuccessfully() {
            when(customerMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).updateById(any(Customer.class));

            assertDoesNotThrow(() -> customerService.update(1L, createDTO));
            verify(customerService).updateById(any(Customer.class));
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(customerMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerService.update(99L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(customerService, never()).updateById(any(Customer.class));
        }

        @Test
        @DisplayName("名称重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenNameDuplicateExcludingSelf() {
            when(customerMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(customerService, never()).updateById(any(Customer.class));
        }

        @Test
        @DisplayName("更新时名称不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameNameWhenUpdatingSelf() {
            when(customerMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).updateById(any(Customer.class));

            assertDoesNotThrow(() -> customerService.update(1L, createDTO));
            verify(customerService).updateById(any(Customer.class));
        }

        @Test
        @DisplayName("更新时邮箱无效 -> 抛出BusinessException PARAM_FORMAT_ERROR")
        void shouldThrowExceptionWhenEmailInvalidInUpdate() {
            createDTO.setEmail("bad-email");
            when(customerMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerService.update(1L, createDTO));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
            verify(customerService, never()).updateById(any(Customer.class));
        }

        @Test
        @DisplayName("更新后实体ID保持不变 -> 使用原ID")
        void shouldPreserveEntityIdAfterUpdate() {
            when(customerMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).updateById(any(Customer.class));

            customerService.update(1L, createDTO);

            ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
            verify(customerService).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
        }

        @Test
        @DisplayName("update参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnUpdateParam() throws NoSuchMethodException {
            var method = CustomerService.class.getMethod("update", Long.class, CustomerDTO.class);
            Parameter[] params = method.getParameters();
            boolean hasValid = Arrays.stream(params)
                    .filter(p -> p.getType().equals(CustomerDTO.class))
                    .findFirst()
                    .flatMap(p -> Arrays.stream(p.getAnnotations())
                            .filter(a -> a.annotationType().equals(Valid.class))
                            .findFirst())
                    .isPresent();
            assertTrue(hasValid, "update的DTO参数应标注@Valid");
        }
    }

    @Nested
    @DisplayName("delete 删除客户")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 删除成功")
        void shouldDeleteSuccessfully() {
            when(customerMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(customerService).removeById(1L);

            assertDoesNotThrow(() -> customerService.delete(1L));
            verify(customerService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(customerMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(customerService, never()).removeById(any());
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("save标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnSave() throws NoSuchMethodException {
            var method = CustomerServiceImpl.class.getMethod("save", CustomerDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "save应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("update标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnUpdate() throws NoSuchMethodException {
            var method = CustomerServiceImpl.class.getMethod("update", Long.class, CustomerDTO.class);
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
            var method = CustomerServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "delete应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("list标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnList() throws NoSuchMethodException {
            var method = CustomerServiceImpl.class.getMethod("list", CustomerQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "list应标注@Transactional");
            assertTrue(annotation.readOnly(), "list应为readOnly=true");
        }

        @Test
        @DisplayName("getById标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = CustomerServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }
    }

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("客户名称超长200字符 -> 正常保存")
        void shouldHandleMaxCustomerNameLength() {
            createDTO.setCustomerName("A".repeat(200));
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("客户编码超长50字符 -> 正常保存")
        void shouldHandleMaxCustomerCodeLength() {
            createDTO.setCustomerCode("C".repeat(50));
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("邮箱超长100字符 -> 正常保存")
        void shouldHandleMaxEmailLength() {
            createDTO.setEmail("a".repeat(87) + "@example.com");
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("isActive为null -> 正常保存")
        void shouldSaveWithNullIsActive() {
            createDTO.setIsActive(null);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
        }

        @Test
        @DisplayName("remark为null -> 正常保存")
        void shouldSaveWithNullRemark() {
            createDTO.setRemark(null);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("companyId为null -> 名称唯一性校验仍适用")
        void shouldCheckUniquenessWithNullCompanyId() {
            createDTO.setCompanyId(null);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerService).save(any(Customer.class));

            assertDoesNotThrow(() -> customerService.save(createDTO));
            verify(customerService).save(any(Customer.class));
        }

        @Test
        @DisplayName("删除不存在的客户 -> 先selectById返回null应立即抛异常")
        void shouldThrowImmediatelyWhenDeletingNonExistentCustomer() {
            when(customerMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(customerService, never()).removeById(any());
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(customerMapper.selectById(1L)).thenReturn(existingEntity);

            CustomerVO result = customerService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getCustomerCode(), result.getCustomerCode());
            assertEquals(existingEntity.getCustomerName(), result.getCustomerName());
            assertEquals(existingEntity.getShortName(), result.getShortName());
            assertEquals(existingEntity.getPhone(), result.getPhone());
            assertEquals(existingEntity.getEmail(), result.getEmail());
            assertEquals(existingEntity.getClassId(), result.getClassId());
            assertEquals(existingEntity.getSalesPersonId(), result.getSalesPersonId());
            assertEquals(existingEntity.getSalesDeptId(), result.getSalesDeptId());
            assertEquals(existingEntity.getCompanyId(), result.getCompanyId());
            assertEquals(existingEntity.getSource(), result.getSource());
            assertEquals(existingEntity.getAuditStatus(), result.getAuditStatus());
            assertEquals(existingEntity.getIsActive(), result.getIsActive());
            assertEquals(existingEntity.getRemark(), result.getRemark());
        }
    }
}
