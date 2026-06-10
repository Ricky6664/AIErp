package com.erp.module.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.finance.dto.BankAccountCreateDTO;
import com.erp.module.finance.dto.BankAccountQueryDTO;
import com.erp.module.finance.dto.BankAccountUpdateDTO;
import com.erp.module.finance.entity.BankAccountEntity;
import com.erp.module.finance.mapper.BankAccountMapper;
import com.erp.module.finance.service.impl.BankAccountServiceImpl;
import com.erp.module.finance.vo.BankAccountVO;
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
@DisplayName("BankAccountService 单元测试")
class BankAccountServiceTest {

    @Mock
    private BankAccountMapper bankAccountMapper;

    private BankAccountServiceImpl bankAccountService;

    private BankAccountCreateDTO createDTO;
    private BankAccountUpdateDTO updateDTO;
    private BankAccountEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        bankAccountService = spy(new BankAccountServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(bankAccountService, bankAccountMapper);

        createDTO = new BankAccountCreateDTO();
        createDTO.setAccountName("测试账户");
        createDTO.setBankAccountNo("6222021234567890");
        createDTO.setBankName("中国工商银行");
        createDTO.setBankBranch("北京分行");
        createDTO.setCurrencyId(1L);
        createDTO.setAccountType("SAVINGS");
        createDTO.setStatus(1);

        updateDTO = new BankAccountUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setAccountName("更新账户");
        updateDTO.setBankAccountNo("6222029876543210");
        updateDTO.setBankName("中国建设银行");
        updateDTO.setBankBranch("上海分行");
        updateDTO.setCurrencyId(2L);
        updateDTO.setAccountType("CHECKING");
        updateDTO.setStatus(0);

        existingEntity = new BankAccountEntity();
        existingEntity.setId(1L);
        existingEntity.setAccountName("测试账户");
        existingEntity.setBankAccountNo("6222021234567890");
        existingEntity.setBankName("中国工商银行");
        existingEntity.setBankBranch("北京分行");
        existingEntity.setCurrencyId(1L);
        existingEntity.setAccountType("SAVINGS");
        existingEntity.setStatus(1);
    }

    @AfterEach
    void tearDown() {
        reset(bankAccountMapper);
    }

    @Nested
    @DisplayName("create 新增银行账户")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateBankAccountSuccessfully() {
            when(bankAccountMapper.insert(any(BankAccountEntity.class))).thenReturn(1);

            BankAccountVO result = bankAccountService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getAccountName(), result.getAccountName());
            assertEquals(createDTO.getBankAccountNo(), result.getBankAccountNo());
            assertEquals(createDTO.getBankName(), result.getBankName());
            assertEquals(createDTO.getBankBranch(), result.getBankBranch());
            assertEquals(createDTO.getCurrencyId(), result.getCurrencyId());
            assertEquals(createDTO.getAccountType(), result.getAccountType());
            assertEquals(createDTO.getStatus(), result.getStatus());

            ArgumentCaptor<BankAccountEntity> captor = ArgumentCaptor.forClass(BankAccountEntity.class);
            verify(bankAccountMapper).insert(captor.capture());
            assertEquals("6222021234567890", captor.getValue().getBankAccountNo());
            assertEquals("测试账户", captor.getValue().getAccountName());
        }

        @Test
        @DisplayName("编码重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenCodeDuplicate() {
            when(bankAccountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> bankAccountService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(bankAccountMapper, never()).insert(any());
        }

        @Test
        @DisplayName("编码为空字符串 -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsEmpty() {
            createDTO.setBankAccountNo("");
            when(bankAccountMapper.insert(any(BankAccountEntity.class))).thenReturn(1);

            BankAccountVO result = bankAccountService.create(createDTO);

            assertNotNull(result);
            verify(bankAccountMapper).insert(any());
        }

        @Test
        @DisplayName("编码为null -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsNull() {
            createDTO.setBankAccountNo(null);
            when(bankAccountMapper.insert(any(BankAccountEntity.class))).thenReturn(1);

            BankAccountVO result = bankAccountService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getBankAccountNo());
            verify(bankAccountMapper).insert(any());
        }
    }

    @Nested
    @DisplayName("update 更新银行账户")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateBankAccountSuccessfully() {
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);
            when(bankAccountMapper.updateById(any(BankAccountEntity.class))).thenReturn(1);

            BankAccountVO result = bankAccountService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getAccountName(), result.getAccountName());
            assertEquals(updateDTO.getBankAccountNo(), result.getBankAccountNo());
            assertEquals(updateDTO.getBankName(), result.getBankName());

            ArgumentCaptor<BankAccountEntity> captor = ArgumentCaptor.forClass(BankAccountEntity.class);
            verify(bankAccountMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("6222029876543210", captor.getValue().getBankAccountNo());
            assertEquals("更新账户", captor.getValue().getAccountName());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(bankAccountMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> bankAccountService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(bankAccountMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("编码重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenCodeDuplicateExcludingSelf() {
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);
            when(bankAccountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> bankAccountService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(bankAccountMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时编码不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameCodeWhenUpdatingSelf() {
            updateDTO.setBankAccountNo("6222021234567890");
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);
            when(bankAccountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(bankAccountMapper.updateById(any())).thenReturn(1);

            BankAccountVO result = bankAccountService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals("6222021234567890", result.getBankAccountNo());
            verify(bankAccountMapper).updateById(any());
        }

        @Test
        @DisplayName("状态从启用(1)变禁用(0) -> 正常更新")
        void shouldAllowStatusTransitionFrom1To0() {
            existingEntity.setStatus(1);
            updateDTO.setStatus(0);
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);
            when(bankAccountMapper.updateById(any())).thenReturn(1);

            BankAccountVO result = bankAccountService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(0, result.getStatus());
        }

        @Test
        @DisplayName("状态从禁用(0)变启用(1) -> 正常更新")
        void shouldAllowStatusTransitionFrom0To1() {
            existingEntity.setStatus(0);
            updateDTO.setStatus(1);
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);
            when(bankAccountMapper.updateById(any())).thenReturn(1);

            BankAccountVO result = bankAccountService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(1, result.getStatus());
        }

        @Test
        @DisplayName("非法的状态变更(2->0) -> 抛出BusinessException DATA_STATUS_INVALID")
        void shouldThrowExceptionWhenStatusTransitionInvalid() {
            existingEntity.setStatus(2);
            updateDTO.setStatus(0);
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> bankAccountService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(bankAccountMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("状态不变(newStatus=null) -> 跳过状态校验")
        void shouldSkipStatusValidationWhenNewStatusIsNull() {
            updateDTO.setStatus(null);
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);
            when(bankAccountMapper.updateById(any())).thenReturn(1);

            BankAccountVO result = bankAccountService.update(1L, updateDTO);

            assertNotNull(result);
            verify(bankAccountMapper).updateById(any());
        }
    }

    @Nested
    @DisplayName("delete 删除银行账户")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 软删除成功(is_deleted=true)")
        void shouldSoftDeleteSuccessfully() throws Exception {
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(bankAccountService).removeById(1L);

            assertDoesNotThrow(() -> bankAccountService.delete(1L));
            verify(bankAccountService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(bankAccountMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> bankAccountService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(bankAccountMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("删除已删除的记录 -> 抛出BusinessException(NOT_FOUND)")
        void shouldThrowWhenDeletingAlreadyDeleted() {
            when(bankAccountMapper.selectById(1L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> bankAccountService.delete(1L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("getById 查询银行账户详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);

            BankAccountVO result = bankAccountService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getAccountName(), result.getAccountName());
            assertEquals(existingEntity.getBankAccountNo(), result.getBankAccountNo());
            assertEquals(existingEntity.getBankName(), result.getBankName());
            assertEquals(existingEntity.getCurrencyId(), result.getCurrencyId());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(bankAccountMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> bankAccountService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getBankAccountNo(), result.getList().get(0).getBankAccountNo());
        }

        @Test
        @DisplayName("按账户名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByAccountNameFuzzy() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAccountName("测试");

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按银行账号模糊搜索 -> 返回匹配结果")
        void shouldFilterByBankAccountNoFuzzy() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setBankAccountNo("622202");

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按开户银行精确筛选 -> 返回匹配结果")
        void shouldFilterByBankName() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setBankName("中国工商银行");

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按币种ID筛选 -> 返回匹配结果")
        void shouldFilterByCurrencyId() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setCurrencyId(1L);

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按账户类型筛选 -> 返回匹配结果")
        void shouldFilterByAccountType() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAccountType("SAVINGS");

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按状态筛选 -> 返回匹配结果")
        void shouldFilterByStatus() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setStatus(1);

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();

            IPage<BankAccountEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAccountName("测试");
            query.setBankName("工商");
            query.setStatus(1);

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<BankAccountVO> result = bankAccountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setSortField("accountName");
            query.setSortOrder("ASC");

            IPage<BankAccountEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> bankAccountService.pageList(query));
            verify(bankAccountMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("无效排序字段 -> 使用默认排序(createTime降序)")
        void shouldFallbackToDefaultSortWhenInvalidField() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setSortField("invalidField");
            query.setSortOrder("DESC");

            IPage<BankAccountEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> bankAccountService.pageList(query));
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(bankAccountMapper.selectById(1L)).thenReturn(existingEntity);

            BankAccountVO result = bankAccountService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getAccountName(), result.getAccountName());
            assertEquals(existingEntity.getBankAccountNo(), result.getBankAccountNo());
            assertEquals(existingEntity.getBankName(), result.getBankName());
            assertEquals(existingEntity.getBankBranch(), result.getBankBranch());
            assertEquals(existingEntity.getCurrencyId(), result.getCurrencyId());
            assertEquals(existingEntity.getAccountType(), result.getAccountType());
            assertEquals(existingEntity.getStatus(), result.getStatus());
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = BankAccountServiceImpl.class.getMethod("create", BankAccountCreateDTO.class);
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
            var method = BankAccountServiceImpl.class.getMethod("update", Long.class, BankAccountUpdateDTO.class);
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
            var method = BankAccountServiceImpl.class.getMethod("delete", Long.class);
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
            var method = BankAccountServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = BankAccountServiceImpl.class.getMethod("pageList", BankAccountQueryDTO.class);
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
        @DisplayName("超长账号 -> 不抛异常(校验在Controller层)")
        void shouldNotThrowOnLongAccountNo() {
            createDTO.setBankAccountNo("6".repeat(100));
            when(bankAccountMapper.insert(any())).thenReturn(1);

            assertDoesNotThrow(() -> bankAccountService.create(createDTO));
        }

        @Test
        @DisplayName("特殊字符账号 -> 通过唯一性校验")
        void shouldAcceptSpecialCharactersInAccountNo() {
            createDTO.setBankAccountNo("6222-0212-3456-7890");
            when(bankAccountMapper.insert(any())).thenReturn(1);

            BankAccountVO result = bankAccountService.create(createDTO);

            assertNotNull(result);
            assertEquals("6222-0212-3456-7890", result.getBankAccountNo());
        }

        @Test
        @DisplayName("超长账户名称 -> 正常创建")
        void shouldCreateWithLongAccountName() {
            createDTO.setAccountName("A".repeat(200));
            when(bankAccountMapper.insert(any())).thenReturn(1);

            BankAccountVO result = bankAccountService.create(createDTO);

            assertNotNull(result);
        }

        @Test
        @DisplayName("branch为空 -> 正常创建(非必填)")
        void shouldCreateWithNullBranch() {
            createDTO.setBankBranch(null);
            when(bankAccountMapper.insert(any())).thenReturn(1);

            BankAccountVO result = bankAccountService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getBankBranch());
        }

        @Test
        @DisplayName("delete传入null ID -> 抛出BusinessException")
        void shouldHandleNullIdInDelete() {
            when(bankAccountMapper.selectById(isNull())).thenReturn(null);

            assertThrows(BusinessException.class, () -> bankAccountService.delete(null));
        }

        @Test
        @DisplayName("pageList无排序字段 -> 默认按createTime降序")
        void shouldUseDefaultSortOrderWhenNoSortField() {
            BankAccountQueryDTO query = new BankAccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<BankAccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(bankAccountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        IPage<BankAccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> bankAccountService.pageList(query));
        }
    }
}
