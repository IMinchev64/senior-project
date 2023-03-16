package edu.aubg.ics.dao;

import edu.aubg.ics.dto.ImageData;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageDAO {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void insertToDatabase(ImageData imageData) throws SQLException {
        insertImage(imageData);
        insertTags(imageData);
        insertImageToTags(imageData);
    }

    public boolean imageExists(String checksum) {
        try {
            String checkQuery = "SELECT COUNT(*) FROM images WHERE checksum = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkQuery);
            preparedStatement.setString(1, checksum);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateImageData(ImageData imageData) throws  SQLException {
        int imageId = getImageIdByChecksum(imageData.getChecksum());
            if (imageId != -1) {
                deleteImageTagsByImageId(imageId);
                deleteImageById(imageId);
            }
            insertToDatabase(imageData);
    }

    public ImageData fetchImage(String checksum) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM images WHERE checksum = ?");
            preparedStatement.setString(1, checksum);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String url = resultSet.getString("url");
                String uploadedAt = resultSet.getString("uploaded_at");
                int width = resultSet.getInt("width");
                int height = resultSet.getInt("height");
                int id = resultSet.getInt("id");

                Map<String, Double> tagMap = getTagMap(id);

                return new ImageData(url, uploadedAt, width, height, tagMap);
            }

        } finally {
            try {
                resultSet.close();
            } catch (Exception e) {}
            try {
                preparedStatement.close();
            } catch (Exception e) {}
        }

        return null;
    }

    public List<ImageData> getAllImages() throws SQLException{
        List<ImageData> images = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM images";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String url = resultSet.getString("url");
                String uploadedAt = resultSet.getString("uploaded_at");
                int width = resultSet.getInt("width");
                int height = resultSet.getInt("height");
                int id = resultSet.getInt("id");

                Map<String, Double> tagMap = getTagMap(id);

                ImageData imageData = new ImageData(url, uploadedAt, width, height, tagMap);
                images.add(imageData);
            }
        } finally {
            try {
                resultSet.close();
            } catch (Exception e) {}
            try {
                preparedStatement.close();
            } catch (Exception e) {}
        }

        return images;
    }

    private Map<String, Double> getTagMap(int id) throws SQLException {
        PreparedStatement preparedStatement = null;
        Map<String, Double> tagMap = new HashMap<>();

        String sql = "SELECT * FROM tags INNER JOIN image_tags ON tags.id = image_tags.tag_id WHERE image_tags.image_id = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet tagResultSet = preparedStatement.executeQuery();

        while (tagResultSet.next()) {
            String tag = tagResultSet.getString("label");
            Double accuracy = tagResultSet.getDouble("accuracy");
            tagMap.put(tag, accuracy);
        }

        return tagMap;
    }

    private void insertImage(ImageData imageData) throws SQLException{
        String sql = "INSERT INTO images (url, width, height, checksum) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, imageData.getUrl());
                preparedStatement.setInt(2, imageData.getWidth());
                preparedStatement.setInt(3, imageData.getHeight());
                preparedStatement.setString(4, imageData.getChecksum());
                preparedStatement.executeUpdate();
            }
    }

    private void insertTags(ImageData imageData) throws SQLException {
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

    private void insertImageToTags(ImageData imageData) throws SQLException {
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

    private int getImageIdByChecksum(String checksum) throws SQLException {
        String sql = "SELECT id FROM images WHERE checksum = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, checksum);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            else {
                return -1;
            }
        }
    }

    private void deleteImageTagsByImageId(int imageId) throws SQLException {
        String sql = "DELETE FROM image_tags WHERE image_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, imageId);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteImageById(int imageId) throws  SQLException {
        String sql = "DELETE FROM images WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, imageId);
            preparedStatement.executeUpdate();
        }
    }
}

