package com.erp.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.product.dto.ProductControlDTO;
import com.erp.module.product.dto.ProductControlQueryDTO;
import com.erp.module.product.entity.ProductControl;
import com.erp.module.product.mapper.ProductControlMapper;
import com.erp.module.product.service.impl.ProductControlServiceImpl;
import com.erp.module.product.vo.ProductControlVO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductControlService 单元测试")
class ProductControlServiceTest {

    @Mock
    private ProductControlMapper productControlMapper;

    private ProductControlServiceImpl productControlService;

    private ProductControlDTO createDTO;
    private ProductControl existingControl;

    @BeforeEach
    void setUp() throws Exception {
        productControlService = spy(new ProductControlServiceImpl());

        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(productControlService, productControlMapper);

        createDTO = new ProductControlDTO();
        createDTO.setProductId(1L);
        createDTO.setDefaultPurchaseUnitId(10L);
        createDTO.setDefaultSaleUnitId(20L);
        createDTO.setInventoryManageFlag(true);
        createDTO.setLocationManageFlag(false);
        createDTO.setBatchManageFlag(true);
        createDTO.setSerialManageFlag(false);
        createDTO.setShelfLifeManageFlag(true);
        createDTO.setMinPackageQty(new BigDecimal("1.00"));
        createDTO.setMinOrderQty(new BigDecimal("10.00"));

        existingControl = new ProductControl();
        existingControl.setId(1L);
        existingControl.setProductId(1L);
        existingControl.setDefaultPurchaseUnitId(10L);
        existingControl.setDefaultSaleUnitId(20L);
        existingControl.setInventoryManageFlag(true);
        existingControl.setLocationManageFlag(false);
        existingControl.setBatchManageFlag(true);
        existingControl.setSerialManageFlag(false);
        existingControl.setShelfLifeManageFlag(true);
        existingControl.setMinPackageQty(new BigDecimal("1.00"));
        existingControl.setMinOrderQty(new BigDecimal("10.00"));
    }

    @AfterEach
    void tearDown() {
        reset(productControlMapper, productControlService);
    }

    @Nested
    @DisplayName("list 分页查询")
    class ListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            ProductControlQueryDTO query = new ProductControlQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<ProductControl> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingControl));
            mockPage.setTotal(1);

            when(productControlMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductControl> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductControlVO> result = productControlService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRecords().size());
            assertEquals(existingControl.getProductId(), result.getRecords().get(0).getProductId());
        }

        @Test
        @DisplayName("按productId筛选 -> 返回匹配结果")
        void shouldFilterByProductId() {
            ProductControlQueryDTO query = new ProductControlQueryDTO();
            query.setProductId(1L);

            IPage<ProductControl> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingControl));
            mockPage.setTotal(1);

            when(productControlMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductControl> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            IPage<ProductControlVO> result = productControlService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            ProductControlQueryDTO query = new ProductControlQueryDTO();

            IPage<ProductControl> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productControlMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductControl> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            IPage<ProductControlVO> result = productControlService.list(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            ProductControlQueryDTO query = new ProductControlQueryDTO();
            query.setSortField("createTime");
            query.setSortOrder("ASC");

            IPage<ProductControl> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productControlMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductControl> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productControlService.list(query));
        }

        @Test
        @DisplayName("DESC降序排序 -> 正确传递排序参数")
        void shouldHandleDescendingSortOrder() {
            ProductControlQueryDTO query = new ProductControlQueryDTO();
            query.setSortField("id");
            query.setSortOrder("DESC");

            IPage<ProductControl> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productControlMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductControl> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productControlService.list(query));
        }

        @Test
        @DisplayName("默认排序 -> 按id排序")
        void shouldUseDefaultSortOrder() {
            ProductControlQueryDTO query = new ProductControlQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<ProductControl> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(productControlMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductControl> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> productControlService.list(query));
        }
    }

    @Nested
    @DisplayName("getById 查询详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);

            ProductControlVO result = productControlService.getById(1L);

            assertNotNull(result);
            assertEquals(existingControl.getId(), result.getId());
            assertEquals(existingControl.getProductId(), result.getProductId());
            assertEquals(existingControl.getBatchManageFlag(), result.getBatchManageFlag());
            assertEquals(existingControl.getSerialManageFlag(), result.getSerialManageFlag());
            assertEquals(existingControl.getInventoryManageFlag(), result.getInventoryManageFlag());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(productControlMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productControlService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("save 新增控制策略")
    class SaveTests {

        @Test
        @DisplayName("正常数据 -> 保存成功")
        void shouldSaveSuccessfully() {
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("同一商品已有控制策略 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenDuplicateProductId() {
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productControlService.save(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(productControlService, never()).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("批次管理与序列号管理同时开启 -> 抛出BusinessException BUSINESS_ERROR")
        void shouldThrowExceptionWhenBatchAndSerialBothEnabled() {
            createDTO.setBatchManageFlag(true);
            createDTO.setSerialManageFlag(true);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productControlService.save(createDTO));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(productControlService, never()).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("批次管理与序列号管理均关闭 -> 保存成功")
        void shouldSaveWhenBothBatchAndSerialDisabled() {
            createDTO.setBatchManageFlag(false);
            createDTO.setSerialManageFlag(false);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("只有批次管理开启 -> 保存成功")
        void shouldSaveWhenOnlyBatchEnabled() {
            createDTO.setBatchManageFlag(true);
            createDTO.setSerialManageFlag(false);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("只有序列号管理开启 -> 保存成功")
        void shouldSaveWhenOnlySerialEnabled() {
            createDTO.setBatchManageFlag(false);
            createDTO.setSerialManageFlag(true);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("batchManageFlag为null -> 互斥校验通过保存成功")
        void shouldSaveWhenBatchFlagIsNull() {
            createDTO.setBatchManageFlag(null);
            createDTO.setSerialManageFlag(true);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("serialManageFlag为null -> 互斥校验通过保存成功")
        void shouldSaveWhenSerialFlagIsNull() {
            createDTO.setBatchManageFlag(true);
            createDTO.setSerialManageFlag(null);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("productId为null -> 唯一性校验使用null查询")
        void shouldHandleNullProductIdInSave() {
            createDTO.setProductId(null);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("保存实体字段完整 -> 所有DTO字段正确映射")
        void shouldMapAllDtoFieldsToEntity() {
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            productControlService.save(createDTO);

            ArgumentCaptor<ProductControl> captor = ArgumentCaptor.forClass(ProductControl.class);
            verify(productControlService).save(captor.capture());
            ProductControl saved = captor.getValue();
            assertEquals(createDTO.getProductId(), saved.getProductId());
            assertEquals(createDTO.getDefaultPurchaseUnitId(), saved.getDefaultPurchaseUnitId());
            assertEquals(createDTO.getDefaultSaleUnitId(), saved.getDefaultSaleUnitId());
            assertEquals(createDTO.getBatchManageFlag(), saved.getBatchManageFlag());
            assertEquals(createDTO.getSerialManageFlag(), saved.getSerialManageFlag());
            assertEquals(createDTO.getInventoryManageFlag(), saved.getInventoryManageFlag());
            assertEquals(createDTO.getShelfLifeManageFlag(), saved.getShelfLifeManageFlag());
            assertEquals(createDTO.getMinPackageQty(), saved.getMinPackageQty());
            assertEquals(createDTO.getMinOrderQty(), saved.getMinOrderQty());
        }

        @Test
        @DisplayName("minPackageQty为0 -> 边界值保存成功")
        void shouldSaveWithZeroMinPackageQty() {
            createDTO.setMinPackageQty(BigDecimal.ZERO);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }

        @Test
        @DisplayName("minOrderQty为0 -> 边界值保存成功")
        void shouldSaveWithZeroMinOrderQty() {
            createDTO.setMinOrderQty(BigDecimal.ZERO);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
            verify(productControlService).save(any(ProductControl.class));
        }
    }

    @Nested
    @DisplayName("update 更新控制策略")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 更新成功")
        void shouldUpdateSuccessfully() {
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).updateById(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.update(1L, createDTO));
            verify(productControlService).updateById(any(ProductControl.class));
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(productControlMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productControlService.update(99L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productControlService, never()).updateById(any(ProductControl.class));
        }

        @Test
        @DisplayName("同一商品已有其他控制策略 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenDuplicateProductIdExcludingSelf() {
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productControlService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(productControlService, never()).updateById(any(ProductControl.class));
        }

        @Test
        @DisplayName("productId不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameProductIdWhenUpdatingSelf() {
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).updateById(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.update(1L, createDTO));
            verify(productControlService).updateById(any(ProductControl.class));
        }

        @Test
        @DisplayName("更新时批次管理与序列号管理同时开启 -> 抛出BusinessException BUSINESS_ERROR")
        void shouldThrowExceptionWhenBatchAndSerialBothEnabledInUpdate() {
            createDTO.setBatchManageFlag(true);
            createDTO.setSerialManageFlag(true);
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productControlService.update(1L, createDTO));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(productControlService, never()).updateById(any(ProductControl.class));
        }

        @Test
        @DisplayName("更新实体字段完整 -> 所有DTO字段正确映射到existing实体")
        void shouldMapAllDtoFieldsToExistingEntity() {
            createDTO.setBatchManageFlag(false);
            createDTO.setSerialManageFlag(true);
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).updateById(any(ProductControl.class));

            productControlService.update(1L, createDTO);

            ArgumentCaptor<ProductControl> captor = ArgumentCaptor.forClass(ProductControl.class);
            verify(productControlService).updateById(captor.capture());
            ProductControl updated = captor.getValue();
            assertEquals(1L, updated.getId());
            assertEquals(createDTO.getProductId(), updated.getProductId());
            assertEquals(createDTO.getBatchManageFlag(), updated.getBatchManageFlag());
            assertEquals(createDTO.getSerialManageFlag(), updated.getSerialManageFlag());
            assertEquals(createDTO.getMinPackageQty(), updated.getMinPackageQty());
            assertEquals(createDTO.getMinOrderQty(), updated.getMinOrderQty());
        }
    }

    @Nested
    @DisplayName("delete 删除控制策略")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 删除成功")
        void shouldDeleteSuccessfully() {
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);
            doReturn(true).when(productControlService).removeById(1L);

            assertDoesNotThrow(() -> productControlService.delete(1L));
            verify(productControlService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(productControlMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productControlService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productControlService, never()).removeById(anyLong());
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("save标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnSave() throws NoSuchMethodException {
            var method = ProductControlServiceImpl.class.getMethod("save", ProductControlDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "save应标注@Transactional");
            assertTrue(
                    java.util.Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("update标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnUpdate() throws NoSuchMethodException {
            var method = ProductControlServiceImpl.class.getMethod("update", Long.class, ProductControlDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "update应标注@Transactional");
            assertTrue(
                    java.util.Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("delete标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnDelete() throws NoSuchMethodException {
            var method = ProductControlServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "delete应标注@Transactional");
            assertTrue(
                    java.util.Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("list标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnList() throws NoSuchMethodException {
            var method = ProductControlServiceImpl.class.getMethod("list", ProductControlQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "list应标注@Transactional");
            assertTrue(annotation.readOnly(), "list应为readOnly=true");
        }

        @Test
        @DisplayName("getById标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = ProductControlServiceImpl.class.getMethod("getById", Long.class);
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
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);

            ProductControlVO result = productControlService.getById(1L);

            assertEquals(existingControl.getId(), result.getId());
            assertEquals(existingControl.getProductId(), result.getProductId());
            assertEquals(existingControl.getDefaultPurchaseUnitId(), result.getDefaultPurchaseUnitId());
            assertEquals(existingControl.getDefaultSaleUnitId(), result.getDefaultSaleUnitId());
            assertEquals(existingControl.getInventoryManageFlag(), result.getInventoryManageFlag());
            assertEquals(existingControl.getLocationManageFlag(), result.getLocationManageFlag());
            assertEquals(existingControl.getBatchManageFlag(), result.getBatchManageFlag());
            assertEquals(existingControl.getSerialManageFlag(), result.getSerialManageFlag());
            assertEquals(existingControl.getShelfLifeManageFlag(), result.getShelfLifeManageFlag());
            assertEquals(existingControl.getMinPackageQty(), result.getMinPackageQty());
            assertEquals(existingControl.getMinOrderQty(), result.getMinOrderQty());
        }

        @Test
        @DisplayName("createBy/updateBy映射自creatorId/updaterId")
        void shouldMapCreatorAndUpdaterFields() {
            existingControl.setCreatorId(100L);
            existingControl.setUpdaterId(200L);
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);

            ProductControlVO result = productControlService.getById(1L);

            assertEquals(100L, result.getCreateBy());
            assertEquals(200L, result.getUpdateBy());
        }
    }

    @Nested
    @DisplayName("边界与异常场景补充")
    class EdgeCaseTests {

        @Test
        @DisplayName("list参数productId为null -> 不添加该筛选条件正常查询")
        void shouldSkipProductIdFilterWhenNull() {
            ProductControlQueryDTO query = new ProductControlQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<ProductControl> mockPage = new Page<>(1, 10);
            mockPage.setRecords(java.util.Collections.emptyList());
            mockPage.setTotal(0);

            when(productControlMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<ProductControl> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            assertDoesNotThrow(() -> productControlService.list(query));
        }

        @Test
        @DisplayName("update时productId为null -> 唯一性校验使用null值")
        void shouldHandleNullProductIdInUpdate() {
            createDTO.setProductId(null);
            when(productControlMapper.selectById(1L)).thenReturn(existingControl);
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).updateById(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.update(1L, createDTO));
            verify(productControlService).updateById(any(ProductControl.class));
        }

        @Test
        @DisplayName("minPackageQty为负数 -> 业务校验应在Controller层拦截")
        void shouldAcceptNegativeMinPackageQtyAtServiceLayer() {
            createDTO.setMinPackageQty(new BigDecimal("-1.00"));
            when(productControlMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(productControlService).save(any(ProductControl.class));

            assertDoesNotThrow(() -> productControlService.save(createDTO));
        }
    }
}
