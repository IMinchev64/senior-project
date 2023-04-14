package edu.aubg.ics.controller;

import edu.aubg.ics.dto.ImageData;
import edu.aubg.ics.model.ImageModel;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

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
                              Model model) throws NoSuchAlgorithmException {
        try {
            imageModel.processImage(url, noCache);
            String checksum = calculateChecksum(url);
            return String.format("redirect:/images/%s", checksum);
        } catch (PSQLException e) {
            e.printStackTrace();
            String checksum = calculateChecksum(url);
            return String.format("redirect:/images/%s", checksum);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("imageUrl", url);

            return "submission";
        }
    }

    @GetMapping("/images")
    public String getImageGallery(@RequestParam(value="pages", defaultValue = "1") int page, Model model) throws SQLException {
        int pageSize = 2;
        List<ImageData> images = imageModel.getImagesByPage(page, pageSize);
        int totalPages = imageModel.getTotalPages(pageSize);
        model.addAttribute("images", images);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "gallery";
    }

    @GetMapping("/images/{checksum}")
    public String getImageData(@PathVariable String checksum, Model model) {
        try {
            ImageData imageData = imageModel.getImageData(checksum);

            if (imageData == null) {
                model.addAttribute("error", "Image not found");
                return "gallery";
            }

            model.addAttribute("imageData", imageData);
            return "imageDetails";
        } catch (SQLException e) {
            model.addAttribute("error", e.getMessage());
            return "gallery";
        }
    }
}
