package com.erp.auth.service;

import com.erp.auth.entity.AuthOnlineDevice;
import com.erp.common.service.IServiceX;

/**
 * 在线设备管理 Service 接口.
 *
 * @author AI
 * @since 2026-06-04
 */
public interface OnlineDeviceService extends IServiceX<AuthOnlineDevice> {

    void kickDevice(String tokenId);

    long countOnline();
}
