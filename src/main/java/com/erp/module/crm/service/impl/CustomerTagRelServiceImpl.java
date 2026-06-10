package com.erp.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.common.service.ServiceImplX;
import com.erp.module.crm.dto.CustomerTagRelDTO;
import com.erp.module.crm.dto.CustomerTagRelQueryDTO;
import com.erp.module.crm.entity.CustomerTagRel;
import com.erp.module.crm.mapper.CustomerTagRelMapper;
import com.erp.module.crm.service.CustomerTagRelService;
import com.erp.module.crm.vo.CustomerTagRelVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户标签关联 Service 实现类.
 *
 * @author AI
 * @since 2026-06-08
 */
@Service
public class CustomerTagRelServiceImpl extends ServiceImplX<CustomerTagRelMapper, CustomerTagRel>
        implements CustomerTagRelService {

    @Override
    @Transactional(readOnly = true)
    public PageResult<CustomerTagRelVO> list(CustomerTagRelQueryDTO query) {
        LambdaQueryWrapper<CustomerTagRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getCustomerId() != null, CustomerTagRel::getCustomerId, query.getCustomerId());
        wrapper.eq(query.getTagId() != null, CustomerTagRel::getTagId, query.getTagId());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if ("createTime".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, CustomerTagRel::getCreateTime);
        } else {
            wrapper.orderBy(true, isAsc, CustomerTagRel::getId);
        }
        return super.pageList(query, wrapper).convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerTagRelVO getById(Long id) {
        CustomerTagRel entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户标签关联不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "新增", description = "新增客户标签关联")
    public void save(CustomerTagRelDTO dto) {
        checkCustomerTagRelUnique(dto.getCustomerId(), dto.getTagId(), null);
        CustomerTagRel entity = new CustomerTagRel();
        BeanUtils.copyProperties(dto, entity);
        super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "新增", description = "批量保存客户标签关联")
    public void saveBatch(Long customerId, List<Long> tagIds) {
        LambdaQueryWrapper<CustomerTagRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTagRel::getCustomerId, customerId);
        super.remove(wrapper);

        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                CustomerTagRel entity = new CustomerTagRel();
                entity.setCustomerId(customerId);
                entity.setTagId(tagId);
                super.save(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "删除", description = "删除客户标签关联")
    public void delete(Long id) {
        CustomerTagRel existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户标签关联不存在");
        }
        super.removeById(id);
    }

    /**
     * 查询客户的所有标签关联.
     *
     * @param customerId 客户ID
     * @return 标签关联列表
     */
    @Transactional(readOnly = true)
    public List<CustomerTagRelVO> listByCustomerId(Long customerId) {
        LambdaQueryWrapper<CustomerTagRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTagRel::getCustomerId, customerId);
        return list(wrapper).stream().map(this::toVO).toList();
    }

    /**
     * 查询标签关联的所有客户.
     *
     * @param tagId 标签ID
     * @return 标签关联列表
     */
    @Transactional(readOnly = true)
    public List<CustomerTagRelVO> listByTagId(Long tagId) {
        LambdaQueryWrapper<CustomerTagRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTagRel::getTagId, tagId);
        return list(wrapper).stream().map(this::toVO).toList();
    }

    private void checkCustomerTagRelUnique(Long customerId, Long tagId, Long excludeId) {
        LambdaQueryWrapper<CustomerTagRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTagRel::getCustomerId, customerId);
        wrapper.eq(CustomerTagRel::getTagId, tagId);
        if (excludeId != null) {
            wrapper.ne(CustomerTagRel::getId, excludeId);
        }
        if (super.count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该客户已存在相同的标签关联");
        }
    }

    private CustomerTagRelVO toVO(CustomerTagRel entity) {
        CustomerTagRelVO vo = new CustomerTagRelVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
