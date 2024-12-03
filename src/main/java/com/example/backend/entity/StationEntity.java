package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "station")
@Data
@Builder
public class StationEntity {

    @Id
    private String stationCode;    // 측정소 코드 (PK)
    
    private String stationName;    // 측정소명
    private String addr;           // 주소
    private String mangName;       // 측정망 정보
    private String item;           // 측정 항목
    private String year;           // 설치년도
    
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double dmX;            // 경도
    
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double dmY;            // 위도
}