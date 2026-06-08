package com.erp.engine.audit.controller;

import com.erp.common.result.RT;
import com.erp.engine.audit.dto.SysAuditConfigUpdateDTO;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.service.AuditConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审核配置管理Controller.
 *
 * @author AI
 */
@Tag(name = "审核配置", description = "审核配置查询与更新接口")
@RestController
@RequestMapping("/api/engine/audit/config")
@RequiredArgsConstructor
public class AuditConfigController {

    private final AuditConfigService auditConfigService;

    @Operation(summary = "更新审核配置", description = "更新单据类型的审核配置，含互斥校验与缓存刷新")
    @PutMapping
    public RT<Void> updateConfig(@Valid @RequestBody SysAuditConfigUpdateDTO dto) {
        auditConfigService.updateConfig(dto);
        return RT.ok();
    }

    @Operation(summary = "查询审核配置", description = "根据单据类型获取审核配置")
    @GetMapping("/{docType}")
    public RT<SysAuditConfigEntity> getConfig(@PathVariable String docType) {
        return RT.ok(auditConfigService.getConfig(docType));
    }
}
