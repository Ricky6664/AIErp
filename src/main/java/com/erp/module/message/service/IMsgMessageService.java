package com.erp.module.message.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.message.dto.MsgMessageCreateDTO;
import com.erp.module.message.dto.MsgMessageQueryDTO;
import com.erp.module.message.dto.MsgMessageUpdateDTO;
import com.erp.module.message.entity.MsgMessageEntity;
import com.erp.module.message.vo.MsgMessageListVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消息Service接口.
 *
 * @author AI
 */
public interface IMsgMessageService extends IServiceX<MsgMessageEntity> {

    PageResult<MsgMessageListVO> pageList(MsgMessageQueryDTO query);

    @Transactional(rollbackFor = Exception.class)
    Long create(MsgMessageCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, MsgMessageUpdateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    @Transactional(rollbackFor = Exception.class)
    void read(Long id);

    @Transactional(rollbackFor = Exception.class)
    void readAll();
}
