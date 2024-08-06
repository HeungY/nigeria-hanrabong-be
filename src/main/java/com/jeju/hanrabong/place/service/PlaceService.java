package com.jeju.hanrabong.place.service;

import com.jeju.hanrabong.place.entity.Place;
import com.jeju.hanrabong.place.repository.PlaceRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.StringTokenizer;

@Service
public class PlaceService {

    @Value("${kakao.api.key}")
    private String apiKey;

    private final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    @Autowired
    private PlaceRepository placeRepository;

    public String searchPlaceAndSave(String query) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        String category = "FD6";
        String x="33.3617";
        String y="126.5292";
        String url = KAKAO_API_URL + "?query=" + query + "&category_group_code=" + category + "&x=" + x + "&y=" + y;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // JSON 파싱 및 데이터 저장
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray documents = jsonResponse.getJSONArray("documents");

        // query를 공백으로 분리하여 region과 fish 설정
        StringTokenizer tokenizer = new StringTokenizer(query," ");
        String region = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
        String fish = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);

            Place place = new Place();
            place.setKakaoPlaceId(Integer.parseInt(document.getString("id")));  // 가게 ID 저장
            place.setPlaceName(document.getString("place_name"));
            place.setPlaceUrl(document.getString("place_url"));
            place.setRoadAddressName(document.getString("road_address_name"));
            place.setRegion(region);
            place.setFish(fish);

            placeRepository.save(place);
        }

        return response.getBody();
    }
}
