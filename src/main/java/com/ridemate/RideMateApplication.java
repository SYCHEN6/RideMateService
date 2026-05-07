package com.ridemate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ridemate.*.mapper")
public class RideMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(RideMateApplication.class, args);
    }

}
