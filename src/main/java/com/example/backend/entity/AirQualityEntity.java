package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "air_quality")
@Data
@Builder
public class AirQualityEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_code")
    private StationEntity station;
    
    private String dataTime;
    private String sidoName;
    private String mangName;
    
    // 미세먼지 관련
    private String pm10Value;
    private String pm10Value24;
    private String pm10Grade;
    private String pm10Grade1h;
    private String pm10Flag;
    
    // 초미세먼지 관련
    private String pm25Value;
    private String pm25Value24;
    private String pm25Grade;
    private String pm25Grade1h;
    private String pm25Flag;
    
    // 다른 대기오염물질
    private String so2Value;
    private String so2Grade;
    private String so2Flag;
    
    private String coValue;
    private String coGrade;
    private String coFlag;
    
    private String o3Value;
    private String o3Grade;
    private String o3Flag;
    
    private String no2Value;
    private String no2Grade;
    private String no2Flag;
    
    // 통합대기환경수치
    private String khaiValue;
    private String khaiGrade;

   
}