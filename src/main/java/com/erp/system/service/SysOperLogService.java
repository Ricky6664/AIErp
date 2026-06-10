package com.erp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.query.PageQuery;
import com.erp.system.entity.SysOperLog;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 条件分页查询操作日志, 默认按创建时间倒序.
     */
    IPage<SysOperLog> pageList(Long operatorId, String module, LocalDateTime startTime,
                               LocalDateTime endTime, String operatorIp, PageQuery query);

    /**
     * 根据ID查询操作日志详情.
     */
    SysOperLog getById(Long id);

    /**
     * 清空所有操作日志.
     */
    void clean();

    /**
     * 按条件查询操作日志列表(用于导出).
     */
    List<SysOperLog> exportList(Long operatorId, String module, LocalDateTime startTime,
                                LocalDateTime endTime, String operatorIp);
}
