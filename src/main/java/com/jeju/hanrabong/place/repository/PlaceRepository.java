package com.jeju.hanrabong.place.repository;

import com.jeju.hanrabong.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place,Integer> {
    List<Place> findByRegionAndFish(String region, String fish);

}
