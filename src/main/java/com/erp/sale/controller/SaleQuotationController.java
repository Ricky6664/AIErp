package com.erp.sale.controller;

import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.sale.dto.SaleQuotationCreateDTO;
import com.erp.sale.dto.SaleQuotationQueryDTO;
import com.erp.sale.dto.SaleQuotationUpdateDTO;
import com.erp.sale.service.ISaleQuotationService;
import com.erp.sale.vo.SaleQuotationDetailVO;
import com.erp.sale.vo.SaleQuotationListVO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报价单Controller.
 *
 * @author AI
 * @since 2026-06-09
 */
@Slf4j
@RestController
@RequestMapping("/api/sale/quotation")
@RequiredArgsConstructor
@Tag(name = "SaleQuotation管理")
public class SaleQuotationController {

    private final ISaleQuotationService saleService;

    @Operation(summary = "分页查询报价单列表")
    @GetMapping
    public RT<PageResult<SaleQuotationListVO>> page(@Valid SaleQuotationQueryDTO query) {
        return RT.ok(PageResult.of(saleService.pageList(query)));
    }

    @Operation(summary = "根据ID查询报价单详情")
    @GetMapping("/{id}")
    public RT<SaleQuotationDetailVO> getById(
            @Parameter(description = "报价单ID") @PathVariable Long id) {
        return RT.ok(saleService.getDetail(id));
    }

    @Operation(summary = "新增报价单")
    @PostMapping
    public RT<Long> create(
            @Parameter(description = "报价单创建参数") @Valid @RequestBody SaleQuotationCreateDTO dto) {
        return RT.ok(saleService.create(dto));
    }

    @Operation(summary = "修改报价单")
    @PutMapping("/{id}")
    public RT<Boolean> update(
            @Parameter(description = "报价单ID") @PathVariable Long id,
            @Parameter(description = "报价单更新参数") @Valid @RequestBody SaleQuotationUpdateDTO dto) {
        dto.setId(id);
        return RT.ok(saleService.update(dto));
    }

    @Operation(summary = "删除报价单")
    @DeleteMapping("/{id}")
    public RT<Void> delete(
            @Parameter(description = "报价单ID") @PathVariable Long id) {
        saleService.delete(id);
        return RT.ok();
    }

    @Operation(summary = "提交审核")
    @PostMapping("/{id}/submit-audit")
    public RT<Boolean> submitAudit(
            @Parameter(description = "报价单ID") @PathVariable Long id,
            @Parameter(description = "审核意见") @RequestParam String opinion) {
        return RT.ok(saleService.submitAudit(id, opinion));
    }
}
