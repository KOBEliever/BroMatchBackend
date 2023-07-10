package com.dy.bromatchbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dy.bromatchbackend.mapper")
public class BroMatchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BroMatchBackendApplication.class, args);
    }

}
