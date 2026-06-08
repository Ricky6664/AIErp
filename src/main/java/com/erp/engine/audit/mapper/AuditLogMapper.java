package com.erp.engine.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审核日志Mapper.
 *
 * @author AI
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<SysAuditLogEntity> {
}
