package com.jeju.hanrabong.place.controller;

import com.jeju.hanrabong.place.service.PlaceService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vi")
public class PlaceController {
    @Autowired
    PlaceService placeService;

    @GetMapping("/search")
    public String searchPlace(@RequestParam String query) throws JSONException {
        return placeService.searchPlaceAndSave(query);
    }

    @GetMapping("/temperature")
    public ResponseEntity<String> temperature(){
        String str = "더워죽습니다. 물고기 삶아집니다.";
        return ResponseEntity.ok(str);
    }

    @GetMapping("/fishing")
    public ResponseEntity<?> fishing(@RequestParam String location){    // body로 날리는지 쿼리스트링인지
        String str ="Service에 location을 전달해서 무언갈 받아야 합니다잉";
        return ResponseEntity.ok(str);
    }

}
