package com.erp.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.product.dto.ProductDTO;
import com.erp.module.product.dto.ProductQueryDTO;
import com.erp.module.product.entity.Product;
import com.erp.module.product.entity.ProductClass;
import com.erp.module.product.mapper.ProductClassMapper;
import com.erp.module.product.mapper.ProductMapper;
import com.erp.module.product.service.impl.ProductServiceImpl;
import com.erp.module.product.vo.ProductVO;
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
@DisplayName("ProductService 单元测试")
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductClassMapper productClassMapper;

    private ProductServiceImpl productService;

    private ProductDTO createDTO;
    private Product existingProduct;
    private ProductClass existingClass;

    @BeforeEach
    void setUp() throws Exception {
        productService = spy(new ProductServiceImpl());

        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(productService, productMapper);

        Field classMapperField = ProductServiceImpl.class.getDeclaredField("productClassMapper");
        classMapperField.setAccessible(true);
        classMapperField.set(productService, productClassMapper);

        createDTO = new ProductDTO();
        createDTO.setProductCode("PROD-001");
        createDTO.setProductName("测试商品");
        createDTO.setModel("M001");
        createDTO.setSpec("S001");
        createDTO.setBrand("测试品牌");
        createDTO.setBaseUnitId(1L);
        createDTO.setIsMultiUnit(false);
        createDTO.setClassId(1L);
        createDTO.setIsSaleable(true);
        createDTO.setIsPurchasable(true);
        createDTO.setIsProducible(false);
        createDTO.setIsOutsourceable(false);
        createDTO.setIsSubPart(false);
        createDTO.setAuditStatus("草稿");
        createDTO.setIsActive(true);
        createDTO.setRemark("测试备注");

        existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setProductCode("PROD-001");
        existingProduct.setProductName("测试商品");
        existingProduct.setModel("M001");
        existingProduct.setSpec("S001");
        existingProduct.setBrand("测试品牌");
        existingProduct.setBaseUnitId(1L);
        existingProduct.setIsMultiUnit(false);
        existingProduct.setClassId(1L);
        existingProduct.setIsSaleable(true);
        existingProduct.setIsPurchasable(true);
        existingProduct.setIsProducible(false);
        existingProduct.setIsOutsourceable(false);
        existingProduct.setIsSubPart(false);
        existingProduct.setAuditStatus("草稿");
        existingProduct.setIsActive(true);
        existingProduct.setRemark("测试备注");

        existingClass = new ProductClass();
        existingClass.setId(1L);
        existingClass.setClassName("电子产品");
        existingClass.setIsActive(true);
    }

    @AfterEach
    void tearDown() {
        reset(productMapper, productClassMapper);
    }

    @Nested
    @DisplayName("list 分页查询")
    class ListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRecords().size());
            assertEquals(existingProduct.getProductName(), result.getRecords().get(0).getProductName());
        }

        @Test
        @DisplayName("按商品编码模糊搜索 -> 返回匹配结果")
        void shouldFilterByProductCodeFuzzy() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setProductCode("PROD");

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按商品名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByProductNameFuzzy() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setProductName("测试");

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按品牌筛选 -> 返回匹配结果")
        void shouldFilterByBrand() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setBrand("测试品牌");

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按分类ID筛选 -> 返回匹配结果")
        void shouldFilterByClassId() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setClassId(1L);

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按审核状态筛选 -> 返回匹配结果")
        void shouldFilterByAuditStatus() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setAuditStatus("草稿");

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按启用状态筛选 -> 返回匹配结果")
        void shouldFilterByIsActive() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setIsActive(true);

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            ProductQueryDTO query = new ProductQueryDTO();

            IPage<Product> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setSortField("productCode");
            query.setSortOrder("ASC");

            IPage<Product> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productService.list(query));
        }

        @Test
        @DisplayName("DESC降序排序 -> 正确传递排序参数")
        void shouldHandleDescendingSortOrder() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setSortField("productName");
            query.setSortOrder("DESC");

            IPage<Product> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productService.list(query));
        }

        @Test
        @DisplayName("默认排序 -> 按创建时间倒序")
        void shouldUseDefaultSortOrder() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productService.list(query));
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回匹配结果")
        void shouldFilterByMultipleConditions() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setProductCode("PROD");
            query.setProductName("测试");
            query.setBrand("测试品牌");
            query.setClassId(1L);
            query.setAuditStatus("草稿");
            query.setIsActive(true);

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

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
            when(productMapper.selectById(1L)).thenReturn(existingProduct);

            ProductVO result = productService.getById(1L);

            assertNotNull(result);
            assertEquals(existingProduct.getId(), result.getId());
            assertEquals(existingProduct.getProductCode(), result.getProductCode());
            assertEquals(existingProduct.getProductName(), result.getProductName());
            assertEquals(existingProduct.getBrand(), result.getBrand());
            assertEquals(existingProduct.getClassId(), result.getClassId());
            assertEquals(existingProduct.getAuditStatus(), result.getAuditStatus());
            assertEquals(existingProduct.getIsActive(), result.getIsActive());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(productMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("save 新增商品")
    class SaveTests {

        @Test
        @DisplayName("正常数据 -> 保存成功")
        void shouldSaveSuccessfully() {
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("商品编码重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenProductCodeDuplicate() {
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.save(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(productService, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("分类不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenClassNotFound() {
            createDTO.setClassId(99L);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.save(createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productService, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("classId为null -> 跳过分类校验正常保存")
        void shouldSkipClassValidationWhenClassIdIsNull() {
            createDTO.setClassId(null);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("无效审核状态 -> 抛出BusinessException DATA_STATUS_INVALID")
        void shouldThrowExceptionWhenInvalidAuditStatus() {
            createDTO.setAuditStatus("无效状态");
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.save(createDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(productService, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("审核状态为草稿 -> 保存成功")
        void shouldSaveWithDraftStatus() {
            createDTO.setAuditStatus("草稿");
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("审核状态为待审核 -> 保存成功")
        void shouldSaveWithPendingAuditStatus() {
            createDTO.setAuditStatus("待审核");
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("审核状态为已审核 -> 保存成功")
        void shouldSaveWithApprovedStatus() {
            createDTO.setAuditStatus("已审核");
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("审核状态为已驳回 -> 保存成功")
        void shouldSaveWithRejectedStatus() {
            createDTO.setAuditStatus("已驳回");
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("审核状态为空字符串 -> 跳过审核校验正常保存")
        void shouldSkipAuditValidationWhenStatusIsEmpty() {
            createDTO.setAuditStatus("");
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("审核状态为null -> 跳过审核校验正常保存")
        void shouldSkipAuditValidationWhenStatusIsNull() {
            createDTO.setAuditStatus(null);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("保存实体字段完整 -> 所有DTO字段正确映射")
        void shouldMapAllDtoFieldsToEntity() {
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            productService.save(createDTO);

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productService).save(captor.capture());
            Product saved = captor.getValue();
            assertEquals(createDTO.getProductCode(), saved.getProductCode());
            assertEquals(createDTO.getProductName(), saved.getProductName());
            assertEquals(createDTO.getModel(), saved.getModel());
            assertEquals(createDTO.getSpec(), saved.getSpec());
            assertEquals(createDTO.getBrand(), saved.getBrand());
            assertEquals(createDTO.getClassId(), saved.getClassId());
            assertEquals(createDTO.getAuditStatus(), saved.getAuditStatus());
            assertEquals(createDTO.getIsActive(), saved.getIsActive());
            assertEquals(createDTO.getRemark(), saved.getRemark());
        }
    }

    @Nested
    @DisplayName("update 更新商品")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 更新成功")
        void shouldUpdateSuccessfully() {
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(productMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.update(99L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productService, never()).updateById(any(Product.class));
        }

        @Test
        @DisplayName("编码重复排除自身 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenCodeDuplicateExcludingSelf() {
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(productService, never()).updateById(any(Product.class));
        }

        @Test
        @DisplayName("编码不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameCodeWhenUpdatingSelf() {
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("分类不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenClassNotFoundInUpdate() {
            createDTO.setClassId(99L);
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productService, never()).updateById(any(Product.class));
        }

        @Test
        @DisplayName("审核状态为空 -> 跳过审核流转校验正常更新")
        void shouldSkipAuditTransitionWhenStatusIsNull() {
            createDTO.setAuditStatus(null);
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("审核状态流转: 草稿->待审核 -> 允许")
        void shouldAllowDraftToPendingAudit() {
            existingProduct.setAuditStatus("草稿");
            createDTO.setAuditStatus("待审核");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("审核状态流转: 待审核->已审核 -> 允许")
        void shouldAllowPendingToApproved() {
            existingProduct.setAuditStatus("待审核");
            createDTO.setAuditStatus("已审核");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("审核状态流转: 已审核->已驳回 -> 允许")
        void shouldAllowApprovedToRejected() {
            existingProduct.setAuditStatus("已审核");
            createDTO.setAuditStatus("已驳回");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("审核状态流转: 已驳回->草稿 -> 允许")
        void shouldAllowRejectedToDraft() {
            existingProduct.setAuditStatus("已驳回");
            createDTO.setAuditStatus("草稿");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("审核状态流转: 已审核->草稿 -> 不允许(抛出异常)")
        void shouldRejectApprovedToDraft() {
            existingProduct.setAuditStatus("已审核");
            createDTO.setAuditStatus("草稿");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(productService, never()).updateById(any(Product.class));
        }

        @Test
        @DisplayName("审核状态流转: 草稿->已审核 -> 不允许(抛出异常)")
        void shouldRejectDraftToApproved() {
            existingProduct.setAuditStatus("草稿");
            createDTO.setAuditStatus("已审核");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(productService, never()).updateById(any(Product.class));
        }

        @Test
        @DisplayName("审核状态相同 -> 跳过流转校验正常更新")
        void shouldSkipTransitionWhenStatusUnchanged() {
            existingProduct.setAuditStatus("已审核");
            createDTO.setAuditStatus("已审核");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("原审核状态为null -> 不限制流转")
        void shouldAllowAnyTransitionWhenCurrentStatusIsNull() {
            existingProduct.setAuditStatus(null);
            createDTO.setAuditStatus("已审核");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("更新后实体ID保持不变 -> 使用原ID")
        void shouldPreserveEntityIdAfterUpdate() {
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            productService.update(1L, createDTO);

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productService).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
        }
    }

    @Nested
    @DisplayName("delete 删除商品")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 删除成功")
        void shouldDeleteSuccessfully() {
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            doReturn(true).when(productService).removeById(1L);

            assertDoesNotThrow(() -> productService.delete(1L));
            verify(productService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(productMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productService, never()).removeById(anyLong());
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("save标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnSave() throws NoSuchMethodException {
            var method = ProductServiceImpl.class.getMethod("save", ProductDTO.class);
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
            var method = ProductServiceImpl.class.getMethod("update", Long.class, ProductDTO.class);
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
            var method = ProductServiceImpl.class.getMethod("delete", Long.class);
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
            var method = ProductServiceImpl.class.getMethod("list", ProductQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "list应标注@Transactional");
            assertTrue(annotation.readOnly(), "list应为readOnly=true");
        }

        @Test
        @DisplayName("getById标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = ProductServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(productMapper.selectById(1L)).thenReturn(existingProduct);

            ProductVO result = productService.getById(1L);

            assertEquals(existingProduct.getId(), result.getId());
            assertEquals(existingProduct.getProductCode(), result.getProductCode());
            assertEquals(existingProduct.getProductName(), result.getProductName());
            assertEquals(existingProduct.getModel(), result.getModel());
            assertEquals(existingProduct.getSpec(), result.getSpec());
            assertEquals(existingProduct.getBrand(), result.getBrand());
            assertEquals(existingProduct.getBaseUnitId(), result.getBaseUnitId());
            assertEquals(existingProduct.getIsMultiUnit(), result.getIsMultiUnit());
            assertEquals(existingProduct.getClassId(), result.getClassId());
            assertEquals(existingProduct.getIsSaleable(), result.getIsSaleable());
            assertEquals(existingProduct.getIsPurchasable(), result.getIsPurchasable());
            assertEquals(existingProduct.getIsProducible(), result.getIsProducible());
            assertEquals(existingProduct.getIsOutsourceable(), result.getIsOutsourceable());
            assertEquals(existingProduct.getIsSubPart(), result.getIsSubPart());
            assertEquals(existingProduct.getAuditStatus(), result.getAuditStatus());
            assertEquals(existingProduct.getIsActive(), result.getIsActive());
            assertEquals(existingProduct.getRemark(), result.getRemark());
        }
    }

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("商品编码超长50字符 -> 边界值正常")
        void shouldHandleMaxProductCodeLength() {
            createDTO.setProductCode("A".repeat(50));
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("商品名称超长200字符 -> 边界值正常")
        void shouldHandleMaxProductNameLength() {
            createDTO.setProductName("测".repeat(200));
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("备注超长500字符 -> 边界值正常")
        void shouldHandleMaxRemarkLength() {
            createDTO.setRemark("备".repeat(500));
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
            verify(productService).save(any(Product.class));
        }

        @Test
        @DisplayName("空字符串商品编码 -> 唯一性校验仍适用")
        void shouldCheckUniquenessForEmptyProductCode() {
            createDTO.setProductCode("");
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            assertDoesNotThrow(() -> productService.save(createDTO));
        }

        @Test
        @DisplayName("isActive为false -> 正常保存")
        void shouldSaveWithInactiveStatus() {
            createDTO.setIsActive(false);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            productService.save(createDTO);

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productService).save(captor.capture());
            assertFalse(captor.getValue().getIsActive());
        }

        @Test
        @DisplayName("所有Boolean字段为false -> 正常保存")
        void shouldSaveWithAllBooleanFieldsFalse() {
            createDTO.setIsMultiUnit(false);
            createDTO.setIsSaleable(false);
            createDTO.setIsPurchasable(false);
            createDTO.setIsProducible(false);
            createDTO.setIsOutsourceable(false);
            createDTO.setIsSubPart(false);
            createDTO.setIsActive(false);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).save(any(Product.class));

            productService.save(createDTO);

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productService).save(captor.capture());
            Product saved = captor.getValue();
            assertFalse(saved.getIsMultiUnit());
            assertFalse(saved.getIsSaleable());
            assertFalse(saved.getIsPurchasable());
            assertFalse(saved.getIsProducible());
            assertFalse(saved.getIsOutsourceable());
            assertFalse(saved.getIsSubPart());
            assertFalse(saved.getIsActive());
        }

        @Test
        @DisplayName("商品编码查询条件为null -> 跳过编码筛选")
        void shouldSkipProductCodeFilterWhenNull() {
            ProductQueryDTO query = new ProductQueryDTO();
            query.setProductCode(null);
            query.setProductName("测试");

            IPage<Product> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingProduct));
            mockPage.setTotal(1);

            when(productMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<Product> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductVO> result = productService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("待审核->已驳回 -> 允许流转")
        void shouldAllowPendingToRejected() {
            existingProduct.setAuditStatus("待审核");
            createDTO.setAuditStatus("已驳回");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }

        @Test
        @DisplayName("已驳回->待审核 -> 允许流转")
        void shouldAllowRejectedToPending() {
            existingProduct.setAuditStatus("已驳回");
            createDTO.setAuditStatus("待审核");
            when(productMapper.selectById(1L)).thenReturn(existingProduct);
            when(productMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productClassMapper.selectById(1L)).thenReturn(existingClass);
            doReturn(true).when(productService).updateById(any(Product.class));

            assertDoesNotThrow(() -> productService.update(1L, createDTO));
            verify(productService).updateById(any(Product.class));
        }
    }
}
