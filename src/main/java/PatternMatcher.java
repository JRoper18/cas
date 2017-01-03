import EquationObjects.EquationObject;
import EquationObjects.MathObjects.MathObject;
import EquationObjects.PatternMatching.GenericExpression;
import EquationObjects.PatternMatching.LogicalOperator;

import java.util.HashMap;

/**
 * Created by jack on 12/29/2016.
 */
public class PatternMatcher {
    private static HashMap<String, Tree<MathObject>> values = new HashMap<>();
    public static boolean patternMatch(Equation eq, PatternEquation pattern){
        Tree<MathObject> eqTree = eq.tree;
        Tree<EquationObject> patternTree = pattern.tree;
        return compareSubTrees(eqTree, patternTree, true);
    }
    private static boolean compareSubTrees(Tree<MathObject> eq, Tree<EquationObject> pattern){
        return compareSubTrees(eq, pattern, false);
    }
    private static boolean compareSubTrees(Tree<MathObject> eq, Tree<EquationObject> pattern, boolean initializer){
        if(initializer){
            values.clear(); //Reset the values
        }
        MathObject current = eq.data;
        EquationObject compare = pattern.data;
        if(compare instanceof LogicalOperator){ //Depending on the operator, check the subtrees
            switch(((LogicalOperator) compare).operator){
                case OR:
                    for(Tree<EquationObject> child : pattern.getChildren()){
                        if(compareSubTrees(eq, child)){
                            return true;
                        }
                    }

                case AND:
                    for(Tree<EquationObject> child : pattern.getChildren()){
                        if(!compareSubTrees(eq, child)){
                            return false;
                        }
                    }
                    return true;

                default:
            }
        }
        //No operator. Time to check for generics.
        if(compare instanceof GenericExpression){ //We have an expression. Check if it's any specific type of expression.
            GenericExpression genEx = (GenericExpression) compare;
            if(genEx.hasTag()){
                String tag = genEx.tag;
                //First, check if we have a tag already
                if(values.containsKey(tag)){
                    return values.get(tag).equals(eq);
                }
                else{
                    //Add the tag to the values map
                    values.put(tag, eq);
                    return true;
                }
            }
            //No tag. Just return true.
            return true;
        }
        //Compare raw data.
        if(!eq.data.equals(pattern.data)){
            return false;
        }
        //Compare the number of children (unless pattern's children are generics or logical operators
        if(!(pattern.getChild(0).data instanceof GenericExpression || pattern.getChild(0).data instanceof LogicalOperator || eq.getNumberOfChildren() == pattern.getNumberOfChildren())){
            return false;
        }
        //Compare the actual children.
        for(int i = 0; i<eq.getNumberOfChildren(); i++){
            if(!compareSubTrees(eq.getChild(i), pattern.getChild(i))){
                return false;
            }
        }
        //Checked everything possible
        return true;
    }
    public HashMap<String, Tree<MathObject>> getLastMatchExpressions(){
        return this.values;
    }
}
