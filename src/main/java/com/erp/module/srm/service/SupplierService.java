package com.erp.module.srm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.srm.dto.SupplierDTO;
import com.erp.module.srm.dto.SupplierQueryDTO;
import com.erp.module.srm.entity.Supplier;
import com.erp.module.srm.vo.SupplierVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供应商Service接口.
 *
 * @author AI
 */
public interface SupplierService extends IServiceX<Supplier> {

    /**
     * 分页查询供应商.
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    IPage<SupplierVO> list(SupplierQueryDTO query);

    /**
     * 根据ID查询供应商详情.
     *
     * @param id 供应商ID
     * @return 供应商VO
     */
    @Transactional(readOnly = true)
    SupplierVO getById(Long id);

    /**
     * 新增供应商.
     *
     * @param dto 供应商DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Valid SupplierDTO dto);

    /**
     * 修改供应商.
     *
     * @param id  供应商ID
     * @param dto 供应商DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid SupplierDTO dto);

    /**
     * 删除供应商.
     *
     * @param id 供应商ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
