public class StrassenMatrixMultiply {
    public static boolean isOddAndIsNotOne(int nbr) {
        if(nbr == 1) {
            return false;
        }

        return nbr % 2 != 0;
    }

    // assume matrices of identical dimensions
    public static int[][] subtract(int[][] matrixA, int[][] matrixB) {
        var matrixC = new int[matrixA.length][matrixA[0].length];

        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixA[0].length; j++) {
                matrixC[i][j] = matrixA[i][j] - matrixB[i][j];
            }
        }
        return matrixC;
    }

    // possible optimization modify in-place
    // assume matrices of identical dimensions
    public static int[][] sum(int[][] matrixA, int[][] matrixB) {
        var matrixC = new int[matrixA.length][matrixA[0].length];

        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixA[0].length; j++) {
                matrixC[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }
        return matrixC;
    }

    // in-place transform
    private static int[][] fillMatrixQuadrant(int[][] dest, int[][] source, int quadrantRelativeOrder) throws Exception {
        var halfOfRowNbr = dest.length / 2;
        var halfOfColumnNbr = dest[0].length / 2;

        int startI = 0;
        int startJ = 0;

        // encode where the indexes are initially positioned in the output matrix
        if (quadrantRelativeOrder == 1) {
            // NTD
        } else if (quadrantRelativeOrder == 2) {
            startJ = halfOfColumnNbr;
        } else if (quadrantRelativeOrder == 3) {
            startI = halfOfRowNbr;
        } else if (quadrantRelativeOrder == 4) {
            startI = halfOfRowNbr;
            startJ = halfOfColumnNbr;
        } else throw new Exception("The specified quadrantRelativeOrder should be comprised between 1 and 4;");


        for (int row = 0, i = startI; row < halfOfRowNbr; row++, i++) {
            for (int col = 0, j = startJ; col < halfOfColumnNbr; col++, j++) {
                // it is effectively the inverse function of getMatrixQuadrant
                dest[i][j] = source[row][col];
            }
        }
        return dest;
    }

    // return the requested relative matrix quadrant (quarter)
    private static int[][] getMatrixQuadrant(int[][] matrix, int quadrantRelativeOrder) throws Exception {
        var halfOfRowNbr = matrix.length / 2;
        var halfOfColumnNbr = matrix[0].length / 2;

        var quadrant = new int[halfOfRowNbr][halfOfColumnNbr];

        int startI = 0;
        int startJ = 0;

        // encode where the indexes are initially positioned in the input matrix
        if (quadrantRelativeOrder == 1) {
            // NTD
        } else if (quadrantRelativeOrder == 2) {
            startJ = halfOfColumnNbr;
        } else if (quadrantRelativeOrder == 3) {
            startI = halfOfRowNbr;
        } else if (quadrantRelativeOrder == 4) {
            startI = halfOfRowNbr;
            startJ = halfOfColumnNbr;
        } else throw new Exception("The specified quadrantRelativeOrder should be comprised between 1 and 4;");


        for (int row = 0, i = startI; row < halfOfRowNbr; row++, i++) {
            for (int col = 0, j = startJ; col < halfOfColumnNbr; col++, j++) {
                quadrant[row][col] = matrix[i][j];
            }
        }
        return quadrant;
    }

    // https://en.wikipedia.org/wiki/Strassen_algorithm#Algorithm
    private static int[][] recursiveStrassenMultiply(int[][] matrixA, int[][] matrixB) throws Exception {
        var matrixC = new int[matrixA.length][matrixA.length];

        // if the matrices are not even, call itself with padding
        if (isOddAndIsNotOne(matrixA.length)) {
            int evenLength = matrixA.length + 1;

            var evenMatrixA = new int[evenLength][evenLength];
            var evenMatrixB = new int[evenLength][evenLength];
            var evenMatrixC = new int[evenLength][evenLength];

            for (int i = 0; i < matrixA.length; i++) {
                System.arraycopy(matrixA[i], 0, evenMatrixA[i], 0, matrixA.length);
                System.arraycopy(matrixB[i], 0, evenMatrixB[i], 0, matrixA.length);
            }

            evenMatrixC = recursiveStrassenMultiply(evenMatrixA, evenMatrixB);

            // strip the zeros
            for(int i = 0; i < matrixA.length; i++) {
                System.arraycopy(evenMatrixC[i], 0, matrixC[i], 0, matrixA.length);
            }
            return matrixC;
        }

        // base, irreducible case has been reached
        if (matrixA.length == 1 && matrixA[0].length == 1) {
            matrixC[0][0] = matrixA[0][0] * matrixB[0][0];
        } else { // default code path, recurse
            // shallow split the matrices
            var A11 = getMatrixQuadrant(matrixA, 1);
            var A12 = getMatrixQuadrant(matrixA, 2);
            var A21 = getMatrixQuadrant(matrixA, 3);
            var A22 = getMatrixQuadrant(matrixA, 4);

            var B11 = getMatrixQuadrant(matrixB, 1);
            var B12 = getMatrixQuadrant(matrixB, 2);
            var B21 = getMatrixQuadrant(matrixB, 3);
            var B22 = getMatrixQuadrant(matrixB, 4);


            // compute the seven Strassen sub-matrices
            var M1 = recursiveStrassenMultiply(sum(A11, A22), sum(B11, B22));
            var M2 = recursiveStrassenMultiply(sum(A21, A22), B11);
            var M3 = recursiveStrassenMultiply(A11, subtract(B12, B22));
            var M4 = recursiveStrassenMultiply(A22, subtract(B21, B11));
            var M5 = recursiveStrassenMultiply(sum(A11, A12), B22);
            var M6 = recursiveStrassenMultiply(subtract(A21, A11), sum(B11, B12));
            var M7 = recursiveStrassenMultiply(subtract(A12, A22), sum(B21, B22));

            // compute the quadrants
            var C11 = sum(subtract(sum(M1, M4), M5), M7);
            var C12 = sum(M3, M5);
            var C21 = sum(M2, M4);
            var C22 = sum(sum(subtract(M1, M2), M3), M6);

            // reassemble the quadrants
            fillMatrixQuadrant(matrixC, C11, 1);
            fillMatrixQuadrant(matrixC, C12, 2);
            fillMatrixQuadrant(matrixC, C21, 3);
            fillMatrixQuadrant(matrixC, C22, 4);
        }

        return matrixC;
    }

    public static int[][] strassenMatrixMultiply(int[][] matrixA, int[][] matrixB) throws Exception {
        // validate the matrices
        if (matrixA.length == 0 && matrixB.length == 0) {
            return new int[0][0];
        } else if (matrixA.length == 0 ^ matrixB.length == 0) {
            throw new Exception("Only one of the two matrices is empty, which is an invalid operation");
        }  else if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new Exception("matrices of non-identical dimensions are not supported");
        } else if (matrixA.length != matrixA[0].length) {
            // support could be added technically
            throw new Exception("non-square matrices are not supported");
        }

        /*if (matrixA.length % 2 != 0) {
            var evenMatrixA = new int[matrixA.length + 1][matrixA.length + 1];
            var evenMatrixB = new int[matrixA.length + 1][matrixA.length + 1];

            System.arraycopy(matrixA, 0, evenMatrixA, 0, matrixA.length);
            System.arraycopy(matrixB, 0, evenMatrixB, 0, matrixA.length);

            return recursiveStrassenMultiply(evenMatrixA, evenMatrixB);
        } else {
            return recursiveStrassenMultiply(matrixA, matrixB);
        }*/
        return recursiveStrassenMultiply(matrixA, matrixB);
    }

    public static void main(String[] args) throws Exception {
        /*int[][] matrixA = { {2, 3, 4, 5, 6},
                            {8, 9, 10, 11, 12},
                            { 14, 15, 16, 17, 18},
                            { 14, 15, 16, 17, 18},
                            { 14, 15, 16, 17, 18}};

        int[][] matrixB = { { 14, 15, 16, 17, 18},
                            { 14, 15, 16, 17, 18},
                            { 14, 15, 16, 17, 18},
                            {2, 3, 4, 5, 6},
                            { 14, 15, 16, 17, 18}};

        int[][] expectedReferenceResult = { {220, 240, 260,  280,  300},
                                            {568, 618, 668,  718,  768},
                                            {916, 996, 1076, 1156, 1236},
                                            {916, 996, 1076, 1156, 1236},
                                            {916, 996, 1076, 1156, 1236}};*/


        int[][] matrixA = { {1, 2, 3, 4, 5, 6},
                            {7, 8, 9, 10, 11, 12},
                            {13, 14, 15, 16, 17, 18},
                            {13, 14, 15, 16, 17, 18},
                            {13, 14, 15, 16, 17, 18},
                            {13, 14, 15, 16, 17, 18}};

        int[][] matrixB = { {13, 14, 15, 16, 17, 18},
                            {13, 14, 15, 16, 17, 18},
                            {13, 14, 15, 16, 17, 18},
                            {1, 2, 3, 4, 5, 6},
                            {7, 8, 9, 10, 11, 12},
                            {13, 14, 15, 16, 17, 18}};

        int[][] expectedReferenceResult = { {195, 216, 237, 258, 279, 300},
                                            {555, 612, 669, 726, 783, 840},
                                            {915, 1008, 1101, 1194, 1287, 1380},
                                            {915, 1008, 1101, 1194, 1287, 1380},
                                            {915, 1008, 1101, 1194, 1287, 1380},
                                            {915, 1008, 1101, 1194, 1287, 1380}};

        var result = strassenMatrixMultiply(matrixA, matrixB);


        for (int i = 0; i < expectedReferenceResult.length; i++) {
            for (int j = 0; j < expectedReferenceResult[i].length; j++) {
                System.out.print(result[i][j] + " ");
                assert result[i][j] == expectedReferenceResult[i][j];
            }
            System.out.println();
        }
    }
}
