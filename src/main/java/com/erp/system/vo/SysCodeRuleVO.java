package com.erp.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 编码规则 VO.
 *
 * @author AI
 * @since 2026-05-29
 */
@Data
public class SysCodeRuleVO {

    @Data
    public static class ListVO {
        private Long id;

        private String ruleCode;

        private String ruleName;

        private String moduleCode;

        private Integer isEnabled;

        private String isEnabledName;

        private String description;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    public static class DetailVO {
        private Long id;

        private String ruleCode;

        private String ruleName;

        private String moduleCode;

        private String description;

        private String separator;

        private Long currentValue;

        private Integer isEnabled;

        private String isEnabledName;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

        private List<SegmentVO> segments;
    }

    @Data
    public static class SegmentVO {
        private Long id;

        private Long ruleId;

        private Integer segmentType;

        private String segmentTypeName;

        private Integer segmentOrder;

        private String segmentValue;

        private Integer segmentLength;

        private String segmentFormat;
    }
}
