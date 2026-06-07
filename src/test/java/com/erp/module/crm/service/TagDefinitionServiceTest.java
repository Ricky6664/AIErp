package com.erp.module.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.crm.dto.TagDefinitionDTO;
import com.erp.module.crm.dto.TagDefinitionQueryDTO;
import com.erp.module.crm.entity.TagDefinitionEntity;
import com.erp.module.crm.mapper.TagDefinitionMapper;
import com.erp.module.crm.service.impl.TagDefinitionServiceImpl;
import com.erp.module.crm.vo.TagDefinitionVO;
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
@DisplayName("TagDefinitionService 单元测试")
class TagDefinitionServiceTest {

    @Mock
    private TagDefinitionMapper tagDefinitionMapper;

    private TagDefinitionServiceImpl tagDefinitionService;

    private TagDefinitionDTO createDTO;
    private TagDefinitionEntity existingEntity;

    @BeforeEach
    void setUp() throws Exception {
        tagDefinitionService = spy(new TagDefinitionServiceImpl());
        Field baseMapperField = com.baomidou.mybatisplus.extension.service.impl.ServiceImpl.class
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(tagDefinitionService, tagDefinitionMapper);

        createDTO = new TagDefinitionDTO();
        createDTO.setTagName("重要客户");
        createDTO.setTagGroup("客户等级");
        createDTO.setTagColor("#FF0000");
        createDTO.setSortOrder(1);
        createDTO.setIsActive(true);

        existingEntity = new TagDefinitionEntity();
        existingEntity.setId(1L);
        existingEntity.setTagName("重要客户");
        existingEntity.setTagGroup("客户等级");
        existingEntity.setTagColor("#FF0000");
        existingEntity.setSortOrder(1);
        existingEntity.setIsActive(true);
    }

    @AfterEach
    void tearDown() {
        reset(tagDefinitionMapper);
    }

    @Nested
    @DisplayName("list 分页查询")
    class ListTests {

        @Test
        @DisplayName("无筛选条件 -> 返回全量分页结果")
        void shouldReturnPagedResultsWithoutFilter() {
            TagDefinitionQueryDTO query = new TagDefinitionQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            when(tagDefinitionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<TagDefinitionEntity> page = inv.getArgument(0);
                        page.setRecords(Arrays.asList(existingEntity));
                        page.setTotal(1);
                        return page;
                    });

            PageResult<TagDefinitionVO> result = tagDefinitionService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal().intValue());
            assertEquals(1, result.getList().size());
            assertEquals(existingEntity.getTagName(), result.getList().get(0).getTagName());
        }

        @Test
        @DisplayName("按标签名称模糊搜索 -> 返回匹配结果")
        void shouldFilterByTagNameFuzzy() {
            TagDefinitionQueryDTO query = new TagDefinitionQueryDTO();
            query.setTagName("重要");

            when(tagDefinitionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<TagDefinitionEntity> page = inv.getArgument(0);
                        page.setRecords(Arrays.asList(existingEntity));
                        page.setTotal(1);
                        return page;
                    });

            PageResult<TagDefinitionVO> result = tagDefinitionService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal().intValue());
        }

        @Test
        @DisplayName("按标签分组筛选 -> 返回匹配结果")
        void shouldFilterByTagGroup() {
            TagDefinitionQueryDTO query = new TagDefinitionQueryDTO();
            query.setTagGroup("客户等级");

            when(tagDefinitionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<TagDefinitionEntity> page = inv.getArgument(0);
                        page.setRecords(Arrays.asList(existingEntity));
                        page.setTotal(1);
                        return page;
                    });

            PageResult<TagDefinitionVO> result = tagDefinitionService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal().intValue());
        }

        @Test
        @DisplayName("按启用状态筛选 -> 返回匹配结果")
        void shouldFilterByIsActive() {
            TagDefinitionQueryDTO query = new TagDefinitionQueryDTO();
            query.setIsActive(true);

            when(tagDefinitionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<TagDefinitionEntity> page = inv.getArgument(0);
                        page.setRecords(Arrays.asList(existingEntity));
                        page.setTotal(1);
                        return page;
                    });

            PageResult<TagDefinitionVO> result = tagDefinitionService.list(query);

            assertNotNull(result);
            assertEquals(1, result.getTotal().intValue());
        }

        @Test
        @DisplayName("无匹配数据 -> 返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            TagDefinitionQueryDTO query = new TagDefinitionQueryDTO();

            when(tagDefinitionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<TagDefinitionEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            PageResult<TagDefinitionVO> result = tagDefinitionService.list(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal().intValue());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("按createTime ASC排序 -> 正确传递排序参数")
        void shouldHandleCreateTimeAscendingSort() {
            TagDefinitionQueryDTO query = new TagDefinitionQueryDTO();
            query.setSortField("createTime");
            query.setSortOrder("ASC");

            when(tagDefinitionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<TagDefinitionEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> tagDefinitionService.list(query));
        }

        @Test
        @DisplayName("按sortOrder DESC排序 -> 正确传递排序参数")
        void shouldHandleSortOrderDescendingSort() {
            TagDefinitionQueryDTO query = new TagDefinitionQueryDTO();
            query.setSortField("sortOrder");
            query.setSortOrder("DESC");

            when(tagDefinitionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<TagDefinitionEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> tagDefinitionService.list(query));
        }

        @Test
        @DisplayName("默认未知排序字段 -> 按sortOrder默认排序")
        void shouldUseDefaultSortWhenUnknownField() {
            TagDefinitionQueryDTO query = new TagDefinitionQueryDTO();
            query.setSortField("unknownField");
            query.setSortOrder("ASC");

            when(tagDefinitionMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        IPage<TagDefinitionEntity> page = inv.getArgument(0);
                        page.setRecords(Collections.emptyList());
                        page.setTotal(0);
                        return page;
                    });

            assertDoesNotThrow(() -> tagDefinitionService.list(query));
        }
    }

    @Nested
    @DisplayName("getById 查询详情")
    class GetByIdTests {

        @Test
        @DisplayName("ID存在 -> 返回VO")
        void shouldReturnVoWhenExists() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);

            TagDefinitionVO result = tagDefinitionService.getById(1L);

            assertNotNull(result);
            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getTagName(), result.getTagName());
            assertEquals(existingEntity.getTagGroup(), result.getTagGroup());
            assertEquals(existingEntity.getTagColor(), result.getTagColor());
            assertEquals(existingEntity.getSortOrder(), result.getSortOrder());
            assertEquals(existingEntity.getIsActive(), result.getIsActive());
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(tagDefinitionMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.getById(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            assertEquals(ErrorCode.DATA_NOT_FOUND.getMessage(), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("save 新增标签定义")
    class SaveTests {

        @Test
        @DisplayName("正常数据 -> 保存成功")
        void shouldSaveSuccessfully() {
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
            verify(tagDefinitionService).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("标签名称重复 -> 抛出BusinessException DATA_ALREADY_EXISTS")
        void shouldThrowExceptionWhenTagNameDuplicate() {
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.save(createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getMessage(), ex.getMessage());
            verify(tagDefinitionService, never()).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("颜色格式无效 -> 抛出BusinessException PARAM_FORMAT_ERROR")
        void shouldThrowExceptionWhenColorFormatInvalid() {
            createDTO.setTagColor("red");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.save(createDTO));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
            verify(tagDefinitionService, never()).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("颜色为null -> 跳过颜色校验正常保存")
        void shouldSkipColorValidationWhenNull() {
            createDTO.setTagColor(null);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
            verify(tagDefinitionService).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("颜色为空字符串 -> 跳过颜色校验正常保存")
        void shouldSkipColorValidationWhenEmpty() {
            createDTO.setTagColor("");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
            verify(tagDefinitionService).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("合法颜色格式(#00FF00) -> 正常保存")
        void shouldAcceptValidHexColor() {
            createDTO.setTagColor("#00FF00");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
            verify(tagDefinitionService).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("小写合法颜色(#aabbcc) -> 正常保存")
        void shouldAcceptLowercaseHexColor() {
            createDTO.setTagColor("#aabbcc");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
            verify(tagDefinitionService).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("保存时实体属性正确复制 -> 验证copyProperties")
        void shouldCopyDtoPropertiesToEntity() {
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            tagDefinitionService.save(createDTO);

            ArgumentCaptor<TagDefinitionEntity> captor = ArgumentCaptor.forClass(TagDefinitionEntity.class);
            verify(tagDefinitionService).save(captor.capture());
            assertEquals(createDTO.getTagName(), captor.getValue().getTagName());
            assertEquals(createDTO.getTagGroup(), captor.getValue().getTagGroup());
            assertEquals(createDTO.getTagColor(), captor.getValue().getTagColor());
            assertEquals(createDTO.getSortOrder(), captor.getValue().getSortOrder());
            assertEquals(createDTO.getIsActive(), captor.getValue().getIsActive());
        }

        @Test
        @DisplayName("save参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnSaveParam() throws NoSuchMethodException {
            var method = TagDefinitionService.class.getMethod("save", TagDefinitionDTO.class);
            Parameter[] params = method.getParameters();
            assertTrue(params.length > 0);
            boolean hasValid = Arrays.stream(params[0].getAnnotations())
                    .anyMatch(a -> a.annotationType().equals(Valid.class));
            assertTrue(hasValid);
        }
    }

    @Nested
    @DisplayName("update 更新标签定义")
    class UpdateTests {

        @Test
        @DisplayName("正常数据 -> 更新成功")
        void shouldUpdateSuccessfully() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).updateById(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.update(1L, createDTO));
            verify(tagDefinitionService).updateById(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(tagDefinitionMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.update(99L, createDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(tagDefinitionService, never()).updateById(any());
        }

        @Test
        @DisplayName("名称重复排除自身 -> 抛出BusinessException")
        void shouldThrowExceptionWhenNameDuplicateExcludingSelf() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.update(1L, createDTO));
            assertEquals(ErrorCode.DATA_ALREADY_EXISTS.getCode(), ex.getCode());
            verify(tagDefinitionService, never()).updateById(any());
        }

        @Test
        @DisplayName("更新时名称不变 -> 唯一性校验排除自身通过")
        void shouldAllowSameNameWhenUpdatingSelf() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).updateById(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.update(1L, createDTO));
            verify(tagDefinitionService).updateById(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("颜色格式无效 -> 抛出BusinessException")
        void shouldThrowExceptionWhenColorFormatInvalidInUpdate() {
            createDTO.setTagColor("invalid");
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.update(1L, createDTO));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
            verify(tagDefinitionService, never()).updateById(any());
        }

        @Test
        @DisplayName("更新后实体ID保持不变 -> 使用原ID")
        void shouldPreserveEntityIdAfterUpdate() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).updateById(any(TagDefinitionEntity.class));

            tagDefinitionService.update(1L, createDTO);

            ArgumentCaptor<TagDefinitionEntity> captor = ArgumentCaptor.forClass(TagDefinitionEntity.class);
            verify(tagDefinitionService).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
        }

        @Test
        @DisplayName("update参数标注@Valid -> 参数校验生效")
        void shouldHaveValidAnnotationOnUpdateParam() throws NoSuchMethodException {
            var method = TagDefinitionService.class.getMethod("update", Long.class, TagDefinitionDTO.class);
            Parameter[] params = method.getParameters();
            boolean hasValid = Arrays.stream(params)
                    .filter(p -> p.getType().equals(TagDefinitionDTO.class))
                    .findFirst()
                    .flatMap(p -> Arrays.stream(p.getAnnotations())
                            .filter(a -> a.annotationType().equals(Valid.class))
                            .findFirst())
                    .isPresent();
            assertTrue(hasValid);
        }
    }

    @Nested
    @DisplayName("delete 删除标签定义")
    class DeleteTests {

        @Test
        @DisplayName("正常删除 -> 删除成功")
        void shouldDeleteSuccessfully() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.countTagReferences(1L)).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).removeById(1L);

            assertDoesNotThrow(() -> tagDefinitionService.delete(1L));
            verify(tagDefinitionService).removeById(1L);
        }

        @Test
        @DisplayName("ID不存在 -> 抛出BusinessException DATA_NOT_FOUND")
        void shouldThrowExceptionWhenIdNotFound() {
            when(tagDefinitionMapper.selectById(99L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.delete(99L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(tagDefinitionService, never()).removeById(any());
        }

        @Test
        @DisplayName("标签已被客户引用 -> 抛出BusinessException BUSINESS_ERROR")
        void shouldThrowExceptionWhenTagReferenced() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.countTagReferences(1L)).thenReturn(1L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.delete(1L));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            assertEquals(ErrorCode.BUSINESS_ERROR.getMessage(), ex.getMessage());
            verify(tagDefinitionService, never()).removeById(any());
        }

        @Test
        @DisplayName("引用计数为0 -> 正常删除")
        void shouldDeleteWhenNoReferences() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.countTagReferences(1L)).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).removeById(1L);

            assertDoesNotThrow(() -> tagDefinitionService.delete(1L));
            verify(tagDefinitionService).removeById(1L);
        }
    }

    @Nested
    @DisplayName("@Transactional 事务注解验证")
    class TransactionalAnnotationTests {

        @Test
        @DisplayName("save标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnSave() throws NoSuchMethodException {
            var method = TagDefinitionServiceImpl.class.getMethod("save", TagDefinitionDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(Arrays.asList(annotation.rollbackFor()).contains(Exception.class));
        }

        @Test
        @DisplayName("update标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnUpdate() throws NoSuchMethodException {
            var method = TagDefinitionServiceImpl.class.getMethod("update", Long.class, TagDefinitionDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(Arrays.asList(annotation.rollbackFor()).contains(Exception.class));
        }

        @Test
        @DisplayName("delete标注@Transactional(rollbackFor=Exception.class)")
        void shouldHaveTransactionalOnDelete() throws NoSuchMethodException {
            var method = TagDefinitionServiceImpl.class.getMethod("delete", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(Arrays.asList(annotation.rollbackFor()).contains(Exception.class));
        }

        @Test
        @DisplayName("list标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnList() throws NoSuchMethodException {
            var method = TagDefinitionServiceImpl.class.getMethod("list", TagDefinitionQueryDTO.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.readOnly());
        }

        @Test
        @DisplayName("getById标注@Transactional(readOnly=true)")
        void shouldHaveReadOnlyTransactionalOnGetById() throws NoSuchMethodException {
            var method = TagDefinitionServiceImpl.class.getMethod("getById", Long.class);
            var annotation = method.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertNotNull(annotation);
            assertTrue(annotation.readOnly());
        }
    }

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("标签名称50字符边界 -> 正常保存")
        void shouldHandleMaxTagNameLength() {
            createDTO.setTagName("A".repeat(50));
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
            verify(tagDefinitionService).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("tagGroup为null -> 正常保存")
        void shouldSaveWithNullTagGroup() {
            createDTO.setTagGroup(null);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
        }

        @Test
        @DisplayName("tagGroup为空字符串 -> 正常保存")
        void shouldSaveWithEmptyTagGroup() {
            createDTO.setTagGroup("");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
        }

        @Test
        @DisplayName("sortOrder为null -> 正常保存")
        void shouldSaveWithNullSortOrder() {
            createDTO.setSortOrder(null);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
            verify(tagDefinitionService).save(any(TagDefinitionEntity.class));
        }

        @Test
        @DisplayName("isActive为false -> 正常保存")
        void shouldSaveWithInactiveStatus() {
            createDTO.setIsActive(false);
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            tagDefinitionService.save(createDTO);

            ArgumentCaptor<TagDefinitionEntity> captor = ArgumentCaptor.forClass(TagDefinitionEntity.class);
            verify(tagDefinitionService).save(captor.capture());
            assertFalse(captor.getValue().getIsActive());
        }

        @Test
        @DisplayName("空字符串标签名称 -> 唯一性校验仍适用")
        void shouldCheckUniquenessForEmptyTagName() {
            createDTO.setTagName("");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
        }

        @Test
        @DisplayName("大写颜色代码(#ABCDEF) -> 正常保存")
        void shouldAcceptUppercaseHexColor() {
            createDTO.setTagColor("#ABCDEF");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).save(any(TagDefinitionEntity.class));

            assertDoesNotThrow(() -> tagDefinitionService.save(createDTO));
        }

        @Test
        @DisplayName("无#的颜色字符串 -> 校验失败")
        void shouldRejectColorWithoutHash() {
            createDTO.setTagColor("FF0000");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.save(createDTO));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("颜色长度不足7位 -> 校验失败")
        void shouldRejectShortColor() {
            createDTO.setTagColor("#FF000");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.save(createDTO));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("颜色含非法字符 -> 校验失败")
        void shouldRejectColorWithInvalidChars() {
            createDTO.setTagColor("#FF00GG");
            when(tagDefinitionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> tagDefinitionService.save(createDTO));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("delete时标签存在但无引用 -> 正常删除")
        void shouldDeleteWhenTagExistsWithoutReferences() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);
            when(tagDefinitionMapper.countTagReferences(1L)).thenReturn(0L);
            doReturn(true).when(tagDefinitionService).removeById(1L);

            tagDefinitionService.delete(1L);

            verify(tagDefinitionMapper).countTagReferences(1L);
            verify(tagDefinitionService).removeById(1L);
        }
    }

    @Nested
    @DisplayName("toVO 实体转换")
    class ToVoTests {

        @Test
        @DisplayName("实体所有字段正确映射到VO")
        void shouldMapAllFieldsFromEntityToVo() {
            when(tagDefinitionMapper.selectById(1L)).thenReturn(existingEntity);

            TagDefinitionVO result = tagDefinitionService.getById(1L);

            assertEquals(existingEntity.getId(), result.getId());
            assertEquals(existingEntity.getTagName(), result.getTagName());
            assertEquals(existingEntity.getTagGroup(), result.getTagGroup());
            assertEquals(existingEntity.getTagColor(), result.getTagColor());
            assertEquals(existingEntity.getSortOrder(), result.getSortOrder());
            assertEquals(existingEntity.getIsActive(), result.getIsActive());
        }
    }
}
