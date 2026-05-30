package com.erp.common.file;

import com.erp.common.config.FileUploadProperties;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysFile;
import com.erp.system.mapper.SysFileMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * 文件预览服务.
 *
 * <p>根据文件 MIME 类型提供不同的预览方式:
 * 图片直接返回字节流、PDF 返回 application/pdf 流、
 * Office 文件尝试 POI 转换(可选)、TXT 返回 UTF-8 文本、
 * 不支持格式返回下载链接. 设置 Content-Disposition: inline.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilePreviewService {

    private final FileUploadProperties fileUploadProperties;
    private final SysFileMapper sysFileMapper;

    private static final int BUFFER_SIZE = 8192;

    /** 支持 inline 预览的 MIME 类型前缀 */
    private static final Set<String> INLINE_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp", "image/svg+xml"
    );

    /** 可直接流式输出的文档类型 */
    private static final Set<String> INLINE_DOC_TYPES = Set.of(
            "application/pdf", "text/html", "text/css", "text/javascript",
            "application/json", "application/xml"
    );

    /**
     * 预览文件.
     *
     * <p>从 sys_file 表查元数据, 根据 MIME 类型选择预览策略,
     * 设置 Content-Disposition: inline 使浏览器内联展示.</p>
     *
     * @param fileId   文件记录ID
     * @param response HTTP 响应
     */
    public void preview(String fileId, HttpServletResponse response) {
        Long id;
        try {
            id = Long.parseLong(fileId);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR);
        }

        SysFile sysFile = sysFileMapper.selectOneById(id);
        if (sysFile == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        String storagePath = sysFile.getStoragePath();
        Path filePath = Paths.get(fileUploadProperties.getPath(), storagePath);
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            log.error("文件不可访问: {}", filePath);
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        String mimeType = sysFile.getMimeType();
        String originalName = sysFile.getOriginalName();
        long fileSize = filePath.toFile().length();

        // 设置 Content-Disposition: inline（浏览器内联展示）
        String encodedName = URLEncoder.encode(originalName, StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "inline;filename=" + encodedName);

        if (mimeType != null && isImage(mimeType)) {
            handleImagePreview(filePath, mimeType, fileSize, response);
        } else if (mimeType != null && isInlineDoc(mimeType)) {
            handleDocPreview(filePath, mimeType, fileSize, response);
        } else if (mimeType != null && isOffice(mimeType)) {
            handleOfficePreview(filePath, mimeType, originalName, response);
        } else if (mimeType != null && mimeType.startsWith("text/")) {
            handleTextPreview(filePath, response);
        } else {
            handleUnsupportedPreview(filePath, originalName, mimeType, response);
        }

        log.info("文件预览完成: id={}, name={}, type={}", fileId, originalName, mimeType);
    }

    // ========== 私有预览方法 ==========

    /**
     * 图片预览 — 直接流式输出字节.
     */
    private void handleImagePreview(Path filePath, String mimeType, long fileSize,
                                     HttpServletResponse response) {
        response.setContentType(mimeType);
        response.setContentLengthLong(fileSize);
        streamFile(filePath, response);
    }

    /**
     * 文档预览 — PDF / 文本类直接流式输出.
     */
    private void handleDocPreview(Path filePath, String mimeType, long fileSize,
                                   HttpServletResponse response) {
        response.setContentType(mimeType);
        response.setContentLengthLong(fileSize);
        streamFile(filePath, response);
    }

    /**
     * Office 文件预览 — 尝试 POI 转换, 失败则返回下载链接.
     */
    private void handleOfficePreview(Path filePath, String mimeType, String originalName,
                                      HttpServletResponse response) {
        // Office 预览可选: 此处返回下载提示, POI 转换在后续任务实现
        response.setContentType("application/json;charset=UTF-8");
        String downloadUrl = "/api/file/download/" + filePath.getFileName().toString();
        String json = "{\"type\":\"unsupported\",\"message\":\"Office文件暂不支持在线预览，请下载后查看\","
                + "\"fileName\":\"" + escapeJson(originalName) + "\","
                + "\"downloadUrl\":\"" + downloadUrl + "\"}";
        try (OutputStream out = response.getOutputStream()) {
            out.write(json.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            log.error("Office 预览响应写入失败: {}", filePath, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

    /**
     * 纯文本预览 — 流式输出文本内容, 避免大文件 OOM.
     */
    private void handleTextPreview(Path filePath, HttpServletResponse response) {
        response.setContentType("text/plain;charset=UTF-8");
        try {
            response.setContentLengthLong(Files.size(filePath));
        } catch (IOException e) {
            log.warn("无法获取文本文件大小, 将不设置 Content-Length: {}", filePath);
        }
        streamFile(filePath, response);
    }

    /**
     * 不支持预览的格式 — 返回 JSON 提示与下载链接.
     */
    private void handleUnsupportedPreview(Path filePath, String originalName, String mimeType,
                                           HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String downloadUrl = "/api/file/download/" + filePath.getFileName().toString();
        String typeStr = mimeType != null ? mimeType : "unknown";
        String json = "{\"type\":\"unsupported\",\"message\":\"不支持预览此文件格式\","
                + "\"fileName\":\"" + escapeJson(originalName) + "\","
                + "\"mimeType\":\"" + escapeJson(typeStr) + "\","
                + "\"downloadUrl\":\"" + downloadUrl + "\"}";
        try (OutputStream out = response.getOutputStream()) {
            out.write(json.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            log.error("不支持预览响应写入失败: {}", filePath, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

    // ========== 辅助方法 ==========

    /**
     * 流式输出文件内容到 response.
     *
     * <p>使用 8KB 缓冲区逐块读取并写入输出流, 避免内存溢出.</p>
     */
    private void streamFile(Path filePath, HttpServletResponse response) {
        try (OutputStream out = response.getOutputStream();
             InputStream in = new BufferedInputStream(Files.newInputStream(filePath), BUFFER_SIZE)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buf)) != -1) {
                out.write(buf, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            log.error("文件流式输出失败: {}", filePath, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private boolean isImage(String mimeType) {
        return INLINE_IMAGE_TYPES.contains(mimeType) || mimeType.startsWith("image/");
    }

    private boolean isInlineDoc(String mimeType) {
        return INLINE_DOC_TYPES.contains(mimeType);
    }

    private boolean isOffice(String mimeType) {
        return mimeType.contains("msword")
                || mimeType.contains("ms-excel")
                || mimeType.contains("ms-powerpoint")
                || mimeType.contains("officedocument")
                || mimeType.contains("opendocument");
    }

    /**
     * JSON 字符串转义.
     */
    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
