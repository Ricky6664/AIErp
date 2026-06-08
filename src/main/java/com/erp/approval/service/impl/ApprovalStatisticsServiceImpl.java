package com.erp.approval.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.approval.service.IApprovalStatisticsService;
import com.erp.approval.vo.ApprovalStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 审批统计Service实现.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalStatisticsServiceImpl implements IApprovalStatisticsService {

    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalRecordMapper recordMapper;
    private final ApprovalDefinitionMapper definitionMapper;

    @Override
    public ApprovalStatisticsVO getStatistics() {
        Long currentUserId = StpUtil.getLoginIdAsLong();

        List<ApprovalInstanceEntity> allInstances = instanceMapper.selectList(null);
        List<ApprovalDefinitionEntity> allDefinitions = definitionMapper.selectList(null);

        Map<String, Long> statusDistribution = allInstances.stream()
                .collect(Collectors.groupingBy(e -> e.getStatus() != null ? e.getStatus() : "UNKNOWN",
                        Collectors.counting()));

        Map<Long, Long> definitionCounts = allInstances.stream()
                .filter(e -> e.getDefinitionId() != null)
                .collect(Collectors.groupingBy(ApprovalInstanceEntity::getDefinitionId,
                        Collectors.counting()));

        long pendingCount = allInstances.stream()
                .filter(e -> "PENDING".equals(e.getStatus()))
                .count();
        long approvedCount = allInstances.stream()
                .filter(e -> "APPROVED".equals(e.getStatus()))
                .count();
        long rejectedCount = allInstances.stream()
                .filter(e -> "REJECTED".equals(e.getStatus()))
                .count();
        long withdrawnCount = allInstances.stream()
                .filter(e -> "WITHDRAWN".equals(e.getStatus()))
                .count();

        long myPendingCount = allInstances.stream()
                .filter(e -> "PENDING".equals(e.getStatus()) && !currentUserId.equals(e.getApplicantId()))
                .count();
        long mySubmittedCount = allInstances.stream()
                .filter(e -> currentUserId.equals(e.getApplicantId()))
                .count();

        LambdaQueryWrapper<ApprovalRecordEntity> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ApprovalRecordEntity::getApproverId, currentUserId);
        long myReviewedCount = recordMapper.selectCount(recordWrapper);

        ApprovalStatisticsVO vo = new ApprovalStatisticsVO();
        vo.setTotalInstances((long) allInstances.size());
        vo.setPendingCount(pendingCount);
        vo.setApprovedCount(approvedCount);
        vo.setRejectedCount(rejectedCount);
        vo.setWithdrawnCount(withdrawnCount);
        vo.setMyPendingCount(myPendingCount);
        vo.setMyReviewedCount(myReviewedCount);
        vo.setMySubmittedCount(mySubmittedCount);
        vo.setStatusDistribution(statusDistribution);
        vo.setDefinitionCounts(definitionCounts);

        log.info("审批统计查询完成: total={}, pending={}", allInstances.size(), pendingCount);
        return vo;
    }
}
