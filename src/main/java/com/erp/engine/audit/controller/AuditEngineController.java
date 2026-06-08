package com.erp.engine.audit.controller;

import com.erp.common.result.RT;
import com.erp.engine.audit.dto.AuditApproveDTO;
import com.erp.engine.audit.dto.AuditOperationDTO;
import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.dto.AuditVoidDTO;
import com.erp.engine.audit.service.AuditEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审核引擎Controller.
 *
 * @author AI
 */
@Tag(name = "审核引擎", description = "通用审核提交、通过、作废与反审接口")
@RestController
@RequestMapping("/api/engine/audit")
@RequiredArgsConstructor
public class AuditEngineController {

    private final AuditEngineService auditEngineService;

    @Operation(summary = "提交审核", description = "将草稿状态单据提交审核，状态从0流转至1")
    @PostMapping("/submit")
    public RT<Void> submit(@Valid @RequestBody AuditSubmitDTO dto) {
        auditEngineService.submit(dto);
        return RT.ok();
    }

    @Operation(summary = "审核通过", description = "将已提交单据审核通过，状态从1流转至2")
    @PostMapping("/approve")
    public RT<Void> approve(@Valid @RequestBody AuditApproveDTO dto) {
        auditEngineService.approve(dto);
        return RT.ok();
    }

    @Operation(summary = "作废单据", description = "将草稿/已提交/已审核状态单据作废，状态流转至4，发布资源释放事件")
    @PostMapping("/void")
    public RT<Void> voidDocument(@Valid @RequestBody AuditVoidDTO dto) {
        auditEngineService.voidDocument(dto);
        return RT.ok();
    }

    @Operation(summary = "反审", description = "将已审核通过单据反审，状态从2回退至0")
    @PostMapping("/unconfirm")
    public RT<Void> unconfirm(@Valid @RequestBody AuditOperationDTO dto) {
        auditEngineService.unconfirm(dto);
        return RT.ok();
    }
}
