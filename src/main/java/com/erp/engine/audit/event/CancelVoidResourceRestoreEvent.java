package com.erp.engine.audit.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

/**
 * 撤销作废资源恢复事件.
 * 事务提交后发布，通知下游模块重新获取作废时释放的资源（库存预留、预算冻结等）.
 *
 * @author AI
 */
@Getter
public class CancelVoidResourceRestoreEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String docType;
    private final Long docId;
    private final int restoredStatus;

    public CancelVoidResourceRestoreEvent(Object source, String docType,
                                          Long docId, int restoredStatus) {
        super(source);
        this.docType = docType;
        this.docId = docId;
        this.restoredStatus = restoredStatus;
    }
}
