package com.erp.system.announcement.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.announcement.dto.AnnouncementCreateDTO;
import com.erp.system.announcement.dto.AnnouncementQueryDTO;
import com.erp.system.announcement.dto.AnnouncementUpdateDTO;
import com.erp.system.announcement.service.IAnnouncementService;
import com.erp.system.announcement.vo.AnnouncementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统公告管理Controller.
 *
 * @author AI
 * @since 2026-06-08
 */
@Slf4j
@RestController
@RequestMapping("/api/system/announcement")
@RequiredArgsConstructor
@Tag(name = "系统公告管理", description = "系统公告CRUD、未读查询、已读标记")
public class AnnouncementController {

    private final IAnnouncementService announcementService;

    @Operation(summary = "分页查询公告列表")
    @GetMapping
    @RequirePermission("system:announcement:manage")
    public RT<PageResult<AnnouncementVO>> pageList(@Valid AnnouncementQueryDTO query) {
        return RT.ok(announcementService.pageList(query));
    }

    @Operation(summary = "新增公告")
    @PostMapping
    @RequirePermission("system:announcement:manage")
    public RT<AnnouncementVO> create(
            @Parameter(description = "公告创建参数") @Valid @RequestBody AnnouncementCreateDTO dto) {
        return RT.ok(announcementService.create(dto));
    }

    @Operation(summary = "修改公告")
    @PutMapping("/{id}")
    @RequirePermission("system:announcement:manage")
    public RT<AnnouncementVO> update(
            @Parameter(description = "公告ID") @PathVariable Long id,
            @Parameter(description = "公告更新参数") @Valid @RequestBody AnnouncementUpdateDTO dto) {
        return RT.ok(announcementService.update(id, dto));
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    @RequirePermission("system:announcement:manage")
    public RT<Void> delete(
            @Parameter(description = "公告ID") @PathVariable Long id) {
        announcementService.delete(id);
        return RT.ok();
    }

    @Operation(summary = "获取未读公告列表")
    @GetMapping("/unread")
    public RT<List<AnnouncementVO>> getUnreadList() {
        return RT.ok(announcementService.getUnreadList());
    }

    @Operation(summary = "标记公告已读")
    @PostMapping("/{id}/read")
    public RT<Void> markAsRead(
            @Parameter(description = "公告ID") @PathVariable Long id) {
        announcementService.markAsRead(id);
        return RT.ok();
    }
}
