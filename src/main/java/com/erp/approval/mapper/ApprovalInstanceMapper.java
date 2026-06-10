package com.erp.approval.mapper;

import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.common.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 审批实例Mapper.
 *
 * @author AI
 */
@Mapper
public interface ApprovalInstanceMapper extends BaseMapperX<ApprovalInstanceEntity> {

    Map<String, Object> selectStatistics(@Param("currentUserId") Long currentUserId);

    List<Map<String, Object>> selectStatusDistribution();

    List<Map<String, Object>> selectDefinitionCounts();
}
