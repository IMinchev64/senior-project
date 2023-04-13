package edu.aubg.ics.knn;

public class LabelPercentagePair {
    private String label;
    private double percentage;
    private double distance;

    public LabelPercentagePair(String label, double distance) {
        this.label = label;
        this.distance = distance;
    }

    public LabelPercentagePair(String label, double percentage, double distance) {
        this.label = label;
        this.percentage = percentage;
        this.distance = distance;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getDistance() {
        return distance;
    }
}
