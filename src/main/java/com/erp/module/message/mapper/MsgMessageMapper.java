package com.erp.module.message.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.mapper.BaseMapperX;
import com.erp.module.message.entity.MsgMessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息Mapper.
 *
 * @author AI
 */
@Mapper
public interface MsgMessageMapper extends BaseMapperX<MsgMessageEntity> {

    IPage<MsgMessageEntity> selectPageList(Page<?> page, @Param("query") Object query);
}
