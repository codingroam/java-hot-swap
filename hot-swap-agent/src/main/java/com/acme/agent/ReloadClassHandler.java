package com.acme.agent;

import com.acme.common.AgentConfig;
import com.acme.common.AnsiLog;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;

public class ReloadClassHandler{

    private static final String HOT_SWAP_WORKER_CLASS = "com.acme.core.HotSwapCoreWorker";
    private static final String HOT_SWAP_WORKER_METHOD = "workStart";

    private static volatile ClassLoader myClassLoader;

    public void process(String args, Instrumentation inst) {
        if (args == null) {
            throw new IllegalArgumentException("Agent args is null");
        }
        try {
            final String targetPort = args;
            File coreJarFile = AgentConfig.getCoreJarFile();
            if (!coreJarFile.exists()) {
                throw new RuntimeException("croe jar [" + coreJarFile.getAbsolutePath() + "] " +
                        "not found");
            }
            if (myClassLoader == null) {
                myClassLoader = new MyClassloader(
                        new URL[]{coreJarFile.toURI().toURL()});
            }
            AnsiLog.info("agent core urls is {}", coreJarFile.toURI().toURL());
            Class<?> hotReloadWorkerClass = myClassLoader.loadClass(HOT_SWAP_WORKER_CLASS);
            Method method = hotReloadWorkerClass.getDeclaredMethod(HOT_SWAP_WORKER_METHOD, Instrumentation.class, String.class);
            method.invoke(null, inst, targetPort);
        } catch (Exception e) {
            AnsiLog.error("Reload failed: {}", e.getMessage());
            AnsiLog.error(e);
        }
    }
}
