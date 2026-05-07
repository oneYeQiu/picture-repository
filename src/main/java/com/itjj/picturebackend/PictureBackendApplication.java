package com.itjj.picturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itjj.picturebackend.mapper")
public class PictureBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(PictureBackendApplication.class, args);

        System.getProperties().list(System.out);
        System.out.println("**********************");
        System.out.println(System.getProperty("user.home"));
    }

}
