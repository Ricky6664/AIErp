package com.erp.system.service.impl;

import com.erp.system.entity.SysOperLog;
import com.erp.system.mapper.SysOperLogMapper;
import com.erp.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现.
 *
 * <p>异步保存操作日志, 避免影响主流程响应时间.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements SysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    @Async
    @Override
    public void save(SysOperLog operLog) {
        sysOperLogMapper.insert(operLog);
        log.debug("操作日志已异步保存: module={}, action={}, status={}",
                operLog.getModule(), operLog.getAction(), operLog.getStatus());
    }
}
