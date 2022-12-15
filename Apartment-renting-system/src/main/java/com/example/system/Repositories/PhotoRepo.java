package com.example.system.Repositories;

import com.example.system.Entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepo extends JpaRepository<Photo, Long> {

    @Modifying
    void deleteByPhotoName(String photoName);

    @Query(value = "SELECT photo_name FROM `photos` WHERE ad_id = ?1" , nativeQuery = true)
    List<String> getAdPhotoNames(long adId);
}
