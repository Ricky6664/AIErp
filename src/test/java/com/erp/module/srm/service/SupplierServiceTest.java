package com.erp.module.srm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.srm.dto.SupplierDTO;
import com.erp.module.srm.dto.SupplierQueryDTO;
import com.erp.module.srm.entity.Supplier;
import com.erp.module.srm.mapper.SupplierMapper;
import com.erp.module.srm.service.impl.SupplierServiceImpl;
import com.erp.module.srm.vo.SupplierVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService 单元测试")
class SupplierServiceTest {

    @Mock
    private SupplierMapper supplierMapper;

    @Spy
    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier existingEntity;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(supplierService, "baseMapper", supplierMapper);

        existingEntity = new Supplier();
        existingEntity.setId(1L);
        existingEntity.setSupplierCode("SUP-001");
        existingEntity.setSupplierName("华为技术有限公司");
        existingEntity.setShortName("华为");
        existingEntity.setPhone("0755-28780808");
        existingEntity.setEmail("huawei@huawei.com");
        existingEntity.setClassId(10L);
        existingEntity.setBuyerId(100L);
        existingEntity.setBuyerDeptId(200L);
        existingEntity.setCompanyId(1L);
        existingEntity.setSource("SELF_REGISTER");
        existingEntity.setAuditStatus("DRAFT");
        existingEntity.setIsActive(true);
        existingEntity.setRemark("测试供应商");
    }

    // ==================== 分页查询 ====================

    @Nested
    @DisplayName("list 分页查询")
    class ListTests {

        @Test
        @DisplayName("按供应商名称模糊查询 -> 返回分页数据")
        void shouldReturnPageWithNameFilter() {
            SupplierQueryDTO query = new SupplierQueryDTO();
            query.setSupplierName("华为");
            query.setPageNum(1);
            query.setPageSize(10);

            Page<Supplier> page = new Page<>(1, 10);
            page.setRecords(List.of(existingEntity));
            page.setTotal(1);

            when(supplierMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            IPage<SupplierVO> result = supplierService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals("华为技术有限公司", result.getRecords().get(0).getSupplierName());
            assertEquals("SUP-001", result.getRecords().get(0).getSupplierCode());
        }

        @Test
        @DisplayName("按审核状态过滤 -> 返回过滤结果")
        void shouldReturnFilteredByAuditStatus() {
            SupplierQueryDTO query = new SupplierQueryDTO();
            query.setAuditStatus("DRAFT");
            query.setPageNum(1);
            query.setPageSize(10);

            Page<Supplier> page = new Page<>(1, 10);
            page.setRecords(List.of(existingEntity));
            page.setTotal(1);

            when(supplierMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            IPage<SupplierVO> result = supplierService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("查询无结果 -> 返回空列表")
        void shouldReturnEmptyPageWhenNoData() {
            SupplierQueryDTO query = new SupplierQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            Page<Supplier> page = new Page<>(1, 10);
            page.setRecords(List.of());
            page.setTotal(0);

            when(supplierMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            IPage<SupplierVO> result = supplierService.list(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }
    }

    // ==================== 详情查询 ====================

    @Nested
    @DisplayName("getById 详情查询")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVOWhenIdExists() {
            when(supplierMapper.selectById(1L)).thenReturn(existingEntity);

            SupplierVO result = supplierService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("SUP-001", result.getSupplierCode());
            assertEquals("华为技术有限公司", result.getSupplierName());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenIdNotExists() {
            when(supplierMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> supplierService.getById(999L));
            assertEquals(50002, ex.getCode());
        }
    }

    // ==================== 新增 ====================

    @Nested
    @DisplayName("save 新增")
    class SaveTests {

        @Test
        @DisplayName("合法数据 -> 保存成功")
        void shouldSaveSuccessfully() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-002");
            dto.setSupplierName("中兴通讯");
            dto.setEmail("zte@zte.com.cn");
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.insert(any(Supplier.class))).thenReturn(1);

            assertDoesNotThrow(() -> supplierService.save(dto));
            verify(supplierMapper).insert(any(Supplier.class));
        }

        @Test
        @DisplayName("同公司主体下名称重复 -> 抛出BusinessException(DATA_ALREADY_EXISTS)")
        void shouldThrowWhenNameDuplicateInSameCompany() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-003");
            dto.setSupplierName("华为技术有限公司");
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> supplierService.save(dto));
            assertEquals(50003, ex.getCode());
            verify(supplierMapper, never()).insert(any());
        }

        @Test
        @DisplayName("邮箱格式不正确 -> 抛出BusinessException(PARAM_FORMAT_ERROR)")
        void shouldThrowWhenEmailFormatInvalid() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-004");
            dto.setSupplierName("测试供应商");
            dto.setEmail("invalid-email");
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> supplierService.save(dto));
            assertEquals(30003, ex.getCode());
            verify(supplierMapper, never()).insert(any());
        }

        @Test
        @DisplayName("邮箱为null -> 保存成功（邮箱非必填）")
        void shouldSaveSuccessfullyWhenEmailIsNull() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-005");
            dto.setSupplierName("无邮箱供应商");
            dto.setEmail(null);
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.insert(any(Supplier.class))).thenReturn(1);

            assertDoesNotThrow(() -> supplierService.save(dto));
            verify(supplierMapper).insert(any(Supplier.class));
        }
    }

    // ==================== 修改 ====================

    @Nested
    @DisplayName("update 修改")
    class UpdateTests {

        @Test
        @DisplayName("合法数据 -> 修改成功")
        void shouldUpdateSuccessfully() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-001");
            dto.setSupplierName("华为技术有限公司(改)");
            dto.setAuditStatus("DRAFT");
            dto.setIsActive(true);

            when(supplierMapper.selectById(1L)).thenReturn(existingEntity);
            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.updateById(any(Supplier.class))).thenReturn(1);

            assertDoesNotThrow(() -> supplierService.update(1L, dto));
            verify(supplierMapper).updateById(any(Supplier.class));
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenIdNotExistsForUpdate() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-999");
            dto.setSupplierName("不存在的供应商");
            dto.setIsActive(true);

            when(supplierMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> supplierService.update(999L, dto));
            assertEquals(50002, ex.getCode());
            verify(supplierMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("修改时名称与其他记录冲突 -> 抛出BusinessException")
        void shouldThrowWhenNameConflictsWithOtherRecord() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-001");
            dto.setSupplierName("已存在供应商");
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectById(1L)).thenReturn(existingEntity);
            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> supplierService.update(1L, dto));
            assertEquals(50003, ex.getCode());
            verify(supplierMapper, never()).updateById(any());
        }
    }

    // ==================== 删除 ====================

    @Nested
    @DisplayName("delete 删除")
    class DeleteTests {

        @Test
        @DisplayName("ID存在 -> 删除成功")
        void shouldDeleteSuccessfully() {
            when(supplierMapper.selectById(1L)).thenReturn(existingEntity);
            doReturn(true).when(supplierService).removeById(1L);

            assertDoesNotThrow(() -> supplierService.delete(1L));
            verify(supplierService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenIdNotExistsForDelete() {
            when(supplierMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> supplierService.delete(999L));
            assertEquals(50002, ex.getCode());
            verify(supplierMapper, never()).deleteById(anyLong());
        }
    }

    // ==================== 业务校验 - 名称唯一性 ====================

    @Nested
    @DisplayName("validateSupplierNameUniqueness 供应商名称唯一性校验")
    class NameUniquenessTests {

        @Test
        @DisplayName("新增时同公司主体下同名已存在 -> 抛出异常")
        void shouldThrowWhenNameExistsOnCreate() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-010");
            dto.setSupplierName("华为技术有限公司");
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            assertThrows(BusinessException.class, () -> supplierService.save(dto));
        }

        @Test
        @DisplayName("新增时名称在不同公司主体 -> 允许保存")
        void shouldAllowSameNameInDifferentCompany() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-011");
            dto.setSupplierName("华为技术有限公司");
            dto.setCompanyId(2L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.insert(any(Supplier.class))).thenReturn(1);

            assertDoesNotThrow(() -> supplierService.save(dto));
            verify(supplierMapper).insert(any(Supplier.class));
        }
    }

    // ==================== 业务校验 - 邮箱格式 ====================

    @Nested
    @DisplayName("validateEmail 邮箱格式校验")
    class EmailValidationTests {

        @Test
        @DisplayName("合法邮箱 -> 校验通过")
        void shouldAcceptValidEmail() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-020");
            dto.setSupplierName("测试");
            dto.setEmail("test@example.com");
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.insert(any(Supplier.class))).thenReturn(1);

            assertDoesNotThrow(() -> supplierService.save(dto));
        }

        @Test
        @DisplayName("缺少@符号 -> 校验失败")
        void shouldRejectEmailWithoutAt() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-022");
            dto.setSupplierName("测试");
            dto.setEmail("testexample.com");
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> supplierService.save(dto));
            assertEquals(30003, ex.getCode());
        }

        @Test
        @DisplayName("邮箱为纯空格 -> 不校验（StringUtils.hasText=false）")
        void shouldSkipValidationForBlankEmail() {
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-024");
            dto.setSupplierName("测试");
            dto.setEmail("   ");
            dto.setCompanyId(1L);
            dto.setIsActive(true);

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.insert(any(Supplier.class))).thenReturn(1);

            assertDoesNotThrow(() -> supplierService.save(dto));
        }
    }

    // ==================== 业务校验 - 审核状态流转 ====================

    @Nested
    @DisplayName("validateAuditStatusTransition 审核状态流转校验")
    class AuditStatusTransitionTests {

        @Test
        @DisplayName("状态不变 -> 允许")
        void shouldAllowSameStatusTransition() {
            existingEntity.setAuditStatus("DRAFT");
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-001");
            dto.setSupplierName("华为技术有限公司");
            dto.setCompanyId(1L);
            dto.setAuditStatus("DRAFT");
            dto.setIsActive(true);

            when(supplierMapper.selectById(1L)).thenReturn(existingEntity);
            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.updateById(any(Supplier.class))).thenReturn(1);

            assertDoesNotThrow(() -> supplierService.update(1L, dto));
            verify(supplierMapper).updateById(any(Supplier.class));
        }

        @Test
        @DisplayName("DRAFT -> PENDING -> 允许流转")
        void shouldAllowDraftToPendingTransition() {
            existingEntity.setAuditStatus("DRAFT");
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-001");
            dto.setSupplierName("华为技术有限公司");
            dto.setCompanyId(1L);
            dto.setAuditStatus("PENDING");
            dto.setIsActive(true);

            when(supplierMapper.selectById(1L)).thenReturn(existingEntity);
            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.updateById(any(Supplier.class))).thenReturn(1);

            assertDoesNotThrow(() -> supplierService.update(1L, dto));
            verify(supplierMapper).updateById(any(Supplier.class));
        }

        @Test
        @DisplayName("APPROVED -> DRAFT -> 抛出BusinessException")
        void shouldThrowWhenApprovedToDraft() {
            existingEntity.setAuditStatus("APPROVED");
            SupplierDTO dto = new SupplierDTO();
            dto.setSupplierCode("SUP-001");
            dto.setSupplierName("华为技术有限公司");
            dto.setCompanyId(1L);
            dto.setAuditStatus("DRAFT");
            dto.setIsActive(true);

            when(supplierMapper.selectById(1L)).thenReturn(existingEntity);
            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> supplierService.update(1L, dto));
            assertEquals(40002, ex.getCode());
            verify(supplierMapper, never()).updateById(any());
        }
    }

    // ==================== 事务注解验证 ====================

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("save方法标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnSave() throws NoSuchMethodException {
            var method = SupplierServiceImpl.class.getMethod("save", SupplierDTO.class);
            var annotation = method.getAnnotation(org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("update方法标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnUpdate() throws NoSuchMethodException {
            var method = SupplierServiceImpl.class.getMethod("update", Long.class, SupplierDTO.class);
            var annotation = method.getAnnotation(org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("delete方法标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnDelete() throws NoSuchMethodException {
            var method = SupplierServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.rollbackFor().length > 0);
            assertEquals(Exception.class, annotation.rollbackFor()[0]);
        }

        @Test
        @DisplayName("list方法标注@Transactional(readOnly=true)")
        void shouldHaveTransactionalReadOnlyOnList() throws NoSuchMethodException {
            var method = SupplierServiceImpl.class.getMethod("list", SupplierQueryDTO.class);
            var annotation = method.getAnnotation(org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.readOnly());
        }

        @Test
        @DisplayName("getById方法标注@Transactional(readOnly=true)")
        void shouldHaveTransactionalReadOnlyOnGetById() throws NoSuchMethodException {
            var method = SupplierServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.readOnly());
        }
    }
}
