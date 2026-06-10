package com.erp.hrm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.dto.AttendanceCreateDTO;
import com.erp.hrm.dto.AttendanceQueryDTO;
import com.erp.hrm.dto.AttendanceUpdateDTO;
import com.erp.hrm.entity.AttendanceEntity;
import com.erp.hrm.mapper.AttendanceMapper;
import com.erp.hrm.service.IAttendanceService;
import com.erp.hrm.vo.AttendanceVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, AttendanceEntity>
        implements IAttendanceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "考勤管理", action = "新增", description = "新增考勤记录")
    public AttendanceVO create(AttendanceCreateDTO dto) {
        validateAttendanceUniqueness(dto.getEmployeeId(), dto.getAttendanceDate(), null);
        AttendanceEntity entity = new AttendanceEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "考勤管理", action = "修改", description = "修改考勤记录")
    public AttendanceVO update(Long id, AttendanceUpdateDTO dto) {
        AttendanceEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "考勤记录不存在");
        }
        validateAttendanceUniqueness(dto.getEmployeeId(), dto.getAttendanceDate(), id);
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "考勤管理", action = "删除", description = "删除考勤记录")
    public void delete(Long id) {
        AttendanceEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "考勤记录不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceVO getById(Long id) {
        AttendanceEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "考勤记录不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AttendanceVO> pageList(AttendanceQueryDTO query) {
        LambdaQueryWrapper<AttendanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getEmployeeId() != null,
                AttendanceEntity::getEmployeeId, query.getEmployeeId());
        wrapper.eq(query.getAttendanceDate() != null,
                AttendanceEntity::getAttendanceDate, query.getAttendanceDate());
        wrapper.eq(StringUtils.hasText(query.getAttendanceType()),
                AttendanceEntity::getAttendanceType, query.getAttendanceType());
        wrapper.orderByDesc(AttendanceEntity::getCreateTime);

        Page<AttendanceEntity> page = new Page<>(
                query.getPageNum() != null ? query.getPageNum() : 1,
                query.getPageSize() != null ? query.getPageSize() : 10);
        IPage<AttendanceEntity> result = page(page, wrapper);
        return PageResult.of(result.convert(this::toVO));
    }

    private void validateAttendanceUniqueness(Long employeeId, java.time.LocalDate attendanceDate, Long excludeId) {
        if (employeeId == null || attendanceDate == null) {
            return;
        }
        LambdaQueryWrapper<AttendanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceEntity::getEmployeeId, employeeId);
        wrapper.eq(AttendanceEntity::getAttendanceDate, attendanceDate);
        if (excludeId != null) {
            wrapper.ne(AttendanceEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "同一员工同一考勤日期已存在考勤记录");
        }
    }

    private AttendanceVO toVO(AttendanceEntity entity) {
        AttendanceVO vo = new AttendanceVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
