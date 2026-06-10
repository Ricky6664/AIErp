package com.erp.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Excel 通用导入工具.
 *
 * <p>基于 EasyExcel, 使用 ReadListener 逐行读取并收集数据和校验错误,
 * 错误行不阻断导入流程, 单表最大 {@value #MAX_ROWS} 行限制.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
public final class ExcelImportUtil {

    /** 单表最大导入行数 */
    private static final int MAX_ROWS = 10_000;

    private ExcelImportUtil() {
    }

    /**
     * 从上传文件导入 Excel 数据.
     *
     * @param <T>   数据类型 (需标注 {@code @ExcelProperty} 注解)
     * @param file  上传的 Excel 文件
     * @param clazz 数据实体类
     * @return 导入结果 (含成功列表和错误行列表)
     */
    public static <T> ImportResult<T> importExcel(MultipartFile file, Class<T> clazz) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "上传文件为空");
        }
        if (clazz == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "目标类型为空");
        }

        ImportReadListener<T> listener = new ImportReadListener<>();
        try (InputStream in = file.getInputStream()) {
            EasyExcel.read(in, clazz, listener)
                    .sheet()
                    .doRead();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Excel 导入失败: fileName={}", file.getOriginalFilename(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "导入失败: " + e.getMessage());
        }

        ImportResult<T> result = listener.buildResult();
        log.info("Excel 导入完成: fileName={}, total={}, success={}, error={}",
                file.getOriginalFilename(),
                result.getTotalRows(),
                result.getSuccessCount(),
                result.getErrorCount());
        return result;
    }

    // ========== ReadListener ==========

    /**
     * EasyExcel 导入监听器.
     *
     * <p>逐行读取, 解析成功加入 successList, 解析失败加入 errorList.
     * 错误行不阻断后续行的导入.</p>
     */
    private static class ImportReadListener<T> extends AnalysisEventListener<T> {

        private final List<T> successList = new ArrayList<>();
        private final List<ImportResult.ImportError> errorList = new ArrayList<>();
        private final AtomicInteger rowCounter = new AtomicInteger(0);

        @Override
        public void invoke(T data, AnalysisContext context) {
            int rowNum = rowCounter.incrementAndGet();
            if (rowNum > MAX_ROWS) {
                throw new BusinessException(ErrorCode.PARAM_RANGE_ERROR,
                        "单表最大导入" + MAX_ROWS + "行, 当前已超出限制");
            }
            Integer rowIndex = context.readRowHolder().getRowIndex();
            try {
                successList.add(data);
            } catch (Exception e) {
                errorList.add(new ImportResult.ImportError(
                        rowIndex != null ? rowIndex + 1 : rowNum, e.getMessage()));
                log.warn("导入行解析异常: row={}", rowIndex, e);
            }
        }

        @Override
        public void onException(Exception exception, AnalysisContext context) throws Exception {
            Integer rowIndex = context.readRowHolder().getRowIndex();
            int row = rowIndex != null ? rowIndex + 1 : rowCounter.get() + 1;
            errorList.add(new ImportResult.ImportError(row,
                    exception.getMessage() != null ? exception.getMessage() : "数据格式错误"));
            log.warn("导入行数据异常: row={}", row, exception);
            // 不重新抛出异常, 错误行不阻断导入
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("EasyExcel 读取完成: totalRows={}, successRows={}, errorRows={}",
                    successList.size() + errorList.size(),
                    successList.size(),
                    errorList.size());
        }

        ImportResult<T> buildResult() {
            ImportResult<T> result = new ImportResult<>();
            result.setSuccessList(successList);
            result.setErrorList(errorList);
            result.setTotalRows(successList.size() + errorList.size());
            result.setSuccessCount(successList.size());
            result.setErrorCount(errorList.size());
            return result;
        }
    }
}
