package AI;

import java.util.Arrays;

public class MatrixVectorMultiply {

    static double[] multiply(double[][] matrix, double[] vector) {
        double[] result = new double[matrix.length];

        int zeilenLaenge = matrix[0].length;
        if(zeilenLaenge != vector.length) throw new IllegalArgumentException();

        for(double[] zeile : matrix) {
            for (int i = 0; i < zeilenLaenge; i++) {
                result[i] += zeile[i] * vector[i];
            }
        }

        return result;
    }

}
