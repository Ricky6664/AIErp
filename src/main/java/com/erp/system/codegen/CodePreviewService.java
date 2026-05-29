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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编码预览服务 —— 解析编码规则各段并生成预览结果, 不消耗真实序列号.
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
     * 预览编码, 使用默认变量值.
     *
     * @param ruleId 规则ID
     * @param count  生成预览数量, 默认5
     * @return 预览编码列表
     */
    public List<String> preview(Long ruleId, int count) {
        return preview(ruleId, count, null);
    }

    /**
     * 预览编码, 支持传入自定义变量值.
     *
     * @param ruleId    规则ID
     * @param count     生成预览数量
     * @param variables 自定义变量值, key=变量名, value=预览值; 传入 null 则使用默认占位值
     * @return 预览编码列表
     */
    public List<String> preview(Long ruleId, int count, Map<String, String> variables) {
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
        Map<String, String> effectiveVars = buildEffectiveVariables(segments, variables);
        String separator = rule.getSeparator() != null ? rule.getSeparator() : "";
        List<String> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            results.add(buildPreviewCode(rule, segments, separator, effectiveVars));
        }
        log.info("编码预览完成: ruleCode={}, count={}, first={}", rule.getRuleCode(), count,
                results.isEmpty() ? "N/A" : results.get(0));
        return results;
    }

    public List<String> preview(Long ruleId) {
        return preview(ruleId, 5, null);
    }

    /**
     * 构建预览用有效变量 Map, 优先使用传入的自定义值, 缺失的变量段使用占位值.
     */
    private Map<String, String> buildEffectiveVariables(List<SysCodeRuleSegment> segments, Map<String, String> provided) {
        Map<String, String> vars = new HashMap<>();
        for (SysCodeRuleSegment seg : segments) {
            if (seg.getSegmentType() != null && seg.getSegmentType() == 4) {
                String key = seg.getSegmentValue();
                if (key != null && !key.isEmpty()) {
                    vars.putIfAbsent(key, "{" + key + "}");
                }
            }
        }
        if (provided != null) {
            vars.putAll(provided);
        }
        return vars;
    }

    private String buildPreviewCode(SysCodeRule rule, List<SysCodeRuleSegment> segments, String separator,
                                     Map<String, String> variables) {
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
                    .variables(variables)
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
