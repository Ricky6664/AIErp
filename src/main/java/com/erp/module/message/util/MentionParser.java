package com.erp.module.message.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @提及解析器 - 从沟通内容中解析@提及的用户ID.
 *
 * <p>支持格式：@[userId:用户名]
 * 示例："请@[1001:张三]确认一下这个数量" → 返回 [1001]</p>
 *
 * @author AI
 */
@Component
public class MentionParser {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@\\[(\\d+):[^\\]]+\\]");

    public List<Long> parseMentionedUsers(String content) {
        if (!StringUtils.hasText(content)) {
            return Collections.emptyList();
        }
        List<Long> userIds = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while (matcher.find()) {
            userIds.add(Long.parseLong(matcher.group(1)));
        }
        return userIds.stream().distinct().collect(Collectors.toList());
    }
}
