package com.than;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ThanDriverBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThanDriverBackApplication.class, args);
    }
}