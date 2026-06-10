package com.erp.module.message.warning;

import com.erp.module.message.entity.MsgAlertRuleEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 预警条件评估器——根据预警规则的条件表达式匹配业务数据.
 *
 * @author AI
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WarningConditionEvaluator {

    private final InventoryQueryService inventoryQueryService;
    private final FinanceQueryService financeQueryService;
    private final ObjectMapper objectMapper;

    public List<WarningMatchResult> evaluate(MsgAlertRuleEntity rule) {
        try {
            JsonNode condition = objectMapper.readTree(rule.getConditionExpression());
            String module = condition.has("module") ? condition.get("module").asText() : "";
            return switch (module) {
                case "inventory" -> inventoryQueryService.findBelowSafetyStock(condition);
                case "receivable" -> financeQueryService.findExceedCreditLimit(condition);
                case "payable" -> financeQueryService.findOverduePayable(condition);
                case "delivery" -> inventoryQueryService.findDelayedDelivery(condition);
                default -> {
                    log.warn("[预警评估] 未知模块: {}", module);
                    yield Collections.emptyList();
                }
            };
        } catch (Exception e) {
            log.error("[预警评估] 解析条件表达式失败 ruleId={}", rule.getId(), e);
            return Collections.emptyList();
        }
    }
}
