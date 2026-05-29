package com.erp.system.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.erp.config.SaTokenProperties;
import com.erp.vo.LoginUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 会话管理服务.
 *
 * <p>提供在线用户查询、强制下线、会话续期、当前用户信息获取等功能.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SaTokenProperties saTokenProperties;

    /**
     * 查询在线用户列表.
     *
     * <p>调用 StpUtil.searchSessionId 分页搜索当前在线会话, 遍历每个会话提取用户信息.
     * 默认查询全部在线用户 (keyword=""、page=1、size=100).</p>
     *
     * @return 在线用户 VO 列表
     */
    public List<LoginUserVO> listOnline() {
        return listOnline("", 1, 100);
    }

    /**
     * 分页查询在线用户列表.
     *
     * @param keyword 搜索关键词(匹配 token 值), 空字符串表示查询全部
     * @param page    页码(从 1 开始)
     * @param size    每页条数
     * @return 在线用户 VO 列表
     */
    public List<LoginUserVO> listOnline(String keyword, int page, int size) {
        int start = (page - 1) * size;
        List<String> sessionIds = StpUtil.searchSessionId(keyword, start, size, false);
        if (sessionIds == null || sessionIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<LoginUserVO> result = new ArrayList<>(sessionIds.size());
        for (String sessionId : sessionIds) {
            try {
                SaSession session = StpUtil.getSessionBySessionId(sessionId);
                LoginUserVO vo = buildLoginUserVO(session);
                result.add(vo);
            } catch (Exception e) {
                log.warn("获取会话信息失败: sessionId={}", sessionId, e);
            }
        }
        return result;
    }

    /**
     * 强制指定 Token 下线.
     *
     * <p>调用 StpUtil.logoutByTokenValue 使指定 Token 立即失效.</p>
     *
     * @param token 目标会话的 Token 值
     */
    public void forceLogout(String token) {
        StpUtil.logoutByTokenValue(token);
        log.info("已强制下线: token={}", token);
    }

    /**
     * 续期当前会话超时时间.
     *
     * <p>调用 StpUtil.renewTimeout 将当前登录会话的过期时间重置为配置值.</p>
     */
    public void renewSession() {
        StpUtil.renewTimeout(saTokenProperties.getTimeout());
        log.debug("会话已续期: timeout={}s", saTokenProperties.getTimeout());
    }

    /**
     * 获取当前登录用户信息.
     *
     * <p>从当前 SaSession 中提取登录用户信息.</p>
     *
     * @return 当前登录用户 VO
     */
    public LoginUserVO getCurrentUser() {
        SaSession session = StpUtil.getSession();
        return buildLoginUserVO(session);
    }

    private LoginUserVO buildLoginUserVO(SaSession session) {
        String token = StpUtil.getTokenValue();
        Object loginId = session.getLoginId();
        LoginUserVO.LoginUserVOBuilder builder = LoginUserVO.builder()
                .token(token)
                .loginId(loginId != null ? loginId.toString() : null);

        long createTime = session.getCreateTime();
        builder.loginTime(formatTimestamp(createTime));

        var tokenSigns = session.getTokenSignList();
        if (tokenSigns != null && !tokenSigns.isEmpty()) {
            var tokenSign = tokenSigns.get(0);
            builder.loginDevice(tokenSign.getDevice());
            builder.lastActiveTime(formatTimestamp(createTime));
        }

        return builder.build();
    }

    private String formatTimestamp(long epochMilli) {
        return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault())
                .format(FORMATTER);
    }
}
