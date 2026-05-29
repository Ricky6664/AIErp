package com.erp.common.file;

import com.erp.common.config.FileUploadProperties;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.mapper.SysFileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FileUploadService 验证测试 —— 按Section 2任务目标逐项验证.
 *
 * @author AI
 * @since 2026-05-30
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileUploadService - 文件上传服务验证")
class FileUploadServiceVerificationTest {

    @Mock
    private SysFileMapper sysFileMapper;

    private FileUploadProperties properties;

    private FileUploadService fileUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        properties = new FileUploadProperties();
        properties.setPath(tempDir.toString());
        properties.setAllowedTypes("image/jpeg,image/png,application/pdf,application/vnd.ms-excel");
        fileUploadService = new FileUploadService(properties, sysFileMapper);
    }

    // ========== 扩展名黑名单 ==========

    @Nested
    @DisplayName("扩展名黑名单校验")
    class ExtensionBlacklist {

        @Test
        @DisplayName("拒绝 .exe")
        void rejectExe() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "malware.exe", "application/octet-stream", new byte[]{0x00, 0x01});

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> fileUploadService.upload(file));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("拒绝 .bat")
        void rejectBat() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "script.bat", "application/octet-stream", new byte[]{0x00, 0x01, 0x02, 0x03});

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> fileUploadService.upload(file));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("拒绝 .sh")
        void rejectSh() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "run.sh", "application/octet-stream", new byte[]{0x00, 0x01, 0x02, 0x03});

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> fileUploadService.upload(file));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("拒绝 .cmd")
        void rejectCmd() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "admin.cmd", "application/octet-stream", new byte[]{0x00, 0x01, 0x02, 0x03});

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> fileUploadService.upload(file));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("缺少扩展名拒绝")
        void rejectNoExtension() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "noextension", "application/octet-stream", new byte[]{0x00, 0x01, 0x02, 0x03});

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> fileUploadService.upload(file));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
        }
    }

    // ========== 文件大小校验 ==========

    @Nested
    @DisplayName("文件大小校验")
    class FileSizeValidation {

        @Test
        @DisplayName("空文件拒绝")
        void rejectEmptyFile() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "empty.jpg", "image/jpeg", new byte[0]);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> fileUploadService.upload(file));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("null 文件拒绝")
        void rejectNullFile() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> fileUploadService.upload(null));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("超大文件拒绝（>10MB）")
        void rejectLargeFile() {
            byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
            MockMultipartFile file = new MockMultipartFile(
                    "file", "big.jpg", "image/jpeg", largeContent);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> fileUploadService.upload(file));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("10MB 边界内文件放行")
        void acceptBoundaryFile() throws IOException {
            byte[] content = new byte[10 * 1024 * 1024]; // exactly 10MB
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "max.jpg", "image/jpeg", content);

            doReturn(1).when(sysFileMapper).insert(any());

            fileUploadService.upload(file);

            ArgumentCaptor<com.erp.system.entity.SysFile> captor =
                    ArgumentCaptor.forClass(com.erp.system.entity.SysFile.class);
            verify(sysFileMapper).insert(captor.capture());
            assertEquals(10 * 1024 * 1024, captor.getValue().getSizeBytes());
        }
    }

    // ========== MIME 白名单校验 ==========

    @Nested
    @DisplayName("MIME魔数白名单校验")
    class MimeWhitelist {

        @Test
        @DisplayName("JPEG 魔数通过")
        void acceptJpeg() throws IOException {
            byte[] content = new byte[]{0x01,0x02,0x03,0x04};
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "photo.jpg", "image/jpeg", content);

            doReturn(1).when(sysFileMapper).insert(any());

            fileUploadService.upload(file);

            verify(sysFileMapper).insert(any());
        }

        @Test
        @DisplayName("PNG 魔数通过")
        void acceptPng() throws IOException {
            byte[] content = new byte[8];
            content[0] = (byte) 0x89;
            content[1] = 0x50;
            content[2] = 0x4E;
            content[3] = 0x47;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "icon.png", "image/png", content);

            doReturn(1).when(sysFileMapper).insert(any());

            fileUploadService.upload(file);

            verify(sysFileMapper).insert(any());
        }

        @Test
        @DisplayName("PDF 魔数通过")
        void acceptPdf() throws IOException {
            byte[] content = new byte[8];
            content[0] = 0x25;
            content[1] = 0x50;
            content[2] = 0x44;
            content[3] = 0x46;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "doc.pdf", "application/pdf", content);

            doReturn(1).when(sysFileMapper).insert(any());

            fileUploadService.upload(file);

            verify(sysFileMapper).insert(any());
        }

        @Test
        @DisplayName("未配置白名单时放行所有类型")
        void allowAllWhenNoWhitelist() throws IOException {
            properties.setAllowedTypes(null);

            // PDF magic bytes: 25 50 44 46
            byte[] content = new byte[8];
            content[0] = 0x25;
            content[1] = 0x50;
            content[2] = 0x44;
            content[3] = 0x46;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "file.pdf", "application/pdf", content);

            doReturn(1).when(sysFileMapper).insert(any());

            // null whitelist → checkMimeWhitelist returns early → upload succeeds
            com.erp.vo.FileVO result = fileUploadService.upload(file);

            assertNotNull(result);
            assertEquals("file.pdf", result.getOriginalName());
            verify(sysFileMapper).insert(any());
        }
    }

    // ========== 成功上传流程 ==========

    @Nested
    @DisplayName("成功上传流程")
    class SuccessfulUpload {

        @Test
        @DisplayName("UUID 文件名格式正确")
        void uuidFilenameFormat() throws IOException {
            byte[] content = new byte[100];
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", content);

            doReturn(1).when(sysFileMapper).insert(any());

            fileUploadService.upload(file);

            String dateDir = java.time.LocalDate.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path uploaded = Files.list(tempDir.resolve(dateDir))
                    .filter(p -> p.getFileName().toString().endsWith(".jpg"))
                    .findFirst().orElse(null);

            assertNotNull(uploaded, "上传文件应存在于日期目录中");
            String filename = uploaded.getFileName().toString();
            assertTrue(filename.matches("[a-f0-9]{32}\\.jpg"),
                    "文件名应为 32 位 UUID + .jpg, 实际: " + filename);
        }

        @Test
        @DisplayName("按日期分目录存储 (yyyy/MM/dd)")
        void dateDirectoryStructure() throws IOException {
            byte[] content = new byte[100];
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "photo.jpg", "image/jpeg", content);

            doReturn(1).when(sysFileMapper).insert(any());

            fileUploadService.upload(file);

            String dateDir = java.time.LocalDate.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path datePath = tempDir.resolve(dateDir);
            assertTrue(Files.isDirectory(datePath), "日期目录应存在");
            assertEquals(1, Files.list(datePath).count(), "目录中应有 1 个文件");
        }

        @Test
        @DisplayName("FileVO 返回完整字段 (url, originalName, size, type)")
        void fileVOComplete() throws IOException {
            byte[] content = new byte[200];
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "myphoto.jpg", "image/jpeg", content);

            doReturn(1).when(sysFileMapper).insert(any());

            com.erp.vo.FileVO result = fileUploadService.upload(file);

            assertNotNull(result, "FileVO 不应为 null");
            assertNotNull(result.getUrl(), "url 不应为 null");
            assertTrue(result.getUrl().startsWith("/api/file/download?path="),
                    "url 应包含下载路径, 实际: " + result.getUrl());
            assertEquals("myphoto.jpg", result.getOriginalName());
            assertEquals(200L, result.getSize());
            assertEquals("image/jpeg", result.getType());
        }

        @Test
        @DisplayName("sys_file 元数据完整入库")
        void sysFileMetadata() throws IOException {
            byte[] content = new byte[500];
            content[0] = (byte) 0xFF;
            content[1] = (byte) 0xD8;
            content[2] = (byte) 0xFF;

            MockMultipartFile file = new MockMultipartFile(
                    "file", "metadata.jpg", "image/jpeg", content);

            doReturn(1).when(sysFileMapper).insert(any());

            fileUploadService.upload(file);

            ArgumentCaptor<com.erp.system.entity.SysFile> captor =
                    ArgumentCaptor.forClass(com.erp.system.entity.SysFile.class);
            verify(sysFileMapper).insert(captor.capture());

            com.erp.system.entity.SysFile saved = captor.getValue();
            assertEquals("metadata.jpg", saved.getOriginalName(), "原始文件名");
            assertTrue(saved.getStoragePath().contains("/"), "存储路径应含日期子目录");
            assertEquals("image/jpeg", saved.getMimeType(), "MIME 类型");
            assertEquals(".jpg", saved.getExtension(), "扩展名");
            assertEquals(500L, saved.getSizeBytes(), "文件大小");
            assertEquals("common", saved.getModule(), "模块");
            assertEquals("CONFIRMED", saved.getStatus(), "状态");
            assertEquals(0, saved.getDownloadCount(), "下载次数");
        }
    }
}
