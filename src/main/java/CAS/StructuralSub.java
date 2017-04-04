package CAS;


import CAS.EquationObjects.GenericExpression;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import com.rits.cloning.Cloner;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by jack on 1/9/2017.
 */
public class StructuralSub extends EquationSub implements Serializable {
    public final Equation before;
    public final Equation after;
    public StructuralSub(String total){
        this(total.split("->")[0], total.split("->")[1]);
    }
    public StructuralSub(String before, String after){
        this(Simplifier.simplifyWithMetaFunction(new Equation(before), MathOperator.AUTOSIMPLIFY), Simplifier.simplifyWithMetaFunction(new Equation(after), MathOperator.AUTOSIMPLIFY));
    }
    public StructuralSub(Equation before, Equation after){
        super((DirectOperation & Serializable) ( equation -> {
            PatternMatcher matcher = new PatternMatcher();
            if(matcher.patternMatch(equation, before)) {
                Equation newEquation = after.clone(); //Quick clone
                HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
                HashMap<String, String> vars = matcher.getLastMatchVariables();
                //Go through conditions
                for (String var : values.keySet()) {
                    Tree<MathObject> substitution = values.get(var);
                    GenericExpression genExToLookFor = new GenericExpression(var);
                    newEquation.tree.replaceAll(new Tree(genExToLookFor), substitution);
                }
                for(String var : vars.keySet()){
                    Tree<MathObject> substitution = new Tree<>(new GenericExpression(vars.get(var)));
                    GenericExpression lookFor = new GenericExpression(var);
                    newEquation.tree.replaceAll(new Tree(lookFor), substitution);

                }
                return Simplifier.simplifyWithMetaFunction(newEquation, MathOperator.AUTOSIMPLIFY);
            }
            else {
                //Can we change the equation to Fix the before algorithm?
            }
            return equation;
        }), getProbableAssignedOperator(before));
        this.before = before.clone();
        this.after = after.clone();
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
