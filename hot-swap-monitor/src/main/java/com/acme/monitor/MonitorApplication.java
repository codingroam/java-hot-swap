package com.acme.monitor;

import com.acme.common.AnsiLog;
import com.acme.monitor.config.ApplicationConfigContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

@SpringBootApplication
public class MonitorApplication {



    @Autowired
    private ApplicationConfigContext applicationConfigContext;

    public static void main(String[] args) {
        ApplicationHome home = new ApplicationHome(MonitorApplication.class);
        File jarFile = home.getSource();
        String homePath = jarFile.getParentFile().toString();
        AnsiLog.info("homePath:{}",homePath);
        System.out.println("homePath:"+homePath);
        System.setProperty("home.path",homePath);
        SpringApplication.run(MonitorApplication.class, args);
    }

}
