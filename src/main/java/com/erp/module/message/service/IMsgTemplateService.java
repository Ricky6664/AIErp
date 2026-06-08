package com.erp.module.message.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.message.dto.MsgTemplateCreateDTO;
import com.erp.module.message.dto.MsgTemplateQueryDTO;
import com.erp.module.message.dto.MsgTemplateUpdateDTO;
import com.erp.module.message.entity.MsgTemplateEntity;
import com.erp.module.message.vo.MsgTemplateListVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消息模板Service接口.
 *
 * @author AI
 */
public interface IMsgTemplateService extends IServiceX<MsgTemplateEntity> {

    PageResult<MsgTemplateListVO> pageList(MsgTemplateQueryDTO query);

    @Transactional(rollbackFor = Exception.class)
    Long create(MsgTemplateCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, MsgTemplateUpdateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
