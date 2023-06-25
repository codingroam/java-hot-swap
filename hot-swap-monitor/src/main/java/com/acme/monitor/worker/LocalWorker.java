package com.acme.monitor.worker;

import com.acme.common.AnsiLog;
import com.acme.common.utils.ProcessUtils;
import com.acme.common.utils.ThreadHelper;
import com.acme.common.utils.VMUtils;
import com.acme.monitor.config.ApplicationConfigContext;
import com.acme.monitor.socket.HotSwapSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/15 22:01
 */
@Component("local")
//@ConditionalOnProperty(name = "mode", havingValue = "local")
public class LocalWorker implements Worker {

    @Autowired
    private ApplicationConfigContext applicationConfigContext;

    @Autowired
    private HotSwapSocketClient hotSwapSocketClient;

    @Override
    public void jps() {
        List<String> result = ProcessUtils.jpsCmd();
        AnsiLog.printList(result);
    }

    @Override
    public boolean attachPid(String pid) {
        applicationConfigContext.pid = pid;
        AnsiLog.println("完成设置 pid=[" + pid +"]");
        AnsiLog.println("**开始attach JVM**");
        new Thread(()->{
            VMUtils.attachVM(applicationConfigContext.pid, applicationConfigContext.targetPort+"_"+applicationConfigContext.mode);
        }).start();

        ThreadHelper.sleep(500);
        applicationConfigContext.isAttach = true;
        return true;


    }

    @Override
    public void sendHotSwapFile(String filePath) {
        hotSwapSocketClient.sendSwapFile(filePath.getBytes());
    }
}
