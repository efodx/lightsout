package lightsout.services;

import lightsout.dtos.ProblemSolutionDTO;
import lightsout.models.Player;
import lightsout.models.Problem;
import lightsout.models.Solution;
import lightsout.models.SolutionStep;
import lightsout.utilities.solutionchecker.NotASolutionException;
import lightsout.utilities.solutionchecker.SolutionChecker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for storing and retrieving lights out solutions by players.
 */
@ApplicationScoped
public class SolutionsService {
    @Inject
    EntityManager em;

    private final SolutionChecker solutionChecker = new SolutionChecker();

    /**
     * @return all solutions
     */
    public List<ProblemSolutionDTO> getSolutions() {
        Query query = em.createQuery("SELECT step FROM SolutionStep step", SolutionStep.class);
        return solutionStepListToProblemSolutionDTOList((List<SolutionStep>) query.getResultList());

    }

    /**
     * Get all solutions from a user.
     *
     * @param solverUsername id of the desired user
     * @return List of all solutions added by desired user
     */
    public List<ProblemSolutionDTO> getSolutionsByUser(String solverUsername) {
        Query query = em.createQuery("SELECT step FROM SolutionStep step WHERE step.solution.solvedBy.username = ?1", SolutionStep.class);
        query.setParameter(1, solverUsername);
        return solutionStepListToProblemSolutionDTOList((List<SolutionStep>) query.getResultList());
    }

    /**
     * Get all solutions for a given problem.
     *
     * @param problemId id of the problem
     * @return List of all solutions
     */
    public List<ProblemSolutionDTO> getSolutionsForProblem(long problemId) {
        Query query = em.createQuery("SELECT step FROM SolutionStep step WHERE step.solution.problem.id=?1", SolutionStep.class);
        query.setParameter(1, problemId);
        return solutionStepListToProblemSolutionDTOList((List<SolutionStep>) query.getResultList());
    }

    /**
     * Adds a solution to a problem to the database.
     *
     * @param problemId      id of the problem the solution is for
     * @param solution       solution to the problem, represented as an array of the field numbers to press.
     * @param solverUsername username of the solver
     * @throws NotASolutionException    if the provided solution is not a real solution to the problem
     * @throws IllegalArgumentException if the problem id or solverUsername are wrong
     */
    @Transactional
    public void addProblemSolution(long problemId, int[] solution, String solverUsername) throws NotASolutionException {
        Player player = getPlayerWithUsername(solverUsername);
        if (player == null) {
            throw new IllegalArgumentException("Player with given username doesn't exist.");
        }
        Problem problem = getProblemById(problemId);
        if (problem == null) {
            throw new IllegalArgumentException("Problem with given ID doesn't exist.");
        }
        solutionChecker.checkSolutionIsCorrect(problem.getGrid().stream().mapToInt(i -> i).toArray(), solution);

        Solution solutionEntity = new Solution();
        solutionEntity.setProblem(problem);
        solutionEntity.setSolvedBy(player);
        em.persist(solutionEntity);
        for (int i = 0; i < solution.length; i++) {
            SolutionStep solutionStep = new SolutionStep();
            solutionStep.setSolution(solutionEntity);
            solutionStep.setStep(solution[i]);
            solutionStep.setStepNum(i);
            em.persist(solutionStep);
        }
    }

    private Player getPlayerWithUsername(String username) {
        Query query = em.createQuery("SELECT p FROM Player p WHERE p.username = ?1");
        query.setParameter(1, username);
        return ((List<Player>) query.getResultList()).stream()
                .findFirst().orElse(null);
    }

    private Problem getProblemById(long id) {
        Query query = em.createQuery("SELECT p FROM Problem p WHERE p.id = ?1", Problem.class);
        query.setParameter(1, id);
        List<Problem> resultList = query.getResultList();
        return resultList.stream().findFirst().orElse(null);
    }

    private List<ProblemSolutionDTO> solutionStepListToProblemSolutionDTOList(List<SolutionStep> solutionSteps) {
        Map<Solution, List<SolutionStep>> grouped = solutionSteps.stream()
                .collect(Collectors.groupingBy(SolutionStep::getSolution));
        List<ProblemSolutionDTO> solutions = new ArrayList<>();
        for (Map.Entry<Solution, List<SolutionStep>> entry : grouped.entrySet()) {
            int[] solutionStepsArr = entry.getValue().stream()
                    .sorted(Comparator.comparingInt(SolutionStep::getStepNum))
                    .mapToInt(SolutionStep::getStep).toArray();
            Solution solutionEntity = entry.getKey();
            ProblemSolutionDTO dto = new ProblemSolutionDTO(solutionEntity.getProblem().getId(), solutionEntity.getProblem().getCreatedBy().getUsername(), solutionStepsArr);
            solutions.add(dto);
        }
        return solutions;
    }
}
