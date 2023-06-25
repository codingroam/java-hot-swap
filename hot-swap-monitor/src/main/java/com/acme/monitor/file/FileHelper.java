package com.acme.monitor.file;

import com.acme.common.AnsiLog;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/17 21:47
 */
public class FileHelper {

    public static byte[] getBytesByFilePath(String filePath){

        byte[] bytes = null;
        try{

            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);

            int available = fileInputStream.available();
            bytes = new byte[available];

            fileInputStream.read(bytes);

        }catch (Exception e){
            AnsiLog.info("getBytesByFilePath failure! filePath={}",filePath);
            e.printStackTrace();
            return bytes;
        }
        return bytes;

    }
}
