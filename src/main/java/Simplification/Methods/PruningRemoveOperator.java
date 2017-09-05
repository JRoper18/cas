package Simplification.Methods;

/**
 * Created by Jack Roper on 9/3/2017.
 */

import CAS.Equation;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import Database.ConfigData;
import Database.DatabaseConnection;
import Database.SubSerializer;
import Simplification.*;
import Substitution.EquationSub;
import Util.Tree;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This strategy attempts to completely remove an operator from the equation by
 */
public class PruningRemoveOperator extends RemoveAllOperatorsStrategy {
    public int maxSteps;
    public PruningRemoveOperator(int maxSteps){
        this.maxSteps = maxSteps;
    }
    public PruningRemoveOperator(){
        this.maxSteps = 10;
    }
    public SimplifierResult simplify(Equation eq) throws SimplifyObjectiveNotDoneException {
        SimplifierResult result = new SimplifierResult(eq);
        List<LinkedList<Integer>> paths;
        while (result.stepsTaken.size() < this.maxSteps && !isSimplifyDone(eq, result.getResult())){
            Equation latest = result.getResult().clone();
            paths = latest.tree.findPaths(eq.getRoot());
            Equation subEq = new Equation(latest.tree.getChildThroughPath(paths.get(0)));
            SimplifierResult subResult = Simplifier.removeSingleRootOperator.simplify(subEq);
            subResult.setPath(paths.get(0));
            result.addSteps(subResult.stepsTaken);
        }
        return result;
    }

}
