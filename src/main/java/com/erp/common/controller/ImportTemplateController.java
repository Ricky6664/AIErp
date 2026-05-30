package com.erp.common.controller;

import com.alibaba.excel.EasyExcel;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 导入模板管理 Controller.
 *
 * <p>提供各模块的导入模板下载和示例数据下载.
 * 模块需先通过 {@link #registerTemplateClass(String, Class)} 注册其
 * {@code @ExcelProperty} 标注的 DTO 类, 模板下载才能正常工作.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Tag(name = "导入模板管理", description = "下载导入模板、示例数据")
@RestController
@RequestMapping("/api/common/templates")
public class ImportTemplateController {

    private static final Map<String, Class<?>> TEMPLATE_CLASS_REGISTRY = new ConcurrentHashMap<>();

    @Operation(summary = "下载导入模板")
    @RequirePermission("common:template:download")
    @GetMapping("/{module}/download")
    public void downloadTemplate(
            @Parameter(description = "模块标识", required = true) @PathVariable String module,
            HttpServletResponse response) throws IOException {
        Class<?> templateClass = getTemplateClass(module);
        setExcelResponseHeaders(response, module + "-import-template");
        EasyExcel.write(response.getOutputStream(), templateClass)
                .sheet("导入模板")
                .doWrite(List.of());
    }

    @Operation(summary = "下载示例数据")
    @RequirePermission("common:template:download")
    @GetMapping("/{module}/sample")
    public void downloadSample(
            @Parameter(description = "模块标识", required = true) @PathVariable String module,
            HttpServletResponse response) throws IOException {
        Class<?> templateClass = getTemplateClass(module);
        setExcelResponseHeaders(response, module + "-sample");
        try {
            Object sampleRow = templateClass.getDeclaredConstructor().newInstance();
            EasyExcel.write(response.getOutputStream(), templateClass)
                    .sheet("示例数据")
                    .doWrite(List.of(sampleRow));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建示例数据行失败: module={}, class={}", module, templateClass.getName(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "生成示例数据失败");
        }
    }

    /**
     * 注册模块的导入模板 DTO 类.
     *
     * <p>模块启动时调用此方法, 将其 {@code @ExcelProperty} 标注的导入 DTO 类注册进来.
     * 注册后, {@code GET /{module}/download} 和 {@code GET /{module}/sample} 即可使用.</p>
     *
     * @param module        模块标识 (如 "product", "order")
     * @param templateClass 标注了 {@code @ExcelProperty} 的 DTO 类
     */
    public static void registerTemplateClass(String module, Class<?> templateClass) {
        TEMPLATE_CLASS_REGISTRY.put(module, templateClass);
        log.info("注册导入模板: module={}, class={}", module, templateClass.getName());
    }

    private Class<?> getTemplateClass(String module) {
        Class<?> templateClass = TEMPLATE_CLASS_REGISTRY.get(module);
        if (templateClass == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND,
                    "模块[" + module + "]未注册导入模板, 请先调用 registerTemplateClass()");
        }
        return templateClass;
    }

    private void setExcelResponseHeaders(HttpServletResponse response, String fileName) {
        String encodedFileName;
        try {
            encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
        } catch (Exception e) {
            log.error("文件名 URL 编码失败: {}", fileName, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFileName + ".xlsx");
    }
}
