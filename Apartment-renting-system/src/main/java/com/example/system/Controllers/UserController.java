package com.example.system.Controllers;

import com.example.system.DTOs.AdDTO;
import com.example.system.DTOs.UserDTO;
import com.example.system.Services.UserService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static com.example.system.EndPoints.URLs.*;
@RestController
@RequestMapping(path = USER_BASE_URL, produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
        UserDTO profile = userService.getProfile();
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }
    @PutMapping(PROFILE)
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

    @PatchMapping(PROFILE)
    public ResponseEntity<?> recoverProfile(){
        userService.recoverProfile();
        return ResponseEntity.noContent().build();
    }

    //Users

    @GetMapping(value = USERS + "/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id){
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping(USERS)
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }


    //Ads

    @GetMapping(ADS)
    public ResponseEntity<?> getAds(@RequestParam(value = "page", required = false, defaultValue = "0") int page, @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        return new ResponseEntity<>(userService.getAds(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}" + ADS)
    public ResponseEntity<?> getUserAds(@PathVariable("id") long id){
        return ResponseEntity.ok().body(userService.getUserAds(id));
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

    @PutMapping(ADS)
    public ResponseEntity<?> editAd(@NotNull @RequestParam("data") String data, MultipartHttpServletRequest request) throws JsonProcessingException {
        AdDTO ad = objectMapper.readValue(data, AdDTO.class);
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
    public ResponseEntity<?> getHouseTypes(){
        return ResponseEntity.ok().body(userService.getHouseTypes());
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
    public ResponseEntity<?> getMySavedList(){
        return ResponseEntity.ok(userService.getMySavedList());
    }

    @PutMapping(SAVED_LIST + "/{adId}")
    public ResponseEntity<?> triggerSavedList(@PathVariable("adId") Long adId){
        return userService.triggerSavedList(adId);
    }



}
