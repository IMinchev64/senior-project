package edu.aubg.ics.knn;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FeatureDimensionalityReducer {
    private final int targetDimensions;
    private RealMatrix transformationMatrix;

    public FeatureDimensionalityReducer(int targetDimensions, List<float[]> data) {
        this.targetDimensions = targetDimensions;
        computeTransformationMatrix(data);
    }

    public float[] reduce(float[] featureVector) {
        double[] doubleFeatureVector = new double[featureVector.length];
        for (int i = 0; i < featureVector.length; i++) {
            doubleFeatureVector[i] = featureVector[i];
        }

        RealMatrix principalComponents = loadPrincipalComponents("PrincipalComponents");
        RealMatrix featureMatrix = new Array2DRowRealMatrix(new double[][] {doubleFeatureVector});
        RealMatrix transposedPrincipalComponents = principalComponents.transpose();
        RealMatrix reducedMatrix = featureMatrix.multiply(transposedPrincipalComponents);

        double[] reducedData = reducedMatrix.getRow(0);
        float[] reducedFeatureVector = new float[reducedData.length];
        for (int i = 0; i < reducedData.length; i++) {
            reducedFeatureVector[i] = (float) reducedData[i];
        }
        return reducedFeatureVector;
    }

    private RealMatrix loadPrincipalComponents(String fileName) {
        RealMatrix principleComponents = null;
        try (FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis)) {
            principleComponents = (RealMatrix) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return principleComponents;
    }

    private void computeTransformationMatrix(List<float[]> data) {
        int numRows = data.size();
        int numCols = data.get(0).length;

        double[][] doubleData = new double[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                doubleData[i][j] = data.get(i)[j];
            }
        }

        RealMatrix matrix = MatrixUtils.createRealMatrix(doubleData);
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        this.transformationMatrix = svd.getV().getSubMatrix(0, numCols - 1, 0, targetDimensions - 1).transpose();

        try (FileOutputStream fos = new FileOutputStream("PrincipalComponents");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this.transformationMatrix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
