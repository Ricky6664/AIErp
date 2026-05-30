package com.erp.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库位辅助属性子表实体.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doc_detail_location")
public class DocDetailLocation extends BaseEntity {

    /** 明细ID（关联业务单据明细主表） */
    private Long detailId;

    /** 库位编码 */
    private String locationCode;

    /** 仓库编码 */
    private String warehouseCode;

    /** 区域编码 */
    private String areaCode;

    /** 货架编码 */
    private String shelfCode;

    /** 层级编码 */
    private String layerCode;

    /** 数量 */
    private BigDecimal quantity;

    /** 是否默认库位(1=是,0=否) */
    private Integer isDefault;
}
