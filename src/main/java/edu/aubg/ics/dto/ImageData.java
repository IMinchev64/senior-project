package edu.aubg.ics.dto;

import edu.aubg.ics.util.ChecksumCalculator;
import edu.aubg.ics.util.ImageDimensions;
import org.json.JSONArray;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static edu.aubg.ics.util.ChecksumCalculator.calculateChecksum;

public class ImageData {
    private String url;
    private String checksum;
    private int width;
    private int height;
    private JSONArray tags;

    public ImageData(String url, JSONArray tags) throws NoSuchAlgorithmException, IOException {
        this.url = url;
        this.tags = tags;
        this.checksum = calculateChecksum(url);

        setDimensions();
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

    private void setDimensions() throws IOException {
        ImageDimensions imageDimensions = new ImageDimensions(this.url);
        this.width = imageDimensions.getWidth();
        this.height = imageDimensions.getHeight();
    }
}
