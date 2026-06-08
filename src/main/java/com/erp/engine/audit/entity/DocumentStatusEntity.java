package com.erp.engine.audit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 单据状态实体.
 *
 * @author AI
 */
@Schema(description = "单据状态")
@TableName("sys_document_status")
@Data
public class DocumentStatusEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "单据类型编码")
    private String docType;

    @Schema(description = "单据ID")
    private Long docId;

    @Schema(description = "状态: 0=草稿,1=已提交,2=已审核,3=已驳回,4=已作废")
    private Integer status;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
