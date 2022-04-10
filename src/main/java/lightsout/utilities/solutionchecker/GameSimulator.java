package lightsout.utilities.solutionchecker;

import java.util.Arrays;

public class GameSimulator {
    private int[][] grid;

    public GameSimulator(int[] vectorizedGrid) {
        int m = vectorizedGrid.length;
        int n = (int) Math.floor(Math.sqrt(m));
        if (n != Math.sqrt(m)) {
            throw new IllegalArgumentException("Vectorized Grid's length should be a perfect square.");
        }
        this.grid = new int[n][n];
        for (int i = 0; i < m; i++) {
            this.grid[i / n][i % n] = vectorizedGrid[i];
        }
    }

    public void press(int i, int j) {
        grid[i][j] = (grid[i][j] + 1) % 2;
        if (i - 1 >= 0) {
            grid[i - 1][j] = (grid[i - 1][j] + 1) % 2;
        }
        if (j - 1 >= 0) {
            grid[i][j - 1] = (grid[i][j - 1] + 1) % 2;
        }
        if (i + 1 < grid.length) {
            grid[i + 1][j] = (grid[i + 1][j] + 1) % 2;
        }
        if (j + 1 < grid.length) {
            grid[i][j + 1] = (grid[i][j + 1] + 1) % 2;
        }
    }

    public boolean isSolved() {
        int n = grid.length;
        return Arrays.stream(this.grid).allMatch(row -> Arrays.stream(row).sum() == n);
    }

    public void repr() {
        for (int[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }

}
