package com.erp.module.org.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.org.dto.PositionCreateDTO;
import com.erp.module.org.dto.PositionQueryDTO;
import com.erp.module.org.dto.PositionUpdateDTO;
import com.erp.module.org.entity.OrgPosition;
import com.erp.module.org.vo.PositionDetailVO;
import com.erp.module.org.vo.PositionListVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

public interface OrgPositionService extends IServiceX<OrgPosition> {

    @Transactional(readOnly = true)
    PageResult<PositionListVO> page(PositionQueryDTO query);

    @Transactional(readOnly = true)
    PositionDetailVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    Long create(@Valid PositionCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid PositionUpdateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
