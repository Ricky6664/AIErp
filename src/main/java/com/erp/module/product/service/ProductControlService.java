package com.erp.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.product.dto.ProductControlDTO;
import com.erp.module.product.dto.ProductControlQueryDTO;
import com.erp.module.product.entity.ProductControl;
import com.erp.module.product.vo.ProductControlVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品控制策略Service接口.
 *
 * @author AI
 */
public interface ProductControlService extends IServiceX<ProductControl> {

    @Transactional(readOnly = true)
    IPage<ProductControlVO> list(ProductControlQueryDTO query);

    @Transactional(readOnly = true)
    ProductControlVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void save(@Valid ProductControlDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid ProductControlDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
