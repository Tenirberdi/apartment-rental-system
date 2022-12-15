package com.example.system.Controllers;

import com.example.system.Services.FilesStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "/media", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MediaController {

    private final FilesStorageServiceImpl filesStorageService;

    @Autowired
    public MediaController(FilesStorageServiceImpl filesStorageService) {
        this.filesStorageService = filesStorageService;
    }

    @GetMapping(value = "/photos/{name}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<?> getPhoto(@PathVariable("name") String photoName){
        return new ResponseEntity<>(filesStorageService.load(photoName), HttpStatus.OK);
    }
}
