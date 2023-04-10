package edu.aubg.ics.knn;

import java.sql.*;
import java.util.*;

public class ImageFeatureKNN {
    public static String findNearestLabel(float[] features, int k, Connection connection) throws SQLException {
        PriorityQueue<DistanceLabelPair> nearestNeighbors = new PriorityQueue<>(Comparator.comparingDouble(DistanceLabelPair::getDistance).reversed());

        String sql = "SELECT label, features FROM coco_val_features";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Array dbArray = resultSet.getArray("features");
                Float[] dbFloatObjArray = (Float[]) dbArray.getArray();
                float[] dbFeatures = new float[dbFloatObjArray.length];

                for (int i = 0; i < dbFloatObjArray.length; i++) {
                    dbFeatures[i] = dbFloatObjArray[i];
                }

                String label = resultSet.getString("label");
                double distance = euclideanDistance(features, dbFeatures);

                if (nearestNeighbors.size() < k) {
                    nearestNeighbors.offer(new DistanceLabelPair(distance, label));
                } else if (distance < nearestNeighbors.peek().getDistance()) {
                    nearestNeighbors.poll();
                    nearestNeighbors.offer(new DistanceLabelPair(distance, label));
                }
            }
        }

        Map<String, Integer> labelCount = new HashMap<>();
        while (!nearestNeighbors.isEmpty()) {
            DistanceLabelPair pair = nearestNeighbors.poll();
            labelCount.put(pair.getLabel(), labelCount.getOrDefault(pair.getLabel(), 0) + 1);
        }

        return Collections.max(labelCount.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    private static double euclideanDistance(float[] a, float[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }

        return Math.sqrt(sum);
    }

}
