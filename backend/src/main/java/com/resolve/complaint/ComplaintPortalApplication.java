package com.resolve.complaint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ComplaintPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComplaintPortalApplication.class, args);
    }
}