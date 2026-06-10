package com.erp.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.AuthSsoConfig;
import com.erp.auth.service.AuthSsoConfigService;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * SSO配置控制器.
 *
 * @author AI
 * @since 2026-06-05
 */
@Slf4j
@RestController
@RequestMapping("/api/system/sso-config")
@RequiredArgsConstructor
@Tag(name = "SSO配置管理", description = "SSO单点登录配置CRUD接口")
public class SsoConfigController {

    private final AuthSsoConfigService authSsoConfigService;

    @Operation(summary = "分页查询SSO配置列表")
    @RequirePermission("system:sso-config:query")
    @GetMapping("/page")
    public RT<PageResult<AuthSsoConfig>> page(@Parameter(description = "分页参数") PageQuery query) {
        LambdaQueryWrapper<AuthSsoConfig> wrapper = new LambdaQueryWrapper<AuthSsoConfig>()
                .orderByDesc(AuthSsoConfig::getCreateTime);
        return RT.ok(authSsoConfigService.pageList(query, wrapper));
    }

    @Operation(summary = "查询SSO配置详情")
    @RequirePermission("system:sso-config:query")
    @GetMapping("/{id}")
    public RT<AuthSsoConfig> get(@Parameter(description = "SSO配置ID") @PathVariable Long id) {
        AuthSsoConfig entity = authSsoConfigService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(entity);
    }

    @Operation(summary = "新增SSO配置")
    @RequirePermission("system:sso-config:create")
    @PostMapping
    public RT<AuthSsoConfig> create(@Valid @RequestBody AuthSsoConfig entity, BindingResult bindingResult) {
        validateCertificate(entity.getCertificate());
        authSsoConfigService.save(entity);
        log.info("SSO配置已新增: id={}, ssoName={}", entity.getId(), entity.getSsoName());
        return RT.ok(entity);
    }

    @Operation(summary = "修改SSO配置")
    @RequirePermission("system:sso-config:update")
    @PutMapping("/{id}")
    public RT<AuthSsoConfig> update(@Parameter(description = "SSO配置ID") @PathVariable Long id,
                                    @Valid @RequestBody AuthSsoConfig entity, BindingResult bindingResult) {
        AuthSsoConfig existing = authSsoConfigService.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        validateCertificate(entity.getCertificate());
        entity.setId(id);
        authSsoConfigService.updateById(entity);
        log.info("SSO配置已修改: id={}, ssoName={}", id, entity.getSsoName());
        return RT.ok(entity);
    }

    @Operation(summary = "删除SSO配置")
    @RequirePermission("system:sso-config:delete")
    @DeleteMapping("/{id}")
    public RT<Boolean> delete(@Parameter(description = "SSO配置ID") @PathVariable Long id) {
        boolean result = authSsoConfigService.removeById(id);
        log.info("SSO配置已删除: id={}, result={}", id, result);
        return RT.ok(result);
    }

    private void validateCertificate(String certificate) {
        if (certificate == null || certificate.isBlank()) {
            return;
        }
        try {
            String pem = certificate.strip();
            if (!pem.startsWith("-----BEGIN CERTIFICATE-----")) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "证书格式错误: 不是有效的X.509 PEM格式");
            }
            String base64 = pem
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));
            cert.checkValidity();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "证书格式校验失败: " + e.getMessage());
        }
    }
}
