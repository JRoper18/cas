import EquationObjects.MathObjects.GenericExpression;
import EquationObjects.MathObjects.MathObject;

import java.util.HashMap;

/**
 * Created by jack on 1/2/2017.
 */
public class EquationSub {
    public final DirectOperation operation;
    public final Equation condition;
    private PatternMatcher matcher = new PatternMatcher();
    public EquationSub(Equation before, Equation after){
        this.operation = (eq -> {
            return this.substitute(before, after, eq);
        });
        this.condition = null;
    }
    public EquationSub(Equation before, Equation after, Equation conditions) {
        this.operation = (eq -> {
            return this.substitute(before, after, eq);
        });
        this.condition = conditions;
    }
    public EquationSub(DirectOperation operation){ //I'm hoping I'll only have to ever use this for adding, subtracting, division, and multiplication. Hoping.
        this.operation = operation;
        this.condition = null;
    }
    public Equation apply(Equation equation){
        return this.operation.operate(equation);
    }
    private Equation substitute(Equation before, Equation after, Equation equation){
        if(matcher.patternMatch(equation, before)) {
            Equation newEquation = new Equation(after); //Quick clone
            HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
            //Go through conditions
            if(this.condition != null){
                Equation temp = condition.clone();
                //Replace generics
                for(String var : values.keySet()){
                    Tree<MathObject> substitution = values.get(var);
                    GenericExpression genExToLookFor = new GenericExpression(var);
                    temp.tree.replaceAll(new Tree(genExToLookFor), substitution);
                }
                //If there's still generics in the equation, the condition fails.
                if(temp.tree.containsClass(GenericExpression.class)){
                    return equation;
                }
                //No generics. Try evaluating it.
            }
            for (String var : values.keySet()) {
                Tree<MathObject> substitution = values.get(var);
                GenericExpression genExToLookFor = new GenericExpression(var);
                newEquation.tree.replaceAll(new Tree(genExToLookFor), substitution);
            }
            return newEquation;
        }
        return equation;
    }
    public Equation applyEverywhere(Equation equation){
        if(equation.tree.getNumberOfChildren() == 0){
            return this.apply(equation);
        }
        else{
            for(Tree<MathObject> childTree : equation.tree.getChildren()){
                childTree.replaceWith(this.applyEverywhere(new Equation(childTree)).tree);
            }
            return this.apply(equation);
        }
    }
}
