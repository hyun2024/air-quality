package com.example.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AirQualityItem {
    private String stationName;
    private String dataTime;
    private String sidoName;
    private String mangName;
    
    private String pm10Value;
    private String pm10Value24;
    private String pm10Grade;
    private String pm10Grade1h;
    private String pm10Flag;
    
    private String pm25Value;
    private String pm25Value24;
    private String pm25Grade;
    private String pm25Grade1h;
    private String pm25Flag;
    
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
    
    private String khaiValue;
    private String khaiGrade;
} 