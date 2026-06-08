package com.erp.module.message.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.message.dto.MsgTypeCreateDTO;
import com.erp.module.message.dto.MsgTypeQueryDTO;
import com.erp.module.message.dto.MsgTypeUpdateDTO;
import com.erp.module.message.entity.MsgTypeEntity;
import com.erp.module.message.vo.MsgTypeListVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消息类型Service接口.
 *
 * @author AI
 */
public interface IMsgTypeService extends IServiceX<MsgTypeEntity> {

    PageResult<MsgTypeListVO> pageList(MsgTypeQueryDTO query);

    @Transactional(rollbackFor = Exception.class)
    Long create(MsgTypeCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, MsgTypeUpdateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
