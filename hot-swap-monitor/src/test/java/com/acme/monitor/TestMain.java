package com.acme.monitor;

import com.acme.common.utils.ProcessUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/9 15:00
 */


public class TestMain {

    public static void main(String[] args) {
        UserInputMonitor userInputMonitor = new UserInputMonitor();
        userInputMonitor.printInteractiveInfo();
    }


    @Test
    public void testJps(){
        File file = Paths.get("D:\\JavaProject\\webdemo\\target\\classes\\com\\example\\webdemo\\controller\\CommonController.class").toFile();
        file.isFile();
        String[] args = {"jps","-l"};
        List<String> process = ProcessUtils.process(args);
        System.out.println();
    }
    @Test
    public void testSocket() throws IOException {
        String path = "D:\\JavaProject\\webdemo\\target\\classes\\com\\example\\webdemo\\controller\\CommonController.class";
        final SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);//15656
        final InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8989);
        if(!socketChannel.connect(inetSocketAddress)){
            while(!socketChannel.finishConnect()){
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其他工作");
            }
        }
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        int available = fileInputStream.available();
        byte[] bytes = new byte[available];

        fileInputStream.read(bytes);

        String str = "hello world";
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        socketChannel.write(byteBuffer);
        ByteBuffer readBuffer = ByteBuffer.allocate(100);

        int read = socketChannel.read(readBuffer);
        String s = new String(readBuffer.array());
        System.out.println(s);

    }

    @Test
    public void testStartSocket(){
        try{


            //创建serverSockerChannel(BIO中的serverSocket)
            final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            //拿到selector对象
            final Selector selector = Selector.open();

            serverSocketChannel.bind(new InetSocketAddress(Integer.valueOf(8989)));

            //非阻塞

            serverSocketChannel.configureBlocking(false);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(true){
                if(selector.select(1000)==0){
//                    log.info("1秒没有客户端连接");
                    continue;
                }

                final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    final SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable()){
                        final SocketChannel accept = serverSocketChannel.accept();
                        System.out.println("客户端连接成功："+accept);
                        accept.configureBlocking(false);
                        accept.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(10240));
                    }
                    if(selectionKey.isReadable()){
                        //通过key反向得到channel
                        final SocketChannel channel = (SocketChannel)selectionKey.channel();
                        //通过
                        ByteBuffer byteBuffer = (ByteBuffer)selectionKey.attachment();


                        int read = channel.read(byteBuffer);
                        byteBuffer.flip();
                        byte[] bytes = new byte[read];
                        byteBuffer.get(bytes);
                        String path = new String(bytes);
                        System.out.println(path);

                    }
                    //操作完要删除
                    iterator.remove();

                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        HashMap<Integer, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put(1,"hjdsfjdh");
        String remove = objectObjectHashMap.remove(1);
        System.out.println();

    }

    @Test
    public void test3(){

        String[] argsCmd = new String[2];
        argsCmd[0] = "C:\\Users\\21036\\Desktop\\jd-gui.exe";
        argsCmd[1] = "D:\\JavaProject\\webdemo\\target\\classes\\com\\example\\webdemo\\controller\\CommonController.class";

        List<String> process = ProcessUtils.process(argsCmd);
        System.out.println();

    }


}
