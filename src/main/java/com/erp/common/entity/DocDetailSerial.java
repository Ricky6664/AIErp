package com.erp.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 序列号管理子表实体.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doc_detail_serial")
public class DocDetailSerial extends BaseEntity {

    /** 明细ID（关联业务单据明细主表） */
    private Long detailId;

    /** 序列号 */
    private String serialNo;

    /** 序列号状态：1在库、2出库、3报废 */
    private Integer status;

    /** 激活时间 */
    private LocalDateTime activationTime;
}
