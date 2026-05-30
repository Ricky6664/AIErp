package com.erp.common.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入结果封装.
 *
 * <p>包含成功数据列表和错误行列表, 错误行不阻断导入流程.</p>
 *
 * @param <T> 数据类型
 * @author AI
 * @since 2026-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 成功导入的数据列表 */
    private List<T> successList = new ArrayList<>();

    /** 导入错误行列表 */
    private List<ImportError> errorList = new ArrayList<>();

    /** 总行数 */
    private int totalRows;

    /** 成功行数 */
    private int successCount;

    /** 错误行数 */
    private int errorCount;

    /**
     * 是否有错误.
     */
    public boolean hasErrors() {
        return errorCount > 0;
    }

    /**
     * 是否全部成功.
     */
    public boolean isAllSuccess() {
        return errorCount == 0;
    }

    /**
     * 导入错误行记录.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** 行号 (从 1 开始) */
        private int row;

        /** 错误原因 */
        private String reason;
    }
}
