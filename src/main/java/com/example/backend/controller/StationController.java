package com.example.backend.controller;

import com.example.backend.entity.StationEntity;
import com.example.backend.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping
    public List<StationEntity> getStations() {
        return stationService.getAllStations(); // 모든 측정소 데이터를 반환하는 메소드
    }

    @GetMapping("/{stationCode}")
    public StationEntity getStationByCode(@PathVariable String stationCode) {
        return stationService.getStationByCode(stationCode); // 특정 측정소 데이터를 반환하는 메소드
    }
}