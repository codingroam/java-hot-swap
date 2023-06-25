package com.acme.monitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/12 22:56
 */
@Component
@PropertySource(value = {"classpath:application.properties","file:${home.path}/conf.properties"},ignoreResourceNotFound = true)
public class ApplicationConfigContext {

    @Value("${mode}")
    public String mode;

    @Value("${monitorPath}")
    public String monitorPath;

    @Value("${pidnum}")
    public String pid;

    @Value("${serverUrl}")
    public String serverUrl;

    @Value("${targetPort}")
    public String targetPort;

    @Value("${hotSwapStyle}")
    public String hotSwapStyle;

    public volatile boolean isAttach;

    @Override
    public String toString() {
        return "当前配置:\n\tconfig{\n\t" +
                "mode='" + mode + '\'' +
                ",\n\tmonitorPath=" + monitorPath  +
                ", \n\tpid=" + pid +
                ", \n\tserverUrl='" + serverUrl + '\'' +
                ", \n\ttargetPort=" + targetPort +
                ", \n\thotSwapStyle=" + hotSwapStyle +
                "\n\t}";
    }

}
