package edu.aubg.ics.knn;

import edu.aubg.ics.aop.MeasurePerformance;
import edu.aubg.ics.dao.PostgresDAO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import static edu.aubg.ics.util.Constants.POSTGRES_COCO_CONNECTION;

public class ImageAnalyzer {
    private int imageWidth = 224;
    private int imageHeight = 224;
    private float[] normalizationFactors = {127.5f, 127.5f};
    private int targetDimensions = 128;
    private double learningRate = 0.001;
    private int k = 5;
    private BufferedImage image;
    private Connection connection;

    @MeasurePerformance
    public String analyzeImage(String imageUrl) throws IOException, SQLException {
        this.connection = new PostgresDAO(POSTGRES_COCO_CONNECTION).getConnection();
        setImage(imageUrl);
        ImageFeatureExtractor extractor = new ImageFeatureExtractor(imageWidth, imageHeight, normalizationFactors);
        FeatureDimensionalityReducer reducer = new FeatureDimensionalityReducer(targetDimensions, learningRate);

        float[] featureVectors = extractor.extractFeatures(image);
        float[] reducedFeatureVectors = reducer.reduceFeatureVector(featureVectors);

        return ImageFeatureKNN.findNearestLabel(reducedFeatureVectors, k, connection);
    }

    private void setImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        this.image = ImageIO.read(is);
        is.close();
    }
}
