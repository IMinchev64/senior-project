package edu.aubg.ics.controller;

import edu.aubg.ics.api.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class ImageController {
    @Autowired
    private Connector imaggaConnector;

    @PostMapping("/images")
    public ResponseEntity<String> getImageTags(@RequestBody String imageUrl) throws IOException {
        imaggaConnector.connect(imageUrl);
        return ResponseEntity.ok("Request submitted");
    }
}
