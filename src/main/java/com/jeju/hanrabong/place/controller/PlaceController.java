package com.jeju.hanrabong.place.controller;

import com.jeju.hanrabong.place.service.PlaceService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vi")
public class PlaceController {
    @Autowired
    PlaceService placeService;



}
