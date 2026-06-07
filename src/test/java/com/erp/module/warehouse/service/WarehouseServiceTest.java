package com.erp.module.warehouse.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.warehouse.dto.WarehouseCreateDTO;
import com.erp.module.warehouse.dto.WarehouseQueryDTO;
import com.erp.module.warehouse.dto.WarehouseUpdateDTO;
import com.erp.module.warehouse.entity.WarehouseEntity;
import com.erp.module.warehouse.mapper.WarehouseMapper;
import com.erp.module.warehouse.service.impl.WarehouseServiceImpl;
import com.erp.module.warehouse.vo.WarehouseVO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseService 单元测试")
class WarehouseServiceTest {

    @Mock
    private WarehouseMapper warehouseMapper;

    private WarehouseServiceImpl warehouseService;

    private WarehouseCreateDTO createDTO;
    private WarehouseUpdateDTO updateDTO;
    private WarehouseEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        warehouseService = spy(new WarehouseServiceImpl());
        // Inject mock mapper into MyBatis-Plus ServiceImpl baseMapper field
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(warehouseService, warehouseMapper);
        createDTO = new WarehouseCreateDTO();
        createDTO.setWarehouseCode("WH001");
        createDTO.setWarehouseName("测试仓库");
        createDTO.setWarehouseType("NORMAL");
        createDTO.setAddress("测试地址");
        createDTO.setPhone("13800138000");
        createDTO.setStatus(1);

        updateDTO = new WarehouseUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setWarehouseCode("WH002");
        updateDTO.setWarehouseName("更新仓库");
        updateDTO.setWarehouseType("COLD");
        updateDTO.setAddress("更新地址");
        updateDTO.setPhone("13900139000");
        updateDTO.setStatus(0);

        existingEntity = new WarehouseEntity();
        existingEntity.setId(1L);
        existingEntity.setWarehouseCode("WH001");
        existingEntity.setWarehouseName("测试仓库");
        existingEntity.setWarehouseType("NORMAL");
        existingEntity.setAddress("测试地址");
        existingEntity.setPhone("13800138000");
        existingEntity.setStatus(1);
    }

    @AfterEach
    void tearDown() {
        reset(warehouseMapper);
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create 新增仓库")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateWarehouseSuccessfully() {
            when(warehouseMapper.insert(any(WarehouseEntity.class))).thenReturn(1);

            WarehouseVO result = warehouseService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getWarehouseCode(), result.getWarehouseCode());
            assertEquals(createDTO.getWarehouseName(), result.getWarehouseName());
            assertEquals(createDTO.getWarehouseType(), result.getWarehouseType());

            ArgumentCaptor<WarehouseEntity> captor = ArgumentCaptor.forClass(WarehouseEntity.class);
            verify(warehouseMapper).insert(captor.capture());
            assertEquals("WH001", captor.getValue().getWarehouseCode());
        }

        @Test
        @DisplayName("编码重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenCodeDuplicate() {
            when(warehouseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> warehouseService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(warehouseMapper, never()).insert(any());
        }

        @Test
        @DisplayName("编码为空 -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsNull() {
            createDTO.setWarehouseCode(null);
            when(warehouseMapper.insert(any(WarehouseEntity.class))).thenReturn(1);

            WarehouseVO result = warehouseService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getWarehouseCode());
            verify(warehouseMapper).insert(any());
        }

        @Test
        @DisplayName("编码为空字符串 -> 跳过唯一性校验")
        void shouldSkipUniquenessCheckWhenCodeIsEmpty() {
            createDTO.setWarehouseCode("");
            when(warehouseMapper.insert(any(WarehouseEntity.class))).thenReturn(1);

            WarehouseVO result = warehouseService.create(createDTO);

            assertNotNull(result);
            verify(warehouseMapper).insert(any());
        }

        @Test
        @DisplayName("insert返回0 -> 仍返回VO(返回值未校验)")
        void shouldReturnVoEvenWhenInsertReturnsZero() {
            WarehouseVO result = warehouseService.create(createDTO);

            assertNotNull(result);
            assertEquals("WH001", result.getWarehouseCode());
            verify(warehouseMapper).insert(any());
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update 更新仓库")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateWarehouseSuccessfully() {
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);
            when(warehouseMapper.updateById(any(WarehouseEntity.class))).thenReturn(1);

            WarehouseVO result = warehouseService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getWarehouseCode(), result.getWarehouseCode());
            assertEquals(updateDTO.getWarehouseName(), result.getWarehouseName());

            ArgumentCaptor<WarehouseEntity> captor = ArgumentCaptor.forClass(WarehouseEntity.class);
            verify(warehouseMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("WH002", captor.getValue().getWarehouseCode());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(warehouseMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> warehouseService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(warehouseMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("编码重复(排除自身) -> 抛出BusinessException")
        void shouldThrowExceptionWhenCodeDuplicateExcludingSelf() {
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);
            when(warehouseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> warehouseService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(warehouseMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("状态从启用改为禁用 -> 状态流转校验通过")
        void shouldAllowStatusTransitionFromActiveToInactive() {
            existingEntity.setStatus(1);
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);
            when(warehouseMapper.updateById(any())).thenReturn(1);

            updateDTO.setStatus(0);
            WarehouseVO result = warehouseService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(0, result.getStatus());
        }

        @Test
        @DisplayName("状态从禁用改为启用 -> 状态流转校验通过")
        void shouldAllowStatusTransitionFromInactiveToActive() {
            existingEntity.setStatus(0);
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);
            when(warehouseMapper.updateById(any())).thenReturn(1);

            updateDTO.setStatus(1);
            WarehouseVO result = warehouseService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(1, result.getStatus());
        }

        @Test
        @DisplayName("非法状态流转 -> 抛出BusinessException DATA_STATUS_INVALID")
        void shouldThrowExceptionOnInvalidStatusTransition() {
            existingEntity.setStatus(2);
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);

            updateDTO.setStatus(0);
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> warehouseService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete 删除仓库")
    class DeleteTests {

        @Test
        @DisplayName("仓库存在 -> 软删除成功")
        void shouldSoftDeleteSuccessfully() throws Exception {
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(warehouseService).removeById(1L);

            assertDoesNotThrow(() -> warehouseService.delete(1L));
            verify(warehouseService).removeById(1L);
        }

        @Test
        @DisplayName("仓库不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenWarehouseNotFound() {
            when(warehouseMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> warehouseService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(warehouseMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("仓库存在 -> deleteById被调用且传入正确ID")
        void shouldCallDeleteByIdWithCorrectId() throws Exception {
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(warehouseService).removeById(1L);

            warehouseService.delete(1L);

            verify(warehouseService).removeById(eq(1L));
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById 查询仓库详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回仓库VO")
        void shouldReturnWarehouseVoWhenExists() {
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);

            WarehouseVO result = warehouseService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getWarehouseCode(), result.getWarehouseCode());
            assertEquals(existingEntity.getWarehouseName(), result.getWarehouseName());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(warehouseMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> warehouseService.getById(99L));
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
            WarehouseQueryDTO query = new WarehouseQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<WarehouseEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(warehouseMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<WarehouseEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<WarehouseVO> result = warehouseService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getWarehouseCode(), result.getList().get(0).getWarehouseCode());
        }

        @Test
        @DisplayName("按名称筛选 -> 返回匹配结果")
        void shouldFilterByWarehouseName() {
            WarehouseQueryDTO query = new WarehouseQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setWarehouseName("测试");

            IPage<WarehouseEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(warehouseMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<WarehouseEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<WarehouseVO> result = warehouseService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按类型和状态筛选 -> 返回匹配结果")
        void shouldFilterByTypeAndStatus() {
            WarehouseQueryDTO query = new WarehouseQueryDTO();
            query.setPageNum(1);
            query.setPageSize(20);
            query.setWarehouseType("NORMAL");
            query.setStatus(1);

            IPage<WarehouseEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(warehouseMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<WarehouseEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<WarehouseVO> result = warehouseService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            WarehouseQueryDTO query = new WarehouseQueryDTO();

            IPage<WarehouseEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(warehouseMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<WarehouseEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<WarehouseVO> result = warehouseService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            WarehouseQueryDTO query = new WarehouseQueryDTO();
            query.setSortField("warehouseName");
            query.setSortOrder("ASC");

            IPage<WarehouseEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(warehouseMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<WarehouseEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> warehouseService.pageList(query));
            verify(warehouseMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("无效排序字段 -> 使用默认createTime排序")
        void shouldFallbackToDefaultSortWhenInvalidField() {
            WarehouseQueryDTO query = new WarehouseQueryDTO();
            query.setSortField("invalidField");
            query.setSortOrder("DESC");

            IPage<WarehouseEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(warehouseMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<WarehouseEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> warehouseService.pageList(query));
        }
    }

    // ==================== 异常与注解验证 ====================

    @Nested
    @DisplayName("异常与注解验证")
    class ExceptionAndAnnotationTests {

        @Test
        @DisplayName("BusinessException包含正确的code和msg")
        void shouldHaveCorrectErrorCodeInBusinessException() {
            BusinessException ex = new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "仓库编码已存在");

            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getMessage(), ex.getMsg());
        }

        @Test
        @DisplayName("@Transactional(rollbackFor=Exception.class)标注在create方法")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = WarehouseServiceImpl.class.getMethod("create", WarehouseCreateDTO.class);
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
            var method = WarehouseServiceImpl.class.getMethod("update", Long.class, WarehouseUpdateDTO.class);
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
            var method = WarehouseServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "delete应标注@Transactional");
            assertTrue(
                    Arrays.asList(annotation.rollbackFor()).contains(Exception.class),
                    "rollbackFor应为Exception.class"
            );
        }
    }

    // ==================== toVO转换 ====================

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(warehouseMapper.selectById(1L)).thenReturn(existingEntity);

            WarehouseVO result = warehouseService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getWarehouseCode(), result.getWarehouseCode());
            assertEquals(existingEntity.getWarehouseName(), result.getWarehouseName());
            assertEquals(existingEntity.getWarehouseType(), result.getWarehouseType());
            assertEquals(existingEntity.getAddress(), result.getAddress());
            assertEquals(existingEntity.getManagerId(), result.getManagerId());
            assertEquals(existingEntity.getPhone(), result.getPhone());
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
            createDTO.setWarehouseCode("W".repeat(100));
            when(warehouseMapper.insert(any())).thenReturn(1);

            assertDoesNotThrow(() -> warehouseService.create(createDTO));
        }

        @Test
        @DisplayName("特殊字符编码 -> 通过唯一性校验")
        void shouldAcceptSpecialCharactersInCode() {
            createDTO.setWarehouseCode("WH-001_TEST");
            when(warehouseMapper.insert(any())).thenReturn(1);

            WarehouseVO result = warehouseService.create(createDTO);

            assertNotNull(result);
            assertEquals("WH-001_TEST", result.getWarehouseCode());
        }

        @Test
        @DisplayName("managerId为null -> 正常创建")
        void shouldCreateWithNullManagerId() {
            createDTO.setManagerId(null);
            when(warehouseMapper.insert(any())).thenReturn(1);

            WarehouseVO result = warehouseService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getManagerId());
        }

        @Test
        @DisplayName("delete空ID -> mybatis-plus处理")
        void shouldHandleNullIdInDelete() {
            when(warehouseMapper.selectById(isNull())).thenReturn(null);

            assertThrows(BusinessException.class, () -> warehouseService.delete(null));
        }
    }
}
