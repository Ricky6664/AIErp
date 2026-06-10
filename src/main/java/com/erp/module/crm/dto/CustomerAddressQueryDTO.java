package com.erp.module.crm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户地址查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户地址查询参数")
public class CustomerAddressQueryDTO extends PageQuery {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "地址类型（收货/开票/其他）")
    private String addressType;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "是否默认地址")
    private Boolean isDefault;
}
