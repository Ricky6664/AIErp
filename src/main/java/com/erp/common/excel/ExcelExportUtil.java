package com.erp.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.RT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel 通用导出工具.
 *
 * <p>基于 EasyExcel, 支持 @ExcelProperty 列注解和分批写入(每批 5000 行), 防止大数据量 OOM.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
public final class ExcelExportUtil {

    /** 每批写入行数 */
    private static final int BATCH_SIZE = 5_000;

    /** 单表最大导出行数 */
    private static final int MAX_ROWS = 10_000;

    private ExcelExportUtil() {
    }

    /**
     * 导出 Excel 到 HTTP 响应流.
     *
     * @param <T>      数据类型 (需标注 {@code @ExcelProperty} 注解)
     * @param response HttpServletResponse
     * @param fileName 文件名 (不含扩展名, 自动 URL 编码)
     * @param clazz    数据实体类
     * @param data     导出数据 (空集合导出空文件含表头)
     */
    public static <T> void export(HttpServletResponse response, String fileName,
                                   Class<T> clazz, List<T> data) {
        if (response == null || fileName == null || fileName.isBlank() || clazz == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING);
        }

        if (data != null && data.size() > MAX_ROWS) {
            log.warn("导出数据行数({})超过单表最大行数限制({})", data.size(), MAX_ROWS);
            throw new BusinessException(ErrorCode.PARAM_RANGE_ERROR);
        }

        setResponseHeaders(response, fileName);

        try {
            doExport(response, fileName, clazz, data);
        } catch (Exception e) {
            log.error("Excel 导出失败: fileName={}", fileName, e);
            writeErrorResponse(response, RT.fail(ErrorCode.INTERNAL_ERROR, "导出失败"));
        }
    }

    /**
     * 写入错误响应 (RT.fail JSON).
     *
     * <p>尝试重置已设置的 Excel 响应头, 改为 JSON 格式返回错误信息.
     * 若响应已提交则无法重置, 仅记录日志.</p>
     *
     * @param response    HttpServletResponse
     * @param errorResult 错误响应体
     */
    private static void writeErrorResponse(HttpServletResponse response, RT<?> errorResult) {
        try {
            if (!response.isCommitted()) {
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                new ObjectMapper().writeValue(response.getOutputStream(), errorResult);
            }
        } catch (Exception ex) {
            log.error("写入导出错误响应失败", ex);
        }
    }

    // ========== 私有方法 ==========

    /**
     * 设置 HTTP 响应头 (Content-Type / Content-Disposition).
     */
    private static void setResponseHeaders(HttpServletResponse response, String fileName) {
        String encodedFileName;
        try {
            encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
        } catch (Exception e) {
            log.error("文件名 URL 编码失败: {}", fileName, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFileName + ".xlsx");
    }

    /**
     * 执行分批写入.
     *
     * <p>每 {@link #BATCH_SIZE} 行一批, 多批时自动分 Sheet.</p>
     */
    private static <T> void doExport(HttpServletResponse response, String fileName,
                                      Class<T> clazz, List<T> data) throws IOException {
        if (data == null || data.isEmpty()) {
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet("Sheet1")
                    .doWrite(List.of());
            log.info("Excel 导出完成(空数据): fileName={}", fileName);
            return;
        }

        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream(), clazz).build();
            int total = data.size();
            int sheetNo = 0;

            for (int i = 0; i < total; i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, total);
                List<T> batch = data.subList(i, end);
                WriteSheet writeSheet;
                if (sheetNo == 0) {
                    writeSheet = EasyExcel.writerSheet("Sheet1").build();
                } else {
                    writeSheet = EasyExcel.writerSheet("Sheet" + (sheetNo + 1)).build();
                }
                excelWriter.write(batch, writeSheet);
                sheetNo++;
            }

            log.info("Excel 导出完成: fileName={}, totalRows={}, sheets={}", fileName, total, sheetNo);
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }
}
