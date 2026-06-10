package com.erp.auth.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.SysUser;
import com.erp.auth.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 密码过期定时任务 — 每日扫描过期用户.
 *
 * @author AI
 * @since 2026-06-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordExpireTask {

    private final SysUserMapper sysUserMapper;

    @Scheduled(cron = "0 0 2 * * ?")
    public void scanExpiredPasswords() {
        log.info("密码过期扫描任务开始");
        try {
            List<SysUser> expiredUsers = sysUserMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .isNotNull(SysUser::getPasswordExpireDate)
                            .lt(SysUser::getPasswordExpireDate, LocalDate.now())
            );
            int count = expiredUsers != null ? expiredUsers.size() : 0;
            log.info("密码过期扫描任务完成，过期用户数: {}", count);
        } catch (Exception e) {
            log.error("密码过期扫描任务异常", e);
        }
    }
}
