package com.erp.system.announcement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnnouncementUpdateDTO extends AnnouncementCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
