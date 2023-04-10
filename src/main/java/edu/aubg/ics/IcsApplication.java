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

		ImageFeatureExtractor extractor = new ImageFeatureExtractor(imageWidth, imageHeight, normalizationFactors);

//		ImageFeatureDatabaseInserter imageFeatureDatabaseInserter = new ImageFeatureDatabaseInserter(imageFeatureExtractor);
//
//		imageFeatureDatabaseInserter.insertFeatures();

		List<float[]> featureVectors = new ArrayList<>();
		String folderPath = COCO_TRAIN_IMAGES_PATH;
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		int numImages = 10000;
		int processedImages = 0;
		for (File file : listOfFiles) {
			if (file.isFile() && isImageFile(file)) {
				try {
					BufferedImage image = ImageIO.read(file);
					float[] featureVector = extractor.extractFeatures(image);
					featureVectors.add(featureVector);
					System.out.println("Processed image: " + file.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
				processedImages++;
				if (processedImages >= numImages) {
					break;
				}
			}
		}

		FeatureDimensionalityReducer featureDimensionalityReducer = new FeatureDimensionalityReducer(100, featureVectors);
//		float[] reducedFeatures = featureDimensionalityReducer.reduce(featureVectors.get(0));
//		System.out.println("Feature vectors: " + Arrays.toString(featureVectors.get(0)));
//		System.out.println("\n");
//		System.out.println("Reduced feature vectors: " + Arrays.toString(reducedFeatures));

	}

	private static boolean isImageFile(File file) {
		String[] imageExtensions = new String[]{"jpg", "jpeg", "png", "bmp"};
		String fileName = file.getName().toLowerCase();
		for (String extension : imageExtensions) {
			if (fileName.endsWith(extension)) {
				return true;
			}
		}
		return false;
	}
}
