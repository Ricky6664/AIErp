package com.erp.system.announcement.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "公告查询参数")
public class AnnouncementQueryDTO extends PageQuery {

    @Schema(description = "公告标题（模糊搜索）")
    private String title;

    @Schema(description = "公告类型")
    private String announcementType;

    @Schema(description = "状态: 1=草稿 2=已发布 3=已撤回")
    private Integer status;

    @Schema(description = "是否置顶")
    private Boolean isTop;
}
