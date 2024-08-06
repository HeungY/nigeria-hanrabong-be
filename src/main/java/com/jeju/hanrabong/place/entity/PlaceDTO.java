package com.jeju.hanrabong.place.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceDTO {
    private String fish;
    private int point;
    private String placeName;
    private String placeUrl;
    private String roadAddressName;
}
