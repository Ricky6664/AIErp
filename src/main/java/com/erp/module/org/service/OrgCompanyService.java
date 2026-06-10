package com.erp.module.org.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.org.dto.CompanyCreateDTO;
import com.erp.module.org.dto.CompanyQueryDTO;
import com.erp.module.org.dto.CompanyUpdateDTO;
import com.erp.module.org.entity.OrgCompany;
import com.erp.module.org.vo.CompanyDetailVO;
import com.erp.module.org.vo.CompanyListVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

public interface OrgCompanyService extends IServiceX<OrgCompany> {

    @Transactional(readOnly = true)
    PageResult<CompanyListVO> page(CompanyQueryDTO query);

    @Transactional(readOnly = true)
    CompanyDetailVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    Long create(@Valid CompanyCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid CompanyUpdateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
