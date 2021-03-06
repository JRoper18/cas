package Substitution;


import CAS.Equation;
import CAS.EquationObjects.GenericExpression;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import Util.Tree;
import PatternMatching.PatternMatchResult;
import PatternMatching.PatternMatcher;
import Simplification.Simplifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by jack on 1/9/2017.
 */
public class StructuralSub extends EquationSub implements Serializable {
    public final Equation before;
    public final Equation after;

    public final List<Equation> conditions;
    public final int operatorCost;

    public StructuralSub(DirectOperation oper, Equation before, Equation after, List<Equation> conditions, int operatorCost){
        super(oper, getProbableAssignedOperator(before));
        this.before = before;
        this.after = after;
        this.conditions = conditions;
        this.operatorCost = operatorCost;
    }
    private static MathObject getProbableAssignedOperator(Equation equation){
        MathObject probableOperator = null;
        if(equation.tree.data.getOperator().toString().contains("PATTERN")){ //Note to future self: Make a way to identify pattern objects from mathobjects.
            //Don't check pattern objects. Check the children for a mathobject.
            for(Tree<MathObject> child : equation.tree.getChildren()){
                MathObject possible = getProbableAssignedOperator(new Equation(child));
                if(probableOperator == null){
                    probableOperator = possible;
                }
                else{
                    if(!probableOperator.equals(possible)){
                        return null; //There's a difference. We can't tell.
                    }
                }
            }
            //Made it to the end with all of the operators the same? Probably that operator.
            return probableOperator;
        }
        else{
            return equation.tree.data;
        }
    }
    public String toString(){
        return (this.before.toString() + " -> " + this.after.toString());
    }
    public boolean equals(Object n){
        if(n instanceof StructuralSub){
            return n.toString().equals(this.toString());
        }
        return false;
    }
}
