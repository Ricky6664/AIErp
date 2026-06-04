package com.erp.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.dto.SysUserGroupDTO;
import com.erp.system.entity.SysUserGroup;
import com.erp.system.mapper.UserGroupMapper;
import com.erp.system.service.UserGroupService;
import com.erp.system.vo.SysUserGroupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "用户组管理", description = "用户组CRUD、成员管理、角色管理、状态管理接口")
@RestController
@RequestMapping("/api/system/user-group")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;
    private final UserGroupMapper userGroupMapper;

    // ==================== 查询接口 ====================

    @Operation(summary = "分页查询用户组列表")
    @SaCheckPermission("system:user-group:query")
    @GetMapping
    public RT<PageResult<SysUserGroupVO.ListVO>> pageList(
            @Valid PageQuery query, BindingResult bindingResult,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean enabled) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        LambdaQueryWrapper<SysUserGroup> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUserGroup::getGroupName, keyword)
                    .or().like(SysUserGroup::getGroupCode, keyword));
        }
        if (enabled != null) {
            wrapper.eq(SysUserGroup::getIsEnabled, enabled);
        }
        wrapper.orderByAsc(SysUserGroup::getSortOrder)
               .orderByDesc(SysUserGroup::getCreateTime);
        PageResult<SysUserGroup> pageResult = userGroupService.pageList(query, wrapper);

        List<Long> groupIds = pageResult.getList().stream()
                .map(SysUserGroup::getId)
                .collect(Collectors.toList());
        Map<Long, Long> memberCountMap = groupIds.isEmpty() ? Map.of()
                : userGroupMapper.countMembersByGroupIds(groupIds).stream()
                .collect(Collectors.toMap(
                        m -> ((Number) m.get("group_id")).longValue(),
                        m -> ((Number) m.get("member_count")).longValue()));

        List<SysUserGroupVO.ListVO> voList = pageResult.getList().stream()
                .map(entity -> {
                    SysUserGroupVO.ListVO vo = new SysUserGroupVO.ListVO();
                    BeanUtils.copyProperties(entity, vo);
                    vo.setMemberCount(memberCountMap.getOrDefault(entity.getId(), 0L));
                    return vo;
                })
                .collect(Collectors.toList());
        PageResult<SysUserGroupVO.ListVO> result = PageResult.of(voList,
                pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
        return RT.ok(result);
    }

    @Operation(summary = "查询用户组详情")
    @SaCheckPermission("system:user-group:query")
    @GetMapping("/{id}")
    public RT<SysUserGroupVO.DetailVO> getById(@PathVariable Long id) {
        SysUserGroup entity = userGroupService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        SysUserGroupVO.DetailVO vo = new SysUserGroupVO.DetailVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setMemberUserIds(userGroupService.getMemberUserIds(id));
        vo.setRoleIds(userGroupService.getRoleIds(id));
        return RT.ok(vo);
    }

    @Operation(summary = "检查组编码唯一性")
    @SaCheckPermission("system:user-group:query")
    @GetMapping("/check-code")
    public RT<Boolean> checkGroupCode(@RequestParam String groupCode,
                                      @RequestParam(required = false) Long excludeId) {
        LambdaQueryWrapper<SysUserGroup> wrapper = new LambdaQueryWrapper<SysUserGroup>()
                .eq(SysUserGroup::getGroupCode, groupCode);
        if (excludeId != null) {
            wrapper.ne(SysUserGroup::getId, excludeId);
        }
        return RT.ok(userGroupService.count(wrapper) == 0);
    }

    // ==================== 新增接口 ====================

    @Operation(summary = "新增用户组")
    @SaCheckPermission("system:user-group:add")
    @PostMapping
    public RT<Long> create(@Valid @RequestBody SysUserGroupDTO.CreateDTO dto,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        if (userGroupService.count(new LambdaQueryWrapper<SysUserGroup>()
                .eq(SysUserGroup::getGroupCode, dto.getGroupCode())) > 0) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "组编码已存在");
        }
        SysUserGroup entity = new SysUserGroup();
        BeanUtils.copyProperties(dto, entity);
        entity.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);
        userGroupService.save(entity);

        if (dto.getMemberUserIds() != null && !dto.getMemberUserIds().isEmpty()) {
            userGroupService.addMembers(entity.getId(), dto.getMemberUserIds());
        }
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            userGroupService.addRoles(entity.getId(), dto.getRoleIds());
        }

        return RT.ok(entity.getId());
    }

    // ==================== 修改接口 ====================

    @Operation(summary = "修改用户组")
    @SaCheckPermission("system:user-group:edit")
    @PutMapping("/{id}")
    public RT<Void> update(@PathVariable Long id,
                           @Valid @RequestBody SysUserGroupDTO.UpdateDTO dto,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        SysUserGroup entity = userGroupService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (StringUtils.hasText(dto.getGroupCode())
                && !dto.getGroupCode().equals(entity.getGroupCode())
                && userGroupService.count(new LambdaQueryWrapper<SysUserGroup>()
                        .eq(SysUserGroup::getGroupCode, dto.getGroupCode())) > 0) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "组编码已存在");
        }
        if (StringUtils.hasText(dto.getGroupCode())) entity.setGroupCode(dto.getGroupCode());
        if (StringUtils.hasText(dto.getGroupName())) entity.setGroupName(dto.getGroupName());
        if (dto.getGroupDesc() != null) entity.setGroupDesc(dto.getGroupDesc());
        if (dto.getIsEnabled() != null) entity.setIsEnabled(dto.getIsEnabled());
        if (dto.getSortOrder() != null) entity.setSortOrder(dto.getSortOrder());
        userGroupService.updateById(entity);

        if (dto.getMemberUserIds() != null) {
            userGroupService.removeAllMembers(id);
            if (!dto.getMemberUserIds().isEmpty()) {
                userGroupService.addMembers(id, dto.getMemberUserIds());
            }
        }
        if (dto.getRoleIds() != null) {
            userGroupService.removeAllRoles(id);
            if (!dto.getRoleIds().isEmpty()) {
                userGroupService.addRoles(id, dto.getRoleIds());
            }
        }

        return RT.ok();
    }

    @Operation(summary = "修改用户组启用状态")
    @SaCheckPermission("system:user-group:edit")
    @PutMapping("/{id}/status")
    public RT<Void> updateStatus(@PathVariable Long id,
                                 @Valid @RequestBody SysUserGroupDTO.StatusDTO dto,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        userGroupService.updateStatus(id, dto.getIsEnabled());
        return RT.ok();
    }

    // ==================== 成员管理接口 ====================

    @Operation(summary = "查询用户组成员ID列表")
    @SaCheckPermission("system:user-group:query")
    @GetMapping("/{id}/members")
    public RT<List<Long>> getMembers(@PathVariable Long id) {
        return RT.ok(userGroupService.getMemberUserIds(id));
    }

    @Operation(summary = "更新用户组成员")
    @SaCheckPermission("system:user-group:member")
    @PostMapping("/{id}/members")
    public RT<Void> updateMembers(@PathVariable Long id,
                                  @Valid @RequestBody SysUserGroupDTO.MembersDTO dto,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        userGroupService.removeAllMembers(id);
        if (!dto.getMemberUserIds().isEmpty()) {
            userGroupService.addMembers(id, dto.getMemberUserIds());
        }
        return RT.ok();
    }

    // ==================== 角色管理接口 ====================

    @Operation(summary = "查询用户组角色ID列表")
    @SaCheckPermission("system:user-group:query")
    @GetMapping("/{id}/roles")
    public RT<List<Long>> getRoles(@PathVariable Long id) {
        return RT.ok(userGroupService.getRoleIds(id));
    }

    @Operation(summary = "更新用户组角色")
    @SaCheckPermission("system:user-group:edit")
    @PostMapping("/{id}/roles")
    public RT<Void> updateRoles(@PathVariable Long id,
                                @Valid @RequestBody SysUserGroupDTO.RolesDTO dto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        userGroupService.removeAllRoles(id);
        if (!dto.getRoleIds().isEmpty()) {
            userGroupService.addRoles(id, dto.getRoleIds());
        }
        return RT.ok();
    }

    // ==================== 删除接口 ====================

    @Operation(summary = "删除用户组")
    @SaCheckPermission("system:user-group:delete")
    @DeleteMapping("/{id}")
    public RT<Void> delete(@PathVariable Long id) {
        SysUserGroup entity = userGroupService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        List<Long> memberIds = userGroupService.getMemberUserIds(id);
        if (!memberIds.isEmpty()) {
            userGroupService.removeAllMembers(id);
        }
        List<Long> roleIds = userGroupService.getRoleIds(id);
        if (!roleIds.isEmpty()) {
            userGroupService.removeAllRoles(id);
        }
        userGroupService.removeById(id);
        return RT.ok();
    }

    // ==================== Helper Methods ====================

    private String getErrorMsg(BindingResult bindingResult) {
        return bindingResult.getFieldError() != null
                ? bindingResult.getFieldError().getDefaultMessage()
                : "参数校验失败";
    }
}
