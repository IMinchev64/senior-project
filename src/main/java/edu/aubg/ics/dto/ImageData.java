package edu.aubg.ics.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.aubg.ics.aop.PerformanceModule;
import edu.aubg.ics.knn.ImageAnalyzer;
import edu.aubg.ics.util.ChecksumCalculator;
import edu.aubg.ics.util.ImageDimensions;
import org.json.JSONArray;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static edu.aubg.ics.util.ChecksumCalculator.calculateChecksum;

public class ImageData {
    @JsonProperty("image_url")
    private String url;
    @JsonIgnore
    private String checksum;
    @JsonProperty("width")
    private int width;
    @JsonProperty("height")
    private int height;
    @JsonIgnore
    private JSONArray tags;
    @JsonProperty("uploaded_at")
    private String uploadedAt;
    @JsonProperty("tags")
    private Map<String, Double> tagMap;
    private String labelKNN;

    public ImageData() {}

    public ImageData(String url, JSONArray tags) throws NoSuchAlgorithmException, IOException, SQLException {
        this.url = url;
        this.tags = tags;
        this.checksum = calculateChecksum(url);

        Injector injector = Guice.createInjector(new PerformanceModule());
        ImageAnalyzer imageAnalyzer = injector.getInstance(ImageAnalyzer.class);
        this.labelKNN = imageAnalyzer.analyzeImage(url);

        setDimensions();
    }

    public ImageData(String checksum, String url, String uploadedAt, int width, int height, Map<String, Double> tagMap, String labelKNN) {
        this.checksum = checksum;
        this.url = url;
        this.width = width;
        this.height = height;
        this.tagMap = tagMap;
        this.labelKNN = labelKNN;
        setUploadedAt(uploadedAt);
    }

    public String getUrl() {
        return url;
    }

    public String getChecksum() {
        return checksum;
    }

    public JSONArray getTags() {
        return tags;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getUploadedAt(){
        return uploadedAt;
    }

    public Map getTagMap(){
        return tagMap;
    }

    public String getLabelKNN() {
        return labelKNN;
    }

    private void setDimensions() throws IOException {
        ImageDimensions imageDimensions = new ImageDimensions(this.url);
        this.width = imageDimensions.getWidth();
        this.height = imageDimensions.getHeight();
    }

    private void setUploadedAt(String uploadedAt) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n");
        LocalDateTime parsedDate = LocalDateTime.parse(uploadedAt, inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.uploadedAt = parsedDate.format(outputFormatter);
    }
}
