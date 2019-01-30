package cn.yueshutong.projecttree.core.util;

import java.util.Stack;

/**
 * Create by yster@foxmail.com 2019/1/29 0029 17:04
 */
public class StringUtil {
    /**
     * 获取当前{}或者()结束索引
     *
     * @param code
     * @param prefix
     * @param suffix
     * @return
     */
    public static int getMethodEnd(String code, int start, String prefix, String suffix) {
        Stack<Integer> stack = new Stack<>();
        int index = code.indexOf(prefix, start) + 1;
        stack.push(1);
        while (!stack.empty()) {
            int i1 = code.indexOf(suffix, index);
            int i2 = code.indexOf(prefix, index);
            if (i1 < i2 || i2 < 0) { //考虑不存在情况
                stack.pop();
                index = i1 + 1;
                continue;
            }
            if (i2 < i1 || i1 < 0) {
                stack.push(1);
                index = i2 + 1;
                continue;
            }
        }
        return index;
    }

    /**
     * 查找字符出现次数
     * @param s
     * @param c
     * @return
     */
    public static int count(String s, char c){
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (c == s.charAt(i)) {
                n++;
            }
        }
        return n;
    }

}
