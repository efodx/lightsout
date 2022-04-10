package lightsout.services;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import lightsout.dtos.ProblemDTO;
import lightsout.dtos.ProblemSolutionDTO;
import lightsout.utilities.solutionchecker.NotASolutionException;
import lightsout.utilities.solver.UnsolvableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@QuarkusTest
public class SolutionsServiceTest {
    @Inject
    PlayersService playersService;
    @Inject
    ProblemsService problemsService;
    @Inject
    SolutionsService solutionsService;

    private final String storedPlayerUsername = "player1";
    private long problemId;


    @BeforeEach
    public void setup() throws UnsolvableException {
        // Add a player and problem, so that we have them in the database
        // we are using Before/After Each, as the AfterAll version is unable to remove the player/problem, due to the loss of context.
        playersService.createPlayer(storedPlayerUsername, 12);
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(1, 0, 1));
        grid.add(Arrays.asList(0, 0, 0));
        grid.add(Arrays.asList(1, 0, 1));

        ProblemDTO problem = problemsService.addProblem(grid, storedPlayerUsername);
        problemId = problem.getId();
    }

    @AfterEach
    public void cleanUp() {
        problemsService.removeProblem(problemId);
        playersService.removePlayer(storedPlayerUsername);
    }

    @Test
    public void testGetSolutions() {
        Assertions.assertEquals(new ArrayList<ProblemSolutionDTO>(), solutionsService.getSolutions());
    }

    @Test
    @TestTransaction
    public void testAddSolution() throws NotASolutionException {
        int[] solution = new int[]{4, 4, 4, 4, 4, 4, 4};

        solutionsService.addProblemSolution(problemId, solution, storedPlayerUsername);

        Assertions.assertEquals(1, solutionsService.getSolutions().size());
        ProblemSolutionDTO savedSolution = solutionsService.getSolutions().get(0);
        Assertions.assertEquals(problemId, savedSolution.getProblemId());
        Assertions.assertArrayEquals(solution, savedSolution.getSolution());
    }

    @Test
    @TestTransaction
    public void testAddIncorrectSolutionThrowsException() {
        int[] solution = new int[]{4, 4};
        Assertions.assertThrows(NotASolutionException.class, () ->
                solutionsService.addProblemSolution(problemId, solution, storedPlayerUsername));

        Assertions.assertEquals(0, solutionsService.getSolutions().size());
    }

    @Test
    @TestTransaction
    public void testAddSolutionWithWrongProblemIdThrowsException() {
        int[] solution = new int[]{4};
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                solutionsService.addProblemSolution(1337, solution, storedPlayerUsername));

        Assertions.assertEquals(0, solutionsService.getSolutions().size());
    }

    @Test
    @TestTransaction
    public void testAddSolutionWithWrongUsernameThrowsException() {
        int[] solution = new int[]{4};
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                solutionsService.addProblemSolution(problemId, solution, "player2"));

        Assertions.assertEquals(0, solutionsService.getSolutions().size());
    }

    @Test
    @TestTransaction
    public void testGetSolutionsByUser() throws NotASolutionException {
        int[] solution = new int[]{4, 4, 4, 4, 4, 4, 4};
        String anotherUsername = storedPlayerUsername + "2";
        solutionsService.addProblemSolution(problemId, solution, storedPlayerUsername);
        playersService.createPlayer(anotherUsername, 12);
        solutionsService.addProblemSolution(problemId, solution, anotherUsername);

        List<ProblemSolutionDTO> userSolutions = solutionsService.getSolutionsByUser(storedPlayerUsername);

        Assertions.assertEquals(1, userSolutions.size());
        Assertions.assertEquals(storedPlayerUsername, userSolutions.get(0).getSolverUsername());
    }

    @Test
    @TestTransaction
    public void testGetSolutionsForProblem() throws NotASolutionException {
        int[] solution = new int[]{4, 4, 4, 4, 4, 4, 4};
        String anotherUsername = storedPlayerUsername + "2";
        solutionsService.addProblemSolution(problemId, solution, storedPlayerUsername);
        playersService.createPlayer(anotherUsername, 12);
        solutionsService.addProblemSolution(problemId, solution, anotherUsername);

        List<ProblemSolutionDTO> userSolutions = solutionsService.getSolutionsByUser(storedPlayerUsername);

        Assertions.assertEquals(1, userSolutions.size());
        Assertions.assertEquals(storedPlayerUsername, userSolutions.get(0).getSolverUsername());
    }

}
