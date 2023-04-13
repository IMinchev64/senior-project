package edu.aubg.ics.knn;

import java.sql.*;
import java.util.*;

public class ImageFeatureKNN {
    public static String findNearestLabel(float[] features, int k, Connection connection) throws SQLException {
        PriorityQueue<DistanceLabelPair> nearestNeighbors = new PriorityQueue<>(Comparator.comparingDouble(DistanceLabelPair::getDistance).reversed());

        String featureCube = "CUBE(ARRAY[" + Arrays.toString(features).substring(1, Arrays.toString(features).length() - 1) + "])";

        String sql = "SELECT label, vector_cube <-> " + featureCube + " AS distance FROM coco_train_features ORDER BY distance LIMIT " + k;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String label = resultSet.getString("label");
                double distance = resultSet.getDouble("distance");

                nearestNeighbors.offer(new DistanceLabelPair(distance, label));
            }
        }

        System.out.println("Nearest Neighbors:");
        for (DistanceLabelPair pair : nearestNeighbors) {
            System.out.println("Label: " + pair.getLabel() + ", Distance: " + pair.getDistance());
        }

        Map<String, Double> labelWeightSum = new HashMap<>();
        while (!nearestNeighbors.isEmpty()) {
            DistanceLabelPair pair = nearestNeighbors.poll();
            double weight = 1 / (pair.getDistance() + 1e-6); // Add a small constant to avoid division by zero
            labelWeightSum.put(pair.getLabel(), labelWeightSum.getOrDefault(pair.getLabel(), 0.0) + weight);
        }

        return Collections.max(labelWeightSum.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
    }

    private static double euclideanDistance(float[] a, float[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }

        return Math.sqrt(sum);
    }

}
