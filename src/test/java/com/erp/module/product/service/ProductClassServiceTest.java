package com.erp.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.product.dto.ProductClassDTO;
import com.erp.module.product.dto.ProductClassQueryDTO;
import com.erp.module.product.entity.ProductClass;
import com.erp.module.product.mapper.ProductClassMapper;
import com.erp.module.product.service.impl.ProductClassServiceImpl;
import com.erp.module.product.vo.ProductClassVO;
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
@DisplayName("ProductClassService 单元测试")
class ProductClassServiceTest {

    @Mock
    private ProductClassMapper productClassMapper;

    private ProductClassServiceImpl productClassService;

    private ProductClassDTO createDTO;
    private ProductClass existingEntity;
    private ProductClass childEntity;

    @BeforeEach
    void setUp() throws Exception {
        productClassService = spy(new ProductClassServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(productClassService, productClassMapper);

        createDTO = new ProductClassDTO();
        createDTO.setClassName("电子产品");
        createDTO.setParentId(null);
        createDTO.setSortOrder(1);
        createDTO.setIsActive(true);

        existingEntity = new ProductClass();
        existingEntity.setId(1L);
        existingEntity.setClassName("电子产品");
        existingEntity.setParentId(null);
        existingEntity.setSortOrder(1);
        existingEntity.setIsActive(true);

        childEntity = new ProductClass();
        childEntity.setId(2L);
        childEntity.setClassName("手机");
        childEntity.setParentId(1L);
        childEntity.setSortOrder(1);
        childEntity.setIsActive(true);
    }

    @AfterEach
    void tearDown() {
        reset(productClassMapper);
    }

    @Nested
    @DisplayName("list 分页查询")
    class ListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<ProductClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductClassVO> result = productClassService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRecords().size());
            assertEquals(existingEntity.getClassName(), result.getRecords().get(0).getClassName());
        }

        @Test
        @DisplayName("按分类名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByClassNameFuzzy() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setClassName("电子");

            IPage<ProductClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductClassVO> result = productClassService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals("电子产品", result.getRecords().get(0).getClassName());
        }

        @Test
        @DisplayName("按父分类ID筛选 -> 返回匹配结果")
        void shouldFilterByParentId() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setParentId(1L);

            IPage<ProductClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(childEntity));
            mockPage.setTotal(1);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductClassVO> result = productClassService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按启用状态筛选 -> 返回匹配结果")
        void shouldFilterByIsActive() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setIsActive(true);

            IPage<ProductClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductClassVO> result = productClassService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();

            IPage<ProductClass> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            IPage<ProductClassVO> result = productClassService.list(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();
            query.setSortField("createTime");
            query.setSortOrder("ASC");

            IPage<ProductClass> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productClassService.list(query));
        }

        @Test
        @DisplayName("DESC降序排序 -> 正确传递排序参数")
        void shouldHandleDescendingSortOrder() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();
            query.setSortField("sortOrder");
            query.setSortOrder("DESC");

            IPage<ProductClass> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productClassService.list(query));
        }

        @Test
        @DisplayName("默认排序 -> 按sortOrder升序")
        void shouldUseDefaultSortOrder() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<ProductClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productClassService.list(query));
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回匹配结果")
        void shouldFilterByMultipleConditions() {
            ProductClassQueryDTO query = new ProductClassQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setClassName("电子");
            query.setParentId(null);
            query.setIsActive(true);

            IPage<ProductClass> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(productClassMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductClass> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductClassVO> result = productClassService.list(query);

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
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);

            ProductClassVO result = productClassService.getById(1L);

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
            when(productClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("save 新增商品分类")
    class SaveTests {

        @Test
        @DisplayName("正常数据 -> 保存成功")
        void shouldSaveSuccessfully() {
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("同级分类名称重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenClassNameDuplicate() {
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.save(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(productClassService, never()).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("排序号小于0 -> 抛出BusinessException PARAM_RANGE_ERROR")
        void shouldThrowExceptionWhenSortOrderBelowMin() {
            createDTO.setSortOrder(-1);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.save(createDTO));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("排序号大于9999 -> 抛出BusinessException PARAM_RANGE_ERROR")
        void shouldThrowExceptionWhenSortOrderAboveMax() {
            createDTO.setSortOrder(10000);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.save(createDTO));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("排序号为null -> 跳过校验正常保存")
        void shouldSkipValidationWhenSortOrderIsNull() {
            createDTO.setSortOrder(null);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("排序号边界值0 -> 正常保存")
        void shouldAllowSortOrderZero() {
            createDTO.setSortOrder(0);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("排序号边界值9999 -> 正常保存")
        void shouldAllowSortOrderMax() {
            createDTO.setSortOrder(9999);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("上级分类不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenParentNotFound() {
            createDTO.setParentId(99L);
            when(productClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.save(createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productClassService, never()).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("上级分类存在 -> 正常保存")
        void shouldSaveWhenParentExists() {
            createDTO.setParentId(1L);
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("parentId为null -> 跳过上级校验正常保存")
        void shouldSkipParentCheckWhenParentIdIsNull() {
            createDTO.setParentId(null);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("parentId为0 -> 跳过上级校验正常保存")
        void shouldSkipParentCheckWhenParentIdIsZero() {
            createDTO.setParentId(0L);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("不同父分类下同名 -> 正常保存")
        void shouldAllowSameNameUnderDifferentParent() {
            createDTO.setParentId(10L);
            when(productClassMapper.selectById(10L)).thenReturn(mock(ProductClass.class));
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("isActive为false -> 正常保存")
        void shouldSaveWithInactiveStatus() {
            createDTO.setIsActive(false);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            ArgumentCaptor<ProductClass> captor = ArgumentCaptor.forClass(ProductClass.class);
            verify(productClassService).save(captor.capture());
            assertFalse(captor.getValue().getIsActive());
        }

        @Test
        @DisplayName("save参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnSaveParam() throws NoSuchMethodException {
            var method = ProductClassService.class.getMethod("save", ProductClassDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0, "save应有参数");
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid, "save参数应标注@Valid");
        }
    }

    @Nested
    @DisplayName("update 更新商品分类")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 更新成功")
        void shouldUpdateSuccessfully() {
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).updateById(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.update(1L, createDTO));
            verify(productClassService).updateById(any(ProductClass.class));
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(productClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.update(99L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productClassService, never()).updateById(any(ProductClass.class));
        }

        @Test
        @DisplayName("名称重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenNameDuplicateExcludingSelf() {
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(productClassService, never()).updateById(any(ProductClass.class));
        }

        @Test
        @DisplayName("更新时名称不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameNameWhenUpdatingSelf() {
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).updateById(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.update(1L, createDTO));
            verify(productClassService).updateById(any(ProductClass.class));
        }

        @Test
        @DisplayName("排序号超出范围 -> 抛出BusinessException")
        void shouldThrowExceptionWhenSortOrderOutOfRange() {
            createDTO.setSortOrder(10000);
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.update(1L, createDTO));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
            verify(productClassService, never()).updateById(any(ProductClass.class));
        }

        @Test
        @DisplayName("上级分类不存在 -> 抛出BusinessException")
        void shouldThrowExceptionWhenParentNotFoundInUpdate() {
            createDTO.setParentId(99L);
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(productClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productClassService, never()).updateById(any(ProductClass.class));
        }

        @Test
        @DisplayName("更新后实体ID保持不变 -> 使用原ID")
        void shouldPreserveEntityIdAfterUpdate() {
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).updateById(any(ProductClass.class));

            productClassService.update(1L, createDTO);

            ArgumentCaptor<ProductClass> captor = ArgumentCaptor.forClass(ProductClass.class);
            verify(productClassService).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
        }

        @Test
        @DisplayName("update参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnUpdateParam() throws NoSuchMethodException {
            var method = ProductClassService.class.getMethod("update", Long.class, ProductClassDTO.class);
            Parameter[] params = method.getParameters();
            boolean hasValid = Arrays.stream(params)
                    .filter(p -> p.getType().equals(ProductClassDTO.class))
                    .findFirst()
                    .flatMap(p -> Arrays.stream(p.getAnnotations())
                            .filter(a -> a.annotationType().equals(Valid.class))
                            .findFirst())
                    .isPresent();
            assertTrue(hasValid, "update的DTO参数应标注@Valid");
        }
    }

    @Nested
    @DisplayName("delete 删除商品分类")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 删除成功")
        void shouldDeleteSuccessfully() {
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).removeById(1L);

            assertDoesNotThrow(() -> productClassService.delete(1L));
            verify(productClassService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(productClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productClassService, never()).removeById(any());
        }

        @Test
        @DisplayName("存在子分类 -> 抛出BusinessException BUSINESS_ERROR")
        void shouldThrowExceptionWhenHasChildren() {
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productClassService.delete(1L));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(productClassService, never()).removeById(any());
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("save标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnSave() throws NoSuchMethodException {
            var method = ProductClassServiceImpl.class.getMethod("save", ProductClassDTO.class);
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
            var method = ProductClassServiceImpl.class.getMethod("update", Long.class, ProductClassDTO.class);
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
            var method = ProductClassServiceImpl.class.getMethod("delete", Long.class);
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
            var method = ProductClassServiceImpl.class.getMethod("list", ProductClassQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "list应标注@Transactional");
            assertTrue(annotation.readOnly(), "list应为readOnly=true");
        }

        @Test
        @DisplayName("getById标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = ProductClassServiceImpl.class.getMethod("getById", Long.class);
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
        @DisplayName("分类名称超长100字符 -> 边界值正常")
        void shouldHandleMaxClassNameLength() {
            createDTO.setClassName("A".repeat(100));
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
            verify(productClassService).save(any(ProductClass.class));
        }

        @Test
        @DisplayName("isActive为null -> 正常保存")
        void shouldSaveWithNullIsActive() {
            createDTO.setIsActive(null);
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
        }

        @Test
        @DisplayName("空字符串分类名称 -> 唯一性校验仍适用")
        void shouldCheckUniquenessForEmptyClassName() {
            createDTO.setClassName("");
            when(productClassMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productClassService).save(any(ProductClass.class));

            assertDoesNotThrow(() -> productClassService.save(createDTO));
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(productClassMapper.selectById(1L)).thenReturn(existingEntity);

            ProductClassVO result = productClassService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getClassName(), result.getClassName());
            assertEquals(existingEntity.getParentId(), result.getParentId());
            assertEquals(existingEntity.getSortOrder(), result.getSortOrder());
            assertEquals(existingEntity.getIsActive(), result.getIsActive());
        }
    }
}
