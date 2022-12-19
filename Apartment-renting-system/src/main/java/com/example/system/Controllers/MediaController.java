package com.example.system.controllers;

import com.example.system.services.FilesStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
    public ResponseEntity<?> getPhoto(@PathVariable("name") String photoName) {
        Resource file = filesStorageService.load(photoName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.parse(Objects.requireNonNull(file.getFilename())));
        return new ResponseEntity<>(filesStorageService.load(photoName), headers, HttpStatus.OK);
    }
}
