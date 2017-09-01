package Simplification;

/**
 * Created by Jack Roper on 9/1/2017.
 */
public class SimplifyObjectiveNotDoneException extends Exception {
    public final SimplifierStrategy strategy;
    public SimplifyObjectiveNotDoneException(SimplifierStrategy strategy){
        super("Equation could not be simplified!");
        this.strategy = strategy;
    }

    @Override
    public String getMessage(){
        return "Equation could not be simplified using strategy: " + this.strategy + ".";
    }
}
