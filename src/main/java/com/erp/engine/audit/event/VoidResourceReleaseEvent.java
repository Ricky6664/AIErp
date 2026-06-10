package com.erp.engine.audit.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

/**
 * 作废资源释放事件.
 * 事务提交后发布，通知下游模块释放关联资源（库存预留、预算冻结等）.
 *
 * @author AI
 */
@Getter
public class VoidResourceReleaseEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String docType;
    private final Long docId;
    private final int fromStatus;

    public VoidResourceReleaseEvent(Object source, String docType, Long docId, int fromStatus) {
        super(source);
        this.docType = docType;
        this.docId = docId;
        this.fromStatus = fromStatus;
    }
}
