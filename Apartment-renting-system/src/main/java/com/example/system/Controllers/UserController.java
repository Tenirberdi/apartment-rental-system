package com.example.system.controllers;

import com.example.system.dtos.AdDTO;
import com.example.system.dtos.UserDTO;
import com.example.system.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static com.example.system.endpoint.URLs.*;
@RestController
@RequestMapping(path = USER_BASE_URL, produces = "application/json")
@Validated
public class UserController {
    private final UserService  userService;
    private final ObjectMapper objectMapper;
    private final Environment env;
    @Autowired
    public UserController(UserService userService, ObjectMapper objectMapper, Environment env) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.env = env;
    }

    //Profile

    @GetMapping(value = PROFILE)
    public ResponseEntity<?> getProfile() {
        return new ResponseEntity<>(userService.getProfile(), HttpStatus.OK);
    }

    @PatchMapping(PROFILE)
    public ResponseEntity<?> editProfile(@NotEmpty @RequestParam("data") String data, MultipartHttpServletRequest request) throws JsonProcessingException {
        UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
        Map< String, MultipartFile> photos = request.getFileMap();
        userService.editProfile(userDTO, photos);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping(PROFILE)
    public ResponseEntity<?> deleteProfile(){
        userService.deleteProfile();
        return ResponseEntity.noContent().build();
    }
//
//    @PatchMapping(PROFILE)
//    public ResponseEntity<?> recoverProfile(){
//        userService.recoverProfile();
//        return ResponseEntity.noContent().build();
//    }

    //Users

    @GetMapping(value = USERS + "/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id){
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping(USERS)
    public ResponseEntity<?> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam(value = "search", defaultValue = "") String search){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(page, size, search));
    }


    //Ads

    @GetMapping(ADS)
    public ResponseEntity<?> getAds(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam(value = "search", defaultValue = "") String search){
        return new ResponseEntity<>(userService.getAds(page, size, search), HttpStatus.OK);
    }

    @GetMapping("/{id}" + ADS)
    public ResponseEntity<?> getUserAds(@PathVariable("id") long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "") String search){
        return ResponseEntity.ok().body(userService.getUserAds(id, page, size, search));
    }

    @PostMapping(ADS)
    public ResponseEntity<?> addAd(@NotEmpty @RequestParam("data") String data, MultipartHttpServletRequest request) throws IOException {
        AdDTO ad = objectMapper.readValue(data, AdDTO.class);
        Map< String, MultipartFile> photos;
        photos = request.getFileMap();
        Long id = userService.addAd(ad, photos);
        return ResponseEntity.created(URI.create(String.valueOf(id))).build();
    }

    @PostMapping(ADS + "/{adId}" + PROMOTION +  "/{promotionId}")
    public ResponseEntity<?> promote(@PathVariable("adId") long adId, @PathVariable("promotionId") long promotionId){
        userService.promote(adId, promotionId);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping(ADS + "/{id}")
    public ResponseEntity<?> editAd(@PathVariable("id") long id,@NotNull @RequestParam("data") String data, MultipartHttpServletRequest request) throws JsonProcessingException {
        AdDTO ad = objectMapper.readValue(data, AdDTO.class);
        ad.setId(id);
        Map< String, MultipartFile> photos;
        photos = request.getFileMap();
        userService.editAd(ad, photos);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ADS + "/{id}")
    public ResponseEntity<?> getAd(@PathVariable("id") long id){
        return new ResponseEntity<>(userService.getAd(id), HttpStatus.OK);
    }

    @DeleteMapping(ADS + "/{id}")
    public ResponseEntity<?> deleteAd(@PathVariable("id") long id) {
        userService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    // House type

    @GetMapping(HOUSE_TYPES)
    public ResponseEntity<?> getHouseTypes(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam( value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok().body(userService.getHouseTypes(page, size));
    }

    @GetMapping( HOUSE_TYPES + "/{id}")
    public ResponseEntity<?> getHouseTypeById(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(userService.getHouseType(id));
    }

    // Promotions

    @GetMapping(PROMOTIONS)
    public ResponseEntity<?> getPromotions(){
        return ResponseEntity.ok().body(userService.getPromotions());
    }

    @GetMapping( PROMOTIONS + "/{id}")
    public ResponseEntity<?> getPromotion(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(userService.getPromotion(id));
    }


    // Saved List

    @GetMapping(SAVED_LIST)
    public ResponseEntity<?> getMySavedList(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam( value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(userService.getMySavedList(page, size));
    }

    @PutMapping(SAVED_LIST + "/{adId}")
    public ResponseEntity<?> triggerSavedList(@PathVariable("adId") Long adId){
        userService.triggerSavedList(adId);
        return ResponseEntity.noContent().build();
    }



}
