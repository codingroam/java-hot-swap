package com.acme.monitor.command;

import org.springframework.beans.BeansException;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/18 16:28
 */
@Component("exit")
public class ExitCmd implements Cmd, ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void execute(String[] args) {
        ((AnnotationConfigReactiveWebServerApplicationContext)this.applicationContext).close();
    }
}
