package com.erp.module.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.common.service.ServiceImplX;
import com.erp.hrm.entity.EmployeeEntity;
import com.erp.hrm.mapper.EmployeeMapper;
import com.erp.module.org.dto.PositionCreateDTO;
import com.erp.module.org.dto.PositionQueryDTO;
import com.erp.module.org.dto.PositionUpdateDTO;
import com.erp.module.org.entity.OrgPosition;
import com.erp.module.org.mapper.OrgPositionMapper;
import com.erp.module.org.service.OrgPositionService;
import com.erp.module.org.vo.PositionDetailVO;
import com.erp.module.org.vo.PositionListVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrgPositionServiceImpl extends ServiceImplX<OrgPositionMapper, OrgPosition>
        implements OrgPositionService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResult<PositionListVO> page(PositionQueryDTO query) {
        LambdaQueryWrapper<OrgPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getDepartmentId() != null,
                OrgPosition::getDepartmentId, query.getDepartmentId());
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(OrgPosition::getPositionName, query.getKeyword())
                    .or()
                    .like(OrgPosition::getPositionCode, query.getKeyword()));
        }
        wrapper.eq(query.getEnableFlag() != null,
                OrgPosition::getEnableFlag, query.getEnableFlag());
        wrapper.orderByAsc(OrgPosition::getSortNo);
        wrapper.orderByDesc(OrgPosition::getCreateTime);

        IPage<OrgPosition> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toListVO));
    }

    @Override
    @Transactional(readOnly = true)
    public PositionDetailVO getById(Long id) {
        OrgPosition entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "岗位不存在");
        }
        return toDetailVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "新增", description = "新增岗位")
    public Long create(PositionCreateDTO dto) {
        validateDepartmentExists(dto.getDepartmentId());
        validatePositionNameUniqueness(dto.getPositionName(), dto.getDepartmentId(), null);
        OrgPosition entity = new OrgPosition();
        BeanUtils.copyProperties(dto, entity);
        entity.setPositionCode(generatePositionCode());
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "修改", description = "修改岗位")
    public void update(Long id, PositionUpdateDTO dto) {
        OrgPosition entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "岗位不存在");
        }
        if (dto.getDepartmentId() != null
                && !dto.getDepartmentId().equals(entity.getDepartmentId())) {
            validateDepartmentExists(dto.getDepartmentId());
            validatePositionNameUniqueness(dto.getPositionName(), dto.getDepartmentId(), id);
        } else {
            validatePositionNameUniqueness(dto.getPositionName(), entity.getDepartmentId(), id);
        }
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "删除", description = "删除岗位")
    public void delete(Long id) {
        OrgPosition entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "岗位不存在");
        }
        LambdaQueryWrapper<EmployeeEntity> empWrapper = new LambdaQueryWrapper<>();
        empWrapper.eq(EmployeeEntity::getPositionId, id);
        if (employeeMapper.selectCount(empWrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该岗位下存在员工，无法删除");
        }
        removeById(id);
    }

    private void validateDepartmentExists(Long departmentId) {
        if (departmentId == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "所属部门不存在");
        }
    }

    private void validatePositionNameUniqueness(String positionName, Long departmentId, Long excludeId) {
        if (!StringUtils.hasText(positionName) || departmentId == null) {
            return;
        }
        LambdaQueryWrapper<OrgPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgPosition::getPositionName, positionName);
        wrapper.eq(OrgPosition::getDepartmentId, departmentId);
        if (excludeId != null) {
            wrapper.ne(OrgPosition::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "同一部门下岗位名称已存在");
        }
    }

    private String generatePositionCode() {
        return "POS" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private PositionListVO toListVO(OrgPosition entity) {
        PositionListVO vo = new PositionListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private PositionDetailVO toDetailVO(OrgPosition entity) {
        PositionDetailVO vo = new PositionDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
