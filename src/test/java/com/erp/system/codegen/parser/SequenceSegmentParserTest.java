package com.erp.system.codegen.parser;

import com.erp.common.exception.BusinessException;
import com.erp.system.codegen.SequenceGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SequenceSegmentParser - 序列段解析器")
class SequenceSegmentParserTest {

    @Mock
    private SequenceGenerator sequenceGenerator;

    private SequenceSegmentParser parser;

    @BeforeEach
    void setUp() {
        parser = new SequenceSegmentParser(sequenceGenerator);
    }

    @Test
    @DisplayName("默认4位左补零")
    void shouldPadTo4DigitsByDefault() {
        when(sequenceGenerator.next("RULE001")).thenReturn(5L);

        SegmentParseContext ctx = SegmentParseContext.builder()
                .ruleCode("RULE001")
                .build();

        String result = parser.parse(ctx);

        assertEquals("0005", result);
    }

    @Test
    @DisplayName("按segmentLength左补零到指定位数")
    void shouldPadToSpecifiedDigits() {
        when(sequenceGenerator.next("RULE001")).thenReturn(42L);

        SegmentParseContext ctx = SegmentParseContext.builder()
                .ruleCode("RULE001")
                .segmentLength(6)
                .build();

        String result = parser.parse(ctx);

        assertEquals("000042", result);
    }

    @Test
    @DisplayName("序列号位数等于segmentLength时不补零")
    void shouldNotPadWhenLengthMatches() {
        when(sequenceGenerator.next("RULE001")).thenReturn(1234L);

        SegmentParseContext ctx = SegmentParseContext.builder()
                .ruleCode("RULE001")
                .segmentLength(4)
                .build();

        String result = parser.parse(ctx);

        assertEquals("1234", result);
    }

    @Test
    @DisplayName("预览模式返回固定值1")
    void shouldReturnPreviewValue() {
        SegmentParseContext ctx = SegmentParseContext.builder()
                .ruleCode("RULE001")
                .segmentLength(5)
                .preview(true)
                .build();

        String result = parser.parse(ctx);

        assertEquals("00001", result);
        verify(sequenceGenerator, never()).next(anyString());
    }

    @Test
    @DisplayName("成功获取序列号不重试")
    void shouldNotRetryOnSuccess() {
        when(sequenceGenerator.next("RULE001")).thenReturn(100L);

        SegmentParseContext ctx = SegmentParseContext.builder()
                .ruleCode("RULE001")
                .segmentLength(3)
                .build();

        String result = parser.parse(ctx);

        assertEquals("100", result);
        verify(sequenceGenerator, times(1)).next("RULE001");
    }

    @Test
    @DisplayName("第一次失败第二次成功时重试")
    void shouldRetryOnFirstFailure() {
        when(sequenceGenerator.next("RULE001"))
                .thenThrow(new RuntimeException("Redis timeout"))
                .thenReturn(7L);

        SegmentParseContext ctx = SegmentParseContext.builder()
                .ruleCode("RULE001")
                .segmentLength(3)
                .build();

        String result = parser.parse(ctx);

        assertEquals("007", result);
        verify(sequenceGenerator, times(2)).next("RULE001");
    }

    @Test
    @DisplayName("三次全部失败抛出BusinessException")
    void shouldThrowAfterMaxRetries() {
        when(sequenceGenerator.next("RULE001"))
                .thenThrow(new RuntimeException("fail1"))
                .thenThrow(new RuntimeException("fail2"))
                .thenThrow(new RuntimeException("fail3"));

        SegmentParseContext ctx = SegmentParseContext.builder()
                .ruleCode("RULE001")
                .segmentLength(3)
                .build();

        assertThrows(BusinessException.class, () -> parser.parse(ctx));
        verify(sequenceGenerator, times(3)).next("RULE001");
    }

    @Test
    @DisplayName("segmentLength为null时默认4位")
    void shouldDefaultTo4WhenLengthNull() {
        when(sequenceGenerator.next("RULE001")).thenReturn(1L);

        SegmentParseContext ctx = SegmentParseContext.builder()
                .ruleCode("RULE001")
                .build();

        String result = parser.parse(ctx);

        assertEquals("0001", result);
    }

    @Test
    @DisplayName("getSegmentType返回3")
    void shouldReturnType3() {
        assertEquals(3, parser.getSegmentType());
    }
}
