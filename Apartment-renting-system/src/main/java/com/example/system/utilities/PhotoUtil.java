package com.example.system.utilities;


import com.example.system.repositories.PhotoRepo;
import com.example.system.services.FilesStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.system.utilities.UriBuilder.*;
import static com.example.system.endpoint.URLs.*;
import static com.sun.activation.registries.LogSupport.log;

import java.util.List;

@Service
public class PhotoUtil {
    @Autowired
    PhotoRepo photoRepo;
    @Autowired
    FilesStorageServiceImpl filesStorageService;


    //cleaning invalid photos
    @Scheduled(cron = "0 1 1 * * ?")
    @Transactional
    public void cleanInvalidPhotos(){
        System.out.println("cleaning invalid photos from system");
        filesStorageService.loadAll().forEach(f -> {
            if(photoRepo.countByPhotoName(f.getFileName().toString()) == 0){
                filesStorageService.delete(f.getFileName().toString());
            }
        });


    }

    public List<String> getAdPhotoNames(long adId){
        return photoRepo.getAdPhotoNames(adId);
    }

    public List<String> getAdPhotoURLs(long adId){
        List<String> urls = getAdPhotoNames(adId);
        for(int i = 0; i<urls.size(); i++){
            urls.set(i, buildUrl(MEDIA_BASE_URL + PHOTOS + "/" + urls.get(i)));
        }
        return urls;
    }

    public String getUserPhotoURL(String photoName){
        String userPhotoURL = photoName;

        if(photoName != null){
            userPhotoURL = buildUrl(MEDIA_BASE_URL + PHOTOS + "/" + photoName);
        }

        return userPhotoURL;
    }
}
