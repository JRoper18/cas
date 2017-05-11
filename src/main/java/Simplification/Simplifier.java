package Simplification;

import CAS.Equation;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.EquationObjects.MathOperatorSubtype;
import CAS.Tree;
import Database.ConfigData;
import Database.DatabaseConnection;
import Database.SubSerializer;
import Substitution.DirectOperation;
import Substitution.EquationSub;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
        try{
            ResultSet results = DatabaseConnection.runQuery("select algorithm from subs where (operator == '" + operator + "')");
            Equation newEq = eq.clone();
            while(results.next()){
                Equation temp = newEq.clone();
                EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                newEq = tempSub.apply(newEq);
                applyAndRecordChange(result, temp, newEq, tempSub);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
    private static SimplifierResult simplifyToRemoveFunction(Equation eq){
        SimplifierResult result = new SimplifierResult(eq);
        boolean overflow = false;
        Equation newEq = eq.clone();
        Equation last;
        do{
            last = newEq.clone();
            result.combine(simplifyWithOperator(eq));
            overflow = (result.subsUsed.size() > ConfigData.simplifyOverflowLimit);
            newEq = result.result;

        } while (!newEq.equals(last) && !overflow);
        result.result = Simplifier.simplifyWithMetaFunction(newEq, MathOperator.AUTOSIMPLIFY);
        return result;

    }

    /**
     * Applys a meta function to the given equation. Equivalent to new Equation("meta(" + oldEq + ")");
     * @param eq The equation that the meta function will be applied to.
     * @param metaFunction The Meta Fcuntion to be applied.
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
    private static SimplifierResult simplifyMetaFunctions(Equation equation){
        EquationSub sub = new EquationSub((DirectOperation) eq -> {
            if(eq.isType(MathOperator.DIVIDE)){
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
        if(applyTo.tree.hasChildren()){
            for(Tree<MathObject> child : applyTo.tree.getChildren()){
                child.replaceWith(recursiveApplyMeta(new Equation(child)).result.tree);
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
