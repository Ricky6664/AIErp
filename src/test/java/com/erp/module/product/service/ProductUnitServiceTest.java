package com.erp.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.product.dto.ProductUnitDTO;
import com.erp.module.product.dto.ProductUnitQueryDTO;
import com.erp.module.product.entity.ProductUnit;
import com.erp.module.product.mapper.ProductUnitMapper;
import com.erp.module.product.service.impl.ProductUnitServiceImpl;
import com.erp.module.product.vo.ProductUnitVO;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductUnitService 单元测试")
class ProductUnitServiceTest {

    @Mock
    private ProductUnitMapper productUnitMapper;

    private ProductUnitServiceImpl productUnitService;

    private ProductUnit existingUnit;
    private ProductUnitDTO createDTO;
    private ProductUnitDTO updateDTO;

    @BeforeEach
    void setUp() throws Exception {
        productUnitService = spy(new ProductUnitServiceImpl());

        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(productUnitService, productUnitMapper);

        existingUnit = new ProductUnit();
        existingUnit.setId(1L);
        existingUnit.setProductId(100L);
        existingUnit.setUnitId(10L);
        existingUnit.setIsBaseUnit(true);
        existingUnit.setIsPurchaseUnit(true);
        existingUnit.setIsSaleUnit(true);
        existingUnit.setIsProductionUnit(false);
        existingUnit.setConversionRate(BigDecimal.ONE);

        createDTO = new ProductUnitDTO();
        createDTO.setProductId(100L);
        createDTO.setUnitId(20L);
        createDTO.setIsBaseUnit(false);
        createDTO.setIsPurchaseUnit(true);
        createDTO.setIsSaleUnit(false);
        createDTO.setIsProductionUnit(false);
        createDTO.setConversionRate(new BigDecimal("100.00"));

        updateDTO = new ProductUnitDTO();
        updateDTO.setProductId(100L);
        updateDTO.setUnitId(20L);
        updateDTO.setIsBaseUnit(false);
        updateDTO.setIsPurchaseUnit(false);
        updateDTO.setIsSaleUnit(true);
        updateDTO.setIsProductionUnit(false);
        updateDTO.setConversionRate(new BigDecimal("50.00"));
    }

    @Nested
    @DisplayName("list 分页查询")
    class ListTests {

        @Test
        @DisplayName("正常分页查询返回VO列表")
        void shouldReturnPagedVOList() {
            ProductUnitQueryDTO query = new ProductUnitQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            Page<ProductUnit> entityPage = new Page<>(1, 10);
            entityPage.setRecords(Collections.singletonList(existingUnit));
            entityPage.setTotal(1);

            when(productUnitMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(entityPage);

            IPage<ProductUnitVO> result = productUnitService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRecords().size());
            ProductUnitVO vo = result.getRecords().get(0);
            assertEquals(existingUnit.getId(), vo.getId());
            assertEquals(existingUnit.getProductId(), vo.getProductId());
            verify(productUnitMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("带过滤条件的分页查询")
        void shouldFilterByProductIdAndUnitId() {
            ProductUnitQueryDTO query = new ProductUnitQueryDTO();
            query.setProductId(100L);
            query.setUnitId(10L);
            query.setPageNum(1);
            query.setPageSize(10);

            Page<ProductUnit> entityPage = new Page<>(1, 10);
            entityPage.setRecords(Collections.singletonList(existingUnit));
            entityPage.setTotal(1);

            when(productUnitMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(entityPage);

            IPage<ProductUnitVO> result = productUnitService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("查询结果为空时返回空列表")
        void shouldReturnEmptyListWhenNoData() {
            ProductUnitQueryDTO query = new ProductUnitQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            Page<ProductUnit> emptyPage = new Page<>(1, 10);
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0);

            when(productUnitMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(emptyPage);

            IPage<ProductUnitVO> result = productUnitService.list(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }
    }

    @Nested
    @DisplayName("getById 查询")
    class GetByIdTests {

        @Test
        @DisplayName("根据ID查询返回VO")
        void shouldReturnVOWhenFound() {
            when(productUnitMapper.selectById(1L)).thenReturn(existingUnit);

            ProductUnitVO result = productUnitService.getById(1L);

            assertNotNull(result);
            assertEquals(existingUnit.getId(), result.getId());
            assertEquals(existingUnit.getProductId(), result.getProductId());
            verify(productUnitMapper).selectById(1L);
        }

        @Test
        @DisplayName("查询不存在的数据抛出BusinessException")
        void shouldThrowWhenNotFound() {
            when(productUnitMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productUnitService.getById(999L));

            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productUnitMapper).selectById(999L);
        }
    }

    @Nested
    @DisplayName("save 新增")
    class SaveTests {

        @Test
        @DisplayName("正常新增单位")
        void shouldSaveSuccessfully() {
            when(productUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productUnitMapper.insert(any(ProductUnit.class))).thenReturn(1);

            productUnitService.save(createDTO);

            ArgumentCaptor<ProductUnit> captor = ArgumentCaptor.forClass(ProductUnit.class);
            verify(productUnitMapper).insert(captor.capture());
            ProductUnit saved = captor.getValue();
            assertEquals(createDTO.getProductId(), saved.getProductId());
            assertEquals(createDTO.getUnitId(), saved.getUnitId());
            assertEquals(createDTO.getConversionRate(), saved.getConversionRate());
        }

        @Test
        @DisplayName("转换比例<=0时抛出BusinessException")
        void shouldThrowWhenConversionRateInvalid() {
            createDTO.setConversionRate(BigDecimal.ZERO);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productUnitService.save(createDTO));

            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
            verify(productUnitMapper, never()).insert(any());
        }

        @Test
        @DisplayName("同一商品下单位重复时抛出BusinessException")
        void shouldThrowWhenUnitDuplicate() {
            when(productUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productUnitService.save(createDTO));

            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(productUnitMapper, never()).insert(any());
        }

        @Test
        @DisplayName("基础单位已存在时新增基础单位抛出BusinessException")
        void shouldThrowWhenBaseUnitAlreadyExists() {
            createDTO.setIsBaseUnit(true);
            when(productUnitMapper.selectCount(any(LambdaQueryWrapper.class)))
                    .thenReturn(0L)  // first call: unit uniqueness check passes
                    .thenReturn(1L); // second call: base unit uniqueness check fails

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productUnitService.save(createDTO));

            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(productUnitMapper, never()).insert(any());
        }

        @Test
        @DisplayName("转换比例为null时跳过校验")
        void shouldSkipConversionRateCheckWhenNull() {
            createDTO.setConversionRate(null);
            when(productUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productUnitMapper.insert(any(ProductUnit.class))).thenReturn(1);

            assertDoesNotThrow(() -> productUnitService.save(createDTO));
            verify(productUnitMapper).insert(any(ProductUnit.class));
        }
    }

    @Nested
    @DisplayName("update 更新")
    class UpdateTests {

        @Test
        @DisplayName("正常更新单位")
        void shouldUpdateSuccessfully() {
            when(productUnitMapper.selectById(1L)).thenReturn(existingUnit);
            when(productUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productUnitMapper.updateById(any(ProductUnit.class))).thenReturn(1);

            productUnitService.update(1L, updateDTO);

            verify(productUnitMapper).selectById(1L);
            verify(productUnitMapper).updateById(any(ProductUnit.class));
        }

        @Test
        @DisplayName("更新不存在的数据抛出BusinessException")
        void shouldThrowWhenUpdatingNonExistent() {
            when(productUnitMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productUnitService.update(999L, updateDTO));

            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productUnitMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时转换比例<=0抛出BusinessException")
        void shouldThrowWhenConversionRateInvalidOnUpdate() {
            when(productUnitMapper.selectById(1L)).thenReturn(existingUnit);
            updateDTO.setConversionRate(BigDecimal.valueOf(-1));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productUnitService.update(1L, updateDTO));

            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
            verify(productUnitMapper).selectById(1L);
            verify(productUnitMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时排除自身检查单位唯一性")
        void shouldExcludeSelfWhenCheckingUniquenessOnUpdate() {
            when(productUnitMapper.selectById(1L)).thenReturn(existingUnit);
            when(productUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(productUnitMapper.updateById(any(ProductUnit.class))).thenReturn(1);

            productUnitService.update(1L, updateDTO);

            ArgumentCaptor<LambdaQueryWrapper> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
            verify(productUnitMapper, atLeastOnce()).selectCount(captor.capture());
            verify(productUnitMapper).updateById(any(ProductUnit.class));
        }
    }

    @Nested
    @DisplayName("delete 删除")
    class DeleteTests {

        @Test
        @DisplayName("正常删除单位")
        void shouldDeleteSuccessfully() {
            when(productUnitMapper.selectById(1L)).thenReturn(existingUnit);
            doReturn(true).when(productUnitService).removeById(anyLong());

            productUnitService.delete(1L);

            verify(productUnitMapper).selectById(1L);
            verify(productUnitService).removeById(1L);
        }

        @Test
        @DisplayName("删除不存在的数据抛出BusinessException")
        void shouldThrowWhenDeletingNonExistent() {
            when(productUnitMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> productUnitService.delete(999L));

            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(productUnitMapper, never()).deleteById(any());
        }
    }
}
