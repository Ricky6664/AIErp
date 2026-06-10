package com.erp.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.service.IServiceX;
import com.erp.module.crm.dto.CustomerClassDTO;
import com.erp.module.crm.dto.CustomerClassQueryDTO;
import com.erp.module.crm.entity.CustomerClass;
import com.erp.module.crm.vo.CustomerClassVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户分类Service接口
 *
 * @author AI
 */
public interface CustomerClassService extends IServiceX<CustomerClass> {

    /**
     * 分页查询客户分类列表
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    IPage<CustomerClassVO> list(CustomerClassQueryDTO query);

    /**
     * 根据ID查询客户分类详情
     *
     * @param id 客户分类ID
     * @return 客户分类VO
     */
    @Transactional(readOnly = true)
    CustomerClassVO getById(Long id);

    /**
     * 新增客户分类
     *
     * @param dto 客户分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Valid CustomerClassDTO dto);

    /**
     * 更新客户分类
     *
     * @param id  客户分类ID
     * @param dto 客户分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid CustomerClassDTO dto);

    /**
     * 删除客户分类
     *
     * @param id 客户分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
