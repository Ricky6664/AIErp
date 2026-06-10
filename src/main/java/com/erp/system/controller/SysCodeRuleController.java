package com.erp.system.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.dto.SysCodeRuleDTO;
import com.erp.system.service.SysCodeRuleService;
import com.erp.system.vo.SysCodeRuleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "编码规则管理", description = "编码规则CRUD、预览、生成接口")
@RestController
@RequestMapping("/api/system/code-rules")
@RequiredArgsConstructor
public class SysCodeRuleController {

    private final SysCodeRuleService sysCodeRuleService;

    @Operation(summary = "新增编码规则")
    @RequirePermission("system:code-rule:create")
    @PostMapping
    public RT<SysCodeRuleVO.DetailVO> create(@Valid @RequestBody SysCodeRuleDTO.CreateDTO dto) {
        return RT.ok(sysCodeRuleService.create(dto));
    }

    @Operation(summary = "修改编码规则")
    @RequirePermission("system:code-rule:update")
    @PutMapping("/{id}")
    public RT<SysCodeRuleVO.DetailVO> update(@PathVariable Long id, @Valid @RequestBody SysCodeRuleDTO.UpdateDTO dto) {
        return RT.ok(sysCodeRuleService.update(id, dto));
    }

    @Operation(summary = "删除编码规则")
    @RequirePermission("system:code-rule:delete")
    @DeleteMapping("/{id}")
    public RT<Boolean> delete(@PathVariable Long id) {
        return RT.ok(sysCodeRuleService.delete(id));
    }

    @Operation(summary = "查询编码规则详情")
    @RequirePermission("system:code-rule:query")
    @GetMapping("/{id}")
    public RT<SysCodeRuleVO.DetailVO> getById(@PathVariable Long id) {
        SysCodeRuleVO.DetailVO vo = sysCodeRuleService.getById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(vo);
    }

    @Operation(summary = "分页查询编码规则列表")
    @RequirePermission("system:code-rule:query")
    @GetMapping("/page")
    public RT<PageResult<SysCodeRuleVO.DetailVO>> pageList(@Valid PageQuery query) {
        return RT.ok(sysCodeRuleService.pageList(query));
    }

    @Operation(summary = "预览编码")
    @RequirePermission("system:code-rule:preview")
    @PostMapping("/{id}/preview")
    public RT<String> preview(@PathVariable Long id) {
        return RT.ok(sysCodeRuleService.preview(id));
    }

    @Operation(summary = "生成编码")
    @RequirePermission("system:code-rule:generate")
    @PostMapping("/{id}/generate")
    public RT<String> generate(@PathVariable Long id) {
        SysCodeRuleVO.DetailVO vo = sysCodeRuleService.getById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(sysCodeRuleService.generate(vo.getRuleCode()));
    }
}
