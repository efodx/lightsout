package lightsout.services;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import lightsout.dtos.ProblemDTO;
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
public class ProblemsServiceTest {
    @Inject
    ProblemsService problemsService;
    @Inject
    PlayersService playersService;
    private final String storedPlayerUsername = "player1";

    @BeforeEach
    public void setup() {
        // Add a player, so that we have one in the database
        // we are using Before/After Each, as the AfterAll version is unable to remove the player, due to the loss of context.
        playersService.createPlayer(storedPlayerUsername, 12);
    }

    @AfterEach
    public void cleanUp() {
        playersService.removePlayer(storedPlayerUsername);
    }

    @Test
    public void testGetProblems() {
        Assertions.assertEquals(new ArrayList<ProblemDTO>(), problemsService.getProblems());
    }

    @Test
    @TestTransaction
    public void testAddProblem() throws UnsolvableException {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0, 0));
        grid.add(Arrays.asList(0, 1, 0));
        grid.add(Arrays.asList(0, 0, 0));

        problemsService.addProblem(grid, storedPlayerUsername);

        List<ProblemDTO> problemDTOS = problemsService.getProblems();
        Assertions.assertEquals(1, problemsService.getProblems().size());

        ProblemDTO storedProblem = problemDTOS.get(0);

        Assertions.assertEquals(grid, storedProblem.getGrid());
        Assertions.assertEquals(storedPlayerUsername, storedProblem.getCreatedByUsername());
    }

    @Test
    public void testAddProblemWithGridBadFormatThrowsException1() {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0, 0));
        grid.add(Arrays.asList(0, 0, 2));
        grid.add(Arrays.asList(0, 0, 0));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                problemsService.addProblem(grid, storedPlayerUsername));
        Assertions.assertEquals(0, problemsService.getProblems().size());

    }

    @Test
    public void testAddProblemWithGridBadFormatThrowsException2() {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0, 0));
        grid.add(Arrays.asList(0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                problemsService.addProblem(grid, storedPlayerUsername));
        Assertions.assertEquals(0, problemsService.getProblems().size());
    }

    @Test
    public void testAddProblemWithGridBadFormatThrowsException3() {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 1, 1));
        grid.add(Arrays.asList(0, 0, 0, 1));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                problemsService.addProblem(grid, storedPlayerUsername));
        Assertions.assertEquals(0, problemsService.getProblems().size());
    }

    @Test
    public void testAddProblemWithGridTooSmallThrowsException() {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0));
        grid.add(Arrays.asList(0, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                problemsService.addProblem(grid, storedPlayerUsername));
        Assertions.assertEquals(0, problemsService.getProblems().size());
    }

    @Test
    public void testAddProblemWithGridTooBigThrowsException() {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 1));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                problemsService.addProblem(grid, storedPlayerUsername));
        Assertions.assertEquals(0, problemsService.getProblems().size());
    }

    @Test
    public void testAddProblemWithUnsolvableProblemThrowsException() {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0, 1, 0));
        grid.add(Arrays.asList(1, 0, 0, 0));
        grid.add(Arrays.asList(0, 0, 0, 1));
        grid.add(Arrays.asList(0, 1, 0, 0));

        Assertions.assertThrows(UnsolvableException.class, () -> problemsService.addProblem(grid, storedPlayerUsername));
        Assertions.assertEquals(0, problemsService.getProblems().size());
    }


    @Test
    @TestTransaction
    public void testGetProblemsByCreatedUsername() throws UnsolvableException {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0, 0));
        grid.add(Arrays.asList(1, 0, 0));
        grid.add(Arrays.asList(0, 0, 0));

        ProblemDTO problemDTO = problemsService.addProblem(grid, storedPlayerUsername);
        ProblemDTO problemDTO2 = problemsService.addProblem(grid, storedPlayerUsername);

        List<ProblemDTO> problemDTOS = problemsService.getProblemsCreatedBy(storedPlayerUsername);

        Assertions.assertTrue(problemDTOS.contains(problemDTO));
        Assertions.assertTrue(problemDTOS.contains(problemDTO2));
    }

    @Test
    @TestTransaction
    public void testGetProblemById() throws UnsolvableException {
        List<List<Integer>> grid = new ArrayList<>();
        grid.add(Arrays.asList(0, 0, 1));
        grid.add(Arrays.asList(0, 0, 0));
        grid.add(Arrays.asList(0, 0, 0));

        ProblemDTO problemDTO = problemsService.addProblem(grid, storedPlayerUsername);
        ProblemDTO storedProblem = problemsService.getProblemById(problemDTO.getId());

        Assertions.assertEquals(problemDTO.getGrid(), storedProblem.getGrid());
        Assertions.assertEquals(problemDTO.getCreatedByUsername(), storedProblem.getCreatedByUsername());
        Assertions.assertEquals(problemDTO.getId(), storedProblem.getId());
    }

}
