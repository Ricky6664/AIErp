package com.erp.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源, 继承 AbstractRoutingDataSource 实现运行时数据源路由.
 *
 * <p>根据 DataSourceContextHolder 中保存的当前线程数据源 key,
 * 从 targetDataSources 映射中选择对应的数据源.
 * 默认使用 master 数据源.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String ds = DataSourceContextHolder.getDataSource();
        if (ds == null) {
            return "master";
        }
        return ds;
    }
}
