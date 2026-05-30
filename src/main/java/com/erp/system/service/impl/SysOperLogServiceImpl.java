package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.query.PageQuery;
import com.erp.system.entity.SysOperLog;
import com.erp.system.mapper.SysOperLogMapper;
import com.erp.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public IPage<SysOperLog> pageList(Long operatorId, String module, LocalDateTime startTime,
                                       LocalDateTime endTime, String operatorIp, PageQuery query) {
        LambdaQueryWrapper<SysOperLog> wrapper = buildQueryWrapper(operatorId, module, startTime, endTime, operatorIp);
        wrapper.orderByDesc(SysOperLog::getCreateTime);
        Page<SysOperLog> page = query.toPage();
        return sysOperLogMapper.selectPageByCondition(page, wrapper);
    }

    @Override
    public SysOperLog getById(Long id) {
        return sysOperLogMapper.selectOneById(id);
    }

    @Override
    public void clean() {
        sysOperLogMapper.delete(new LambdaQueryWrapper<>());
        log.info("操作日志已全部清空");
    }

    @Override
    public List<SysOperLog> exportList(Long operatorId, String module, LocalDateTime startTime,
                                        LocalDateTime endTime, String operatorIp) {
        LambdaQueryWrapper<SysOperLog> wrapper = buildQueryWrapper(operatorId, module, startTime, endTime, operatorIp);
        wrapper.orderByDesc(SysOperLog::getCreateTime);
        return sysOperLogMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<SysOperLog> buildQueryWrapper(Long operatorId, String module,
                                                              LocalDateTime startTime, LocalDateTime endTime,
                                                              String operatorIp) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(operatorId != null, SysOperLog::getOperatorId, operatorId)
                .eq(StringUtils.hasText(module), SysOperLog::getModule, module)
                .ge(startTime != null, SysOperLog::getCreateTime, startTime)
                .le(endTime != null, SysOperLog::getCreateTime, endTime)
                .eq(StringUtils.hasText(operatorIp), SysOperLog::getOperatorIp, operatorIp);
        return wrapper;
    }
}
