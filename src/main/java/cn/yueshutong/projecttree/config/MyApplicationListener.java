package cn.yueshutong.projecttree.config;

import cn.yueshutong.projecttree.core.ScanClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Create by yster@foxmail.com 2019/1/30 0030 18:23
 */
@Configuration
public class MyApplicationListener implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ScanClass scanClass;
    @Value("${pt:}")
    private String pt;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (pt ==null|| pt.isEmpty()) {
            logger.error("请使用--pt参数追加的方式声明Package根目录：（例如 D:\\src\\main\\java）");
        }else {
            logger.info("开始分析...");
            scanClass.start(new File(pt), "");
            logger.info("分析完毕！");
        }
    }
}
