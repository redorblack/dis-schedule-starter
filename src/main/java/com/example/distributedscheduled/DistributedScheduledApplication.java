package com.example.distributedscheduled;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DistributedScheduledApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedScheduledApplication.class, args);
    }

}
