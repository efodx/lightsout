package lightsout.utilities.solutionchecker;

public class SolutionChecker {

    public void checkSolutionIsCorrect(int[] problem, int[] solution) throws NotASolutionException {
        GameSimulator gameSimulator = new GameSimulator(problem);
        int n = (int) Math.floor(Math.sqrt(problem.length));
        for (int i : solution) {
            int pressRow = i / n;
            int pressColumn = i % n;
            gameSimulator.press(pressRow, pressColumn);
        }
        if (!gameSimulator.isSolved()) {
            throw new NotASolutionException();
        }
    }
}
