package com.hello.helloworld;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan({"com.hello.helloworld"})


@Import({com.gokiwi.core.utils.helper.CacheHelper.class,
        com.gokiwi.core.config.RedisSyncConnectionManager.class,
})
//@EnableAsync
public class HelloworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloworldApplication.class, args);
    }

}
