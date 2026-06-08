package com.erp.engine.audit.controller;

import com.erp.common.result.RT;
import com.erp.engine.audit.dto.AuditApproveDTO;
import com.erp.engine.audit.dto.AuditSubmitDTO;
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
@Tag(name = "审核引擎", description = "通用审核提交与通过接口")
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
}
