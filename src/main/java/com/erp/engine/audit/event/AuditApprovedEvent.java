package com.erp.engine.audit.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

/**
 * 审核通过事件.
 *
 * @author AI
 */
@Getter
public class AuditApprovedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String docType;
    private final Long docId;

    public AuditApprovedEvent(Object source, String docType, Long docId) {
        super(source);
        this.docType = docType;
        this.docId = docId;
    }
}
