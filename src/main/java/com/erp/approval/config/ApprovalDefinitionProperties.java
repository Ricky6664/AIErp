package com.erp.approval.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 审批定义配置属性.
 *
 * <p>绑定 application.yml 中 approval.definition.* 配置项, 提供类型安全的配置访问.</p>
 *
 * @author AI
 * @since 2026-06-09
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "approval.definition")
public class ApprovalDefinitionProperties {

    private Pagination pagination = new Pagination();

    private Sort sort = new Sort();

    private Node node = new Node();

    private Cache cache = new Cache();

    @Data
    public static class Pagination {

        @Min(value = 5, message = "approval.definition.pagination.default-page-size 最小为 5")
        @Max(value = 100, message = "approval.definition.pagination.default-page-size 最大为 100")
        private int defaultPageSize = 10;

        private int maxPageSize = 50;
    }

    @Data
    public static class Sort {

        private String defaultField = "createTime";

        private String defaultOrder = "desc";
    }

    @Data
    public static class Node {

        @Min(value = 1, message = "approval.definition.node.max-nodes-per-definition 必须大于 0")
        private int maxNodesPerDefinition = 20;

        private boolean allowEmptyNodeName = false;
    }

    @Data
    public static class Cache {

        private boolean enabled = true;

        @Min(value = 1, message = "approval.definition.cache.ttl-minutes 必须大于 0")
        private int ttlMinutes = 30;
    }
}
