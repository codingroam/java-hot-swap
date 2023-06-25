package com.acme.monitor.command;

import com.acme.common.AnsiLog;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/16 10:58
 */
@Component("h")
public class HelpCmd implements Cmd {

    private List<String> helpList = new ArrayList<>();

    @Override
    public void execute(String[] args) {
        AnsiLog.printList(helpList);

    }

    @PostConstruct
    private void  initHelpList(){
        helpList.add("jps [查看运行java进程]");
        helpList.add("attach-pid pid号码 [连接目标java进程]");
        helpList.add("conf [查看当前配置信息]");
        helpList.add("r 文件号1 文件号2 [手动模式下执行热加载文件,支持多个]");
        helpList.add("show [手动模式查看ChangeFile]");
        helpList.add("exit [退出应用]");
        helpList.add("h [查看命令帮助说明]");

    }




}
