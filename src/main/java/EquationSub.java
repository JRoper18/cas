import EquationObjects.MathObjects.GenericExpression;
import EquationObjects.MathObjects.MathObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jack on 1/2/2017.
 */
public class EquationSub {
    public final Equation before;
    public final Equation after;
    private PatternMatcher matcher = new PatternMatcher();
    public EquationSub(Equation before, Equation after){
        this.before = before;
        this.after = after;
    }
    public Equation apply(Equation equation){
        Equation newEquation = new Equation(after.tree); //Quick clone
        if(matcher.patternMatch(equation, before)) {
            HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
            for (String var : values.keySet()) {
                Tree<MathObject> substitution = values.get(var);
                List<LinkedList<Integer>> paths = newEquation.tree.findPaths(new GenericExpression(var)); //Find the matching expressions
                for (LinkedList<Integer> path : paths) {
                    Tree<MathObject> temp = newEquation.tree.getChildThroughPath(path);
                    temp.replaceWith(substitution);
                }
            }
        }
        return newEquation;
    }
}
