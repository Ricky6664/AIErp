package com.erp.config;

/**
 * 数据源上下文持有者, 使用 ThreadLocal 保存当前线程的数据源 key.
 *
 * <p>在请求开始时由 DataSourceAspect 设置, 请求结束时清理,
 * 避免线程复用时的数据源污染.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前线程的数据源 key.
     *
     * @param ds 数据源名称
     */
    public static void setDataSource(String ds) {
        CONTEXT.set(ds);
    }

    /**
     * 获取当前线程的数据源 key.
     *
     * @return 数据源名称, 未设置时返回 null
     */
    public static String getDataSource() {
        return CONTEXT.get();
    }

    /**
     * 清理当前线程的数据源 key, 必须在请求结束时调用.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
