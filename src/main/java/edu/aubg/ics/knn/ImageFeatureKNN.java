package edu.aubg.ics.knn;

import java.sql.*;
import java.util.*;

public class ImageFeatureKNN {
    public static String findNearestLabel(float[] features, int k, Connection connection) throws SQLException {
        final int n = 12000;
        PriorityQueue<DistanceLabelPair> closeSamples = new PriorityQueue<>(Comparator.comparingDouble(DistanceLabelPair::getDistance).reversed());

        String featureCube = "CUBE(ARRAY[" + Arrays.toString(features).substring(1, Arrays.toString(features).length() - 1) + "])";

        String sql = "SELECT label, features, vector_cube <-> " + featureCube + " AS distance FROM coco_train_features ORDER BY distance LIMIT " + n;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String label = resultSet.getString("label");
                Array dbArray = resultSet.getArray("features");
                Float[] dbFloatObjArray = (Float[]) dbArray.getArray();
                float[] dbFeatures = new float[dbFloatObjArray.length];

                for (int i = 0; i < dbFloatObjArray.length; i++) {
                    dbFeatures[i] = dbFloatObjArray[i];
                }

                double distance = resultSet.getDouble("distance");
                closeSamples.offer(new DistanceLabelPair(distance, label, dbFeatures));
            }
        }

        PriorityQueue<DistanceLabelPair> nearestNeighbors = new PriorityQueue<>(Comparator.comparingDouble(DistanceLabelPair::getDistance));

        for (int i = 0; i < n; i++) {
            DistanceLabelPair sample = closeSamples.poll();
            double euclideanDist = euclideanDistance(features, sample.getFeatures());
            nearestNeighbors.offer(new DistanceLabelPair(euclideanDist, sample.getLabel()));
        }

        PriorityQueue<DistanceLabelPair> kNearestNeighbors = new PriorityQueue<>(Comparator.comparingDouble(DistanceLabelPair::getDistance));
        for (int i = 0; i < k; i++) {
            DistanceLabelPair pair = nearestNeighbors.poll();
            kNearestNeighbors.offer(pair);
        }

        Map<String, Double> labelWeightSum = new HashMap<>();
        double totalWeight = 0;

        while (!kNearestNeighbors.isEmpty()) {
            DistanceLabelPair pair = kNearestNeighbors.poll();
            double weight = 1 / (pair.getDistance() + 1e-6); // Add a small constant to avoid division by zero
            labelWeightSum.put(pair.getLabel(), labelWeightSum.getOrDefault(pair.getLabel(), 0.0) + weight);
            totalWeight += weight;
        }

        List<LabelPercentagePair> labelsWithPercentages = new ArrayList<>();
        for (Map.Entry<String, Double> entry : labelWeightSum.entrySet()) {
            double percentage = entry.getValue() / totalWeight * 100;
            labelsWithPercentages.add(new LabelPercentagePair(entry.getKey(), percentage, entry.getValue()));
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
