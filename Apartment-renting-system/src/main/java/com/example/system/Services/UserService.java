package com.example.system.services;


import com.example.system.dtos.*;
import com.example.system.models.*;
import com.example.system.repositories.*;
import com.example.system.utilities.Converter;
import com.example.system.utilities.PaginationUtility;
import com.example.system.utilities.PhotoUtil;
import com.example.system.utilities.ResponseMapper;
import static com.example.system.utilities.UriBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
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
import static com.example.system.endpoint.URLs.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ResponseMapper responseMapper;


    @Autowired
    private Environment env;
    @Autowired
    private FilesStorageServiceImpl filesStorageService;
    @Autowired
    private PaginationUtility paginationUtility;

    private final PhotoUtil photoUtil;
    @Autowired
    public UserService(ViewersRepo viewersRepo, PromotionExpirationRepo promotionExpirationRepo, UserRepo userRepo, AdRepo adRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo, HouseTypeRepo houseTypeRepo, PromotionTypeRepo promotionTypeRepo, PhotoRepo photoRepo, Converter converter, SavedListRepo savedListRepo, ResponseMapper responseMapper, PhotoUtil photoUtil) {
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
        this.responseMapper = responseMapper;
        this.photoUtil = photoUtil;
    }

    //Users
    public Map<String, ?> getUsers(int pageNumber, int pageSize, String search){
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

        users = users.stream().filter(a ->
                a.getFullName().matches("(?i).*" + search + ".*") || a.getContactInfo().matches("(?i).*" + search + ".*")).collect(Collectors.toList());

        PagedListHolder<?> pages = paginationUtility.getPages(users, pageNumber, pageSize);


        return responseMapper.mapResponse(users, PaginationMetaDataDTO.builder()
                .totalRecords(pages.getNrOfElements())
                .totalPages(pages.getPageCount()).build(), PaginationLinksDTO.builder()
                .firstPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + USERS + "?page=" + pages.getFirstLinkedPage() + "&size=" + pages.getPageSize()))
                .lastPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + USERS + "?page=" + pages.getLastLinkedPage() + "&size=" + pages.getPageSize()))
                .nextPage((pages.isLastPage() ? null: buildUrl(USER_BASE_URL + USERS + "?page=" +  (pages.getPage() + 1) + "&size=" + pages.getPageSize())))
                .previousPage(pages.isFirstPage()? null : buildUrl(USER_BASE_URL + USERS + "?page=" + (pages.getPage() - 1) + "&size=" + pages.getPageSize())).build());
    }

    public Map<String, ?> getUserById(long id){
        if(userRepo.findById(id).isPresent()){
            User user = userRepo.findById(id).get();
            return responseMapper.mapResponse(UserDTO.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .activeAds(adRepo.getActiveAdAmount(id))
                    .contactInfo(user.getContactInfo())
                    .photoURL(photoUtil.getUserPhotoURL(user.getPhotoName()))
                    .build());
        }
        return null;

    }


    //security
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
    public Map<String,?> getProfile(){
        try {
        User currentUser = findByUsername(getCurrentUserUsername());
        UserDTO profile = UserDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .activeAds(adRepo.getActiveAdAmount(currentUser.getId()))
                .photoURL(photoUtil.getUserPhotoURL(currentUser.getPhotoName()))
                .contactInfo(currentUser.getContactInfo())
                .build();

        return responseMapper.mapResponse(profile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void editProfile(UserDTO user, Map< String, MultipartFile> files){
//      possible to change only name, photo, phone number (pass also, but it's done separately)
        try{
            User userEntity = findByUsername(getCurrentUserUsername());
            if(user.getFullName() != null && !user.getFullName().isEmpty()){
                userEntity.setFullName(user.getFullName());
            }
            if(user.getContactInfo() != null && !user.getContactInfo().isEmpty()){
                userEntity.setContactInfo(user.getContactInfo());
            }
            if(userEntity.getPhotoName() != null ){
                filesStorageService.delete(userEntity.getPhotoName());
                userEntity.setPhotoName(null);
            }

            if(files.values().size() > 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed to upload only one photo for profile");
            }
            files.values().forEach(file -> {
                if(!file.isEmpty()){
                    String fileName = filesStorageService.save(file);
                    userEntity.setPhotoName(fileName);
                }
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


//    //this feature is not implemented yet
//    public void recoverProfile(){
//        User currentUser = findByUsername(getCurrentUserUsername());
//        currentUser.setEnabled(true);
//        userRepo.save(currentUser);
//    }

    // Ads
    public Map<String, ?> getUserAds(long userId, int page, int size, String search){
        if(userRepo.findById(userId).isPresent()){
            List<AdDTO> ads = converter.convertAdViewsToAdDTOs(adRepo.getAds(userId)).stream().filter(a ->
                    a.getTitle().matches("(?i).*" + search + ".*") || a.getDescription().matches("(?i).*" + search + ".*") || a.getFurniture().matches("(?i).*" + search + ".*") || a.getHouseType().matches("(?i).*" + search + ".*") || a.getLocation().matches("(?i).*" + search + ".*")).collect(Collectors.toList());
            ;

            PagedListHolder<?> pages = paginationUtility.getPages(ads, page, size);

            return responseMapper.mapResponse(pages.getPageList(),PaginationMetaDataDTO.builder()
                    .totalRecords(pages.getNrOfElements())
                    .totalPages(pages.getPageCount()).build(), PaginationLinksDTO.builder()
                    .firstPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + ADS + "?page=" + pages.getFirstLinkedPage() + "&size=" + pages.getPageSize()))
                    .lastPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + ADS + "?page=" + pages.getLastLinkedPage() + "&size=" + pages.getPageSize()))
                    .nextPage((pages.isLastPage() ? null: buildUrl(USER_BASE_URL + ADS + "?page=" +  (pages.getPage() + 1) + "&size=" + pages.getPageSize())))
                    .previousPage(pages.isFirstPage()? null : buildUrl(USER_BASE_URL + ADS + "?page=" + (pages.getPage() - 1) + "&size=" + pages.getPageSize())).build());

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

    public Map<String, ?> getAds(int page, int size, String search){
        List<AdDTO> ads = converter.convertAdViewsToAdDTOs(adRepo.getAds()).stream().filter(a ->
                a.getTitle().matches("(?i).*" + search + ".*") || a.getDescription().matches("(?i).*" + search + ".*") || a.getFurniture().matches("(?i).*" + search + ".*") || a.getHouseType().matches("(?i).*" + search + ".*") || a.getLocation().matches("(?i).*" + search + ".*") || a.getPricePerMonth().toString().matches("(?i).*" + search + ".*")).collect(Collectors.toList());

        PagedListHolder<?> pages = paginationUtility.getPages(ads, page, size);

        return responseMapper.mapResponse(pages.getPageList(),PaginationMetaDataDTO.builder()
                .totalRecords(pages.getNrOfElements())
                .totalPages(pages.getPageCount()).build(), PaginationLinksDTO.builder()
                .firstPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + ADS + "?page=" + pages.getFirstLinkedPage() + "&size=" + pages.getPageSize()))
                .lastPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + ADS + "?page=" + pages.getLastLinkedPage() + "&size=" + pages.getPageSize()))
                .nextPage((pages.isLastPage() ? null: buildUrl(USER_BASE_URL + ADS + "?page=" +  (pages.getPage() + 1) + "&size=" + pages.getPageSize())))
                .previousPage(pages.isFirstPage()? null : buildUrl(USER_BASE_URL + ADS + "?page=" + (pages.getPage() - 1) + "&size=" + pages.getPageSize())).build());
    }

    public Map<String, ?> getAd(long id){
        if (adRepo.existsById(id)) {
            AdDTO ad = converter.convertAdViewToAdDTO(adRepo.getAdById(id));

            // add view
            User currentUser = userRepo.findByUsername(getCurrentUserUsername());
            if (viewersRepo.alreadyViewed(id ,currentUser.getId()) == 0) {
                Viewers view = Viewers.builder().ad(adRepo.findById(id).get()).viewer(currentUser).build();
                viewersRepo.save(view);
            }
            return responseMapper.mapResponse(ad);
        }
        return null;
    }

    public void editAd( AdDTO ad, Map<String, MultipartFile> mapFiles){
        User currentUser = findByUsername(getCurrentUserUsername());

        if(adRepo.existsById(ad.getId())){
            Ad adEntity = adRepo.findById(ad.getId()).get();
            if (adEntity.getRenter().getId() == currentUser.getId()) {

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
                    if(ad.getTitle() != null && !ad.getTitle().isEmpty()){
                        adEntity.setTitle(ad.getTitle());
                    } if(ad.getDescription() != null && !ad.getDescription().isEmpty()){
                        adEntity.setDescription(ad.getDescription());
                    } if(ad.getPricePerMonth() != null && ad.getPricePerMonth() >= 0){
                        adEntity.setPricePerMonth(ad.getPricePerMonth());
                    } if(ad.getTotalRoomAmount() != null && ad.getTotalRoomAmount() > 0){
                        adEntity.setTotalRoomAmount(ad.getTotalRoomAmount());
                    } if(ad.getBathRoomAmount() != null && ad.getBedRoomAmount() >= 0){
                        adEntity.setBathRoomAmount(ad.getBathRoomAmount());
                    }if(ad.getBedRoomAmount() != null && ad.getBedRoomAmount() >= 0){
                        adEntity.setBedRoomAmount(ad.getBedRoomAmount());
                    } if(ad.getKitchenRoomAmount() != null && ad.getKitchenRoomAmount() >= 0){
                        adEntity.setKitchenRoomAmount(ad.getKitchenRoomAmount());
                    } if(ad.getArea() != null && ad.getArea() > 0){
                        adEntity.setArea(ad.getArea());
                    } if(ad.getWhichFloor() != null && ad.getWhichFloor() > 0){
                        adEntity.setWhichFloor(ad.getWhichFloor());
                    } if(ad.getFurniture() != null && !ad.getFurniture().isEmpty()){
                        adEntity.setFurniture(ad.getFurniture());
                    } if(ad.getLocation() != null && !ad.getLocation().isEmpty()){
                        adEntity.setLocation(ad.getLocation());
                    } if(ad.getAvailable() != null){
                        adEntity.setAvailable(ad.getAvailable());
                    } if(ad.getHouseType() != null && !ad.getHouseType().isEmpty()){
                        if(houseTypeRepo.findByType(ad.getHouseType()) == null){
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House Type not found");
                        }
                        adEntity.setHouseType(houseTypeRepo.findByType(ad.getHouseType()));
                    }


                    adRepo.save(adEntity);


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
    public Map<String, ?> getHouseTypes(int page, int size){
        List<HouseTypeDTO> types = new ArrayList<>();
        houseTypeRepo.findAll().forEach(type -> {
            types.add(HouseTypeDTO.builder()
                    .id(type.getId())
                    .type(type.getType()).build());
        });

        PagedListHolder<?> pages = paginationUtility.getPages(types, page, size);

        return responseMapper.mapResponse(pages.getPageList(),PaginationMetaDataDTO.builder()
                .totalRecords(pages.getNrOfElements())
                .totalPages(pages.getPageCount()).build(), PaginationLinksDTO.builder()
                .firstPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + HOUSE_TYPES + "?page=" + pages.getFirstLinkedPage() + "&size=" + pages.getPageSize()))
                .lastPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + HOUSE_TYPES + "?page=" + pages.getLastLinkedPage() + "&size=" + pages.getPageSize()))
                .nextPage((pages.isLastPage() ? null: buildUrl(USER_BASE_URL + HOUSE_TYPES + "?page=" +  (pages.getPage() + 1) + "&size=" + pages.getPageSize())))
                .previousPage(pages.isFirstPage()? null : buildUrl(USER_BASE_URL + HOUSE_TYPES + "?page=" + (pages.getPage() - 1) + "&size=" + pages.getPageSize())).build());
    }

    public Map<String, ?> getHouseType(Long id){
        if (houseTypeRepo.findById(id).isPresent()) {
            HouseType type = houseTypeRepo.findById(id).get();
            return responseMapper.mapResponse(HouseTypeDTO.builder().id(type.getId()).type(type.getType()).build());
        }

        return null;

    }


    //promotions
    public Map<String, ?> getPromotions(){
        List<PromotionDTO> promotions = new ArrayList<>();
        promotionTypeRepo.findAll().forEach(promotion -> {
            promotions.add(PromotionDTO.builder()
                    .id(promotion.getId())
                    .name(promotion.getName())
                    .ordered(promotion.getOrdered())
                    .price(promotion.getPrice())
                    .build());});
        return responseMapper.mapResponse(promotions);
    }

    public Map<String, ?> getPromotion(Long id){
        if(promotionTypeRepo.findById(id).isPresent()){
            PromotionType promotion = promotionTypeRepo.findById(id).get();
            return responseMapper.mapResponse(PromotionDTO.builder().id(promotion.getId()).ordered(promotion.getOrdered()).name(promotion.getName()).price(promotion.getPrice()).build());
        }

        return null;
    }

    @Scheduled(cron = "0 1 1 * * ?")
    @Transactional
    public void expirePromotion(){
        System.out.println("Expiring promotions");
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

    public void triggerSavedList(Long adId){
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
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ad with id: " + adId + " not found");
        }

    }

    public Map<String, ?> getMySavedList(int page, int size){
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

        PagedListHolder<?> pages = paginationUtility.getPages(list, page, size);

        return responseMapper.mapResponse(pages.getPageList(),PaginationMetaDataDTO.builder()
                .totalRecords(pages.getNrOfElements())
                .totalPages(pages.getPageCount()).build(), PaginationLinksDTO.builder()
                .firstPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + SAVED_LIST + "?page=" + pages.getFirstLinkedPage() + "&size=" + pages.getPageSize()))
                .lastPage(pages.getPageCount() < 2 ? null : buildUrl(USER_BASE_URL + SAVED_LIST + "?page=" + pages.getLastLinkedPage() + "&size=" + pages.getPageSize()))
                .nextPage((pages.isLastPage() ? null: buildUrl(USER_BASE_URL + SAVED_LIST + "?page=" +  (pages.getPage() + 1) + "&size=" + pages.getPageSize())))
                .previousPage(pages.isFirstPage()? null : buildUrl(USER_BASE_URL + SAVED_LIST + "?page=" + (pages.getPage() - 1) + "&size=" + pages.getPageSize())).build());

    }

}


