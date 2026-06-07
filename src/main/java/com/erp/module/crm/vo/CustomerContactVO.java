package com.erp.module.crm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户联系人VO.
 *
 * @author AI
 */
@Data
public class CustomerContactVO {

    private Long id;

    private Long customerId;

    private String contactName;

    private String nickname;

    private String gender;

    private String position;

    private String role;

    private Boolean isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long creatorId;

    private Long updaterId;
}
