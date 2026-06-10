package com.erp.module.org.service;

import com.erp.module.org.vo.WorkbenchVO;
import org.springframework.transaction.annotation.Transactional;

public interface OrgWorkbenchService {

    @Transactional(readOnly = true)
    WorkbenchVO getWorkbenchData();
}
