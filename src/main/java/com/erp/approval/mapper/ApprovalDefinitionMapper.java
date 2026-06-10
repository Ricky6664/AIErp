package com.erp.approval.mapper;

import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.common.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 审批定义Mapper.
 *
 * @author AI
 */
@Mapper
public interface ApprovalDefinitionMapper extends BaseMapperX<ApprovalDefinitionEntity> {

    /**
     * 批量插入预置审批定义（跳过已存在的code）.
     *
     * @param list 预置审批定义列表
     * @return 插入行数
     */
    int batchInsertPreset(@Param("list") List<ApprovalDefinitionEntity> list);

    /**
     * 按定义编码查询（不含逻辑删除）.
     *
     * @param definitionCode 定义编码
     * @return 审批定义实体，不存在返回null
     */
    ApprovalDefinitionEntity selectByDefinitionCode(@Param("definitionCode") String definitionCode);

    /**
     * 查询所有启用的审批定义.
     *
     * @return 审批定义列表
     */
    List<ApprovalDefinitionEntity> selectAllEnabled();
}
