package com.erp.message.service;

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
import com.erp.module.message.dto.ReplyCreateDTO;
import com.erp.module.message.entity.MsgCollaborationEntity;
import com.erp.module.message.entity.MsgCollaborationReplyEntity;
import com.erp.module.message.mapper.MsgCollaborationMapper;
import com.erp.module.message.mapper.MsgCollaborationReplyMapper;
import com.erp.module.message.service.IMsgMessageService;
import com.erp.module.message.service.MsgWebSocketService;
import com.erp.module.message.service.impl.MsgCollaborationServiceImpl;
import com.erp.module.message.util.MentionParser;
import com.erp.module.message.vo.CollaborationDetailVO;
import com.erp.module.message.vo.CollaborationListVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MsgCollaborationService 协作讨论Service单元测试")
class MsgCollaborationServiceTest {

    @Mock
    private MsgCollaborationMapper collaborationMapper;
    @Mock
    private MsgCollaborationReplyMapper replyMapper;
    @Mock
    private IMsgMessageService messageService;
    @Mock
    private MsgWebSocketService webSocketService;
    @Mock
    private MentionParser mentionParser;

    private MsgCollaborationServiceImpl service;
    private AtomicLong idSequence;

    private static final Long CURRENT_USER_ID = 1001L;
    private static final Long OTHER_USER_ID = 2001L;

    @BeforeEach
    void setUp() {
        SecurityUtils.setCurrentUserId(CURRENT_USER_ID);
        idSequence = new AtomicLong(1);
        service = new MsgCollaborationServiceImpl(collaborationMapper, replyMapper,
                messageService, webSocketService, mentionParser);
    }

    @AfterEach
    void tearDown() {
        SecurityUtils.clear();
        reset(collaborationMapper, replyMapper, messageService, webSocketService, mentionParser);
    }

    private void mockCollaborationInsert() {
        doAnswer(inv -> {
            MsgCollaborationEntity e = inv.getArgument(0);
            e.setId(idSequence.getAndIncrement());
            return 1;
        }).when(collaborationMapper).insert(any(MsgCollaborationEntity.class));
    }

    private void mockReplyInsert() {
        doAnswer(inv -> {
            MsgCollaborationReplyEntity e = inv.getArgument(0);
            e.setId(idSequence.getAndIncrement());
            return 1;
        }).when(replyMapper).insert(any(MsgCollaborationReplyEntity.class));
    }

    private MsgCollaborationEntity buildEntity(Long id, Long creatorId, String topic,
                                                String participants, Boolean isClosed) {
        MsgCollaborationEntity e = new MsgCollaborationEntity();
        e.setId(id);
        e.setCreatorId(creatorId);
        e.setTopic(topic);
        e.setCategory("general");
        e.setContent("讨论内容-" + id);
        e.setParticipants(participants);
        e.setIsClosed(isClosed != null ? isClosed : false);
        e.setCloseTime(null);
        e.setCreateTime(LocalDateTime.now());
        e.setUpdateTime(LocalDateTime.now());
        return e;
    }

    private MsgCollaborationReplyEntity buildReplyEntity(Long id, Long collabId,
                                                          Long replierId, String content,
                                                          Long parentReplyId) {
        MsgCollaborationReplyEntity e = new MsgCollaborationReplyEntity();
        e.setId(id);
        e.setCollaborationId(collabId);
        e.setReplierId(replierId);
        e.setReplyContent(content);
        e.setParentReplyId(parentReplyId != null ? parentReplyId : 0L);
        e.setCreateTime(LocalDateTime.now());
        return e;
    }

    private String participantsJson(Long... userIds) {
        try {
            return new ObjectMapper().writeValueAsString(Arrays.asList(userIds));
        } catch (Exception e) {
            return "[]";
        }
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create - 创建讨论主题")
    class CreateTests {

        @Test
        @DisplayName("正常流程 → 传入完整DTO，返回新建ID")
        void shouldCreateDiscussionSuccessfully() {
            mockCollaborationInsert();
            when(mentionParser.parseMentionedUsers(any())).thenReturn(Collections.emptyList());

            CollaborationCreateDTO dto = new CollaborationCreateDTO();
            dto.setTopic("测试讨论");
            dto.setCategory("bug");
            dto.setContent("发现了什么问题？");
            dto.setParticipantIds(Arrays.asList(OTHER_USER_ID));

            Long id = service.create(dto);

            assertNotNull(id);
            assertEquals(1L, id);
            verify(collaborationMapper).insert(any(MsgCollaborationEntity.class));
            verify(messageService, atLeastOnce()).create(any());
            verify(webSocketService, atLeastOnce()).pushToUser(anyLong(), any());
        }

        @Test
        @DisplayName("创建人自动加入参与人列表 → 创建人未在参与人列表中时自动添加")
        void shouldAddCreatorToParticipants() {
            mockCollaborationInsert();
            when(mentionParser.parseMentionedUsers(any())).thenReturn(Collections.emptyList());

            CollaborationCreateDTO dto = new CollaborationCreateDTO();
            dto.setTopic("测试讨论");
            dto.setContent("内容");
            // 参与人列表中不包含当前用户
            dto.setParticipantIds(Arrays.asList(OTHER_USER_ID));

            service.create(dto);

            verify(collaborationMapper).insert(argThat(entity -> {
                MsgCollaborationEntity e = (MsgCollaborationEntity) entity;
                return e.getParticipants() != null
                        && e.getParticipants().contains(String.valueOf(CURRENT_USER_ID))
                        && e.getParticipants().contains(String.valueOf(OTHER_USER_ID));
            }));
        }

        @Test
        @DisplayName("边界-空值 → 参与人列表为空时抛出BusinessException")
        void shouldThrowWhenParticipantsEmpty() {
            CollaborationCreateDTO dto = new CollaborationCreateDTO();
            dto.setTopic("测试讨论");
            dto.setContent("内容");
            dto.setParticipantIds(Collections.emptyList());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.create(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("边界-空值 → 参与人列表为null时抛出BusinessException")
        void shouldThrowWhenParticipantsNull() {
            CollaborationCreateDTO dto = new CollaborationCreateDTO();
            dto.setTopic("测试讨论");
            dto.setContent("内容");
            dto.setParticipantIds(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.create(dto));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("默认category → 不传category时默认为general")
        void shouldDefaultCategoryToGeneral() {
            mockCollaborationInsert();
            when(mentionParser.parseMentionedUsers(any())).thenReturn(Collections.emptyList());

            CollaborationCreateDTO dto = new CollaborationCreateDTO();
            dto.setTopic("测试");
            dto.setContent("内容");
            dto.setParticipantIds(Arrays.asList(OTHER_USER_ID));
            dto.setCategory(null);

            service.create(dto);

            verify(collaborationMapper).insert(argThat(entity ->
                    "general".equals(((MsgCollaborationEntity) entity).getCategory())));
        }
    }

    // ==================== page ====================

    @Nested
    @DisplayName("page - 分页查询讨论列表")
    class PageTests {

        @Test
        @DisplayName("正常流程 → 分页查询返回正确数据")
        void shouldReturnPagedResults() {
            CollaborationQueryDTO query = new CollaborationQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10);

            MsgCollaborationEntity entity = buildEntity(1L, CURRENT_USER_ID, "测试讨论",
                    participantsJson(CURRENT_USER_ID, OTHER_USER_ID), false);
            Page<MsgCollaborationEntity> mpPage = new Page<>(1, 10, 1);
            mpPage.setRecords(List.of(entity));
            when(collaborationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mpPage);

            PageResult<CollaborationListVO> result = service.page(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
            CollaborationListVO vo = result.getList().get(0);
            assertEquals(1L, vo.getId());
            assertEquals("测试讨论", vo.getTopic());
        }

        @Test
        @DisplayName("查询无记录 → 返回空列表")
        void shouldReturnEmptyWhenNoRecords() {
            CollaborationQueryDTO query = new CollaborationQueryDTO();
            query.setPageNum(1);
            query.setPageSize(20);

            when(collaborationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(new Page<>(1, 20, 0));

            PageResult<CollaborationListVO> result = service.page(query);

            assertNotNull(result);
            assertTrue(result.getList().isEmpty());
            assertEquals(0L, result.getTotal());
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById - 查询讨论详情")
    class GetByIdTests {

        @Test
        @DisplayName("正常流程 → 查询存在的讨论返回详情含回复树")
        void shouldReturnDetailWithReplies() {
            MsgCollaborationEntity entity = buildEntity(1L, CURRENT_USER_ID, "测试讨论",
                    participantsJson(CURRENT_USER_ID, OTHER_USER_ID), false);
            when(collaborationMapper.selectById(1L)).thenReturn(entity);

            MsgCollaborationReplyEntity reply1 = buildReplyEntity(1L, 1L, OTHER_USER_ID,
                    "回复1", 0L);
            MsgCollaborationReplyEntity reply2 = buildReplyEntity(2L, 1L, CURRENT_USER_ID,
                    "子回复", 1L);
            when(replyMapper.selectByCollaborationId(1L))
                    .thenReturn(Arrays.asList(reply1, reply2));

            CollaborationDetailVO result = service.getById(1L);

            assertNotNull(result);
            assertEquals("测试讨论", result.getTopic());
            assertNotNull(result.getReplies());
            assertEquals(2, result.getParticipants().size());
        }

        @Test
        @DisplayName("异常-不存在 → 查询不存在的ID抛出BusinessException")
        void shouldThrowWhenNotFound() {
            when(collaborationMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.getById(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== reply ====================

    @Nested
    @DisplayName("reply - 回复讨论")
    class ReplyTests {

        @Test
        @DisplayName("正常流程 → 参与人回复成功，通知其他参与人")
        void shouldReplySuccessfully() {
            mockReplyInsert();
            MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "测试讨论",
                    participantsJson(CURRENT_USER_ID, OTHER_USER_ID), false);
            when(collaborationMapper.selectById(1L)).thenReturn(collab);
            when(mentionParser.parseMentionedUsers(any())).thenReturn(Collections.emptyList());

            ReplyCreateDTO dto = new ReplyCreateDTO();
            dto.setReplyContent("我的回复");
            dto.setParentReplyId(null);

            Long replyId = service.reply(1L, dto);

            assertNotNull(replyId);
            assertEquals(1L, replyId);
            verify(replyMapper).insert(any(MsgCollaborationReplyEntity.class));
            verify(messageService, atLeastOnce()).create(any());
            verify(webSocketService, atLeastOnce()).pushToUser(eq(OTHER_USER_ID), any());
            verify(webSocketService, never()).pushToUser(eq(CURRENT_USER_ID), any());
        }

        @Test
        @DisplayName("异常-不存在 → 回复不存在的讨论抛出BusinessException")
        void shouldThrowWhenDiscussionNotFound() {
            when(collaborationMapper.selectById(999L)).thenReturn(null);

            ReplyCreateDTO dto = new ReplyCreateDTO();
            dto.setReplyContent("回复内容");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.reply(999L, dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("异常-已关闭 → 回复已关闭的讨论抛出BusinessException")
        void shouldThrowWhenDiscussionClosed() {
            MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "已关闭讨论",
                    participantsJson(CURRENT_USER_ID, OTHER_USER_ID), true);
            when(collaborationMapper.selectById(1L)).thenReturn(collab);

            ReplyCreateDTO dto = new ReplyCreateDTO();
            dto.setReplyContent("回复内容");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.reply(1L, dto));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("异常-权限 → 非参与人回复抛出BusinessException")
        void shouldThrowWhenNotParticipant() {
            MsgCollaborationEntity collab = buildEntity(1L, OTHER_USER_ID, "讨论",
                    participantsJson(OTHER_USER_ID), false);
            when(collaborationMapper.selectById(1L)).thenReturn(collab);

            ReplyCreateDTO dto = new ReplyCreateDTO();
            dto.setReplyContent("非参与人回复");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.reply(1L, dto));
            assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("楼中楼回复 → parentReplyId正确传递给Entity")
        void shouldSetParentReplyId() {
            mockReplyInsert();
            MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "测试讨论",
                    participantsJson(CURRENT_USER_ID, OTHER_USER_ID), false);
            when(collaborationMapper.selectById(1L)).thenReturn(collab);
            when(mentionParser.parseMentionedUsers(any())).thenReturn(Collections.emptyList());

            ReplyCreateDTO dto = new ReplyCreateDTO();
            dto.setReplyContent("子回复");
            dto.setParentReplyId(5L);

            service.reply(1L, dto);

            verify(replyMapper).insert(argThat(reply ->
                    Long.valueOf(5L).equals(((MsgCollaborationReplyEntity) reply).getParentReplyId())));
        }

        @Test
        @DisplayName("@提及去重 → 同一用户既是参与人又被@时只通知一次")
        void shouldDeduplicateNotifications() {
            mockReplyInsert();
            MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "测试讨论",
                    participantsJson(CURRENT_USER_ID, OTHER_USER_ID), false);
            when(collaborationMapper.selectById(1L)).thenReturn(collab);
            // mentionParser returns OTHER_USER_ID who is already a participant
            when(mentionParser.parseMentionedUsers(any()))
                    .thenReturn(Arrays.asList(OTHER_USER_ID));

            ReplyCreateDTO dto = new ReplyCreateDTO();
            dto.setReplyContent("@[2001:张三] 看下这个");

            service.reply(1L, dto);

            // OTHER_USER_ID should be notified exactly once (not twice)
            verify(webSocketService, times(1)).pushToUser(eq(OTHER_USER_ID), any());
        }
    }

    // ==================== closeDiscussion ====================

    @Nested
    @DisplayName("closeDiscussion - 关闭讨论")
    class CloseDiscussionTests {

        @Test
        @DisplayName("正常流程 → 创建人关闭讨论成功")
        void shouldCloseByCreator() {
            MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "测试讨论",
                    participantsJson(CURRENT_USER_ID, OTHER_USER_ID), false);
            when(collaborationMapper.selectById(1L)).thenReturn(collab);

            service.closeDiscussion(1L);

            verify(collaborationMapper).updateById(argThat(entity -> {
                MsgCollaborationEntity e = (MsgCollaborationEntity) entity;
                return Boolean.TRUE.equals(e.getIsClosed()) && e.getCloseTime() != null;
            }));
        }

        @Test
        @DisplayName("正常流程 → 管理员关闭他人创建的讨论")
        void shouldCloseByAdmin() {
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(() -> StpUtil.hasRole("admin")).thenReturn(true);
                // 当前用户是OTHER_USER_ID, 但创建人是CURRENT_USER_ID
                SecurityUtils.setCurrentUserId(OTHER_USER_ID);

                MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "测试讨论",
                        participantsJson(CURRENT_USER_ID, OTHER_USER_ID), false);
                when(collaborationMapper.selectById(1L)).thenReturn(collab);

                service.closeDiscussion(1L);

                verify(collaborationMapper).updateById(any(MsgCollaborationEntity.class));
            } finally {
                SecurityUtils.setCurrentUserId(CURRENT_USER_ID);
            }
        }

        @Test
        @DisplayName("异常-不存在 → 关闭不存在的讨论抛出BusinessException")
        void shouldThrowWhenNotFound() {
            when(collaborationMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.closeDiscussion(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("异常-权限 → 非创建人也非管理员关闭时抛出BusinessException")
        void shouldThrowWhenNotAuthorized() {
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(() -> StpUtil.hasRole("admin")).thenReturn(false);
                SecurityUtils.setCurrentUserId(OTHER_USER_ID);

                MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "测试讨论",
                        participantsJson(CURRENT_USER_ID, OTHER_USER_ID), false);
                when(collaborationMapper.selectById(1L)).thenReturn(collab);

                BusinessException ex = assertThrows(BusinessException.class,
                        () -> service.closeDiscussion(1L));
                assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getCode());
            } finally {
                SecurityUtils.setCurrentUserId(CURRENT_USER_ID);
            }
        }
    }

    // ==================== reopenDiscussion ====================

    @Nested
    @DisplayName("reopenDiscussion - 重开讨论")
    class ReopenDiscussionTests {

        @Test
        @DisplayName("正常流程 → 创建人重开讨论成功")
        void shouldReopenByCreator() {
            MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "已关闭讨论",
                    participantsJson(CURRENT_USER_ID, OTHER_USER_ID), true);
            collab.setCloseTime(LocalDateTime.now());
            when(collaborationMapper.selectById(1L)).thenReturn(collab);

            service.reopenDiscussion(1L);

            verify(collaborationMapper).updateById(argThat(entity -> {
                MsgCollaborationEntity e = (MsgCollaborationEntity) entity;
                return Boolean.FALSE.equals(e.getIsClosed()) && e.getCloseTime() == null;
            }));
        }

        @Test
        @DisplayName("正常流程 → 管理员重开他人创建的讨论")
        void shouldReopenByAdmin() {
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(() -> StpUtil.hasRole("admin")).thenReturn(true);
                SecurityUtils.setCurrentUserId(OTHER_USER_ID);

                MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "已关闭讨论",
                        participantsJson(CURRENT_USER_ID, OTHER_USER_ID), true);
                when(collaborationMapper.selectById(1L)).thenReturn(collab);

                service.reopenDiscussion(1L);

                verify(collaborationMapper).updateById(any(MsgCollaborationEntity.class));
            } finally {
                SecurityUtils.setCurrentUserId(CURRENT_USER_ID);
            }
        }

        @Test
        @DisplayName("异常-不存在 → 重开不存在的讨论抛出BusinessException")
        void shouldThrowWhenNotFound() {
            when(collaborationMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.reopenDiscussion(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("异常-权限 → 非创建人也非管理员重开时抛出BusinessException")
        void shouldThrowWhenNotAuthorized() {
            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(() -> StpUtil.hasRole("admin")).thenReturn(false);
                SecurityUtils.setCurrentUserId(OTHER_USER_ID);

                MsgCollaborationEntity collab = buildEntity(1L, CURRENT_USER_ID, "已关闭讨论",
                        participantsJson(CURRENT_USER_ID, OTHER_USER_ID), true);
                when(collaborationMapper.selectById(1L)).thenReturn(collab);

                BusinessException ex = assertThrows(BusinessException.class,
                        () -> service.reopenDiscussion(1L));
                assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getCode());
            } finally {
                SecurityUtils.setCurrentUserId(CURRENT_USER_ID);
            }
        }
    }
}
