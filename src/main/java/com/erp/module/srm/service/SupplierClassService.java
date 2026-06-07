package com.erp.module.srm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.srm.dto.SupplierClassDTO;
import com.erp.module.srm.dto.SupplierClassQueryDTO;
import com.erp.module.srm.entity.SupplierClass;
import com.erp.module.srm.vo.SupplierClassVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供应商分类Service接口.
 *
 * @author AI
 */
public interface SupplierClassService extends IServiceX<SupplierClass> {

    /**
     * 分页查询供应商分类.
     *
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<SupplierClassVO> list(SupplierClassQueryDTO query);

    /**
     * 根据ID查询供应商分类详情.
     *
     * @param id 供应商分类ID
     * @return 供应商分类VO
     */
    SupplierClassVO getById(Long id);

    /**
     * 新增供应商分类.
     *
     * @param dto 供应商分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Valid SupplierClassDTO dto);

    /**
     * 修改供应商分类.
     *
     * @param id  供应商分类ID
     * @param dto 供应商分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid SupplierClassDTO dto);

    /**
     * 删除供应商分类.
     *
     * @param id 供应商分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
