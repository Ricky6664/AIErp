package com.erp.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.product.dto.ProductUnitDTO;
import com.erp.module.product.dto.ProductUnitQueryDTO;
import com.erp.module.product.entity.ProductUnit;
import com.erp.module.product.vo.ProductUnitVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品多单位Service接口.
 *
 * @author AI
 */
public interface ProductUnitService extends IServiceX<ProductUnit> {

    @Transactional(readOnly = true)
    IPage<ProductUnitVO> list(ProductUnitQueryDTO query);

    @Transactional(readOnly = true)
    ProductUnitVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void save(@Valid ProductUnitDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid ProductUnitDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
