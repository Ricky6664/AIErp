package com.erp.module.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.message.dto.MsgMessageCreateDTO;
import com.erp.module.message.dto.MsgMessageQueryDTO;
import com.erp.module.message.entity.MsgMessageEntity;
import com.erp.module.message.mapper.MsgMessageMapper;
import com.erp.module.message.service.IMsgMessageService;
import com.erp.module.message.vo.MsgMessageListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 消息Service实现.
 *
 * @author AI
 */
@Service
public class MsgMessageServiceImpl
        extends ServiceImpl<MsgMessageMapper, MsgMessageEntity>
        implements IMsgMessageService {

    @Override
    public PageResult<MsgMessageListVO> pageList(MsgMessageQueryDTO query) {
        LambdaQueryWrapper<MsgMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getMessageTitle()),
                MsgMessageEntity::getMessageTitle, query.getMessageTitle());
        wrapper.eq(query.getReadStatus() != null,
                MsgMessageEntity::getReadStatus, query.getReadStatus());
        wrapper.eq(query.getMsgTypeId() != null,
                MsgMessageEntity::getMsgTypeId, query.getMsgTypeId());
        wrapper.eq(query.getReceiverId() != null,
                MsgMessageEntity::getReceiverId, query.getReceiverId());
        wrapper.orderByDesc(MsgMessageEntity::getCreateTime);

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        IPage<MsgMessageEntity> page = page(new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(page).convert(this::toListVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MsgMessageCreateDTO dto) {
        validateCreate(dto);
        MsgMessageEntity entity = new MsgMessageEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setStatus(0);
        save(entity);
        return entity.getId();
    }

    private void validateCreate(MsgMessageCreateDTO dto) {
        if (!StringUtils.hasText(dto.getMessageTitle())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        if (dto.getReceiverId() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
    }

    private MsgMessageListVO toListVO(MsgMessageEntity entity) {
        MsgMessageListVO vo = new MsgMessageListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
