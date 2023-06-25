package com.acme.common;

import org.springframework.boot.system.ApplicationHome;

import java.io.File;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/14 22:07
 */
public class AgentConfig {


    private static String jarFilePath;

    static{
        ApplicationHome applicationHome = new ApplicationHome(AgentConfig.class);
        jarFilePath = applicationHome.getSource().getParentFile().toString();
        AnsiLog.info("jarFilePath:{}",jarFilePath);
    }

    public static String getAgentJar(){
        return jarFilePath+ File.separator + Const.AGENT_JAR;
    }

    public static File getCoreJarFile(){
        String coreJarPath = jarFilePath+ File.separator + Const.CORE_JAR;
        return new File(coreJarPath);
    }
}
