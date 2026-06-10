package com.erp.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用树节点.
 *
 * <p>封装树形结构中的节点数据, 包含节点标识、父节点标识、原始数据和子节点列表.
 * 与 {@link TreeUtil} 配合使用, 支持任意实体类型的树形构建.</p>
 *
 * @param <T> 原始数据类型
 * @author AI
 * @since 2026-05-30
 */
public class TreeNode<T> {

    private Long id;
    private Long parentId;
    private T data;
    private List<TreeNode<T>> children = new ArrayList<>();

    public TreeNode() {
    }

    public TreeNode(Long id, Long parentId, T data) {
        this.id = id;
        this.parentId = parentId;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    public boolean isRoot() {
        return parentId == null || parentId == 0L;
    }
}
