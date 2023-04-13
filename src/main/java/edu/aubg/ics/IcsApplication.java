package edu.aubg.ics;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import edu.aubg.ics.knn.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.aubg.ics.util.Constants.*;

@SpringBootApplication
public class IcsApplication {

	public static void main(String[] args) throws IOException, SQLException {
		//CocoAnnotationParser.connectToDB();
		//SpringApplication.run(IcsApplication.class, args);

		int imageWidth = 224;
		int imageHeight = 224;
		float[] normalizationFactors = {127.5f, 127.5f};

		// Initialize the ImageFeatureExtractor
		ImageFeatureExtractor extractor = new ImageFeatureExtractor(imageWidth, imageHeight, normalizationFactors);

		// Load the image
		BufferedImage image = ImageIO.read(new File(COCO_TRAIN_IMAGES_PATH + "000000000034.jpg"));

		// Extract feature vectors from the image
		float[] featureVectors = extractor.extractFeatures(image);

		// Initialize the FeatureDimensionalityReducer
		int targetDimensions = 128;
		double learningRate = 0.001;
		FeatureDimensionalityReducer reducer = new FeatureDimensionalityReducer(targetDimensions, learningRate);

		// Reduce the dimensionality of the feature vectors
		float[] reducedFeatureVectors = reducer.reduceFeatureVector(featureVectors);

		// Initialize the KNN algorithm and connect to the database
		int k = 5;
		Connection connection = DriverManager.getConnection(POSTGRES_COCO_CONNECTION, POSTGRES_USERNAME, POSTGRES_PASSWORD);

		// Call the KNN algorithm to find the nearest label and print it
		List<LabelPercentagePair> nearestLabel2 = ImageFeatureKNN2.findNearestLabel(reducedFeatureVectors, k, connection);
		String nearestLabel = ImageFeatureKNN.findNearestLabel(reducedFeatureVectors, k, connection);

		System.out.println("The nearest label is according to KNN1: " + nearestLabel);
		System.out.println("The nearest label is according to KNN2: " + nearestLabel2.get(0).getLabel());
		//System.out.println(Arrays.toString(reducedFeatureVectors));
	}
}
