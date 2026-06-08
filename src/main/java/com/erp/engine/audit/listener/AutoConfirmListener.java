package com.erp.engine.audit.listener;

import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.event.DocCreatedEvent;
import com.erp.engine.audit.service.AuditConfigService;
import com.erp.engine.audit.service.AuditEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 自动确认事件监听器.
 *
 * @author AI
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AutoConfirmListener {

    private final AuditConfigService auditConfigService;
    private final AuditEngineService auditEngineService;

    @EventListener
    public void onDocCreated(DocCreatedEvent event) {
        String docType = event.getDocType();
        Long docId = event.getDocId();

        try {
            SysAuditConfigEntity config = auditConfigService.getConfig(docType);
            if (config == null || !Boolean.TRUE.equals(config.getAutoConfirm())) {
                return;
            }

            log.info("自动确认触发: docType={}, docId={}", docType, docId);

            AuditSubmitDTO submitDTO = new AuditSubmitDTO();
            submitDTO.setDocType(docType);
            submitDTO.setDocId(docId);
            submitDTO.setSubmitRemark("系统自动确认");
            auditEngineService.submit(submitDTO);

            log.info("自动确认完成: docType={}, docId={}", docType, docId);
        } catch (Exception e) {
            log.error("自动确认失败: docType={}, docId={}, error={}",
                    docType, docId, e.getMessage(), e);
        }
    }
}
