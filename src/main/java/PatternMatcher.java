import EquationObjects.EquationObject;
import EquationObjects.MathObjects.MathObject;
import EquationObjects.PatternMatching.GenericExpression;
import EquationObjects.PatternMatching.LogicalOperator;

import java.util.HashMap;

/**
 * Created by jack on 12/29/2016.
 */
public class PatternMatcher {
    public HashMap<String, Tree<MathObject>> values = new HashMap<>();
    public static boolean patternMatch(Equation eq, PatternEquation pattern){
        Tree<MathObject> eqTree = eq.tree;
        Tree<EquationObject> patternTree = pattern.tree;
        return false;
    }
    public static boolean compareSubTrees(Tree<MathObject> eq, Tree<MathObject> pattern){
        MathObject current = eq.data;
        EquationObject compare = pattern.data;
        if(compare instanceof LogicalOperator){ //Depending on the operator, check the subtrees
            switch(((LogicalOperator) compare).operator){
                case OR:
                    for(Tree<MathObject> child : pattern.getChildren()){
                        if(compareSubTrees(eq, child)){
                            return true;
                        }
                    }

                case AND:
                    for(Tree<MathObject> child : pattern.getChildren()){
                        if(!compareSubTrees(eq, child)){
                            return false;
                        }
                    }
                    return true;

                default:
            }
        }
        //No operator. Time to check for generics.
        if(compare instanceof GenericExpression){ //We have a constant. Check if it's any specific type of constant.

        }
        return false; //CHANGE
    }
}
