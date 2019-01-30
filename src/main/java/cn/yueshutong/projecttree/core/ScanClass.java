package cn.yueshutong.projecttree.core;

import cn.yueshutong.projecttree.core.entity.ClassInfo;
import cn.yueshutong.projecttree.core.entity.JavaFileInfo;
import cn.yueshutong.projecttree.core.entity.MethodInfo;
import cn.yueshutong.projecttree.core.util.StringUtil;
import cn.yueshutong.projecttree.vo.TreeNode;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.yueshutong.projecttree.core.util.StringUtil.getMethodEnd;

/**
 * Create by yster@foxmail.com 2019/1/28 0028 15:47
 */
@Component
public class ScanClass {
    private List<JavaFileInfo> javaFileInfos = new ArrayList<>();
    private TreeNode rootNode = new TreeNode("JavaApplication");


    /**
     * 开始
     *
     * @param file
     * @param prefix
     * @throws IOException
     */
    public void start(File file, String prefix) throws IOException {
        scanClassFile(file, prefix);
        scanController();
    }

    /**
     * 递归遍历Java文件
     *
     * @param file
     * @param prefix
     */
    private void scanClassFile(File file, String prefix) {
        rootNode.setValue(file.getPath());
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File childFile = files[i];
            if (childFile.isDirectory()) {
                scanClassFile(childFile, prefix + childFile.getName() + ".");
            }
            if (childFile.isFile() && childFile.getName().endsWith(".java")) {
                String simpleName = childFile.getName().replace(".java", "");
                javaFileInfos.add(new JavaFileInfo(simpleName, prefix + simpleName, childFile.getAbsolutePath()));
            }
        }
    }

    /**
     * 扫描所有Controller方法
     *
     * @throws IOException
     */
    private void scanController() throws IOException {
        Pattern controllerPattern = Pattern.compile("@[a-zA-Z]+Mapping\\(.*\\)");
        List<TreeNode> controllerNodes = new ArrayList<>();
        rootNode.setChildren(controllerNodes);
        for (JavaFileInfo info : javaFileInfos) {
            String code = FileUtils.readFileToString(new File(info.getFilePath()), "UTF-8");
            ClassInfo classInfo = new ClassInfo(code);
            String body = classInfo.getBody();
            Matcher matcher = controllerPattern.matcher(body);
            //对Controller方法进行分析
            while (matcher.find()) {
                //Controller方法信息
                String mapping = matcher.group(); //@mapping()注解
                String url = getMappingPath(mapping);
                int start = matcher.end();

                start = body.indexOf("public",start);
                int e = body.indexOf("{",start);

                String line = body.substring(start,e).trim() + "{}";
                String value = mapping + "\n" + line;
                //新建Controller节点
                TreeNode controllerNode = new TreeNode(url, value);
                controllerNodes.add(controllerNode);
                //新建service节点集合
                List<TreeNode> serviceNodes = new ArrayList<>();
                controllerNode.setChildren(serviceNodes);
                //controller方法代码块
                MethodInfo method = new MethodInfo(body.substring(start));
                getChildNode(classInfo, serviceNodes, method);
            }
        }
    }

    /**
     * 扫描所有的service方法
     *
     * @param classInfo
     * @param serviceNodes
     * @param method
     * @throws IOException
     */
    private void getChildNode(ClassInfo classInfo, List<TreeNode> serviceNodes, MethodInfo method) throws IOException {
        if (method.getBody()==null){
            return;
        }
        //类方法体代码
        String body = classInfo.getBody();
        for (JavaFileInfo fileInfo : javaFileInfos) {
            //判断代码是否引用其他类
            if (body.contains(" "+fileInfo.getSimpleName()+" ")) {
                Pattern pattern = Pattern.compile(fileInfo.getSimpleName() + " +[a-zA-Z1-9]+ *;");
                Matcher matcher = pattern.matcher(body);
                while (matcher.find()) {
                    String group = matcher.group().replace(fileInfo.getSimpleName(), "").trim();
                    String sub = group.substring(0, group.length() - 1);
                    String methodBody = method.getBody();
                    //方法内部使用了该变量
                    if (methodBody.contains(sub)) {
                        int beginIndex = methodBody.indexOf(sub) + sub.length();
                        int end = getMethodEnd(methodBody, beginIndex, "(", ")");
                        if (end <= beginIndex) {
                            continue;
                        }
                        String substring = methodBody.substring(beginIndex, end);
                        TreeNode serviceNode = new TreeNode();
                        serviceNode.setValue(fileInfo.getSimpleName() + substring);
                        serviceNode.setName(getServiceName(fileInfo, substring).toString());
                        serviceNodes.add(serviceNode);
                        List<TreeNode> serviceChildNodes = new ArrayList<>();
                        serviceNode.setChildren(serviceChildNodes);
                        String javaCode = FileUtils.readFileToString(new File(fileInfo.getFilePath()), "UTF-8");
                        String methodName = substring.substring(substring.indexOf(".")+1);
                        String methodCode = getMethodCode(fileInfo, javaCode, methodName, classInfo, method);
                        getChildNode(new ClassInfo(javaCode), serviceChildNodes, new MethodInfo(methodCode));
                    }
                }
            }
        }
    }

    /**
     * 获取方法代码
     * 若是遇到方法重载且形参数量相等的就会出错
     *
     * @param fileInfo
     * @param javaCode
     * @param methodName .ser("")
     * @param classInfo
     * @param methodInfo
     * @return
     */
    private String getMethodCode(JavaFileInfo fileInfo, String javaCode, String methodName, ClassInfo classInfo, MethodInfo methodInfo) {
        String regex = methodName.substring(0, methodName.indexOf("(")) + "\\(.*"+"\\)";
        int dog = StringUtil.count(methodName,',');
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(javaCode);
        String method = null;

        Map<Integer,String> hashMap = new HashMap<>();
        matcher.reset();
        while (matcher.find()){
            int count = StringUtil.count(matcher.group(), ',');
            if (count!=dog){
                continue;
            }
            hashMap.put(matcher.start(),matcher.group());
        }

        if (hashMap.size()==1) {
            int start = hashMap.keySet().stream().findFirst().get();
            int end = getMethodEnd(javaCode, start, "{", "}");
            String w = "";
            if (start > end) {
                end = javaCode.indexOf(";", start);
                w = "{}";
            }
            method = javaCode.substring(start, end);
            method += w;
        } else if (hashMap.size()>1){
            System.out.println(classInfo.getReference() + "的" + methodInfo.getName() + methodInfo.getParm() + "方法在调用");
            System.out.println(fileInfo.getReference() + "类的方法时检测到方法重载，请查看后输入正确的方法序号：");
            Map<Integer,String> map = new HashMap<>();
            for (Map.Entry<Integer,String> s: hashMap.entrySet()) {
                System.out.println(map.size() + " -- " + s.getValue());
                int start = s.getKey();
                int end = getMethodEnd(javaCode, start, "{", "}");
                String w = "";
                if (start > end) {
                    end = javaCode.indexOf(";", start);
                    w = "{}";
                }
                method = javaCode.substring(start, end);
                method += w;
                map.put(map.size(),method);
            }
            Scanner scanner = new Scanner(System.in);
            int s = scanner.nextInt();
            method = hashMap.get(s);
        }
        return method;
    }

    /**
     * Servic方法Name
     *
     * @param fileInfo
     * @param substring
     * @return
     */
    private StringBuilder getServiceName(JavaFileInfo fileInfo, String substring) {
        StringBuilder builder = new StringBuilder();
        builder.append(fileInfo.getSimpleName());
        builder.append(substring.substring(0, substring.indexOf("(")));
        builder.append("(");
        if (substring.contains(",")) {
            for (int i = 0; i < substring.length(); i++) {
                if (',' == substring.charAt(i)) {
                    builder.append(",");
                }
            }
        }
        builder.append(")");
        return builder;
    }


    /**
     * 提取Mapping注解中的url路径
     *
     * @param group
     * @return
     */
    private String getMappingPath(String group) {
        if (group.contains("value")) {
            int i1 = group.indexOf("\"", group.indexOf("value"));
            int i2 = group.indexOf("\"", i1 += 1);
            if (i1 < 0 || i2 < 0) {
                i1 = group.indexOf("value=") + "value=".length();
                if (group.contains(",")) {
                    i2 = group.indexOf(",", i1);
                } else {
                    i2 = group.indexOf(")", i1);
                }
            }
            return group.substring(i1, i2);
        }
        if (group.contains("path")) {
            int i1 = group.indexOf("\"", group.indexOf("path"));
            int i2 = group.indexOf("\"", i1 += 1);
            return group.substring(i1, i2);
        }
        if (!group.contains("=")) {
            int i1 = group.indexOf("\"");
            int i2 = group.indexOf("\"", i1 += 1);
            return group.substring(i1, i2);
        }
        return null;
    }

    public List<JavaFileInfo> getJavaFileInfos() {
        return javaFileInfos;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }
}
