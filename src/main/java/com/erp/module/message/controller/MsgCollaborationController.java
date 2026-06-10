package com.erp.module.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.module.message.dto.CollaborationCreateDTO;
import com.erp.module.message.dto.CollaborationQueryDTO;
import com.erp.module.message.dto.ReplyCreateDTO;
import com.erp.module.message.service.MsgCollaborationService;
import com.erp.module.message.vo.CollaborationDetailVO;
import com.erp.module.message.vo.CollaborationListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 协作讨论接口Controller.
 *
 * @author AI
 */
@RestController
@RequestMapping("/api/message/discussion")
@RequiredArgsConstructor
@Tag(name = "协作讨论管理", description = "协作讨论CRUD、回复、关闭/重开接口")
public class MsgCollaborationController {

    private final MsgCollaborationService collaborationService;

    @Operation(summary = "分页查询讨论列表")
    @GetMapping
    @SaCheckPermission("message:discussion:query")
    public RT<PageResult<CollaborationListVO>> page(@Valid CollaborationQueryDTO query) {
        return RT.ok(collaborationService.page(query));
    }

    @Operation(summary = "查询讨论详情（含回复树）")
    @GetMapping("/{id}")
    @SaCheckPermission("message:discussion:query")
    public RT<CollaborationDetailVO> getById(
            @Parameter(description = "讨论ID") @PathVariable Long id) {
        return RT.ok(collaborationService.getById(id));
    }

    @Operation(summary = "新增讨论主题")
    @PostMapping
    @SaCheckPermission("message:discussion:create")
    public RT<Long> create(@Valid @RequestBody CollaborationCreateDTO dto) {
        return RT.ok(collaborationService.create(dto));
    }

    @Operation(summary = "回复讨论")
    @PostMapping("/{id}/reply")
    @SaCheckPermission("message:discussion:create")
    public RT<Long> reply(
            @Parameter(description = "讨论ID") @PathVariable("id") Long collabId,
            @Valid @RequestBody ReplyCreateDTO dto) {
        return RT.ok(collaborationService.reply(collabId, dto));
    }

    @Operation(summary = "关闭讨论")
    @PutMapping("/{id}/close")
    @SaCheckPermission("message:discussion:update")
    public RT<Void> closeDiscussion(
            @Parameter(description = "讨论ID") @PathVariable("id") Long collabId) {
        collaborationService.closeDiscussion(collabId);
        return RT.ok();
    }

    @Operation(summary = "重开讨论")
    @PutMapping("/{id}/reopen")
    @SaCheckPermission("message:discussion:update")
    public RT<Void> reopenDiscussion(
            @Parameter(description = "讨论ID") @PathVariable("id") Long collabId) {
        collaborationService.reopenDiscussion(collabId);
        return RT.ok();
    }
}
