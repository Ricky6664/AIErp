package com.erp.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 单据关联关系实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doc_relation")
public class DocRelationEntity extends BaseEntity {

    private String sourceDocType;

    private Long sourceDocId;

    private Long sourceDetailId;

    private String targetDocType;

    private Long targetDocId;

    private Long targetDetailId;

    private String relationType;

    private BigDecimal relationQty;
}
