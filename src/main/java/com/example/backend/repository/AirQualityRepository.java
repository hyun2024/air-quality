package com.example.backend.repository;

import com.example.backend.entity.AirQualityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirQualityRepository extends JpaRepository<AirQualityEntity, Long> {
    List<AirQualityEntity> findBySidoName(String sidoName);
    List<AirQualityEntity> findByStation_StationCode(String stationCode);
}