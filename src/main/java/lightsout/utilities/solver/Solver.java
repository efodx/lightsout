package lightsout.utilities.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for solving the lights out problem on a nxn matrix.
 * It transforms the problem into an equivalent system of equations in modulo2
 * and solves the system if the system is solvable.
 */
public class Solver {

    /**
     * Solves the lights out problem on a nxn matrix.
     *
     * @param problem grid reshaped(by rows) into a vector of length n^2
     * @return solution to the problem
     * @throws UnsolvableException      if problem is unsolvable
     * @throws IllegalArgumentException if problem of incorrect length
     */
    public int[] solve(int[] problem) throws UnsolvableException {
        double m = Math.sqrt(problem.length);
        if (m % 1 != 0) {
            throw new IllegalArgumentException("Problem must be of length n^2.");
        }
        int n = (int) m;
        int[] y = Arrays.stream(problem).map(i -> (i + 1) % 2).toArray();// b+Ax =z -> Ax = (z-b) = y
        Mod2Matrix equationsMatrix = new Mod2Matrix(generateEquationsMatrixElements(n));
        int[] solution = equationsMatrix.solveFor(y, true);
        return equationSolutionToPressSolution(solution);
    }

    private int[] equationSolutionToPressSolution(int[] solution) {
        List<Integer> pressSolution = new ArrayList<>();
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                pressSolution.add(i);
            }
        }
        return pressSolution.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Generates the matrix elements representing the system of n^2 equations that the solution
     * to the lights out problem must satisfy.
     *
     * @param n size of the grid
     * @return matrix elements
     */
    private int[][] generateEquationsMatrixElements(int n) {
        int m = n * n;
        int[][] elements = new int[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                int row1 = i / n;
                int column1 = i % n;

                int row2 = j / n;
                int column2 = j % n;

                if (Math.abs(row1 - row2) + Math.abs(column1 - column2) < 2) {
                    elements[i][j] = 1;
                }
            }
        }
        return elements;
    }


}
