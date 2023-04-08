package edu.aubg.ics.knn;

import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Pipeline;

import java.awt.image.BufferedImage;

public class ImageFeatureExtractor {
    private int imageWidth;
    private int imageHeight;
    private float[] normalizationFactors;
    private Pipeline pipeline;
    private ImagePreprocessor imagePreprocessor;

    public ImageFeatureExtractor(int imageWidth, int imageHeight, float[] normalizationFactors) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.normalizationFactors = normalizationFactors;
        this.pipeline = new Pipeline();
        this.imagePreprocessor = new ImagePreprocessor(imageWidth, imageHeight, normalizationFactors);
    }

    public float[] extractFeatures(BufferedImage image) {
        float[] preprocessedPixels = imagePreprocessor.preprocess(image);

        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray array = manager.create(preprocessedPixels, new Shape(1, imageHeight, imageWidth));
            NDArray processedArray = processImage(array, pipeline);
            return processedArray.toFloatArray();
        }
    }

    private NDArray processImage(NDArray input, Pipeline pipeline) {
        NDArray normalizedArray = input.sub(normalizationFactors[0]).div(normalizationFactors[1]);
        return pipeline.transform(new NDList(normalizedArray)).singletonOrThrow();
    }
}
