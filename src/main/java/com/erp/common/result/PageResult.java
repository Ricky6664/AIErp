package com.erp.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应包装类(PageResult).
 *
 * <p>全系统所有分页查询接口的统一返回结构. 与 {@link RT} 组合使用:
 * {@code RT<PageResult<T>>}. 支持从 MyBatis-Plus {@link IPage} 对象直接转换.</p>
 *
 * <p>字段说明:
 * <ul>
 *   <li>{@code list} -- 当前页数据列表</li>
 *   <li>{@code total} -- 总记录数</li>
 *   <li>{@code pageNum} -- 当前页码(从1开始)</li>
 *   <li>{@code pageSize} -- 每页大小</li>
 *   <li>{@code pages} -- 总页数(自动计算)</li>
 * </ul>
 * </p>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 从 MyBatis-Plus IPage 转换
 * IPage<User> page = userService.page(new Page<>(1, 20));
 * return RT.ok(PageResult.of(page));
 *
 * // 返回空分页
 * return RT.ok(PageResult.empty());
 *
 * // 手动构造
 * return RT.ok(PageResult.of(dataList, 100L, 1, 20));
 * }</pre>
 * </p>
 *
 * @param <T> 列表元素类型
 * @author AI
 * @since 2026-05-29
 */
@Schema(description = "分页响应包装对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 当前页数据列表 */
    @Schema(description = "当前页数据列表")
    private List<T> list;

    /** 总记录数 */
    @Schema(description = "总记录数", example = "1500")
    private Long total;

    /** 当前页码(从1开始) */
    @Schema(description = "当前页码(从1开始)", example = "1")
    private Integer pageNum;

    /** 每页大小 */
    @Schema(description = "每页大小", example = "20")
    private Integer pageSize;

    /** 总页数 */
    @Schema(description = "总页数", example = "75")
    private Integer pages;

    // ========== 静态工厂方法 ==========

    /**
     * 从 MyBatis-Plus {@link IPage} 对象构建分页响应.
     *
     * <p>自动提取列表数据、总记录数、页码、页大小并计算总页数.</p>
     *
     * @param page MyBatis-Plus 分页查询结果
     * @param <T>  列表元素类型
     * @return 分页响应对象
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setList(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageNum((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setPages((int) page.getPages());
        return result;
    }

    /**
     * 手动构建分页响应.
     *
     * @param list     当前页数据列表
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param <T>      列表元素类型
     * @return 分页响应对象
     */
    public static <T> PageResult<T> of(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        long ps = pageSize != null ? pageSize.longValue() : 0L;
        long t = total != null ? total.longValue() : 0L;
        result.setPages(ps > 0 ? (int) ((t + ps - 1) / ps) : 0);
        return result;
    }

    /**
     * 返回空分页结果.
     *
     * <p>列表为空集合, 总记录数和总页数均为 0, 页码默认为 1, 页大小默认为 0.</p>
     *
     * @param <T> 列表元素类型
     * @return 空分页响应对象
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0L, 1, 0, 0);
    }
}
