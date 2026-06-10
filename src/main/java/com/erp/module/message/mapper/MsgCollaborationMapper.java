package com.erp.module.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.module.message.dto.CollaborationQueryDTO;
import com.erp.module.message.entity.MsgCollaborationEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 协作讨论Mapper接口.
 *
 * @author AI
 */
public interface MsgCollaborationMapper extends BaseMapper<MsgCollaborationEntity> {

    IPage<MsgCollaborationEntity> selectPage(Page<MsgCollaborationEntity> page,
                                             @Param("query") CollaborationQueryDTO query);
}
