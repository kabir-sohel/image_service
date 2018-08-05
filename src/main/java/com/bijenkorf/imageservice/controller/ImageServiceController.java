package com.bijenkorf.imageservice.controller;

import com.bijenkorf.imageservice.service.ImageService;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageServiceController {

    @Autowired
    private final ImageService imageService;

    public ImageServiceController(ImageService imageService){
        this.imageService = imageService;
    }


    @GetMapping("/image/show/{typeName}/{seoName}/")
    public ResponseEntity getImage(@PathVariable("typeName") String typeName, @PathVariable("seoName") String seoName, @RequestParam("reference") String reference){
        System.out.println(" type name = " + typeName + " seo name " + seoName + " reference = " + reference);

        InputStream inputStream = imageService.getImageAsInputStream(typeName, seoName, reference);
        if(null  == inputStream){
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Image Not Found");
        }


        MediaType mediaType = MediaType.IMAGE_JPEG;

        return ResponseEntity
            .ok()
            .contentType(mediaType)
            .body(new InputStreamResource(inputStream));
    }
}
