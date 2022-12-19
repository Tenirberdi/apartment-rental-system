package com.example.system.repositories;

import com.example.system.models.Photo;
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

    @Query(value = "select count(photo_name) from (select photo_name from photos UNION SELECT photo_name from users where photo_name is not null) as list where photo_name like CONCAT('%' ,?1, '%')", nativeQuery = true)
    int countByPhotoName(String photoName);

}
