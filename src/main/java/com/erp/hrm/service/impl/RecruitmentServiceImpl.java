package com.erp.hrm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.hrm.dto.RecruitmentCreateDTO;
import com.erp.hrm.dto.RecruitmentQueryDTO;
import com.erp.hrm.dto.RecruitmentUpdateDTO;
import com.erp.hrm.entity.RecruitmentEntity;
import com.erp.hrm.mapper.RecruitmentMapper;
import com.erp.hrm.service.IRecruitmentService;
import com.erp.hrm.vo.RecruitmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class RecruitmentServiceImpl extends ServiceImpl<RecruitmentMapper, RecruitmentEntity>
        implements IRecruitmentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "招聘管理", action = "新增", description = "新增招聘")
    public RecruitmentVO create(RecruitmentCreateDTO dto) {
        validateDeadline(dto.getDeadline());
        RecruitmentEntity entity = new RecruitmentEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setRecruitStatus("recruiting");
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "招聘管理", action = "修改", description = "修改招聘")
    public RecruitmentVO update(Long id, RecruitmentUpdateDTO dto) {
        RecruitmentEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "招聘信息不存在");
        }
        if ("completed".equals(existing.getRecruitStatus())
                || "cancelled".equals(existing.getRecruitStatus())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID,
                    "已完成或已取消的招聘不允许修改");
        }
        validateDeadline(dto.getDeadline());
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "招聘管理", action = "删除", description = "删除招聘")
    public void delete(Long id) {
        RecruitmentEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "招聘信息不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RecruitmentVO getById(Long id) {
        RecruitmentEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "招聘信息不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<RecruitmentVO> pageList(RecruitmentQueryDTO query) {
        LambdaQueryWrapper<RecruitmentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getDepartmentId() != null,
                RecruitmentEntity::getDepartmentId, query.getDepartmentId());
        wrapper.eq(query.getPositionId() != null,
                RecruitmentEntity::getPositionId, query.getPositionId());
        wrapper.eq(StringUtils.hasText(query.getRecruitStatus()),
                RecruitmentEntity::getRecruitStatus, query.getRecruitStatus());
        wrapper.like(StringUtils.hasText(query.getKeyword()),
                RecruitmentEntity::getRequirement, query.getKeyword());
        wrapper.orderByDesc(RecruitmentEntity::getCreateTime);

        Page<RecruitmentEntity> page = new Page<>(
                query.getPageNum() != null ? query.getPageNum() : 1,
                query.getPageSize() != null ? query.getPageSize() : 10);
        IPage<RecruitmentEntity> result = page(page, wrapper);
        return PageResult.of(result.convert(this::toVO));
    }

    private void validateDeadline(LocalDate deadline) {
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.PARAM_RANGE_ERROR,
                    "截止日期不得早于当前日期");
        }
    }

    private RecruitmentVO toVO(RecruitmentEntity entity) {
        RecruitmentVO vo = new RecruitmentVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
