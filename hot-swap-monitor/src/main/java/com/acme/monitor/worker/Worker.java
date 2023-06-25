package com.acme.monitor.worker;


public interface Worker {


    void jps();

    boolean attachPid(String pid);

    void sendHotSwapFile(String filePath);


}
