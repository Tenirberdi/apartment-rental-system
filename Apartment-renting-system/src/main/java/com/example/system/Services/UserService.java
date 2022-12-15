package com.example.system.Services;


import com.example.system.DTOs.*;
import com.example.system.Entities.*;
import com.example.system.Repositories.*;
import com.example.system.Utils.Converter;
import com.example.system.Utils.PhotoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.sql.Date;
import java.util.*;

@Service
@Validated
@Transactional
public class UserService {

    private final ViewersRepo viewersRepo;
    private final PromotionExpirationRepo promotionExpirationRepo;
    private final UserRepo userRepo;
    private final AdRepo adRepo;
    private final RoleRepo roleRepo;
    private final HouseTypeRepo houseTypeRepo;
    private final PromotionTypeRepo promotionTypeRepo;
    private final PhotoRepo photoRepo;
    private final Converter converter;
    private final SavedListRepo savedListRepo;

    @Autowired
    private Environment env;
    @Autowired
    private FilesStorageServiceImpl filesStorageService;

    private final PhotoUtil photoUtil;
    @Autowired
    public UserService(ViewersRepo viewersRepo, PromotionExpirationRepo promotionExpirationRepo, UserRepo userRepo, AdRepo adRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo, HouseTypeRepo houseTypeRepo, PromotionTypeRepo promotionTypeRepo, PhotoRepo photoRepo, Converter converter, SavedListRepo savedListRepo, PhotoUtil photoUtil) {
        this.viewersRepo = viewersRepo;
        this.promotionExpirationRepo = promotionExpirationRepo;
        this.userRepo = userRepo;
        this.adRepo = adRepo;
        this.roleRepo = roleRepo;
        this.houseTypeRepo = houseTypeRepo;
        this.promotionTypeRepo = promotionTypeRepo;
        this.photoRepo = photoRepo;
        this.converter = converter;
        this.savedListRepo = savedListRepo;
        this.photoUtil = photoUtil;
    }

    //Users
    public List<UserDTO> getUsers(){
        List<UserDTO> users = new ArrayList<>();
        for (User user : userRepo.findAllByEnabled(true)) {
            UserDTO userDTO = UserDTO.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .contactInfo(user.getContactInfo())
                    .activeAds(adRepo.getActiveAdAmount(user.getId()))
                    .photoURL(photoUtil.getUserPhotoURL(user.getPhotoName())).build();
            users.add(userDTO);
        }
        return users;
    }

    public UserDTO getUserById(long id){
        if(userRepo.findById(id).isPresent()){
            User user = userRepo.findById(id).get();
            return UserDTO.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .activeAds(adRepo.getActiveAdAmount(id))
                    .contactInfo(user.getContactInfo())
                    .photoURL(photoUtil.getUserPhotoURL(user.getPhotoName()))
                    .build();
        }
        return null;

    }


    //Security
    public User findByUsername(String username){
        return userRepo.findByUsername(username);
    }


    public String getCurrentUserUsername(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername();
        }
        return principal.toString();
    }

    //Profile
    public UserDTO getProfile(){
        try {
        User currentUser = findByUsername(getCurrentUserUsername());
        return UserDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .activeAds(adRepo.getActiveAdAmount(currentUser.getId()))
                .photoURL(photoUtil.getUserPhotoURL(currentUser.getPhotoName()))
                .contactInfo(currentUser.getContactInfo())
                .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void editProfile(@Valid UserDTO user, Map< String, MultipartFile> files){
//      possible to change only name, photo, phone number (pass also, but it's done separately)
        try{
            User userEntity = findByUsername(getCurrentUserUsername());;

            userEntity.setFullName(user.getFullName());
            userEntity.setContactInfo(user.getContactInfo());

            if(userEntity.getPhotoName() != null){
                filesStorageService.delete(userEntity.getPhotoName());
                userEntity.setPhotoName(null);
            }

            if(files.values().size() > 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed to upload only one photo for profile");
            }
            files.values().forEach(file -> {
                String fileName = filesStorageService.save(file);
                userEntity.setPhotoName(fileName);
            });

            userRepo.save(userEntity);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during saving profile changes: " + e.getMessage());
        }
    }

    public void deleteProfile(){
        try{
            User currentUser = findByUsername(getCurrentUserUsername());
            if(currentUser.getRole().getRole().equals("ROLE_ADMIN")){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin's profile can't be deleted. Consider consulting with developer");
            }

            if(currentUser.getPhotoName() != null){
                filesStorageService.delete(currentUser.getPhotoName());
            }

            userRepo.delete(currentUser);
        }catch (ResponseStatusException e){
            throw new ResponseStatusException(e.getStatus(), e.getReason());
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while profile deleting process. The error: " + e.getMessage());
        }
    }


    //this feature is not implemented yet
    public void recoverProfile(){
        User currentUser = findByUsername(getCurrentUserUsername());
        currentUser.setEnabled(true);
        userRepo.save(currentUser);
    }

    // Ads
    public List<AdDTO> getUserAds(long userId){
        if(userRepo.findById(userId).isPresent()){
            return converter.convertAdViewsToAdDTOs(adRepo.getAds(userId));
        }
        return null;
    }

    public Long addAd(@Valid AdDTO adDTO, Map<String, MultipartFile> mapFiles){
        if(mapFiles.size() > 3){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed to upload max 3 photos of advertisement");
        }

        if(!houseTypeRepo.existsByType(adDTO.getHouseType())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No house type found");
        }

        Ad ad = Ad.builder()
                .title(adDTO.getTitle())
                .description(adDTO.getDescription()) //nullable
                .pricePerMonth(adDTO.getPricePerMonth())
                .totalRoomAmount(adDTO.getTotalRoomAmount())
                .bathRoomAmount(adDTO.getBathRoomAmount())
                .bedRoomAmount(adDTO.getBedRoomAmount())
                .kitchenRoomAmount(adDTO.getKitchenRoomAmount())
                .area(adDTO.getArea())
                .whichFloor(adDTO.getWhichFloor())
                .furniture(adDTO.getFurniture())//nullable
                .location(adDTO.getLocation())
                .houseType(houseTypeRepo.findByType(adDTO.getHouseType()))
                .renter(findByUsername(getCurrentUserUsername()))
                .build();
        Long id = adRepo.save(ad).getId();

        mapFiles.values().forEach(photo -> {
            photoRepo.save(Photo.builder().photoName(filesStorageService.save(photo)).ad(ad).build());
        });

        return id;

    }

    public Map<Object, Object> getAds(int currentPageNumber, int pageSize){
        List<AdDTO> ads = converter.convertAdViewsToAdDTOs(adRepo.getAds());

        PagedListHolder<AdDTO> pages = new PagedListHolder<>(ads);
        pages.setPage(currentPageNumber); //set current page number
        pages.setPageSize(pageSize); // set the size of page

        Map<Object, Object> response = new HashMap<>();

        response.put("metadata", PaginationMetaDataDTO.builder()
                .totalElementCount(pages.getNrOfElements())
                .pageSize(pages.getPageSize())
                .firstPage(pages.getFirstLinkedPage())
                .currentPage(pages.getPage())
                .lastPage(pages.getLastLinkedPage()).build());
        response.put("data", pages.getPageList());

        return response;
    }

    public AdDTO getAd(long id){
        if (adRepo.existsById(id)) {
            AdDTO ad = converter.convertAdViewToAdDTO(adRepo.getAdById(id));

            // add view
            User currentUser = userRepo.findByUsername(getCurrentUserUsername());
            if (viewersRepo.alreadyViewed(id ,currentUser.getId()) == 0) {
                Viewers view = Viewers.builder().ad(adRepo.findById(id).get()).viewer(currentUser).build();
                viewersRepo.save(view);
            }
            return ad;
        }
        return null;
    }

    public void editAd(@Valid AdDTO ad, Map<String, MultipartFile> mapFiles){
        User currentUser = findByUsername(getCurrentUserUsername());

        if(adRepo.existsById(ad.getId())){
            Ad adEntity = adRepo.findById(ad.getId()).get();
            if (adEntity.getRenter().getId() == currentUser.getId()) {
                if(houseTypeRepo.existsByType(ad.getHouseType())){

                    // Photos

                    if(mapFiles.size() > 3){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed to upload max 3 advertisement photos");
                    }

                    //delete all old photos
                    photoRepo.getAdPhotoNames(adEntity.getId()).forEach(name -> {
                        filesStorageService.delete(name);
                        photoRepo.deleteByPhotoName(name);
                    });

                    for(MultipartFile photo : mapFiles.values()) {
                        if (!photo.isEmpty()) {
                            // store all new photos instead
                            photoRepo.save(Photo.builder().photoName(filesStorageService.save(photo)).ad(adEntity).build());
                        }
                    }


                    // Ad details
                    adEntity.setTitle(ad.getTitle());
                    adEntity.setDescription(ad.getDescription());
                    adEntity.setPricePerMonth(ad.getPricePerMonth());
                    adEntity.setTotalRoomAmount(ad.getTotalRoomAmount());
                    adEntity.setBathRoomAmount(ad.getBathRoomAmount());
                    adEntity.setBedRoomAmount(ad.getBedRoomAmount());
                    adEntity.setKitchenRoomAmount(ad.getKitchenRoomAmount());
                    adEntity.setArea(ad.getArea());
                    adEntity.setWhichFloor(ad.getWhichFloor());
                    adEntity.setFurniture(ad.getFurniture());
                    adEntity.setLocation(ad.getLocation());
                    adEntity.setAvailable(ad.getAvailable());
                    adEntity.setHouseType(houseTypeRepo.findByType(ad.getHouseType()));

                    adRepo.save(adEntity);
                }else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House Type not found");
                }

            }else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only edit ad of yours");
            }

        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ad not found");
        }
    }

    public void deleteAd(long id){
        User currentUser = userRepo.findByUsername(getCurrentUserUsername());
        if(adRepo.existsById(id)){
            Ad ad = adRepo.findById(id).get();
            if(ad.getRenter().getId() == currentUser.getId()){
                photoRepo.getAdPhotoNames(ad.getId()).forEach(name -> {
                    filesStorageService.delete(name);
                });
                adRepo.delete(ad);
            } else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't delete ad of others");

            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no ad with id: " + id);

        }
    }

    //House Type
    public List<HouseTypeDTO> getHouseTypes(){
        List<HouseTypeDTO> types = new ArrayList<>();
        houseTypeRepo.findAll().forEach(type -> {
            types.add(HouseTypeDTO.builder()
                    .id(type.getId())
                    .type(type.getType()).build());
        });
        return types;
    }

    public HouseTypeDTO getHouseType(Long id){
        if (houseTypeRepo.findById(id).isPresent()) {
            HouseType type = houseTypeRepo.findById(id).get();
            return HouseTypeDTO.builder().id(type.getId()).type(type.getType()).build();
        }

        return null;

    }


    //promotions
    public List<PromotionDTO> getPromotions(){
        List<PromotionDTO> promotions = new ArrayList<>();
        promotionTypeRepo.findAll().forEach(promotion -> {
            promotions.add(PromotionDTO.builder()
                    .id(promotion.getId())
                    .name(promotion.getName())
                    .ordered(promotion.getOrdered())
                    .price(promotion.getPrice())
                    .build());});
        return promotions;
    }

    public PromotionDTO getPromotion(Long id){
        if(promotionTypeRepo.findById(id).isPresent()){
            PromotionType promotion = promotionTypeRepo.findById(id).get();
            return PromotionDTO.builder().id(promotion.getId()).ordered(promotion.getOrdered()).name(promotion.getName()).price(promotion.getPrice()).build();
        }

        return null;
    }

    @Scheduled(cron = "0 1 1 * * ?")
    @Transactional
    public void expirePromotion(){
        promotionExpirationRepo.takeOffPromotionType();
        promotionExpirationRepo.expire();
    }


   public void promote(long adId, long promotionTypeId){
        if(adRepo.findById(adId).isPresent() && promotionTypeRepo.findById(promotionTypeId).isPresent()){
            Ad ad = adRepo.findById(adId).get();
            if(Objects.equals(ad.getRenter().getId(), findByUsername(getCurrentUserUsername()).getId())){

                // Promoting
                ad.setPromotionType(promotionTypeRepo.findById(promotionTypeId).get());
                adRepo.save(ad);

                // recording expiration date
                int days = Integer.parseInt(Objects.requireNonNull(env.getProperty("promotion.expiration.in.days")));

                promotionExpirationRepo.save(PromotionExpiration.builder()
                        .expirationDate(Date.valueOf(new java.sql.Date(System.currentTimeMillis()).toLocalDate().plusDays(days)))
                        .ad(ad).build());
            }else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed to promote only own advertisements");
            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ad with id: " + adId + " or promotion type with id: " + promotionTypeId + " not found");
        }
   }

   //Saved ad list

    public ResponseEntity<?> triggerSavedList(Long adId){
        User user = findByUsername(getCurrentUserUsername());
        SavedList record = savedListRepo.findByAdIdAndRenteeId(adId, user.getId());

        if(adRepo.findById(adId).isPresent()){

            if(record == null){
                savedListRepo.save(SavedList.builder()
                        .ad(adRepo.findById(adId).get())
                        .rentee(user).build());
            }else {
                savedListRepo.delete(record);
            }
            return ResponseEntity.noContent().build();
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ad with id: " + adId + " not found");
        }

    }

    public List<SavedListDTO> getMySavedList(){
        User user = findByUsername(getCurrentUserUsername());
        List<SavedListDTO> list = new ArrayList<>();

        savedListRepo.findAllByRenteeId(user.getId()).forEach(savedList -> {
            Ad ad = savedList.getAd();

            list.add(SavedListDTO.builder()
                    .id(ad.getId())
                    .title(ad.getTitle())
                    .price(ad.getPricePerMonth())
                    .available(ad.isAvailable())
                    .dateOfPosting(ad.getDateOfPosting())
                    .location(ad.getLocation()).build());});
        return list;
    }

}


