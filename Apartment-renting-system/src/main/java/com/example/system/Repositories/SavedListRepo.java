package com.example.system.Repositories;

import com.example.system.Entities.SavedList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SavedListRepo extends JpaRepository<SavedList, Long> {
    SavedList findByAdIdAndRenteeId(long adId, long renteeId);
    List<SavedList> findAllByRenteeId(long renteeId);
}
