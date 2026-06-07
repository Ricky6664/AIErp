package com.erp.module.warehouse.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.warehouse.dto.LocationCreateDTO;
import com.erp.module.warehouse.dto.LocationQueryDTO;
import com.erp.module.warehouse.dto.LocationUpdateDTO;
import com.erp.module.warehouse.entity.LocationEntity;
import com.erp.module.warehouse.mapper.LocationMapper;
import com.erp.module.warehouse.service.impl.LocationServiceImpl;
import com.erp.module.warehouse.vo.LocationVO;
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
@DisplayName("LocationService 单元测试")
class LocationServiceTest {

    @Mock
    private LocationMapper locationMapper;

    private LocationServiceImpl locationService;

    private LocationCreateDTO createDTO;
    private LocationUpdateDTO updateDTO;
    private LocationEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        locationService = spy(new LocationServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(locationService, locationMapper);

        createDTO = new LocationCreateDTO();
        createDTO.setWarehouseId(1L);
        createDTO.setLocationCode("LOC-001");
        createDTO.setLocationName("测试库位");
        createDTO.setLocationType("STORAGE");
        createDTO.setSortOrder(1);
        createDTO.setStatus(1);

        updateDTO = new LocationUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setWarehouseId(1L);
        updateDTO.setLocationCode("LOC-002");
        updateDTO.setLocationName("更新库位");
        updateDTO.setLocationType("PICKING");
        updateDTO.setSortOrder(2);
        updateDTO.setStatus(0);

        existingEntity = new LocationEntity();
        existingEntity.setId(1L);
        existingEntity.setWarehouseId(1L);
        existingEntity.setLocationCode("LOC-001");
        existingEntity.setLocationName("测试库位");
        existingEntity.setLocationType("STORAGE");
        existingEntity.setSortOrder(1);
        existingEntity.setStatus(1);
    }

    @AfterEach
    void tearDown() {
        reset(locationMapper);
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create 新增库位")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateLocationSuccessfully() {
            when(locationMapper.insert(any(LocationEntity.class))).thenReturn(1);

            LocationVO result = locationService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getLocationCode(), result.getLocationCode());
            assertEquals(createDTO.getLocationName(), result.getLocationName());
            assertEquals(createDTO.getLocationType(), result.getLocationType());
            assertEquals(createDTO.getWarehouseId(), result.getWarehouseId());

            ArgumentCaptor<LocationEntity> captor = ArgumentCaptor.forClass(LocationEntity.class);
            verify(locationMapper).insert(captor.capture());
            assertEquals("LOC-001", captor.getValue().getLocationCode());
            assertEquals(1L, captor.getValue().getWarehouseId());
        }

        @Test
        @DisplayName("编码在同一仓库内重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenCodeDuplicateInSameWarehouse() {
            when(locationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> locationService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(locationMapper, never()).insert(any());
        }

        @Test
        @DisplayName("编码为空 -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsNull() {
            createDTO.setLocationCode(null);
            when(locationMapper.insert(any(LocationEntity.class))).thenReturn(1);

            LocationVO result = locationService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getLocationCode());
            verify(locationMapper).insert(any());
        }

        @Test
        @DisplayName("编码为空字符串 -> 跳过唯一性校验")
        void shouldSkipUniquenessCheckWhenCodeIsEmpty() {
            createDTO.setLocationCode("");
            when(locationMapper.insert(any(LocationEntity.class))).thenReturn(1);

            LocationVO result = locationService.create(createDTO);

            assertNotNull(result);
            verify(locationMapper).insert(any());
        }

        @Test
        @DisplayName("warehouseId为null -> 跳过唯一性校验")
        void shouldSkipUniquenessCheckWhenWarehouseIdIsNull() {
            createDTO.setWarehouseId(null);
            when(locationMapper.insert(any(LocationEntity.class))).thenReturn(1);

            assertDoesNotThrow(() -> locationService.create(createDTO));
            verify(locationMapper).insert(any());
        }

        @Test
        @DisplayName("不同仓库相同编码 -> 允许创建（唯一性仅在仓库内）")
        void shouldAllowSameCodeInDifferentWarehouse() {
            createDTO.setWarehouseId(2L);
            when(locationMapper.insert(any(LocationEntity.class))).thenReturn(1);

            LocationVO result = locationService.create(createDTO);

            assertNotNull(result);
            verify(locationMapper).insert(any());
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update 更新库位")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateLocationSuccessfully() {
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);
            when(locationMapper.updateById(any(LocationEntity.class))).thenReturn(1);

            LocationVO result = locationService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getLocationCode(), result.getLocationCode());
            assertEquals(updateDTO.getLocationName(), result.getLocationName());

            ArgumentCaptor<LocationEntity> captor = ArgumentCaptor.forClass(LocationEntity.class);
            verify(locationMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("LOC-002", captor.getValue().getLocationCode());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(locationMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> locationService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(locationMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("编码在同一仓库内重复(排除自身) -> 抛出BusinessException")
        void shouldThrowExceptionWhenCodeDuplicateExcludingSelf() {
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);
            when(locationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> locationService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(locationMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("状态从启用改为禁用 -> 状态流转校验通过")
        void shouldAllowStatusTransitionFromActiveToInactive() {
            existingEntity.setStatus(1);
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);
            when(locationMapper.updateById(any())).thenReturn(1);

            updateDTO.setStatus(0);
            LocationVO result = locationService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(0, result.getStatus());
        }

        @Test
        @DisplayName("状态从禁用改为启用 -> 状态流转校验通过")
        void shouldAllowStatusTransitionFromInactiveToActive() {
            existingEntity.setStatus(0);
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);
            when(locationMapper.updateById(any())).thenReturn(1);

            updateDTO.setStatus(1);
            LocationVO result = locationService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(1, result.getStatus());
        }

        @Test
        @DisplayName("非法状态流转(2->0) -> 抛出BusinessException DATA_STATUS_INVALID")
        void shouldThrowExceptionOnInvalidStatusTransition() {
            existingEntity.setStatus(2);
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);

            updateDTO.setStatus(0);
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> locationService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("newStatus为null -> 跳过状态校验")
        void shouldSkipStatusValidationWhenNewStatusIsNull() {
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);
            when(locationMapper.updateById(any())).thenReturn(1);

            updateDTO.setStatus(null);
            assertDoesNotThrow(() -> locationService.update(1L, updateDTO));
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete 删除库位")
    class DeleteTests {

        @Test
        @DisplayName("库位存在 -> 软删除成功(is_deleted=true)")
        void shouldSoftDeleteSuccessfully() throws Exception {
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(locationService).removeById(1L);

            assertDoesNotThrow(() -> locationService.delete(1L));
            verify(locationService).removeById(1L);
        }

        @Test
        @DisplayName("库位不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenLocationNotFound() {
            when(locationMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> locationService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(locationMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("库位存在 -> removeById被调用且传入正确ID")
        void shouldCallRemoveByIdWithCorrectId() throws Exception {
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(locationService).removeById(1L);

            locationService.delete(1L);

            verify(locationService).removeById(eq(1L));
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById 查询库位详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回库位VO")
        void shouldReturnLocationVoWhenExists() {
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);

            LocationVO result = locationService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getLocationCode(), result.getLocationCode());
            assertEquals(existingEntity.getLocationName(), result.getLocationName());
            assertEquals(existingEntity.getWarehouseId(), result.getWarehouseId());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(locationMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> locationService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== pageList ====================

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            LocationQueryDTO query = new LocationQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<LocationEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(locationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<LocationEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<LocationVO> result = locationService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getLocationCode(), result.getList().get(0).getLocationCode());
        }

        @Test
        @DisplayName("按仓库ID筛选 -> 返回匹配结果")
        void shouldFilterByWarehouseId() {
            LocationQueryDTO query = new LocationQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setWarehouseId(1L);

            IPage<LocationEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(locationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<LocationEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<LocationVO> result = locationService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按类型和状态筛选 -> 返回匹配结果")
        void shouldFilterByTypeAndStatus() {
            LocationQueryDTO query = new LocationQueryDTO();
            query.setPageNum(1);
            query.setPageSize(20);
            query.setLocationType("STORAGE");
            query.setStatus(1);

            IPage<LocationEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(locationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<LocationEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<LocationVO> result = locationService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("按库位名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByLocationNameFuzzy() {
            LocationQueryDTO query = new LocationQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setLocationName("测试");

            IPage<LocationEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(locationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<LocationEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<LocationVO> result = locationService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            LocationQueryDTO query = new LocationQueryDTO();

            IPage<LocationEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(locationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<LocationEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<LocationVO> result = locationService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            LocationQueryDTO query = new LocationQueryDTO();
            query.setSortField("locationName");
            query.setSortOrder("ASC");

            IPage<LocationEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(locationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<LocationEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> locationService.pageList(query));
            verify(locationMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("无效排序字段 -> 使用默认sortOrder升序")
        void shouldFallbackToDefaultSortWhenInvalidField() {
            LocationQueryDTO query = new LocationQueryDTO();
            query.setSortField("invalidField");
            query.setSortOrder("DESC");

            IPage<LocationEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(locationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<LocationEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> locationService.pageList(query));
        }
    }

    // ==================== 异常与注解验证 ====================

    @Nested
    @DisplayName("异常与注解验证")
    class ExceptionAndAnnotationTests {

        @Test
        @DisplayName("BusinessException包含正确的code和msg")
        void shouldHaveCorrectErrorCodeInBusinessException() {
            BusinessException ex = new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "库位编码在该仓库内已存在");

            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getMessage(), ex.getMsg());
        }

        @Test
        @DisplayName("@Transactional(rollbackFor=Exception.class)标注在create方法")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = LocationServiceImpl.class.getMethod("create", LocationCreateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "create应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("@Transactional(rollbackFor=Exception.class)标注在update方法")
        void shouldHaveTransactionalOnUpdate() throws NoSuchMethodException {
            var method = LocationServiceImpl.class.getMethod("update", Long.class, LocationUpdateDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "update应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("@Transactional(rollbackFor=Exception.class)标注在delete方法")
        void shouldHaveTransactionalOnDelete() throws NoSuchMethodException {
            var method = LocationServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "delete应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }

        @Test
        @DisplayName("getById使用@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = LocationServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }
    }

    // ==================== toVO转换 ====================

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(locationMapper.selectById(1L)).thenReturn(existingEntity);

            LocationVO result = locationService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getWarehouseId(), result.getWarehouseId());
            assertEquals(existingEntity.getLocationCode(), result.getLocationCode());
            assertEquals(existingEntity.getLocationName(), result.getLocationName());
            assertEquals(existingEntity.getLocationType(), result.getLocationType());
            assertEquals(existingEntity.getSortOrder(), result.getSortOrder());
            assertEquals(existingEntity.getStatus(), result.getStatus());
        }
    }

    // ==================== 边界场景 ====================

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("超长编码 -> 不抛异常(校验在Controller层)")
        void shouldNotThrowOnLongCode() {
            createDTO.setLocationCode("L".repeat(100));
            when(locationMapper.insert(any())).thenReturn(1);

            assertDoesNotThrow(() -> locationService.create(createDTO));
        }

        @Test
        @DisplayName("特殊字符编码 -> 通过唯一性校验")
        void shouldAcceptSpecialCharactersInCode() {
            createDTO.setLocationCode("LOC-001_TEST");
            when(locationMapper.insert(any())).thenReturn(1);

            LocationVO result = locationService.create(createDTO);

            assertNotNull(result);
            assertEquals("LOC-001_TEST", result.getLocationCode());
        }

        @Test
        @DisplayName("sortOrder为null -> 正常创建")
        void shouldCreateWithNullSortOrder() {
            createDTO.setSortOrder(null);
            when(locationMapper.insert(any())).thenReturn(1);

            LocationVO result = locationService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getSortOrder());
        }

        @Test
        @DisplayName("delete传入null ID -> 抛出BusinessException")
        void shouldHandleNullIdInDelete() {
            when(locationMapper.selectById(isNull())).thenReturn(null);

            assertThrows(BusinessException.class, () -> locationService.delete(null));
        }

        @Test
        @DisplayName("pageList按sortOrder默认排序 -> 不抛异常")
        void shouldUseDefaultSortOrderWhenNoSortField() {
            LocationQueryDTO query = new LocationQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<LocationEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(locationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<LocationEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> locationService.pageList(query));
        }
    }
}
