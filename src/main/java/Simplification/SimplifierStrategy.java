package Simplification;

import CAS.Equation;

/**
 * Created by Jack Roper on 8/29/2017.
 */
public abstract class SimplifierStrategy {
    public abstract SimplifierResult simplify(Equation eq) throws SimplifyObjectiveNotDoneException;
    public Equation getResult(Equation eq) throws SimplifyObjectiveNotDoneException {
        return this.simplify(eq).getResult();
    }
}
