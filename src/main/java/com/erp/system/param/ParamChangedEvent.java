package com.erp.system.param;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

/**
 * 系统参数变更事件.
 *
 * <p>当管理后台修改系统参数时发布此事件, 通知所有节点刷新缓存.
 * 支持多实例部署场景, 通过 Redis Pub/Sub 或事件总线传播到所有节点.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Getter
public class ParamChangedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String category;
    private final String key;

    public ParamChangedEvent(Object source, String category, String key) {
        super(source);
        this.category = category;
        this.key = key;
    }
}
