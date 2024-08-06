package com.jeju.hanrabong.redis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Service
public class ApiService {

//    @Value("${api.key}")
//    private String API_KEY;

    private static final String API_URL_TEMPLATE = "https://www.khoa.go.kr/api/oceangrid/tideObsTemp/search.do?ServiceKey=FDNYheFOCUD2tnqe/ouq8w==&ObsCode=DT_0004&Date=%s&ResultType=json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public double getCurrentWaterTemperature() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String apiUrl = String.format(API_URL_TEMPLATE, currentDate);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode dataNode = rootNode.path("result").path("data");
            if (dataNode.isArray()) {
                JsonNode mostRecentData = null;
                for (Iterator<JsonNode> it = dataNode.elements(); it.hasNext(); ) {
                    JsonNode currentNode = it.next();
                    if (mostRecentData == null || currentNode.path("record_time").asText().compareTo(mostRecentData.path("record_time").asText()) > 0) {
                        mostRecentData = currentNode;
                    }
                }
                if (mostRecentData != null) {
                    return mostRecentData.path("water_temp").asDouble();
                } else {
                    throw new RuntimeException("No data found for the current date.");
                }
            } else {
                throw new RuntimeException("Invalid response format.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch current temperature", e);
        }
    }

    @Scheduled(fixedRate = 3600000) // every hour
    public void fetchAndPrintData() {
        double currentTemp = getCurrentWaterTemperature();
        System.out.println("Current Water Temperature: " + currentTemp);
    }
}
