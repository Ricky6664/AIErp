package com.erp.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 拦截器与 CORS 跨域配置.
 *
 * @author AI
 * @since 2026-05-29
 */
@Configuration
public class SaInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    SaRouter.match("/api/**")
                            .notMatch("/api/auth/**", "/doc.html", "/v3/api-docs/**", "/favicon.ico")
                            .check(StpUtil::checkLogin);
                }).isAnnotation(true))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/doc.html", "/v3/api-docs/**", "/favicon.ico");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
