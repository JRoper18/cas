package CAS;

import CAS.EquationObjects.MathObjects.GenericExpression;
import CAS.EquationObjects.MathObjects.MathObject;
import CAS.EquationObjects.MathObjects.MathSymbol;

import java.util.HashMap;

/**
 * Created by jack on 12/29/2016.
 */
public class PatternMatcher {
    private HashMap<String, Tree<MathObject>> values = new HashMap<>(); //Definitely not thread-safe. Fix later
    public boolean patternMatch(Equation eq, Equation pattern){
        Tree<MathObject> eqTree = eq.tree;
        Tree<MathObject> patternTree = pattern.tree;
        return compareSubTrees(eqTree, patternTree, true);
    }
    private boolean compareSubTrees(Tree<MathObject> eq, Tree<MathObject> pattern){
        return compareSubTrees(eq, pattern, false);
    }
    private boolean compareSubTrees(Tree<MathObject> eq, Tree<MathObject> pattern, boolean initializer){
        if(initializer){
            values.clear(); //Reset the values
        }
        MathObject current = eq.data;
        MathObject compare = pattern.data;
        MathSymbol mathSymbol = compare.getOperator();
        switch(compare.getOperator()) { //Depending on the operator, check the subtrees
            case PATTERN_OR:
                for (Tree<MathObject> child : pattern.getChildren()) {
                    if (compareSubTrees(eq, child)) {
                        return true;
                    }
                }

            case PATTERN_AND:
                for (Tree<MathObject> child : pattern.getChildren()) {
                    if (!compareSubTrees(eq, child)) {
                        return false;
                    }
                }
                return true;
            case EXPRESSION:
                //We have an expression. Check if it's any specific type of expression
                GenericExpression genEx = (GenericExpression) compare;
                if (genEx.hasTag()) {

                    String tag = genEx.tag;
                    //First, check if we have a tag already
                    if (values.containsKey(tag)) {
                        return values.get(tag).equals(eq);
                    } else {
                        //Add the tag to the values map
                        values.put(tag, eq);
                        return true;
                    }
                }
                //No tag. Just return true.
                return true;
            default:
        }
        //Compare raw data.
        if(!eq.data.equals(pattern.data)){
            return false;
        }
        //Compare the number of children (unless pattern's children are generics or logical operators
        if(mathSymbol != MathSymbol.OR && mathSymbol != MathSymbol.AND && mathSymbol != MathSymbol.EXPRESSION && eq.getNumberOfChildren() != pattern.getNumberOfChildren()){
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