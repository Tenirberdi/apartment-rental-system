package com.example.system.Repositories;

import com.example.system.Entities.HouseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseTypeRepo extends JpaRepository<HouseType, Long> {
    HouseType findByType(String type);
    boolean existsByType(String type);
}
