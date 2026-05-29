package com.erp.system.codegen;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.codegen.parser.SegmentParseContext;
import com.erp.system.codegen.parser.SegmentParser;
import com.erp.system.codegen.parser.SegmentParserFactory;
import com.erp.system.entity.SysCodeRule;
import com.erp.system.entity.SysCodeRuleSegment;
import com.erp.system.mapper.SysCodeRuleMapper;
import com.erp.system.mapper.SysCodeRuleSegmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 编码预览服务.
 *
 * @author AI
 * @since 2026-05-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodePreviewService {

    private final SysCodeRuleMapper ruleMapper;
    private final SysCodeRuleSegmentMapper segmentMapper;
    private final SegmentParserFactory parserFactory;

    /**
     * 预览编码.
     *
     * @param ruleId 规则ID
     * @param count  生成预览数量, 默认5
     * @return 预览编码列表
     */
    public List<String> preview(Long ruleId, int count) {
        SysCodeRule rule = ruleMapper.selectById(ruleId);
        if (rule == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "编码规则不存在: id=" + ruleId);
        }
        List<SysCodeRuleSegment> segments = segmentMapper.selectList(
                new LambdaQueryWrapper<SysCodeRuleSegment>()
                        .eq(SysCodeRuleSegment::getRuleId, ruleId)
                        .orderByAsc(SysCodeRuleSegment::getSegmentOrder));
        if (segments == null || segments.isEmpty()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码规则未配置编码段");
        }
        String separator = rule.getSeparator() != null ? rule.getSeparator() : "";
        List<String> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            results.add(buildPreviewCode(rule, segments, separator));
        }
        log.info("编码预览完成: ruleCode={}, count={}, first={}", rule.getRuleCode(), count,
                results.isEmpty() ? "N/A" : results.get(0));
        return results;
    }

    public List<String> preview(Long ruleId) {
        return preview(ruleId, 5);
    }

    private String buildPreviewCode(SysCodeRule rule, List<SysCodeRuleSegment> segments, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            SysCodeRuleSegment seg = segments.get(i);
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentType(seg.getSegmentType())
                    .segmentValue(seg.getSegmentValue())
                    .segmentFormat(seg.getSegmentFormat())
                    .segmentLength(seg.getSegmentLength())
                    .ruleCode(rule.getRuleCode())
                    .preview(true)
                    .build();
            SegmentParser parser = parserFactory.getParser(seg.getSegmentType());
            sb.append(parser.parse(ctx));
            if (i < segments.size() - 1 && !separator.isEmpty()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
}
