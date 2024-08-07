package com.jeju.hanrabong.place.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeju.hanrabong.place.entity.Place;
import com.jeju.hanrabong.place.entity.PlaceDTO;
import com.jeju.hanrabong.place.entity.TempDTO;
import com.jeju.hanrabong.place.repository.PlaceRepository;
import com.jeju.hanrabong.user.entity.User;
import com.jeju.hanrabong.user.repository.UserRepository;
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
    private static final String API_URL_TEMPLATE = "https://www.khoa.go.kr/api/oceangrid/tideObsTemp/search.do?ServiceKey=FDNYheFOCUD2tnqe/ouq8w==&ObsCode=DT_0004&Date=%s&ResultType=json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();
    private final String[] fishes = {"다금바리", "참돔", "돌돔", "감성돔", "방어", "우럭", "벵에돔", "무늬오징어", "한치", "고등어", "돌멩이", "나이지리아산 한라봉", "뚱이", "징징이", "앨런"};
    private final Map<String, Integer> fishPoints = Map.ofEntries(
            Map.entry("다금바리", 100),
            Map.entry("참돔", 85),
            Map.entry("돌돔", 90),
            Map.entry("감성돔", 87),
            Map.entry("방어", 75),
            Map.entry("우럭", 60),
            Map.entry("벵에돔", 50),
            Map.entry("무늬오징어", 80),
            Map.entry("한치", 73),
            Map.entry("고등어", 77),
            Map.entry("돌멩이", 1),
            Map.entry("나이지리아산 한라봉", 2),
            Map.entry("뚱이", 3),
            Map.entry("징징이", 5),
            Map.entry("앨런", 14)
    );

    @Autowired
    public PlaceService(PlaceRepository placeRepository, UserRepository userRepository) {
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }

    public Optional<PlaceDTO> getPlaceByLocationWithRandomFish(String location, String nickname) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        if (user.getCount() <= 0) {
            return Optional.empty();
        }
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

        // 포인트와 낚시 횟수 업데이트
        int points = fishPoints.get(randomFish);
        user.setScore(user.getScore() + points);
        user.setCount(user.getCount() - 1);
        userRepository.save(user);

        return Optional.of(new PlaceDTO(
                randomFish,
                points,
                place.getPlaceName(),
                place.getPlaceUrl(),
                place.getRoadAddressName()
        ));
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

    public TempDTO getCurrentTemperature() {
//        double currentTemp = getCurrentWaterTemperature();
//        return String.format("%.2f°C", currentTemp);
        double currentTemp = getCurrentWaterTemperature();
        String formattedTemp = String.format("%.2f°C", currentTemp);
        return new TempDTO(formattedTemp);
    }

    @Scheduled(fixedRate = 3600000) // every hour
    public void fetchAndPrintData() {
        TempDTO currentTemp = getCurrentTemperature();
        System.out.println("Current Water Temperature: " + currentTemp.getData());
    }
}
