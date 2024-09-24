package com.jeju.hanrabong.place.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jeju.hanrabong.place.entity.PlaceDTO;
import com.jeju.hanrabong.place.entity.TempDTO;
import com.jeju.hanrabong.place.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PlaceController {
    @Autowired
    PlaceService placeService;

    @GetMapping("/temperature")
    public ResponseEntity<TempDTO> temperature(){
        return ResponseEntity.ok(placeService.getCurrentTemperature());
    }

    @GetMapping("/fishing")
    public ResponseEntity<?> fishing(@RequestParam String location, @RequestParam String nickname) {    // body로 날리는지 쿼리스트링인지
        Optional<PlaceDTO> placeResponse = placeService.getPlaceByLocationWithRandomFish(location, nickname);
        if (placeResponse.isPresent()) {
            return ResponseEntity.ok(placeResponse.get());
        } else {
            return ResponseEntity.status(400).body("No fishing attempts left or user not found");
        }
    }
}
