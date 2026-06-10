package com.erp.engine.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 审核日志Mapper.
 *
 * @author AI
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<SysAuditLogEntity> {

    @Select("SELECT from_status FROM sys_audit_log "
            + "WHERE doc_type = #{docType} AND doc_id = #{docId} "
            + "AND operation_type = 'VOID' "
            + "ORDER BY created_at DESC LIMIT 1")
    Integer findPreviousStatusBeforeVoid(@Param("docType") String docType,
                                         @Param("docId") Long docId);
}
