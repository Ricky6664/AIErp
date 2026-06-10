package com.erp.module.srm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.srm.dto.SupplierContactDTO;
import com.erp.module.srm.dto.SupplierContactQueryDTO;
import com.erp.module.srm.entity.SupplierContact;
import com.erp.module.srm.vo.SupplierContactVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供应商联系人Service接口.
 *
 * @author AI
 */
public interface SupplierContactService extends IServiceX<SupplierContact> {

    @Transactional(readOnly = true)
    IPage<SupplierContactVO> list(SupplierContactQueryDTO query);

    @Transactional(readOnly = true)
    SupplierContactVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void save(@Valid SupplierContactDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid SupplierContactDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
