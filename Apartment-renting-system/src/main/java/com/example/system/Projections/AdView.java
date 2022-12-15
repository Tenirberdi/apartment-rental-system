package com.example.system.Projections;

import java.sql.Date;

public interface AdView {
    Long getId();
    String getTitle();
    String getDescription();

    Integer getPrice_per_month();
    Integer getTotal_room_amount();
    Integer getBath_room_amount();
    Integer getBed_room_amount();
    Integer getKitchen_room_amount();
    Integer getArea();
    Integer getWhich_floor();
    String getFurniture();
    String getLocation();
    Date getDate_of_posting();
    Boolean getAvailable();
    String getHouse_type();
    Long getRenter_id();
    Integer getViewers();
    Integer getSaved_amount();
    String getPromotion_type();
}
