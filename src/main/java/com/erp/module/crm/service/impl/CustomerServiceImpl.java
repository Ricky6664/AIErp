package com.erp.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.crm.dto.CustomerDTO;
import com.erp.module.crm.dto.CustomerQueryDTO;
import com.erp.module.crm.entity.Customer;
import com.erp.module.crm.mapper.CustomerMapper;
import com.erp.module.crm.service.CustomerService;
import com.erp.module.crm.vo.CustomerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 客户Service实现类.
 *
 * @author AI
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer>
        implements CustomerService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    @Transactional(readOnly = true)
    public IPage<CustomerVO> list(CustomerQueryDTO query) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getCustomerCode()),
                Customer::getCustomerCode, query.getCustomerCode());
        wrapper.like(StringUtils.hasText(query.getCustomerName()),
                Customer::getCustomerName, query.getCustomerName());
        wrapper.eq(query.getClassId() != null,
                Customer::getClassId, query.getClassId());
        wrapper.eq(query.getSalesPersonId() != null,
                Customer::getSalesPersonId, query.getSalesPersonId());
        wrapper.eq(query.getSalesDeptId() != null,
                Customer::getSalesDeptId, query.getSalesDeptId());
        wrapper.eq(query.getCompanyId() != null,
                Customer::getCompanyId, query.getCompanyId());
        wrapper.eq(query.getAuditStatus() != null,
                Customer::getAuditStatus, query.getAuditStatus());
        wrapper.eq(query.getIsActive() != null,
                Customer::getIsActive, query.getIsActive());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "customerCode" -> Customer::getCustomerCode;
                case "customerName" -> Customer::getCustomerName;
                case "createTime" -> Customer::getCreateTime;
                case "updateTime" -> Customer::getUpdateTime;
                default -> Customer::getCreateTime;
            });
        } else {
            wrapper.orderByDesc(Customer::getCreateTime);
        }

        IPage<Customer> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerVO getById(Long id) {
        Customer entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "新增", description = "新增客户")
    public void save(CustomerDTO dto) {
        validateCustomerNameUniqueness(dto.getCustomerName(), dto.getCompanyId(), null);
        validateEmail(dto.getEmail());
        Customer entity = new Customer();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "修改", description = "修改客户")
    public void update(Long id, CustomerDTO dto) {
        Customer existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户不存在");
        }
        validateCustomerNameUniqueness(dto.getCustomerName(), dto.getCompanyId(), id);
        validateEmail(dto.getEmail());
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "删除", description = "删除客户")
    public void delete(Long id) {
        Customer existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户不存在");
        }
        removeById(id);
    }

    private void validateCustomerNameUniqueness(String customerName, Long companyId, Long excludeId) {
        if (!StringUtils.hasText(customerName)) {
            return;
        }
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getCustomerName, customerName);
        if (companyId != null) {
            wrapper.eq(Customer::getCompanyId, companyId);
        }
        if (excludeId != null) {
            wrapper.ne(Customer::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该客户名称在同一公司主体下已存在");
        }
    }

    private void validateEmail(String email) {
        if (StringUtils.hasText(email) && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR, "邮箱格式不正确");
        }
    }

    private CustomerVO toVO(Customer entity) {
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
