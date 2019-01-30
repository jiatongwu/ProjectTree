package cn.yueshutong.projecttree.core.entity;

import lombok.Data;

/**
 * Create by yster@foxmail.com 2019/1/28 0028 16:56
 */
@Data
public class JavaFileInfo {
    private String simpleName; //java文件名
    private String reference; //全限定名
    private String filePath; //绝对路径

    public JavaFileInfo(String simpleName, String reference, String filePath) {
        this.simpleName = simpleName;
        this.reference = reference;
        this.filePath = filePath;
    }

}
