package com.erp.common.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用分页查询参数.
 *
 * <p>前端分页请求的统一参数格式, 可转换为 MyBatis-Plus {@link Page} 对象.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Schema(description = "通用分页查询参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    @Schema(description = "当前页码(从1开始)", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize = 20;

    /**
     * 转换为 MyBatis-Plus 分页对象.
     *
     * @param <T> 实体类型
     * @return MyBatis-Plus Page 对象
     */
    public <T> Page<T> toPage() {
        return new Page<>(
                pageNum != null ? pageNum.longValue() : 1L,
                pageSize != null ? pageSize.longValue() : 20L
        );
    }
}
