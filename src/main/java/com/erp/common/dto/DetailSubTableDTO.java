package com.erp.common.dto;

import lombok.Data;

import java.util.List;

/**
 * 单据从表统一DTO，包含三类从表数据列表
 *
 * @author AI
 */
@Data
public class DetailSubTableDTO {

    /** 库位从表数据 */
    private List<?> locations;

    /** 批次从表数据 */
    private List<?> batches;

    /** 序列号从表数据 */
    private List<?> serials;

    public boolean isEmpty() {
        return (locations == null || locations.isEmpty())
            && (batches == null || batches.isEmpty())
            && (serials == null || serials.isEmpty());
    }
}
