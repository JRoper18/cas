package PatternMatching;

import CAS.Equation;
import CAS.EquationObjects.GenericExpression;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.Tree;
import Identification.IdentificationType;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by jack on 12/29/2016.
 */
public class PatternMatcher {
    public PatternMatcher(){

    }
    public static PatternMatchResult patternMatch(Equation eq, Equation pattern){
        Tree<MathObject> eqTree = eq.tree;
        Tree<MathObject> patternTree = pattern.tree;
        MatchData unprocessedData = compareSubTrees(eqTree, patternTree, new PatternMatcher().new MatchData(false /*Isn't ever looked at*/, new HashMap<String,Tree<MathObject>>(), new HashMap<String, String>(),new LinkedList<Integer>(), new HashMap<String, String>()));
        HashMap<String, Equation> newMap = new HashMap<>();
        HashMap<String, Equation> newFunctions = new HashMap<>();
        for(String key: unprocessedData.values.keySet()){
            newMap.put(key, new Equation(unprocessedData.values.get(key)));
        }
        for(String key: unprocessedData.varTags.keySet()){
            newMap.put(key, new Equation("_" + unprocessedData.varTags.get(key)));
        }
        for(String key: unprocessedData.functions.keySet()){
            newFunctions.put(key, new Equation(unprocessedData.functions.get(key), 0));
        }
        return new PatternMatchResult(eq, pattern, unprocessedData.match, newMap, unprocessedData.errorPath, newFunctions);
    }
    private static MatchData compareSubTrees(Tree<MathObject> eq, Tree<MathObject> pattern, MatchData soFar){
        MatchData data = soFar.clone();
        HashMap<String, Tree<MathObject>> values = data.values;
        HashMap<String, String> varTags = data.varTags; //This is for named variables
        LinkedList<Integer> path = data.errorPath;
        HashMap<String, String> functions = data.functions;
        MathObject current = eq.data;
        MathObject compare = pattern.data;
        switch(compare.getOperator()) { //Depending on the operator, check the subtrees
            case PATTERN_OR:
                for (Tree<MathObject> child : pattern.getChildren()) {
                    if (compareSubTrees(eq, child, data).match) {
                        return new PatternMatcher().new MatchData(true, values, varTags, path, functions);
                    }
                }

            case PATTERN_AND:
                for (Tree<MathObject> child : pattern.getChildren()) {
                    if (!compareSubTrees(eq, child, data).match) {
                        return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
                    }
                }
                return new PatternMatcher().new MatchData(true, values, varTags, path, functions);

            case EXPRESSION:
                //We have an expression. Check if it's any specific type of expression
                GenericExpression genEx = (GenericExpression) compare;
                if(genEx.named){
                    if(!(eq.data instanceof GenericExpression)) {
                        return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
                    }
                    if(!((GenericExpression) eq.data).tag.equals(genEx.tag)){
                        return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
                    }

                }
                if(genEx.type != IdentificationType.EXPRESSION){
                    if(!new Equation(eq, 0).isType(genEx.type)){
                        return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
                    }
                }
                else{

                }
                if (genEx.hasTag()) {
                    String tag = genEx.tag;
                    if(tag.charAt(0) == '#'){ //We have an unspecific but named.
                        if(!(eq.data instanceof GenericExpression)){
                            return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
                        }

                        if(!varTags.containsKey(tag)){
                            //A # means that it's unique. PLUS(_x, _x) will not doesMatchPattern PLUS(_#1, _#2).
                            if(varTags.containsValue(((GenericExpression) eq.data).tag)){
                                return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
                            }
                            varTags.put(tag, ((GenericExpression) eq.data).tag);
                            return new PatternMatcher().new MatchData(true, values, varTags, path, functions);

                        }
                        if(((GenericExpression) eq.data).type == IdentificationType.VARIABLE){
                            return new PatternMatcher().new MatchData(((GenericExpression) eq.data).tag.equals(varTags.get(tag)), values, varTags, path, functions);
                        }
                        return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
                    }
                    //First, check if we have a tag already
                    if (values.containsKey(tag)) {
                        return new PatternMatcher().new MatchData(values.get(tag).equals(eq), values, varTags, path, functions);
                    } else {
                        //Add the tag to the values map
                        values.put(tag, eq);
                        return new PatternMatcher().new MatchData(true, values, varTags, path, functions);
                    }
                }

                //No tag. Just return true.
                return new PatternMatcher().new MatchData(true, values, varTags, path, functions);
            default:
        }
        //Compare raw data.
        if(!eq.data.equals(pattern.data) && compare.getOperator() != MathOperator.GENERIC_FUNCTION){
            return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
        }
        //Compare the number of children (unless pattern's children are generics or logical operators
        if(compare.getOperator() != MathOperator.OR && compare.getOperator() != MathOperator.AND && compare.getOperator() != MathOperator.EXPRESSION && (!compare.getOperator().isAssociative() && eq.getNumberOfChildren() != pattern.getNumberOfChildren())){
            return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
        }
        //Compare the actual children.
        for(int i = 0; i<pattern.getNumberOfChildren(); i++){
            Tree<MathObject> childPattern = pattern.getChild(i);
            if(i >= eq.getNumberOfChildren()){
                if(childPattern.data instanceof GenericExpression) {
                    if (((GenericExpression) childPattern.data).type == IdentificationType.EXPRESSION && compare.getOperator().isAssociative()) {

                        if(!eq.data.getOperator().hasIdentity()){
                            path.addFirst(i);
                            return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
                        }
                        Equation idenEq = eq.data.getOperator().identity();
                        //Just check to see if we've checked this expression before.
                        if (values.containsKey(((GenericExpression) childPattern.data).tag)) {
                            boolean matches = values.get(((GenericExpression) childPattern.data).tag).equals(idenEq.tree);
                            if(!matches){
                                path.addFirst(i);
                            }
                            return new PatternMatcher().new MatchData(matches, values, varTags, path, functions);
                        } else {
                            //Add the tag to the values map
                            values.put(((GenericExpression) childPattern.data).tag, idenEq.tree);
                            return new PatternMatcher().new MatchData(true, values, varTags, path, functions);
                        }
                    }
                }
            }
            Tree<MathObject> childEq = eq.getChild(i);
            if(childPattern.data instanceof GenericExpression){
                if(((GenericExpression) childPattern.data).type == IdentificationType.EXPRESSION && compare.getOperator().isAssociative()){
                    //If we have an associative operator (lets call it OP) then we can skip the rest of our children. and just assume that there are an expression with root OP.
                    //OP(2, _EXPRESSION) matches OP(2, 1), OP(2, 3, 4, 5), or anything that is OP(2, .....)
                    //However, we have to think of if we have only one expression in our equation, and 2 expressions in our pattern. Then we can use the identity (if op has one)
                    //AN example of too many args is OP(2, _EXPRESSION) matching OP(2) and setting expression as identity.
                    Tree<MathObject> toCheckFor = new Tree<>(eq.data);
                    int mark = eq.getNumberOfChildren();
                    for(int j = i + 1; j<pattern.getNumberOfChildren(); j++){
                        //What if we have OP(_EXPRESSION, _EXPRESSION)?
                        if(pattern.getChild(j).data instanceof GenericExpression){
                            if(((GenericExpression) pattern.getChild(j).data).type == IdentificationType.EXPRESSION){
                                mark = j; //ToCheckFor only needs all the terms UNTIL the next generic expression
                                break;
                            }
                        }
                    }
                    toCheckFor.setChildren(eq.getChildren().subList(i, mark));
                    int numChildren = toCheckFor.getNumberOfChildren();
                    if(numChildren >= compare.getOperator().getArguments()){
                        //Just check to see if we've checked this expression before.
                        if (values.containsKey(((GenericExpression) childPattern.data).tag)) {
                            boolean matches = values.get(((GenericExpression) childPattern.data).tag).equals(toCheckFor);
                            if(!matches){
                                path.addFirst(i);
                            }
                            return new PatternMatcher().new MatchData(matches, values, varTags, path, functions);
                        } else {
                            //Add the tag to the values map
                            values.put(((GenericExpression) childPattern.data).tag, toCheckFor);
                            if(mark == eq.getNumberOfChildren()){ //This was the last generic expression, no need to keep looking.
                                return new PatternMatcher().new MatchData(true, values, varTags, path, functions);
                            }
                        }
                    }
                }
            }
            MatchData childData = compareSubTrees(eq.getChild(i), pattern.getChild(i), data);
            varTags.putAll(childData.varTags);
            values.putAll(childData.values);
            if(!childData.match) {
                path.addFirst(i);
                return new PatternMatcher().new MatchData(false, values, varTags, path, functions);
            }
        }
        if(compare.getOperator() == MathOperator.GENERIC_FUNCTION){
            functions.put(compare.getName(), eq.toString());
        }
        //Checked everything possible
        return new PatternMatcher().new MatchData(true, values, varTags, path, functions);
    }
    public static boolean doesMatchPattern(Equation eq, Equation pattern){
        return patternMatch(eq, pattern).match;
    }
    private class MatchData{
        public boolean match;
        public HashMap<String, Tree<MathObject>> values;
        public HashMap<String, String> varTags;
        public LinkedList<Integer> errorPath;
        public HashMap<String, String> functions;
        public MatchData(boolean match, HashMap<String, Tree<MathObject>> values, HashMap<String, String> varTags, LinkedList<Integer> path, HashMap<String, String> functions){
            this.match = match;
            this.values = values;
            this.varTags = varTags;
            this.errorPath = path;
            this.functions = functions;
        }
        public MatchData clone(){
            return new MatchData(this.match, new HashMap<>(this.values), new HashMap<>(this.varTags), this.errorPath, this.functions);
        }
        public void addToPath(int i){
            if(this.errorPath == null){
                this.errorPath = new LinkedList<Integer>();
            }
            this.errorPath.addFirst(i);
        }
    }
}
