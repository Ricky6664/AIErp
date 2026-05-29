package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传结果视图对象.
 *
 * @author AI
 * @since 2026-05-30
 */
@Schema(description = "文件上传结果视图对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件访问URL")
    private String url;

    @Schema(description = "文件原始名称")
    private String originalName;

    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "文件MIME类型")
    private String type;
}
