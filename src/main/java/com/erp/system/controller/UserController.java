package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.annotation.RequirePermission;
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
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @RequirePermission("system:user:query")
    @GetMapping("/page")
    public RT<PageResult<SysUserVO.ListVO>> pageList(@Valid PageQuery query, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreateTime);
        PageResult<SysUser> pageResult = userService.pageList(query, wrapper);
        List<SysUserVO.ListVO> voList = pageResult.getList().stream()
                .map(this::toListVO)
                .collect(Collectors.toList());
        PageResult<SysUserVO.ListVO> result = PageResult.of(voList,
                pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
        return RT.ok(result);
    }

    @Operation(summary = "查询用户详情")
    @RequirePermission("system:user:query")
    @GetMapping("/{id}")
    public RT<SysUserVO.DetailVO> getById(@PathVariable Long id) {
        SysUser entity = userService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(toDetailVO(entity));
    }

    @Operation(summary = "新增用户")
    @RequirePermission("system:user:create")
    @PostMapping
    public RT<Long> create(@Valid @RequestBody SysUserDTO.CreateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        SysUser entity = toEntity(dto);
        userService.save(entity);
        return RT.ok(entity.getId());
    }

    @Operation(summary = "修改用户")
    @RequirePermission("system:user:update")
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
        mergeEntity(entity, dto);
        userService.updateById(entity);
        return RT.ok();
    }

    @Operation(summary = "删除用户")
    @RequirePermission("system:user:delete")
    @DeleteMapping("/{id}")
    public RT<Boolean> delete(@PathVariable Long id) {
        boolean result = userService.removeById(id);
        return RT.ok(result);
    }

    @Operation(summary = "分配用户角色")
    @RequirePermission("system:user:role-assign")
    @PostMapping("/{id}/roles")
    public RT<Void> assignRoles(@PathVariable Long id,
                                @Valid @RequestBody SysUserDTO.RoleAssignDTO dto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        userService.assignRoles(id, dto.getRoleIds());
        return RT.ok();
    }

    @Operation(summary = "查询用户角色名称列表")
    @RequirePermission("system:user:query")
    @GetMapping("/{id}/roles")
    public RT<List<String>> getRoleNames(@PathVariable Long id) {
        return RT.ok(userService.getRoleNames(id));
    }

    @Operation(summary = "修改密码")
    @RequirePermission("system:user:password-change")
    @PutMapping("/{id}/password")
    public RT<Void> changePassword(@PathVariable Long id,
                                   @Valid @RequestBody SysUserDTO.ChangePwdDTO dto,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        userService.changePassword(id, dto.getOldPassword(), dto.getNewPassword());
        return RT.ok();
    }

    @Operation(summary = "重置用户密码")
    @RequirePermission("system:user:password-reset")
    @PutMapping("/{id}/password/reset")
    public RT<Void> resetPassword(@PathVariable Long id,
                                  @Valid @RequestBody SysUserDTO.ResetPwdDTO dto,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        userService.resetPassword(id, dto.getNewPassword());
        return RT.ok();
    }

    @Operation(summary = "修改用户状态")
    @RequirePermission("system:user:status")
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
    @RequirePermission("system:user:unlock")
    @PutMapping("/{id}/unlock")
    public RT<Void> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return RT.ok();
    }

    @Operation(summary = "检查用户名唯一性")
    @RequirePermission("system:user:query")
    @GetMapping("/check-username")
    public RT<Boolean> checkUsername(@RequestParam String username,
                                     @RequestParam(required = false) Long excludeId) {
        return RT.ok(userService.isUsernameUnique(username, excludeId));
    }

    // ========== Helper Methods ==========

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
        BeanUtils.copyProperties(dto, entity);
        if (StringUtils.hasText(dto.getPassword())) {
            entity.setPasswordHash(dto.getPassword());
        }
        entity.setStatus("normal");
        entity.setIsLocked(false);
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
