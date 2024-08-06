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
}
