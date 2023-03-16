package edu.aubg.ics.controller;

import edu.aubg.ics.model.ImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@RestController
public class ImageController {

    private final ImageModel imageModel;

    @Autowired
    public ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @GetMapping("/")
    public ResponseEntity<String> getImageTags(@RequestParam String imageURL, @RequestParam(required = false, defaultValue = "false") boolean noCache) throws NoSuchAlgorithmException, SQLException {
        imageModel.processImage(imageURL, noCache);
        return ResponseEntity.ok("Request submitted");
    }
}
