package com.erp.finance.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 凭证字VO.
 *
 * @author AI
 */
@Data
public class VoucherWordVO {

    private Long id;

    private String wordName;

    private String wordCode;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
