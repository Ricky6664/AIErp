package com.erp.module.message.service;

import com.erp.common.result.PageResult;
import com.erp.module.message.dto.DiscussionCreateDTO;
import com.erp.module.message.dto.DiscussionQueryDTO;
import com.erp.module.message.vo.DiscussionListVO;

import java.util.List;

/**
 * 单据沟通Service接口.
 *
 * @author AI
 */
public interface MsgDiscussionService {

    PageResult<DiscussionListVO> pageByDoc(DiscussionQueryDTO query);

    Long create(DiscussionCreateDTO dto);

    List<DiscussionListVO> getReplies(Long parentId);
}
