package edu.aubg.ics.knn;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CocoAnnotationParser {

    private static void parseCocoAnnotation(String filePath, Connection connection) throws IOException, SQLException {
        Gson gson = new Gson();
        Map<Integer, String> categoryMap = new HashMap<>();
        Map<Integer, Integer> imageIdToCategoryId = new HashMap<>();

        try (JsonReader jsonReader = new JsonReader(new FileReader(filePath))) {
            jsonReader.beginObject();
            while(jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("categories".equals(name)) {
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        JsonObject category = gson.fromJson(jsonReader, JsonObject.class);
                        int id = category.get("id").getAsInt();
                        String categoryName = category.get("name").getAsString();
                        categoryMap.put(id, categoryName);
                    }
                    jsonReader.endArray();
                } else if ("annotations".equals(name)) {
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        JsonObject annotation = gson.fromJson(jsonReader, JsonObject.class);
                        int imageId = annotation.get("image_id").getAsInt();
                        int categoryId = annotation.get("category_id").getAsInt();
                        imageIdToCategoryId.put(imageId, categoryId);
                    }
                    jsonReader.endArray();
                }  else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }

        try (JsonReader jsonReader = new JsonReader(new FileReader(filePath))) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("images".equals(name)) {
                    String insertSql = "INSERT INTO coco_val_images (file_name, label) VALUES (?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            JsonObject image = gson.fromJson(jsonReader, JsonObject.class);
                            int id = image.get("id").getAsInt();
                            String fileName = image.get("file_name").getAsString();
                            if (imageIdToCategoryId.containsKey(id)) {
                                int categoryId = imageIdToCategoryId.get(id);
                                String label = categoryMap.get(categoryId);

                                preparedStatement.setString(1, fileName);
                                preparedStatement.setString(2, label);
                                preparedStatement.addBatch();
                                System.out.println("Pair added to batch.");
                            }
                        }
                        jsonReader.endArray();
                        preparedStatement.executeBatch();
                    }
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
    }
}
