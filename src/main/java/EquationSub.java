import EquationObjects.EquationObject;
import EquationObjects.MathObjects.MathObject;
import EquationObjects.PatternMatching.GenericExpression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jack on 1/2/2017.
 */
public class EquationSub {
    public PatternEquation before;
    public PatternEquation after;
    private PatternMatcher matcher = new PatternMatcher();
    public EquationSub(PatternEquation before, PatternEquation after){
        this.before = before;
        this.after = after;
    }
    public Equation apply(Equation equation){
        if(matcher.patternMatch(equation, before)){
            HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
            for(String var : values.keySet()){
                Tree<MathObject> substitution = values.get(var);
                List<LinkedList<Integer>> paths = after.tree.findPaths(new GenericExpression(var)); //Find the matching expressions
                for(LinkedList<Integer> path : paths){
                    Tree<EquationObject> temp = after.tree.getChildThroughPath(path);
                    temp.replaceWith((Tree<EquationObject>)(Tree<?>) substitution); //This casting is annoying.
                }
            }
    }
}
