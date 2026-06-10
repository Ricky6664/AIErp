package com.erp.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数据视图字段 DTO.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
public class SysDataViewFieldDTO {

    @Data
    public static class CreateDTO {
        @NotNull(message = "视图ID不能为空")
        private Long viewId;

        @NotBlank(message = "字段编码不能为空")
        private String fieldCode;

        @NotBlank(message = "字段名称不能为空")
        private String fieldName;

        private String fieldType;

        private Integer fieldOrder;

        private Boolean isSearchable;

        private Boolean isSortable;

        private Boolean isVisible;

        private String searchType;

        private String searchComponent;
    }

    @Data
    public static class UpdateDTO {
        private String fieldCode;

        private String fieldName;

        private String fieldType;

        private Integer fieldOrder;

        private Boolean isSearchable;

        private Boolean isSortable;

        private Boolean isVisible;

        private String searchType;

        private String searchComponent;
    }

    @Data
    public static class QueryDTO {
        private Long viewId;

        private String fieldCode;

        private String fieldName;

        private Integer page = 1;

        private Integer size = 10;
    }
}
