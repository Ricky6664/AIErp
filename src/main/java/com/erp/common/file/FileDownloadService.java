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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件下载服务.
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileDownloadService {

    private final FileUploadProperties fileUploadProperties;
    private final SysFileMapper sysFileMapper;

    private static final int BUFFER_SIZE = 8192;

    /**
     * 下载文件.
     *
     * <p>从 sys_file 表查元数据, 校验文件存在与可读,
     * 设置 Content-Type / Content-Disposition / Content-Length 响应头,
     * 使用 StreamingResponseBody 流式输出避免内存溢出, 支持断点续传 (Range 头).</p>
     *
     * @param fileId   文件记录ID
     * @param response HTTP 响应
     */
    public void download(String fileId, HttpServletResponse response) {
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

        String originalName = sysFile.getOriginalName();
        String mimeType = sysFile.getMimeType();
        long fileSize = filePath.toFile().length();

        response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + URLEncoder.encode(originalName, StandardCharsets.UTF_8)
                        .replace("+", "%20"));

        String rangeHeader = getRangeHeader();
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            handleRangeDownload(filePath, fileSize, rangeHeader, response);
        } else {
            handleFullDownload(filePath, fileSize, response);
        }

        sysFile.setDownloadCount(sysFile.getDownloadCount() != null
                ? sysFile.getDownloadCount() + 1 : 1);
        sysFileMapper.updateById(sysFile);
        log.info("文件下载完成: id={}, name={}", fileId, originalName);
    }

    /**
     * 全量下载, 使用 StreamingResponseBody 流式输出.
     */
    private void handleFullDownload(Path filePath, long fileSize, HttpServletResponse response) {
        response.setContentLengthLong(fileSize);
        StreamingResponseBody stream = out -> {
            try (InputStream in = Files.newInputStream(filePath)) {
                byte[] buf = new byte[BUFFER_SIZE];
                int read;
                while ((read = in.read(buf)) != -1) {
                    out.write(buf, 0, read);
                }
                out.flush();
            }
        };
        try {
            stream.writeTo(response.getOutputStream());
        } catch (IOException e) {
            log.error("文件下载流式输出失败: {}", filePath, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

    /**
     * Range 断点续传下载, 返回 206 Partial Content.
     */
    private void handleRangeDownload(Path filePath, long fileSize, String rangeHeader,
                                     HttpServletResponse response) {
        String rangeValue = rangeHeader.substring("bytes=".length());
        String[] parts = rangeValue.split("-");
        long start = Long.parseLong(parts[0]);
        long end = parts.length > 1 && !parts[1].isEmpty()
                ? Long.parseLong(parts[1]) : fileSize - 1;

        if (start >= fileSize || end >= fileSize || start > end) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize);
            return;
        }

        long contentLength = end - start + 1;
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader(HttpHeaders.CONTENT_RANGE,
                "bytes " + start + "-" + end + "/" + fileSize);
        response.setContentLengthLong(contentLength);

        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r");
             OutputStream out = response.getOutputStream()) {
            raf.seek(start);
            byte[] buf = new byte[BUFFER_SIZE];
            long remaining = contentLength;
            int read;
            while (remaining > 0
                    && (read = raf.read(buf, 0, (int) Math.min(BUFFER_SIZE, remaining))) != -1) {
                out.write(buf, 0, read);
                remaining -= read;
            }
            out.flush();
        } catch (IOException e) {
            log.error("Range 下载失败: {}", filePath, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

    /**
     * 从当前请求中获取 Range 头.
     */
    private String getRangeHeader() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                return attrs.getRequest().getHeader(HttpHeaders.RANGE);
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
