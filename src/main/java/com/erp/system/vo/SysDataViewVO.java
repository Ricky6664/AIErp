package com.erp.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据视图 VO.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
public class SysDataViewVO {

    @Data
    public static class ListVO {
        private Long id;

        private String viewCode;

        private String viewName;

        private String sourceTable;

        private Integer sourceType;

        private String sourceTypeName;

        private String description;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    public static class DetailVO {
        private Long id;

        private String viewCode;

        private String viewName;

        private String sourceTable;

        private Integer sourceType;

        private String sourceTypeName;

        private String sourceSql;

        private String description;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

        private List<SysDataViewFieldVO.DetailVO> fields;
    }
}
