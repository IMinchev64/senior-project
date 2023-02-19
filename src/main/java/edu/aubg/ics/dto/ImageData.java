package edu.aubg.ics.dto;

import edu.aubg.ics.util.ChecksumCalculator;
import org.json.JSONArray;

import java.awt.*;
import java.security.NoSuchAlgorithmException;

import static edu.aubg.ics.util.ChecksumCalculator.calculateChecksum;

public class ImageData {
    private String url;
    private String checksum;
    private int width;
    private int height;
    private JSONArray tags;

    public ImageData(String url, JSONArray tags) throws NoSuchAlgorithmException {
        this.url = url;
        this.tags = tags;
        this.checksum = calculateChecksum(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public JSONArray getTags() {
        return tags;
    }

    public void setTags(JSONArray tags) {
        this.tags = tags;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
