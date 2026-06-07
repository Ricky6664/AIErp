package com.erp.module.crm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户地址VO.
 *
 * @author AI
 */
@Data
public class CustomerAddressVO {

    private Long id;

    private Long customerId;

    private String addressType;

    private String country;

    private String province;

    private String city;

    private String district;

    private String addressDetail;

    private String contactName;

    private String contactPhone;

    private Boolean isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long creatorId;

    private Long updaterId;
}
