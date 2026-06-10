package com.erp.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 编码规则 DTO.
 *
 * @author AI
 * @since 2026-05-29
 */
@Data
public class SysCodeRuleDTO {

    @Data
    public static class CreateDTO {
        @NotBlank(message = "规则编码不能为空")
        private String ruleCode;

        @NotBlank(message = "规则名称不能为空")
        private String ruleName;

        private String moduleCode;

        private String description;

        private String separator;

        private Integer isEnabled;

        @NotNull(message = "编码段列表不能为空")
        @Size(min = 1, message = "至少需要1个编码段")
        private List<SegmentDTO> segments;
    }

    @Data
    public static class UpdateDTO {
        private String ruleName;

        private String moduleCode;

        private String description;

        private String separator;

        private Integer isEnabled;

        private List<SegmentDTO> segments;
    }

    @Data
    public static class QueryDTO {
        private String ruleCode;

        private String ruleName;

        private String moduleCode;

        private Integer isEnabled;

        private Integer page = 1;

        private Integer size = 10;
    }

    @Data
    public static class SegmentDTO {
        @NotNull(message = "段类型不能为空")
        private Integer segmentType;

        @NotNull(message = "段排序不能为空")
        private Integer segmentOrder;

        private String segmentValue;

        private Integer segmentLength;

        private String segmentFormat;
    }
}
