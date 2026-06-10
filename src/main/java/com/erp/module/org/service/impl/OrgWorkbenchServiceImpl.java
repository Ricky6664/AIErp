package com.erp.module.org.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.hrm.entity.EmployeeEntity;
import com.erp.hrm.mapper.EmployeeMapper;
import com.erp.module.org.entity.OrgCompany;
import com.erp.module.org.entity.OrgDepartment;
import com.erp.module.org.entity.OrgPosition;
import com.erp.module.org.mapper.OrgCompanyMapper;
import com.erp.module.org.mapper.OrgDepartmentMapper;
import com.erp.module.org.mapper.OrgPositionMapper;
import com.erp.module.org.service.OrgWorkbenchService;
import com.erp.module.org.vo.WorkbenchVO;
import com.erp.module.org.vo.WorkbenchVO.CompanyDeptCount;
import com.erp.module.org.vo.WorkbenchVO.DeptStaffCount;
import com.erp.module.org.vo.WorkbenchVO.TypeDistribution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织架构工作台聚合数据Service实现.
 *
 * @author AI
 * @since 2026-06-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrgWorkbenchServiceImpl implements OrgWorkbenchService {

    private final OrgCompanyMapper companyMapper;
    private final OrgDepartmentMapper departmentMapper;
    private final OrgPositionMapper positionMapper;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "orgWorkbench", key = "'org:workbench:' + #root.target.getTenantId()",
            unless = "#result == null")
    public WorkbenchVO getWorkbenchData() {
        WorkbenchVO vo = new WorkbenchVO();

        vo.setCompanyCount(countEnabled(OrgCompany.class));
        vo.setDepartmentCount(countEnabled(OrgDepartment.class));
        vo.setPositionCount(countEnabled(OrgPosition.class));

        vo.setEmployeeCount(countActiveEmployees());
        vo.setDeptTypeDistribution(getDeptTypeDistribution());
        vo.setCompanyDeptCount(getCompanyDeptCount());
        vo.setDeptStaffDistribution(getDeptStaffDistribution());

        return vo;
    }

    public Long getTenantId() {
        try {
            Object tid = StpUtil.getSession().get("tenantId");
            if (tid != null) {
                return Long.parseLong(tid.toString());
            }
        } catch (Exception e) {
            log.debug("获取租户ID失败: {}", e.getMessage());
        }
        return 0L;
    }

    private Long countEnabled(Class<?> entityClass) {
        if (entityClass == OrgCompany.class) {
            return companyMapper.selectCount(
                    new LambdaQueryWrapper<OrgCompany>()
                            .eq(OrgCompany::getEnableFlag, true));
        } else if (entityClass == OrgDepartment.class) {
            return departmentMapper.selectCount(
                    new LambdaQueryWrapper<OrgDepartment>()
                            .eq(OrgDepartment::getEnableFlag, true));
        } else if (entityClass == OrgPosition.class) {
            return positionMapper.selectCount(
                    new LambdaQueryWrapper<OrgPosition>()
                            .eq(OrgPosition::getEnableFlag, true));
        }
        return 0L;
    }

    private Long countActiveEmployees() {
        try {
            return employeeMapper.selectCount(
                    new LambdaQueryWrapper<EmployeeEntity>()
                            .eq(EmployeeEntity::getEmployeeStatus, "active"));
        } catch (Exception e) {
            log.warn("查询在职员工总数失败(hrm_employee表可能未就绪): {}", e.getMessage());
            return 0L;
        }
    }

    private List<TypeDistribution> getDeptTypeDistribution() {
        try {
            List<OrgDepartment> depts = departmentMapper.selectList(
                    new LambdaQueryWrapper<OrgDepartment>()
                            .eq(OrgDepartment::getEnableFlag, true));
            Map<String, Long> grouped = depts.stream()
                    .filter(d -> d.getDeptType() != null)
                    .collect(Collectors.groupingBy(OrgDepartment::getDeptType, Collectors.counting()));
            return grouped.entrySet().stream()
                    .map(e -> {
                        TypeDistribution item = new TypeDistribution();
                        item.setDeptType(e.getKey());
                        item.setCount(e.getValue());
                        return item;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("查询部门类型分布失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<CompanyDeptCount> getCompanyDeptCount() {
        List<OrgCompany> companies = companyMapper.selectList(
                new LambdaQueryWrapper<OrgCompany>()
                        .eq(OrgCompany::getEnableFlag, true));
        List<OrgDepartment> depts = departmentMapper.selectList(
                new LambdaQueryWrapper<OrgDepartment>()
                        .eq(OrgDepartment::getEnableFlag, true));
        Map<Long, Long> deptCountByCompany = depts.stream()
                .filter(d -> d.getCompanyId() != null)
                .collect(Collectors.groupingBy(OrgDepartment::getCompanyId, Collectors.counting()));

        List<CompanyDeptCount> result = new ArrayList<>();
        for (OrgCompany company : companies) {
            CompanyDeptCount item = new CompanyDeptCount();
            item.setCompanyId(company.getId());
            item.setCompanyName(company.getCompanyName());
            item.setDeptCount(deptCountByCompany.getOrDefault(company.getId(), 0L));
            result.add(item);
        }
        return result;
    }

    private List<DeptStaffCount> getDeptStaffDistribution() {
        try {
            List<EmployeeEntity> activeEmployees = employeeMapper.selectList(
                    new LambdaQueryWrapper<EmployeeEntity>()
                            .eq(EmployeeEntity::getEmployeeStatus, "active"));
            Map<Long, Long> countByDept = activeEmployees.stream()
                    .filter(e -> e.getDepartmentId() != null)
                    .collect(Collectors.groupingBy(EmployeeEntity::getDepartmentId, Collectors.counting()));
            long total = countByDept.values().stream().mapToLong(Long::longValue).sum();

            List<OrgDepartment> depts = departmentMapper.selectList(
                    new LambdaQueryWrapper<OrgDepartment>()
                            .eq(OrgDepartment::getEnableFlag, true));
            Map<Long, String> deptNameMap = depts.stream()
                    .collect(Collectors.toMap(OrgDepartment::getId, OrgDepartment::getDepartmentName, (a, b) -> a));

            List<DeptStaffCount> result = new ArrayList<>();
            countByDept.forEach((deptId, count) -> {
                DeptStaffCount item = new DeptStaffCount();
                item.setDeptId(deptId);
                item.setDeptName(deptNameMap.getOrDefault(deptId, "未知部门"));
                item.setStaffCount(count);
                if (total > 0) {
                    BigDecimal pct = BigDecimal.valueOf(count * 100.0 / total)
                            .setScale(1, RoundingMode.HALF_UP);
                    item.setPercentage(pct.toString());
                } else {
                    item.setPercentage("0.0");
                }
                result.add(item);
            });
            return result;
        } catch (Exception e) {
            log.warn("查询各部门人员分布失败(hrm_employee表可能未就绪): {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
