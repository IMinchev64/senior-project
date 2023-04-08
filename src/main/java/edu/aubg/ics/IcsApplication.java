package edu.aubg.ics;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import edu.aubg.ics.knn.ImageFeatureExtractor;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static edu.aubg.ics.util.Constants.*;

@SpringBootApplication
public class IcsApplication {

	public static void main(String[] args) throws IOException, SQLException {
		//CocoAnnotationParser.connectToDB();
		//SpringApplication.run(IcsApplication.class, args);
		// Read an image from the file system
		String imagePath = "../COCO/train2017/000000000009.jpg";
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			System.err.println("Error reading image file: " + e.getMessage());
			System.exit(1);
		}

		// Set image dimensions and normalization factors
		int imageWidth = 224;
		int imageHeight = 224;
		float[] normalizationFactors = {127.5f, 127.5f};

		// Create an instance of ImageFeatureExtractor
		ImageFeatureExtractor featureExtractor = new ImageFeatureExtractor(imageWidth, imageHeight, normalizationFactors);

		// Extract features from the image
		float[] features = featureExtractor.extractFeatures(image);

		// Print the features
		for (int i = 0; i < features.length; i++) {
			System.out.print(features[i] + " ");
			if ((i + 1) % imageWidth == 0) {
				System.out.println();
			}
		}
	}
}
