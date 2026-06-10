package com.erp.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.crm.dto.CustomerContactDTO;
import com.erp.module.crm.dto.CustomerContactQueryDTO;
import com.erp.module.crm.entity.CustomerContact;
import com.erp.module.crm.vo.CustomerContactVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户联系人Service接口.
 *
 * @author AI
 */
public interface CustomerContactService extends IServiceX<CustomerContact> {

    @Transactional(readOnly = true)
    IPage<CustomerContactVO> list(CustomerContactQueryDTO query);

    @Transactional(readOnly = true)
    CustomerContactVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void save(@Valid CustomerContactDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid CustomerContactDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
