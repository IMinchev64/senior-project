package edu.aubg.ics.controller;

import edu.aubg.ics.api.Connector;
import edu.aubg.ics.dto.ImageData;
import edu.aubg.ics.util.ImageDimensions;
import edu.aubg.ics.util.ResponseParser;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class ImageController {
    @Autowired
    private Connector imaggaConnector;

    @GetMapping("/")
    public ResponseEntity<String> getImageTags(@RequestParam String imageUrl) throws IOException, NoSuchAlgorithmException {
        String jsonResponse;
        JSONArray tags;
        ResponseParser responseParser = new ResponseParser();
        jsonResponse = imaggaConnector.connect(imageUrl);
        tags = responseParser.jsonParser(jsonResponse);
        ImageData imageData = new ImageData(imageUrl, tags);
        ImageDimensions imageDimensions = new ImageDimensions(imageUrl);
        System.out.println(String.format("URL: %s,\nChecksum: %s,\nWidth: %s\nHeight: %s\nTags: %s",
                imageData.getUrl(),
                imageData.getChecksum(),
                imageDimensions.getWidth(),
                imageDimensions.getHeight(),
                imageData.getTags().toString(4)));
        return ResponseEntity.ok("Request submitted");
    }
}
