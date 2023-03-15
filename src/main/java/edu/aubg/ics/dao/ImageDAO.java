package edu.aubg.ics.dao;

import edu.aubg.ics.dto.ImageData;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ImageDAO {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void insertImage(ImageData imageData) throws SQLException{
        String sql = "INSERT INTO images (url, width, height, checksum) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, imageData.getUrl());
                preparedStatement.setInt(2, imageData.getWidth());
                preparedStatement.setInt(3, imageData.getHeight());
                preparedStatement.setString(4, imageData.getChecksum());
                preparedStatement.executeUpdate();
            }
    }

    public void insertTags(ImageData imageData) throws SQLException {
        String sql = "INSERT INTO tags (label) VALUES (?) ON CONFLICT DO NOTHING";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (Object tagObject : imageData.getTags()) {
                JSONObject tagValue = (JSONObject) tagObject;
                String tag = tagValue.getJSONObject("tag").getString("en");
                preparedStatement.setString(1, tag);
                preparedStatement.executeUpdate();
            }
        }
    }

    public void insertImageToTags(ImageData imageData) throws SQLException {
        int imageID;
        Map<String, Double> tagIDs = new HashMap<>();

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        final String getImageIDQuery = "SELECT id FROM images WHERE checksum = ?";
        final String getTagIDQuery = "SELECT id FROM tags WHERE label = ?";
        final String insertIDs = "INSERT INTO image_tags (image_id, tag_id, accuracy)\n" +
                "VALUES ((SELECT id FROM images WHERE checksum = ?), (SELECT id FROM tags WHERE label = ?), ?);";

        preparedStatement = connection.prepareStatement(getImageIDQuery);
        preparedStatement.setString(1, imageData.getChecksum());

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            imageID = resultSet.getInt("id");

            for (Object tagObject : imageData.getTags()) {
                JSONObject tag = (JSONObject) tagObject;

                preparedStatement = connection.prepareStatement(getTagIDQuery);
                preparedStatement.setString(1, tag.getJSONObject("tag").getString("en"));

                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    tagIDs.put(tag.getJSONObject("tag").getString("en"), tag.getDouble("confidence"));
                }
            }

            for (Map.Entry<String, Double> tag : tagIDs.entrySet()) {
                preparedStatement = connection.prepareStatement(insertIDs);

                preparedStatement.setString(1, imageData.getChecksum());
                preparedStatement.setString(2, tag.getKey());
                preparedStatement.setDouble(3, tag.getValue());

                preparedStatement.executeUpdate();
            }
        }


    }
}

