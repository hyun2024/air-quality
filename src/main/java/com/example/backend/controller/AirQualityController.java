package com.example.backend.controller;

import com.example.backend.service.AirQualityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
} 