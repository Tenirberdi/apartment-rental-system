package com.example.system.Repositories;

import com.example.system.Entities.PromotionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionTypeRepo extends JpaRepository<PromotionType, Long> {
    PromotionType findByName(String name);
}
