package com.erp.common.file;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.config.FileUploadProperties;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysFile;
import com.erp.system.mapper.SysFileMapper;
import com.erp.vo.FileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传服务.
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadProperties fileUploadProperties;

    private final SysFileMapper sysFileMapper;

    /** 单文件最大大小（字节）：10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /** 扩展名黑名单 */
    private static final Set<String> EXTENSION_BLACKLIST = new HashSet<>(Arrays.asList(
            "exe", "bat", "sh", "cmd"
    ));

    // ========== MIME 魔数映射 ==========

    /** 常见文件类型魔数（magic number）→ MIME 类型 */
    private static final byte[] MAGIC_JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] MAGIC_PNG  = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] MAGIC_PDF  = {0x25, 0x50, 0x44, 0x46};
    private static final byte[] MAGIC_XLS  = {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0};
    private static final byte[] MAGIC_OOXML = {0x50, 0x4B, 0x03, 0x04};

    /**
     * 上传文件.
     *
     * @param file 上传的 MultipartFile
     * @return 文件上传结果 VO
     */
    public FileVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "上传文件不能为空");
        }

        // 1. 扩展名黑名单校验
        String originalName = file.getOriginalFilename();
        checkExtensionBlacklist(originalName);

        // 2. 文件大小校验（单文件最大 10MB）
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.PARAM_RANGE_ERROR,
                    "文件大小超出限制, 最大 10MB");
        }

        // 3. MIME 魔数白名单校验
        String mimeType = detectMimeType(file);
        checkMimeWhitelist(mimeType);

        // 4. 生成 UUID 文件名（保留原始扩展名）
        String extension = getExtension(originalName);
        String uuidFileName = IdUtil.fastSimpleUUID() + extension;

        // 5. 按日期分目录 (yyyy/MM/dd)
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadDir = Paths.get(fileUploadProperties.getPath(), dateDir);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("创建上传目录失败: {}", uploadDir, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建上传目录失败");
        }

        // 6. 存储文件
        Path destPath = uploadDir.resolve(uuidFileName);
        try {
            Files.copy(file.getInputStream(), destPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件存储失败: {}", destPath, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件存储失败");
        }

        long fileSize = destPath.toFile().length();
        log.info("文件上传成功: original={}, stored={}, size={}, type={}",
                originalName, destPath, fileSize, mimeType);

        // 7. 记录 sys_file 元数据
        SysFile sysFile = new SysFile();
        sysFile.setOriginalName(originalName);
        sysFile.setStoragePath(dateDir + "/" + uuidFileName);
        sysFile.setMimeType(mimeType);
        sysFile.setExtension(extension);
        sysFile.setSizeBytes(fileSize);
        sysFile.setModule("common");
        sysFile.setStatus("CONFIRMED");
        sysFile.setDownloadCount(0);
        try {
            sysFile.setUploaderId(StpUtil.getLoginIdAsLong());
        } catch (Exception e) {
            sysFile.setUploaderId(0L);
        }
        sysFileMapper.insert(sysFile);
        log.info("sys_file 元数据已入库: id={}", sysFile.getId());

        // 8. 构建返回 VO
        String url = "/api/file/download?path=" + dateDir + "/" + uuidFileName;
        return FileVO.builder()
                .url(url)
                .originalName(originalName)
                .size(fileSize)
                .type(mimeType)
                .build();
    }

    // ========== 私有方法 ==========

    /**
     * 通过魔数检测文件 MIME 类型.
     *
     * <p>读取文件头魔数字节，匹配已知类型。无法识别时回退到 Hutool 检测或 Content-Type.</p>
     */
    private String detectMimeType(MultipartFile file) {
        byte[] header = readHeader(file, 8);
        if (header != null && header.length >= 4) {
            if (startsWith(header, MAGIC_JPEG)) {
                return "image/jpeg";
            }
            if (startsWith(header, MAGIC_PNG)) {
                return "image/png";
            }
            if (startsWith(header, MAGIC_PDF)) {
                return "application/pdf";
            }
            if (startsWith(header, MAGIC_XLS)) {
                return "application/vnd.ms-excel";
            }
            if (startsWith(header, MAGIC_OOXML)) {
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
        }

        // 回退：Hutool 基于魔数的探测
        try (InputStream in = file.getInputStream()) {
            String hutoolType = FileTypeUtil.getType(in);
            if (hutoolType != null) {
                return mapHutoolType(hutoolType);
            }
        } catch (IOException e) {
            log.warn("Hutool 文件类型探测失败, 回退到 Content-Type", e);
        }

        // 最终回退到 Content-Type
        String contentType = file.getContentType();
        if (contentType != null) {
            return contentType;
        }

        throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR, "无法识别文件类型");
    }

    /**
     * 校验扩展名黑名单.
     */
    private void checkExtensionBlacklist(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR, "文件缺少扩展名");
        }
        String ext = getExtension(originalFilename).toLowerCase();
        // 去掉开头的 "."
        if (ext.startsWith(".")) {
            ext = ext.substring(1);
        }
        if (EXTENSION_BLACKLIST.contains(ext)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "不允许上传的文件类型: ." + ext);
        }
    }

    /**
     * 校验 MIME 类型白名单.
     */
    private void checkMimeWhitelist(String mimeType) {
        String allowedTypesStr = fileUploadProperties.getAllowedTypes();
        if (allowedTypesStr == null || allowedTypesStr.isBlank()) {
            return; // 未配置白名单时放行
        }
        Set<String> allowed = new HashSet<>(
                Arrays.asList(allowedTypesStr.split("\\s*,\\s*"))
        );
        if (!allowed.contains(mimeType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "不允许上传的文件类型: " + mimeType);
        }
    }

    /**
     * 读取文件头部字节.
     */
    private byte[] readHeader(MultipartFile file, int len) {
        try (InputStream in = file.getInputStream()) {
            byte[] buf = new byte[len];
            int read = in.read(buf);
            if (read > 0) {
                return Arrays.copyOf(buf, read);
            }
        } catch (IOException e) {
            log.warn("读取文件头失败", e);
        }
        return null;
    }

    /**
     * 检查字节数组是否以指定前缀开头.
     */
    private boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取文件扩展名（含 .）.
     */
    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return filename.substring(dot).toLowerCase();
    }

    /**
     * 将 Hutool FileTypeUtil 返回的类型映射为 MIME 类型.
     */
    private String mapHutoolType(String hutoolType) {
        return switch (hutoolType.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png"  -> "image/png";
            case "pdf"  -> "application/pdf";
            case "xls"  -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "zip"  -> "application/zip";
            default     -> "application/octet-stream";
        };
    }
}
