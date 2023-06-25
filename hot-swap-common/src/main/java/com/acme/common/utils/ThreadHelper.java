package com.acme.common.utils;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/17 17:09
 */
public class ThreadHelper {

    public static void sleep(long l){
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
