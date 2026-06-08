package com.erp.engine.audit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 审核配置实体.
 *
 * @author AI
 */
@Schema(description = "审核配置")
@TableName("sys_audit_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysAuditConfigEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "单据类型编码")
    private String docType;

    @Schema(description = "单据类型名称")
    private String docTypeName;

    @Schema(description = "是否启用审批流程")
    private Boolean approvalEnabled;

    @Schema(description = "是否自动审核通过")
    private Boolean autoConfirm;
}
