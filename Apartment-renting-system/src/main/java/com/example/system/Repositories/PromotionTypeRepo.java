package com.example.system.repositories;

import com.example.system.models.PromotionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionTypeRepo extends JpaRepository<PromotionType, Long> {
    PromotionType findByName(String name);
}
