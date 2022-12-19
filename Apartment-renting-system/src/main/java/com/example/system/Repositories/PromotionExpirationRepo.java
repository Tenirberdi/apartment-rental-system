package com.example.system.repositories;

import com.example.system.models.PromotionExpiration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionExpirationRepo extends JpaRepository<PromotionExpiration, Long> {

    @Modifying
    @Query(value = "UPDATE `ads` as a JOIN `promotion_expiration` as pe on pe.ad_id = a.id SET a.`promotion_type_id`= null  WHERE pe.expiration_date < CURDATE()", nativeQuery = true)
    void takeOffPromotionType();
    @Modifying
    @Query(value = "DELETE FROM `promotion_expiration` WHERE `expiration_date` < CURDATE()", nativeQuery = true)
    void expire();
}
