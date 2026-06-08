package com.erp.system.announcement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.system.announcement.dto.AnnouncementCreateDTO;
import com.erp.system.announcement.dto.AnnouncementQueryDTO;
import com.erp.system.announcement.dto.AnnouncementUpdateDTO;
import com.erp.system.announcement.entity.AnnouncementEntity;
import com.erp.system.announcement.entity.AnnouncementReadEntity;
import com.erp.system.announcement.mapper.AnnouncementMapper;
import com.erp.system.announcement.mapper.AnnouncementReadMapper;
import com.erp.system.announcement.service.IAnnouncementService;
import com.erp.system.announcement.vo.AnnouncementVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementEntity>
        implements IAnnouncementService {

    private final AnnouncementReadMapper announcementReadMapper;

    public AnnouncementServiceImpl(AnnouncementReadMapper announcementReadMapper) {
        this.announcementReadMapper = announcementReadMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "系统公告", action = "新增", description = "新增系统公告")
    public AnnouncementVO create(AnnouncementCreateDTO dto) {
        AnnouncementEntity entity = new AnnouncementEntity();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getIsTop() == null) {
            entity.setIsTop(false);
        }
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "系统公告", action = "修改", description = "修改系统公告")
    public AnnouncementVO update(Long id, AnnouncementUpdateDTO dto) {
        AnnouncementEntity existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "公告不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "系统公告", action = "删除", description = "删除系统公告")
    public void delete(Long id) {
        AnnouncementEntity existing = getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "公告不存在");
        }
        removeById(id);
    }

    @Override
    public PageResult<AnnouncementVO> pageList(AnnouncementQueryDTO query) {
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTitle()),
                AnnouncementEntity::getTitle, query.getTitle());
        wrapper.eq(StringUtils.hasText(query.getAnnouncementType()),
                AnnouncementEntity::getAnnouncementType, query.getAnnouncementType());
        wrapper.eq(query.getStatus() != null,
                AnnouncementEntity::getStatus, query.getStatus());
        wrapper.eq(query.getIsTop() != null,
                AnnouncementEntity::getIsTop, query.getIsTop());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "title" -> AnnouncementEntity::getTitle;
                case "publishTime" -> AnnouncementEntity::getPublishTime;
                case "createTime" -> AnnouncementEntity::getCreateTime;
                case "updateTime" -> AnnouncementEntity::getUpdateTime;
                default -> AnnouncementEntity::getCreateTime;
            });
        } else {
            wrapper.orderByDesc(AnnouncementEntity::getIsTop);
            wrapper.orderByDesc(AnnouncementEntity::getPublishTime);
        }

        IPage<AnnouncementEntity> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public List<AnnouncementVO> getUnreadList() {
        Long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementEntity::getStatus, 2);
        wrapper.orderByDesc(AnnouncementEntity::getIsTop);
        wrapper.orderByDesc(AnnouncementEntity::getPublishTime);
        List<AnnouncementEntity> publishedList = list(wrapper);

        if (publishedList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> readAnnouncementIds = announcementReadMapper.selectList(
                new LambdaQueryWrapper<AnnouncementReadEntity>()
                        .eq(AnnouncementReadEntity::getUserId, userId)
                        .in(AnnouncementReadEntity::getAnnouncementId,
                                publishedList.stream().map(AnnouncementEntity::getId).collect(Collectors.toList()))
        ).stream().map(AnnouncementReadEntity::getAnnouncementId).collect(Collectors.toSet());

        return publishedList.stream()
                .filter(e -> !readAnnouncementIds.contains(e.getId()))
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "系统公告", action = "阅读", description = "标记公告已读")
    public void markAsRead(Long announcementId) {
        AnnouncementEntity announcement = getById(announcementId);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "公告不存在");
        }

        Long userId = StpUtil.getLoginIdAsLong();

        Long count = announcementReadMapper.selectCount(
                new LambdaQueryWrapper<AnnouncementReadEntity>()
                        .eq(AnnouncementReadEntity::getAnnouncementId, announcementId)
                        .eq(AnnouncementReadEntity::getUserId, userId));
        if (count > 0) {
            return;
        }

        AnnouncementReadEntity readRecord = new AnnouncementReadEntity();
        readRecord.setAnnouncementId(announcementId);
        readRecord.setUserId(userId);
        readRecord.setReadTime(LocalDateTime.now());
        announcementReadMapper.insert(readRecord);
    }

    private AnnouncementVO toVO(AnnouncementEntity entity) {
        AnnouncementVO vo = new AnnouncementVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
