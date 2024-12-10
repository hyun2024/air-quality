package com.example.backend.repository;

import com.example.backend.entity.AirQualityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AirQualityRepository extends JpaRepository<AirQualityEntity, Long> {
    List<AirQualityEntity> findBySidoName(String sidoName);
    List<AirQualityEntity> findByStation_StationCode(String stationCode);

    @Query(value = """
            SELECT aq.*
            FROM air_quality aq
            INNER JOIN (
                SELECT station_code, MAX(dataTime) as max_time
                FROM air_quality
                GROUP BY station_code
            ) latest
            ON aq.station_code = latest.station_code
            AND aq.dataTime = latest.max_time
            """, nativeQuery = true)
    List<AirQualityEntity> findLatestAirQualityData();
}