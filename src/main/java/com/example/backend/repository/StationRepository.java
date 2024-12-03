package com.example.backend.repository;

import com.example.backend.entity.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<StationEntity, String> {
} 