package com.example.volleybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VolleybotApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolleybotApplication.class, args);
    }
}
