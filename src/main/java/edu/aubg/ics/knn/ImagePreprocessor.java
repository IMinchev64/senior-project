package edu.aubg.ics.knn;

import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.image.*;

public class ImagePreprocessor {

    private int imageWidth;
    private int imageHeight;
    private float[] normalizationFactors;

    public ImagePreprocessor(int imageWidth, int imageHeight, float[] normalizationFactors) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.normalizationFactors = normalizationFactors;
    }

    public float[] preprocess(BufferedImage image) {
        // Resize the image
        PlanarImage planarImage = PlanarImage.wrapRenderedImage(image);
        planarImage = ScaleDescriptor.create(planarImage, (float) imageWidth / image.getWidth(), (float) imageHeight / image.getHeight(),
                0.0f, 0.0f, Interpolation.getInstance(Interpolation.INTERP_BILINEAR), null);
        BufferedImage resizedImage = planarImage.getAsBufferedImage();

        // Convert the image to grayscale
        BufferedImageOp grayscaleFilter = new ColorConvertOp(java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_GRAY), null);
        BufferedImage grayscaleImage = grayscaleFilter.filter(resizedImage, null);

        // Convert the image to a float array and normalize
        float[] pixelValues = getPixelValues(grayscaleImage);
        for (int i = 0; i < pixelValues.length; i++) {
            pixelValues[i] = (pixelValues[i] - normalizationFactors[0]) / normalizationFactors[1];
        }

        return pixelValues;
    }

    private float[] getPixelValues(BufferedImage image) {
        // Get the pixel values from the image
        Raster raster = image.getData();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        byte[] pixels = buffer.getData();

        // Convert the pixel values to a float array
        float[] pixelValues = new float[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            pixelValues[i] = (float) (pixels[i] & 0xff);
        }

        return pixelValues;
    }
}
