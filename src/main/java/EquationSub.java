import EquationObjects.MathObjects.GenericExpression;
import EquationObjects.MathObjects.MathObject;

import java.util.HashMap;

/**
 * Created by jack on 1/2/2017.
 */
public class EquationSub {
    public final Equation before;
    public final Equation after;
    public final EquationCondition[] conditions;
    private PatternMatcher matcher = new PatternMatcher();
    public EquationSub(Equation before, Equation after){
        this.before = before;
        this.after = after;
        this.conditions = null;
    }
    public EquationSub(String before, String after){
        this.before = new Equation(before);
        this.after = new Equation(after);
        this.conditions = null;
    }
    public EquationSub(Equation before, String after){
        this.before = before;
        this.after = new Equation(after);
        this.conditions = null;
    }
    public EquationSub(Equation before, Equation after, EquationCondition[] conditions) {
        this.before = before;
        this.after = after;
        this.conditions = conditions;
    }
    public Equation apply(Equation equation){
        if(matcher.patternMatch(equation, before)) {
            Equation newEquation = new Equation(after); //Quick clone
            HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
            //Go through conditions
            if(this.conditions != null){
                for(EquationCondition condition : conditions){
                    //Replace generics
                    for(String var : values.keySet()){
                        Tree<MathObject> substitution = values.get(var);
                        GenericExpression genExToLookFor = new GenericExpression(var);
                        condition.arg1.tree.replaceAll(new Tree(genExToLookFor), substitution);
                    }
                    //Now check the condition
                    if(!condition.fitsCondition()){
                        //Don't apply the substitution
                        return equation;
                    }
                }
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
}
