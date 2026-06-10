package com.erp.module.message.service;

import com.erp.common.result.PageResult;
import com.erp.module.message.dto.CollaborationCreateDTO;
import com.erp.module.message.dto.CollaborationQueryDTO;
import com.erp.module.message.dto.ReplyCreateDTO;
import com.erp.module.message.vo.CollaborationDetailVO;
import com.erp.module.message.vo.CollaborationListVO;

/**
 * 协作讨论Service接口.
 *
 * @author AI
 */
public interface MsgCollaborationService {

    PageResult<CollaborationListVO> page(CollaborationQueryDTO query);

    CollaborationDetailVO getById(Long id);

    Long create(CollaborationCreateDTO dto);

    Long reply(Long collabId, ReplyCreateDTO dto);

    void closeDiscussion(Long collabId);

    void reopenDiscussion(Long collabId);
}
