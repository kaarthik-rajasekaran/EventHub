package com.eventhub.eventhubbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventhubBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventhubBackendApplication.class, args);
    }

}