package com.erp.module.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.finance.dto.AccountCreateDTO;
import com.erp.module.finance.dto.AccountQueryDTO;
import com.erp.module.finance.dto.AccountUpdateDTO;
import com.erp.module.finance.entity.AccountEntity;
import com.erp.module.finance.mapper.AccountMapper;
import com.erp.module.finance.service.impl.AccountServiceImpl;
import com.erp.module.finance.vo.AccountVO;
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
@DisplayName("AccountService 单元测试")
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    private AccountServiceImpl accountService;

    private AccountCreateDTO createDTO;
    private AccountUpdateDTO updateDTO;
    private AccountEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        accountService = spy(new AccountServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(accountService, accountMapper);

        createDTO = new AccountCreateDTO();
        createDTO.setAccountCode("1001");
        createDTO.setAccountName("现金");
        createDTO.setParentId(0L);
        createDTO.setLevel(1);
        createDTO.setAccountType(1);
        createDTO.setCategory("ASSET");
        createDTO.setBalanceDirection(1);
        createDTO.setIsLeaf(true);
        createDTO.setIsCash(true);
        createDTO.setIsBank(false);
        createDTO.setIsForeignCurrency("N");
        createDTO.setIsAuxiliary("N");

        updateDTO = new AccountUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setAccountCode("1002");
        updateDTO.setAccountName("银行存款");
        updateDTO.setParentId(0L);
        updateDTO.setLevel(1);
        updateDTO.setAccountType(1);
        updateDTO.setCategory("ASSET");
        updateDTO.setBalanceDirection(1);
        updateDTO.setIsLeaf(true);
        updateDTO.setIsCash(false);
        updateDTO.setIsBank(true);
        updateDTO.setIsForeignCurrency("N");
        updateDTO.setIsAuxiliary("N");

        existingEntity = new AccountEntity();
        existingEntity.setId(1L);
        existingEntity.setAccountCode("1001");
        existingEntity.setAccountName("现金");
        existingEntity.setParentId(0L);
        existingEntity.setLevel(1);
        existingEntity.setAccountType(1);
        existingEntity.setCategory("ASSET");
        existingEntity.setBalanceDirection(1);
        existingEntity.setIsLeaf(true);
        existingEntity.setIsCash(true);
        existingEntity.setIsBank(false);
        existingEntity.setIsForeignCurrency("N");
        existingEntity.setIsAuxiliary("N");
    }

    @AfterEach
    void tearDown() {
        reset(accountMapper);
    }

    // ==================== 5.1.1 create正常数据 ====================

    @Nested
    @DisplayName("create 新增会计科目")
    class CreateTests {

        @Test
        @DisplayName("正常数据 -> 返回VO，数据已持久化")
        void shouldCreateAccountSuccessfully() {
            when(accountMapper.insert(any(AccountEntity.class))).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            assertEquals(createDTO.getAccountCode(), result.getAccountCode());
            assertEquals(createDTO.getAccountName(), result.getAccountName());
            assertEquals(createDTO.getParentId(), result.getParentId());
            assertEquals(createDTO.getLevel(), result.getLevel());
            assertEquals(createDTO.getAccountType(), result.getAccountType());
            assertEquals(createDTO.getCategory(), result.getCategory());
            assertEquals(createDTO.getBalanceDirection(), result.getBalanceDirection());
            assertTrue(result.getIsLeaf());
            assertTrue(result.getIsCash());
            assertFalse(result.getIsBank());
            assertEquals("N", result.getIsForeignCurrency());
            assertEquals("N", result.getIsAuxiliary());

            ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
            verify(accountMapper).insert(captor.capture());
            assertEquals("1001", captor.getValue().getAccountCode());
            assertEquals("现金", captor.getValue().getAccountName());
        }

        // ==================== 5.1.2 create编码重复 ====================

        @Test
        @DisplayName("编码重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenCodeDuplicate() {
            when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountService.create(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(accountMapper, never()).insert(any());
        }

        @Test
        @DisplayName("编码为空字符串 -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsEmpty() {
            createDTO.setAccountCode("");
            when(accountMapper.insert(any(AccountEntity.class))).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            verify(accountMapper).insert(any());
        }

        @Test
        @DisplayName("编码为null -> 跳过唯一性校验，正常创建")
        void shouldSkipUniquenessCheckWhenCodeIsNull() {
            createDTO.setAccountCode(null);
            when(accountMapper.insert(any(AccountEntity.class))).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getAccountCode());
            verify(accountMapper).insert(any());
        }

        // ==================== 5.1.3 create必填字段缺失(注解验证) ====================

        @Test
        @DisplayName("create参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnCreateParam() throws NoSuchMethodException {
            var method = IAccountService.class.getMethod("create", AccountCreateDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0, "create应有参数");
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid, "create参数应标注@Valid");
        }
    }

    // ==================== 5.1.4 update正常数据 ====================

    @Nested
    @DisplayName("update 更新会计科目")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 返回更新后VO")
        void shouldUpdateAccountSuccessfully() {
            when(accountMapper.selectById(1L)).thenReturn(existingEntity);
            when(accountMapper.updateById(any(AccountEntity.class))).thenReturn(1);

            AccountVO result = accountService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals(updateDTO.getAccountCode(), result.getAccountCode());
            assertEquals(updateDTO.getAccountName(), result.getAccountName());
            assertTrue(result.getIsBank());

            ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
            verify(accountMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("1002", captor.getValue().getAccountCode());
            assertEquals("银行存款", captor.getValue().getAccountName());
        }

        // ==================== 5.1.5 update不存在ID ====================

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(accountMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountService.update(99L, updateDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(accountMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("编码重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenCodeDuplicateExcludingSelf() {
            when(accountMapper.selectById(1L)).thenReturn(existingEntity);
            when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountService.update(1L, updateDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(accountMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时编码不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameCodeWhenUpdatingSelf() {
            updateDTO.setAccountCode("1001");
            when(accountMapper.selectById(1L)).thenReturn(existingEntity);
            when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(accountMapper.updateById(any())).thenReturn(1);

            AccountVO result = accountService.update(1L, updateDTO);

            assertNotNull(result);
            assertEquals("1001", result.getAccountCode());
            verify(accountMapper).updateById(any());
        }
    }

    // ==================== 5.1.6 & 5.1.7 delete ====================

    @Nested
    @DisplayName("delete 删除会计科目")
    class DeleteTests {

        @Test
        @DisplayName("无关联数据 -> 软删除成功(is_deleted=true)")
        void shouldSoftDeleteSuccessfully() {
            when(accountMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(accountService).removeById(1L);

            assertDoesNotThrow(() -> accountService.delete(1L));
            verify(accountService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(accountMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(accountMapper, never()).deleteById(any());
        }

        @Test
        @DisplayName("关联校验: 当前实现仅检查存在性 -> 不检查关联数据(已知限制)")
        void shouldNoteThatDeleteDoesNotCheckRelatedData() {
            when(accountMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(accountService).removeById(1L);

            assertDoesNotThrow(() -> accountService.delete(1L));
        }
    }

    // ==================== 5.1.8 pageList ====================

    @Nested
    @DisplayName("pageList 分页查询")
    class PageListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getAccountCode(), result.getList().get(0).getAccountCode());
        }

        @Test
        @DisplayName("按科目编码模糊搜索 -> 返回匹配结果")
        void shouldFilterByAccountCodeFuzzy() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAccountCode("100");

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按科目名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByAccountNameFuzzy() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAccountName("现金");

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按上级科目ID筛选 -> 返回匹配结果")
        void shouldFilterByParentId() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setParentId(0L);

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按科目类型筛选 -> 返回匹配结果")
        void shouldFilterByAccountType() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAccountType(1);

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按科目类别筛选 -> 返回匹配结果")
        void shouldFilterByCategory() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setCategory("ASSET");

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("按是否末级筛选 -> 返回匹配结果")
        void shouldFilterByIsLeaf() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setIsLeaf(true);

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("多条件联合筛选 -> 返回结果与筛选条件一致")
        void shouldFilterByMultipleConditions() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);
            query.setAccountCode("100");
            query.setAccountName("现金");
            query.setAccountType(1);
            query.setCategory("ASSET");
            query.setIsLeaf(true);

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(existingEntity));
            mockPage.setTotal(1);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(mockPage.getRecords());
                        page.setTotal(mockPage.getTotal());
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getPages());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            AccountQueryDTO query = new AccountQueryDTO();

            IPage<AccountEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<AccountVO> result = accountService.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("ASC升序排序 -> 正确传递排序参数")
        void shouldHandleAscendingSortOrder() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setSortField("accountCode");
            query.setSortOrder("ASC");

            IPage<AccountEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> accountService.pageList(query));
            verify(accountMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("DESC降序排序 -> 正确传递排序参数")
        void shouldHandleDescendingSortOrder() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setSortField("createTime");
            query.setSortOrder("DESC");

            IPage<AccountEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> accountService.pageList(query));
        }

        @Test
        @DisplayName("无效排序字段 -> 使用默认排序(createTime降序)")
        void shouldFallbackToDefaultSortWhenInvalidField() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setSortField("invalidField");
            query.setSortOrder("DESC");

            IPage<AccountEntity> mockPage = new Page<>(1, 20);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> accountService.pageList(query));
        }

        @Test
        @DisplayName("无排序字段 -> 默认按createTime降序")
        void shouldUseDefaultSortOrderWhenNoSortField() {
            AccountQueryDTO query = new AccountQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            IPage<AccountEntity> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);

            when(accountMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<AccountEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> accountService.pageList(query));
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById 查询会计科目详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(accountMapper.selectById(1L)).thenReturn(existingEntity);

            AccountVO result = accountService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getAccountCode(), result.getAccountCode());
            assertEquals(existingEntity.getAccountName(), result.getAccountName());
            assertEquals(existingEntity.getLevel(), result.getLevel());
            assertEquals(existingEntity.getAccountType(), result.getAccountType());
            assertEquals(existingEntity.getCategory(), result.getCategory());
            assertEquals(existingEntity.getBalanceDirection(), result.getBalanceDirection());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(accountMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> accountService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== 5.1.9 事务回滚 ====================

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("create标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnCreate() throws NoSuchMethodException {
            var method = AccountServiceImpl.class.getMethod("create", AccountCreateDTO.class);
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
            var method = AccountServiceImpl.class.getMethod("update", Long.class, AccountUpdateDTO.class);
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
            var method = AccountServiceImpl.class.getMethod("delete", Long.class);
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
            var method = AccountServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "getById应标注@Transactional");
            assertTrue(annotation.readOnly(), "getById应为readOnly=true");
        }

        @Test
        @DisplayName("pageList标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnPageList() throws NoSuchMethodException {
            var method = AccountServiceImpl.class.getMethod("pageList", AccountQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation, "pageList应标注@Transactional");
            assertTrue(annotation.readOnly(), "pageList应为readOnly=true");
        }
    }

    // ==================== 边界场景 ====================

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("create 所有可选字段为null -> 正常创建")
        void shouldCreateWithNullOptionalFields() {
            createDTO.setIsCash(null);
            createDTO.setIsBank(null);
            createDTO.setIsForeignCurrency(null);
            createDTO.setIsAuxiliary(null);
            when(accountMapper.insert(any())).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            assertNull(result.getIsCash());
            assertNull(result.getIsBank());
        }

        @Test
        @DisplayName("创建末级科目(isLeaf=true) -> 正确设置末级标记")
        void shouldSetLeafFlagCorrectly() {
            createDTO.setIsLeaf(true);
            when(accountMapper.insert(any())).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            assertTrue(result.getIsLeaf());
        }

        @Test
        @DisplayName("创建非末级科目(isLeaf=false) -> 正确设置非末级标记")
        void shouldSetNonLeafFlagCorrectly() {
            createDTO.setIsLeaf(false);
            when(accountMapper.insert(any())).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            assertFalse(result.getIsLeaf());
        }

        @Test
        @DisplayName("超长科目名称(200字符) -> 正常创建")
        void shouldCreateWithLongAccountName() {
            createDTO.setAccountName("A".repeat(200));
            when(accountMapper.insert(any())).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            assertEquals(200, result.getAccountName().length());
        }

        @Test
        @DisplayName("余额方向: 借方(1) vs 贷方(-1) -> 正确保存")
        void shouldSaveBalanceDirectionCorrectly() {
            createDTO.setBalanceDirection(-1);
            when(accountMapper.insert(any())).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            assertEquals(-1, result.getBalanceDirection());
        }

        @Test
        @DisplayName("多级科目(parentId非0) -> 正确保存父子关系")
        void shouldSaveParentChildRelationship() {
            createDTO.setParentId(100L);
            createDTO.setLevel(2);
            when(accountMapper.insert(any())).thenReturn(1);

            AccountVO result = accountService.create(createDTO);

            assertNotNull(result);
            assertEquals(100L, result.getParentId());
            assertEquals(2, result.getLevel());
        }
    }

    // ==================== toVO 实体转换 ====================

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(accountMapper.selectById(1L)).thenReturn(existingEntity);

            AccountVO result = accountService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getAccountCode(), result.getAccountCode());
            assertEquals(existingEntity.getAccountName(), result.getAccountName());
            assertEquals(existingEntity.getParentId(), result.getParentId());
            assertEquals(existingEntity.getLevel(), result.getLevel());
            assertEquals(existingEntity.getAccountType(), result.getAccountType());
            assertEquals(existingEntity.getCategory(), result.getCategory());
            assertEquals(existingEntity.getBalanceDirection(), result.getBalanceDirection());
            assertEquals(existingEntity.getIsLeaf(), result.getIsLeaf());
            assertEquals(existingEntity.getIsCash(), result.getIsCash());
            assertEquals(existingEntity.getIsBank(), result.getIsBank());
            assertEquals(existingEntity.getIsForeignCurrency(), result.getIsForeignCurrency());
            assertEquals(existingEntity.getIsAuxiliary(), result.getIsAuxiliary());
        }
    }
}
