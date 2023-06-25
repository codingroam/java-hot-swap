package com.acme.common.utils;

import com.acme.common.AgentConfig;
import com.acme.common.AnsiLog;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

import com.sun.tools.attach.VirtualMachine;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/13 23:17
 */
public class VMUtils {

    public static void reloadClassForLocalVM(String pid, String targetFilePath) throws
            Exception {
        if (StringUtils.isBlank(pid)) {
            return;
        }
        VirtualMachine attach = null;
        try {
            attach = VirtualMachine.attach(pid);
            String agentJar = AgentConfig.getAgentJar();
            File jarfile = new File(agentJar);
            if (!jarfile.exists()) {
                AnsiLog.error("agent jar [{}] not found, see com.fengjx.reload.common.AgentConfig#getAgentJar", agentJar);
                return;
            }
            attach.loadAgent(AgentConfig.getAgentJar(), targetFilePath);
        } finally {
            if (attach != null) {
                attach.detach();
            }
        }
    }

    public static boolean attachVM(String pid,String targetPort) {
        if (StringUtils.isBlank(pid)) {
            return false;
        }
        VirtualMachine attach = null;
        try {
            attach = VirtualMachine.attach(pid);
            String agentJar = AgentConfig.getAgentJar();
            File jarfile = new File(agentJar);
            if (!jarfile.exists()) {
                AnsiLog.error("agent jar [{}] not found",agentJar);
                return false;
            }
            attach.loadAgent(AgentConfig.getAgentJar(),targetPort);
        } catch(Exception e){
            AnsiLog.error("attach VM failed [{}] ",e.getMessage());
            e.printStackTrace();
            return false;
        }finally {

            if (attach != null) {
                try {
                    attach.detach();
                } catch (IOException e) {
                    AnsiLog.error("aattach.detach failed [{}] ",e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


}
