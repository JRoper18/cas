package CAS;

import CAS.EquationObjects.GenericExpression;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import sun.net.www.content.text.Generic;

import java.util.HashMap;

/**
 * Created by jack on 12/29/2016.
 */
public class PatternMatcher {
    private HashMap<String, Tree<MathObject>> values = new HashMap<>();
    private HashMap<String, String> varTags = new HashMap<>(); //This is for named variables.
    public PatternMatcher(){

    }
    public boolean patternMatch(Equation eq, Equation pattern){
        values.clear();
        varTags.clear();
        Tree<MathObject> eqTree = eq.tree;
        Tree<MathObject> patternTree = pattern.tree;
        return compareSubTrees(eqTree, patternTree);
    }
    private boolean compareSubTrees(Tree<MathObject> eq, Tree<MathObject> pattern){
        MathObject current = eq.data;
        MathObject compare = pattern.data;
        MathOperator mathSymbol = compare.getOperator();
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
                if(genEx.named){
                    if(!(eq.data instanceof GenericExpression)) {
                        return false;
                    }
                    if(!((GenericExpression) eq.data).tag.equals(genEx.tag)){
                        return false;
                    }

                }
                if(genEx.type != IdentificationType.EXPRESSION){
                    if(!new Equation(eq, 0).isType(genEx.type)){
                        return false;
                    }
                }
                if (genEx.hasTag()) {
                    String tag = genEx.tag;
                    if(tag.charAt(0) == '#'){ //We have an unspecific but named.
                        if(!(eq.data instanceof GenericExpression)){
                            return false;
                        }
                        if(!varTags.containsKey(tag)){
                            varTags.put(tag, ((GenericExpression) eq.data).tag);
                            return true;
                        }
                        if(((GenericExpression) eq.data).type == IdentificationType.VARIABLE){
                            return ((GenericExpression) eq.data).tag.equals(varTags.get(tag));
                        }
                        return false;
                    }
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
        if(mathSymbol != MathOperator.OR && mathSymbol != MathOperator.AND && mathSymbol != MathOperator.EXPRESSION && eq.getNumberOfChildren() != pattern.getNumberOfChildren()){
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
    public HashMap<String, String> getLastMatchVariables() {
        return this.varTags;
    }
    public HashMap<String, Tree<MathObject>> getLastMatchExpressions(){
        return this.values;
    }
}
