package AI;

import java.util.Arrays;

public class MathHelper {

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

    static double multiply(double[] vector1, double[] vector2) {
        if(vector1.length != vector2.length) throw new IllegalArgumentException();

        double result = 0;
        for (int i = 0; i < vector1.length; i++) {
            result += vector1[i] * vector2[i];
        }
        return result;
    }

}
