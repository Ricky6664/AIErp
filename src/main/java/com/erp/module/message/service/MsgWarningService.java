package com.erp.module.message.service;

import com.erp.module.message.entity.MsgWarningEntity;
import com.erp.module.message.vo.WarningDashboardVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务预警触发Service接口.
 *
 * @author AI
 */
public interface MsgWarningService {

    @Transactional(rollbackFor = Exception.class)
    void scanAndAlert();

    WarningDashboardVO getWarningDashboard(String module);

    @Transactional(rollbackFor = Exception.class)
    void handleWarning(Long warningId);
}
