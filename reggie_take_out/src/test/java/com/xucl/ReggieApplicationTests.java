package com.xucl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;

@Slf4j
@SpringBootTest
class ReggieApplicationTests {

    @Test
    void contextLoads() {
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            log.info("路径=== {}",System.getProperty("user.dir"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
