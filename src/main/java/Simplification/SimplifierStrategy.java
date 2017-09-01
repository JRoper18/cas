package Simplification;

import CAS.Equation;

/**
 * Created by Jack Roper on 8/29/2017.
 */
public interface SimplifierStrategy {
    public SimplifierResult simplify(Equation eq) throws SimplifyObjectiveNotDoneException;
}
