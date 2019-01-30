package cn.yueshutong.projecttree.core;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanClassTest {

    @Test
    public void name() {
        String s = "public void ser(int s){44\n" +
                "public void ser(int s){44";
        Pattern pattern = Pattern.compile("ser\\(.*\\)");
        Matcher matcher = pattern.matcher(s);
        System.out.println(matcher.groupCount());
        for (int i = 0; i < matcher.groupCount(); i++) {
            System.out.println(matcher.group(i));
        }
        while (matcher.find()){
            System.out.println(matcher.group());
        }
    }

    public static void main(String[] args) throws IOException {
        String property = "D:\\IDEA\\workSpace\\jobsite\\src\\main\\java";
        System.out.println("===开始遍历===");
        ScanClass scanClass = new ScanClass();
        scanClass.start(new File(property),"");
        System.out.println(scanClass.getJavaFileInfos());
        String s = JSON.toJSONString(scanClass.getRootNode());
        System.out.println(s);
    }
}