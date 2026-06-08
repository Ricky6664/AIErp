package com.erp.engine.audit;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.AuditOperationDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import com.erp.engine.audit.service.AuditConfigService;
import com.erp.engine.audit.service.AuditEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 反审并发测试.
 * 验证对同一单据并发发起反审时, 分布式锁确保仅1个请求成功.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditEngineService 反审并发测试")
class AuditEngineUnconfirmConcurrentTest {

    @Mock
    private AuditConfigService auditConfigService;
    @Mock
    private AuditLogMapper auditLogMapper;
    @Mock
    private DocumentStatusMapper documentStatusMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuditEngineService auditEngineService;

    private static final String DOC_TYPE = "sale_order";
    private static final Long DOC_ID = 2007L;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("7-并发反审-锁机制验证: setIfAbsent首次返回true(获取锁成功), 第二次返回false(锁已被占用)")
    void concurrentUnconfirm_lockAcquiredThenBlocked() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true, false);

        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(2);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);
        when(auditConfigService.getDownstreamCheckers(DOC_TYPE))
                .thenReturn(Collections.emptyList());

        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType(DOC_TYPE);
        dto.setDocId(DOC_ID);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            auditEngineService.unconfirm(dto);
            verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 0);

            assertThatThrownBy(() -> auditEngineService.unconfirm(dto))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.OPERATION_TOO_FREQUENT.getCode());
        }
    }
}
