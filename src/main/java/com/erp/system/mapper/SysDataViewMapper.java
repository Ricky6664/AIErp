package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysDataView;
import com.erp.system.vo.SysDataViewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 数据视图配置表 Mapper 接口.
 *
 * @author AI
 * @since 2026-05-30
 */
@Mapper
public interface SysDataViewMapper extends BaseMapperX<SysDataView> {

    /**
     * 联查视图主表及其字段配置列表.
     */
    SysDataViewVO.DetailVO selectViewWithFields(@Param("viewId") Long viewId);

    /**
     * 按视图编码查询.
     */
    SysDataView selectByViewCode(@Param("viewCode") String viewCode);
}
