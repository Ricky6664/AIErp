package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据视图字段配置表实体.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_data_view_field")
public class SysDataViewField extends BaseEntity {

    private Long viewId;

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
