package com.jeju.hanrabong.redis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ApiService apiService;

    public void saveValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Map<String, String> getAllValues() {
        Set<String> keys = redisTemplate.keys("*");
        return keys.stream().collect(Collectors.toMap(key -> key, key -> {
            Object value = getValue(key);
            if (value instanceof byte[]) {
                return new String((byte[]) value, StandardCharsets.UTF_8);
            }
            return value.toString();
        }));
    }

    public void clearAllValues() {
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    public Map<String, String> getFishBasedOnTemperature() {
        double currentTemp = apiService.getCurrentWaterTemperature();
        Map<String, String> allValues = getAllValues();

        return allValues.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    String value = entry.getValue();
                    JsonNode jsonNode;
                    try {
                        jsonNode = new ObjectMapper().readTree(value);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse JSON", e);
                    }

                    double springTemp = jsonNode.path("봄온").asDouble();
                    double summerTemp = jsonNode.path("여름온").asDouble();
                    double fallTemp = jsonNode.path("가을온").asDouble();
                    double winterTemp = jsonNode.path("겨울온").asDouble();
                    String springFish = jsonNode.path("봄물").asText();
                    String summerFish = jsonNode.path("여름물").asText();
                    String fallFish = jsonNode.path("가을물").asText();
                    String winterFish = jsonNode.path("겨울물").asText();

                    double minDiff = Math.abs(currentTemp - springTemp);
                    String closestFish = springFish;

                    if (Math.abs(currentTemp - summerTemp) < minDiff) {
                        minDiff = Math.abs(currentTemp - summerTemp);
                        closestFish = summerFish;
                    }
                    if (Math.abs(currentTemp - fallTemp) < minDiff) {
                        minDiff = Math.abs(currentTemp - fallTemp);
                        closestFish = fallFish;
                    }
                    if (Math.abs(currentTemp - winterTemp) < minDiff) {
                        closestFish = winterFish;
                    }

                    return closestFish;
                }
        ));
    }

    public Map<String, Object> getFilteredFishWithCurrentTemp() {
        clearAllValues();
        double currentTemp = apiService.getCurrentWaterTemperature();
        Map<String, String> filteredFish = getFishBasedOnTemperature();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("current_t", currentTemp);
        resultMap.putAll(filteredFish);
        return resultMap;
    }

    public void printFilteredFish() {
        Map<String, Object> resultMap = getFilteredFishWithCurrentTemp();
        resultMap.forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
    }
}
