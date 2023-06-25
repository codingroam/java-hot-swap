package com.acme.monitor.command;

import com.acme.monitor.config.ApplicationConfigContext;
import com.acme.monitor.socket.HotSwapSocketClient;
import com.acme.monitor.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/14 22:05
 */

@Component("attach-pid")
public class AttachCmd implements Cmd{

    @Autowired
    private Map<String,Worker> workerFactory;
    @Autowired
    private ApplicationConfigContext applicationConfigContext;
    @Autowired
    private HotSwapSocketClient hotSwapSocketClient;

    @Override
    public void execute(String[] args) {
        boolean flag = workerFactory.get(applicationConfigContext.mode).attachPid(args[1]);
        if(flag){
            hotSwapSocketClient.connectServer();
        }

    }
}
