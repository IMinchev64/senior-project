package edu.aubg.ics.controller;

import edu.aubg.ics.dto.ImageData;
import edu.aubg.ics.model.ImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static edu.aubg.ics.util.ChecksumCalculator.calculateChecksum;

@Controller
public class ImageController {

    private final ImageModel imageModel;

    @Autowired
    public ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @GetMapping({"/"})
    public String getHomePage(Model model) {
        model.addAttribute("imageData", new ImageData());
        return "submission";
    }

    @PostMapping("/")
    public String submitImage(@RequestParam("url") String url,
                              @RequestParam(required = false, defaultValue = "false") boolean noCache,
                              Model model) {
        try {
            imageModel.processImage(url, noCache);
            String checksum = calculateChecksum(url);
            return "redirect:/images/" + checksum;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("imageUrl", url);
            return "submission";
        }
    }

    @GetMapping("/images")
    public String getImageGallery(Model model) throws SQLException {
        List<ImageData> images = imageModel.getAllImages();
        model.addAttribute("images", images);
        return "gallery";
    }

    @GetMapping("/images/{checksum}")
    public String getImageData(@PathVariable String checksum, Model model) {
        try {
            ImageData imageData = imageModel.getImageData(checksum);
            model.addAttribute("imageData", imageData);
            return "imageDetails";
        } catch (SQLException e) {
            model.addAttribute("error", e.getMessage());
            return "gallery";
        }
    }
}
