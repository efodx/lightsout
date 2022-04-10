package lightsout.services;

import io.quarkus.logging.Log;
import lightsout.dtos.ProblemDTO;
import lightsout.models.Player;
import lightsout.models.Problem;
import lightsout.utilities.solver.Solver;
import lightsout.utilities.solver.UnsolvableException;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for storing and retrieving lights out problems.
 */
@ApplicationScoped
public class ProblemsService {
    @Inject
    EntityManager em;

    private final Solver solver = new Solver();

    /**
     * @return all problems
     */
    public List<ProblemDTO> getProblems() {
        Query query = em.createQuery("SELECT p FROM Problem p", Problem.class);
        return ((List<Problem>) query.getResultList()).stream()
                .map(p -> new ProblemDTO(p.getId(), vectorToGrid(p.getGrid()), p.getCreatedBy().getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * @param username creator's username
     * @return all problems created by creator with given username
     */
    public List<ProblemDTO> getProblemsCreatedBy(String username) {
        Query query = em.createQuery("SELECT p FROM Problem p WHERE p.createdBy.username = ?1", Problem.class);
        query.setParameter(1, username);
        return ((List<Problem>) query.getResultList()).stream()
                .map(p -> new ProblemDTO(p.getId(), vectorToGrid(p.getGrid()), p.getCreatedBy().getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * @param id problem id
     * @return problem with given id or null if the problem wasn't found
     */
    public ProblemDTO getProblemById(long id) {
        Query query = em.createQuery("SELECT p FROM Problem p WHERE p.id = ?1", Problem.class);
        query.setParameter(1, id);
        List<Problem> resultList = query.getResultList();
        return resultList.stream().map(p -> new ProblemDTO(p.getId(), vectorToGrid(p.getGrid()), p.getCreatedBy().getUsername()))
                .findFirst().orElse(null);
    }

    /**
     * Adds a new problem to the database.
     *
     * @param grid              the problem grid
     * @param createdByUsername username of the creator of the problem
     * @return persisted problem
     * @throws UnsolvableException      if the problem is unsolvable
     * @throws IllegalArgumentException if the grid is not made of 0s and 1s,  is not a square, or user doesn't exist
     */
    @Transactional
    public ProblemDTO addProblem(List<List<Integer>> grid, String createdByUsername) throws UnsolvableException {
        if (!grid.stream().allMatch(row -> row.stream().allMatch(el -> el.equals(0) || el.equals(1)))) {
            throw new IllegalArgumentException("Grid must be made of 0s and 1s only.");
        }
        int n = grid.get(0).size();
        if (!grid.stream().allMatch(row -> row.size() == n) || n < 3 || n > 8 || n != grid.size()) {
            throw new IllegalArgumentException("Grid must be of square size with size between 3 and 8 (both inclusive).");
        }
        Player player = getPlayerByUsername(createdByUsername);
        if (player == null) {
            throw new IllegalArgumentException("Player with username does not exist.");
        }

        int[] vectorizedGrid = gridToVector(grid);
        long startTimeInNanoSeconds = System.nanoTime();
        int[] solution = solver.solve(vectorizedGrid);
        long duration = System.nanoTime() - startTimeInNanoSeconds;
        double durationInMilliseconds = ((double) duration) / 1000000;
        Log.log(Logger.Level.INFO, "The problem was solved in " + durationInMilliseconds + " ms with " + solution.length + " steps.");

        Problem problem = new Problem();
        problem.setGrid(Arrays.stream(vectorizedGrid).boxed().collect(Collectors.toList()));
        problem.setCreatedBy(player);

        em.persist(problem);
        em.flush();
        return new ProblemDTO(problem.getId(), grid, problem.getCreatedBy().getUsername());
    }

    /**
     * Removes the problem with id.
     * Action is idempotent and gives no feedback on whether anything was removed.
     *
     * @param id id of problem to be removed
     */
    @Transactional
    public void removeProblem(long id) {
        Problem problem = em.find(Problem.class, id);
        if (problem != null) {
            em.remove(problem);
        }
    }

    private Player getPlayerByUsername(String username) {
        Query query = em.createQuery("SELECT p FROM Player p WHERE p.username = ?1");
        query.setParameter(1, username);
        return ((List<Player>) query.getResultList()).stream()
                .findFirst().orElse(null);
    }

    private int[] gridToVector(List<List<Integer>> grid) {
        int n = grid.get(0).size();
        int m = n * n;
        int[] vectorized = new int[m];
        for (int i = 0; i < m; i++) {
            vectorized[i] = grid.get(i / n).get(i % n);
        }
        return vectorized;
    }

    private List<List<Integer>> vectorToGrid(int[] vector) {
        int m = vector.length;
        int n = (int) Math.floor(Math.sqrt(m));
        List<List<Integer>> grid = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Integer> gridRow = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                gridRow.add(vector[i * n + j]);
            }
            grid.add(gridRow);
        }
        return grid;
    }

    private List<List<Integer>> vectorToGrid(List<Integer> vector) {
        return vectorToGrid(vector.stream().mapToInt(i -> i).toArray());
    }
}
