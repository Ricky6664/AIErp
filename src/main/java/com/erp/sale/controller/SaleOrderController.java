package com.erp.sale.controller;

import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.sale.dto.SaleQuotationCreateDTO;
import com.erp.sale.dto.SaleQuotationQueryDTO;
import com.erp.sale.dto.SaleQuotationUpdateDTO;
import com.erp.sale.service.ISaleOrderService;
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
 * 销售订单Controller.
 *
 * @author AI
 * @since 2026-06-09
 */
@Slf4j
@RestController
@RequestMapping("/api/sale/order")
@RequiredArgsConstructor
@Tag(name = "SaleOrder管理", description = "SaleOrder相关接口")
public class SaleOrderController {

    private final ISaleOrderService saleService;

    @Operation(summary = "分页查询列表", description = "支持多条件筛选+分页排序")
    @GetMapping
    public RT<PageResult<SaleQuotationListVO>> page(@Valid SaleQuotationQueryDTO query) {
        return RT.ok(PageResult.of(saleService.pageList(query)));
    }

    @Operation(summary = "根据ID查询详情", description = "包含主表信息和明细行列表")
    @GetMapping("/{id}")
    public RT<SaleQuotationDetailVO> getById(
            @Parameter(description = "销售订单ID") @PathVariable Long id) {
        return RT.ok(saleService.getDetail(id));
    }

    @Operation(summary = "新增", description = "创建销售订单，含明细行")
    @PostMapping
    public RT<Long> create(
            @Parameter(description = "销售订单创建参数") @Valid @RequestBody SaleQuotationCreateDTO dto) {
        return RT.ok(saleService.create(dto));
    }

    @Operation(summary = "修改", description = "更新销售订单，自动校验版本号")
    @PutMapping
    public RT<Boolean> update(
            @Parameter(description = "销售订单更新参数") @Valid @RequestBody SaleQuotationUpdateDTO dto) {
        return RT.ok(saleService.update(dto));
    }

    @Operation(summary = "删除", description = "根据ID逻辑删除销售订单")
    @DeleteMapping("/{id}")
    public RT<Boolean> delete(
            @Parameter(description = "销售订单ID") @PathVariable Long id) {
        return RT.ok(saleService.delete(id));
    }

    @Operation(summary = "提交审核", description = "将销售订单提交至审批流程")
    @PostMapping("/{id}/submit-audit")
    public RT<Boolean> submitAudit(
            @Parameter(description = "销售订单ID") @PathVariable Long id,
            @Parameter(description = "审核意见") @RequestParam String opinion) {
        return RT.ok(saleService.submitAudit(id, opinion));
    }
}
