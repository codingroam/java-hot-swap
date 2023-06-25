package com.acme.monitor.command;

import com.acme.common.AnsiLog;
import com.acme.common.Const;
import com.acme.monitor.config.ApplicationConfigContext;
import com.acme.monitor.file.FileCache;
import com.acme.monitor.socket.HotSwapSocketClient;
import com.acme.monitor.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/18 13:16
 */
@Component("r")
public class ManualSwapCmd implements Cmd{
    @Autowired
    private ApplicationConfigContext applicationConfigContext;


    @Autowired
    private Map<String, Worker> workerFactory;

    @Override
    public void execute(String[] args) {
        if(!Const.MANUAL_HOT_SWAP.equals(applicationConfigContext.hotSwapStyle)){
            AnsiLog.info("请在手动热加载模式下使用r命令");
        }
        Map<Integer, String> cacheMap = FileCache.getCacheMap();
        for(int i=1; i<args.length; i++){
            if(Integer.valueOf(args[i]) >cacheMap.size()){
                AnsiLog.info("文件号[" + args[i] +"]不存在");
                continue;
            }
            String path = cacheMap.remove(Integer.valueOf(args[i]));
            workerFactory.get(applicationConfigContext.mode).sendHotSwapFile(path);
        }
    }
}
