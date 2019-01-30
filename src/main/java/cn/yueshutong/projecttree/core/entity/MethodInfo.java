package cn.yueshutong.projecttree.core.entity;

import lombok.Data;

import static cn.yueshutong.projecttree.core.util.StringUtil.getMethodEnd;

/**
 * Create by yster@foxmail.com 2019/1/29 0029 16:29
 */
@Data
public class MethodInfo {
    private String name; // method
    private String body; //{...}
    private String parm; //(...)

    public MethodInfo(){
    }

    public MethodInfo(String code){
        if (code==null||"".equals(code)){
            return;
        }
        this.body = code.substring(code.indexOf("{"), getMethodEnd(code, 0, "{", "}"));
        int endIndex = code.indexOf("(");
        this.parm = code.substring(endIndex,getMethodEnd(code,0,"(",")"));
        String p = code.substring(0, endIndex);
        int of = p.lastIndexOf(' ');
        this.name = code.substring(of<0?0:of, endIndex).trim();
    }

}
