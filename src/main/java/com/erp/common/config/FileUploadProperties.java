package com.erp.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置属性.
 *
 * @author AI
 * @since 2026-05-30
 */
@Component
@ConfigurationProperties(prefix = "file.upload")
@Data
public class FileUploadProperties {

    /** 允许的MIME类型白名单（逗号分隔） */
    private String allowedTypes;

    /** 文件上传存储根路径 */
    private String path;
}
