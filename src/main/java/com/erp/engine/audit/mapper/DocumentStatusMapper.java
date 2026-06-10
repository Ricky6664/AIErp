package com.erp.engine.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 单据状态Mapper.
 *
 * @author AI
 */
@Mapper
public interface DocumentStatusMapper extends BaseMapper<DocumentStatusEntity> {

    /**
     * SELECT FOR UPDATE 行级锁查询.
     */
    @Select("SELECT * FROM sys_document_status WHERE doc_type = #{docType} AND doc_id = #{docId} FOR UPDATE")
    DocumentStatusEntity selectForUpdate(@Param("docType") String docType, @Param("docId") Long docId);

    /**
     * 更新单据状态.
     */
    @Update("UPDATE sys_document_status SET status = #{status}, update_time = NOW() WHERE doc_type = #{docType} AND doc_id = #{docId}")
    int updateStatus(@Param("docType") String docType, @Param("docId") Long docId, @Param("status") Integer status);
}
