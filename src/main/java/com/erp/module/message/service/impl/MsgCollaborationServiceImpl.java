package com.erp.module.message.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.common.utils.SecurityUtils;
import com.erp.module.message.dto.CollaborationCreateDTO;
import com.erp.module.message.dto.CollaborationQueryDTO;
import com.erp.module.message.dto.MsgMessageCreateDTO;
import com.erp.module.message.dto.ReplyCreateDTO;
import com.erp.module.message.entity.MsgCollaborationEntity;
import com.erp.module.message.entity.MsgCollaborationReplyEntity;
import com.erp.module.message.mapper.MsgCollaborationMapper;
import com.erp.module.message.mapper.MsgCollaborationReplyMapper;
import com.erp.module.message.service.IMsgMessageService;
import com.erp.module.message.service.MsgCollaborationService;
import com.erp.module.message.service.MsgWebSocketService;
import com.erp.module.message.util.MentionParser;
import com.erp.module.message.vo.CollaborationDetailVO;
import com.erp.module.message.vo.CollaborationListVO;
import com.erp.module.message.vo.ParticipantVO;
import com.erp.module.message.vo.ReplyTreeVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 协作讨论Service实现.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MsgCollaborationServiceImpl implements MsgCollaborationService {

    private final MsgCollaborationMapper collaborationMapper;
    private final MsgCollaborationReplyMapper replyMapper;
    private final IMsgMessageService messageService;
    private final MsgWebSocketService webSocketService;
    private final MentionParser mentionParser;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public PageResult<CollaborationListVO> page(CollaborationQueryDTO query) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        query.setParticipantId(currentUserId);

        LambdaQueryWrapper<MsgCollaborationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTopic()),
                MsgCollaborationEntity::getTopic, query.getTopic());
        wrapper.eq(StringUtils.hasText(query.getCategory()),
                MsgCollaborationEntity::getCategory, query.getCategory());
        if (query.getIsClosed() != null) {
            wrapper.eq(MsgCollaborationEntity::getIsClosed, query.getIsClosed());
        }
        wrapper.orderByDesc(MsgCollaborationEntity::getCreateTime);

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 20;
        IPage<MsgCollaborationEntity> page = collaborationMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        List<CollaborationListVO> records = page.getRecords().stream()
                .map(this::toListVO)
                .collect(Collectors.toList());

        return PageResult.of(records, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public CollaborationDetailVO getById(Long id) {
        MsgCollaborationEntity collab = collaborationMapper.selectById(id);
        if (collab == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        CollaborationDetailVO vo = toDetailVO(collab);
        List<MsgCollaborationReplyEntity> replies = replyMapper.selectByCollaborationId(id);
        vo.setReplies(buildReplyTree(replies));
        vo.setParticipants(getParticipantUsers(collab.getParticipants()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CollaborationCreateDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (dto.getParticipantIds() == null || dto.getParticipantIds().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "至少需要一个参与人");
        }
        MsgCollaborationEntity entity = new MsgCollaborationEntity();
        entity.setTopic(dto.getTopic());
        entity.setCategory(dto.getCategory() != null ? dto.getCategory() : "general");
        entity.setContent(dto.getContent());
        entity.setCreatorId(currentUserId);
        List<Long> participants = new ArrayList<>(dto.getParticipantIds());
        if (!participants.contains(currentUserId)) {
            participants.add(currentUserId);
        }
        try {
            entity.setParticipants(OBJECT_MAPPER.writeValueAsString(participants));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "序列化参与人列表失败");
        }
        entity.setIsClosed(false);
        collaborationMapper.insert(entity);
        notifyParticipants(participants, currentUserId, "您有新的协作讨论：" + dto.getTopic(), entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long reply(Long collabId, ReplyCreateDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        MsgCollaborationEntity collab = collaborationMapper.selectById(collabId);
        if (collab == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(collab.getIsClosed())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "讨论已关闭，无法回复");
        }
        List<Long> participants = parseParticipantList(collab.getParticipants());
        if (!participants.contains(currentUserId) && !collab.getCreatorId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅参与人可回复讨论");
        }
        MsgCollaborationReplyEntity reply = new MsgCollaborationReplyEntity();
        reply.setCollaborationId(collabId);
        reply.setReplyContent(dto.getReplyContent());
        reply.setReplierId(currentUserId);
        reply.setParentReplyId(dto.getParentReplyId() != null ? dto.getParentReplyId() : 0L);
        replyMapper.insert(reply);

        List<Long> notifyUsers = participants.stream()
                .filter(uid -> !uid.equals(currentUserId))
                .collect(Collectors.toList());
        List<Long> mentionedUsers = mentionParser.parseMentionedUsers(dto.getReplyContent());
        notifyUsers.addAll(mentionedUsers);
        notifyUsers = notifyUsers.stream().distinct().collect(Collectors.toList());

        for (Long uid : notifyUsers) {
            sendMessage(uid, "协作讨论新回复", collab.getTopic());
            webSocketService.pushToUser(uid, buildReplyPushMessage(collab, reply));
        }
        return reply.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeDiscussion(Long collabId) {
        MsgCollaborationEntity collab = collaborationMapper.selectById(collabId);
        if (collab == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!collab.getCreatorId().equals(currentUserId) && !StpUtil.hasRole("admin")) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅发起人或管理员可关闭讨论");
        }
        collab.setIsClosed(true);
        collab.setCloseTime(LocalDateTime.now());
        collaborationMapper.updateById(collab);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reopenDiscussion(Long collabId) {
        MsgCollaborationEntity collab = collaborationMapper.selectById(collabId);
        if (collab == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!collab.getCreatorId().equals(currentUserId) && !StpUtil.hasRole("admin")) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅发起人或管理员可重开讨论");
        }
        collab.setIsClosed(false);
        collab.setCloseTime(null);
        collaborationMapper.updateById(collab);
    }

    private List<ReplyTreeVO> buildReplyTree(List<MsgCollaborationReplyEntity> replies) {
        Map<Long, ReplyTreeVO> voMap = new LinkedHashMap<>();
        List<ReplyTreeVO> roots = new ArrayList<>();
        for (MsgCollaborationReplyEntity r : replies) {
            ReplyTreeVO vo = toReplyVO(r);
            vo.setChildReplies(new ArrayList<>());
            voMap.put(r.getId(), vo);
        }
        for (MsgCollaborationReplyEntity r : replies) {
            ReplyTreeVO vo = voMap.get(r.getId());
            if (r.getParentReplyId() == null || r.getParentReplyId() == 0L
                    || !voMap.containsKey(r.getParentReplyId())) {
                roots.add(vo);
            } else {
                voMap.get(r.getParentReplyId()).getChildReplies().add(vo);
            }
        }
        return roots;
    }

    private void notifyParticipants(List<Long> participantIds, Long excludeUserId,
                                     String message, MsgCollaborationEntity entity) {
        for (Long uid : participantIds) {
            if (!uid.equals(excludeUserId)) {
                sendMessage(uid, message, entity.getTopic());
                webSocketService.pushToUser(uid, buildCreatePushMessage(entity));
            }
        }
    }

    private void sendMessage(Long receiverId, String title, String topic) {
        MsgMessageCreateDTO msgDto = new MsgMessageCreateDTO();
        msgDto.setMessageTitle(title);
        msgDto.setMessageContent("协作讨论：" + topic);
        msgDto.setReceiverId(receiverId);
        msgDto.setSourceType("collaboration");
        messageService.create(msgDto);
    }

    private List<Long> parseParticipantList(String participantsJson) {
        if (!StringUtils.hasText(participantsJson)) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(participantsJson, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.warn("解析参与人列表失败: {}", participantsJson, e);
            return Collections.emptyList();
        }
    }

    private List<ParticipantVO> getParticipantUsers(String participantsJson) {
        List<Long> userIds = parseParticipantList(participantsJson);
        return userIds.stream()
                .map(uid -> new ParticipantVO(uid, "用户" + uid))
                .collect(Collectors.toList());
    }

    private CollaborationListVO toListVO(MsgCollaborationEntity entity) {
        CollaborationListVO vo = new CollaborationListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private CollaborationDetailVO toDetailVO(MsgCollaborationEntity entity) {
        CollaborationDetailVO vo = new CollaborationDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private ReplyTreeVO toReplyVO(MsgCollaborationReplyEntity entity) {
        ReplyTreeVO vo = new ReplyTreeVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setReplierId(entity.getReplierId());
        vo.setReplierName("用户" + entity.getReplierId());
        return vo;
    }

    private String buildReplyPushMessage(MsgCollaborationEntity collab,
                                          MsgCollaborationReplyEntity reply) {
        return "{\"action\":\"reply\",\"collaborationId\":" + collab.getId()
                + ",\"replyId\":" + reply.getId()
                + ",\"topic\":\"" + collab.getTopic() + "\"}";
    }

    private String buildCreatePushMessage(MsgCollaborationEntity entity) {
        return "{\"action\":\"create\",\"collaborationId\":" + entity.getId()
                + ",\"topic\":\"" + entity.getTopic() + "\"}";
    }
}
