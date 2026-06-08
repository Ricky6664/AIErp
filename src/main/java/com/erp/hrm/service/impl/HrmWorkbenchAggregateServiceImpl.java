package com.erp.hrm.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.hrm.entity.AttendanceEntity;
import com.erp.hrm.entity.EmployeeEntity;
import com.erp.hrm.entity.RecruitmentEntity;
import com.erp.hrm.entity.SalaryEntity;
import com.erp.hrm.mapper.AttendanceMapper;
import com.erp.hrm.mapper.EmployeeMapper;
import com.erp.hrm.mapper.RecruitmentMapper;
import com.erp.hrm.mapper.SalaryMapper;
import com.erp.hrm.vo.HrmWorkbenchAggregateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HRM工作台聚合数据Service实现.
 *
 * @author AI
 * @since 2026-06-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrmWorkbenchAggregateServiceImpl {

    private final EmployeeMapper employeeMapper;
    private final AttendanceMapper attendanceMapper;
    private final RecruitmentMapper recruitmentMapper;
    private final SalaryMapper salaryMapper;

    @Cacheable(value = "workbench", key = "'hrm:' + #root.target.getCurrentTenantId()",
            unless = "#result == null")
    public HrmWorkbenchAggregateVO getWorkbenchData() {
        Long tenantId = getCurrentTenantId();
        HrmWorkbenchAggregateVO vo = new HrmWorkbenchAggregateVO();

        vo.setTotalEmployees(countEmployees(tenantId, null));
        vo.setActiveEmployees(countEmployees(tenantId, "在职"));
        vo.setNewHiresThisMonth(countNewHiresThisMonth(tenantId));
        vo.setOpenRecruitments(countRecruitmentsByStatus(tenantId, "招聘中"));
        vo.setTotalMonthlySalary(sumMonthlySalary(tenantId));
        vo.setDepartmentCount(countDistinctDepartments(tenantId));
        vo.setEmployeeMonthlyTrend(getEmployeeMonthlyTrend(tenantId));
        vo.setDepartmentDistribution(getDepartmentDistribution(tenantId));
        vo.setRecruitmentStatusDistribution(getRecruitmentStatusDistribution(tenantId));
        vo.setAttendanceMonthlyTrend(getAttendanceMonthlyTrend(tenantId));

        return vo;
    }

    public Long getCurrentTenantId() {
        try {
            Object tenantId = StpUtil.getSession().get("tenantId");
            if (tenantId != null) {
                return Long.parseLong(tenantId.toString());
            }
        } catch (Exception e) {
            log.debug("获取租户ID失败: {}", e.getMessage());
        }
        return 0L;
    }

    private Long countEmployees(Long tenantId, String status) {
        LambdaQueryWrapper<EmployeeEntity> wrapper = new LambdaQueryWrapper<EmployeeEntity>()
                .eq(EmployeeEntity::getTenantId, tenantId);
        if (status != null) {
            wrapper.eq(EmployeeEntity::getEmployeeStatus, status);
        }
        return employeeMapper.selectCount(wrapper);
    }

    private Long countNewHiresThisMonth(Long tenantId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return employeeMapper.selectCount(
                new LambdaQueryWrapper<EmployeeEntity>()
                        .eq(EmployeeEntity::getTenantId, tenantId)
                        .ge(EmployeeEntity::getCreateTime, monthStart)
                        .le(EmployeeEntity::getCreateTime, now));
    }

    private Long countRecruitmentsByStatus(Long tenantId, String status) {
        return recruitmentMapper.selectCount(
                new LambdaQueryWrapper<RecruitmentEntity>()
                        .eq(RecruitmentEntity::getTenantId, tenantId)
                        .eq(RecruitmentEntity::getRecruitStatus, status));
    }

    private BigDecimal sumMonthlySalary(Long tenantId) {
        LocalDateTime now = LocalDateTime.now();
        String salaryMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<SalaryEntity> salaries = salaryMapper.selectList(
                new LambdaQueryWrapper<SalaryEntity>()
                        .eq(SalaryEntity::getTenantId, tenantId)
                        .eq(SalaryEntity::getSalaryMonth, salaryMonth));
        return salaries.stream()
                .map(SalaryEntity::getNetSalary)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long countDistinctDepartments(Long tenantId) {
        List<EmployeeEntity> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<EmployeeEntity>()
                        .eq(EmployeeEntity::getTenantId, tenantId));
        return employees.stream()
                .map(EmployeeEntity::getDepartmentId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();
    }

    private List<HrmWorkbenchAggregateVO.TrendItem> getEmployeeMonthlyTrend(Long tenantId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");

        List<EmployeeEntity> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<EmployeeEntity>()
                        .eq(EmployeeEntity::getTenantId, tenantId)
                        .ge(EmployeeEntity::getCreateTime, sixMonthsAgo));

        Map<String, Long> monthCounts = new LinkedHashMap<>();
        LocalDateTime cursor = sixMonthsAgo;
        for (int i = 0; i < 6; i++) {
            monthCounts.put(cursor.format(monthFmt), 0L);
            cursor = cursor.plusMonths(1);
        }

        for (EmployeeEntity emp : employees) {
            if (emp.getCreateTime() != null) {
                String month = emp.getCreateTime().format(monthFmt);
                monthCounts.merge(month, 1L, Long::sum);
            }
        }

        List<HrmWorkbenchAggregateVO.TrendItem> trend = new ArrayList<>();
        monthCounts.forEach((month, count) -> {
            HrmWorkbenchAggregateVO.TrendItem item = new HrmWorkbenchAggregateVO.TrendItem();
            item.setMonth(month);
            item.setCount(count);
            trend.add(item);
        });
        return trend;
    }

    private Map<String, Long> getDepartmentDistribution(Long tenantId) {
        List<EmployeeEntity> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<EmployeeEntity>()
                        .eq(EmployeeEntity::getTenantId, tenantId));
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (EmployeeEntity emp : employees) {
            if (emp.getDepartmentId() != null) {
                String deptKey = "部门-" + emp.getDepartmentId();
                distribution.merge(deptKey, 1L, Long::sum);
            }
        }
        return distribution;
    }

    private Map<String, Long> getRecruitmentStatusDistribution(Long tenantId) {
        List<RecruitmentEntity> recruitments = recruitmentMapper.selectList(
                new LambdaQueryWrapper<RecruitmentEntity>()
                        .eq(RecruitmentEntity::getTenantId, tenantId));
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (RecruitmentEntity rec : recruitments) {
            String status = rec.getRecruitStatus() != null ? rec.getRecruitStatus() : "未知";
            distribution.merge(status, 1L, Long::sum);
        }
        return distribution;
    }

    private List<HrmWorkbenchAggregateVO.TrendItem> getAttendanceMonthlyTrend(Long tenantId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");

        List<AttendanceEntity> attendances = attendanceMapper.selectList(
                new LambdaQueryWrapper<AttendanceEntity>()
                        .eq(AttendanceEntity::getTenantId, tenantId)
                        .ge(AttendanceEntity::getCreateTime, sixMonthsAgo));

        Map<String, Long> monthCounts = new LinkedHashMap<>();
        LocalDateTime cursor = sixMonthsAgo;
        for (int i = 0; i < 6; i++) {
            monthCounts.put(cursor.format(monthFmt), 0L);
            cursor = cursor.plusMonths(1);
        }

        for (AttendanceEntity att : attendances) {
            if (att.getCreateTime() != null) {
                String month = att.getCreateTime().format(monthFmt);
                monthCounts.merge(month, 1L, Long::sum);
            }
        }

        List<HrmWorkbenchAggregateVO.TrendItem> trend = new ArrayList<>();
        monthCounts.forEach((month, count) -> {
            HrmWorkbenchAggregateVO.TrendItem item = new HrmWorkbenchAggregateVO.TrendItem();
            item.setMonth(month);
            item.setCount(count);
            trend.add(item);
        });
        return trend;
    }
}
