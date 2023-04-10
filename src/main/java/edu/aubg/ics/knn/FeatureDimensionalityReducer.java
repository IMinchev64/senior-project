package edu.aubg.ics.knn;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.*;
import java.util.List;

public class FeatureDimensionalityReducer {
    private final int targetDimensions;
    private RealMatrix principalComponents;
    private double learningRate;

    public FeatureDimensionalityReducer(int targetDimensions, double learningRate) {
        this.targetDimensions = targetDimensions;
        this.learningRate = learningRate;
    }

    public float[] reduceFeatureVector(float[] featureVector) {
        loadPrincipalComponents();
        RealVector inputVector = MatrixUtils.createRealVector(toDoubleArray(featureVector));
        RealVector reducedVector = principalComponents.transpose().operate(inputVector);
        return toFloatArray(reducedVector.toArray());
    }

    public void partialFit(List<float[]> data) {
        RealMatrix dataMatrix = convertToRealMatrix(data);

        if (principalComponents == null) {
            principalComponents = initializePrincipleComponents(dataMatrix.getColumnDimension(), targetDimensions);
        }

        double epsilon = 1e-8;

        for (int i = 0; i < dataMatrix.getRowDimension(); i++) {
            RealVector x = dataMatrix.getRowVector(i);
            for (int j = 0; j < principalComponents.getColumnDimension(); j++) {
                RealVector w = principalComponents.getColumnVector(j);
                double dotProduct = w.dotProduct(x);
                RealVector deltaW = x.mapMultiply(dotProduct).subtract(w.mapMultiply(dotProduct * dotProduct + epsilon)).mapMultiply(learningRate);
                w = w.add(deltaW);
                w = w.unitVector();
                principalComponents.setColumnVector(j, w);
            }
        }
        savePrincipleComponents();
    }

    private RealMatrix convertToRealMatrix(List<float[]> data) {
        int numRows = data.size();
        int numCols = data.get(0).length;
        double[][] doubleData = new double[numRows][];

        for (int i = 0; i < numRows; i++) {
            doubleData[i] = new double[numCols];
            for (int j = 0; j < numCols; j++) {
                doubleData[i][j] = data.get(i)[j];
            }
        }
        return MatrixUtils.createRealMatrix(doubleData);
    }

    private double[] toDoubleArray(float[] floatArray) {
        double[] doubleArray = new double[floatArray.length];
        for (int i = 0; i < floatArray.length; i++) {
            doubleArray[i] = floatArray[i];
        }
        return doubleArray;
    }

    private float[] toFloatArray(double[] doubleArray) {
        float[] floatArray = new float[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) {
            floatArray[i] = (float) doubleArray[i];
        }
        return floatArray;
    }

    private RealMatrix initializePrincipleComponents(int numRows, int numCols) {
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        double[][] randomData = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                randomData[i][j] = randomDataGenerator.nextUniform(-0.01,0.01);
            }
        }
        return MatrixUtils.createRealMatrix(randomData);
    }

    public void loadPrincipalComponents() {
        try (FileInputStream fis = new FileInputStream("PrincipleComponents");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            this.principalComponents = (RealMatrix) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void savePrincipleComponents() {
        try (FileOutputStream fos = new FileOutputStream("PrincipleComponents");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(principalComponents);
            System.out.println(principalComponents.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
