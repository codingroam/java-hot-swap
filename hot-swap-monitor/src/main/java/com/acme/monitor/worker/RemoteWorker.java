package com.acme.monitor.worker;

import com.acme.common.AnsiLog;
import com.acme.common.utils.ThreadHelper;
import com.acme.monitor.config.ApplicationConfigContext;
import com.acme.monitor.file.FileHelper;
import com.acme.monitor.server.RemoteServerApi;
import com.acme.monitor.socket.HotSwapSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/15 22:01
 */

@Component("server")
//@Conditional(ServerModeConfCondition.class)
//@Lazy
public class RemoteWorker implements Worker{

    @Autowired
    private RemoteServerApi remoteServerApi;

    @Autowired
    private ApplicationConfigContext applicationConfigContext;

    @Autowired
    private HotSwapSocketClient hotSwapSocketClient;

    @Override
    public void jps() {
        List<String> result = remoteServerApi.jpsCmdToRemoteServer();
        AnsiLog.printList(result);
    }

    @Override
    public boolean attachPid(String pid) {
        new Thread(()->{
            remoteServerApi.attachPidToRemoteServer(pid);
        }).start();

        ThreadHelper.sleep(500);
        applicationConfigContext.isAttach = true;


        return true;
    }

    @Override
    public void sendHotSwapFile(String filePath) {
        hotSwapSocketClient.sendSwapFile(FileHelper.getBytesByFilePath(filePath));
    }
}
