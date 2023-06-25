package com.acme.monitor.file;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/17 17:34
 */
public class FileCache {
    private static AtomicInteger fileNum = new AtomicInteger(0);
    private static Set<String> fileCacheSet = new HashSet<>();
    private static Map<Integer,String> fileCacheMap = new ConcurrentHashMap<>();

    public static void addCache(String filePath){
        fileCacheSet.add(filePath);
        fileCacheMap.put(AddAndGetFileNum(),filePath);
    }

    public static Set<String> getCacheSet(){
        return fileCacheSet;
    }

    public static Map<Integer,String> getCacheMap(){

        return fileCacheMap;
    }

    public static Integer AddAndGetFileNum(){
        return fileNum.addAndGet(1);
    }


}
