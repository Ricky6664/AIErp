package com.erp.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 批次辅助属性子表实体.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doc_detail_batch")
public class DocDetailBatch extends BaseEntity {

    /** 明细ID（关联业务单据明细主表） */
    private Long detailId;

    /** 批次号 */
    private String batchNo;

    /** 生产日期 */
    private LocalDate productionDate;

    /** 有效期 */
    private LocalDate expiryDate;

    /** 供应商批号 */
    private String supplierBatchNo;

    /** 数量 */
    private BigDecimal quantity;
}
