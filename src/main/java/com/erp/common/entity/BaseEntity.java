package com.erp.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类, 定义通用字段.
 *
 * <p>所有业务实体必须继承本类, 自动获得主键、多租户、审计、逻辑删除、乐观锁等字段.
 * 新增时自动填充通过 {@link com.erp.config.MyMetaObjectHandler} 完成.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long creatorId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updaterId;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean isDeleted;

    private Long ownerDeptId;

    private Long ownerId;

    @Version
    private Integer version;
}
