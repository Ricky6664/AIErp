package com.erp.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.product.dto.ProductDTO;
import com.erp.module.product.dto.ProductQueryDTO;
import com.erp.module.product.entity.Product;
import com.erp.module.product.vo.ProductVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品主表Service接口.
 *
 * @author AI
 */
public interface ProductService extends IServiceX<Product> {

    @Transactional(readOnly = true)
    IPage<ProductVO> list(ProductQueryDTO query);

    @Transactional(readOnly = true)
    ProductVO getById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void save(@Valid ProductDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid ProductDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
