package com.example.system.Repositories;

import com.example.system.Entities.Ad;
import com.example.system.Projections.AdView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepo extends JpaRepository<Ad, Long> {


    @Query(value = "SELECT count(id) FROM `ads` WHERE renter_id = ?1 and available = 1 and enabled = 1", nativeQuery = true)
    int getActiveAdAmount(long id);

    @Query(value = "SELECT a.id, a.title, a.description, a.price_per_month, a.total_room_amount, a.bath_room_amount, a.bed_room_amount , a.kitchen_room_amount, a.area, a.which_floor, a.furniture, a.location, a.date_of_posting , a.available, ht.type as house_type, a.renter_id , count(vw.viewer_id) as 'viewers' , COALESCE(fv.saved_amount, 0) as 'saved_amount', pt.name as 'promotion_type' FROM `ads` as a JOIN house_types as ht on ht.id = a.house_type_id left JOIN (select sl.ad_id, count(sl.rentee_id) as 'saved_amount' from `saved_list` as sl GROUP by sl.ad_id) as fv on fv.ad_id = a.id left JOIN `viewers` as vw on vw.ad_id = a.id LEFT JOIN promotion_types as pt on pt.id = a.promotion_type_id WHERE renter_id = ?1  and a.enabled = 1 GROUP BY 1 ORDER BY pt.ordered desc;", nativeQuery = true)
    List<AdView> getAds(long userId);

    @Query(value = "SELECT a.id, a.title, a.description, a.price_per_month, a.total_room_amount, a.bath_room_amount, a.bed_room_amount , a.kitchen_room_amount, a.area, a.which_floor, a.furniture, a.location, a.date_of_posting , a.available, ht.type as house_type, a.renter_id , count(vw.viewer_id) as 'viewers' , COALESCE(fv.saved_amount, 0) as 'saved_amount', pt.name as 'promotion_type' FROM `ads` as a JOIN house_types as ht on ht.id = a.house_type_id left JOIN (select sl.ad_id, count(sl.rentee_id) as 'saved_amount' from `saved_list` as sl GROUP by sl.ad_id) as fv on fv.ad_id = a.id left JOIN `viewers` as vw on vw.ad_id = a.id LEFT JOIN promotion_types as pt on pt.id = a.promotion_type_id WHERE a.enabled = 1 and a.available = 1 GROUP BY 1 ORDER BY pt.ordered desc;", nativeQuery = true)
    List<AdView> getAds();

    @Query(value = "SELECT  a.id, a.title, a.description, a.price_per_month, a.total_room_amount, a.bath_room_amount, a.bed_room_amount , a.kitchen_room_amount, a.area, a.which_floor, a.furniture, a.location, a.date_of_posting , a.available, ht.type as house_type, a.renter_id , count(vw.viewer_id) as 'viewers' , COALESCE(fv.saved_amount, 0) as 'saved_amount', pt.name as 'promotion_type' FROM `ads` as a JOIN house_types as ht on ht.id = a.house_type_id left JOIN (select sl.ad_id, count(sl.rentee_id) as 'saved_amount' from `saved_list` as sl GROUP by sl.ad_id) as fv on fv.ad_id = a.id left JOIN `viewers` as vw on vw.ad_id = a.id LEFT JOIN promotion_types as pt on pt.id = a.promotion_type_id WHERE a.id = ?1  and a.enabled = 1 GROUP BY 1 ORDER BY pt.ordered desc;", nativeQuery = true)
    AdView getAdById(long id);

    @Query(value = "SELECT  a.id, a.title, a.description, a.price_per_month, a.total_room_amount, a.bath_room_amount, a.bed_room_amount , a.kitchen_room_amount, a.area, a.which_floor, a.furniture, a.location, a.date_of_posting , a.available, ht.type as house_type, a.renter_id , count(vw.viewer_id) as 'viewers' , COALESCE(fv.saved_amount, 0) as 'saved_amount', pt.name as 'promotion_type' FROM `ads` as a JOIN house_types as ht on ht.id = a.house_type_id left JOIN (select sl.ad_id, count(sl.rentee_id) as 'saved_amount' from `saved_list` as sl GROUP by sl.ad_id) as fv on fv.ad_id = a.id left JOIN `viewers` as vw on vw.ad_id = a.id LEFT JOIN promotion_types as pt on pt.id = a.promotion_type_id WHERE a.enabled != 1 GROUP BY 1 ORDER BY pt.ordered desc", nativeQuery = true)
    List<AdView> getAllDisabled();

    @Query("select coalesce(max(id), 0) from Ad")
    Long findMaxId();
}
