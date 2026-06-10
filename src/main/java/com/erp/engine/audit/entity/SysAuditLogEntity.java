package com.erp.engine.audit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 审核日志实体.
 *
 * @author AI
 */
@Schema(description = "审核日志")
@TableName("sys_audit_log")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysAuditLogEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "单据类型编码")
    private String docType;

    @Schema(description = "单据ID")
    private Long docId;

    @Schema(description = "操作类型: SUBMIT/APPROVE/REJECT/CANCEL")
    private String operationType;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "操作前状态")
    private Integer fromStatus;

    @Schema(description = "操作后状态")
    private Integer toStatus;

    @Schema(description = "审核意见")
    private String opinion;

    @Schema(description = "操作时间")
    private LocalDateTime createdAt;
}
