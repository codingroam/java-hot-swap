package com.acme.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MonitorApplicationTests {

    @Test
    void contextLoads() {

        UserInputMonitor userInputMonitor = new UserInputMonitor();
        userInputMonitor.printInteractiveInfo();
    }

}
