import EquationObjects.EquationObject;
import EquationObjects.EquationObjectAbbriviations;
import EquationObjects.MathObjects.MathNumberInteger;
import EquationObjects.MathObjects.MathNumberRational;
import EquationObjects.MathObjects.MathObject;
import EquationObjects.MathObjects.MathSymbol;
import EquationObjects.PatternMatching.GenericExpression;
import EquationObjects.PatternMatching.LogicalOperator;
import EquationObjects.PatternMatching.LogicalOperatorType;
import EquationObjects.SyntaxObject;
import EquationObjects.SyntaxObjectType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class EquationBuilder{
    public static PatternEquation makePatternEquation(String equationStr){
        return new PatternEquation(makeEquationTree(equationStr, true));
    }
    public static Equation makeEquation(String equationStr){
        return new Equation(makeEquationTree(equationStr));
    }


    private static Tree<MathObject> makeEquationTree(String equationStr){
        return (Tree<MathObject>)(Tree<?>) makeEquationTree(equationStr, false);
    }
    private static Tree<EquationObject> makeEquationTree(String equationStr, boolean isPattern) { //This stakes a string input of which I hope is correctly formatted.
        List<EquationObject> equationObjectList = preProcess(equationStr, isPattern);
        Tree<EquationObject> tree = new Tree<>();
        Tree<EquationObject> selected = tree;
        for (EquationObject equationObject : equationObjectList) {
            if (equationObject instanceof SyntaxObject) {
                SyntaxObjectType objType = ((SyntaxObject) equationObject).syntax;
                switch (objType) {
                    case OPEN_PAREN:
                        //An open paren means arguments are beginning.
                        selected.addEmptyChild();
                        selected = selected.getChild(selected.getNumberOfChildren() - 1);
                        break;
                    case CLOSE_PAREN:
                        selected = selected.getParent();
                        //Check the number of args isn't too little.
                        MathObject operator = (MathObject) selected.data;
                        if (operator.getArgs() > selected.getNumberOfChildren()) {
                            throw new UncheckedIOException(new IOException("You have the wrong number of arguments for the operator: " + selected.data + ". It takes " + operator.getArgs() + " arguments, but you put in too little. "));
                        }
                        break;
                    case COMMA:
                        selected = selected.getParent();
                        //Check to see if we have too many arguments
                        MathObject parentOperator = ((MathObject) selected.data);
                        int argsNeeded = parentOperator.getArgs();
                        if (argsNeeded <= selected.getNumberOfChildren() && !parentOperator.isAssociative()) { //Too many arguments
                            throw new UncheckedIOException(new IOException("You have the wrong number of arguments for the operator: " + selected.data + ". It takes " + argsNeeded + " arguments, but you put in too much. "));
                        }
                        selected.addEmptyChild();
                        selected = selected.getChild(selected.getNumberOfChildren() - 1);
                        break;
                    default:
                }
            } else {
                //It's not syntax.
                selected.data = equationObject;
            }
        }
        return tree;
    }
    private static List<EquationObject> preProcess(String equationStr, boolean isPattern){
        String[] tokens = equationStr.split(" ");
        List<EquationObject> equationObjectList = new ArrayList<>();
        for(int i = 0; i<tokens.length; i++){
            equationObjectList.add(parseString(tokens[i], isPattern));
        }
        return equationObjectList;
    }
    public static EquationObject parseString(String str){
        return parseString(str, true);
    }
    public static EquationObject parseString(String str, boolean allowPattern){
        //First, check for numbers
        try{
            double num = Double.parseDouble(str);
            if(Math.floor(num) == num){ //Easy way to check if num is an int
                return new MathNumberInteger((int) num);
            }
            //Nope, it's decimal. We hate decimal numbers, turn them into rational numbers.
            //Remove the decimal place and turn it into a fraction.
            /*
            3.7 = 37/10
            3.503 = 3503 / 1000
            345.234 = 345234 / 1000
             */
            int afterDecLength = str.length() - str.indexOf('.') - 1;

            int denom = (int) Math.pow(10, afterDecLength);
            int numer = (int) (num * denom);
            return new MathNumberRational(numer, denom);
        } catch(NumberFormatException e){
            //IGNORE IT
        }
        //Check the list of mathematical operators.
        try{
            MathObject obj = new MathObject(MathSymbol.valueOf(str));
            //If no error, we found our math object. USE IT!
            return obj;
        } catch (IllegalArgumentException er){
            //IGNORED (I'm such a badass)
        }

        //Now do the same for syntax operators
        try{
            SyntaxObjectType obj = (SyntaxObjectType.valueOf(str));
            return new SyntaxObject(obj);
        } catch (IllegalArgumentException er){
        }

        //Check the abbriviations table
        EquationObject possibleObject = (EquationObjectAbbriviations.abbriviations.get(str));
        if(possibleObject != null){ //A HashMap's .get() method returns null if there's no key.
            return possibleObject;
        }
        if(allowPattern){ //If we are allowing pattern objects, check for logical operators
            switch(str){
                case "||":
                    return new LogicalOperator(LogicalOperatorType.OR);
                case "&&":
                    return new LogicalOperator(LogicalOperatorType.AND);
                default:
                    //If more logical operators are added, put them in this switch statement.
            }

            //Nope, not logical. Maybe it's a generic. If it's an expression it'll look like _<genericName>
            if(str.charAt(0) == '_'){ //There's a _ so it's a generic.
                // NOTE: In the future you might make a function with a _ in it's name. Sorry, but I don't care enough to account for that.
                if(str.length() == 1){ //It's just a _, which means any expression of any type with any name.
                    return new GenericExpression();
                }
                String name = str.substring(1, str.length());
                return new GenericExpression(name);
            }
        }
        throw new UncheckedIOException(new IOException("Operator: " + str + " is not recognized by EquationBuilder. "));
    }
    private static List<ParenInfo> findParen(List<EquationObject> eq){
        List<ParenInfo> result = new ArrayList<ParenInfo>();
        Stack<Integer> notClosedParens = new Stack<Integer>();
        for(int i = 0; i<eq.size(); i++){
            EquationObject current = eq.get(i);
            if(current instanceof SyntaxObject){
                SyntaxObjectType mathObject = ((SyntaxObject) current).syntax;
                if(mathObject == SyntaxObjectType.OPEN_PAREN) {
                    notClosedParens.push(new Integer(i));
                }
                else if(mathObject == SyntaxObjectType.CLOSE_PAREN){
                    int nextParenIndex = notClosedParens.pop();
                    ParenInfo newParenInfo = new ParenInfo(nextParenIndex, new Integer(i), notClosedParens.size() + 1);
                    result.add(newParenInfo);
                }
            }
        }
        return result;
    }
    private static class ParenInfo{
        public int start;
        public int end;
        public int level;
        public ParenInfo(int startIndex, int endIndex, int level){
            this.start = startIndex;
            this.end = endIndex;
            this.level = level;
        }
    }
}