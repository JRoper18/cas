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
                else{

                }
                if (genEx.hasTag()) {
                    String tag = genEx.tag;
                    if(tag.charAt(0) == '#'){ //We have an unspecific but named.
                        if(!(eq.data instanceof GenericExpression)){
                            return false;
                        }
                        if(!varTags.containsKey(tag)){
                            //A # means that it's unique. PLUS(_x, _x) will not match PLUS(_#1, _#2).
                            if(varTags.containsValue(((GenericExpression) eq.data).tag)){
                                return false;
                            }
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
        if(compare.getOperator() != MathOperator.OR && compare.getOperator() != MathOperator.AND && compare.getOperator() != MathOperator.EXPRESSION && (!compare.getOperator().isAssociative() && eq.getNumberOfChildren() != pattern.getNumberOfChildren())){
            return false;

        }
        //Compare the actual children.
        for(int i = 0; i<pattern.getNumberOfChildren(); i++){
            Tree<MathObject> childPattern = pattern.getChild(i);
            if(i >= eq.getNumberOfChildren()){
                if(childPattern.data instanceof GenericExpression) {
                    if (((GenericExpression) childPattern.data).type == IdentificationType.EXPRESSION && compare.getOperator().isAssociative()) {
                        if(!eq.data.getOperator().hasIdentity()){
                            return false;
                        }
                        Equation idenEq = eq.data.getOperator().identity();
                        //Just check to see if we've checked this expression before.
                        if (values.containsKey(((GenericExpression) childPattern.data).tag)) {
                            return values.get(((GenericExpression) childPattern.data).tag).equals(idenEq.tree);
                        } else {
                            //Add the tag to the values map
                            values.put(((GenericExpression) childPattern.data).tag, idenEq.tree);
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            Tree<MathObject> childEq = eq.getChild(i);
            if(childPattern.data instanceof GenericExpression){
                if(((GenericExpression) childPattern.data).type == IdentificationType.EXPRESSION && compare.getOperator().isAssociative()){
                    //If we have an associative operator (lets call it OP) then we can skip the rest of our children. and just assume that there are an expression with root OP.
                    //OP(2, _EXPRESSION) matches OP(2, 1), OP(2, 3, 4, 5), or anything that is OP(2, .....)
                    //However, we have to think of if we have only one expression in our equation, and 2 expressions in our pattern. Then we can use the identity (if op has one)
                    //AN example of too many args is OP(2, _EXPRESSION) matching OP(2) and setting expression as identity.
                    Tree<MathObject> toCheckFor = new Tree<>(eq.data);
                    toCheckFor.setChildren(eq.getChildren().subList(i, eq.getNumberOfChildren()));
                    int numChildren = toCheckFor.getNumberOfChildren();
                    if(numChildren > compare.getOperator().getArguments()){
                        //Just check to see if we've checked this expression before.
                        if (values.containsKey(((GenericExpression) childPattern.data).tag)) {
                            return values.get(((GenericExpression) childPattern.data).tag).equals(toCheckFor);
                        } else {
                            //Add the tag to the values map
                            values.put(((GenericExpression) childPattern.data).tag, toCheckFor);
                            return true;
                        }
                    }
                    else{
                        return true; //Same number of children
                    }
                }
            }
            if(!compareSubTrees(eq.getChild(i), pattern.getChild(i))) {
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
