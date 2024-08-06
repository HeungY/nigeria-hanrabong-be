package com.jeju.hanrabong.redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisExample implements CommandLineRunner {

    @Autowired
    private RedisService redisService;

    @Override
    public void run(String... args) throws Exception {
        Map<String, String> allValues = redisService.getAllValues();
        System.out.println("All values in Redis:");
        allValues.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}