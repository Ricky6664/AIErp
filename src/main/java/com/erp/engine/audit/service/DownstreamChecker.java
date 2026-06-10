package com.erp.engine.audit.service;

/**
 * 下游单据检查器接口.
 * 各业务模块实现此接口注册到审核引擎，反审时自动调用.
 *
 * @author AI
 */
public interface DownstreamChecker {

    /**
     * 检查指定单据是否存在下游关联单据.
     *
     * @param docId 上游单据ID
     * @return 检查结果，包含是否存在下游及下游单据编号
     */
    DownstreamCheckResult check(Long docId);

    /**
     * 返回此检查器支持的单据类型.
     *
     * @return 单据类型编码
     */
    String supportedDocType();
}
