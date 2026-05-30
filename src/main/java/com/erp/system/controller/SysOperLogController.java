package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.entity.SysOperLog;
import com.erp.system.mapper.SysOperLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "操作日志管理", description = "操作日志分页查询、详情、导出、清理接口")
@RestController
@RequestMapping("/api/system/oper-logs")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogMapper sysOperLogMapper;

    @Operation(summary = "分页查询操作日志")
    @RequirePermission("system:oper-log:query")
    @GetMapping("/page")
    public RT<PageResult<SysOperLog>> pageList(
            @Parameter(description = "操作人ID") @RequestParam(required = false) Long operatorId,
            @Parameter(description = "模块") @RequestParam(required = false) String module,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "操作IP") @RequestParam(required = false) String operatorIp,
            @Valid PageQuery query) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(operatorId != null, SysOperLog::getOperatorId, operatorId)
                .eq(StringUtils.hasText(module), SysOperLog::getModule, module)
                .ge(startTime != null, SysOperLog::getCreateTime, startTime)
                .le(endTime != null, SysOperLog::getCreateTime, endTime)
                .eq(StringUtils.hasText(operatorIp), SysOperLog::getOperatorIp, operatorIp)
                .orderByDesc(SysOperLog::getCreateTime);
        Page<SysOperLog> page = query.toPage();
        IPage<SysOperLog> result = sysOperLogMapper.selectPageByCondition(page, wrapper);
        return RT.ok(PageResult.of(result));
    }

    @Operation(summary = "查询操作日志详情")
    @RequirePermission("system:oper-log:query")
    @GetMapping("/{id}")
    public RT<SysOperLog> getById(
            @Parameter(description = "日志ID") @PathVariable Long id) {
        SysOperLog log = sysOperLogMapper.selectOneById(id);
        if (log == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "操作日志不存在: id=" + id);
        }
        return RT.ok(log);
    }

    @Operation(summary = "清空操作日志")
    @RequirePermission("system:oper-log:query")
    @DeleteMapping("/clean")
    public RT<Void> clean() {
        sysOperLogMapper.delete(new LambdaQueryWrapper<>());
        log.info("操作日志已全部清空");
        return RT.ok();
    }

    @Operation(summary = "导出操作日志")
    @RequirePermission("system:oper-log:query")
    @GetMapping("/export")
    public RT<List<SysOperLog>> export(
            @Parameter(description = "操作人ID") @RequestParam(required = false) Long operatorId,
            @Parameter(description = "模块") @RequestParam(required = false) String module,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "操作IP") @RequestParam(required = false) String operatorIp) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(operatorId != null, SysOperLog::getOperatorId, operatorId)
                .eq(StringUtils.hasText(module), SysOperLog::getModule, module)
                .ge(startTime != null, SysOperLog::getCreateTime, startTime)
                .le(endTime != null, SysOperLog::getCreateTime, endTime)
                .eq(StringUtils.hasText(operatorIp), SysOperLog::getOperatorIp, operatorIp)
                .orderByDesc(SysOperLog::getCreateTime);
        List<SysOperLog> list = sysOperLogMapper.selectList(wrapper);
        return RT.ok(list);
    }
}
