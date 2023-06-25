package com.acme.agent;

import com.acme.common.AnsiLog;

import java.lang.instrument.Instrumentation;


public class HotSwapAgentApplication {


    /**
     * 启动时加载
     */
    public static void premain(String args, Instrumentation inst) {
        AnsiLog.info("premain agent args: {}", args);
    }

    /**
     * 运行时加载（attach）
     */
    public static void agentmain(String args, Instrumentation inst) {
        AnsiLog.info("agentmain agent args：{}",  args);
        ReloadClassHandler handler = new ReloadClassHandler();
        handler.process(args, inst);
    }


}
