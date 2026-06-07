package com.erp.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.crm.dto.CustomerAddressDTO;
import com.erp.module.crm.dto.CustomerAddressQueryDTO;
import com.erp.module.crm.entity.CustomerAddress;
import com.erp.module.crm.vo.CustomerAddressVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户地址Service接口
 *
 * @author AI
 */
public interface CustomerAddressService extends IServiceX<CustomerAddress> {

    /**
     * 分页查询客户地址列表
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    IPage<CustomerAddressVO> list(CustomerAddressQueryDTO query);

    /**
     * 根据ID查询客户地址详情
     *
     * @param id 客户地址ID
     * @return 客户地址VO
     */
    CustomerAddressVO getById(Long id);

    /**
     * 新增客户地址
     *
     * @param dto 客户地址DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Valid CustomerAddressDTO dto);

    /**
     * 更新客户地址
     *
     * @param id  客户地址ID
     * @param dto 客户地址DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid CustomerAddressDTO dto);

    /**
     * 删除客户地址
     *
     * @param id 客户地址ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
