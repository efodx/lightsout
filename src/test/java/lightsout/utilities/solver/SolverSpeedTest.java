package lightsout.utilities.solver;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Random;

public class SolverSpeedTest {

    Random random = new Random();

    @Test
    void testSolverSpeed() {
        long startTime = Instant.now().getEpochSecond();
        Solver solver = new Solver();
        int numberOfProblems = 100000;
        for (int n = 3; n <= 8; n++) {
            int unsolvableCounter = 0;
            int[][] problems = generateRandomProblems(n, numberOfProblems);
            for (int[] problem : problems) {
                try {
                    int[] solution = solver.solve(problem);
                } catch (UnsolvableException e) {
                    unsolvableCounter++;
                }
            }
            long endTime = Instant.now().getEpochSecond() - startTime;
            System.out.println("Took me " + endTime + " seconds to solve " + numberOfProblems + " of size " + n + "x" + n);
            System.out.println("Encountered " + unsolvableCounter + " unsolvable systems.");
        }
    }

    private int[][] generateRandomProblems(int n, int numberOfProblems) {
        int[][] problems = new int[numberOfProblems][n];
        for (int i = 0; i < numberOfProblems; i++) {
            problems[i] = generateRandomProblem(n);
        }
        return problems;
    }

    private int[] generateRandomProblem(int n) {
        int[] problem = new int[n * n];
        for (int i = 0; i < n * n; i++) {
            if (random.nextBoolean()) {
                problem[i] = 1;
            }
        }
        return problem;
    }

}
