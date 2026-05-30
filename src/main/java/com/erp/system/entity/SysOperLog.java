package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志实体.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity {

    /** 所属模块 */
    private String module;

    /** 操作类型: add/delete/edit/query/export/import */
    private String action;

    /** 操作描述 */
    private String description;

    /** 请求方法: GET/POST/PUT/DELETE */
    private String requestMethod;

    /** 请求URL */
    private String requestUrl;

    /** 请求数据(JSON) */
    private String requestData;

    /** 响应数据(JSON) */
    private String responseData;

    /** 操作人IP */
    private String operatorIp;

    /** 操作人ID */
    private Long operatorId;

    /** 操作耗时(毫秒) */
    private Long elapsedMs;

    /** 执行状态: SUCCESS/FAIL */
    private String status;

    /** 错误信息 */
    private String errorMsg;

    /** 异常堆栈(截取前2000字符) */
    private String errorTrace;
}
