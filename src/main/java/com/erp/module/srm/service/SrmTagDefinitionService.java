package com.erp.module.srm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.module.srm.dto.SrmTagDefinitionDTO;
import com.erp.module.srm.dto.SrmTagDefinitionQueryDTO;
import com.erp.module.srm.entity.SrmTagDefinition;
import com.erp.module.srm.vo.SrmTagDefinitionVO;

public interface SrmTagDefinitionService extends IService<SrmTagDefinition> {
    IPage<SrmTagDefinitionVO> list(SrmTagDefinitionQueryDTO query);
    SrmTagDefinitionVO getById(Long id);
    void save(SrmTagDefinitionDTO dto);
    void update(Long id, SrmTagDefinitionDTO dto);
    void delete(Long id);
}
