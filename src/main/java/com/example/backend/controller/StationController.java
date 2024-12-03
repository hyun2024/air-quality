package com.example.backend.controller;

import com.example.backend.service.StationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping("station")
    public String fetchStationData() {
        try {
            stationService.fetchAndSaveStations();
            return "success";
        } catch (Exception e) {
            log.error("측정소 정보 업데이트 중 오류 발생: ", e);
            return "error";
        }
    }
} 