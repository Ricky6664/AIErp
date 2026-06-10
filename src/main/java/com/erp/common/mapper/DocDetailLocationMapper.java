package com.erp.common.mapper;

import com.erp.common.entity.DocDetailLocation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 库位辅助属性子表 Mapper 接口.
 *
 * @author AI
 * @since 2026-05-30
 */
@Mapper
public interface DocDetailLocationMapper extends BaseMapperX<DocDetailLocation> {

    /**
     * 根据明细ID查询库位列表.
     *
     * @param detailId 明细ID
     * @return 库位列表
     */
    List<DocDetailLocation> selectByDetailId(Long detailId);
}
