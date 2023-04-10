package edu.aubg.ics.knn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.stream.Stream;

import static edu.aubg.ics.util.Constants.*;

public class ImageFeatureDatabaseInserter {
    private ImageFeatureExtractor featureExtractor;
    //private FeatureDimensionalityReducer featureDimensionalityReducer = new FeatureDimensionalityReducer(100);

    public ImageFeatureDatabaseInserter(ImageFeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;
    }

    public void insertFeatures() throws IOException, SQLException {
        try (Connection connection = DriverManager.getConnection(POSTGRES_COCO_CONNECTION, POSTGRES_USERNAME, POSTGRES_PASSWORD)) {
            insertFeaturesForFolder(connection, COCO_TRAIN_IMAGES_PATH, "coco_train_features");
        }
    }

    private void insertFeaturesForFolder(Connection connection, String folderPath, String tableName) throws IOException, SQLException {
        ImageIO.scanForPlugins();

        try (Stream<Path> paths = Files.walk(Path.of(folderPath))) {
            paths.filter(Files::isRegularFile)
                    .forEach(
                            imagePath -> {
                                try {
                                    BufferedImage image = ImageIO.read(imagePath.toFile());
                                    String fileName = imagePath.getFileName().toString();
                                    String label = getLabelForImage(connection, fileName);

                                    if (label != null) {
                                        float[] features = featureExtractor.extractFeatures(image);
                                        //float[] reducedFeatures = featureDimensionalityReducer.reduce(features);
                                        insertFeatureInDatabase(connection, tableName, fileName, label, features);
                                    }
                                    else {
                                        System.err.println("Label not found for file: " + fileName);
                                    }
                                } catch (IOException | SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
        }
    }

    private String getLabelForImage(Connection connection, String fileName) throws SQLException {
        String label = null;
        String sqlTrainImages = "SELECT label FROM coco_train_images WHERE file_name = ?";
        String sqlValImages = "SELECT label FROM coco_val_images WHERE file_name = ?";

        try (PreparedStatement preparedStatementTrain = connection.prepareStatement(sqlTrainImages)) {
            preparedStatementTrain.setString(1, fileName);

            try (ResultSet resultSet = preparedStatementTrain.executeQuery()) {
                if (resultSet.next()) {
                    label = resultSet.getString("label");
                }
            }
        }

        if (label == null) {
            try (PreparedStatement preparedStatementVal = connection.prepareStatement(sqlValImages)) {
                preparedStatementVal.setString(1, fileName);

                try (ResultSet resultSet = preparedStatementVal.executeQuery()) {
                    if (resultSet.next()) {
                        label = resultSet.getString("label");
                    }
                }
            }
        }

        return label;
    }

    private void insertFeatureInDatabase(Connection connection, String tableName, String fileName, String label, float[] features) throws SQLException {
        String sql = String.format("INSERT INTO %s (file_name, label, features) VALUES (?, ?, ?);", tableName);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setString(2, label);

            Float[] floatObjArray = new Float[features.length];
            for (int i = 0; i < features.length; i++) {
                floatObjArray[i] = features[i];
            }
            preparedStatement.setArray(3, connection.createArrayOf("float4", floatObjArray));
            preparedStatement.executeUpdate();
            System.out.println("Successfully inserted features!");
        }
    }
}
