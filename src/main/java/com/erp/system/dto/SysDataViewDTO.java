package com.erp.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 数据视图 DTO.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
public class SysDataViewDTO {

    @Data
    public static class CreateDTO {
        @NotBlank(message = "视图编码不能为空")
        private String viewCode;

        @NotBlank(message = "视图名称不能为空")
        private String viewName;

        @NotBlank(message = "来源表不能为空")
        private String sourceTable;

        private Integer sourceType;

        private String sourceSql;

        private String description;
    }

    @Data
    public static class UpdateDTO {
        private String viewName;

        private String sourceTable;

        private Integer sourceType;

        private String sourceSql;

        private String description;
    }

    @Data
    public static class QueryDTO {
        private String viewCode;

        private String viewName;

        private String sourceTable;

        private Integer sourceType;

        private Integer page = 1;

        private Integer size = 10;
    }
}
