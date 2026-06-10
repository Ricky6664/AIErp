package com.erp.finance.controller;

import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.finance.dto.VoucherWordCreateDTO;
import com.erp.finance.dto.VoucherWordQueryDTO;
import com.erp.finance.dto.VoucherWordUpdateDTO;
import com.erp.finance.service.IVoucherWordService;
import com.erp.finance.vo.VoucherWordVO;
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

import java.util.Map;

/**
 * 凭证字管理Controller.
 *
 * @author AI
 * @since 2026-06-08
 */
@Slf4j
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Tag(name = "凭证字管理", description = "凭证字CRUD接口")
public class VoucherWordController {

    private final IVoucherWordService voucherWordService;

    @Operation(summary = "分页查询凭证字列表")
    @GetMapping("/voucher-word")
    public RT<PageResult<VoucherWordVO>> pageList(@Valid VoucherWordQueryDTO query) {
        return RT.ok(voucherWordService.pageList(query));
    }

    @Operation(summary = "根据ID查询凭证字")
    @GetMapping("/voucher-word/{id}")
    public RT<VoucherWordVO> getById(
            @Parameter(description = "凭证字ID") @PathVariable Long id) {
        return RT.ok(voucherWordService.getById(id));
    }

    @Operation(summary = "新增凭证字")
    @PostMapping("/voucher-word")
    public RT<VoucherWordVO> create(
            @Parameter(description = "凭证字创建参数") @Valid @RequestBody VoucherWordCreateDTO dto) {
        return RT.ok(voucherWordService.create(dto));
    }

    @Operation(summary = "修改凭证字")
    @PutMapping("/voucher-word/{id}")
    public RT<VoucherWordVO> update(
            @Parameter(description = "凭证字ID") @PathVariable Long id,
            @Parameter(description = "凭证字更新参数") @Valid @RequestBody VoucherWordUpdateDTO dto) {
        return RT.ok(voucherWordService.update(id, dto));
    }

    @Operation(summary = "切换凭证字启用状态")
    @PutMapping("/voucher-word/{id}/status")
    public RT<Void> updateStatus(
            @Parameter(description = "凭证字ID") @PathVariable Long id,
            @Parameter(description = "状态值(1启用/0停用)") @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        voucherWordService.updateStatus(id, status);
        return RT.ok();
    }

    @Operation(summary = "删除凭证字")
    @DeleteMapping("/voucher-word/{id}")
    public RT<Void> delete(
            @Parameter(description = "凭证字ID") @PathVariable Long id) {
        voucherWordService.delete(id);
        return RT.ok();
    }
}
