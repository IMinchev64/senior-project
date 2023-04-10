package edu.aubg.ics.knn;

public class DistanceLabelPair {
    private double distance;
    private String label;

    public DistanceLabelPair(double distance, String label) {
        this.distance = distance;
        this.label = label;
    }

    public double getDistance() {
        return distance;
    }

    public String getLabel() {
        return label;
    }
}
