package com.example.system.Utils;

import com.example.system.DTOs.AdDTO;
import com.example.system.Projections.AdView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Converter {

    private final PhotoUtil photoUtil;

    @Autowired
    public Converter(PhotoUtil photoUtil) {
        this.photoUtil = photoUtil;
    }

    public List<AdDTO> convertAdViewsToAdDTOs(List<AdView> views){
        List<AdDTO> DTOs = new ArrayList<>();

        views.forEach(view -> {
            DTOs.add(convertAdViewToAdDTO(view));
        });

        return DTOs;
    }

    public AdDTO convertAdViewToAdDTO(AdView view){

        return AdDTO.builder()
                .id(view.getId())
                .title(view.getTitle())
                .description(view.getDescription())
                .photoURLs(photoUtil.getAdPhotoURLs(view.getId()))
                .pricePerMonth(view.getPrice_per_month())
                .totalRoomAmount(view.getTotal_room_amount())
                .bathRoomAmount(view.getBath_room_amount())
                .bedRoomAmount(view.getBed_room_amount())
                .kitchenRoomAmount(view.getKitchen_room_amount())
                .area(view.getArea())
                .whichFloor(view.getWhich_floor())
                .furniture(view.getFurniture())
                .location(view.getLocation())
                .dateOfPosting(view.getDate_of_posting())
                .available(view.getAvailable())
                .houseType(view.getHouse_type())
                .renterId(view.getRenter_id())
                .viewers(view.getViewers())
                .savedAmount(view.getSaved_amount())
                .promotionType(view.getPromotion_type()).build();
    }
}
