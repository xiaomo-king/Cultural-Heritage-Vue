package com.jiangyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JiangyouApplication {
    public static void main(String[] args) {
        SpringApplication.run(JiangyouApplication.class, args);
        System.out.println("\n=========================================");
        System.out.println("  江右拾遗 后端启动成功！");
        System.out.println("  API 地址: http://localhost:8080/api");
        System.out.println("=========================================\n");
    }
}
