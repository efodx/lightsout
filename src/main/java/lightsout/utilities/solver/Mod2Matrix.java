package lightsout.utilities.solver;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Class representing a Mod2 Matrix.
 */
public class Mod2Matrix {
    private final int[][] elements;

    public Mod2Matrix(int[][] elements) {
        this.elements = elements;
    }

    /**
     * Solves the system of equations Ax=y in modulo2, where
     * A is matrix represented by this class and y the given input.
     *
     * @param y               y
     * @param optimalSolution if set to true, returns the vector with the smallest length
     * @return x
     * @throws UnsolvableException if the system is unsolvable
     */
    public int[] solveFor(int[] y, boolean optimalSolution) throws UnsolvableException {
        int[] yy = Arrays.copyOf(y, y.length);
        toEchelonForm(yy);
        if (!isSolvableFor(yy)) {
            throw new UnsolvableException();
        }
        int substitutionStartRowNum = getReverseSubstitutionStartRowNumber(yy);
        if (optimalSolution) {
            return getOptimalSolution(yy, substitutionStartRowNum);
        } else {
            reverseSubstitution(yy, substitutionStartRowNum);
            return yy;
        }
    }

    public int[] solveFor(int[] y) throws UnsolvableException {
        return solveFor(y, false);
    }

    private int[] getOptimalSolution(int[] yy, int substitutionStartRowNum) {
        if (yy.length - substitutionStartRowNum - 1 == 0) {
            reverseSubstitution(yy, substitutionStartRowNum);
            return yy;
        }
        int currentOptimalLength = yy.length;
        int[] currentOptimalSolution = new int[0];
        int[][] solutionEndings = generateAllVectorsMod2OfSize(yy.length - substitutionStartRowNum - 1);
        for (int[] solutionEnding : solutionEndings) {
            int[] yyy = IntStream.concat(Arrays.stream(Arrays.copyOf(yy, yy.length - solutionEnding.length)),
                    Arrays.stream(solutionEnding)
            ).toArray(); // copy of yy with the last few digits changed
            reverseSubstitution(yyy, substitutionStartRowNum);
            int solutionLength = Arrays.stream(yyy).sum();
            if (solutionLength < currentOptimalLength) {
                currentOptimalLength = solutionLength;
                currentOptimalSolution = yyy;
            }
        }
        return currentOptimalSolution;
    }

    private int[][] generateAllVectorsMod2OfSize(int n) {
        int[][] result = new int[(int) Math.pow(2, n)][n];
        if (n == 1) {
            return new int[][]{{0}, {1}};
        } else {
            int[][] vectorsOfSizeOneLess = generateAllVectorsMod2OfSize(n - 1);
            for (int i = 0; i < vectorsOfSizeOneLess.length; i++) {
                result[i] = IntStream
                        .concat(Arrays.stream(new int[]{0}), Arrays.stream(vectorsOfSizeOneLess[i])).toArray();
                result[i + vectorsOfSizeOneLess.length] = IntStream
                        .concat(Arrays.stream(new int[]{1}), Arrays.stream(vectorsOfSizeOneLess[i])).toArray();
            }
        }
        return result;
    }

    /**
     * Transforms self to echelon form, using the same transformations on the vector y.
     *
     * @param y vector to be transformed in the same way as the matrix
     */
    private void toEchelonForm(int[] y) {
        int m = y.length;
        for (int i = 0; i < m; i++) { // Gaussian substitution in
            if (elements[i][i] != 0) {
                for (int j = i + 1; j < m; j++) {
                    if (elements[j][i] == 1) {
                        elements[j] = addRows(elements[i], elements[j]);
                        y[j] = (y[i] + y[j]) % 2;
                    }
                }
            } else {
                for (int j = i + 1; j < m; j++) {
                    if (elements[j][i] == 1) {
                        // swap rows
                        int[] tempRow = elements[i];
                        elements[i] = elements[j];
                        elements[j] = tempRow;

                        int tempElement = y[i];
                        y[i] = y[j];
                        y[j] = tempElement;

                        i--; // redo this column with rows swapped
                        break;
                    }
                }
            }
        }
    }

    private boolean isSolvableFor(int[] y) {
        for (int i = y.length - 1; i > 0; i--) {
            if (elements[i][i] == 0) {
                if (y[i] != 0) {
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    private int getReverseSubstitutionStartRowNumber(int[] y) {
        int substitutionStartRow = y.length - 1;
        for (int i = y.length - 1; i > 0; i--) {
            if (elements[i][i] != 0) {
                return i;
            }
        }
        return substitutionStartRow;
    }

    private int[] reverseSubstitution(int[] y, int substitutionStartRowNum) {
        for (int i = substitutionStartRowNum; i >= 0; i--) {
            int sum = 0;
            for (int j = i; j < y.length; j++) {
                sum += elements[i][j] * y[j];
            }
            y[i] = sum % 2;
        }
        return y;
    }

    private int[] addRows(int[] row1, int[] row2) {
        int[] newRow = new int[row1.length];
        for (int i = 0; i < row1.length; i++) {
            newRow[i] = (row1[i] + row2[i]) % 2;
        }
        return newRow;
    }

}
