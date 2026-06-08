package com.erp.module.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.message.dto.MsgTemplateCreateDTO;
import com.erp.module.message.dto.MsgTemplateQueryDTO;
import com.erp.module.message.dto.MsgTemplateUpdateDTO;
import com.erp.module.message.entity.MsgTemplateEntity;
import com.erp.module.message.mapper.MsgTemplateMapper;
import com.erp.module.message.service.IMsgTemplateService;
import com.erp.module.message.vo.MsgTemplateListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 消息模板Service实现.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
public class MsgTemplateServiceImpl
        extends ServiceImpl<MsgTemplateMapper, MsgTemplateEntity>
        implements IMsgTemplateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MsgTemplateCreateDTO dto) {
        validateCreate(dto);
        MsgTemplateEntity entity = new MsgTemplateEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getId();
    }

    @Override
    public PageResult<MsgTemplateListVO> pageList(MsgTemplateQueryDTO query) {
        LambdaQueryWrapper<MsgTemplateEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTemplateCode()),
                MsgTemplateEntity::getTemplateCode, query.getTemplateCode());
        wrapper.like(StringUtils.hasText(query.getTitleTemplate()),
                MsgTemplateEntity::getTitleTemplate, query.getTitleTemplate());
        wrapper.eq(query.getIsEnabled() != null,
                MsgTemplateEntity::getIsEnabled, query.getIsEnabled());
        wrapper.orderByDesc(MsgTemplateEntity::getCreateTime);

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        var page = page(new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(page).convert(this::toListVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MsgTemplateUpdateDTO dto) {
        MsgTemplateEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (StringUtils.hasText(dto.getTitleTemplate())) {
            entity.setTitleTemplate(dto.getTitleTemplate());
        }
        if (dto.getContentTemplate() != null) {
            entity.setContentTemplate(dto.getContentTemplate());
        }
        if (dto.getIsEnabled() != null) {
            entity.setIsEnabled(dto.getIsEnabled());
        }
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        MsgTemplateEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        entity.setIsDeleted(true);
        updateById(entity);
    }

    private void validateCreate(MsgTemplateCreateDTO dto) {
        if (!StringUtils.hasText(dto.getTemplateCode())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
    }

    private MsgTemplateListVO toListVO(MsgTemplateEntity entity) {
        MsgTemplateListVO vo = new MsgTemplateListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
