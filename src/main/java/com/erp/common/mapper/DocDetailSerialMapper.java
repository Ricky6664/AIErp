package com.erp.common.mapper;

import com.erp.common.entity.DocDetailSerial;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 序列号管理子表 Mapper 接口.
 *
 * @author AI
 * @since 2026-05-30
 */
@Mapper
public interface DocDetailSerialMapper extends BaseMapperX<DocDetailSerial> {

    /**
     * 根据明细ID查询序列号列表.
     *
     * @param detailId 明细ID
     * @return 序列号列表
     */
    List<DocDetailSerial> selectByDetailId(Long detailId);
}
