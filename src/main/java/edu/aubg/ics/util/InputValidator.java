package edu.aubg.ics.util;

import java.net.URL;

public class InputValidator {
    public static boolean isValidImageUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String protocol = url.getProtocol();

            if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
                return false;
            }

            String file = url.getFile().toLowerCase();
            return file.endsWith(".jpg") || file.endsWith(".jpeg") || file.endsWith(".png") || file.endsWith(".gif");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
