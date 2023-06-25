package com.acme.monitor.socket;

import com.acme.common.AnsiLog;
import com.acme.common.Const;
import com.acme.common.utils.ThreadHelper;
import com.acme.monitor.config.ApplicationConfigContext;
import com.acme.monitor.file.FileCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/17 16:37
 */
@Component
public class HotSwapSocketClient {

    @Autowired
    private ApplicationConfigContext applicationConfigContext;

    private SocketChannel socketChannel = null;

    public void connectServer(){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);//15656
            String hostName = null;
            if(Const.LOCAL_HOT_SWAP.equals(applicationConfigContext.mode)){
                hostName = "127.0.0.1";
            }else{
                String rex = "\\d+\\.\\d+\\.\\d+\\.\\d*";
                Pattern pattern = Pattern.compile(rex);
                Matcher matcher = pattern.matcher(applicationConfigContext.serverUrl);
                while (matcher.find()) {
                    hostName = matcher.group();
                    break;
                }

            }

            final InetSocketAddress inetSocketAddress = new InetSocketAddress(hostName, Integer.valueOf(applicationConfigContext.targetPort));
            if(!socketChannel.connect(inetSocketAddress)){
                while(!socketChannel.finishConnect()){
                    ThreadHelper.sleep(200);
                }
            }
            AnsiLog.println("连接目标主机socket成功");
            snedCacheFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void snedCacheFile() {
        if(Const.AUTO_HOT_SWAP.equals(applicationConfigContext.hotSwapStyle)){
            Set<String> cacheFileSet = FileCache.getCacheSet();
            for(String cachePath : cacheFileSet){
                sendSwapFile(cachePath.getBytes());
            }
        }else{
            Map<Integer, String> cacheMap = FileCache.getCacheMap();
            if(cacheMap.size()>0){
                AnsiLog.info("**手动模式** 记录改变的文件如下，请输入[r 文件号1 文件号2 .。]对一至多个文件热替换，如r 2 6 11");
                cacheMap.keySet().forEach(u->{
                    AnsiLog.info("["+u+"] "+cacheMap.get(u));
                });
            }

        }

    }

    public boolean sendSwapFile(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        String filePath = new String(bytes);
        try {
            socketChannel.write(byteBuffer);
            ByteBuffer msgByteBuffer = ByteBuffer.allocate(4096);
            int read = socketChannel.read(msgByteBuffer);
            msgByteBuffer.flip();
            byte[] msgBytes = new byte[read];
            msgByteBuffer.get(msgBytes);
            String msg = new String(msgBytes);
            if("success".startsWith(msg)){
                AnsiLog.info("hot swap success! " + filePath);
            }else {
                AnsiLog.info("hot swap success failed! "+filePath);
                AnsiLog.info("failed cause "+msg);
            }



        } catch (IOException e) {
            e.printStackTrace();
            AnsiLog.println(e.getMessage());
            return false;
        }

        return true;
    }

}
