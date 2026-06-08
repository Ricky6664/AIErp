package com.erp.system.announcement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 公告创建DTO.
 *
 * @author AI
 */
@Data
public class AnnouncementCreateDTO {

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题最长200个字符")
    private String title;

    private String content;

    @Size(max = 50, message = "公告类型最长50个字符")
    private String announcementType;

    private java.time.LocalDateTime publishTime;

    private Boolean isTop;

    private Integer status;
}
