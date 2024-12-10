package com.example.backend.service;

import com.example.backend.entity.AirQualityDTO;
import com.example.backend.entity.AirQualityEntity;
import com.example.backend.entity.StationEntity;
import com.example.backend.repository.AirQualityRepository;
import com.example.backend.repository.StationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    private String buildUrl() {
        try {
            String encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");
            return BASE_URL +
                    "?serviceKey=" + encodedServiceKey +
                    "&returnType=json" +
                    "&numOfRows=1000" +
                    "&pageNo=1" +
                    "&sidoName=%EC%A0%84%EA%B5%AD" +
                    "&ver=1.5";
        } catch (UnsupportedEncodingException e) {
            log.error("서비스 키 인코딩 중 오류 발생: ", e);
            throw new RuntimeException("서비스 키 인코딩에 실패했습니다.", e);
        }
    }

    public void fetchAndSaveAirQuality() {
        try {
            String urlString = buildUrl();
            log.info("Request URL: {}", urlString);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("API 요청 실패: 응답 코드 {}", responseCode);
                throw new RuntimeException("API 요청이 실패했습니다. 응답 코드: " + responseCode);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            String responseBody = response.toString();
            log.info("Response: {}", responseBody);

            if (responseBody.trim().startsWith("<")) {
                log.error("HTML 또는 XML 응답을 수신했습니다. URL과 파라미터를 확인하세요.");
                log.error("응답 내용: {}", responseBody);
                throw new RuntimeException("HTML 또는 XML 응답을 받았습니다. API 요청이 실패한 것으로 보입니다.");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode items = rootNode.path("response").path("body").path("items");

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



    // 대기질 데이터 반환 메소드 추가
    public List<AirQualityEntity> getLatestAirQualityData() {
        // 이미 쿼리에서 최신 데이터만 가져온다면 바로 반환
        return airQualityRepository.findLatestAirQualityData();
    }
}
