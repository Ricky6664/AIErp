package com.erp.engine.audit;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.SysAuditConfigUpdateDTO;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.mapper.AuditConfigMapper;
import com.erp.engine.audit.service.AuditConfigService;
import com.erp.engine.audit.service.DownstreamChecker;
import com.erp.engine.audit.service.DownstreamCheckResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuditConfigService单元测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
class AuditConfigServiceTest {

    @Mock
    private AuditConfigMapper auditConfigMapper;
    @Mock
    private List<DownstreamChecker> downstreamCheckers;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuditConfigService auditConfigService;

    private static final String DOC_TYPE = "PURCHASE_ORDER";
    private static final String CACHE_KEY = "audit:config:PURCHASE_ORDER";

    @BeforeEach
    void setUp() {
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void updateConfig_shouldThrowWhenBothAutoConfirmAndApprovalEnabledTrue() {
        SysAuditConfigUpdateDTO dto = new SysAuditConfigUpdateDTO();
        dto.setDocType(DOC_TYPE);
        dto.setAutoConfirm(true);
        dto.setApprovalEnabled(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> auditConfigService.updateConfig(dto));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.BUSINESS_ERROR.getCode());
    }

    @Test
    void updateConfig_shouldClearRedisCache() throws Exception {
        SysAuditConfigEntity existing = new SysAuditConfigEntity();
        existing.setId(1L);
        existing.setDocType(DOC_TYPE);
        existing.setAutoConfirm(false);
        existing.setApprovalEnabled(true);
        when(auditConfigMapper.selectOne(any()))
                .thenReturn(existing);
        when(auditConfigMapper.updateById(any())).thenReturn(1);
        when(stringRedisTemplate.delete(CACHE_KEY)).thenReturn(true);

        SysAuditConfigUpdateDTO dto = new SysAuditConfigUpdateDTO();
        dto.setDocType(DOC_TYPE);
        dto.setAutoConfirm(true);
        dto.setApprovalEnabled(false);

        auditConfigService.updateConfig(dto);

        verify(stringRedisTemplate).delete(CACHE_KEY);
    }

    @Test
    void updateConfig_shouldThrowWhenConfigNotFound() {
        SysAuditConfigUpdateDTO dto = new SysAuditConfigUpdateDTO();
        dto.setDocType("NONEXISTENT");
        dto.setAutoConfirm(true);

        when(auditConfigMapper.selectOne(any())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> auditConfigService.updateConfig(dto));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.DATA_NOT_FOUND.getCode());
    }

    @Test
    void getConfig_shouldReturnFromCache() throws Exception {
        SysAuditConfigEntity entity = new SysAuditConfigEntity();
        entity.setId(1L);
        entity.setDocType(DOC_TYPE);
        entity.setAutoConfirm(true);
        String json = objectMapper.writeValueAsString(entity);

        when(valueOperations.get(CACHE_KEY)).thenReturn(json);

        SysAuditConfigEntity result = auditConfigService.getConfig(DOC_TYPE);

        assertThat(result.getDocType()).isEqualTo(DOC_TYPE);
        assertThat(result.getAutoConfirm()).isTrue();
        verify(auditConfigMapper, never()).selectOne(any());
    }

    @Test
    void getConfig_shouldFallbackToDbOnCacheMiss() throws Exception {
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);
        SysAuditConfigEntity entity = new SysAuditConfigEntity();
        entity.setId(1L);
        entity.setDocType(DOC_TYPE);
        entity.setAutoConfirm(false);
        when(auditConfigMapper.selectOne(any())).thenReturn(entity);

        SysAuditConfigEntity result = auditConfigService.getConfig(DOC_TYPE);

        assertThat(result.getDocType()).isEqualTo(DOC_TYPE);
        assertThat(result.getAutoConfirm()).isFalse();
        verify(auditConfigMapper).selectOne(any());
    }

    @Test
    void getConfig_shouldThrowWhenConfigNotFound() {
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);
        when(auditConfigMapper.selectOne(any())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> auditConfigService.getConfig(DOC_TYPE));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.DATA_NOT_FOUND.getCode());
    }

    @Test
    void updateConfig_shouldSucceedWhenOnlyAutoConfirmTrue() {
        SysAuditConfigEntity existing = new SysAuditConfigEntity();
        existing.setId(1L);
        existing.setDocType(DOC_TYPE);
        existing.setAutoConfirm(false);
        existing.setApprovalEnabled(false);
        when(auditConfigMapper.selectOne(any())).thenReturn(existing);
        when(auditConfigMapper.updateById(any())).thenReturn(1);

        SysAuditConfigUpdateDTO dto = new SysAuditConfigUpdateDTO();
        dto.setDocType(DOC_TYPE);
        dto.setAutoConfirm(true);
        dto.setApprovalEnabled(false);

        assertDoesNotThrow(() -> auditConfigService.updateConfig(dto));
        verify(stringRedisTemplate).delete(CACHE_KEY);
    }

    @Test
    void updateConfig_shouldSucceedWhenOnlyApprovalEnabledTrue() {
        SysAuditConfigEntity existing = new SysAuditConfigEntity();
        existing.setId(1L);
        existing.setDocType(DOC_TYPE);
        existing.setAutoConfirm(false);
        existing.setApprovalEnabled(false);
        when(auditConfigMapper.selectOne(any())).thenReturn(existing);
        when(auditConfigMapper.updateById(any())).thenReturn(1);

        SysAuditConfigUpdateDTO dto = new SysAuditConfigUpdateDTO();
        dto.setDocType(DOC_TYPE);
        dto.setAutoConfirm(false);
        dto.setApprovalEnabled(true);

        assertDoesNotThrow(() -> auditConfigService.updateConfig(dto));
        verify(stringRedisTemplate).delete(CACHE_KEY);
    }

    @Test
    void updateConfig_shouldNotTriggerMutualExclusionWhenAutoConfirmNull() {
        SysAuditConfigEntity existing = new SysAuditConfigEntity();
        existing.setId(1L);
        existing.setDocType(DOC_TYPE);
        existing.setApprovalEnabled(true);
        when(auditConfigMapper.selectOne(any())).thenReturn(existing);
        when(auditConfigMapper.updateById(any())).thenReturn(1);

        SysAuditConfigUpdateDTO dto = new SysAuditConfigUpdateDTO();
        dto.setDocType(DOC_TYPE);
        dto.setAutoConfirm(null);
        dto.setApprovalEnabled(true);

        assertDoesNotThrow(() -> auditConfigService.updateConfig(dto));
    }

    @Test
    void updateConfig_shouldNotTriggerMutualExclusionWhenApprovalEnabledNull() {
        SysAuditConfigEntity existing = new SysAuditConfigEntity();
        existing.setId(1L);
        existing.setDocType(DOC_TYPE);
        existing.setAutoConfirm(true);
        when(auditConfigMapper.selectOne(any())).thenReturn(existing);
        when(auditConfigMapper.updateById(any())).thenReturn(1);

        SysAuditConfigUpdateDTO dto = new SysAuditConfigUpdateDTO();
        dto.setDocType(DOC_TYPE);
        dto.setAutoConfirm(true);
        dto.setApprovalEnabled(null);

        assertDoesNotThrow(() -> auditConfigService.updateConfig(dto));
    }

    @Test
    void getConfig_shouldWriteToCacheAfterDbFallback() throws Exception {
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);
        SysAuditConfigEntity entity = new SysAuditConfigEntity();
        entity.setId(1L);
        entity.setDocType(DOC_TYPE);
        entity.setAutoConfirm(true);
        entity.setApprovalEnabled(false);
        when(auditConfigMapper.selectOne(any())).thenReturn(entity);

        auditConfigService.getConfig(DOC_TYPE);

        verify(valueOperations).set(eq(CACHE_KEY), anyString(),
                eq(2L), eq(TimeUnit.HOURS));
    }

    @Test
    void getDownstreamCheckers_shouldFilterByDocType() {
        DownstreamChecker saleChecker = new DownstreamChecker() {
            @Override
            public DownstreamCheckResult check(Long docId) {
                return DownstreamCheckResult.none();
            }
            @Override
            public String supportedDocType() {
                return "sale_order";
            }
        };
        DownstreamChecker purchaseChecker = new DownstreamChecker() {
            @Override
            public DownstreamCheckResult check(Long docId) {
                return DownstreamCheckResult.exists("PO-001");
            }
            @Override
            public String supportedDocType() {
                return "purchase_order";
            }
        };
        List<DownstreamChecker> realList = List.of(saleChecker, purchaseChecker);
        when(downstreamCheckers.iterator()).thenReturn(realList.iterator());

        List<DownstreamChecker> result = auditConfigService.getDownstreamCheckers("purchase_order");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).supportedDocType()).isEqualTo("purchase_order");
    }
}
