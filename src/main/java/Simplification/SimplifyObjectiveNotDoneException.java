package Simplification;

import CAS.Equation;

/**
 * Created by Jack Roper on 9/1/2017.
 */
public class SimplifyObjectiveNotDoneException extends Exception {
    public final SimplifierStrategy strategy;
    public final Equation equation;
    public SimplifyObjectiveNotDoneException(SimplifierStrategy strategy, Equation equation){
        super("Equation could not be simplified!");
        this.strategy = strategy;
        this.equation = equation;
    }

    @Override
    public String getMessage(){
        return "Equation: " + equation.toString() + " could not be simplified using strategy: " + this.strategy + ".";
    }
}
