package com.erp.message.service;

import com.erp.common.result.PageResult;
import com.erp.common.utils.SecurityUtils;
import com.erp.module.message.dto.DiscussionCreateDTO;
import com.erp.module.message.dto.DiscussionQueryDTO;
import com.erp.module.message.entity.MsgDiscussionEntity;
import com.erp.module.message.mapper.MsgDiscussionMapper;
import com.erp.module.message.service.IMsgMessageService;
import com.erp.module.message.service.MsgWebSocketService;
import com.erp.module.message.service.impl.MsgDiscussionServiceImpl;
import com.erp.module.message.util.MentionParser;
import com.erp.module.message.vo.DiscussionListVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MsgDiscussionService 单据沟通Service单元测试")
class MsgDiscussionServiceTest {

    @Mock
    private MsgDiscussionMapper discussionMapper;
    @Mock
    private IMsgMessageService messageService;
    @Mock
    private MsgWebSocketService webSocketService;
    @Mock
    private MentionParser mentionParser;

    private MsgDiscussionServiceImpl service;
    private AtomicLong idSequence;

    private static final Long CURRENT_USER_ID = 1001L;
    private static final Long OTHER_USER_ID = 2001L;

    @BeforeEach
    void setUp() {
        SecurityUtils.setCurrentUserId(CURRENT_USER_ID);
        idSequence = new AtomicLong(1);
        service = new MsgDiscussionServiceImpl(discussionMapper, messageService,
                webSocketService, mentionParser);
    }

    @AfterEach
    void tearDown() {
        SecurityUtils.clear();
        reset(discussionMapper, messageService, webSocketService, mentionParser);
    }

    private void mockInsert() {
        doAnswer(inv -> {
            MsgDiscussionEntity e = inv.getArgument(0);
            e.setId(idSequence.getAndIncrement());
            return 1;
        }).when(discussionMapper).insert(any(MsgDiscussionEntity.class));
    }

    // ==================== pageByDoc ====================

    @Nested
    @DisplayName("pageByDoc - 按单据分页查询沟通记录")
    class PageByDocTests {

        @Test
        @DisplayName("正常流程-查询 → 返回正确分页数据")
        void shouldReturnPagedResults() {
            DiscussionQueryDTO query = new DiscussionQueryDTO();
            query.setBusinessType("purchase_order");
            query.setBusinessId(100L);
            query.setPageNum(1);
            query.setPageSize(10);

            MsgDiscussionEntity entity = buildEntity(1L, CURRENT_USER_ID, "测试留言", 0L);
            when(discussionMapper.selectPage(any(), any()))
                    .thenReturn(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10, 1)
                            .setRecords(List.of(entity)));

            PageResult<DiscussionListVO> result = service.pageByDoc(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1, result.getPages());
            DiscussionListVO vo = result.getList().get(0);
            assertEquals(1L, vo.getId());
            assertEquals("purchase_order", vo.getBusinessType());
            assertEquals(100L, vo.getBusinessId());
        }

        @Test
        @DisplayName("查询无记录 → 返回空列表")
        void shouldReturnEmptyWhenNoRecords() {
            DiscussionQueryDTO query = new DiscussionQueryDTO();
            query.setBusinessType("sales_order");
            query.setBusinessId(999L);

            when(discussionMapper.selectPage(any(), any()))
                    .thenReturn(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 20, 0));

            PageResult<DiscussionListVO> result = service.pageByDoc(query);

            assertNotNull(result);
            assertTrue(result.getList().isEmpty());
            assertEquals(0L, result.getTotal());
        }

        @Test
        @DisplayName("pageNum/pageSize为null → 使用默认值1和20")
        void shouldUseDefaultPageParamsWhenNull() {
            DiscussionQueryDTO query = new DiscussionQueryDTO();
            query.setBusinessType("purchase_order");
            query.setBusinessId(100L);

            when(discussionMapper.selectPage(any(), any()))
                    .thenReturn(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 20, 0));

            PageResult<DiscussionListVO> result = service.pageByDoc(query);

            assertNotNull(result);
            assertEquals(1, result.getPageNum());
            assertEquals(20, result.getPageSize());
        }
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create - 新增留言/回复")
    class CreateTests {

        @Test
        @DisplayName("正常流程-新增 → 返回新建ID，数据库记录正确")
        void shouldCreateSimpleComment() {
            mockInsert();

            DiscussionCreateDTO dto = new DiscussionCreateDTO();
            dto.setBusinessType("purchase_order");
            dto.setBusinessId(100L);
            dto.setContent("请确认这个采购单的数量");
            dto.setParentId(0L);

            when(mentionParser.parseMentionedUsers("请确认这个采购单的数量"))
                    .thenReturn(Collections.emptyList());

            Long id = service.create(dto);

            assertNotNull(id);
            assertEquals(1L, id);
            verify(discussionMapper).insert(argThat(entity ->
                    "purchase_order".equals(entity.getBusinessType())
                            && Long.valueOf(100L).equals(entity.getBusinessId())
                            && CURRENT_USER_ID.equals(entity.getSenderId())
                            && "请确认这个采购单的数量".equals(entity.getContent())
            ));
            verify(messageService, never()).create(any());
            verify(webSocketService, never()).pushToUser(anyLong(), anyString());
        }

        @Test
        @DisplayName("@提及用户 → 解析@提及，发送消息通知+WebSocket推送")
        void shouldCreateCommentWithMentions() {
            mockInsert();

            DiscussionCreateDTO dto = new DiscussionCreateDTO();
            dto.setBusinessType("purchase_order");
            dto.setBusinessId(100L);
            dto.setContent("请@[2001:张三]确认数量，@[3001:李四]审核");
            dto.setParentId(0L);

            when(mentionParser.parseMentionedUsers("请@[2001:张三]确认数量，@[3001:李四]审核"))
                    .thenReturn(List.of(2001L, 3001L));
            when(messageService.create(any())).thenReturn(500L);

            Long id = service.create(dto);

            assertNotNull(id);
            verify(messageService, times(2)).create(any());
            verify(webSocketService, times(2)).pushToUser(anyLong(), anyString());
        }

        @Test
        @DisplayName("@提及自己 → 不给自己发通知")
        void shouldNotNotifyWhenMentioningSelf() {
            mockInsert();

            DiscussionCreateDTO dto = new DiscussionCreateDTO();
            dto.setBusinessType("purchase_order");
            dto.setBusinessId(100L);
            dto.setContent("请@[1001:我]确认");
            dto.setParentId(0L);

            when(mentionParser.parseMentionedUsers("请@[1001:我]确认"))
                    .thenReturn(List.of(CURRENT_USER_ID));

            service.create(dto);

            verify(messageService, never()).create(any());
            verify(webSocketService, never()).pushToUser(anyLong(), anyString());
        }

        @Test
        @DisplayName("回复父留言 → 通知父留言作者+WebSocket推送")
        void shouldCreateReplyToParent() {
            mockInsert();

            DiscussionCreateDTO dto = new DiscussionCreateDTO();
            dto.setBusinessType("purchase_order");
            dto.setBusinessId(100L);
            dto.setContent("已确认，没问题");
            dto.setParentId(10L);

            MsgDiscussionEntity parentEntity = buildEntity(10L, OTHER_USER_ID, "原始留言", 0L);

            when(mentionParser.parseMentionedUsers("已确认，没问题"))
                    .thenReturn(Collections.emptyList());
            when(discussionMapper.selectById(10L)).thenReturn(parentEntity);
            when(messageService.create(any())).thenReturn(501L);

            Long id = service.create(dto);

            assertNotNull(id);
            verify(messageService).create(any());
            verify(webSocketService).pushToUser(eq(OTHER_USER_ID), anyString());
        }

        @Test
        @DisplayName("回复自己的留言 → 不给自己发回复通知")
        void shouldNotNotifyWhenReplyingToSelf() {
            mockInsert();

            DiscussionCreateDTO dto = new DiscussionCreateDTO();
            dto.setBusinessType("purchase_order");
            dto.setBusinessId(100L);
            dto.setContent("补充说明");
            dto.setParentId(10L);

            MsgDiscussionEntity parentEntity = buildEntity(10L, CURRENT_USER_ID, "我的留言", 0L);

            when(mentionParser.parseMentionedUsers("补充说明"))
                    .thenReturn(Collections.emptyList());
            when(discussionMapper.selectById(10L)).thenReturn(parentEntity);

            service.create(dto);

            verify(messageService, never()).create(any());
            verify(webSocketService, never()).pushToUser(anyLong(), anyString());
        }

        @Test
        @DisplayName("@提及用户与回复同一人 → 去重，仅通知一次")
        void shouldNotDuplicateNotificationForMentionAndReply() {
            mockInsert();

            DiscussionCreateDTO dto = new DiscussionCreateDTO();
            dto.setBusinessType("purchase_order");
            dto.setBusinessId(100L);
            dto.setContent("回复@[2001:张三]的问题");
            dto.setParentId(10L);

            MsgDiscussionEntity parentEntity = buildEntity(10L, OTHER_USER_ID, "原始留言", 0L);

            when(mentionParser.parseMentionedUsers("回复@[2001:张三]的问题"))
                    .thenReturn(List.of(OTHER_USER_ID));
            when(discussionMapper.selectById(10L)).thenReturn(parentEntity);
            when(messageService.create(any())).thenReturn(502L);

            service.create(dto);

            verify(messageService, times(1)).create(any());
            verify(webSocketService, times(1)).pushToUser(eq(OTHER_USER_ID), anyString());
        }

        @Test
        @DisplayName("父留言不存在 → 正常创建(不触发回复通知)")
        void shouldCreateEvenWhenParentNotFound() {
            mockInsert();

            DiscussionCreateDTO dto = new DiscussionCreateDTO();
            dto.setBusinessType("purchase_order");
            dto.setBusinessId(100L);
            dto.setContent("回复一条已删除的留言");
            dto.setParentId(999L);

            when(mentionParser.parseMentionedUsers("回复一条已删除的留言"))
                    .thenReturn(Collections.emptyList());
            when(discussionMapper.selectById(999L)).thenReturn(null);

            Long id = service.create(dto);

            assertNotNull(id);
            verify(messageService, never()).create(any());
        }

        @Test
        @DisplayName("批量@提及去重 → 相同用户仅通知一次")
        void shouldDeduplicateMentions() {
            mockInsert();

            DiscussionCreateDTO dto = new DiscussionCreateDTO();
            dto.setBusinessType("purchase_order");
            dto.setBusinessId(100L);
            dto.setContent("@[2001:张三] @[2001:张三] 请确认");
            dto.setParentId(0L);

            when(mentionParser.parseMentionedUsers("@[2001:张三] @[2001:张三] 请确认"))
                    .thenReturn(List.of(2001L));
            when(messageService.create(any())).thenReturn(503L);

            service.create(dto);

            verify(messageService, times(1)).create(any());
            verify(webSocketService, times(1)).pushToUser(anyLong(), anyString());
        }
    }

    // ==================== getReplies ====================

    @Nested
    @DisplayName("getReplies - 查询回复列表")
    class GetRepliesTests {

        @Test
        @DisplayName("正常流程-查询 → 返回回复列表")
        void shouldReturnReplies() {
            MsgDiscussionEntity reply1 = buildEntity(2L, OTHER_USER_ID, "回复1", 1L);
            MsgDiscussionEntity reply2 = buildEntity(3L, CURRENT_USER_ID, "回复2", 1L);

            when(discussionMapper.selectList(any())).thenReturn(List.of(reply1, reply2));

            List<DiscussionListVO> replies = service.getReplies(1L);

            assertNotNull(replies);
            assertEquals(2, replies.size());
            assertEquals(2L, replies.get(0).getId());
            assertEquals(3L, replies.get(1).getId());
        }

        @Test
        @DisplayName("无回复 → 返回空列表")
        void shouldReturnEmptyWhenNoReplies() {
            when(discussionMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<DiscussionListVO> replies = service.getReplies(999L);

            assertNotNull(replies);
            assertTrue(replies.isEmpty());
        }
    }

    private MsgDiscussionEntity buildEntity(Long id, Long senderId, String content, Long parentId) {
        MsgDiscussionEntity entity = new MsgDiscussionEntity();
        entity.setId(id);
        entity.setBusinessType("purchase_order");
        entity.setBusinessId(100L);
        entity.setSenderId(senderId);
        entity.setContent(content);
        entity.setParentId(parentId);
        return entity;
    }
}
