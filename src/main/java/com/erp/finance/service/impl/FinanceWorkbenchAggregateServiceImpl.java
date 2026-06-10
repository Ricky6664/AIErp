package com.erp.finance.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.finance.entity.VoucherWordEntity;
import com.erp.finance.mapper.VoucherWordMapper;
import com.erp.finance.vo.FinanceWorkbenchAggregateVO;
import com.erp.module.finance.entity.AccountEntity;
import com.erp.module.finance.entity.BankAccountEntity;
import com.erp.module.finance.entity.CurrencyRateEntity;
import com.erp.module.finance.mapper.AccountMapper;
import com.erp.module.finance.mapper.BankAccountMapper;
import com.erp.module.finance.mapper.CurrencyRateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务基础设置工作台聚合数据Service实现.
 *
 * @author AI
 * @since 2026-06-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceWorkbenchAggregateServiceImpl {

    private final CurrencyRateMapper currencyRateMapper;
    private final BankAccountMapper bankAccountMapper;
    private final AccountMapper accountMapper;
    private final VoucherWordMapper voucherWordMapper;

    @Cacheable(value = "workbench", key = "'finance:' + #root.target.getCurrentTenantId()",
            unless = "#result == null")
    public FinanceWorkbenchAggregateVO getWorkbenchData() {
        Long tenantId = getCurrentTenantId();
        FinanceWorkbenchAggregateVO vo = new FinanceWorkbenchAggregateVO();

        vo.setCurrencyRateCount(countByTenant(CurrencyRateEntity.class, tenantId));
        vo.setBankAccountCount(countByTenant(BankAccountEntity.class, tenantId));
        vo.setActiveBankAccountCount(countActive(BankAccountEntity.class, tenantId));
        vo.setAccountCount(countByTenant(AccountEntity.class, tenantId));
        vo.setLeafAccountCount(countLeafAccounts(tenantId));
        vo.setVoucherWordCount(countByTenant(VoucherWordEntity.class, tenantId));
        vo.setActiveVoucherWordCount(countActiveVoucherWords(tenantId));
        vo.setAccountTypeDistribution(getAccountTypeDistribution(tenantId));
        vo.setMonthlyTrend(getMonthlyTrend(tenantId));

        return vo;
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

    private Long countByTenant(Class<?> entityClass, Long tenantId) {
        if (entityClass == CurrencyRateEntity.class) {
            return currencyRateMapper.selectCount(
                    new LambdaQueryWrapper<CurrencyRateEntity>()
                            .eq(CurrencyRateEntity::getTenantId, tenantId));
        } else if (entityClass == BankAccountEntity.class) {
            return bankAccountMapper.selectCount(
                    new LambdaQueryWrapper<BankAccountEntity>()
                            .eq(BankAccountEntity::getTenantId, tenantId));
        } else if (entityClass == AccountEntity.class) {
            return accountMapper.selectCount(
                    new LambdaQueryWrapper<AccountEntity>()
                            .eq(AccountEntity::getTenantId, tenantId));
        } else if (entityClass == VoucherWordEntity.class) {
            return voucherWordMapper.selectCount(
                    new LambdaQueryWrapper<VoucherWordEntity>()
                            .eq(VoucherWordEntity::getTenantId, tenantId));
        }
        return 0L;
    }

    private Long countActive(Class<?> entityClass, Long tenantId) {
        if (entityClass == BankAccountEntity.class) {
            return bankAccountMapper.selectCount(
                    new LambdaQueryWrapper<BankAccountEntity>()
                            .eq(BankAccountEntity::getTenantId, tenantId)
                            .eq(BankAccountEntity::getStatus, 1));
        }
        return 0L;
    }

    private Long countLeafAccounts(Long tenantId) {
        return accountMapper.selectCount(
                new LambdaQueryWrapper<AccountEntity>()
                        .eq(AccountEntity::getTenantId, tenantId)
                        .eq(AccountEntity::getIsLeaf, true));
    }

    private Long countActiveVoucherWords(Long tenantId) {
        return voucherWordMapper.selectCount(
                new LambdaQueryWrapper<VoucherWordEntity>()
                        .eq(VoucherWordEntity::getTenantId, tenantId)
                        .eq(VoucherWordEntity::getStatus, 1));
    }

    private Map<String, Long> getAccountTypeDistribution(Long tenantId) {
        List<AccountEntity> accounts = accountMapper.selectList(
                new LambdaQueryWrapper<AccountEntity>()
                        .eq(AccountEntity::getTenantId, tenantId));
        Map<String, Long> distribution = new LinkedHashMap<>();
        Map<Integer, String> typeMap = Map.of(
                1, "资产", 2, "负债", 3, "权益", 4, "成本", 5, "损益");
        for (AccountEntity account : accounts) {
            if (account.getAccountType() != null) {
                String typeName = typeMap.getOrDefault(account.getAccountType(), "其他");
                distribution.merge(typeName, 1L, Long::sum);
            }
        }
        return distribution;
    }

    private List<FinanceWorkbenchAggregateVO.TrendItem> getMonthlyTrend(Long tenantId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");

        List<AccountEntity> accounts = accountMapper.selectList(
                new LambdaQueryWrapper<AccountEntity>()
                        .eq(AccountEntity::getTenantId, tenantId));

        Map<String, Long> monthCounts = new LinkedHashMap<>();
        LocalDateTime cursor = sixMonthsAgo;
        for (int i = 0; i < 6; i++) {
            monthCounts.put(cursor.format(monthFmt), 0L);
            cursor = cursor.plusMonths(1);
        }

        for (AccountEntity account : accounts) {
            if (account.getCreateTime() != null) {
                String month = account.getCreateTime().format(monthFmt);
                monthCounts.merge(month, 1L, Long::sum);
            }
        }

        List<FinanceWorkbenchAggregateVO.TrendItem> trend = new ArrayList<>();
        monthCounts.forEach((month, count) -> {
            FinanceWorkbenchAggregateVO.TrendItem item = new FinanceWorkbenchAggregateVO.TrendItem();
            item.setMonth(month);
            item.setCount(count);
            trend.add(item);
        });
        return trend;
    }
}
