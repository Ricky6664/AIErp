package com.erp.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.AuthOnlineDevice;
import com.erp.auth.mapper.AuthOnlineDeviceMapper;
import com.erp.auth.service.OnlineDeviceService;
import com.erp.common.service.ServiceImplX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 在线设备管理 Service 实现.
 *
 * @author AI
 * @since 2026-06-04
 */
@Slf4j
@Service
public class OnlineDeviceServiceImpl extends ServiceImplX<AuthOnlineDeviceMapper, AuthOnlineDevice> implements OnlineDeviceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void kickDevice(String tokenId) {
        StpUtil.logoutByTokenValue(tokenId);

        AuthOnlineDevice device = getOne(new LambdaQueryWrapper<AuthOnlineDevice>()
                .eq(AuthOnlineDevice::getSessionTokenId, tokenId)
                .last("LIMIT 1"));
        if (device != null) {
            device.setStatus("kicked");
            updateById(device);
            log.info("设备已强制下线: tokenId={}, userId={}", tokenId, device.getUserId());
        }
    }

    @Override
    public long countOnline() {
        return count(new LambdaQueryWrapper<AuthOnlineDevice>()
                .eq(AuthOnlineDevice::getStatus, "online"));
    }
}
