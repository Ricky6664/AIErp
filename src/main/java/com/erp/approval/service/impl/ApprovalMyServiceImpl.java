package com.erp.approval.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.approval.dto.MyApprovalQueryDTO;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.approval.service.IApprovalMyService;
import com.erp.approval.vo.MyApprovalVO;
import com.erp.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 我的审批Service实现.
 * 支持待审(pending)、已审(reviewed)、我的申请(submitted)三个维度查询.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalMyServiceImpl implements IApprovalMyService {

    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalRecordMapper recordMapper;
    private final ApprovalDefinitionMapper definitionMapper;

    @Override
    public PageResult<MyApprovalVO> pageList(MyApprovalQueryDTO query) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        String tab = query.getTab() != null ? query.getTab() : "pending";
        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;

        switch (tab) {
            case "reviewed":
                return queryReviewed(currentUserId, pageNum, pageSize);
            case "submitted":
                return querySubmitted(currentUserId, pageNum, pageSize);
            default:
                return queryPending(currentUserId, pageNum, pageSize);
        }
    }

    private PageResult<MyApprovalVO> queryPending(Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<ApprovalInstanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstanceEntity::getStatus, "PENDING");
        wrapper.ne(ApprovalInstanceEntity::getApplicantId, userId);
        wrapper.orderByDesc(ApprovalInstanceEntity::getCreateTime);

        IPage<ApprovalInstanceEntity> page = instanceMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        if (page.getRecords().isEmpty()) {
            return PageResult.empty();
        }

        List<ApprovalInstanceEntity> records = page.getRecords();
        Set<Long> instanceIds = records.stream()
                .map(ApprovalInstanceEntity::getId).collect(Collectors.toSet());

        LambdaQueryWrapper<ApprovalRecordEntity> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.in(ApprovalRecordEntity::getInstanceId, instanceIds);
        recordWrapper.eq(ApprovalRecordEntity::getApproverId, userId);
        Set<Long> actedInstanceIds = recordMapper.selectList(recordWrapper).stream()
                .map(ApprovalRecordEntity::getInstanceId)
                .collect(Collectors.toSet());

        List<MyApprovalVO> voList = records.stream()
                .filter(r -> !actedInstanceIds.contains(r.getId()))
                .map(e -> toVO(e, null))
                .collect(Collectors.toList());

        return PageResult.of(voList, (long) voList.size(), pageNum, pageSize);
    }

    private PageResult<MyApprovalVO> queryReviewed(Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<ApprovalRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalRecordEntity::getApproverId, userId);
        wrapper.orderByDesc(ApprovalRecordEntity::getOperateTime);

        IPage<ApprovalRecordEntity> page = recordMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        if (page.getRecords().isEmpty()) {
            return PageResult.empty();
        }

        List<Long> instanceIds = page.getRecords().stream()
                .map(ApprovalRecordEntity::getInstanceId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, ApprovalInstanceEntity> instanceMap = instanceMapper.selectBatchIds(instanceIds).stream()
                .collect(Collectors.toMap(ApprovalInstanceEntity::getId, e -> e));

        List<MyApprovalVO> voList = page.getRecords().stream()
                .map(r -> toVO(instanceMap.get(r.getInstanceId()), r))
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), pageNum, pageSize);
    }

    private PageResult<MyApprovalVO> querySubmitted(Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<ApprovalInstanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstanceEntity::getApplicantId, userId);
        wrapper.orderByDesc(ApprovalInstanceEntity::getCreateTime);

        IPage<ApprovalInstanceEntity> page = instanceMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        if (page.getRecords().isEmpty()) {
            return PageResult.empty();
        }

        List<MyApprovalVO> voList = page.getRecords().stream()
                .map(e -> toVO(e, null))
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), pageNum, pageSize);
    }

    private MyApprovalVO toVO(ApprovalInstanceEntity instance, ApprovalRecordEntity record) {
        MyApprovalVO vo = new MyApprovalVO();
        if (instance != null) {
            vo.setInstanceId(instance.getId());
            vo.setDefinitionId(instance.getDefinitionId());
            vo.setBusinessType(instance.getBusinessType());
            vo.setBusinessId(instance.getBusinessId());
            vo.setApplicantId(instance.getApplicantId());
            vo.setStatus(instance.getStatus());
            vo.setCreateTime(instance.getCreateTime());

            ApprovalDefinitionEntity definition = definitionMapper.selectById(instance.getDefinitionId());
            if (definition != null) {
                vo.setDefinitionName(definition.getDefinitionName());
            }
        }
        if (record != null) {
            vo.setMyAction(record.getAction());
            vo.setMyComment(record.getComment());
            vo.setMyOperateTime(record.getOperateTime());
            vo.setCurrentNodeName(record.getNodeName());
            if (instance == null) {
                vo.setInstanceId(record.getInstanceId());
            }
        }
        return vo;
    }
}
