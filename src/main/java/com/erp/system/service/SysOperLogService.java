package com.erp.system.service;

import com.erp.system.entity.SysOperLog;

/**
 * 操作日志服务接口.
 *
 * @author AI
 * @since 2026-05-30
 */
public interface SysOperLogService {

    /**
     * 异步保存操作日志.
     *
     * @param operLog 操作日志实体
     */
    void save(SysOperLog operLog);
}
