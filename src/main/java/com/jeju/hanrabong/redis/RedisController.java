package com.jeju.hanrabong.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/set")
    public String setValue() {
        redisService.saveValue("myKey", "myValue");
        return "Value set in Redis";
    }

    @GetMapping("/get")
    public String getValue() {
        Object value = redisService.getValue("myKey");
        return "Value retrieved from Redis: " + value;
    }
}