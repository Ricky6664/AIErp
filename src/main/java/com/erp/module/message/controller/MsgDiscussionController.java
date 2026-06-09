package com.erp.module.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.module.message.dto.DiscussionCreateDTO;
import com.erp.module.message.dto.DiscussionQueryDTO;
import com.erp.module.message.service.MsgDiscussionService;
import com.erp.module.message.vo.DiscussionListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 单据沟通接口Controller.
 *
 * @author AI
 */
@RestController
@RequestMapping("/api/message/doc-comment")
@RequiredArgsConstructor
@Tag(name = "单据沟通管理", description = "按单据查询沟通记录、新增留言与回复接口")
public class MsgDiscussionController {

    private final MsgDiscussionService discussionService;

    @Operation(summary = "按单据分页查询沟通记录")
    @GetMapping
    @SaCheckPermission("message:discussion:query")
    public RT<PageResult<DiscussionListVO>> pageByDoc(@Valid DiscussionQueryDTO query) {
        return RT.ok(discussionService.pageByDoc(query));
    }

    @Operation(summary = "新增留言/回复")
    @PostMapping
    @SaCheckPermission("message:discussion:create")
    public RT<Long> create(@Valid @RequestBody DiscussionCreateDTO dto) {
        return RT.ok(discussionService.create(dto));
    }

    @Operation(summary = "查询回复列表")
    @GetMapping("/{parentId}/replies")
    @SaCheckPermission("message:discussion:query")
    public RT<List<DiscussionListVO>> getReplies(
            @Parameter(description = "父留言ID") @PathVariable Long parentId) {
        return RT.ok(discussionService.getReplies(parentId));
    }
}
