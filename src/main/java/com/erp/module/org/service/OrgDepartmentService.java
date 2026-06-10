package com.erp.module.org.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.org.dto.DeptCreateDTO;
import com.erp.module.org.dto.DeptQueryDTO;
import com.erp.module.org.dto.DeptUpdateDTO;
import com.erp.module.org.entity.OrgDepartment;
import com.erp.module.org.vo.DeptDetailVO;
import com.erp.module.org.vo.DeptListVO;
import com.erp.module.org.vo.DeptTreeVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrgDepartmentService extends IServiceX<OrgDepartment> {

    @Transactional(readOnly = true)
    PageResult<DeptListVO> page(DeptQueryDTO query);

    @Transactional(readOnly = true)
    List<DeptTreeVO> tree(Long companyId);

    @Transactional(readOnly = true)
    DeptDetailVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    Long create(@Valid DeptCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid DeptUpdateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
