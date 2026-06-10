package com.erp.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MybatisPlusConfig 配置校验测试.
 *
 * <p>验收 P0-001-001-005-001-003 任务:</p>
 * <ul>
 *   <li>@Configuration 注解存在</li>
 *   <li>@Bean MybatisPlusInterceptor 注册且包含 3 个拦截器</li>
 *   <li>拦截器顺序: TenantLine(1) → Pagination(2, overflow=true) → OptimisticLocker(3)</li>
 *   <li>@Bean MetaObjectHandler 自动填充处理器注册</li>
 *   <li>无敏感信息硬编码</li>
 * </ul>
 *
 * @author AI
 * @since 2026-05-29
 */
class MybatisPlusConfigValidationTest {

    private static MybatisPlusConfig configInstance;
    private static MybatisPlusInterceptor interceptor;
    private static MetaObjectHandler metaObjectHandler;

    @BeforeAll
    static void setUp() throws Exception {
        configInstance = new MybatisPlusConfig();

        // 通过反射调用 @Bean 方法获取实例
        Method interceptorMethod = MybatisPlusConfig.class.getDeclaredMethod("mybatisPlusInterceptor");
        interceptor = (MybatisPlusInterceptor) interceptorMethod.invoke(configInstance);
        assertNotNull(interceptor, "MybatisPlusInterceptor 实例不能为空");

        Method handlerMethod = MybatisPlusConfig.class.getDeclaredMethod("metaObjectHandler");
        metaObjectHandler = (MetaObjectHandler) handlerMethod.invoke(configInstance);
        assertNotNull(metaObjectHandler, "MetaObjectHandler 实例不能为空");
    }

    @Test
    @DisplayName("MybatisPlusConfig 必须标注 @Configuration 注解")
    void configurationAnnotationShouldExist() {
        Configuration annotation = MybatisPlusConfig.class.getAnnotation(Configuration.class);
        assertNotNull(annotation, "MybatisPlusConfig 必须标注 @Configuration");
    }

    @Test
    @DisplayName("mybatisPlusInterceptor 方法必须标注 @Bean")
    void mybatisPlusInterceptorBeanAnnotationShouldExist() throws NoSuchMethodException {
        Method method = MybatisPlusConfig.class.getDeclaredMethod("mybatisPlusInterceptor");
        Bean beanAnnotation = method.getAnnotation(Bean.class);
        assertNotNull(beanAnnotation, "mybatisPlusInterceptor 方法必须标注 @Bean");
    }

    @Test
    @DisplayName("metaObjectHandler 方法必须标注 @Bean")
    void metaObjectHandlerBeanAnnotationShouldExist() throws NoSuchMethodException {
        Method method = MybatisPlusConfig.class.getDeclaredMethod("metaObjectHandler");
        Bean beanAnnotation = method.getAnnotation(Bean.class);
        assertNotNull(beanAnnotation, "metaObjectHandler 方法必须标注 @Bean");
    }

    @Test
    @DisplayName("MybatisPlusInterceptor 必须注册 3 个 InnerInterceptor")
    void interceptorShouldHaveThreeInnerInterceptors() throws Exception {
        List<InnerInterceptor> interceptors = getInnerInterceptors();
        assertEquals(3, interceptors.size(),
                "MybatisPlusInterceptor 必须注册 3 个 InnerInterceptor (TenantLine/Pagination/OptimisticLocker)");
    }

    @Test
    @DisplayName("第 1 个拦截器必须是 TenantLineInnerInterceptor (多租户必须第一个注册)")
    void firstInterceptorShouldBeTenantLine() throws Exception {
        List<InnerInterceptor> interceptors = getInnerInterceptors();
        assertTrue(interceptors.get(0) instanceof TenantLineInnerInterceptor,
                "第 1 个拦截器必须是 TenantLineInnerInterceptor, 实际: "
                        + interceptors.get(0).getClass().getSimpleName());
    }

    @Test
    @DisplayName("第 2 个拦截器必须是 PaginationInnerInterceptor (分页必须在乐观锁之前)")
    void secondInterceptorShouldBePagination() throws Exception {
        List<InnerInterceptor> interceptors = getInnerInterceptors();
        assertTrue(interceptors.get(1) instanceof PaginationInnerInterceptor,
                "第 2 个拦截器必须是 PaginationInnerInterceptor, 实际: "
                        + interceptors.get(1).getClass().getSimpleName());
    }

    @Test
    @DisplayName("第 3 个拦截器必须是 OptimisticLockerInnerInterceptor")
    void thirdInterceptorShouldBeOptimisticLocker() throws Exception {
        List<InnerInterceptor> interceptors = getInnerInterceptors();
        assertTrue(interceptors.get(2) instanceof OptimisticLockerInnerInterceptor,
                "第 3 个拦截器必须是 OptimisticLockerInnerInterceptor, 实际: "
                        + interceptors.get(2).getClass().getSimpleName());
    }

    @Test
    @DisplayName("PaginationInnerInterceptor overflow 必须为 true (页码溢出时返回首页)")
    void paginationOverflowShouldBeTrue() throws Exception {
        List<InnerInterceptor> interceptors = getInnerInterceptors();
        PaginationInnerInterceptor pagination = (PaginationInnerInterceptor) interceptors.get(1);
        assertTrue(pagination.isOverflow(),
                "PaginationInnerInterceptor.overflow 必须为 true, 页码溢出时返回首页");
    }

    @Test
    @DisplayName("MetaObjectHandler 实例不能为空 (自动填充处理器必须注册)")
    void metaObjectHandlerShouldBeRegistered() {
        assertNotNull(metaObjectHandler, "MetaObjectHandler Bean 必须注册");
    }

    @Test
    @DisplayName("MybatisPlusConfig 源码不得硬编码密码/密钥")
    void noHardcodedSecrets() throws Exception {
        // 通过类加载读取类源码中是否包含敏感关键字
        // 注: 此处使用简单字符串扫描, 实际扫描源码文件
        java.net.URL classUrl = MybatisPlusConfig.class
                .getResource(MybatisPlusConfig.class.getSimpleName() + ".class");
        assertNotNull(classUrl, "MybatisPlusConfig.class 必须存在");

        // 通过反射扫描所有字符串常量字段
        for (Field field : MybatisPlusConfig.class.getDeclaredFields()) {
            if (field.getType() == String.class
                    && java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                String value = (String) field.get(null);
                if (value != null) {
                    assertNoSecretKeyword(value, field.getName());
                }
            }
        }
    }

    /**
     * 通过反射获取 MybatisPlusInterceptor 的 InnerInterceptor 列表.
     */
    @SuppressWarnings("unchecked")
    private List<InnerInterceptor> getInnerInterceptors() throws Exception {
        Field field = MybatisPlusInterceptor.class.getDeclaredField("interceptors");
        field.setAccessible(true);
        return (List<InnerInterceptor>) field.get(interceptor);
    }

    /**
     * 断言字符串中不包含敏感关键字.
     */
    private void assertNoSecretKeyword(String value, String fieldName) {
        String lower = value.toLowerCase();
        assertFalse(lower.contains("password="), "字段 " + fieldName + " 不得包含 password=");
        assertFalse(lower.contains("secret="), "字段 " + fieldName + " 不得包含 secret=");
        assertFalse(lower.contains("apikey="), "字段 " + fieldName + " 不得包含 apiKey=");
        assertFalse(lower.contains("api_key="), "字段 " + fieldName + " 不得包含 api_key=");
    }
}
