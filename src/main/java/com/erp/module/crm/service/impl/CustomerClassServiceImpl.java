package com.erp.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.crm.dto.CustomerClassDTO;
import com.erp.module.crm.dto.CustomerClassQueryDTO;
import com.erp.module.crm.entity.Customer;
import com.erp.module.crm.entity.CustomerClass;
import com.erp.module.crm.mapper.CustomerClassMapper;
import com.erp.module.crm.mapper.CustomerMapper;
import com.erp.module.crm.service.CustomerClassService;
import com.erp.module.crm.vo.CustomerClassVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 客户分类Service实现类.
 *
 * @author AI
 */
@Service
public class CustomerClassServiceImpl extends ServiceImpl<CustomerClassMapper, CustomerClass>
        implements CustomerClassService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public IPage<CustomerClassVO> list(CustomerClassQueryDTO query) {
        LambdaQueryWrapper<CustomerClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getClassName()), CustomerClass::getClassName, query.getClassName());
        wrapper.eq(query.getParentId() != null, CustomerClass::getParentId, query.getParentId());
        wrapper.eq(query.getIsActive() != null, CustomerClass::getIsActive, query.getIsActive());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if ("createTime".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, CustomerClass::getCreateTime);
        } else {
            wrapper.orderBy(true, isAsc, CustomerClass::getSortOrder);
        }

        IPage<CustomerClass> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerClassVO getById(Long id) {
        CustomerClass entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户分类不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "新增", description = "新增客户分类")
    public void save(CustomerClassDTO dto) {
        validateClassNameUniqueness(dto.getClassName(), dto.getParentId(), null);
        validateParentExists(dto.getParentId());

        CustomerClass entity = new CustomerClass();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "修改", description = "修改客户分类")
    public void update(Long id, CustomerClassDTO dto) {
        CustomerClass existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户分类不存在");
        }

        validateClassNameUniqueness(dto.getClassName(), dto.getParentId(), id);
        validateParentExists(dto.getParentId());

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "删除", description = "删除客户分类")
    public void delete(Long id) {
        CustomerClass existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户分类不存在");
        }

        LambdaQueryWrapper<CustomerClass> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(CustomerClass::getParentId, id);
        if (count(childWrapper) > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "存在子分类，无法删除");
        }

        LambdaQueryWrapper<Customer> customerWrapper = new LambdaQueryWrapper<>();
        customerWrapper.eq(Customer::getClassId, id);
        if (customerMapper.selectCount(customerWrapper) > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该分类下存在关联客户，无法删除");
        }

        removeById(id);
    }

    private void validateClassNameUniqueness(String className, Long parentId, Long excludeId) {
        LambdaQueryWrapper<CustomerClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerClass::getClassName, className);
        if (parentId != null) {
            wrapper.eq(CustomerClass::getParentId, parentId);
        } else {
            wrapper.isNull(CustomerClass::getParentId);
        }
        if (excludeId != null) {
            wrapper.ne(CustomerClass::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "同级分类名称已存在");
        }
    }

    private void validateParentExists(Long parentId) {
        if (parentId != null && parentId > 0) {
            if (super.getById(parentId) == null) {
                throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "上级分类不存在");
            }
        }
    }

    private CustomerClassVO toVO(CustomerClass entity) {
        CustomerClassVO vo = new CustomerClassVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
