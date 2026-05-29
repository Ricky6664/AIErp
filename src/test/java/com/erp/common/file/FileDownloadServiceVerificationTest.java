package com.erp.common.file;

import com.erp.common.config.FileUploadProperties;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysFile;
import com.erp.system.mapper.SysFileMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FileDownloadService 验证测试 — 按 Section 2 任务目标逐项验证.
 *
 * @author AI
 * @since 2026-05-30
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileDownloadService - 文件下载服务验证")
class FileDownloadServiceVerificationTest {

    @Mock
    private SysFileMapper sysFileMapper;

    private FileUploadProperties properties;

    private FileDownloadService downloadService;

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        properties = new FileUploadProperties();
        properties.setPath(tempDir.toString());
        downloadService = new FileDownloadService(properties, sysFileMapper);

        testFile = tempDir.resolve("test-download.dat");
        byte[] content = new byte[4096];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        Files.write(testFile, content);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    // ========== 参数校验 ==========

    @Nested
    @DisplayName("参数校验")
    class ParameterValidation {

        @Test
        @DisplayName("非法 fileId 格式返回 PARAM_FORMAT_ERROR")
        void rejectInvalidFileId() {
            MockHttpServletResponse response = new MockHttpServletResponse();

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> downloadService.download("not-a-number", response));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("fileId 为空字符串返回 PARAM_FORMAT_ERROR")
        void rejectEmptyFileId() {
            MockHttpServletResponse response = new MockHttpServletResponse();

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> downloadService.download("", response));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
        }
    }

    // ========== 文件不存在 ==========

    @Nested
    @DisplayName("文件不存在校验")
    class FileNotFound {

        @Test
        @DisplayName("数据库无记录返回 NOT_FOUND")
        void rejectDbNotFound() {
            MockHttpServletResponse response = new MockHttpServletResponse();
            when(sysFileMapper.selectOneById(1L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> downloadService.download("1", response));
            assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
            verify(sysFileMapper).selectOneById(1L);
        }

        @Test
        @DisplayName("磁盘文件不可读返回 NOT_FOUND")
        void rejectFileNotReadable() {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = new SysFile();
            record.setId(1L);
            record.setOriginalName("missing.txt");
            record.setStoragePath("nonexistent/file.txt");
            record.setMimeType("text/plain");
            record.setDownloadCount(0);

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> downloadService.download("1", response));
            assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ========== 全量下载 ==========

    @Nested
    @DisplayName("全量下载")
    class FullDownload {

        @Test
        @DisplayName("Content-Type 响应头正确设置")
        void contentDispositionAndType() throws IOException {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("report.pdf", testFile, "application/pdf");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals("application/pdf", response.getContentType());
            String disposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
            assertNotNull(disposition);
            assertTrue(disposition.startsWith("attachment;filename="),
                    "Content-Disposition 应以 attachment;filename= 开头");
            assertTrue(disposition.contains("report.pdf"),
                    "Content-Disposition 应包含原始文件名");
            assertEquals(200, response.getStatus());
        }

        @Test
        @DisplayName("Content-Length 与文件实际大小一致")
        void contentLengthMatchesFileSize() throws IOException {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals(4096L, response.getContentLengthLong());
        }

        @Test
        @DisplayName("流式输出内容与源文件完全一致")
        void streamOutputMatchesFile() throws IOException {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            byte[] expected = Files.readAllBytes(testFile);
            assertArrayEquals(expected, response.getContentAsByteArray(),
                    "下载内容应与源文件完全一致");
        }

        @Test
        @DisplayName("下载计数递增")
        void downloadCountIncremented() throws IOException {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");
            record.setDownloadCount(5);

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals(6, record.getDownloadCount(), "下载次数应从 5 变为 6");
            verify(sysFileMapper).updateById(record);
        }

        @Test
        @DisplayName("初始 downloadCount 为 null 时从 1 开始计数")
        void downloadCountNullTo1() throws IOException {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");
            record.setDownloadCount(null);

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals(1, record.getDownloadCount());
        }

        @Test
        @DisplayName("MIME 为 null 时默认 application/octet-stream")
        void defaultMimeWhenNull() throws IOException {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, null);

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals("application/octet-stream", response.getContentType());
        }

        @Test
        @DisplayName("中文文件名使用 UTF-8 编码")
        void chineseFilenameUtf8Encoded() throws IOException {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("测试文件.pdf", testFile, "application/pdf");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            String expected = "attachment;filename="
                    + URLEncoder.encode("测试文件.pdf", StandardCharsets.UTF_8)
                            .replace("+", "%20");
            assertEquals(expected, response.getHeader(HttpHeaders.CONTENT_DISPOSITION));
        }
    }

    // ========== Range 断点续传 ==========

    @Nested
    @DisplayName("Range 断点续传")
    class RangeDownload {

        @Test
        @DisplayName("Range 请求返回 206 Partial Content")
        void rangeReturns206() throws IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HttpHeaders.RANGE, "bytes=0-1023");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals(206, response.getStatus());
            String contentRange = response.getHeader(HttpHeaders.CONTENT_RANGE);
            assertNotNull(contentRange, "应设置 Content-Range 头");
            assertTrue(contentRange.startsWith("bytes "));
            assertEquals(1024L, response.getContentLengthLong());
        }

        @Test
        @DisplayName("Range 部分内容与源文件对应区间一致")
        void rangeContentMatches() throws IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HttpHeaders.RANGE, "bytes=100-199");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            byte[] fullFile = Files.readAllBytes(testFile);
            byte[] expectedRange = new byte[100];
            System.arraycopy(fullFile, 100, expectedRange, 0, 100);

            assertArrayEquals(expectedRange, response.getContentAsByteArray(),
                    "Range 请求返回的字节应与源文件 [100,199] 区间一致");
        }

        @Test
        @DisplayName("Range 不合法返回 416 Range Not Satisfiable")
        void invalidRangeReturns416() throws IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            // start beyond file size
            request.addHeader(HttpHeaders.RANGE, "bytes=5000-6000");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals(416, response.getStatus());
            String contentRange = response.getHeader(HttpHeaders.CONTENT_RANGE);
            assertNotNull(contentRange);
            assertTrue(contentRange.contains("bytes */4096"));
        }

        @Test
        @DisplayName("Range 仅指定 start 时下载到文件末尾")
        void rangeStartOnly() throws IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HttpHeaders.RANGE, "bytes=4000-");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals(206, response.getStatus());
            assertEquals(96L, response.getContentLengthLong(),
                    "应从 4000 到 4095, 共 96 字节");
            String contentRange = response.getHeader(HttpHeaders.CONTENT_RANGE);
            assertTrue(contentRange.contains("bytes 4000-4095/4096"));
        }
    }

    // ========== 无需 Range 时全量下载 ==========

    @Nested
    @DisplayName("Range 头不存在时全量下载")
    class NoRangeHeader {

        @Test
        @DisplayName("无 Range 头时返回 200 全量内容")
        void noRangeReturnsFullContent() throws IOException {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.bin", testFile, "application/octet-stream");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);
            when(sysFileMapper.updateById(any())).thenReturn(1);

            downloadService.download("1", response);

            assertEquals(200, response.getStatus());
            assertArrayEquals(Files.readAllBytes(testFile),
                    response.getContentAsByteArray());
        }
    }

    // ========== 辅助方法 ==========

    private SysFile sysFileRecord(String originalName, Path filePath, String mimeType) {
        SysFile record = new SysFile();
        record.setId(1L);
        record.setOriginalName(originalName);
        record.setStoragePath(filePath.getFileName().toString());
        record.setMimeType(mimeType);
        record.setDownloadCount(0);
        return record;
    }
}
