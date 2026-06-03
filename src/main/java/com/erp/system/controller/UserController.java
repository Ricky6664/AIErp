package com.erp.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.dto.SysUserDTO;
import com.erp.system.entity.SysUser;
import com.erp.system.service.UserService;
import com.erp.system.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
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
import java.util.stream.Collectors;

@Tag(name = "用户管理", description = "用户CRUD、角色分配、密码管理、状态管理接口")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ==================== 查询接口 ====================

    @Operation(summary = "检查用户名唯一性")
    @SaCheckPermission("system:user:query")
    @GetMapping("/check-username")
    public RT<Boolean> checkUsername(@RequestParam String username,
                                     @RequestParam(required = false) Long excludeId) {
        return RT.ok(userService.isUsernameUnique(username, excludeId));
    }

    @Operation(summary = "分页查询用户列表")
    @SaCheckPermission("system:user:query")
    @GetMapping
    public RT<PageResult<SysUserVO.ListVO>> pageList(
            @Valid PageQuery query, BindingResult bindingResult,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long deptId) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getNickname, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        PageResult<SysUser> pageResult = userService.pageList(query, wrapper);
        List<SysUserVO.ListVO> voList = pageResult.getList().stream()
                .map(this::toListVO)
                .collect(Collectors.toList());
        PageResult<SysUserVO.ListVO> result = PageResult.of(voList,
                pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
        return RT.ok(result);
    }

    @Operation(summary = "查询用户详情(含角色名称列表)")
    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}")
    public RT<SysUserVO.DetailVO> getById(@PathVariable Long id) {
        SysUser entity = userService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        SysUserVO.DetailVO vo = toDetailVO(entity);
        vo.setRoleNames(userService.getRoleNames(id));
        return RT.ok(vo);
    }

    @Operation(summary = "查询用户角色名称列表")
    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}/roles")
    public RT<List<String>> getRoleNames(@PathVariable Long id) {
        return RT.ok(userService.getRoleNames(id));
    }

    // ==================== 新增接口 ====================

    @Operation(summary = "新增用户")
    @SaCheckPermission("system:user:add")
    @PostMapping
    public RT<Long> create(@Valid @RequestBody SysUserDTO.CreateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        if (!userService.isUsernameUnique(dto.getUsername(), null)) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "用户名已存在");
        }
        SysUser entity = toEntity(dto);
        userService.save(entity);
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            userService.assignRoles(entity.getId(), dto.getRoleIds());
        }
        return RT.ok(entity.getId());
    }

    // ==================== 修改接口 ====================

    @Operation(summary = "用户自行修改密码")
    @SaCheckPermission("system:user:password")
    @PutMapping("/password")
    public RT<Void> changePassword(@Valid @RequestBody SysUserDTO.ChangePwdDTO dto,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        Long currentUserId = StpUtil.getLoginIdAsLong();
        userService.changePassword(currentUserId, dto.getOldPassword(), dto.getNewPassword());
        return RT.ok();
    }

    @Operation(summary = "修改用户")
    @SaCheckPermission("system:user:edit")
    @PutMapping("/{id}")
    public RT<Void> update(@PathVariable Long id, @Valid @RequestBody SysUserDTO.UpdateDTO dto,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        SysUser entity = userService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (StringUtils.hasText(dto.getUsername())
                && !dto.getUsername().equals(entity.getUsername())
                && !userService.isUsernameUnique(dto.getUsername(), id)) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "用户名已存在");
        }
        mergeEntity(entity, dto);
        userService.updateById(entity);
        return RT.ok();
    }

    @Operation(summary = "修改用户状态")
    @SaCheckPermission("system:user:edit")
    @PutMapping("/{id}/status")
    public RT<Void> updateStatus(@PathVariable Long id,
                                 @Valid @RequestBody SysUserDTO.StatusDTO dto,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        userService.updateStatus(id, dto.getStatus());
        return RT.ok();
    }

    @Operation(summary = "解锁用户")
    @SaCheckPermission("system:user:edit")
    @PutMapping("/{id}/unlock")
    public RT<Void> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return RT.ok();
    }

    // ==================== 删除接口 ====================

    @Operation(summary = "删除用户(软删除, 清理角色与部门关联)")
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/{id}")
    public RT<Void> delete(@PathVariable Long id) {
        userService.deleteUserWithCleanup(id);
        return RT.ok();
    }

    // ==================== 密码管理接口 ====================

    @Operation(summary = "管理员重置用户密码")
    @SaCheckPermission("system:user:password-reset")
    @PostMapping("/{id}/reset-password")
    public RT<String> resetPassword(@PathVariable Long id) {
        String newPassword = userService.resetPasswordAndReturn(id);
        return RT.ok("密码已重置", newPassword);
    }

    // ==================== 角色管理接口 ====================

    @Operation(summary = "批量分配用户角色")
    @SaCheckPermission("system:user:role-assign")
    @PostMapping("/role")
    public RT<Void> assignRoles(@Valid @RequestBody SysUserDTO.BatchRoleAssignDTO dto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        userService.assignRoles(dto.getUserId(), dto.getRoleIds());
        return RT.ok();
    }

    @Operation(summary = "清除用户所有角色")
    @SaCheckPermission("system:user:role-assign")
    @DeleteMapping("/role/{userId}")
    public RT<Void> clearRoles(@PathVariable Long userId) {
        userService.assignRoles(userId, List.of());
        return RT.ok();
    }

    // ==================== Helper Methods ====================

    private String getErrorMsg(BindingResult bindingResult) {
        return bindingResult.getFieldError() != null
                ? bindingResult.getFieldError().getDefaultMessage()
                : "参数校验失败";
    }

    private SysUserVO.ListVO toListVO(SysUser entity) {
        SysUserVO.ListVO vo = new SysUserVO.ListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private SysUserVO.DetailVO toDetailVO(SysUser entity) {
        SysUserVO.DetailVO vo = new SysUserVO.DetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private SysUser toEntity(SysUserDTO.CreateDTO dto) {
        SysUser entity = new SysUser();
        BeanUtils.copyProperties(dto, entity, "password");
        if (StringUtils.hasText(dto.getPassword())) {
            entity.setPasswordHash(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        }
        entity.setStatus(StringUtils.hasText(dto.getStatus()) ? dto.getStatus() : "normal");
        entity.setIsLocked(dto.getIsLocked() != null ? dto.getIsLocked() : false);
        return entity;
    }

    private void mergeEntity(SysUser entity, SysUserDTO.UpdateDTO dto) {
        if (StringUtils.hasText(dto.getUsername())) {
            entity.setUsername(dto.getUsername());
        }
        if (StringUtils.hasText(dto.getRealName())) {
            entity.setRealName(dto.getRealName());
        }
        if (dto.getNickname() != null) {
            entity.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            entity.setAvatar(dto.getAvatar());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getMobile() != null) {
            entity.setMobile(dto.getMobile());
        }
        if (StringUtils.hasText(dto.getGender())) {
            entity.setGender(dto.getGender());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getIsLocked() != null) {
            entity.setIsLocked(dto.getIsLocked());
        }
        if (dto.getLockedUntil() != null) {
            entity.setLockedUntil(dto.getLockedUntil());
        }
        if (dto.getOwnerDeptId() != null) {
            entity.setOwnerDeptId(dto.getOwnerDeptId());
        }
        if (dto.getOwnerId() != null) {
            entity.setOwnerId(dto.getOwnerId());
        }
    }
}
