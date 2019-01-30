package cn.yueshutong.projecttree.vo;

import lombok.Data;

import java.util.List;

/**
 * Create by yster@foxmail.com 2019/1/27 0027 22:40
 */
@Data
public class TreeNode {
    private String name;
    private String value;
    private List<TreeNode> children;

    public TreeNode() {
    }

    public TreeNode(String name) {
        this.name = name;
    }

    public TreeNode(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
