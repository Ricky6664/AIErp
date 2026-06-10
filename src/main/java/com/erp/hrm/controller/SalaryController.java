package com.erp.hrm.controller;

import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.hrm.dto.SalaryCreateDTO;
import com.erp.hrm.dto.SalaryQueryDTO;
import com.erp.hrm.dto.SalaryUpdateDTO;
import com.erp.hrm.service.ISalaryService;
import com.erp.hrm.vo.SalaryVO;
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
 * 薪资管理Controller.
 *
 * @author AI
 * @since 2026-06-09
 */
@Slf4j
@RestController
@RequestMapping("/api/hrm")
@RequiredArgsConstructor
@Tag(name = "薪资管理", description = "薪资管理CRUD接口")
public class SalaryController {

    private final ISalaryService salaryService;

    @Operation(summary = "分页查询薪资记录")
    @GetMapping("/salary")
    public RT<PageResult<SalaryVO>> pageList(@Valid SalaryQueryDTO query) {
        return RT.ok(salaryService.pageList(query));
    }

    @Operation(summary = "根据ID查询薪资详情")
    @GetMapping("/salary/{id}")
    public RT<SalaryVO> getById(
            @Parameter(description = "薪资ID") @PathVariable Long id) {
        return RT.ok(salaryService.getById(id));
    }

    @Operation(summary = "新增薪资记录")
    @PostMapping("/salary")
    public RT<SalaryVO> create(
            @Parameter(description = "薪资创建参数") @Valid @RequestBody SalaryCreateDTO dto) {
        return RT.ok(salaryService.create(dto));
    }

    @Operation(summary = "更新薪资记录")
    @PutMapping("/salary/{id}")
    public RT<SalaryVO> update(
            @Parameter(description = "薪资ID") @PathVariable Long id,
            @Parameter(description = "薪资更新参数") @Valid @RequestBody SalaryUpdateDTO dto) {
        return RT.ok(salaryService.update(id, dto));
    }

    @Operation(summary = "删除薪资记录")
    @DeleteMapping("/salary/{id}")
    public RT<Void> delete(
            @Parameter(description = "薪资ID") @PathVariable Long id) {
        salaryService.delete(id);
        return RT.ok();
    }
}
