package com.erp.module.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.utils.SecurityUtils;
import com.erp.module.message.dto.MsgMessageCreateDTO;
import com.erp.module.message.entity.MsgAlertRuleEntity;
import com.erp.module.message.entity.MsgWarningEntity;
import com.erp.module.message.mapper.MsgAlertRuleMapper;
import com.erp.module.message.mapper.MsgWarningMapper;
import com.erp.module.message.service.IMsgMessageService;
import com.erp.module.message.service.MsgWarningService;
import com.erp.module.message.service.MsgWebSocketService;
import com.erp.module.message.vo.WarningDashboardVO;
import com.erp.module.message.vo.WarningListItemVO;
import com.erp.module.message.warning.WarningConditionEvaluator;
import com.erp.module.message.warning.WarningMatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 业务预警触发Service实现.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MsgWarningServiceImpl implements MsgWarningService {

    private final MsgAlertRuleMapper ruleMapper;
    private final MsgWarningMapper warningMapper;
    private final IMsgMessageService messageService;
    private final MsgWebSocketService webSocketService;
    private final WarningConditionEvaluator conditionEvaluator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "${msg.warning.scan-cron:0 0 * * * ?}")
    public void scanAndAlert() {
        log.info("[预警扫描] 开始执行预警规则扫描");
        List<MsgAlertRuleEntity> rules = ruleMapper.selectList(
                new LambdaQueryWrapper<MsgAlertRuleEntity>()
                        .eq(MsgAlertRuleEntity::getIsEnabled, true));
        int alertCount = 0;

        for (MsgAlertRuleEntity rule : rules) {
            try {
                List<WarningMatchResult> matches = conditionEvaluator.evaluate(rule);
                for (WarningMatchResult match : matches) {
                    if (warningMapper.existsToday(rule.getId(), match.getBusinessId())) {
                        continue;
                    }
                    MsgWarningEntity warning = buildWarning(rule, match);
                    warningMapper.insert(warning);

                    MsgMessageCreateDTO msgDto = new MsgMessageCreateDTO();
                    msgDto.setMessageTitle(warning.getTitle());
                    msgDto.setMessageContent(warning.getContent());
                    msgDto.setMsgTypeId(getMsgTypeId(rule));
                    msgDto.setReceiverId(getNotifyUserId(rule));
                    messageService.create(msgDto);

                    webSocketService.pushToRole(getNotifyRole(rule),
                            buildWarningPushMessage(warning));
                    alertCount++;
                }
            } catch (Exception e) {
                log.error("[预警扫描] 规则{}执行失败", rule.getRuleName(), e);
            }
        }
        log.info("[预警扫描] 完成，本次生成{}条预警", alertCount);
    }

    @Override
    public WarningDashboardVO getWarningDashboard(String module) {
        WarningDashboardVO vo = new WarningDashboardVO();
        vo.setModuleCounts(warningMapper.countByModule());
        vo.setTrendData(warningMapper.selectTrend(LocalDate.now().minusDays(30)));
        vo.setDistributionData(warningMapper.selectDistribution());
        List<MsgWarningEntity> warningList = warningMapper.selectWarningList(module);
        vo.setWarningList(warningList.stream().map(this::toListItemVO).toList());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWarning(Long warningId) {
        MsgWarningEntity warning = warningMapper.selectById(warningId);
        if (warning == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(warning.getIsHandled())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID);
        }
        warning.setIsHandled(true);
        warning.setHandleTime(LocalDateTime.now());
        warning.setHandlerId(SecurityUtils.getCurrentUserId());
        warningMapper.updateById(warning);
    }

    private MsgWarningEntity buildWarning(MsgAlertRuleEntity rule, WarningMatchResult match) {
        MsgWarningEntity entity = new MsgWarningEntity();
        entity.setRuleId(rule.getId());
        entity.setBusinessId(match.getBusinessId());
        entity.setTitle(match.getTitle());
        entity.setContent(match.getContent());
        entity.setWarningType(match.getModule());
        entity.setIsHandled(false);
        return entity;
    }

    private String buildWarningPushMessage(MsgWarningEntity warning) {
        return "{\"type\":\"warning\",\"id\":" + warning.getId()
                + ",\"title\":\"" + warning.getTitle() + "\"}";
    }

    private String getNotifyRole(MsgAlertRuleEntity rule) {
        try {
            return rule.getTriggerActionConfig() != null ? rule.getTriggerActionConfig() : "admin";
        } catch (Exception e) {
            return "admin";
        }
    }

    private Long getNotifyUserId(MsgAlertRuleEntity rule) {
        return SecurityUtils.getCurrentUserId();
    }

    private Long getMsgTypeId(MsgAlertRuleEntity rule) {
        return 0L;
    }

    private WarningListItemVO toListItemVO(MsgWarningEntity entity) {
        WarningListItemVO vo = new WarningListItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
