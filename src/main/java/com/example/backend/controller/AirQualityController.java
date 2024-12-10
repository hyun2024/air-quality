package com.example.backend.controller;

import com.example.backend.entity.AirQualityDTO;
import com.example.backend.entity.AirQualityEntity;
import com.example.backend.service.AirQualityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/api/air-quality")
@RequiredArgsConstructor
public class AirQualityController {

    private final AirQualityService airQualityService;

    @GetMapping("/update")
    public String fetchAirQualityData() {
        try {
            airQualityService.fetchAndSaveAirQuality();
            return "success";
        } catch (Exception e) {
            log.error("대기질 정보 업데이트 중 오류 발생: ", e);
            return "error";
        }
    }
    @GetMapping("/latest")
    public List<AirQualityEntity> getAirQualityData() {
        try {
            return airQualityService.getLatestAirQualityData(); // 모든 대기질 데이터 조회
        } catch (Exception e) {
            log.error("대기질 데이터 조회 중 오류 발생: ", e);
            return null; // 오류 발생 시 null 반환
        }
    }
} 