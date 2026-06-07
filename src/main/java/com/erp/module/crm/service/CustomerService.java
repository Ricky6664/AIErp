package com.erp.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.crm.dto.CustomerDTO;
import com.erp.module.crm.dto.CustomerQueryDTO;
import com.erp.module.crm.entity.Customer;
import com.erp.module.crm.vo.CustomerVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户Service接口
 *
 * @author AI
 */
public interface CustomerService extends IServiceX<Customer> {

    /**
     * 分页查询客户列表
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    IPage<CustomerVO> list(CustomerQueryDTO query);

    /**
     * 根据ID查询客户详情
     *
     * @param id 客户ID
     * @return 客户VO
     */
    @Transactional(readOnly = true)
    CustomerVO getById(Long id);

    /**
     * 新增客户
     *
     * @param dto 客户DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Valid CustomerDTO dto);

    /**
     * 更新客户
     *
     * @param id  客户ID
     * @param dto 客户DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid CustomerDTO dto);

    /**
     * 删除客户
     *
     * @param id 客户ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
