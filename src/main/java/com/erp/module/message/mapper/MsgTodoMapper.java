package com.erp.module.message.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.mapper.BaseMapperX;
import com.erp.module.message.entity.MsgTodoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 单据待办Mapper.
 *
 * @author AI
 */
@Mapper
public interface MsgTodoMapper extends BaseMapperX<MsgTodoEntity> {

    IPage<MsgTodoEntity> selectTodoPage(Page<?> page, @Param("query") Object query);
}
