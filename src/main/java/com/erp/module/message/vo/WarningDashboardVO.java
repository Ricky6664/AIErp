package com.erp.module.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 预警看板数据VO.
 *
 * @author AI
 */
@Data
@Schema(description = "预警看板数据")
public class WarningDashboardVO {

    @Schema(description = "按模块统计预警数量")
    private List<Map<String, Object>> moduleCounts;

    @Schema(description = "近30天预警趋势数据")
    private List<Map<String, Object>> trendData;

    @Schema(description = "预警类型分布")
    private List<Map<String, Object>> distributionData;

    @Schema(description = "预警明细列表")
    private List<WarningListItemVO> warningList;
}
