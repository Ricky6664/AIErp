package com.erp.module.srm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.module.srm.dto.SrmTagDefinitionDTO;
import com.erp.module.srm.dto.SrmTagDefinitionQueryDTO;
import com.erp.module.srm.entity.SrmTagDefinition;
import com.erp.module.srm.mapper.SrmTagDefinitionMapper;
import com.erp.module.srm.service.SrmTagDefinitionService;
import com.erp.module.srm.vo.SrmTagDefinitionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
public class SrmTagDefinitionServiceImpl extends ServiceImpl<SrmTagDefinitionMapper, SrmTagDefinition>
        implements SrmTagDefinitionService {

    private static final Pattern COLOR_PATTERN =
            Pattern.compile("^#[0-9A-Fa-f]{6}$");

    @Override
    @Transactional(readOnly = true)
    public IPage<SrmTagDefinitionVO> list(SrmTagDefinitionQueryDTO query) {
        LambdaQueryWrapper<SrmTagDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTagName()),
                SrmTagDefinition::getTagName, query.getTagName());
        wrapper.eq(StringUtils.hasText(query.getTagGroup()),
                SrmTagDefinition::getTagGroup, query.getTagGroup());
        wrapper.eq(query.getIsActive() != null,
                SrmTagDefinition::getEnableFlag, query.getIsActive());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        wrapper.orderBy(true, isAsc, SrmTagDefinition::getSortNo);

        IPage<SrmTagDefinition> page = page(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    @Transactional(readOnly = true)
    public SrmTagDefinitionVO getById(Long id) {
        SrmTagDefinition entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SrmTagDefinitionDTO dto) {
        validateTagNameUnique(dto.getTagName(), null);
        validateColorFormat(dto.getTagColor());

        SrmTagDefinition entity = new SrmTagDefinition();
        BeanUtils.copyProperties(dto, entity);
        entity.setSortNo(dto.getSortOrder());
        entity.setEnableFlag(dto.getIsActive());
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SrmTagDefinitionDTO dto) {
        SrmTagDefinition existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        validateTagNameUnique(dto.getTagName(), id);
        validateColorFormat(dto.getTagColor());

        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        existing.setSortNo(dto.getSortOrder());
        existing.setEnableFlag(dto.getIsActive());
        updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SrmTagDefinition existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        removeById(id);
    }

    private void validateTagNameUnique(String tagName, Long excludeId) {
        LambdaQueryWrapper<SrmTagDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SrmTagDefinition::getTagName, tagName);
        if (excludeId != null) {
            wrapper.ne(SrmTagDefinition::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "标签名称已存在");
        }
    }

    private void validateColorFormat(String tagColor) {
        if (StringUtils.hasText(tagColor) && !COLOR_PATTERN.matcher(tagColor).matches()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "标签颜色格式错误，应为#RRGGBB格式");
        }
    }

    private SrmTagDefinitionVO toVO(SrmTagDefinition entity) {
        SrmTagDefinitionVO vo = new SrmTagDefinitionVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setSortOrder(entity.getSortNo());
        vo.setIsActive(entity.getEnableFlag());
        return vo;
    }
}
