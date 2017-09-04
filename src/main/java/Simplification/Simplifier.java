package Simplification;

import CAS.Equation;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.EquationObjects.MathOperatorSubtype;
import Simplification.Methods.OrderEquationSimplify;
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
        return simplifyMetaFunctions(new Equation(newTree));
    }
    public static Equation simplifyMetaFunctions(Equation equation) {
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
        Equation data = recursiveApplyMeta(newEq);
        return data;
    }
    public static Equation orderEquation(Equation equation){
        return new OrderEquationSimplify().simplify(equation).getResult();
    }
    private static Equation recursiveApplyMeta(Equation applyTo){
        Equation result = applyTo.clone();
        if(result.tree.hasChildren()){
            for(Tree<MathObject> child : applyTo.tree.getChildren()){
                Equation newChild = recursiveApplyMeta(new Equation(child));
                child.replaceWith(newChild.tree);
            }
        }
        try{
            Equation newEq = new Equation(applyTo);
            String sql = "select algorithm from subs where (subtype =='META' and operator == '" + applyTo.getRoot().toString() + "')";
            ResultSet results = DatabaseConnection.runQuery(sql);
            Equation last = newEq.clone();
            while(results.next()){
                EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                result = tempSub.apply(result);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
}
