package com.erp.common.mapper;

import com.erp.common.entity.DocDetailBatch;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 批次辅助属性子表 Mapper 接口.
 *
 * @author AI
 * @since 2026-05-30
 */
@Mapper
public interface DocDetailBatchMapper extends BaseMapperX<DocDetailBatch> {

    /**
     * 根据明细ID查询批次列表.
     *
     * @param detailId 明细ID
     * @return 批次列表
     */
    List<DocDetailBatch> selectByDetailId(Long detailId);
}
