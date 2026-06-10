package com.erp.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.crm.dto.CustomerContactDTO;
import com.erp.module.crm.dto.CustomerContactQueryDTO;
import com.erp.module.crm.entity.CustomerContact;
import com.erp.module.crm.mapper.CustomerContactMapper;
import com.erp.module.crm.service.CustomerContactService;
import com.erp.module.crm.vo.CustomerContactVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户联系人Service实现类.
 *
 * @author AI
 */
@Service
public class CustomerContactServiceImpl extends ServiceImpl<CustomerContactMapper, CustomerContact>
        implements CustomerContactService {

    @Override
    @Transactional(readOnly = true)
    public IPage<CustomerContactVO> list(CustomerContactQueryDTO query) {
        LambdaQueryWrapper<CustomerContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getCustomerId() != null, CustomerContact::getCustomerId, query.getCustomerId());
        wrapper.like(StringUtils.hasText(query.getContactName()), CustomerContact::getContactName, query.getContactName());
        wrapper.eq(StringUtils.hasText(query.getGender()), CustomerContact::getGender, query.getGender());
        wrapper.eq(StringUtils.hasText(query.getPosition()), CustomerContact::getPosition, query.getPosition());
        wrapper.eq(query.getIsDefault() != null, CustomerContact::getIsDefault, query.getIsDefault());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        wrapper.orderBy(true, isAsc, CustomerContact::getCreateTime);

        IPage<CustomerContact> page = page(query.toPage(), wrapper);
        List<CustomerContactVO> voList = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return page(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerContactVO getById(Long id) {
        CustomerContact entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户联系人不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "新增", description = "新增客户联系人")
    public void save(CustomerContactDTO dto) {
        validateContactNameUniqueness(dto.getCustomerId(), dto.getContactName(), null);

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefaultContact(dto.getCustomerId());
        }

        CustomerContact entity = new CustomerContact();
        BeanUtils.copyProperties(dto, entity);
        baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "修改", description = "修改客户联系人")
    public void update(Long id, CustomerContactDTO dto) {
        CustomerContact existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户联系人不存在");
        }

        validateContactNameUniqueness(dto.getCustomerId(), dto.getContactName(), id);

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefaultContact(dto.getCustomerId());
        }

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "删除", description = "删除客户联系人")
    public void delete(Long id) {
        CustomerContact existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "客户联系人不存在");
        }
        removeById(id);
    }

    /**
     * 校验同一客户下联系人姓名唯一性.
     */
    private void validateContactNameUniqueness(Long customerId, String contactName, Long excludeId) {
        LambdaQueryWrapper<CustomerContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerContact::getCustomerId, customerId);
        wrapper.eq(CustomerContact::getContactName, contactName);
        if (excludeId != null) {
            wrapper.ne(CustomerContact::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该客户下已存在同名联系人");
        }
    }

    /**
     * 清除同一客户下的其他默认联系人标记.
     */
    private void clearDefaultContact(Long customerId) {
        LambdaQueryWrapper<CustomerContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerContact::getCustomerId, customerId);
        wrapper.eq(CustomerContact::getIsDefault, true);
        List<CustomerContact> list = list(wrapper);
        for (CustomerContact contact : list) {
            contact.setIsDefault(false);
            updateById(contact);
        }
    }

    private CustomerContactVO toVO(CustomerContact entity) {
        CustomerContactVO vo = new CustomerContactVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 构建分页结果.
     */
    private IPage<CustomerContactVO> page(List<CustomerContactVO> records, long total, long current, long size) {
        IPage<CustomerContactVO> result = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        return result;
    }
}
