package com.erp.engine.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审核配置Mapper.
 *
 * @author AI
 */
@Mapper
public interface AuditConfigMapper extends BaseMapper<SysAuditConfigEntity> {
}
