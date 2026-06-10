package com.erp.hrm.controller;

import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.hrm.dto.EmployeeCreateDTO;
import com.erp.hrm.dto.EmployeeQueryDTO;
import com.erp.hrm.dto.EmployeeUpdateDTO;
import com.erp.hrm.service.IEmployeeService;
import com.erp.hrm.vo.EmployeeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工档案Controller.
 *
 * @author AI
 * @since 2026-06-08
 */
@Slf4j
@RestController
@RequestMapping("/api/hrm")
@RequiredArgsConstructor
@Tag(name = "员工档案管理", description = "员工档案CRUD接口")
public class EmployeeController {

    private final IEmployeeService employeeService;

    @Operation(summary = "分页查询员工档案列表")
    @GetMapping("/employee")
    public RT<PageResult<EmployeeVO>> pageList(@Valid EmployeeQueryDTO query) {
        return RT.ok(employeeService.pageList(query));
    }

    @Operation(summary = "根据ID查询员工档案")
    @GetMapping("/employee/{id}")
    public RT<EmployeeVO> getById(
            @Parameter(description = "员工ID") @PathVariable Long id) {
        return RT.ok(employeeService.getById(id));
    }

    @Operation(summary = "新增员工档案")
    @PostMapping("/employee")
    public RT<EmployeeVO> create(
            @Parameter(description = "员工创建参数") @Valid @RequestBody EmployeeCreateDTO dto) {
        return RT.ok(employeeService.create(dto));
    }

    @Operation(summary = "更新员工档案")
    @PutMapping("/employee/{id}")
    public RT<EmployeeVO> update(
            @Parameter(description = "员工ID") @PathVariable Long id,
            @Parameter(description = "员工更新参数") @Valid @RequestBody EmployeeUpdateDTO dto) {
        return RT.ok(employeeService.update(id, dto));
    }

    @Operation(summary = "删除员工档案")
    @DeleteMapping("/employee/{id}")
    public RT<Void> delete(
            @Parameter(description = "员工ID") @PathVariable Long id) {
        employeeService.delete(id);
        return RT.ok();
    }
}
