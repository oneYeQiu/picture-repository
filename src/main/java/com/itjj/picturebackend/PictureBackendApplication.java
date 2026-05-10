package com.itjj.picturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.itjj.picturebackend.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true) // 开启AspectJ自动代理
public class PictureBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(PictureBackendApplication.class, args);
//
//        System.getProperties().list(System.out);
//        System.out.println("**********************");
//        System.out.println(System.getProperty("user.home"));
    }

}
