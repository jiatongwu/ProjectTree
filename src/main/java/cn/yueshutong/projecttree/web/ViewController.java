package cn.yueshutong.projecttree.web;

import cn.yueshutong.projecttree.core.ScanClass;
import cn.yueshutong.projecttree.vo.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Create by yster@foxmail.com 2019/1/28 0028 17:18
 */
@Controller
public class ViewController {
    @Autowired
    private ScanClass scanClass;

    @RequestMapping(value = "/tree")
    @ResponseBody
    public TreeNode tree(){
        return scanClass.getRootNode();
    }

}
