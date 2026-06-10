package com.erp.module.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.crm.dto.CustomerClassDTO;
import com.erp.module.crm.dto.CustomerClassQueryDTO;
import com.erp.module.crm.entity.CustomerClass;
import com.erp.module.crm.mapper.CustomerClassMapper;
import com.erp.module.crm.mapper.CustomerMapper;
import com.erp.module.crm.service.impl.CustomerClassServiceImpl;
import com.erp.module.crm.vo.CustomerClassVO;
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
@DisplayName("CustomerClassService 单元测试")
class CustomerClassServiceTest {

    @Mock
    private CustomerClassMapper customerClassMapper;

    @Mock
    private CustomerMapper customerMapper;

    private CustomerClassServiceImpl customerClassService;

    private CustomerClassDTO createDTO;
    private CustomerClass existingEntity;
    private CustomerClass childEntity;

    @BeforeEach
    void setUp() throws Exception {
        customerClassService = spy(new CustomerClassServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(customerClassService, customerClassMapper);

        Field customerMapperField = CustomerClassServiceImpl.class.getDeclaredField("customerMapper");
        customerMapperField.setAccessible(true);
        customerMapperField.set(customerClassService, customerMapper);

        createDTO = new CustomerClassDTO();
        createDTO.setClassName("VIP客户");
        createDTO.setParentId(null);
        createDTO.setSortOrder(1);
        createDTO.setIsActive(1);

        existingEntity = new CustomerClass();
        existingEntity.setId(1L);
        existingEntity.setClassName("VIP客户");
        existingEntity.setParentId(null);
        existingEntity.setSortOrder(1);
        existingEntity.setIsActive(1);

        childEntity = new CustomerClass();
        childEntity.setId(2L);
        childEntity.setClassName("黄金VIP");
        childEntity.setParentId(1L);
        childEntity.setSortOrder(1);
        childEntity.setIsActive(1);
    }

    @AfterEach
    void tearDown() {
        reset(customerClassMapper, customerMapper);
    }

    @Nested
    @DisplayName("list 分页查询")
    class ListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<CustomerClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerClassVO> result = customerClassService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRecords().size());
            assertEquals(existingEntity.getClassName(), result.getRecords().get(0).getClassName());
        }

        @Test
        @DisplayName("按分类名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByClassNameFuzzy() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setClassName("VIP");

            IPage<CustomerClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerClassVO> result = customerClassService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals("VIP客户", result.getRecords().get(0).getClassName());
        }

        @Test
        @DisplayName("按父分类ID筛选 -> 返回匹配结果")
        void shouldFilterByParentId() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setParentId(1L);

            IPage<CustomerClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(childEntity));
            mockPage.setTotal(1);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerClassVO> result = customerClassService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按启用状态筛选 -> 返回匹配结果")
        void shouldFilterByIsActive() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setIsActive(1);

            IPage<CustomerClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerClassVO> result = customerClassService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();

            IPage<CustomerClass> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            IPage<CustomerClassVO> result = customerClassService.list(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();
            query.setSortField("createTime");
            query.setSortOrder("ASC");

            IPage<CustomerClass> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> customerClassService.list(query));
        }

        @Test
        @DisplayName("DESC降序排序 -> 正确传递排序参数")
        void shouldHandleDescendingSortOrder() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();
            query.setSortField("sortOrder");
            query.setSortOrder("DESC");

            IPage<CustomerClass> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> customerClassService.list(query));
        }

        @Test
        @DisplayName("默认排序 -> 按sortOrder升序")
        void shouldUseDefaultSortOrder() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<CustomerClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> customerClassService.list(query));
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回匹配结果")
        void shouldFilterByMultipleConditions() {
            CustomerClassQueryDTO query = new CustomerClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setClassName("VIP");
            query.setParentId(null);
            query.setIsActive(1);

            IPage<CustomerClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(customerClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<CustomerClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<CustomerClassVO> result = customerClassService.list(query);

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
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);

            CustomerClassVO result = customerClassService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getClassName(), result.getClassName());
            assertEquals(existingEntity.getParentId(), result.getParentId());
            assertEquals(existingEntity.getSortOrder(), result.getSortOrder());
            assertEquals(existingEntity.getIsActive(), result.getIsActive());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(customerClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("save 新增客户分类")
    class SaveTests {

        @Test
        @DisplayName("正常数据 -> 保存成功")
        void shouldSaveSuccessfully() {
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
            verify(customerClassService).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("同级分类名称重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenClassNameDuplicate() {
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.save(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(customerClassService, never()).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("上级分类不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenParentNotFound() {
            createDTO.setParentId(99L);
            when(customerClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.save(createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(customerClassService, never()).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("上级分类存在 -> 正常保存")
        void shouldSaveWhenParentExists() {
            createDTO.setParentId(1L);
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
            verify(customerClassService).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("parentId为null -> 跳过上级校验正常保存")
        void shouldSkipParentCheckWhenParentIdIsNull() {
            createDTO.setParentId(null);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
            verify(customerClassService).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("parentId为0 -> 跳过上级校验正常保存")
        void shouldSkipParentCheckWhenParentIdIsZero() {
            createDTO.setParentId(0L);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
            verify(customerClassService).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("不同父分类下同名 -> 正常保存")
        void shouldAllowSameNameUnderDifferentParent() {
            createDTO.setParentId(10L);
            when(customerClassMapper.selectById(10L)).thenReturn(mock(CustomerClass.class));
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
            verify(customerClassService).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("isActive为0 -> 正常保存")
        void shouldSaveWithInactiveStatus() {
            createDTO.setIsActive(0);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
            ArgumentCaptor<CustomerClass> captor = ArgumentCaptor.forClass(CustomerClass.class);
            verify(customerClassService).save(captor.capture());
            assertEquals(0, captor.getValue().getIsActive());
        }

        @Test
        @DisplayName("save参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnSaveParam() throws NoSuchMethodException {
            var method = CustomerClassService.class.getMethod("save", CustomerClassDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0, "save应有参数");
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid, "save参数应标注@Valid");
        }
    }

    @Nested
    @DisplayName("update 更新客户分类")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 更新成功")
        void shouldUpdateSuccessfully() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).updateById(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.update(1L, createDTO));
            verify(customerClassService).updateById(any(CustomerClass.class));
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(customerClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.update(99L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(customerClassService, never()).updateById(any(CustomerClass.class));
        }

        @Test
        @DisplayName("名称重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenNameDuplicateExcludingSelf() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(customerClassService, never()).updateById(any(CustomerClass.class));
        }

        @Test
        @DisplayName("更新时名称不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameNameWhenUpdatingSelf() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).updateById(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.update(1L, createDTO));
            verify(customerClassService).updateById(any(CustomerClass.class));
        }

        @Test
        @DisplayName("上级分类不存在 -> 抛出BusinessException")
        void shouldThrowExceptionWhenParentNotFoundInUpdate() {
            createDTO.setParentId(99L);
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(customerClassService, never()).updateById(any(CustomerClass.class));
        }

        @Test
        @DisplayName("更新后实体ID保持不变 -> 使用原ID")
        void shouldPreserveEntityIdAfterUpdate() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).updateById(any(CustomerClass.class));

            customerClassService.update(1L, createDTO);

            ArgumentCaptor<CustomerClass> captor = ArgumentCaptor.forClass(CustomerClass.class);
            verify(customerClassService).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
        }

        @Test
        @DisplayName("update参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnUpdateParam() throws NoSuchMethodException {
            var method = CustomerClassService.class.getMethod("update", Long.class, CustomerClassDTO.class);
            Parameter[] params = method.getParameters();
            boolean hasValid = Arrays.stream(params)
                    .filter(p -> p.getType().equals(CustomerClassDTO.class))
                    .findFirst()
                    .flatMap(p -> Arrays.stream(p.getAnnotations())
                            .filter(a -> a.annotationType().equals(Valid.class))
                            .findFirst())
                    .isPresent();
            assertTrue(hasValid, "update的DTO参数应标注@Valid");
        }
    }

    @Nested
    @DisplayName("delete 删除客户分类")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 删除成功")
        void shouldDeleteSuccessfully() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).removeById(1L);

            assertDoesNotThrow(() -> customerClassService.delete(1L));
            verify(customerClassService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(customerClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(customerClassService, never()).removeById(any());
        }

        @Test
        @DisplayName("存在子分类 -> 抛出BusinessException BUSINESS_ERROR")
        void shouldThrowExceptionWhenHasChildren() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.delete(1L));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(customerClassService, never()).removeById(any());
        }

        @Test
        @DisplayName("存在关联客户 -> 抛出BusinessException BUSINESS_ERROR")
        void shouldThrowExceptionWhenHasAssociatedCustomers() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> customerClassService.delete(1L));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(customerClassService, never()).removeById(any());
        }

        @Test
        @DisplayName("无子分类且无关联客户 -> 正常删除")
        void shouldDeleteWhenNoChildrenAndNoCustomers() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).removeById(1L);

            assertDoesNotThrow(() -> customerClassService.delete(1L));
            verify(customerClassService).removeById(1L);
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("save标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnSave() throws NoSuchMethodException {
            var method = CustomerClassServiceImpl.class.getMethod("save", CustomerClassDTO.class);
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
            var method = CustomerClassServiceImpl.class.getMethod("update", Long.class, CustomerClassDTO.class);
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
            var method = CustomerClassServiceImpl.class.getMethod("delete", Long.class);
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
            var method = CustomerClassServiceImpl.class.getMethod("list", CustomerClassQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "list应标注@Transactional");
            assertTrue(annotation.readOnly(), "list应为readOnly=true");
        }

        @Test
        @DisplayName("getById标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = CustomerClassServiceImpl.class.getMethod("getById", Long.class);
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
        @DisplayName("分类名称超长100字符 -> 正常保存")
        void shouldHandleMaxClassNameLength() {
            createDTO.setClassName("A".repeat(100));
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
            verify(customerClassService).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("isActive为null -> 正常保存")
        void shouldSaveWithNullIsActive() {
            createDTO.setIsActive(null);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
        }

        @Test
        @DisplayName("空字符串分类名称 -> 唯一性校验仍适用")
        void shouldCheckUniquenessForEmptyClassName() {
            createDTO.setClassName("");
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
        }

        @Test
        @DisplayName("sortOrder为null -> 正常保存")
        void shouldSaveWithNullSortOrder() {
            createDTO.setSortOrder(null);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).save(any(CustomerClass.class));

            assertDoesNotThrow(() -> customerClassService.save(createDTO));
            verify(customerClassService).save(any(CustomerClass.class));
        }

        @Test
        @DisplayName("delete时分类存在但子分类检查->第二次count返回0后继续检查客户")
        void shouldCheckCustomersAfterNoChildren() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(customerClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(customerClassService).removeById(1L);

            customerClassService.delete(1L);

            verify(customerMapper).selectCount(any(LambdaQueryWrapper.class));
            verify(customerClassService).removeById(1L);
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(customerClassMapper.selectById(1L)).thenReturn(existingEntity);

            CustomerClassVO result = customerClassService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getClassName(), result.getClassName());
            assertEquals(existingEntity.getParentId(), result.getParentId());
            assertEquals(existingEntity.getSortOrder(), result.getSortOrder());
            assertEquals(existingEntity.getIsActive(), result.getIsActive());
        }
    }
}
