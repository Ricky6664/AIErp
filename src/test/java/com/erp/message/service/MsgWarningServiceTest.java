package com.erp.message.service;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.message.entity.MsgAlertRuleEntity;
import com.erp.module.message.entity.MsgWarningEntity;
import com.erp.module.message.mapper.MsgAlertRuleMapper;
import com.erp.module.message.mapper.MsgWarningMapper;
import com.erp.module.message.service.IMsgMessageService;
import com.erp.module.message.service.MsgWebSocketService;
import com.erp.module.message.service.impl.MsgWarningServiceImpl;
import com.erp.module.message.vo.WarningDashboardVO;
import com.erp.module.message.warning.WarningConditionEvaluator;
import com.erp.module.message.warning.WarningMatchResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MsgWarningService 单元测试")
class MsgWarningServiceTest {

    @Mock
    private MsgAlertRuleMapper ruleMapper;
    @Mock
    private MsgWarningMapper warningMapper;
    @Mock
    private IMsgMessageService messageService;
    @Mock
    private MsgWebSocketService webSocketService;
    @Mock
    private WarningConditionEvaluator conditionEvaluator;

    private MsgWarningServiceImpl service;

    private MsgAlertRuleEntity rule;
    private MsgWarningEntity warning;
    private WarningMatchResult matchResult;

    @BeforeEach
    void setUp() {
        service = new MsgWarningServiceImpl(ruleMapper, warningMapper, messageService,
                webSocketService, conditionEvaluator);

        rule = new MsgAlertRuleEntity();
        rule.setId(1L);
        rule.setRuleName("库存预警规则");
        rule.setConditionExpression("{\"module\":\"inventory\",\"field\":\"stock_qty\",\"operator\":\"lt\"}");
        rule.setIsEnabled(true);
        rule.setTriggerActionConfig("warehouse_manager");
        rule.setVersion(0);

        warning = new MsgWarningEntity();
        warning.setId(100L);
        warning.setRuleId(1L);
        warning.setBusinessId("BIZ-001");
        warning.setTitle("库存不足预警");
        warning.setContent("商品库存低于安全库存");
        warning.setWarningType("inventory");
        warning.setIsHandled(false);

        matchResult = WarningMatchResult.builder()
                .businessId("BIZ-001")
                .module("inventory")
                .title("库存不足预警")
                .content("商品库存低于安全库存")
                .build();
    }

    @AfterEach
    void tearDown() {
        reset(ruleMapper, warningMapper, messageService, webSocketService, conditionEvaluator);
    }

    // ==================== scanAndAlert ====================

    @Nested
    @DisplayName("scanAndAlert - 预警规则扫描与触发")
    class ScanAndAlertTests {

        @Test
        @DisplayName("有启用的规则且有匹配数据 → 生成预警、推送消息、WebSocket推送")
        void shouldGenerateWarningWhenRuleMatches() {
            when(ruleMapper.selectList(any())).thenReturn(List.of(rule));
            when(conditionEvaluator.evaluate(rule)).thenReturn(List.of(matchResult));
            when(warningMapper.existsToday(1L, "BIZ-001")).thenReturn(false);
            when(warningMapper.insert(any(MsgWarningEntity.class))).thenReturn(1);
            when(messageService.create(any())).thenReturn(200L);

            service.scanAndAlert();

            verify(warningMapper).insert(any(MsgWarningEntity.class));
            verify(messageService).create(any());
            verify(webSocketService).pushToRole(eq("warehouse_manager"), anyString());
        }

        @Test
        @DisplayName("无启用的规则 → 不生成任何预警")
        void shouldNotGenerateWhenNoEnabledRules() {
            when(ruleMapper.selectList(any())).thenReturn(Collections.emptyList());

            service.scanAndAlert();

            verify(warningMapper, never()).insert(any());
            verify(messageService, never()).create(any());
            verify(webSocketService, never()).pushToRole(anyString(), anyString());
        }

        @Test
        @DisplayName("规则存在但无匹配数据 → 不生成预警")
        void shouldNotGenerateWhenNoMatches() {
            when(ruleMapper.selectList(any())).thenReturn(List.of(rule));
            when(conditionEvaluator.evaluate(rule)).thenReturn(Collections.emptyList());

            service.scanAndAlert();

            verify(warningMapper, never()).insert(any());
            verify(messageService, never()).create(any());
        }

        @Test
        @DisplayName("已存在今日预警(幂等检查) → 跳过不重复生成")
        void shouldSkipWhenAlreadyExistsToday() {
            when(ruleMapper.selectList(any())).thenReturn(List.of(rule));
            when(conditionEvaluator.evaluate(rule)).thenReturn(List.of(matchResult));
            when(warningMapper.existsToday(1L, "BIZ-001")).thenReturn(true);

            service.scanAndAlert();

            verify(warningMapper, never()).insert(any());
            verify(messageService, never()).create(any());
        }

        @Test
        @DisplayName("单条规则评估抛异常 → 捕获后继续处理其他规则")
        void shouldContinueWhenRuleEvaluationFails() {
            MsgAlertRuleEntity rule2 = new MsgAlertRuleEntity();
            rule2.setId(2L);
            rule2.setRuleName("应收预警规则");
            rule2.setConditionExpression("{\"module\":\"receivable\"}");
            rule2.setIsEnabled(true);
            rule2.setTriggerActionConfig("finance_manager");

            when(ruleMapper.selectList(any())).thenReturn(List.of(rule, rule2));
            when(conditionEvaluator.evaluate(rule))
                    .thenThrow(new RuntimeException("DB connection lost"));
            when(conditionEvaluator.evaluate(rule2)).thenReturn(List.of(matchResult));
            when(warningMapper.existsToday(2L, "BIZ-001")).thenReturn(false);
            when(warningMapper.insert(any(MsgWarningEntity.class))).thenReturn(1);
            when(messageService.create(any())).thenReturn(200L);

            assertDoesNotThrow(() -> service.scanAndAlert());

            verify(warningMapper).insert(any(MsgWarningEntity.class));
        }

        @Test
        @DisplayName("多条匹配结果 → 逐一生成预警")
        void shouldGenerateMultipleWarnings() {
            WarningMatchResult match2 = WarningMatchResult.builder()
                    .businessId("BIZ-002")
                    .module("inventory")
                    .title("库存预警2")
                    .content("另一商品库存不足")
                    .build();

            when(ruleMapper.selectList(any())).thenReturn(List.of(rule));
            when(conditionEvaluator.evaluate(rule)).thenReturn(List.of(matchResult, match2));
            when(warningMapper.existsToday(anyLong(), anyString())).thenReturn(false);
            when(warningMapper.insert(any(MsgWarningEntity.class))).thenReturn(1);
            when(messageService.create(any())).thenReturn(200L);

            service.scanAndAlert();

            verify(warningMapper, times(2)).insert(any(MsgWarningEntity.class));
            verify(messageService, times(2)).create(any());
        }
    }

    // ==================== getWarningDashboard ====================

    @Nested
    @DisplayName("getWarningDashboard - 预警看板数据聚合")
    class GetWarningDashboardTests {

        @Test
        @DisplayName("不传module → 返回完整看板数据(统计+趋势+分布+明细)")
        void shouldReturnFullDashboard() {
            List<Map<String, Object>> moduleCounts = List.of(
                    Map.of("module", "inventory", "cnt", 5L),
                    Map.of("module", "receivable", "cnt", 3L));
            List<Map<String, Object>> trendData = List.of(
                    Map.of("date", LocalDate.now().minusDays(1), "cnt", 2L));
            List<Map<String, Object>> distributionData = List.of(
                    Map.of("name", "inventory", "value", 5L));

            when(warningMapper.countByModule()).thenReturn(moduleCounts);
            when(warningMapper.selectTrend(any(LocalDate.class))).thenReturn(trendData);
            when(warningMapper.selectDistribution()).thenReturn(distributionData);
            when(warningMapper.selectWarningList(isNull())).thenReturn(List.of(warning));

            WarningDashboardVO result = service.getWarningDashboard(null);

            assertNotNull(result);
            assertEquals(2, result.getModuleCounts().size());
            assertEquals(1, result.getTrendData().size());
            assertEquals(1, result.getDistributionData().size());
            assertEquals(1, result.getWarningList().size());
        }

        @Test
        @DisplayName("传module过滤 → 明细按模块过滤")
        void shouldFilterByModule() {
            when(warningMapper.countByModule()).thenReturn(Collections.emptyList());
            when(warningMapper.selectTrend(any(LocalDate.class))).thenReturn(Collections.emptyList());
            when(warningMapper.selectDistribution()).thenReturn(Collections.emptyList());
            when(warningMapper.selectWarningList("inventory")).thenReturn(List.of(warning));

            WarningDashboardVO result = service.getWarningDashboard("inventory");

            assertNotNull(result);
            assertEquals(1, result.getWarningList().size());
            verify(warningMapper).selectWarningList("inventory");
        }

        @Test
        @DisplayName("无任何预警数据 → 返回空列表")
        void shouldReturnEmptyWhenNoData() {
            when(warningMapper.countByModule()).thenReturn(Collections.emptyList());
            when(warningMapper.selectTrend(any(LocalDate.class))).thenReturn(Collections.emptyList());
            when(warningMapper.selectDistribution()).thenReturn(Collections.emptyList());
            when(warningMapper.selectWarningList(isNull())).thenReturn(Collections.emptyList());

            WarningDashboardVO result = service.getWarningDashboard(null);

            assertNotNull(result);
            assertTrue(result.getModuleCounts().isEmpty());
            assertTrue(result.getWarningList().isEmpty());
        }
    }

    // ==================== handleWarning ====================

    @Nested
    @DisplayName("handleWarning - 预警处理")
    class HandleWarningTests {

        @Test
        @DisplayName("预警存在且未处理 → 标记已处理，记录处理人和时间")
        void shouldHandleWhenWarningExists() {
            when(warningMapper.selectById(100L)).thenReturn(warning);
            when(warningMapper.updateById(any(MsgWarningEntity.class))).thenReturn(1);

            assertDoesNotThrow(() -> service.handleWarning(100L));

            verify(warningMapper).updateById(argThat(w ->
                    Boolean.TRUE.equals(w.getIsHandled())
                            && w.getHandleTime() != null
                            && w.getHandlerId() != null));
        }

        @Test
        @DisplayName("预警不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenWarningNotFound() {
            when(warningMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.handleWarning(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(warningMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("预警已处理 → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenAlreadyHandled() {
            warning.setIsHandled(true);
            when(warningMapper.selectById(100L)).thenReturn(warning);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.handleWarning(100L));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(warningMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("并发更新失败(乐观锁) → 异常传播")
        void shouldThrowOnOptimisticLockConflict() {
            when(warningMapper.selectById(100L)).thenReturn(warning);
            when(warningMapper.updateById(any(MsgWarningEntity.class)))
                    .thenThrow(new RuntimeException("OptimisticLockingFailure"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.handleWarning(100L));
            assertTrue(ex.getMessage().contains("OptimisticLockingFailure"));
        }
    }
}
