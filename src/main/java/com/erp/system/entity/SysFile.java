package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件记录表实体.
 *
 * @author AI
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {

    /** 原始文件名 */
    private String originalName;

    /** 存储路径（不含bucket，相对路径如 yyyy/MM/dd/uuid.ext） */
    private String storagePath;

    /** MIME 类型 */
    private String mimeType;

    /** 文件扩展名 */
    private String extension;

    /** 文件大小（字节） */
    private Long sizeBytes;

    /** 所属模块 */
    private String module;

    /** 文件状态：PENDING=待确认 / CONFIRMED=已确认 */
    private String status;

    /** 上传人ID */
    private Long uploaderId;

    /** 下载次数 */
    private Integer downloadCount;
}
