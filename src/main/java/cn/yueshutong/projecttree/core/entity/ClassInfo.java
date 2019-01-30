package cn.yueshutong.projecttree.core.entity;

import lombok.Data;

/**
 * Create by yster@foxmail.com 2019/1/29 0029 16:30
 */
@Data
public class ClassInfo {
    private String name; //className
    private String body; //{..}
    private String head; // package...import
    private String packageName;
    private String reference;
    private boolean clazz;

    public ClassInfo() {
    }

    public ClassInfo(String code){
        this.body = code.substring(code.indexOf("{"), code.lastIndexOf("}"));
        this.head = code.substring(0,code.indexOf("{"));
        this.packageName = code.substring(code.indexOf("package")+"package".length(), code.indexOf(";")).trim();
        String trim = this.head.trim();
        this.name = trim.substring(trim.lastIndexOf(' ')).trim();
        this.reference = packageName+"."+name;
        if (this.head.substring(this.head.lastIndexOf(";")).contains("class")){
            this.clazz = true;
        }
    }
}
