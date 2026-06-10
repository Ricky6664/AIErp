package com.erp.module.message.warning;

import com.erp.module.message.warning.WarningMatchResult;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * 库存查询服务接口（跨模块抽象）.
 *
 * @author AI
 */
public interface InventoryQueryService {

    List<WarningMatchResult> findBelowSafetyStock(JsonNode condition);

    List<WarningMatchResult> findDelayedDelivery(JsonNode condition);
}
