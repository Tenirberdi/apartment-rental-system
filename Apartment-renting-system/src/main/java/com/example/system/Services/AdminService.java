package com.example.system.Services;


import com.example.system.DTOs.HouseTypeDTO;
import com.example.system.DTOs.PromotionDTO;
import com.example.system.Entities.Ad;
import com.example.system.Entities.HouseType;
import com.example.system.Entities.PromotionType;
import com.example.system.Entities.User;
import com.example.system.Repositories.*;
import com.example.system.Utils.Converter;
import com.example.system.Utils.PhotoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Service
@Validated
public class AdminService extends UserService{
    private final ViewersRepo viewersRepo;
    private final PromotionExpirationRepo promotionExpirationRepo;
    private final UserRepo userRepo;
    private final AdRepo adRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;
    private final HouseTypeRepo houseTypeRepo;
    private final PromotionTypeRepo promotionTypeRepo;

    private final Converter converter;

    private final SavedListRepo savedListRepo;

    @Autowired
    public AdminService(PhotoRepo photoRepo, PhotoUtil photoUtil, UserRepo userRepo, AdRepo adRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo, HouseTypeRepo houseTypeRepo, PromotionTypeRepo promotionTypeRepo, ViewersRepo viewersRepo, PromotionExpirationRepo promotionExpirationRepo, UserRepo userRepo1, AdRepo adRepo1, PasswordEncoder passwordEncoder1, RoleRepo roleRepo1, HouseTypeRepo houseTypeRepo1, PromotionTypeRepo promotionTypeRepo1, Converter converter, SavedListRepo savedListRepo) {
        super(viewersRepo, promotionExpirationRepo, userRepo, adRepo, passwordEncoder, roleRepo, houseTypeRepo, promotionTypeRepo, photoRepo, converter, savedListRepo, photoUtil);
        this.viewersRepo = viewersRepo;
        this.promotionExpirationRepo = promotionExpirationRepo;
        this.userRepo = userRepo1;
        this.adRepo = adRepo1;
        this.passwordEncoder = passwordEncoder1;
        this.roleRepo = roleRepo1;
        this.houseTypeRepo = houseTypeRepo1;
        this.promotionTypeRepo = promotionTypeRepo1;
        this.converter = converter;
        this.savedListRepo = savedListRepo;
    }

    // Users
    public ResponseEntity<?> triggerUserBlocking(long id){
        if(userRepo.existsById(id)){
            User user = userRepo.findById(id).get();
            user.setEnabled(!user.isEnabled());
            userRepo.save(user);
            if(user.isEnabled()){
                return ResponseEntity.ok("User was unblocked");
            }else {
                return ResponseEntity.ok("User was blocked");
            }
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + id + " not found");
        }
    }

    // Ads

    public ResponseEntity<?> triggerAdBlocking(long id){
        if(adRepo.findById(id).isPresent()){
            Ad ad = adRepo.findById(id).get();
            ad.setEnabled(!ad.isEnabled());
            adRepo.save(ad);

            if(ad.isEnabled()){
                return ResponseEntity.ok("Advertisement was unblocked");
            }else {
                return ResponseEntity.ok("Advertisement was blocked");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ad with id: " + id + " not found");
        }
    }

    //House types
    public void editHouseType(@Valid HouseTypeDTO houseType){
        if(houseTypeRepo.findById(houseType.getId()).isPresent()){
            houseTypeRepo.save(HouseType.builder()
                    .id(houseType.getId())
                    .type(houseType.getType()).build());
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House type not found");
        }
    }

    public Long addHouseType(@Valid HouseTypeDTO type){
        return houseTypeRepo.save(HouseType.builder()
                .type(type.getType()).build()).getId();
    }

    public void deleteHouseType(long id){
        if(houseTypeRepo.findById(id).isPresent()){
            houseTypeRepo.deleteById(id);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House type with id: " + id + " not found");
        }
    }

    // Promotions

    public Long addPromotion(@Valid PromotionDTO promotion){
        return promotionTypeRepo.save(PromotionType.builder()
                .name(promotion.getName())
                .price(promotion.getPrice())
                .build()).getId();
    }

    public void editPromotion(@Valid PromotionDTO promotionDTO){
        if (promotionTypeRepo.findById(promotionDTO.getId()).isPresent()) {
            PromotionType promotionType = promotionTypeRepo.findById(promotionDTO.getId()).get();
            promotionType.setName(promotionDTO.getName());
            promotionType.setPrice(promotionDTO.getPrice());
            promotionTypeRepo.save(promotionType);
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion with id: " + promotionDTO.getId() + " not found");
        }
    }

    public void deletePromotion(long id){
        if(promotionTypeRepo.findById(id).isPresent()){
            if(!promotionTypeRepo.findById(id).get().getName().equals("DEFAULT")){
                promotionTypeRepo.deleteById(id);
            }else{
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete default promotion");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion with id: " + id + " not found");
        }
    }

    // Statistics

    // promotions
    public ResponseEntity<?> getPromotionStatistics(){
        return ResponseEntity.ok(promotionExpirationRepo.findAll());
    }

    // users
    public ResponseEntity<?> getUserStatistics(){
        return ResponseEntity.ok(userRepo.getUserStatistics());
    }

    // ads

    


}
