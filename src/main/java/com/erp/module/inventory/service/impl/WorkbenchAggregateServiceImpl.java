package com.erp.module.inventory.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.module.inventory.mapper.WorkbenchAggregateMapper;
import com.erp.module.inventory.service.WorkbenchAggregateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 仓库管理工作台聚合数据Service实现.
 *
 * @author AI
 */
@Slf4j
@Service
public class WorkbenchAggregateServiceImpl implements WorkbenchAggregateService {

    private final WorkbenchAggregateMapper workbenchAggregateMapper;

    public WorkbenchAggregateServiceImpl(WorkbenchAggregateMapper workbenchAggregateMapper) {
        this.workbenchAggregateMapper = workbenchAggregateMapper;
    }

    @Override
    @Cacheable(value = "workbench:kpi", key = "#root.target.getCurrentTenantId()")
    public Map<String, Object> getKpiStats() {
        Long tenantId = getCurrentTenantId();
        Map<String, Object> result = workbenchAggregateMapper.selectKpiStats(tenantId);
        if (result == null) {
            return Collections.emptyMap();
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getWarehouseTrend(String startDate, String endDate) {
        Long tenantId = getCurrentTenantId();
        List<Map<String, Object>> result = workbenchAggregateMapper.selectWarehouseTrendByDay(tenantId, startDate, endDate);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getLocationTrend(String startDate, String endDate) {
        Long tenantId = getCurrentTenantId();
        List<Map<String, Object>> result = workbenchAggregateMapper.selectLocationTrendByDay(tenantId, startDate, endDate);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public Long getCurrentTenantId() {
        try {
            Object tenantId = StpUtil.getSession().get("tenantId");
            if (tenantId != null) {
                return Long.parseLong(tenantId.toString());
            }
        } catch (Exception e) {
            log.debug("获取租户ID失败: {}", e.getMessage());
        }
        return 0L;
    }
}
