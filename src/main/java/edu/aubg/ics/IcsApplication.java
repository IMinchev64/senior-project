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

		String folderPath = COCO_TRAIN_IMAGES_PATH;
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		int numImages = 24000;
		int batchSize = 500;
		int numBatches = numImages / batchSize;

		FeatureDimensionalityReducer reducer = new FeatureDimensionalityReducer(100, 0.001);

//		for (int batch = 0; batch < numBatches; batch++) {
//			List<float[]> batchFeatureVectors = new ArrayList<>();
//
//			for (int i = 0; i < batchSize; i++) {
//				File file = listOfFiles[batch * batchSize + i];
//				if (file.isFile() && isImageFile(file)) {
//					try {
//						BufferedImage image = ImageIO.read(file);
//						float[] featureVector = extractor.extractFeatures(image);
//						batchFeatureVectors.add(featureVector);
//						System.out.println("Processed image: " + file.getName());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			System.out.println("Current batch is: " + (batch+1));
//			System.out.println("Batches remaining: " + (numBatches-batch-1));
//			reducer.partialFit(batchFeatureVectors);
//		}
//
//		reducer.savePrincipleComponents();

		BufferedImage image = ImageIO.read(new File(String.format("%s/%s", folderPath, "000000000009.jpg")));
		float[] features = extractor.extractFeatures(image);
		float[] reducedFeatures = reducer.reduceFeatureVector(features);
		System.out.println("Feature vectors: " + Arrays.toString(features));
		System.out.println("\n");
		System.out.println("Reduced feature vectors: " + Arrays.toString(reducedFeatures));

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
