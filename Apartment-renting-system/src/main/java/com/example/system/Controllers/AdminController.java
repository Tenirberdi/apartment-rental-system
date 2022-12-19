package com.example.system.controllers;

import com.example.system.dtos.HouseTypeDTO;
import com.example.system.dtos.PromotionDTO;
import com.example.system.services.AdminService;
import com.example.system.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import static com.example.system.endpoint.URLs.*;

@RestController
@RequestMapping(path = ADMIN_BASE_URL, produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class AdminController {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final AdminService adminService;

    @Autowired
    public AdminController(UserService userService, ObjectMapper objectMapper, AdminService adminService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.adminService = adminService;
    }

    // Users
    @PutMapping(USERS + "/{id}")
    public ResponseEntity<?> triggerUserBlocking(@PathVariable("id") long id){
        return adminService.triggerUserBlocking(id);
    }

    // Ads
    @PutMapping(ADS + "/{id}")
    public ResponseEntity<?> triggerAdBlocking(@PathVariable("id") long id){
        return adminService.triggerAdBlocking(id);
    }

    // House Type
    @PutMapping(HOUSE_TYPES)
    public ResponseEntity<?> editHouseType(@RequestBody HouseTypeDTO houseType){
        adminService.editHouseType(houseType);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(HOUSE_TYPES)
    public ResponseEntity<?> addHouseType(@RequestBody HouseTypeDTO houseType){
        Long id = adminService.addHouseType(houseType);
        return ResponseEntity.created(URI.create("/user/house-types/" + id)).build();
    }

    @DeleteMapping(HOUSE_TYPES + "/{id}")
    public ResponseEntity<?> deleteHouseType(@PathVariable("id") long id){
        adminService.deleteHouseType(id);
        return ResponseEntity.noContent().build();
    }

    // Promotion
    @PostMapping(PROMOTIONS)
    public ResponseEntity<?> addPromotion(@RequestBody PromotionDTO promotion){
        Long id = adminService.addPromotion(promotion);
        return ResponseEntity.created(URI.create("/user/promotions/" + id)).build();
    }

    @PutMapping(PROMOTIONS)
    public ResponseEntity<?> editPromotion(@RequestBody PromotionDTO promotion){
        adminService.editPromotion(promotion);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(PROMOTIONS + "/{id}")
    public ResponseEntity<?> deletePromotions(@PathVariable("id") long id){
        adminService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

}
