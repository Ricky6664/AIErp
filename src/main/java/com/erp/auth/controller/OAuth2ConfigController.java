package com.erp.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.AuthOauth2Config;
import com.erp.auth.service.AuthOauth2ConfigService;
import com.erp.auth.util.AesEncryptUtil;
import com.erp.auth.vo.OAuth2TestResult;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.net.URI;
import java.net.URL;

/**
 * OAuth2配置控制器.
 *
 * @author AI
 * @since 2026-06-05
 */
@Slf4j
@RestController
@RequestMapping("/api/system/oauth2-config")
@RequiredArgsConstructor
@Tag(name = "OAuth2配置管理", description = "OAuth2第三方登录配置CRUD接口")
public class OAuth2ConfigController {

    private final AuthOauth2ConfigService authOauth2ConfigService;

    @Value("${aes.secret-key:default-key-change-me-in-production}")
    private String aesSecretKey;

    @Operation(summary = "分页查询OAuth2配置列表")
    @RequirePermission("system:oauth2-config:query")
    @GetMapping("/page")
    public RT<PageResult<AuthOauth2Config>> page(@Parameter(description = "分页参数") PageQuery query) {
        LambdaQueryWrapper<AuthOauth2Config> wrapper = new LambdaQueryWrapper<AuthOauth2Config>()
                .orderByDesc(AuthOauth2Config::getCreateTime);
        PageResult<AuthOauth2Config> result = authOauth2ConfigService.pageList(query, wrapper);
        result.getList().forEach(r -> r.setClientSecret(AesEncryptUtil.mask(r.getClientSecret())));
        return RT.ok(result);
    }

    @Operation(summary = "查询OAuth2配置详情")
    @RequirePermission("system:oauth2-config:query")
    @GetMapping("/{id}")
    public RT<AuthOauth2Config> get(@Parameter(description = "OAuth2配置ID") @PathVariable Long id) {
        AuthOauth2Config entity = authOauth2ConfigService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        entity.setClientSecret(AesEncryptUtil.mask(entity.getClientSecret()));
        return RT.ok(entity);
    }

    @Operation(summary = "新增OAuth2配置")
    @RequirePermission("system:oauth2-config:create")
    @PostMapping
    public RT<AuthOauth2Config> create(@Valid @RequestBody AuthOauth2Config entity, BindingResult bindingResult) {
        entity.setClientSecret(AesEncryptUtil.encrypt(entity.getClientSecret(), aesSecretKey));
        authOauth2ConfigService.save(entity);
        entity.setClientSecret(AesEncryptUtil.mask(entity.getClientSecret()));
        log.info("OAuth2配置已新增: id={}, supplierName={}", entity.getId(), entity.getSupplierName());
        return RT.ok(entity);
    }

    @Operation(summary = "修改OAuth2配置")
    @RequirePermission("system:oauth2-config:update")
    @PutMapping("/{id}")
    public RT<AuthOauth2Config> update(@Parameter(description = "OAuth2配置ID") @PathVariable Long id,
                                       @Valid @RequestBody AuthOauth2Config entity, BindingResult bindingResult) {
        AuthOauth2Config existing = authOauth2ConfigService.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        entity.setClientSecret(AesEncryptUtil.encrypt(entity.getClientSecret(), aesSecretKey));
        entity.setId(id);
        authOauth2ConfigService.updateById(entity);
        entity.setClientSecret(AesEncryptUtil.mask(entity.getClientSecret()));
        log.info("OAuth2配置已修改: id={}, supplierName={}", id, entity.getSupplierName());
        return RT.ok(entity);
    }

    @Operation(summary = "删除OAuth2配置")
    @RequirePermission("system:oauth2-config:delete")
    @DeleteMapping("/{id}")
    public RT<Boolean> delete(@Parameter(description = "OAuth2配置ID") @PathVariable Long id) {
        boolean result = authOauth2ConfigService.removeById(id);
        log.info("OAuth2配置已删除: id={}, result={}", id, result);
        return RT.ok(result);
    }

    @Operation(summary = "测试OAuth2连接")
    @RequirePermission("system:oauth2-config:query")
    @PostMapping("/{id}/test")
    public RT<OAuth2TestResult> testConnection(@Parameter(description = "OAuth2配置ID") @PathVariable Long id) {
        AuthOauth2Config entity = authOauth2ConfigService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        String authUrl = entity.getAuthUrl();
        long startTime = System.currentTimeMillis();
        try {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(10000);
            RestTemplate restTemplate = new RestTemplate(factory);
            URI uri = new URL(authUrl).toURI();
            var response = restTemplate.execute(uri, HttpMethod.GET, null, clientResponse -> {
                int status = clientResponse.getStatusCode().value();
                long elapsed = System.currentTimeMillis() - startTime;
                if (status == 200 || status == 302 || status == 401) {
                    return OAuth2TestResult.builder()
                            .success(true)
                            .statusCode(status)
                            .responseTime(elapsed)
                            .message("授权URL可访问")
                            .testUrl(authUrl)
                            .build();
                }
                return OAuth2TestResult.builder()
                        .success(false)
                        .statusCode(status)
                        .responseTime(elapsed)
                        .message("HTTP错误: " + status)
                        .testUrl(authUrl)
                        .build();
            });
            if (response != null) {
                log.info("OAuth2连接测试完成: id={}, url={}, success={}, time={}ms",
                        id, authUrl, response.isSuccess(), response.getResponseTime());
                return RT.ok(response);
            }
            long elapsed = System.currentTimeMillis() - startTime;
            return RT.ok(OAuth2TestResult.builder()
                    .success(false).statusCode(0).responseTime(elapsed)
                    .message("未知错误").testUrl(authUrl).build());
        } catch (ResourceAccessException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            Throwable cause = e.getCause();
            if (cause instanceof java.net.UnknownHostException) {
                log.warn("OAuth2连接测试DNS解析失败: id={}, url={}", id, authUrl);
                return RT.ok(OAuth2TestResult.builder()
                        .success(false).statusCode(0).responseTime(elapsed)
                        .message("DNS解析失败: " + cause.getMessage())
                        .testUrl(authUrl).build());
            }
            if (cause instanceof java.net.SocketTimeoutException) {
                log.warn("OAuth2连接测试超时: id={}, url={}", id, authUrl);
                return RT.ok(OAuth2TestResult.builder()
                        .success(false).statusCode(0).responseTime(elapsed)
                        .message("连接超时(10秒)")
                        .testUrl(authUrl).build());
            }
            if (cause instanceof java.net.ConnectException) {
                log.warn("OAuth2连接测试连接被拒绝: id={}, url={}", id, authUrl);
                return RT.ok(OAuth2TestResult.builder()
                        .success(false).statusCode(0).responseTime(elapsed)
                        .message("连接失败: " + cause.getMessage())
                        .testUrl(authUrl).build());
            }
            log.warn("OAuth2连接测试网络错误: id={}, url={}, error={}", id, authUrl, e.getMessage());
            return RT.ok(OAuth2TestResult.builder()
                    .success(false).statusCode(0).responseTime(elapsed)
                    .message("网络错误: " + e.getMessage())
                    .testUrl(authUrl).build());
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.warn("OAuth2连接测试失败: id={}, url={}, error={}", id, authUrl, e.getMessage());
            return RT.ok(OAuth2TestResult.builder()
                    .success(false).statusCode(0).responseTime(elapsed)
                    .message("连接失败: " + e.getMessage())
                    .testUrl(authUrl).build());
        }
    }
}
