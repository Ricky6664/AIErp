package com.erp.module.crm.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.crm.dto.CustomerTagRelDTO;
import com.erp.module.crm.dto.CustomerTagRelQueryDTO;
import com.erp.module.crm.entity.CustomerTagRel;
import com.erp.module.crm.vo.CustomerTagRelVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户标签关联Service接口.
 *
 * @author AI
 */
public interface CustomerTagRelService extends IServiceX<CustomerTagRel> {

    /**
     * 分页查询客户标签关联列表.
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<CustomerTagRelVO> list(CustomerTagRelQueryDTO query);

    /**
     * 根据ID查询客户标签关联详情.
     *
     * @param id 关联ID
     * @return 客户标签关联VO
     */
    CustomerTagRelVO getById(Long id);

    /**
     * 新增客户标签关联.
     *
     * @param dto 客户标签关联DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Valid CustomerTagRelDTO dto);

    /**
     * 批量保存客户标签关联（先删后增）.
     *
     * @param customerId 客户ID
     * @param tagIds     标签ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    void saveBatch(Long customerId, java.util.List<Long> tagIds);

    /**
     * 删除客户标签关联.
     *
     * @param id 关联ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
