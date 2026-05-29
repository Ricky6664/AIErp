package com.erp.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据视图字段 VO.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
public class SysDataViewFieldVO {

    @Data
    public static class ListVO {
        private Long id;

        private Long viewId;

        private String fieldCode;

        private String fieldName;

        private String fieldType;

        private String fieldTypeName;

        private Integer fieldOrder;

        private Boolean isSearchable;

        private Boolean isSortable;

        private Boolean isVisible;
    }

    @Data
    public static class DetailVO {
        private Long id;

        private Long viewId;

        private String fieldCode;

        private String fieldName;

        private String fieldType;

        private String fieldTypeName;

        private Integer fieldOrder;

        private Boolean isSearchable;

        private Boolean isSortable;

        private Boolean isVisible;

        private String searchType;

        private String searchComponent;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;
    }
}
