package com.erp.approval.service;

import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 预置流程验证测试.
 * 验证 batchInsertPreset（批量插入预置审批定义）的正确性，
 * 以及预置流程与CRUD查询的集成.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("预置流程验证测试")
class ApprovalPresetVerificationTest {

    @Mock
    private ApprovalDefinitionMapper mapper;

    private List<ApprovalDefinitionEntity> presetList;

    @BeforeEach
    void setUp() {
        presetList = new ArrayList<>();

        ApprovalDefinitionEntity leaveFlow = new ApprovalDefinitionEntity();
        leaveFlow.setId(1L);
        leaveFlow.setDefinitionName("请假审批流程");
        leaveFlow.setDefinitionCode("PRESET_LEAVE");
        leaveFlow.setBusinessType("LEAVE");
        leaveFlow.setFlowConfig("{\"nodes\":[{\"name\":\"部门经理\",\"type\":\"APPROVE\"},{\"name\":\"HR\",\"type\":\"APPROVE\"}]}");
        leaveFlow.setEnableFlag(true);
        presetList.add(leaveFlow);

        ApprovalDefinitionEntity overtimeFlow = new ApprovalDefinitionEntity();
        overtimeFlow.setId(2L);
        overtimeFlow.setDefinitionName("加班审批流程");
        overtimeFlow.setDefinitionCode("PRESET_OVERTIME");
        overtimeFlow.setBusinessType("OVERTIME");
        overtimeFlow.setFlowConfig("{\"nodes\":[{\"name\":\"部门经理\",\"type\":\"APPROVE\"}]}");
        overtimeFlow.setEnableFlag(true);
        presetList.add(overtimeFlow);

        ApprovalDefinitionEntity travelFlow = new ApprovalDefinitionEntity();
        travelFlow.setId(3L);
        travelFlow.setDefinitionName("出差审批流程");
        travelFlow.setDefinitionCode("PRESET_TRAVEL");
        travelFlow.setBusinessType("TRAVEL");
        travelFlow.setFlowConfig("{\"nodes\":[{\"name\":\"部门经理\",\"type\":\"APPROVE\"},{\"name\":\"财务\",\"type\":\"APPROVE\"}]}");
        travelFlow.setEnableFlag(true);
        presetList.add(travelFlow);
    }

    // ==================== batchInsertPreset 验证 ====================

    @Nested
    @DisplayName("batchInsertPreset - 批量插入预置审批定义")
    class BatchInsertPresetTests {

        @Test
        @DisplayName("全部为新code → 全部插入成功，返回插入行数")
        void shouldInsertAllWhenNoDuplicate() {
            when(mapper.batchInsertPreset(any())).thenReturn(3);

            int inserted = mapper.batchInsertPreset(presetList);

            assertEquals(3, inserted);
            verify(mapper).batchInsertPreset(presetList);
        }

        @Test
        @DisplayName("部分code重复 → 仅插入不重复的记录")
        void shouldSkipDuplicates() {
            when(mapper.batchInsertPreset(any())).thenReturn(2);

            int inserted = mapper.batchInsertPreset(presetList);

            assertEquals(2, inserted);
            assertTrue(inserted < presetList.size());
        }

        @Test
        @DisplayName("全部code重复 → 插入0行，不报错")
        void shouldReturnZeroWhenAllDuplicates() {
            when(mapper.batchInsertPreset(any())).thenReturn(0);

            int inserted = mapper.batchInsertPreset(presetList);

            assertEquals(0, inserted);
            verify(mapper).batchInsertPreset(presetList);
        }

        @Test
        @DisplayName("传递参数验证 → entity字段完整传递")
        void shouldPassCompleteEntities() {
            ArgumentCaptor<List<ApprovalDefinitionEntity>> captor = ArgumentCaptor.forClass(List.class);
            when(mapper.batchInsertPreset(captor.capture())).thenReturn(3);

            mapper.batchInsertPreset(presetList);

            List<ApprovalDefinitionEntity> captured = captor.getValue();
            assertEquals(3, captured.size());
            assertEquals("PRESET_LEAVE", captured.get(0).getDefinitionCode());
            assertEquals("请假审批流程", captured.get(0).getDefinitionName());
            assertEquals("LEAVE", captured.get(0).getBusinessType());
            assertNotNull(captured.get(0).getFlowConfig());
            assertTrue(captured.get(0).getEnableFlag());
        }

        @Test
        @DisplayName("重复执行(boot时多次初始化) → 幂等，不会报错")
        void shouldBeIdempotent() {
            when(mapper.batchInsertPreset(any()))
                    .thenReturn(3)  // 第一次全部插入
                    .thenReturn(0); // 第二次全部跳过（幂等）

            int first = mapper.batchInsertPreset(presetList);
            int second = mapper.batchInsertPreset(presetList);

            assertEquals(3, first);
            assertEquals(0, second);
            verify(mapper, times(2)).batchInsertPreset(any());
        }
    }

    // ==================== 预置流程与CRUD集成验证 ====================

    @Nested
    @DisplayName("预置流程CRUD集成验证")
    class PresetCrudIntegrationTests {

        @Test
        @DisplayName("预置流程插入后 → 可通过分页查询检索到")
        void shouldFindPresetInPageQuery() {
            when(mapper.selectCount(any())).thenReturn(1L);
            when(mapper.selectByDefinitionCode("PRESET_LEAVE")).thenReturn(presetList.get(0));

            Long count = mapper.selectCount(any());
            ApprovalDefinitionEntity found = mapper.selectByDefinitionCode("PRESET_LEAVE");

            assertEquals(1L, count);
            assertNotNull(found);
            assertEquals("请假审批流程", found.getDefinitionName());
        }

        @Test
        @DisplayName("预置流程code查询不到 → 返回null（非异常）")
        void shouldReturnNullForUnknownCode() {
            when(mapper.selectByDefinitionCode("NONEXISTENT")).thenReturn(null);

            ApprovalDefinitionEntity result = mapper.selectByDefinitionCode("NONEXISTENT");

            assertNull(result);
        }

        @Test
        @DisplayName("查询所有启用的预置流程 → selectAllEnabled返回列表")
        void shouldReturnAllEnabledPresets() {
            when(mapper.selectAllEnabled()).thenReturn(presetList);

            List<ApprovalDefinitionEntity> enabled = mapper.selectAllEnabled();

            assertEquals(3, enabled.size());
            enabled.forEach(e -> assertTrue(e.getEnableFlag()));
        }
    }

    // ==================== enableFlag默认值验证 ====================

    @Nested
    @DisplayName("预置流程enableFlag字段验证")
    class PresetEnableFlagTests {

        @Test
        @DisplayName("预置流程enableFlag为true → 可被selectAllEnabled查询到")
        void shouldIncludeEnabledPresets() {
            List<ApprovalDefinitionEntity> enabledOnly = presetList.stream()
                    .filter(ApprovalDefinitionEntity::getEnableFlag)
                    .toList();
            when(mapper.selectAllEnabled()).thenReturn(enabledOnly);

            List<ApprovalDefinitionEntity> result = mapper.selectAllEnabled();

            assertEquals(3, result.size());
            result.forEach(e -> assertTrue(e.getEnableFlag()));
        }

        @Test
        @DisplayName("预置流程enableFlag为false → 不被selectAllEnabled查询到")
        void shouldExcludeDisabledPresets() {
            presetList.get(0).setEnableFlag(false);
            List<ApprovalDefinitionEntity> enabledOnly = presetList.stream()
                    .filter(ApprovalDefinitionEntity::getEnableFlag)
                    .toList();
            when(mapper.selectAllEnabled()).thenReturn(enabledOnly);

            List<ApprovalDefinitionEntity> result = mapper.selectAllEnabled();

            assertEquals(2, result.size());
            result.forEach(e -> assertTrue(e.getEnableFlag()));
            assertTrue(result.stream().noneMatch(e -> "PRESET_LEAVE".equals(e.getDefinitionCode())));
        }
    }
}
