package com.acme.monitor.file;

import com.acme.common.AnsiLog;
import com.acme.common.Const;
import com.acme.monitor.config.ApplicationConfigContext;
import com.acme.monitor.socket.HotSwapSocketClient;
import com.acme.monitor.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/9 14:28
 */

@Component
@Slf4j
public class FileChangeListener extends FileAlterationListenerAdaptor {

    @Autowired
    private ApplicationConfigContext applicationConfigContext;


    @Autowired
    private Map<String, Worker> workerFactory;



    @Override
    public void onFileChange(File file) {
        if(file.isFile()){
            String absolutePath = file.getAbsolutePath();
            if (!Const.CLASS_FILE_EXTENSION.endsWith(absolutePath)){
                return;
            }
            AnsiLog.info("class file Change "+absolutePath);
            doFileHotSwap(absolutePath);
        }

    }

    @Override
    public void onFileCreate(File file) {

        if(file.isFile()){
            String absolutePath = file.getAbsolutePath();
            if (!Const.CLASS_FILE_EXTENSION.endsWith(absolutePath)){
                return;
            }
            AnsiLog.info("class file create "+absolutePath);
            doFileHotSwap(absolutePath);
        }
    }

    private void  doFileHotSwap(String filePath){
        if(!applicationConfigContext.isAttach){
            AnsiLog.println("未连接到JAVA进程,请使用[attach-pid pid号码]连接到目标进程");
            FileCache.addCache(filePath);
            return;
        }
        if(Const.AUTO_HOT_SWAP.equals(applicationConfigContext)){
            workerFactory.get(applicationConfigContext.mode).sendHotSwapFile(filePath);
        }else{
            Map<Integer, String> cacheMap = FileCache.getCacheMap();
            int key = FileCache.AddAndGetFileNum();
            cacheMap.put(key,filePath);
            AnsiLog.info("手动模式文件号["+key+"] "+filePath);
        }

    }

}
