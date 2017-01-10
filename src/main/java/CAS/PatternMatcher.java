package CAS;

import CAS.EquationObjects.MathObjects.GenericExpression;
import CAS.EquationObjects.MathObjects.MathObject;
import CAS.EquationObjects.MathObjects.MathSymbol;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by jack on 12/29/2016.
 */
public class PatternMatcher {
    private HashMap<String, Tree<MathObject>> values = new HashMap<>();
    private LinkedList<Integer> badTreePath = new LinkedList<>();
    private Tree<MathObject> expected;
    public PatternMatcher(){

    }
    public boolean patternMatch(Equation eq, Equation pattern){
        Tree<MathObject> eqTree = eq.tree;
        Tree<MathObject> patternTree = pattern.tree;
        return compareSubTrees(eqTree, patternTree);
    }
    private boolean compareSubTrees(Tree<MathObject> eq, Tree<MathObject> pattern){
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
                this.expected = pattern;
                return false;
            case PATTERN_AND:
                for (Tree<MathObject> child : pattern.getChildren()) {
                    if (!compareSubTrees(eq, child)) {
                        this.expected = pattern;
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
            this.expected = pattern;
            return false;
        }
        //Compare the number of children
        if(eq.getNumberOfChildren() != pattern.getNumberOfChildren()){
            this.expected = pattern;
            return false;
        }
        //Compare the actual children.
        for(int i = 0; i<eq.getNumberOfChildren(); i++){
            if(!compareSubTrees(eq.getChild(i), pattern.getChild(i))){
                badTreePath.add(i);
                this.expected = pattern;
                return false;
            }
        }
        //Checked everything possible
        return true;
    }
    public LinkedList<Integer> getPathToFail(){
        return this.badTreePath;
    }
    public Tree<MathObject> getExpectedTree(){
        return this.expected;
    }
    public HashMap<String, Tree<MathObject>> getLastMatchExpressions(){
        return this.values;
    }
}
