package com.erp.hrm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.dto.EmployeeCreateDTO;
import com.erp.hrm.dto.EmployeeQueryDTO;
import com.erp.hrm.dto.EmployeeUpdateDTO;
import com.erp.hrm.entity.EmployeeEntity;
import com.erp.hrm.mapper.EmployeeMapper;
import com.erp.hrm.service.IEmployeeService;
import com.erp.hrm.vo.EmployeeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, EmployeeEntity>
        implements IEmployeeService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "员工档案", action = "新增", description = "新增员工档案")
    public EmployeeVO create(EmployeeCreateDTO dto) {
        validateEmployeeNoUniqueness(dto.getEmployeeNo(), null);
        EmployeeEntity entity = new EmployeeEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "员工档案", action = "修改", description = "修改员工档案")
    public EmployeeVO update(Long id, EmployeeUpdateDTO dto) {
        EmployeeEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "员工档案不存在");
        }
        validateEmployeeNoUniqueness(dto.getEmployeeNo(), id);
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "员工档案", action = "删除", description = "删除员工档案")
    public void delete(Long id) {
        EmployeeEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "员工档案不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeVO getById(Long id) {
        EmployeeEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "员工档案不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<EmployeeVO> pageList(EmployeeQueryDTO query) {
        LambdaQueryWrapper<EmployeeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getName()),
                EmployeeEntity::getName, query.getName());
        wrapper.eq(StringUtils.hasText(query.getEmployeeStatus()),
                EmployeeEntity::getEmployeeStatus, query.getEmployeeStatus());
        wrapper.eq(query.getDepartmentId() != null,
                EmployeeEntity::getDepartmentId, query.getDepartmentId());
        wrapper.orderByDesc(EmployeeEntity::getCreateTime);

        Page<EmployeeEntity> page = new Page<>(
                query.getPageNum() != null ? query.getPageNum() : 1,
                query.getPageSize() != null ? query.getPageSize() : 10);
        IPage<EmployeeEntity> result = page(page, wrapper);
        return PageResult.of(result.convert(this::toVO));
    }

    private void validateEmployeeNoUniqueness(String employeeNo, Long excludeId) {
        if (!StringUtils.hasText(employeeNo)) {
            return;
        }
        LambdaQueryWrapper<EmployeeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeEntity::getEmployeeNo, employeeNo);
        if (excludeId != null) {
            wrapper.ne(EmployeeEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "工号已存在");
        }
    }

    private EmployeeVO toVO(EmployeeEntity entity) {
        EmployeeVO vo = new EmployeeVO();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getIdCard() != null && entity.getIdCard().length() >= 10) {
            vo.setIdCard(entity.getIdCard().substring(0, 6) + "****"
                    + entity.getIdCard().substring(entity.getIdCard().length() - 4));
        }
        return vo;
    }
}
