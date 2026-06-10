package com.erp.module.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.message.entity.MsgCollaborationReplyEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 协作讨论回复Mapper接口.
 *
 * @author AI
 */
public interface MsgCollaborationReplyMapper extends BaseMapper<MsgCollaborationReplyEntity> {

    @Select("SELECT * FROM msg_collaboration_reply WHERE collaboration_id = #{collaborationId} AND is_deleted = false ORDER BY create_time ASC")
    List<MsgCollaborationReplyEntity> selectByCollaborationId(@Param("collaborationId") Long collaborationId);
}
