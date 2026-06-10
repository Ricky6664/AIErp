package com.erp.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.product.dto.ProductClassDTO;
import com.erp.module.product.dto.ProductClassQueryDTO;
import com.erp.module.product.entity.ProductClass;
import com.erp.module.product.vo.ProductClassVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品分类Service接口.
 *
 * @author AI
 */
public interface ProductClassService extends IServiceX<ProductClass> {

    /**
     * 分页查询商品分类.
     *
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<ProductClassVO> list(ProductClassQueryDTO query);

    /**
     * 根据ID查询商品分类详情.
     *
     * @param id 商品分类ID
     * @return 商品分类VO
     */
    ProductClassVO getById(Long id);

    /**
     * 新增商品分类.
     *
     * @param dto 商品分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Valid ProductClassDTO dto);

    /**
     * 修改商品分类.
     *
     * @param id  商品分类ID
     * @param dto 商品分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid ProductClassDTO dto);

    /**
     * 删除商品分类.
     *
     * @param id 商品分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
