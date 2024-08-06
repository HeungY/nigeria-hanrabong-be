package com.jeju.hanrabong.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.Map;

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
//            redisService.printFilteredFish();

            // Retrieve and print the filtered fish map
            Map<String, Object> filteredFishMap = redisService.getFilteredFishMap();
            System.out.println("Filtered Fish Map: " + filteredFishMap);
        };
    }
}
