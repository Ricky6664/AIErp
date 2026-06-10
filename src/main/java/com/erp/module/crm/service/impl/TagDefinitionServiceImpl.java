package com.erp.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.crm.dto.TagDefinitionDTO;
import com.erp.module.crm.dto.TagDefinitionQueryDTO;
import com.erp.module.crm.entity.TagDefinitionEntity;
import com.erp.module.crm.mapper.TagDefinitionMapper;
import com.erp.module.crm.service.TagDefinitionService;
import com.erp.module.crm.vo.TagDefinitionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签定义Service实现类.
 *
 * @author AI
 */
@Service
public class TagDefinitionServiceImpl extends ServiceImpl<TagDefinitionMapper, TagDefinitionEntity>
        implements TagDefinitionService {

    @Override
    @Transactional(readOnly = true)
    public PageResult<TagDefinitionVO> list(TagDefinitionQueryDTO query) {
        LambdaQueryWrapper<TagDefinitionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTagName()), TagDefinitionEntity::getTagName, query.getTagName());
        wrapper.eq(StringUtils.hasText(query.getTagGroup()), TagDefinitionEntity::getTagGroup, query.getTagGroup());
        wrapper.eq(query.getIsActive() != null, TagDefinitionEntity::getIsActive, query.getIsActive());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if ("createTime".equals(query.getSortField())) {
            wrapper.orderBy(true, isAsc, TagDefinitionEntity::getCreateTime);
        } else {
            wrapper.orderBy(true, isAsc, TagDefinitionEntity::getSortOrder);
        }

        IPage<TagDefinitionEntity> page = page(query.toPage(), wrapper);
        List<TagDefinitionVO> voList = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public TagDefinitionVO getById(Long id) {
        TagDefinitionEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "标签定义不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "新增", description = "新增标签定义")
    public void save(TagDefinitionDTO dto) {
        validateTagNameUniqueness(dto.getTagName(), null);
        validateTagColor(dto.getTagColor());

        TagDefinitionEntity entity = new TagDefinitionEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "修改", description = "修改标签定义")
    public void update(Long id, TagDefinitionDTO dto) {
        TagDefinitionEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "标签定义不存在");
        }

        validateTagNameUniqueness(dto.getTagName(), id);
        validateTagColor(dto.getTagColor());

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "CRM客户管理", action = "删除", description = "删除标签定义")
    public void delete(Long id) {
        TagDefinitionEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "标签定义不存在");
        }

        if (baseMapper.countTagReferences(id) > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该标签已被客户引用，无法删除");
        }

        removeById(id);
    }

    private void validateTagNameUniqueness(String tagName, Long excludeId) {
        LambdaQueryWrapper<TagDefinitionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagDefinitionEntity::getTagName, tagName);
        if (excludeId != null) {
            wrapper.ne(TagDefinitionEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "标签名称已存在");
        }
    }

    private void validateTagColor(String tagColor) {
        if (StringUtils.hasText(tagColor) && !tagColor.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR, "标签颜色格式不正确，应为#RRGGBB");
        }
    }

    private TagDefinitionVO toVO(TagDefinitionEntity entity) {
        TagDefinitionVO vo = new TagDefinitionVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
