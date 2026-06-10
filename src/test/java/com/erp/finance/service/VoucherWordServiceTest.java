package com.erp.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.finance.dto.VoucherWordCreateDTO;
import com.erp.finance.dto.VoucherWordQueryDTO;
import com.erp.finance.dto.VoucherWordUpdateDTO;
import com.erp.finance.entity.VoucherWordEntity;
import com.erp.finance.mapper.VoucherWordMapper;
import com.erp.finance.service.impl.VoucherWordServiceImpl;
import com.erp.finance.vo.VoucherWordVO;
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
@DisplayName("VoucherWordService 单元测试")
class VoucherWordServiceTest {

    @Mock
    private VoucherWordMapper voucherWordMapper;

    private VoucherWordServiceImpl voucherWordService;

    private VoucherWordCreateDTO createDTO;
    private VoucherWordUpdateDTO updateDTO;
    private VoucherWordEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        voucherWordService = spy(new VoucherWordServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(voucherWordService, voucherWordMapper);

        createDTO = new VoucherWordCreateDTO();
        createDTO.setWordName("记");
        createDTO.setWordCode("JI");
        createDTO.setSortOrder(1);
        createDTO.setStatus(1);

        updateDTO = new VoucherWordUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setWordName("收");
        updateDTO.setWordCode("SHOU");
        updateDTO.setSortOrder(2);
        updateDTO.setStatus(1);

        existingEntity = new VoucherWordEntity();
        existingEntity.setId(1L);
        existingEntity.setWordName("记");
        existingEntity.setWordCode("JI");
        existingEntity.setSortOrder(1);
        existingEntity.setStatus(1);
    }

    @AfterEach
    void tearDown() {
        reset(voucherWordMapper);
    }

    @Nested
    @DisplayName("create 新增凭证字")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateVoucherWordSuccessfully() {
            when(voucherWordMapper.insert(any(VoucherWordEntity.class))).thenReturn(1);

            VoucherWordVO result = voucherWordService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getWordName(), result.getWordName());
            assertEquals(createDTO.getWordCode(), result.getWordCode());
            assertEquals(createDTO.getSortOrder(), result.getSortOrder());
            assertEquals(createDTO.getStatus(), result.getStatus());

            ArgumentCaptor<VoucherWordEntity> captor = ArgumentCaptor.forClass(VoucherWordEntity.class);
            verify(voucherWordMapper).insert(captor.capture());
            assertEquals("记", captor.getValue().getWordName());
            assertEquals("JI", captor.getValue().getWordCode());
        }

        @Test
        @DisplayName("编码重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenCodeDuplicate() {
            when(voucherWordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> voucherWordService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(voucherWordMapper, never()).insert(any());
        }

        @Test
        @DisplayName("编码为空字符串 -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsEmpty() {
            createDTO.setWordCode("");
            when(voucherWordMapper.insert(any(VoucherWordEntity.class))).thenReturn(1);

            VoucherWordVO result = voucherWordService.create(createDTO);

            assertNotNull(result);
            verify(voucherWordMapper).insert(any());
        }

        @Test
        @DisplayName("编码为null -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsNull() {
            createDTO.setWordCode(null);
            when(voucherWordMapper.insert(any(VoucherWordEntity.class))).thenReturn(1);

            VoucherWordVO result = voucherWordService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getWordCode());
            verify(voucherWordMapper).insert(any());
        }

        @Test
        @DisplayName("create参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnCreateParam() throws NoSuchMethodException {
            var method = IVoucherWordService.class.getMethod("create", VoucherWordCreateDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0, "create应有参数");
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid, "create参数应标注@Valid");
        }
    }

    @Nested
    @DisplayName("update 更新凭证字")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateVoucherWordSuccessfully() {
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            when(voucherWordMapper.updateById(any(VoucherWordEntity.class))).thenReturn(1);

            VoucherWordVO result = voucherWordService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getWordName(), result.getWordName());
            assertEquals(updateDTO.getWordCode(), result.getWordCode());
            assertEquals(updateDTO.getSortOrder(), result.getSortOrder());

            ArgumentCaptor<VoucherWordEntity> captor = ArgumentCaptor.forClass(VoucherWordEntity.class);
            verify(voucherWordMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("SHOU", captor.getValue().getWordCode());
            assertEquals("收", captor.getValue().getWordName());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(voucherWordMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> voucherWordService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(voucherWordMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("编码重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenCodeDuplicateExcludingSelf() {
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            when(voucherWordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> voucherWordService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(voucherWordMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时编码不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameCodeWhenUpdatingSelf() {
            updateDTO.setWordCode("JI");
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            when(voucherWordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(voucherWordMapper.updateById(any())).thenReturn(1);

            VoucherWordVO result = voucherWordService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals("JI", result.getWordCode());
            verify(voucherWordMapper).updateById(any());
        }

        @Test
        @DisplayName("状态变更 0→1(禁用→启用) -> 正常更新")
        void shouldAllowStatusTransitionFromDisabledToEnabled() {
            existingEntity.setStatus(0);
            updateDTO.setStatus(1);
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            when(voucherWordMapper.updateById(any())).thenReturn(1);

            VoucherWordVO result = voucherWordService.update(1L, updateDTO);

            assertNotNull(result);
            verify(voucherWordMapper).updateById(any());
        }

        @Test
        @DisplayName("状态变更 1→0(启用→禁用) -> 正常更新")
        void shouldAllowStatusTransitionFromEnabledToDisabled() {
            existingEntity.setStatus(1);
            updateDTO.setStatus(0);
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            when(voucherWordMapper.updateById(any())).thenReturn(1);

            VoucherWordVO result = voucherWordService.update(1L, updateDTO);

            assertNotNull(result);
            verify(voucherWordMapper).updateById(any());
        }

        @Test
        @DisplayName("非法状态变更(如0→2) -> 抛出BusinessException")
        void shouldThrowExceptionOnInvalidStatusTransition() {
            existingEntity.setStatus(0);
            updateDTO.setStatus(2);
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> voucherWordService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(voucherWordMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("状态不变 -> 跳过状态校验，正常更新")
        void shouldSkipStatusValidationWhenUnchanged() {
            existingEntity.setStatus(1);
            updateDTO.setStatus(1);
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            when(voucherWordMapper.updateById(any())).thenReturn(1);

            assertDoesNotThrow(() -> voucherWordService.update(1L, updateDTO));
            verify(voucherWordMapper).updateById(any());
        }
    }

    @Nested
    @DisplayName("delete 删除凭证字")
    class DeleteTests {

        @Test
        @DisplayName("无关联数据 -> 软删除成功")
        void shouldSoftDeleteSuccessfully() {
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(voucherWordService).removeById(1L);

            assertDoesNotThrow(() -> voucherWordService.delete(1L));
            verify(voucherWordService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(voucherWordMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> voucherWordService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(voucherWordMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("关联数据校验: 当前实现仅检查存在性 -> 不检查关联数据(已知限制)")
        void shouldNoteThatDeleteDoesNotCheckRelatedData() {
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(voucherWordService).removeById(1L);

            assertDoesNotThrow(() -> voucherWordService.delete(1L));
        }
    }

    @Nested
    @DisplayName("getById 查询凭证字详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);

            VoucherWordVO result = voucherWordService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getWordName(), result.getWordName());
            assertEquals(existingEntity.getWordCode(), result.getWordCode());
            assertEquals(existingEntity.getSortOrder(), result.getSortOrder());
            assertEquals(existingEntity.getStatus(), result.getStatus());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(voucherWordMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> voucherWordService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果(默认按sortOrder升序)")
        void shouldReturnPagedResultsWithoutFilter() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<VoucherWordVO> result = voucherWordService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getWordCode(), result.getList().get(0).getWordCode());
        }

        @Test
        @DisplayName("按凭证字名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByWordNameFuzzy() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setWordName("记");

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<VoucherWordVO> result = voucherWordService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按凭证字编码筛选 -> 返回匹配结果")
        void shouldFilterByWordCode() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setWordCode("JI");

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<VoucherWordVO> result = voucherWordService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按启用状态筛选 -> 返回匹配结果")
        void shouldFilterByStatus() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setStatus(1);

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<VoucherWordVO> result = voucherWordService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setWordName("记");
            query.setWordCode("JI");
            query.setStatus(1);

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<VoucherWordVO> result = voucherWordService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<VoucherWordVO> result = voucherWordService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setSortField("sortOrder");
            query.setSortOrder("ASC");

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> voucherWordService.pageList(query));
            verify(voucherWordMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("DESC降序排序 -> 正确传递排序参数")
        void shouldHandleDescendingSortOrder() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setSortField("createTime");
            query.setSortOrder("DESC");

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> voucherWordService.pageList(query));
        }

        @Test
        @DisplayName("无效排序字段 -> 使用默认排序(createTime降序)")
        void shouldFallbackToDefaultSortWhenInvalidField() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setSortField("invalidField");
            query.setSortOrder("DESC");

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> voucherWordService.pageList(query));
        }

        @Test
        @DisplayName("无排序字段 -> 默认按sortOrder升序")
        void shouldUseDefaultSortOrderWhenNoSortField() {
            VoucherWordQueryDTO query = new VoucherWordQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<VoucherWordEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(voucherWordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<VoucherWordEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> voucherWordService.pageList(query));
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = VoucherWordServiceImpl.class.getMethod("create", VoucherWordCreateDTO.class);
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
            var method = VoucherWordServiceImpl.class.getMethod("update", Long.class, VoucherWordUpdateDTO.class);
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
            var method = VoucherWordServiceImpl.class.getMethod("delete", Long.class);
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
            var method = VoucherWordServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = VoucherWordServiceImpl.class.getMethod("pageList", VoucherWordQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "pageList应标注@Transactional");
            assertTrue(annotation.readOnly(), "pageList应为readOnly=true");
        }
    }

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("create sortOrder=0 -> 正常创建")
        void shouldCreateWithZeroSortOrder() {
            createDTO.setSortOrder(0);
            when(voucherWordMapper.insert(any())).thenReturn(1);

            VoucherWordVO result = voucherWordService.create(createDTO);

            assertNotNull(result);
            assertEquals(0, result.getSortOrder());
        }

        @Test
        @DisplayName("create 超长名称 -> 正常创建")
        void shouldCreateWithLongWordName() {
            createDTO.setWordName("A".repeat(100));
            when(voucherWordMapper.insert(any())).thenReturn(1);

            VoucherWordVO result = voucherWordService.create(createDTO);

            assertNotNull(result);
            assertEquals(100, result.getWordName().length());
        }

        @Test
        @DisplayName("create status=0(禁用状态) -> 正常创建")
        void shouldCreateWithDisabledStatus() {
            createDTO.setStatus(0);
            when(voucherWordMapper.insert(any())).thenReturn(1);

            VoucherWordVO result = voucherWordService.create(createDTO);

            assertNotNull(result);
            assertEquals(0, result.getStatus());
        }

        @Test
        @DisplayName("update newStatus=null -> 跳过状态校验，正常更新")
        void shouldSkipStatusValidationWhenNewStatusNull() {
            updateDTO.setStatus(null);
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);
            when(voucherWordMapper.updateById(any())).thenReturn(1);

            assertDoesNotThrow(() -> voucherWordService.update(1L, updateDTO));
            verify(voucherWordMapper).updateById(any());
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(voucherWordMapper.selectById(1L)).thenReturn(existingEntity);

            VoucherWordVO result = voucherWordService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getWordName(), result.getWordName());
            assertEquals(existingEntity.getWordCode(), result.getWordCode());
            assertEquals(existingEntity.getSortOrder(), result.getSortOrder());
            assertEquals(existingEntity.getStatus(), result.getStatus());
        }
    }
}
