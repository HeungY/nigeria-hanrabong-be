package com.jeju.hanrabong.place.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeju.hanrabong.place.entity.Place;
import com.jeju.hanrabong.place.entity.PlaceDTO;
import com.jeju.hanrabong.place.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PlaceService {
<<<<<<< HEAD
=======
    private static final String API_URL_TEMPLATE = "https://www.khoa.go.kr/api/oceangrid/tideObsTemp/search.do?ServiceKey=FDNYheFOCUD2tnqe/ouq8w==&ObsCode=DT_0004&Date=%s&ResultType=json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PlaceRepository placeRepository;
    private final Random random = new Random();
    private final String[] fishes = {"방어", "참돔", "돌돔", "다금바리", "고등어"};
    private final Map<String, Integer> fishPoints = Map.of(
            "방어", 100,
            "참돔", 150,
            "돌돔", 200,
            "다금바리", 250,
            "고등어", 70
    );

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public PlaceDTO getPlaceByLocationWithRandomFish(String location) {
        String randomFish = fishes[random.nextInt(fishes.length)];
        List<Place> places = placeRepository.findByRegionAndFish(location, randomFish);

        if (places.isEmpty()) { // 정확한 지역과 생선이 없다면, 해당 지역의 횟집 검색
            places = placeRepository.findByRegionAndFish(location, "횟집");
        }
        if (places.isEmpty()) { // 해당 지역의 횟집도 없다면, 제주 전역의 타겟 생선 검색
            places = placeRepository.findByRegionAndFish("제주", randomFish);
        }
        if (places.isEmpty()) { // 제주 전역의 타겟 생선이 없다면, 제주 전역의 횟집 검색
            places = placeRepository.findByRegionAndFish("제주", "횟집");
        }


        Place place = places.get(random.nextInt(places.size()));
        return new PlaceDTO(
                randomFish,
                fishPoints.get(randomFish),
                place.getPlaceName(),
                place.getPlaceUrl(),
                place.getRoadAddressName()
        );
    }

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

    public String getCurrentTemperature() {
        double currentTemp = getCurrentWaterTemperature();
        return String.format("%.2f°C", currentTemp);
    }

    @Scheduled(fixedRate = 3600000) // every hour
    public void fetchAndPrintData() {
        String currentTemp = getCurrentTemperature();
        System.out.println("Current Water Temperature: " + currentTemp);
    }
>>>>>>> feature/place
}
