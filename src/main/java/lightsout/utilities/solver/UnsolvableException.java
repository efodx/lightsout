package lightsout.utilities.solver;

public class UnsolvableException extends Exception {
    public UnsolvableException(){
        super("The problem provided is unsolvable.");
    }
}
