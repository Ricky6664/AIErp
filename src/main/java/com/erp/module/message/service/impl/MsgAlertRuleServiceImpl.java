package com.erp.module.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.message.dto.MsgAlertRuleCreateDTO;
import com.erp.module.message.dto.MsgAlertRuleQueryDTO;
import com.erp.module.message.dto.MsgAlertRuleUpdateDTO;
import com.erp.module.message.entity.MsgAlertRuleEntity;
import com.erp.module.message.mapper.MsgAlertRuleMapper;
import com.erp.module.message.service.IMsgAlertRuleService;
import com.erp.module.message.vo.MsgAlertRuleListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 业务预警规则Service实现.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
public class MsgAlertRuleServiceImpl
        extends ServiceImpl<MsgAlertRuleMapper, MsgAlertRuleEntity>
        implements IMsgAlertRuleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MsgAlertRuleCreateDTO dto) {
        validateCreate(dto);
        MsgAlertRuleEntity entity = convertToEntity(dto);
        entity.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);
        save(entity);
        return entity.getId();
    }

    @Override
    public PageResult<MsgAlertRuleListVO> pageList(MsgAlertRuleQueryDTO query) {
        LambdaQueryWrapper<MsgAlertRuleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getRuleName()),
                MsgAlertRuleEntity::getRuleName, query.getRuleName());
        wrapper.eq(query.getIsEnabled() != null,
                MsgAlertRuleEntity::getIsEnabled, query.getIsEnabled());
        wrapper.orderByDesc(MsgAlertRuleEntity::getCreateTime);

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        IPage<MsgAlertRuleEntity> page = page(new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(page).convert(this::toListVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MsgAlertRuleUpdateDTO dto) {
        MsgAlertRuleEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (StringUtils.hasText(dto.getRuleName())) {
            entity.setRuleName(dto.getRuleName());
        }
        if (dto.getConditionExpression() != null) {
            entity.setConditionExpression(dto.getConditionExpression());
        }
        if (dto.getThreshold() != null) {
            entity.setThreshold(dto.getThreshold());
        }
        if (StringUtils.hasText(dto.getFrequency())) {
            entity.setFrequency(dto.getFrequency());
        }
        if (dto.getTriggerActionConfig() != null) {
            entity.setTriggerActionConfig(dto.getTriggerActionConfig());
        }
        if (dto.getIsEnabled() != null) {
            entity.setIsEnabled(dto.getIsEnabled());
        }
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        MsgAlertRuleEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        entity.setIsDeleted(true);
        updateById(entity);
    }

    private void validateCreate(MsgAlertRuleCreateDTO dto) {
        if (!StringUtils.hasText(dto.getRuleName())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
    }

    private MsgAlertRuleEntity convertToEntity(MsgAlertRuleCreateDTO dto) {
        MsgAlertRuleEntity entity = new MsgAlertRuleEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private MsgAlertRuleListVO toListVO(MsgAlertRuleEntity entity) {
        MsgAlertRuleListVO vo = new MsgAlertRuleListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
