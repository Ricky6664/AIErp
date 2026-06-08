package com.erp.approval.mapper;

import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.common.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 审批记录Mapper.
 *
 * @author AI
 */
@Mapper
public interface ApprovalRecordMapper extends BaseMapperX<ApprovalRecordEntity> {

    /**
     * 分页查询审批日志（JOIN instance + definition）.
     *
     * @param definitionId 审批定义ID（可选）
     * @param action       审批动作（可选）
     * @param status       实例状态（可选）
     * @param businessType 业务类型（可选）
     * @param applicantId  申请人ID（可选）
     * @param approverId   审批人ID（可选）
     * @param startTime    开始时间（可选）
     * @param endTime      结束时间（可选）
     * @param offset       分页偏移
     * @param limit        分页大小
     * @return 日志列表
     */
    List<Map<String, Object>> selectLogList(@Param("definitionId") Long definitionId,
                                            @Param("action") String action,
                                            @Param("status") String status,
                                            @Param("businessType") String businessType,
                                            @Param("applicantId") Long applicantId,
                                            @Param("approverId") Long approverId,
                                            @Param("startTime") String startTime,
                                            @Param("endTime") String endTime,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);

    /**
     * 统计审批日志数量（与selectLogList条件一致）.
     */
    long selectLogCount(@Param("definitionId") Long definitionId,
                        @Param("action") String action,
                        @Param("status") String status,
                        @Param("businessType") String businessType,
                        @Param("applicantId") Long applicantId,
                        @Param("approverId") Long approverId,
                        @Param("startTime") String startTime,
                        @Param("endTime") String endTime);

    /**
     * 按实例ID查询完整日志（审批链路）.
     *
     * @param instanceId 审批实例ID
     * @return 日志列表（按时间升序）
     */
    List<Map<String, Object>> selectLogByInstanceId(@Param("instanceId") Long instanceId);
}
