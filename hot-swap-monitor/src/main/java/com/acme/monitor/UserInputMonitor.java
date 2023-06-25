package com.acme.monitor;

import com.acme.common.AnsiLog;
import com.acme.monitor.command.Cmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Scanner;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/9 14:26
 */
@Component
@Slf4j
public class UserInputMonitor {

    @Autowired
    public Map<String, Cmd> cmdFactory;


    private volatile boolean isRunning;

    private Thread thread;



    public void start(){
        if(isRunning){
            throw new IllegalStateException("User Monitor is already running");
        }
        isRunning = true;
        printInteractiveInfo();
    }

    public void stop(){
        isRunning = false;

    }

    public void printInteractiveInfo(){
        AnsiLog.println("请输入命令:[输入h查看命令帮助]");
        thread = new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while(isRunning){
                try{
                    String readLine = scanner.nextLine();
                    String[] cmdArgs = readLine.split(" ");
                    Cmd cmd = cmdFactory.get(cmdArgs[0]);
                    if(cmd == null){
                        AnsiLog.info("请输入正确命令，输入h查看帮助");
                        break;
                    }
                    cmd.execute(cmdArgs);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }

        },"userinputmonitor");
        thread.start();



    }


}
