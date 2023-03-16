package edu.aubg.ics.model;

import edu.aubg.ics.api.Connector;
import edu.aubg.ics.dao.ImageDAO;
import edu.aubg.ics.dao.PostgresDAO;
import edu.aubg.ics.dto.ImageData;
import edu.aubg.ics.util.ResponseParser;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import static edu.aubg.ics.util.ChecksumCalculator.calculateChecksum;

@Service
public class ImageModel {

    @Autowired
    private PostgresDAO postgresDAO;

    @Autowired
    private ImageDAO imageDAO;

    private final Connector imaggaConnector;
    private final ResponseParser responseParser = new ResponseParser();
    private ImageData imageData;

    public ImageModel(Connector imaggaConnector) {
        this.imaggaConnector = imaggaConnector;
    }

    public void processImage(String imageURL, boolean noCache) throws NoSuchAlgorithmException, SQLException {

        String checksum = calculateChecksum(imageURL);
        Connection connection;

        connection = postgresDAO.newConnection();
        imageDAO.setConnection(connection);

        if(!noCache && checkExisting(checksum)) {
            System.out.println("Image already exists");
            return;
        }

        try {

            String jsonResponse = imaggaConnector.connect(imageURL);
            JSONArray tags = responseParser.jsonParser(jsonResponse);
            imageData = new ImageData(imageURL, tags);

            if (!noCache) {
                insertImage();
            }
            else {
                updateImage();
            }
            System.out.println("Image inserted");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to Imagga API");
        } finally {
            postgresDAO.close();
            System.out.println("Operation successfull!");
        }

    }

    public ImageData getImageData(String checksum) throws SQLException {
        Connection connection;
        try {
            connection = postgresDAO.newConnection();
            imageDAO.setConnection(connection);

            imageData = fetchImage(checksum);
        } finally {
            postgresDAO.close();
        }

        return imageData;
    }

    public List getAllImages() throws SQLException {
        Connection connection;

        try {
            connection = postgresDAO.newConnection();
            imageDAO.setConnection(connection);

            List<ImageData> images = imageDAO.getAllImages();
            return images;
        } finally {
            postgresDAO.close();
        }
    }

    private void insertImage() throws SQLException {
        imageDAO.insertToDatabase(imageData);
    }

    private void printInfo() {
        System.out.println(String.format("URL: %s,\nChecksum: %s,\nWidth: %s\nHeight: %s\nTags: %s",
                imageData.getUrl(),
                imageData.getChecksum(),
                imageData.getWidth(),
                imageData.getHeight(),
                imageData.getTags().toString(4)));
    }

    private boolean checkExisting(String checksum) {
        return imageDAO.imageExists(checksum);
    }

    private ImageData fetchImage(String checksum) throws SQLException {
        return imageDAO.fetchImage(checksum);
    }

    private void updateImage() throws SQLException {
        imageDAO.updateImageData(imageData);
    }
}
