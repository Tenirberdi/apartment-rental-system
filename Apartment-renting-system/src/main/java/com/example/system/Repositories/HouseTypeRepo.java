package com.example.system.repositories;

import com.example.system.models.HouseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseTypeRepo extends JpaRepository<HouseType, Long> {
    HouseType findByType(String type);
    boolean existsByType(String type);
}
