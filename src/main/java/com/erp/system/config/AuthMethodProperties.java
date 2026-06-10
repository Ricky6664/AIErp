package com.erp.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 认证方式配置页属性.
 *
 * <p>绑定 application.yml 中 auth-method.* 配置项, 提供类型安全的配置访问.
 * 在应用启动时自动执行 @Validated 校验, 配置错误会导致启动失败.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>auth-method.page.default-page-size — 分页默认每页条数，默认 20</li>
 *   <li>auth-method.page.default-sort-field — 默认排序字段，默认 priority</li>
 *   <li>auth-method.page.show-disabled — 列表中是否显示已禁用的认证方式，默认 true</li>
 *   <li>auth-method.form.method-name-min-length — 认证方式名称最小长度，默认 2</li>
 *   <li>auth-method.form.method-name-max-length — 认证方式名称最大长度，默认 50</li>
 *   <li>auth-method.form.require-config-json — 新建时是否要求填写配置JSON，默认 false</li>
 *   <li>auth-method.priority.min — 优先级最小值，默认 1</li>
 *   <li>auth-method.priority.max — 优先级最大值，默认 999</li>
 *   <li>auth-method.priority.default-priority — 新建认证方式的默认优先级，默认 100</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-05
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "auth-method")
public class AuthMethodProperties {

    private Page page = new Page();

    private Form form = new Form();

    private Priority priority = new Priority();

    @Data
    public static class Page {

        private int defaultPageSize = 20;

        private String defaultSortField = "priority";

        private boolean showDisabled = true;
    }

    @Data
    public static class Form {

        private int methodNameMinLength = 2;

        private int methodNameMaxLength = 50;

        private boolean requireConfigJson = false;
    }

    @Data
    public static class Priority {

        private int min = 1;

        private int max = 999;

        private int defaultPriority = 100;
    }
}
