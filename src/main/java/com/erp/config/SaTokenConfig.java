package com.erp.config;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.result.RT;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置类.
 *
 * <p>配置路由拦截 + 注解鉴权双机制:
 * <ul>
 *   <li>路由拦截: SaInterceptor 拦截 /api/** 路径, 校验登录状态</li>
 *   <li>注解鉴权: 支持 @SaCheckLogin / @SaCheckPermission 等注解</li>
 *   <li>Servlet 过滤器: SaServletFilter 自定义未登录响应为 RT.fail(ErrorCode.UNAUTHORIZED)</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /** 需要排除登录校验的路径 */
    private static final String[] EXCLUDE_PATHS = {
            "/api/auth/login",
            "/api/auth/logout",
            "/doc.html",
            "/v3/api-docs/**"
    };

    /**
     * 注册 Sa-Token 路由拦截器, 拦截 /api/** 并排除登录/登出/文档路径.
     * isAnnotation=true 同时启用注解鉴权机制.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    SaRouter.match("/api/**")
                            .notMatch(EXCLUDE_PATHS)
                            .check(StpUtil::checkLogin);
                }).isAnnotation(true))
                .addPathPatterns("/api/**")
                .excludePathPatterns(EXCLUDE_PATHS);
    }

    /**
     * 注册 SaServletFilter, 自定义未登录响应体为统一 RT 格式.
     */
    @Bean
    public SaServletFilter saServletFilter() {
        return new SaServletFilter()
                .addInclude("/api/**")
                .addExclude(EXCLUDE_PATHS)
                .setAuth(obj -> StpUtil.checkLogin())
                .setError(e -> RT.fail(ErrorCode.UNAUTHORIZED));
    }
}
