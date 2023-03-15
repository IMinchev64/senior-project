package edu.aubg.ics.model;

import edu.aubg.ics.api.Connector;
import edu.aubg.ics.dao.ImageDAO;
import edu.aubg.ics.dao.PostgresDAO;
import edu.aubg.ics.dto.ImageData;
import edu.aubg.ics.util.ImageDimensions;
import edu.aubg.ics.util.ResponseParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class ImageModel {

    @Autowired
    private PostgresDAO postgresDAO;

    @Autowired
    private ImageDAO imageDAO;

    private Connector imaggaConnector;

    private String jsonResponse;
    private JSONArray tags;
    private ResponseParser responseParser = new ResponseParser();

    ImageData imageData;

    public ImageModel(Connector imaggaConnector) {
        this.imaggaConnector = imaggaConnector;
    }

    public void insert(String imageURL) throws IOException, NoSuchAlgorithmException, SQLException {
        jsonResponse = imaggaConnector.connect(imageURL);
        tags = responseParser.jsonParser(jsonResponse);
        imageData = new ImageData(imageURL, tags);

        insertIntoDB();
    }

    private void insertIntoDB() throws SQLException {
        Connection connection = null;
        try {
            connection = postgresDAO.getConnection();
            imageDAO.setConnection(connection);

            imageDAO.insertImage(imageData);
            imageDAO.insertTags(imageData);
            imageDAO.insertImageToTags(imageData);
        } finally {
            postgresDAO.close();
            System.out.println("Operation successfull!");
        }
    }

    private void printInfo() {
        System.out.println(String.format("URL: %s,\nChecksum: %s,\nWidth: %s\nHeight: %s\nTags: %s",
                imageData.getUrl(),
                imageData.getChecksum(),
                imageData.getWidth(),
                imageData.getHeight(),
                imageData.getTags().toString(4)));
    }

}
