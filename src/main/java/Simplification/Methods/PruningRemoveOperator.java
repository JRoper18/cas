package Simplification.Methods;

/**
 * Created by Jack Roper on 9/3/2017.
 */

import CAS.Equation;
import Simplification.SimplifierResult;
import Simplification.SimplifierStrategy;
import Simplification.SimplifyObjectiveNotDoneException;

/**
 * This strategy attempts to completely remove an operator from the equation by
 */
public class PruningRemoveOperator implements SimplifierStrategy {
    public int maxTries;
    public PruningRemoveOperator(int maxTries){
        this.maxTries = maxTries;
    }
    public PruningRemoveOperator(){
        this.maxTries = 10;
    }
    public SimplifierResult simplify(Equation eq) throws SimplifyObjectiveNotDoneException {
        
        throw new SimplifyObjectiveNotDoneException(this);
    }

}
