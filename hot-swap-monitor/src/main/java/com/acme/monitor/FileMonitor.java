package com.acme.monitor;

import com.acme.common.AnsiLog;
import com.acme.monitor.command.Cmd;
import com.acme.monitor.config.ApplicationConfigContext;
import com.acme.monitor.file.FileChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/9 14:25
 */
@Component
@Slf4j
public class FileMonitor {

    @Autowired
    private FileChangeListener fileChangeListener;

    @Autowired
    private ApplicationConfigContext applicationConfigContext;

    private FileAlterationMonitor monitor;

    public void start(){
        // 轮询间隔 3 秒
        long interval = TimeUnit.SECONDS.toMillis(3);
        FileAlterationObserver observer = new FileAlterationObserver(new File(applicationConfigContext.monitorPath));
        observer.addListener(fileChangeListener);
        //创建文件变化监听器
        monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        try{
            monitor.start();

            AnsiLog.println("开始监听文件变化--------");
        }
        catch (Exception e){
            log.error("异常处理",e);
        }
    }

    public void stop(){
        try {
            monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
