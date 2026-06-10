package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据视图配置表实体.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_data_view")
public class SysDataView extends BaseEntity {

    private String viewCode;

    private String viewName;

    private String sourceTable;

    private Integer sourceType;

    private String sourceSql;

    private String description;
}
