package com.example.backend.service;

import com.example.backend.entity.StationEntity;
import com.example.backend.repository.StationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class StationService {

    @Value("${serviceKey}")
    private String serviceKey;

    private final StationRepository stationRepository;
    private final WebClient webClient;

    private static final String BASE_URL = "https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList";

    public void fetchAndSaveStations() {
        try {
//            // 서비스키에서 이미 인코딩된 문자를 디코딩
//            String decodedServiceKey = serviceKey
//                    .replace("%2B", "+")
//                    .replace("%2F", "/")
//                    .replace("%3D", "=");
//
//            // 다시 정확하게 인코딩
//            String encodedServiceKey = decodedServiceKey
//                    .replace("+", "%2B")
//                    .replace("/", "%2F")
//                    .replace("=", "%3D");

            String url = BASE_URL +
                    "?serviceKey=" + serviceKey +
                    "&returnType=json" +
                    "&numOfRows=1000" +
                    "&pageNo=1" +
                    "&ver=1.1";

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
                List<StationEntity> stations = new ArrayList<>();
                for (JsonNode item : items) {
                    StationEntity station = new StationEntity(
                            item.path("stationCode").asText(),
                            item.path("stationName").asText(),
                            item.path("addr").asText(),
                            item.path("mangName").asText(),
                            item.path("item").asText(),
                            item.path("year").asText(),
                            Double.parseDouble(item.path("dmX").asText()),
                            Double.parseDouble(item.path("dmY").asText())
                    );
                    stations.add(station);
                }
                stationRepository.saveAll(stations);
            }

        } catch (Exception e) {
            log.error("측정소 정보 조회 중 오류 발생: ", e);
            throw new RuntimeException("측정소 정보를 가져오는데 실패했습니다.", e);
        }
    }
}