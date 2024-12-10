package com.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirQualityDTO {

    private Long id; // ID
    private String stationName; // 측정소 이름
    private String dataTime; // 데이터 시간
    private String sidoName; // 시도 이름
    private String mangName; // 망 이름

    // 미세먼지 관련
    private String pm10Value; // PM10 값
    private String pm10Value24; // PM10 24시간 평균 값
    private String pm10Grade; // PM10 등급
    private String pm10Grade1h; // PM10 1시간 등급
    private String pm10Flag; // PM10 플래그

    // 초미세먼지 관련
    private String pm25Value; // PM2.5 값
    private String pm25Value24; // PM2.5 24시간 평균 값
    private String pm25Grade; // PM2.5 등급
    private String pm25Grade1h; // PM2.5 1시간 등급
    private String pm25Flag; // PM2.5 플래그

    // 다른 대기오염물질
    private String so2Value; // SO2 값
    private String so2Grade; // SO2 등급
    private String so2Flag; // SO2 플래그

    private String coValue; // CO 값
    private String coGrade; // CO 등급
    private String coFlag; // CO 플래그

    private String o3Value; // O3 값
    private String o3Grade; // O3 등급
    private String o3Flag; // O3 플래그

    private String no2Value; // NO2 값
    private String no2Grade; // NO2 등급
    private String no2Flag; // NO2 플래그

    // 통합대기환경수치
    private String khaiValue; // 통합대기환경수치
    private String khaiGrade; // 통합대기환경수치 등급


    public AirQualityDTO(StationEntity station, String pm10Value, String pm25Value) {
        this.stationName = station != null ? station.getStationName() : null; // stationName 설정
        this.pm10Value = pm10Value;
        this.pm25Value = pm25Value;
    }
}