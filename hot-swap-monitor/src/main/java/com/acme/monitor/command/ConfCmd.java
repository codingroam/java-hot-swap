package com.acme.monitor.command;

import com.acme.common.AnsiLog;
import com.acme.monitor.config.ApplicationConfigContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/16 11:22
 */

@Component("conf")
public class ConfCmd implements Cmd{
    @Autowired
    private ApplicationConfigContext applicationConfigContext;
    @Override
    public void execute(String[] args) {
        AnsiLog.println(applicationConfigContext.toString());
    }
}
