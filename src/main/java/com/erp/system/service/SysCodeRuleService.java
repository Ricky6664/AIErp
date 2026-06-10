package com.erp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.BaseCrudService;
import com.erp.system.dto.SysCodeRuleDTO;
import com.erp.system.entity.SysCodeRule;
import com.erp.system.entity.SysCodeRuleSegment;
import com.erp.system.mapper.SysCodeRuleSegmentMapper;
import com.erp.system.vo.SysCodeRuleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 编码规则 Service.
 *
 * @author AI
 * @since 2026-05-29
 */
public abstract class SysCodeRuleService
        extends BaseCrudService<SysCodeRule, SysCodeRuleDTO.CreateDTO, SysCodeRuleDTO.UpdateDTO, SysCodeRuleVO.DetailVO> {

    @Autowired
    protected SysCodeRuleSegmentMapper segmentMapper;

    // ========== 转换方法 ==========

    @Override
    protected SysCodeRule toEntity(SysCodeRuleDTO.CreateDTO dto) {
        SysCodeRule entity = new SysCodeRule();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected void updateEntity(SysCodeRuleDTO.UpdateDTO dto, SysCodeRule entity) {
        if (dto.getRuleName() != null) {
            entity.setRuleName(dto.getRuleName());
        }
        if (dto.getModuleCode() != null) {
            entity.setModuleCode(dto.getModuleCode());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getSeparator() != null) {
            entity.setSeparator(dto.getSeparator());
        }
        if (dto.getIsEnabled() != null) {
            entity.setIsEnabled(dto.getIsEnabled());
        }
    }

    @Override
    protected SysCodeRuleVO.DetailVO toVO(SysCodeRule entity) {
        SysCodeRuleVO.DetailVO vo = new SysCodeRuleVO.DetailVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setIsEnabledName(entity.getIsEnabled() != null && entity.getIsEnabled() == 1 ? "启用" : "停用");
        return vo;
    }

    // ========== CRUD 覆写（含 segment 处理） ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysCodeRuleVO.DetailVO create(SysCodeRuleDTO.CreateDTO dto) {
        validateRuleCodeUnique(dto.getRuleCode(), null);
        validateSegments(dto.getSegments());
        SysCodeRule entity = toEntity(dto);
        save(entity);
        batchSaveSegments(entity.getId(), dto.getSegments());
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysCodeRuleVO.DetailVO update(Long id, SysCodeRuleDTO.UpdateDTO dto) {
        SysCodeRule entity = getBaseMapper().selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        updateEntity(dto, entity);
        updateById(entity);

        if (dto.getSegments() != null) {
            validateSegments(dto.getSegments());
            deleteSegmentsByRuleId(id);
            batchSaveSegments(id, dto.getSegments());
        }

        SysCodeRuleVO.DetailVO vo = toVO(entity);
        vo.setSegments(querySegmentsByRuleId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        deleteSegmentsByRuleId(id);
        return removeById(id);
    }

    @Override
    public SysCodeRuleVO.DetailVO getById(Long id) {
        SysCodeRule entity = getBaseMapper().selectById(id);
        if (entity == null) {
            return null;
        }
        SysCodeRuleVO.DetailVO vo = toVO(entity);
        vo.setSegments(querySegmentsByRuleId(id));
        return vo;
    }

    // ========== Segment 辅助方法 ==========

    protected void batchSaveSegments(Long ruleId, List<SysCodeRuleDTO.SegmentDTO> segmentDTOs) {
        if (segmentDTOs == null || segmentDTOs.isEmpty()) {
            return;
        }
        List<SysCodeRuleSegment> segments = new ArrayList<>(segmentDTOs.size());
        for (SysCodeRuleDTO.SegmentDTO dto : segmentDTOs) {
            SysCodeRuleSegment seg = new SysCodeRuleSegment();
            BeanUtils.copyProperties(dto, seg);
            seg.setRuleId(ruleId);
            segments.add(seg);
        }
        segmentMapper.insertBatch(segments, 50);
    }

    protected void deleteSegmentsByRuleId(Long ruleId) {
        LambdaQueryWrapper<SysCodeRuleSegment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCodeRuleSegment::getRuleId, ruleId);
        segmentMapper.delete(wrapper);
    }

    protected List<SysCodeRuleVO.SegmentVO> querySegmentsByRuleId(Long ruleId) {
        LambdaQueryWrapper<SysCodeRuleSegment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCodeRuleSegment::getRuleId, ruleId);
        wrapper.orderByAsc(SysCodeRuleSegment::getSegmentOrder);
        List<SysCodeRuleSegment> segments = segmentMapper.selectList(wrapper);
        if (segments == null || segments.isEmpty()) {
            return new ArrayList<>();
        }
        return segments.stream().map(seg -> {
            SysCodeRuleVO.SegmentVO vo = new SysCodeRuleVO.SegmentVO();
            BeanUtils.copyProperties(seg, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    // ========== 唯一性校验 ==========

    protected void validateRuleCodeUnique(String ruleCode, Long excludeId) {
        LambdaQueryWrapper<SysCodeRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCodeRule::getRuleCode, ruleCode);
        if (excludeId != null) {
            wrapper.ne(SysCodeRule::getId, excludeId);
        }
        if (getBaseMapper().selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "规则编码已存在: " + ruleCode);
        }
    }

    // ========== 段数据校验 ==========

    private static final Set<Integer> VALID_SEGMENT_TYPES = Set.of(1, 2, 3, 4);

    protected void validateSegments(List<SysCodeRuleDTO.SegmentDTO> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "编码段不能为空");
        }
        Set<Integer> orders = new HashSet<>();
        for (int i = 0; i < segments.size(); i++) {
            SysCodeRuleDTO.SegmentDTO seg = segments.get(i);
            if (seg.getSegmentType() == null || !VALID_SEGMENT_TYPES.contains(seg.getSegmentType())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                        "编码段[" + i + "]类型无效: " + seg.getSegmentType());
            }
            if (seg.getSegmentOrder() == null) {
                throw new BusinessException(ErrorCode.PARAM_MISSING,
                        "编码段[" + i + "]排序号不能为空");
            }
            if (!orders.add(seg.getSegmentOrder())) {
                throw new BusinessException(ErrorCode.PARAM_DUPLICATE,
                        "编码段排序号重复: " + seg.getSegmentOrder());
            }
            if (seg.getSegmentType() == 1 && (seg.getSegmentValue() == null || seg.getSegmentValue().isEmpty())) {
                throw new BusinessException(ErrorCode.PARAM_MISSING,
                        "固定段[" + i + "]的值不能为空");
            }
            if (seg.getSegmentType() == 3 && seg.getSegmentLength() == null) {
                throw new BusinessException(ErrorCode.PARAM_MISSING,
                        "序列段[" + i + "]的长度不能为空");
            }
        }
    }

    // ========== 自定义方法（子类实现） ==========

    /**
     * 预览编码.
     *
     * @param ruleId 规则ID
     * @return 预览编码字符串
     */
    public abstract String preview(Long ruleId);

    /**
     * 生成编码（内部加分布式锁）.
     *
     * @param ruleCode 规则编码
     * @return 生成的编码字符串
     */
    public abstract String generate(String ruleCode);

    /**
     * 刷新Redis缓存.
     *
     * @param ruleCode 规则编码
     */
    public abstract void refreshCache(String ruleCode);
}
