package edu.aubg.ics.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class ImageDimensions {
    private int width;
    private int height;

    public ImageDimensions(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(url.openStream());
        Iterator<ImageReader> itr = ImageIO.getImageReaders(imageInputStream);

        if (itr.hasNext()) {
            ImageReader reader = itr.next();
            try {
                reader.setInput(imageInputStream);
                width = reader.getWidth(reader.getMinIndex());
                height = reader.getHeight(reader.getMinIndex());
            }
            finally {
                reader.dispose();
            }
        }
        else {
            System.out.println("Image reading error!");
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
