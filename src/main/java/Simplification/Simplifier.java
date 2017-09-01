package Simplification;

import CAS.Equation;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.EquationObjects.MathOperatorSubtype;
import Util.Tree;
import Database.DatabaseConnection;
import Database.SubSerializer;
import Simplification.Methods.BruteForceRemoveOperator;
import Substitution.DirectOperation;
import Substitution.EquationSub;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.ResultSet;
import java.util.*;


/**
 * Created by jack on 1/5/2017.
 */
public class Simplifier {

    public static SimplifierResult simplify(Equation eq, SimplifierObjective objective) {
        switch (objective) {
            case LEAST_COMPLEX:
                break;
            case REMOVE_META:
                return simplifyMetaFunctions(eq);
            case REMOVE_OPERATOR:
                return simplifyToRemoveFunction(eq);
            case SIMPLIFY_TOP_OPERATOR:
                return simplifyWithOperator(eq);
        }
        return null;
    }
    public static Equation directSimplify(Equation eq, SimplifierObjective objective) {
        return simplify(eq, objective).result;
    }
    private static SimplifierResult simplifyWithOperator(Equation eq){
        MathOperator operator = eq.getRoot().getOperator();
        SimplifierResult result = new SimplifierResult(eq);
        try {
            Equation newEq = eq.clone();
            Equation temp = new Equation("0", 0);
            ResultSet results = DatabaseConnection.runQuery("select algorithm from subs where (operator == '" + operator + "')");
            while (results.next()) {
                EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                applyAndRecordChange(result, temp, newEq, tempSub);
                if(result.subsUsed.size() == 1){
                    //We only do 1 sub
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    private static SimplifierResult simplifyToRemoveFunction(Equation eq) {
        SimplifierStrategy strat = new BruteForceRemoveOperator(3);
        return strat.simplify(eq);
    }
    private int numberOfOperators(Equation eq, MathOperator op){ //Will be used as a hueristic for traversing a graph of all possible alternate forms of our input equation.
        return eq.tree.getNumberOfOccurances(new MathObject(op)); //Note: I'm not actually sure if this is admissible, and I'm using an A* algorithm to traverse simplifications. So, it might
        //not find the optimal solution. Still, better than a brute-force greedy or depth-first search.

        //Note: It's only hueristic if we don't define rules for a function that skip steps. For example, definining that sin*sin + cos^2 = 1 would not make the removal of sin admissable.
        //This is because our hueristic wil return 2 because there's two occurances of sin, but with this transformation will only take 1 transformation/step. So it's sometimes admissable. 
    }

    /**
     * Applys a meta function to the given equation. Equivalent to new Equation("meta(" + oldEq + ")");
     * @param eq The equation that the meta function will be applied to.
     * @param metaFunction The Meta Function to be applied.
     * @return The equation with the meta function applied.
     */
    public static Equation simplifyWithMetaFunction(Equation eq, MathOperator metaFunction){
        if(metaFunction.getSubType() != MathOperatorSubtype.META){
            throw new UncheckedIOException(new IOException("Function provided is not a meta function!"));
        }
        Tree<MathObject> newTree = new Tree<>(new MathObject(metaFunction));
        newTree.addChild(eq.tree);
        return simplifyMetaFunctions(new Equation(newTree)).result;
    }
    private static SimplifierResult simplifyMetaFunctions(Equation equation) {
        EquationSub sub = new EquationSub((DirectOperation) eq -> {
            if (eq.isType(MathOperator.DIVIDE)) {
                return new Equation("TIMES(" + eq.getSubEquation(0) + ", POWER(" + eq.getSubEquation(1) + ", -1))", 0);
            }
            if (eq.isType(MathOperator.SUBTRACT)) {
                return new Equation("ADD(" + eq.getSubEquation(0) + ", TIMES(" + eq.getSubEquation(1) + ", -1))", 0);
            }
            return eq;
        });
        Equation newEq = sub.applyEverywhere(equation);
        SimplifierResult data = recursiveApplyMeta(newEq);
        data.changes.add(0, equation);
        data.subsUsed.add(0, sub);
        return data;
    }
    public static Equation orderEquation(Equation equation){
        Tree<MathObject> eqTree = equation.tree.clone();
        List<Equation> newChildren = equation.getOperands();
        if(eqTree.hasChildren()){
            newChildren.clear();
            for(int i = 0; i<eqTree.getNumberOfChildren(); i++){
                Equation child = equation.getSubEquation(i);
                newChildren.add(orderEquation(child));
            }
        }
        if(!eqTree.data.isOrdered()){
            Collections.sort(newChildren);
        }
        List<Tree<MathObject>> newOperands = new ArrayList<>();
        for(Equation newChild : newChildren){
            newOperands.add(newChild.tree);
        }
        eqTree.setChildren(newOperands);
        return new Equation(eqTree);
    }
    private static SimplifierResult recursiveApplyMeta(Equation applyTo){
        SimplifierResult result = new SimplifierResult(applyTo);
        result.result = applyTo.clone();
        if(applyTo.tree.hasChildren()){
            for(Tree<MathObject> child : applyTo.tree.getChildren()){
                SimplifierResult data = recursiveApplyMeta(new Equation(child));
                child.replaceWith(data.result.tree);
            }
        }
        try{
            Equation newEq = new Equation(applyTo);
            String sql = "select algorithm from subs where (subtype =='META' and operator == '" + applyTo.getRoot().toString() + "')";
            ResultSet results = DatabaseConnection.runQuery(sql);
            Equation last = newEq.clone();
            while(results.next()){
                EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                applyAndRecordChange(result, last, newEq, tempSub);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
    private static void applyAndRecordChange(SimplifierResult result, Equation old, Equation current, EquationSub sub){
        old = current.clone();
        current = sub.apply(current);
        if(!old.equals(current)) {
            result.subsUsed.add(sub);
            result.changes.add(current);
            result.result = current;
        }
    }
}
