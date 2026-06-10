package com.erp.module.message.warning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预警匹配结果VO.
 *
 * @author AI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarningMatchResult {

    private String businessId;

    private String module;

    private String title;

    private String content;
}
