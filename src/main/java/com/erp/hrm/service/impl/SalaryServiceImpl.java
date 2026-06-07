package com.erp.hrm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.dto.SalaryCreateDTO;
import com.erp.hrm.dto.SalaryQueryDTO;
import com.erp.hrm.dto.SalaryUpdateDTO;
import com.erp.hrm.entity.SalaryEntity;
import com.erp.hrm.mapper.SalaryMapper;
import com.erp.hrm.service.ISalaryService;
import com.erp.hrm.vo.SalaryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, SalaryEntity>
        implements ISalaryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "薪资管理", action = "新增", description = "新增薪资记录")
    public SalaryVO create(SalaryCreateDTO dto) {
        validateUniqueness(dto.getEmployeeId(), dto.getSalaryMonth(), null);
        SalaryEntity entity = new SalaryEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setNetSalary(calcNetSalary(dto.getBaseSalary(), dto.getAllowance(), dto.getDeduction()));
        entity.setEnableFlag(true);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "薪资管理", action = "修改", description = "修改薪资记录")
    public SalaryVO update(Long id, SalaryUpdateDTO dto) {
        SalaryEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "薪资记录不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        existing.setNetSalary(calcNetSalary(existing.getBaseSalary(), existing.getAllowance(), existing.getDeduction()));
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "薪资管理", action = "删除", description = "删除薪资记录")
    public void delete(Long id) {
        SalaryEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "薪资记录不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryVO getById(Long id) {
        SalaryEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "薪资记录不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<SalaryVO> pageList(SalaryQueryDTO query) {
        LambdaQueryWrapper<SalaryEntity> wrapper = new LambdaQueryWrapper<SalaryEntity>()
                .eq(query.getEmployeeId() != null,
                        SalaryEntity::getEmployeeId, query.getEmployeeId())
                .eq(StringUtils.hasText(query.getSalaryMonth()),
                        SalaryEntity::getSalaryMonth, query.getSalaryMonth())
                .orderByDesc(SalaryEntity::getCreateTime);

        Page<SalaryEntity> page = new Page<>(
                query.getPageNum() != null ? query.getPageNum() : 1,
                query.getPageSize() != null ? query.getPageSize() : 10);
        IPage<SalaryEntity> result = page(page, wrapper);
        return PageResult.of(result.convert(this::toVO));
    }

    private void validateUniqueness(Long employeeId, String salaryMonth, Long excludeId) {
        if (employeeId == null || !StringUtils.hasText(salaryMonth)) {
            return;
        }
        LambdaQueryWrapper<SalaryEntity> wrapper = new LambdaQueryWrapper<SalaryEntity>()
                .eq(SalaryEntity::getEmployeeId, employeeId)
                .eq(SalaryEntity::getSalaryMonth, salaryMonth);
        if (excludeId != null) {
            wrapper.ne(SalaryEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该员工本月已有薪资记录");
        }
    }

    private BigDecimal calcNetSalary(BigDecimal baseSalary, BigDecimal allowance, BigDecimal deduction) {
        BigDecimal base = baseSalary != null ? baseSalary : BigDecimal.ZERO;
        BigDecimal allow = allowance != null ? allowance : BigDecimal.ZERO;
        BigDecimal deduct = deduction != null ? deduction : BigDecimal.ZERO;
        return base.add(allow).subtract(deduct);
    }

    private SalaryVO toVO(SalaryEntity entity) {
        SalaryVO vo = new SalaryVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
