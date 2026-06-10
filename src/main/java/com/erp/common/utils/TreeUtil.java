package com.erp.common.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 树形结构工具类.
 *
 * <p>提供通用的树形结构操作方法: 构建树、展平树、权限过滤等.
 * 通过函数式接口获取实体的 ID 和父 ID, 适配任意实体类型.</p>
 *
 * <p>使用示例:
 * <pre>{@code
 * List<Dept> deptList = deptService.list();
 * List<TreeNode<Dept>> tree = TreeUtil.buildTree(
 *     deptList,
 *     Dept::getId,
 *     Dept::getParentId,
 *     d -> new TreeNode<>(d.getId(), d.getParentId(), d)
 * );
 * List<TreeNode<Dept>> flat = TreeUtil.flatten(tree);
 * }</pre>
 * </p>
 *
 * @author AI
 * @since 2026-05-30
 */
public final class TreeUtil {

    private TreeUtil() {
        throw new UnsupportedOperationException("Utility class, do not instantiate");
    }

    private static final long DEFAULT_ROOT_PARENT_ID = 0L;

    /**
     * 构建树形结构(默认根节点 parentId=0).
     *
     * @param list        原始数据列表
     * @param idGetter    获取节点 ID 的函数
     * @param parentGetter 获取父节点 ID 的函数
     * @param converter   将原始数据转换为 TreeNode 的函数
     * @param <T>         原始数据类型
     * @return 树形结构根节点列表
     */
    public static <T> List<TreeNode<T>> buildTree(
            List<T> list,
            Function<T, Long> idGetter,
            Function<T, Long> parentGetter,
            Function<T, TreeNode<T>> converter) {
        return buildTree(list, idGetter, parentGetter, converter, DEFAULT_ROOT_PARENT_ID);
    }

    /**
     * 构建树形结构(指定根节点 parentId).
     *
     * @param list          原始数据列表
     * @param idGetter      获取节点 ID 的函数
     * @param parentGetter  获取父节点 ID 的函数
     * @param converter     将原始数据转换为 TreeNode 的函数
     * @param rootParentId  根节点的父 ID 值
     * @param <T>           原始数据类型
     * @return 树形结构根节点列表
     */
    public static <T> List<TreeNode<T>> buildTree(
            List<T> list,
            Function<T, Long> idGetter,
            Function<T, Long> parentGetter,
            Function<T, TreeNode<T>> converter,
            long rootParentId) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }

        // 按 parentId 分组，一次遍历 O(n)
        Map<Long, List<TreeNode<T>>> parentToChildren = new LinkedHashMap<>();
        for (T item : list) {
            TreeNode<T> node = converter.apply(item);
            parentToChildren.computeIfAbsent(node.getParentId(), k -> new ArrayList<>()).add(node);
        }

        List<TreeNode<T>> roots = parentToChildren.getOrDefault(rootParentId, List.of());
        for (TreeNode<T> root : roots) {
            buildChildren(root, parentToChildren);
        }

        return roots;
    }

    /**
     * 递归构建子节点.
     *
     * @param parentNode         父节点
     * @param parentToChildren   按 parentId 分组的节点映射
     * @param <T>                原始数据类型
     */
    private static <T> void buildChildren(
            TreeNode<T> parentNode,
            Map<Long, List<TreeNode<T>>> parentToChildren) {
        List<TreeNode<T>> children = parentToChildren.get(parentNode.getId());
        if (children != null && !children.isEmpty()) {
            parentNode.setChildren(children);
            for (TreeNode<T> child : children) {
                buildChildren(child, parentToChildren);
            }
        }
    }

    /**
     * 展平树形结构为列表(深度优先).
     *
     * @param nodes 树形结构根节点列表
     * @param <T>   原始数据类型
     * @return 展平后的节点列表
     */
    public static <T> List<TreeNode<T>> flatten(List<TreeNode<T>> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return List.of();
        }
        List<TreeNode<T>> result = new ArrayList<>();
        flattenDfs(nodes, result);
        return result;
    }

    /**
     * 深度优先展平递归.
     */
    private static <T> void flattenDfs(List<TreeNode<T>> nodes, List<TreeNode<T>> result) {
        for (TreeNode<T> node : nodes) {
            result.add(node);
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                flattenDfs(node.getChildren(), result);
            }
        }
    }

    /**
     * 按权限过滤树节点.
     *
     * <p>使用 Predicate 对树节点进行过滤.
     * 如果父节点被保留但所有子节点被过滤, 父节点仍保留(作为叶子节点).
     * 如果父节点被过滤但子节点中有符合条件的, 子节点会上移.</p>
     *
     * @param nodes     树形结构根节点列表
     * @param predicate 过滤条件
     * @param <T>       原始数据类型
     * @return 过滤后的树形结构
     */
    public static <T> List<TreeNode<T>> filterByPermission(
            List<TreeNode<T>> nodes,
            Predicate<TreeNode<T>> predicate) {
        if (nodes == null || nodes.isEmpty()) {
            return List.of();
        }
        List<TreeNode<T>> result = new ArrayList<>();
        for (TreeNode<T> node : nodes) {
            List<TreeNode<T>> filteredChildren = filterByPermission(
                    node.getChildren() != null ? node.getChildren() : List.of(),
                    predicate);

            if (predicate.test(node)) {
                node.setChildren(filteredChildren);
                result.add(node);
            } else if (!filteredChildren.isEmpty()) {
                result.addAll(filteredChildren);
            }
        }
        return result;
    }
}
