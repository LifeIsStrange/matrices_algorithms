import java.util.ArrayList;
        import java.util.List;

public class NaiveMatrixMultiplication {
    public static int[][] naiveMatrixMultiply(int[][] matrixA, int[][] matrixB) throws Exception {
        // validate the matrices
        if (matrixA.length == 0 && matrixB.length == 0) {
            return new int[0][0];
        } else if (matrixA.length == 0 ^ matrixB.length == 0) {
            throw new Exception("Only one of the two matrices are empty, which is an invalid operation");
        } else if (matrixA[0].length != matrixB.length) {
            // the number of columns in the first matrix must be equal to the number of rows in the second matrix.
            throw new Exception("The matrices have an invalid shape");
        }

        var matrixC = new int[matrixA.length][matrixB[0].length];
        int interlacedAxisSize = matrixA[0].length; // equivalent to matrixB.length

        for (int i = 0; i < matrixC.length; i++) {
            for (int j = 0; j < matrixC[i].length; j++) {
                for (int k = 0; k < interlacedAxisSize; k++) {
                    matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return matrixC;
    }

    public static void main(String[] args) throws Exception {
        int[][] matrixA = {{1, 2, 3},
                            {4, 5, 6}};
        int[][] matrixB = {{10, 11},
                            {20, 21},
                            {30, 31}};
        int[][] expectedReferenceResult = {{140, 146},
                                           {320, 335}};

        var result = naiveMatrixMultiply(matrixA, matrixB);

        /*for (int i = 0; i < expectedReferenceResult.length; i++) {
            for (int j = 0; j < expectedReferenceResult[i].length; j++) {
                System.out.print(result[i][j] + " ");
                assert result[i][j] == expectedReferenceResult[i][j];
            }
            System.out.println();
        }*/
    }
}
