package com.jeju.hanrabong.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringRedisExampleApplication {

    @Autowired
    private RedisService redisService;

    public static void main(String[] args) {
        SpringApplication.run(SpringRedisExampleApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            // Print the filtered fish based on the current temperature
            redisService.printFilteredFish();
        };
    }
}
