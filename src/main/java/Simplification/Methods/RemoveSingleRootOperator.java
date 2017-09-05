package Simplification.Methods;

import CAS.Equation;
import CAS.EquationObjects.MathOperator;
import Database.DatabaseConnection;
import Database.SubSerializer;
import Simplification.*;
import Substitution.EquationSub;
import Util.Tree;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by Jack Roper on 9/2/2017.
 */

/**
 * This strategy only removes the root operator from the top level of the equation. If we start with TIMES(2, 3) and go to PLUS(TIMES(1, 2), 4), then it would be ok since TIMES is no longer the root.
 */
public class RemoveSingleRootOperator extends SimplifierStrategy{
    public final boolean autoSimplifyAfter;
    public RemoveSingleRootOperator(boolean autoSimplifyAfter){
        this.autoSimplifyAfter = autoSimplifyAfter;
    }
    public RemoveSingleRootOperator(){
        this.autoSimplifyAfter = true;
    }
    public boolean isSimplifyDone(Equation begin, Equation current){
        return current.getRoot() != begin.getRoot();
    }
    public SimplifierResult simplify(Equation eq) throws SimplifyObjectiveNotDoneException{
        MathOperator operator = eq.getRoot().getOperator();
        try {
            Equation newEq = eq.clone();
            ResultSet results = DatabaseConnection.runQuery("select algorithm from subs where (operator == '" + operator + "')");
            while (results.next()) {
                EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                Equation temp = tempSub.apply(newEq);
                if(this.autoSimplifyAfter){
                    temp = Simplifier.simplifyWithMetaFunction(temp, MathOperator.AUTOSIMPLIFY);
                }
                if(temp.tree.data.getOperator() != operator){
                    LinkedList<SubstitutionData> steps = new LinkedList<>();
                    steps.add(new SubstitutionData(null, eq));
                    steps.add(new SubstitutionData(tempSub, temp));
                    return new SimplifierResult(steps);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new SimplifyObjectiveNotDoneException(this, eq);
    }
}
