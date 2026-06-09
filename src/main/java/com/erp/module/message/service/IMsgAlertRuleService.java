package com.erp.module.message.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.message.dto.MsgAlertRuleCreateDTO;
import com.erp.module.message.dto.MsgAlertRuleQueryDTO;
import com.erp.module.message.dto.MsgAlertRuleUpdateDTO;
import com.erp.module.message.entity.MsgAlertRuleEntity;
import com.erp.module.message.vo.MsgAlertRuleListVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务预警规则Service接口.
 *
 * @author AI
 */
public interface IMsgAlertRuleService extends IServiceX<MsgAlertRuleEntity> {

    PageResult<MsgAlertRuleListVO> pageList(MsgAlertRuleQueryDTO query);

    @Transactional(rollbackFor = Exception.class)
    Long create(MsgAlertRuleCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, MsgAlertRuleUpdateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
