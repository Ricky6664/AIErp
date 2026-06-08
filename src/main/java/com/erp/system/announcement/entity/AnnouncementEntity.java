package com.erp.system.announcement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统公告实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_announcement")
public class AnnouncementEntity extends BaseEntity {

    private String title;

    private String content;

    private String announcementType;

    private java.time.LocalDateTime publishTime;

    private Boolean isTop;

    private Integer status;
}
