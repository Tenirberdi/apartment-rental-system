package com.example.system.Utils;


import com.example.system.Repositories.PhotoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.example.system.EndPoints.URLs.*;

import java.util.List;

@Service
public class PhotoUtil {

    @Autowired
    private Environment env;
    @Autowired
    PhotoRepo photoRepo;

    private String getBaseUrl(String photoName){
        return ServletUriComponentsBuilder.fromCurrentRequest().replacePath(env.getProperty("server.servlet.context-path") + MEDIA_BASE_URL + PHOTOS + "/" + photoName).toUriString();
    }

    public List<String> getAdPhotoNames(long adId){
        return photoRepo.getAdPhotoNames(adId);
    }

    public List<String> getAdPhotoURLs(long adId){
        List<String> urls = getAdPhotoNames(adId);
        for(int i = 0; i<urls.size(); i++){
            urls.set(i, getBaseUrl(urls.get(i)));
        }
        return urls;
    }

    public String getUserPhotoURL(String photoName){
        String userPhotoURL = photoName;

        if(photoName != null){
            userPhotoURL = getBaseUrl(photoName);
        }

        return userPhotoURL;
    }
}
