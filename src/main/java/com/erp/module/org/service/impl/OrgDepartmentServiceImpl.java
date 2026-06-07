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
import com.erp.module.org.dto.DeptCreateDTO;
import com.erp.module.org.dto.DeptQueryDTO;
import com.erp.module.org.dto.DeptUpdateDTO;
import com.erp.module.org.entity.OrgDepartment;
import com.erp.module.org.entity.OrgPosition;
import com.erp.module.org.mapper.OrgCompanyMapper;
import com.erp.module.org.mapper.OrgDepartmentMapper;
import com.erp.module.org.mapper.OrgPositionMapper;
import com.erp.module.org.service.OrgDepartmentService;
import com.erp.module.org.vo.DeptDetailVO;
import com.erp.module.org.vo.DeptListVO;
import com.erp.module.org.vo.DeptTreeVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrgDepartmentServiceImpl extends ServiceImplX<OrgDepartmentMapper, OrgDepartment>
        implements OrgDepartmentService {

    @Resource
    private OrgCompanyMapper companyMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private OrgPositionMapper positionMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResult<DeptListVO> page(DeptQueryDTO query) {
        LambdaQueryWrapper<OrgDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getCompanyId() != null, OrgDepartment::getCompanyId, query.getCompanyId());
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(OrgDepartment::getDepartmentName, query.getKeyword())
                    .or()
                    .like(OrgDepartment::getDeptCode, query.getKeyword()));
        }
        wrapper.eq(query.getEnableFlag() != null, OrgDepartment::getEnableFlag, query.getEnableFlag());
        wrapper.orderByAsc(OrgDepartment::getSortNo);
        wrapper.orderByDesc(OrgDepartment::getCreateTime);

        IPage<OrgDepartment> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toListVO));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeptTreeVO> tree(Long companyId) {
        LambdaQueryWrapper<OrgDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgDepartment::getCompanyId, companyId);
        wrapper.orderByAsc(OrgDepartment::getSortNo);
        List<OrgDepartment> list = list(wrapper);

        List<DeptTreeVO> voList = list.stream().map(this::toTreeVO).collect(Collectors.toList());
        Map<Long, DeptTreeVO> map = voList.stream()
                .collect(Collectors.toMap(DeptTreeVO::getId, v -> v));

        List<DeptTreeVO> roots = new ArrayList<>();
        for (DeptTreeVO vo : voList) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                roots.add(vo);
            } else {
                DeptTreeVO parent = map.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }
        return roots;
    }

    @Override
    @Transactional(readOnly = true)
    public DeptDetailVO getById(Long id) {
        OrgDepartment entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "部门不存在");
        }
        return toDetailVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "新增", description = "新增部门")
    public Long create(DeptCreateDTO dto) {
        validateCompanyExists(dto.getCompanyId());
        validateDeptNameUniqueness(dto.getDeptName(), dto.getCompanyId(), dto.getParentId(), null);

        OrgDepartment entity = new OrgDepartment();
        BeanUtils.copyProperties(dto, entity);
        entity.setDepartmentName(dto.getDeptName());
        entity.setDeptCode(generateDeptCode());
        entity.setEnableFlag(true);
        if (dto.getManagerId() != null) {
            EmployeeEntity emp = employeeMapper.selectById(dto.getManagerId());
            if (emp != null) {
                entity.setManagerName(emp.getName());
            }
        }
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "修改", description = "修改部门")
    public void update(Long id, DeptUpdateDTO dto) {
        OrgDepartment entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "部门不存在");
        }
        validateDeptNameUniqueness(dto.getDeptName(), entity.getCompanyId(), dto.getParentId(), id);
        checkCircularReference(id, dto.getParentId());

        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        entity.setDepartmentName(dto.getDeptName());
        if (dto.getManagerId() != null) {
            EmployeeEntity emp = employeeMapper.selectById(dto.getManagerId());
            if (emp != null) {
                entity.setManagerName(emp.getName());
            }
        }
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "删除", description = "删除部门")
    public void delete(Long id) {
        OrgDepartment entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "部门不存在");
        }

        LambdaQueryWrapper<OrgDepartment> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.eq(OrgDepartment::getParentId, id);
        if (count(deptWrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该部门下存在子部门，无法删除");
        }

        LambdaQueryWrapper<OrgPosition> posWrapper = new LambdaQueryWrapper<>();
        posWrapper.eq(OrgPosition::getDepartmentId, id);
        if (positionMapper.selectCount(posWrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该部门下存在岗位，无法删除");
        }

        LambdaQueryWrapper<EmployeeEntity> empWrapper = new LambdaQueryWrapper<>();
        empWrapper.eq(EmployeeEntity::getDepartmentId, id);
        if (employeeMapper.selectCount(empWrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该部门下存在员工，无法删除");
        }

        removeById(id);
    }

    private void validateCompanyExists(Long companyId) {
        if (companyId == null || companyMapper.selectById(companyId) == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "所属公司不存在");
        }
    }

    private void validateDeptNameUniqueness(String deptName, Long companyId, Long parentId, Long excludeId) {
        if (!StringUtils.hasText(deptName)) {
            return;
        }
        LambdaQueryWrapper<OrgDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgDepartment::getDepartmentName, deptName);
        wrapper.eq(OrgDepartment::getCompanyId, companyId);
        if (parentId != null) {
            wrapper.eq(OrgDepartment::getParentId, parentId);
        }
        if (excludeId != null) {
            wrapper.ne(OrgDepartment::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "同一上级下部门名称已存在");
        }
    }

    private void checkCircularReference(Long deptId, Long parentId) {
        if (parentId == null || parentId == 0) {
            return;
        }
        if (parentId.equals(deptId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "不能将部门设为自身的子部门");
        }
        OrgDepartment parent = super.getById(parentId);
        if (parent != null && parent.getParentId() != null && parent.getParentId() != 0) {
            checkCircularReference(deptId, parent.getParentId());
        }
    }

    private String generateDeptCode() {
        return "DEPT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private DeptListVO toListVO(OrgDepartment entity) {
        DeptListVO vo = new DeptListVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setDeptName(entity.getDepartmentName());
        return vo;
    }

    private DeptTreeVO toTreeVO(OrgDepartment entity) {
        DeptTreeVO vo = new DeptTreeVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setDeptName(entity.getDepartmentName());
        return vo;
    }

    private DeptDetailVO toDetailVO(OrgDepartment entity) {
        DeptDetailVO vo = new DeptDetailVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setDeptName(entity.getDepartmentName());
        return vo;
    }
}
