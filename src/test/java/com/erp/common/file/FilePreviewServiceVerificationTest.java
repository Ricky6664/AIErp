package com.erp.common.file;

import com.erp.common.config.FileUploadProperties;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysFile;
import com.erp.system.mapper.SysFileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FilePreviewService 验证测试 —— 按 Section 2 任务目标逐项验证.
 *
 * @author AI
 * @since 2026-05-30
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FilePreviewService - 文件预览服务验证")
class FilePreviewServiceVerificationTest {

    @Mock
    private SysFileMapper sysFileMapper;

    private FileUploadProperties properties;

    private FilePreviewService previewService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        properties = new FileUploadProperties();
        properties.setPath(tempDir.toString());
        previewService = new FilePreviewService(properties, sysFileMapper);
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
                    () -> previewService.preview("not-a-number", response));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("空字符串 fileId 返回 PARAM_FORMAT_ERROR")
        void rejectEmptyFileId() {
            MockHttpServletResponse response = new MockHttpServletResponse();

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> previewService.preview("", response));
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
                    () -> previewService.preview("1", response));
            assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
            verify(sysFileMapper).selectOneById(1L);
        }

        @Test
        @DisplayName("磁盘文件不可读返回 NOT_FOUND")
        void rejectFileNotReadable() {
            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = new SysFile();
            record.setId(1L);
            record.setOriginalName("missing.png");
            record.setStoragePath("nonexistent/file.png");
            record.setMimeType("image/png");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> previewService.preview("1", response));
            assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ========== 图片预览 ==========

    @Nested
    @DisplayName("图片预览 — 直接返回字节流")
    class ImagePreview {

        @Test
        @DisplayName("Content-Disposition 设置为 inline")
        void contentDispositionInline() throws IOException {
            Path imgFile = tempDir.resolve("photo.jpg");
            byte[] content = new byte[1024];
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;
            Files.write(imgFile, content);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("photo.jpg", imgFile, "image/jpeg");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            String disposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
            assertNotNull(disposition, "Content-Disposition 不应为 null");
            assertTrue(disposition.startsWith("inline;filename="),
                    "Content-Disposition 应以 inline;filename= 开头, 实际: " + disposition);
        }

        @Test
        @DisplayName("Content-Type 与文件 MIME 一致")
        void contentTypeMatchesMime() throws IOException {
            Path imgFile = tempDir.resolve("icon.png");
            byte[] content = new byte[512];
            content[0] = (byte) 0x89;
            content[1] = 0x50;
            content[2] = 0x4E;
            content[3] = 0x47;
            Files.write(imgFile, content);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("icon.png", imgFile, "image/png");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals("image/png", response.getContentType());
        }

        @Test
        @DisplayName("流式输出内容与源文件一致")
        void streamContentMatchesFile() throws IOException {
            Path imgFile = tempDir.resolve("drawing.jpg");
            byte[] content = new byte[2048];
            for (int i = 0; i < content.length; i++) {
                content[i] = (byte) (i % 256);
            }
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;
            Files.write(imgFile, content);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("drawing.jpg", imgFile, "image/jpeg");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertArrayEquals(content, response.getContentAsByteArray(),
                    "预览输出的字节应与源文件完全一致");
        }

        @Test
        @DisplayName("Content-Length 与文件大小一致")
        void contentLengthMatchesFileSize() throws IOException {
            Path imgFile = tempDir.resolve("photo.jpg");
            byte[] content = new byte[3072];
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;
            Files.write(imgFile, content);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("photo.jpg", imgFile, "image/jpeg");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals(3072L, response.getContentLengthLong());
        }
    }

    // ========== PDF 预览 ==========

    @Nested
    @DisplayName("PDF 预览 — 返回 application/pdf 流")
    class PdfPreview {

        @Test
        @DisplayName("Content-Type 为 application/pdf")
        void contentTypeIsPdf() throws IOException {
            Path pdfFile = tempDir.resolve("report.pdf");
            byte[] content = new byte[1024];
            content[0] = 0x25;
            content[1] = 0x50;
            content[2] = 0x44;
            content[3] = 0x46;
            Files.write(pdfFile, content);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("report.pdf", pdfFile, "application/pdf");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals("application/pdf", response.getContentType());
        }

        @Test
        @DisplayName("Content-Disposition: inline 设置正确")
        void contentDispositionInline() throws IOException {
            Path pdfFile = tempDir.resolve("doc.pdf");
            byte[] content = new byte[512];
            content[0] = 0x25;
            content[1] = 0x50;
            content[2] = 0x44;
            content[3] = 0x46;
            Files.write(pdfFile, content);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("doc.pdf", pdfFile, "application/pdf");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            String disposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
            assertNotNull(disposition);
            assertTrue(disposition.startsWith("inline;filename="));
            assertTrue(disposition.contains("doc.pdf"));
        }

        @Test
        @DisplayName("PDF 内容流式输出完整")
        void pdfContentStreamedCompletely() throws IOException {
            Path pdfFile = tempDir.resolve("manual.pdf");
            byte[] content = new byte[4096];
            for (int i = 0; i < content.length; i++) {
                content[i] = (byte) (i % 256);
            }
            content[0] = 0x25;
            content[1] = 0x50;
            content[2] = 0x44;
            content[3] = 0x46;
            Files.write(pdfFile, content);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("manual.pdf", pdfFile, "application/pdf");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertArrayEquals(content, response.getContentAsByteArray());
        }
    }

    // ========== TXT 文本预览 ==========

    @Nested
    @DisplayName("TXT 文本预览 — 返回 UTF-8 文本")
    class TextPreview {

        @Test
        @DisplayName("Content-Type 为 text/plain;charset=UTF-8")
        void contentTypeIsUtf8Text() throws IOException {
            Path txtFile = tempDir.resolve("readme.txt");
            String text = "Hello World\n这是一行中文\n第三行";
            Files.write(txtFile, text.getBytes(StandardCharsets.UTF_8));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("readme.txt", txtFile, "text/plain");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals("text/plain;charset=UTF-8", response.getContentType());
        }

        @Test
        @DisplayName("UTF-8 文本内容完整输出")
        void utf8ContentComplete() throws IOException {
            Path txtFile = tempDir.resolve("notes.txt");
            String text = "行1: 测试数据\n行2: Test Data\n行3: 混合中文English";
            Files.write(txtFile, text.getBytes(StandardCharsets.UTF_8));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("notes.txt", txtFile, "text/plain");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals(text, response.getContentAsString(StandardCharsets.UTF_8),
                    "UTF-8 文本内容应完整一致");
        }

        @Test
        @DisplayName("Content-Disposition: inline 带文件名")
        void inlineWithFilename() throws IOException {
            Path txtFile = tempDir.resolve("log.txt");
            Files.write(txtFile, "some log content".getBytes(StandardCharsets.UTF_8));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("log.txt", txtFile, "text/plain");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            String disposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
            assertNotNull(disposition);
            assertTrue(disposition.contains("inline;filename="));
        }
    }

    // ========== Office 文件预览 ==========

    @Nested
    @DisplayName("Office 文件预览 — 暂不支持在线预览返回下载链接")
    class OfficePreview {

        @Test
        @DisplayName("DOC 文件返回 JSON 提示")
        void docReturnsJson() throws IOException {
            Path docFile = tempDir.resolve("report.docx");
            Files.write(docFile, new byte[]{0x50, 0x4B, 0x03, 0x04});

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("report.docx", docFile,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals("application/json;charset=UTF-8", response.getContentType());
            String body = response.getContentAsString();
            assertTrue(body.contains("\"type\":\"unsupported\""),
                    "Office 文件应返回 unsupported JSON");
            assertTrue(body.contains("Office文件暂不支持在线预览"),
                    "应提示 Office 文件暂不支持在线预览");
            assertTrue(body.contains("downloadUrl"),
                    "应包含下载链接 downloadUrl");
        }

        @Test
        @DisplayName("XLS 文件返回下载链接 JSON")
        void xlsReturnsDownloadJson() throws IOException {
            Path xlsFile = tempDir.resolve("data.xlsx");
            Files.write(xlsFile, new byte[]{0x50, 0x4B, 0x03, 0x04});

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.xlsx", xlsFile,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            String body = response.getContentAsString();
            assertTrue(body.contains("\"type\":\"unsupported\""));
            assertTrue(body.contains("\"fileName\":\"data.xlsx\""));
            assertTrue(body.contains("downloadUrl"));
        }

        @Test
        @DisplayName("PPT 文件返回下载链接 JSON")
        void pptReturnsDownloadJson() throws IOException {
            Path pptFile = tempDir.resolve("slides.pptx");
            Files.write(pptFile, new byte[]{0x50, 0x4B, 0x03, 0x04});

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("slides.pptx", pptFile,
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            String body = response.getContentAsString();
            assertTrue(body.contains("\"type\":\"unsupported\""));
            assertTrue(body.contains("\"fileName\":\"slides.pptx\""));
            assertTrue(body.contains("downloadUrl"));
        }
    }

    // ========== 不支持格式预览 ==========

    @Nested
    @DisplayName("不支持格式 — 返回下载链接")
    class UnsupportedPreview {

        @Test
        @DisplayName("未知格式返回 JSON 含 downloadUrl")
        void unknownFormatReturnsDownloadJson() throws IOException {
            Path unknownFile = tempDir.resolve("file.xyz");
            Files.write(unknownFile, new byte[]{0x00, 0x01, 0x02, 0x03});

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("file.xyz", unknownFile, "application/octet-stream");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals("application/json;charset=UTF-8", response.getContentType());
            String body = response.getContentAsString();
            assertTrue(body.contains("\"type\":\"unsupported\""),
                    "不支持的格式应返回 unsupported JSON");
            assertTrue(body.contains("不支持预览此文件格式"),
                    "应提示不支持预览");
            assertTrue(body.contains("downloadUrl"),
                    "应包含 downloadUrl 供用户下载");
        }

        @Test
        @DisplayName("MIME 为 null 时仍返回 JSON 提示")
        void nullMimeStillReturnsJson() throws IOException {
            Path unknownFile = tempDir.resolve("unknown.bin");
            Files.write(unknownFile, new byte[]{0x01, 0x02});

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("unknown.bin", unknownFile, null);

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            String body = response.getContentAsString();
            assertTrue(body.contains("\"type\":\"unsupported\""));
            assertTrue(body.contains("downloadUrl"));
        }
    }

    // ========== 中文文件名 ==========

    @Nested
    @DisplayName("中文文件名处理")
    class ChineseFilename {

        @Test
        @DisplayName("中文文件名使用 UTF-8 URL 编码")
        void chineseFilenameUtf8Encoded() throws IOException {
            Path txtFile = tempDir.resolve("测试文件.txt");
            Files.write(txtFile, "中文内容".getBytes(StandardCharsets.UTF_8));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("测试文件.txt", txtFile, "text/plain");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            String expected = "inline;filename="
                    + URLEncoder.encode("测试文件.txt", StandardCharsets.UTF_8)
                            .replace("+", "%20");
            assertEquals(expected, response.getHeader(HttpHeaders.CONTENT_DISPOSITION));
        }
    }

    // ========== 内联文档类型 ==========

    @Nested
    @DisplayName("内联文档类型 — JSON/HTML/XML 直接流式输出")
    class InlineDocPreview {

        @Test
        @DisplayName("JSON 文件设置 Content-Disposition: inline")
        void jsonInline() throws IOException {
            Path jsonFile = tempDir.resolve("data.json");
            String json = "{\"key\":\"value\"}";
            Files.write(jsonFile, json.getBytes(StandardCharsets.UTF_8));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("data.json", jsonFile, "application/json");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals("application/json", response.getContentType());
            String disposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
            assertTrue(disposition.startsWith("inline;filename="));
            assertEquals(json, response.getContentAsString());
        }

        @Test
        @DisplayName("HTML 文件设置 Content-Disposition: inline")
        void htmlInline() throws IOException {
            Path htmlFile = tempDir.resolve("page.html");
            String html = "<html><body>Test</body></html>";
            Files.write(htmlFile, html.getBytes(StandardCharsets.UTF_8));

            MockHttpServletResponse response = new MockHttpServletResponse();
            SysFile record = sysFileRecord("page.html", htmlFile, "text/html");

            when(sysFileMapper.selectOneById(1L)).thenReturn(record);

            previewService.preview("1", response);

            assertEquals("text/html", response.getContentType());
            String disposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);
            assertTrue(disposition.startsWith("inline;filename="));
            assertEquals(html, response.getContentAsString());
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
