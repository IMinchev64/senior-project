package edu.aubg.ics.knn;

public class DistanceLabelPair {
    private double distance;
    private String label;
    private float[] features;

    public DistanceLabelPair(double distance, String label) {
        this.distance = distance;
        this.label = label;
    }

    public DistanceLabelPair(double distance, String label, float[] features) {
        this.distance = distance;
        this.label = label;
        this.features = features;
    }

    public double getDistance() {
        return distance;
    }

    public String getLabel() {
        return label;
    }

    public float[] getFeatures() {
        return features;
    }
}
