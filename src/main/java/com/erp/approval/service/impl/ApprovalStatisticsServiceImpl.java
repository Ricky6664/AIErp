package com.erp.approval.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.approval.service.IApprovalStatisticsService;
import com.erp.approval.vo.ApprovalStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批统计Service实现（SQL聚合，避免全量查表后在内存计算）.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalStatisticsServiceImpl implements IApprovalStatisticsService {

    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalRecordMapper recordMapper;

    @Override
    public ApprovalStatisticsVO getStatistics() {
        Long currentUserId = StpUtil.getLoginIdAsLong();

        Map<String, Object> stats = instanceMapper.selectStatistics(currentUserId);

        List<Map<String, Object>> statusRows = instanceMapper.selectStatusDistribution();
        Map<String, Long> statusDistribution = new LinkedHashMap<>();
        for (Map<String, Object> row : statusRows) {
            String name = (String) row.get("name");
            Object value = row.get("value");
            statusDistribution.put(name != null ? name : "UNKNOWN",
                    value instanceof Number ? ((Number) value).longValue() : 0L);
        }

        List<Map<String, Object>> defRows = instanceMapper.selectDefinitionCounts();
        Map<Long, Long> definitionCounts = new LinkedHashMap<>();
        for (Map<String, Object> row : defRows) {
            Object defId = row.get("definition_id");
            Object cnt = row.get("cnt");
            if (defId instanceof Number) {
                definitionCounts.put(((Number) defId).longValue(),
                        cnt instanceof Number ? ((Number) cnt).longValue() : 0L);
            }
        }

        long myReviewedCount = recordMapper.countByApproverId(currentUserId);

        ApprovalStatisticsVO vo = new ApprovalStatisticsVO();
        vo.setTotalInstances(toLong(stats.get("total_instances")));
        vo.setPendingCount(toLong(stats.get("pending_count")));
        vo.setApprovedCount(toLong(stats.get("approved_count")));
        vo.setRejectedCount(toLong(stats.get("rejected_count")));
        vo.setWithdrawnCount(toLong(stats.get("withdrawn_count")));
        vo.setMyPendingCount(toLong(stats.get("my_pending_count")));
        vo.setMyReviewedCount(myReviewedCount);
        vo.setMySubmittedCount(toLong(stats.get("my_submitted_count")));
        vo.setStatusDistribution(statusDistribution);
        vo.setDefinitionCounts(definitionCounts);

        log.info("审批统计查询完成: total={}, pending={}", vo.getTotalInstances(), vo.getPendingCount());
        return vo;
    }

    private long toLong(Object val) {
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return 0L;
    }
}
