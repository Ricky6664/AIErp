package com.erp.module.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预警明细列表项VO.
 *
 * @author AI
 */
@Data
@Schema(description = "预警明细列表项")
public class WarningListItemVO {

    @Schema(description = "预警ID")
    private Long id;

    @Schema(description = "预警标题")
    private String title;

    @Schema(description = "预警内容")
    private String content;

    @Schema(description = "预警类型")
    private String warningType;

    @Schema(description = "是否已处理")
    private Boolean isHandled;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理人ID")
    private Long handlerId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
