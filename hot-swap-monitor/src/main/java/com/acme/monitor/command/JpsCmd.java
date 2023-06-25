package com.acme.monitor.command;

import com.acme.monitor.config.ApplicationConfigContext;
import com.acme.monitor.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/10 11:44
 */

@Component("jps")
public class JpsCmd implements Cmd{

    @Autowired
    private Map<String,Worker> workerFactory;
    @Autowired
    private ApplicationConfigContext applicationConfigContext;

    @Override
    public void execute(String[] args) {
        workerFactory.get(applicationConfigContext.mode).jps();
    }
}
