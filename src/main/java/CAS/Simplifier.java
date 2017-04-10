package CAS;

import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.EquationObjects.MathOperatorSubtype;
import Database.DatabaseConnection;
import Database.SubSerializer;
import com.rits.cloning.Cloner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by jack on 1/5/2017.
 */
public class Simplifier {
    public static SimplifierResult simplifyWithData(Equation eq, boolean full){
        return simplifyWithData(eq, eq.getRoot(), full);
    }
    public static SimplifierResult simplifyWithData(Equation eq, MathObject operation, boolean full){
        List<EquationSub> steps = new ArrayList<>();
        List<Equation> changes = new ArrayList<>();
        Equation newEq = eq.clone();
        Equation last;
        do{
            last = newEq;
            try{
                ResultSet results = DatabaseConnection.runQuery("select algorithm from subs where (operator == '" + operation + "')");
                while(results.next()){
                    Equation temp = newEq.clone();
                    EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                    newEq = (full)? Simplifier.simplifyWithMetaFunction(tempSub.applyEverywhere(newEq), MathOperator.AUTOSIMPLIFY) : tempSub.apply(newEq);
                    if(!temp.equals(newEq)){
                        steps.add(tempSub);
                        changes.add(newEq);
                    }
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        } while (full==true && !newEq.equals(last));
        Equation finalEq = (full)? Simplifier.simplifyWithMetaFunction(newEq, MathOperator.AUTOSIMPLIFY) : newEq;
        return new SimplifierResult(eq, finalEq, steps, changes);

    }
    public static Equation simplifyByOperator(Equation eq, MathObject operation, boolean full){
        return simplifyWithData(eq, operation, full).result     ;
    }
    public static Equation simplifyByOperator(Equation eq, boolean full){
        return simplifyByOperator(eq, eq.getRoot(), full);
    }
    public static Equation simplifyWithMetaFunction(Equation eq, MathOperator metaFunction){
        if(metaFunction.getSubType() != MathOperatorSubtype.META){
            throw new UncheckedIOException(new IOException("Function provided is not a meta function!"));
        }
        Tree<MathObject> newTree = new Tree<>(new MathObject(metaFunction));
        newTree.addChild(eq.tree);
        return simplifyMetaFunctions(new Equation(newTree));
    }
    public static Equation simplifyByOperator(Equation eq){
        return simplifyByOperator(eq, eq.getRoot(), false);
    }
    public static Equation simplify(Equation eq){
        Cloner cloner = new Cloner();
        Equation newEq = cloner.deepClone(eq);
        //Start from the bottom of the tree to the top.
        if(newEq.tree.hasChildren()){
            for(Tree<MathObject> child : newEq.tree.getChildren()){
                Equation simplified = simplify(new Equation(child));
                child.replaceWith(simplified.tree);
            }
        }
        //Get newEq's root term.
        MathObject root = newEq.getRoot();
        //Find all the substitutions from the database that operate on that term.
        int minComplexity;
        Equation lastIteration;
        do{
            minComplexity = newEq.complexity();
            lastIteration = newEq;
            try{
                ResultSet results = DatabaseConnection.runQuery("select algorithm from subs where (operator == '" + root.toString() + "')");
                while(results.next()){
                    EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                    newEq = tempSub.apply(newEq);
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        } while (!lastIteration.equals(newEq));
        return newEq;
    }
    public static Equation simplifyMetaFunctions(Equation equation){
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
        return recursiveApplyMeta(newEq.tree);
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
    private static Equation recursiveApplyMeta(Tree<MathObject> applyTo){
        if(applyTo.hasChildren()){
            for(Tree<MathObject> child : applyTo.getChildren()){
                child.replaceWith(recursiveApplyMeta(child).tree);
            }
        }
        try{
            Equation newEq = new Equation(applyTo);
            String sql = "select algorithm from subs where (subtype =='META' and operator == '" + applyTo.data.toString() + "')";
            ResultSet results = DatabaseConnection.runQuery(sql);
            while(results.next()){
                EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                newEq = tempSub.apply(newEq);
            }
            return newEq;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
