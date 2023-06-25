package com.acme.core;


import com.acme.common.Const;
import com.acme.common.utils.IOUtils;
import com.acme.core.dynamiccompiler.DynamicCompiler;
import com.acme.core.utils.ClassByteCodeUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
@Slf4j
public class HotSwapCoreWorker {

    private static final Map<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();
    private static String mode = null;

    public static String doReload(Instrumentation instrumentation, String replaceTargetFile) {
        log.info("class reload start, current classloader is " + HotSwapCoreWorker.class.getClassLoader());
        try {
            if (replaceTargetFile == null) {
                log.error("invalid argument file is null");
                return "failed:invalid argument file is null";
            }
            File file = Paths.get(replaceTargetFile).toFile();
            if (replaceTargetFile.endsWith(Const.CLASS_FILE_EXTENSION)) {
                log.info("reload by class file");
                byte[] newClazzByteCode = Files.readAllBytes(file.toPath());
                String className = ClassByteCodeUtils.getClassNameFromByteCode(newClazzByteCode);
                doReloadClassFile(instrumentation, className, newClazzByteCode);
            } else if (replaceTargetFile.endsWith(Const.JAVA_FILE_EXTENSION)) {
                log.info("reload by java file");
                byte[] newClazzSourceBytes = Files.readAllBytes(file.toPath());
                String className = ClassByteCodeUtils.getClassNameFromSourceCode(IOUtils.toString(new FileInputStream(file)));
                doCompileThenReloadClassFile(instrumentation, className, new String(newClazzSourceBytes, UTF_8));
            }
        } catch (Exception e) {
            log.error("class reload failed: {}", replaceTargetFile, e);
            return "failed:" + e.getMessage();
        }
        return "success";
    }

    public static String doReload(Instrumentation instrumentation, byte[] newClazzByteCode) {
        try{
            String className = ClassByteCodeUtils.getClassNameFromByteCode(newClazzByteCode);
            doReloadClassFile(instrumentation, className, newClazzByteCode);
        } catch (Exception e) {
            log.error("class reload failed newClazzByteCode: {} ", e);
            e.printStackTrace();
            return "failed:" + e.getMessage();
        }
        return "success";


    }


    private static void doReloadClassFile(Instrumentation instrumentation, String className,
                                          byte[] newClazzByteCode) throws UnmodifiableClassException, ClassNotFoundException {
        Class<?> clazz = getToReloadClass(instrumentation, className, newClazzByteCode);
        if (clazz == null) {
            log.error("Class " + className + " not found");
        } else {
            instrumentation.redefineClasses(new ClassDefinition(clazz, newClazzByteCode));
            log.info("Class: " + clazz + " reload success!");
        }
    }

    private static void doCompileThenReloadClassFile(Instrumentation instrumentation, String className,
                                                     String sourceCode) {
        ClassLoader classLoader = getClassLoader(className, instrumentation);
        log.info("Target class {} class loader {}", className, classLoader);
        DynamicCompiler dynamicCompiler = new DynamicCompiler(classLoader);
        dynamicCompiler.addSource(className, sourceCode);
        Map<String, byte[]> classNameToByteCodeMap = dynamicCompiler.buildByteCodes();
        classNameToByteCodeMap.forEach((clazzName, bytes) -> {
            try {
                doReloadClassFile(instrumentation, clazzName, bytes);
            } catch (Exception e) {
                log.error("Class " + clazzName + " reload error ", e);
            }
        });
    }

    private static ClassLoader getClassLoader(String className, Instrumentation instrumentation) {
        Class<?> targetClass = findTargetClass(className, instrumentation);
        if (targetClass != null) {
            return targetClass.getClassLoader();
        }
        return HotSwapCoreWorker.class.getClassLoader();
    }

    private static Class<?> getToReloadClass(Instrumentation instrumentation, String className,
                                             byte[] newClazzByteCode) {
        Class<?> clazz = findTargetClass(className, instrumentation);
        if (clazz == null) {
            clazz = defineNewClass(className, newClazzByteCode, clazz);
        }
        return clazz;
    }

    private static Class<?> defineNewClass(String className, byte[] newClazzByteCode, Class<?> clazz) {
        log.info("Class " + className + " not found, try to define a new class");
        ClassLoader classLoader = HotSwapCoreWorker.class.getClassLoader();
        try {
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class,
                    byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            clazz = (Class<?>) defineClass.invoke(classLoader, className, newClazzByteCode
                    , 0, newClazzByteCode.length);
            log.info("Class " + className + " define success " + clazz);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("defineNewClass {} failed ", className, e);
        }
        return clazz;
    }

    @SuppressWarnings("rawtypes")
    protected static Class<?> findTargetClass(String className, Instrumentation instrumentation) {
        return CLASS_CACHE.computeIfAbsent(className, clazzName -> {
            Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
            return Arrays.stream(allLoadedClasses)
                    .parallel()
                    .filter(clazz -> clazzName.equals(clazz.getName()))
                    .findFirst()
                    .orElse(null);
        });
    }

    public static void workStart(Instrumentation instrumentation, String targetPortAndMode){
        try{

            String[] targetPortAndModes = targetPortAndMode.split("_");
            String targetPort = targetPortAndModes[0];
            mode = targetPortAndModes[1];

            log.info("start worker socket");
            //创建serverSockerChannel(BIO中的serverSocket)
            final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            //拿到selector对象
            final Selector selector = Selector.open();

            serverSocketChannel.bind(new InetSocketAddress(Integer.valueOf(targetPort)));

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
                        log.info("客户端连接成功："+accept);
                        accept.configureBlocking(false);
                        accept.register(selector,SelectionKey.OP_READ, ByteBuffer.allocateDirect(1024*100));
                    }
                    if(selectionKey.isReadable()){
                        //通过key反向得到channel
                        SocketChannel channel = null;
                        try{
                            channel = (SocketChannel)selectionKey.channel();
                            //通过
                            ByteBuffer byteBuffer = (ByteBuffer)selectionKey.attachment();
                            int read = channel.read(byteBuffer);
                            byteBuffer.flip();
                            byte[] bytes = new byte[read];
                            byteBuffer.get(bytes,0,read);
                            byteBuffer.clear();
                            String result = null;
                            if("local".equals(mode)){
                                String path = new String(bytes).trim();
                                if(path != null && !"".equals(path)){
                                    log.info("reload class form path:{}",path);
                                    result = doReload(instrumentation,path);
                                }
                            }else{
                                log.info("reload class form bytes");
                                result = doReload(instrumentation,bytes);

                            }

                            ByteBuffer byteBufferMsg = ByteBuffer.wrap(result.getBytes());
                            channel.write(byteBufferMsg);

                        }catch (Exception e){
                            //取消注册
                            selectionKey.cancel();
                            //关闭通道
                            channel.close();
                        }



                    }
                    //操作完要删除
                    iterator.remove();

                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
