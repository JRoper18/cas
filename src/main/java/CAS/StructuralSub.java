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
    public Equation before;
    public Equation after;
    public Equation condition;
    public StructuralSub(String before, String after){
        this(new Equation(before), new Equation(after));
    }
    public StructuralSub(Equation before, Equation after, Equation condition){
        super((DirectOperation & Serializable) ( equation -> {
            PatternMatcher matcher = new PatternMatcher();
            if(matcher.patternMatch(equation, before)) {
                Equation newEquation = new Equation(after); //Quick clone
                HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
                //Go through conditions
                Cloner cloner = new Cloner();
                Equation temp = cloner.deepClone(condition);
                //Replace generics
                for(String var : values.keySet()){
                    Tree<MathObject> substitution = values.get(var);
                    GenericExpression genExToLookFor = new GenericExpression(var);
                    temp.tree.replaceAll(new Tree(genExToLookFor), substitution);
                }
                Equation simplified = Simplifier.simplify(temp);
                if(simplified.equals(new Equation("FALSE"))){
                    return equation; //Again, do nothing to the equation.
                }
                for (String var : values.keySet()) {
                    Tree<MathObject> substitution = values.get(var);
                    GenericExpression genExToLookFor = new GenericExpression(var);
                    newEquation.tree.replaceAll(new Tree(genExToLookFor), substitution);
                }
                return newEquation;
            }
            else {
                //Can we change the equation to Fix the before algorithm?
            }
            return equation;
        }), getProbableAssignedOperator(before));
        this.before = before;
        this.after = after;
        this.condition = condition;
    }
    public StructuralSub(Equation before, Equation after){
        super((DirectOperation & Serializable) ( equation -> {
            PatternMatcher matcher = new PatternMatcher();
            if(matcher.patternMatch(equation, before)) {
                Equation newEquation = new Equation(after); //Quick clone
                HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
                //Go through conditions
                for (String var : values.keySet()) {
                    Tree<MathObject> substitution = values.get(var);
                    GenericExpression genExToLookFor = new GenericExpression(var);
                    newEquation.tree.replaceAll(new Tree(genExToLookFor), substitution);
                }
                return Simplifier.simplifyWithMetaFunction(newEquation, MathOperator.AUTOSIMPLIFY);
            }
            else {
                //Can we change the equation to Fix the before algorithm?
            }
            return equation;
        }), getProbableAssignedOperator(before));
        this.before = before;
        this.after = after;
        this.condition = null;
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
}
