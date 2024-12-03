package com.example.backend.service;

import com.example.backend.entity.AirQualityEntity;
import com.example.backend.entity.StationEntity;
import com.example.backend.repository.AirQualityRepository;
import com.example.backend.repository.StationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirQualityService {

    @Value("${serviceKey}")
    private String serviceKey;

    private final AirQualityRepository airQualityRepository;
    private final StationRepository stationRepository;
    private final WebClient webClient;

    private static final String BASE_URL = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";

    public void fetchAndSaveAirQuality() {
        try {
            String url = BASE_URL +
                    "?serviceKey=" + serviceKey +
                    "&returnType=json" +
                    "&numOfRows=1000" +
                    "&pageNo=1" +
                    "&sidoName=%EC%A0%84%EA%B5%AD" +
                    "&ver=1.5";

            log.info("Request URL: {}", url);

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Response: {}", response);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");


            // HTML 응답인지 확인
            if (response != null && response.trim().startsWith("<!DOCTYPE html") || response.contains("<html>")) {
                log.error("HTML 응답을 수신했습니다. URL과 파라미터를 확인하세요.");
                throw new RuntimeException("HTML 응답을 받았습니다. API 요청이 실패한 것으로 보입니다.");
            }


            if (items.isArray()) {
                List<AirQualityEntity> airQualities = new ArrayList<>();
                for (JsonNode item : items) {
                    String stationName = item.path("stationName").asText();
                    StationEntity station = stationRepository.findById(item.path("stationCode").asText()).orElse(null);

                    AirQualityEntity airQuality = AirQualityEntity.builder()
                            .station(station)
                            .dataTime(item.path("dataTime").asText())
                            .sidoName(item.path("sidoName").asText())
                            .mangName(item.path("mangName").asText())
                            .pm10Value(item.path("pm10Value").asText())
                            .pm10Value24(item.path("pm10Value24").asText())
                            .pm10Grade(item.path("pm10Grade").asText())
                            .pm10Grade1h(item.path("pm10Grade1h").asText())
                            .pm25Value(item.path("pm25Value").asText())
                            .pm25Value24(item.path("pm25Value24").asText())
                            .pm25Grade(item.path("pm25Grade").asText())
                            .pm25Grade1h(item.path("pm25Grade1h").asText())
                            .so2Value(item.path("so2Value").asText())
                            .so2Grade(item.path("so2Grade").asText())
                            .coValue(item.path("coValue").asText())
                            .coGrade(item.path("coGrade").asText())
                            .o3Value(item.path("o3Value").asText())
                            .o3Grade(item.path("o3Grade").asText())
                            .no2Value(item.path("no2Value").asText())
                            .no2Grade(item.path("no2Grade").asText())
                            .khaiValue(item.path("khaiValue").asText())
                            .khaiGrade(item.path("khaiGrade").asText())
                            .build();
                    airQualities.add(airQuality);
                }
                airQualityRepository.saveAll(airQualities);
            }

        } catch (Exception e) {
            log.error("대기질 정보 조회 중 오류 발생: ", e);
            throw new RuntimeException("대기질 정보를 가져오는데 실패했습니다.", e);
        }
    }
} 