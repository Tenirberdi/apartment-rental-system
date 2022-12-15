package com.example.system.Repositories;

import com.example.system.Entities.Viewers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewersRepo extends JpaRepository<Viewers, Long> {
    @Query( value = "SELECT if(count(*)>0, 1, 0) FROM `viewers` WHERE ad_id = ?1 and viewer_id = ?2" ,nativeQuery = true)
    int alreadyViewed(long adId, long userId);


}
