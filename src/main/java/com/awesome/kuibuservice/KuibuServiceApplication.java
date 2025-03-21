package com.awesome.kuibuservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.awesome.kuibuservice.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class KuibuServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KuibuServiceApplication.class, args);
    }

}
