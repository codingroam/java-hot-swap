package com.acme.monitor;

import com.acme.common.AnsiLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/13 22:07
 */
@Component
public class MonitorStart implements CommandLineRunner {

    @Autowired
    private FileMonitor fileMonitor;

    @Autowired
    private UserInputMonitor userInputMonitor;

    @Override
    public void run(String... args) throws Exception {
        fileMonitor.start();
        userInputMonitor.start();

    }
    @PreDestroy
    public void destory(){
        AnsiLog.info("正在退出应用...");
        userInputMonitor.stop();
        fileMonitor.stop();
    }
}
