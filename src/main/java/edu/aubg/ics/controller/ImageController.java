package edu.aubg.ics.controller;

import edu.aubg.ics.dto.ImageData;
import edu.aubg.ics.model.ImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static edu.aubg.ics.util.ChecksumCalculator.calculateChecksum;

@RestController
public class ImageController {

    private final ImageModel imageModel;

    @Autowired
    public ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @GetMapping("/")
    public RedirectView getImageTags(@RequestParam String imageURL, @RequestParam(required = false, defaultValue = "false") boolean noCache) throws NoSuchAlgorithmException, SQLException {
        imageModel.processImage(imageURL, noCache);
        String checksum = calculateChecksum(imageURL);
        return new RedirectView("/images/" + checksum);
    }

    @GetMapping("/images")
    public ResponseEntity<List<ImageData>> getAllImages() {
        try {
            List<ImageData> images = imageModel.getAllImages();
            return ResponseEntity.ok(images);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/{checksum}")
    public ResponseEntity<ImageData> getImageData(@PathVariable String checksum) {
        try {
            ImageData imageData = imageModel.getImageData(checksum);
            return ResponseEntity.ok(imageData);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
