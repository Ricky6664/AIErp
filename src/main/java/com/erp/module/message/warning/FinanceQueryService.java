package com.erp.module.message.warning;

import com.erp.module.message.warning.WarningMatchResult;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * 财务查询服务接口（跨模块抽象）.
 *
 * @author AI
 */
public interface FinanceQueryService {

    List<WarningMatchResult> findExceedCreditLimit(JsonNode condition);

    List<WarningMatchResult> findOverduePayable(JsonNode condition);
}
