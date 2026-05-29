package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysDataViewField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据视图字段配置表 Mapper 接口.
 *
 * @author AI
 * @since 2026-05-30
 */
@Mapper
public interface SysDataViewFieldMapper extends BaseMapperX<SysDataViewField> {

    /**
     * 查询指定视图的可见字段列表, 按 field_order 排序.
     */
    @Select("SELECT * FROM sys_data_view_field WHERE view_id = #{viewId} AND is_visible = TRUE AND is_deleted = FALSE ORDER BY field_order ASC")
    List<SysDataViewField> selectVisibleFields(@Param("viewId") Long viewId);
}
