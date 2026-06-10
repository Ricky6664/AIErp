package com.erp.system.announcement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告已读记录实体.
 *
 * @author AI
 */
@Data
@TableName("sys_announcement_read")
public class AnnouncementReadEntity {

    private Long id;

    private Long tenantId;

    private Long announcementId;

    private Long userId;

    private LocalDateTime readTime;

    private LocalDateTime createTime;
}
