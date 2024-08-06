package com.jeju.hanrabong.place.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "kakao_place_id")
    private int kakaoPlaceId;

    @Column(name = "place_name")
    private String placeName;
    @Column(name = "place_url")
    private String placeUrl;
    @Column(name = "road_address_name")
    private String roadAddressName;

    private String region;

    private String fish;
}
