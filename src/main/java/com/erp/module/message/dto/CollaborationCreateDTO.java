package com.erp.module.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 协作讨论创建DTO.
 *
 * @author AI
 */
@Data
public class CollaborationCreateDTO {

    @NotBlank(message = "讨论主题不能为空")
    @Size(max = 200, message = "主题长度不能超过200")
    private String topic;

    private String category;

    private String content;

    private List<Long> participantIds;
}
