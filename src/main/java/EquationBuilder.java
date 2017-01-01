import EquationObjects.EquationObject;
import EquationObjects.MathObjects.*;
import EquationObjects.PatternMatching.GenericExpression;
import EquationObjects.PatternMatching.GenericType;
import EquationObjects.PatternMatching.LogicalOperator;
import EquationObjects.PatternMatching.LogicalOperatorType;
import EquationObjects.SyntaxObject;
import EquationObjects.SyntaxObjectAbbriviations;
import EquationObjects.SyntaxObjectType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class EquationBuilder{
    public static PatternEquation makePatternEquation(String equationStr){
        return new PatternEquation(makeEquationTree(equationStr, false));
    }
    public static Equation makeEquation(String equationStr){
        return new Equation(makeEquationTree(equationStr));
    }


    private static Tree<MathObject> makeEquationTree(String equationStr){
        return (Tree<MathObject>)(Tree<?>) makeEquationTree(equationStr, true);
    }
    private static Tree<EquationObject> makeEquationTree(String equationStr, boolean isPattern){ //This stakes a string input of which I hope is correctly formatted.
        List<EquationObject> equationObjectList = preProcess(equationStr, isPattern);
        Tree<EquationObject> tree = new Tree<>();
        Tree<EquationObject> selected = tree;
        for(EquationObject equationObject : equationObjectList){
            if(equationObject instanceof SyntaxObject){
                SyntaxObjectType objType = ((SyntaxObject) equationObject).syntax;
                switch(objType){
                    case OPEN_PAREN:
                        //An open paren means arguments are beginning.
                        selected.addEmptyChild();
                        selected = selected.getChild(selected.getNumberOfChildren()-1);
                        break;
                    case CLOSE_PAREN:
                        selected = selected.getParent();
                        break;
                    case COMMA:
                        selected = selected.getParent();
                        selected.addEmptyChild();
                        selected = selected.getChild(selected.getNumberOfChildren()-1);
                        break;
                    default:
                }
            }
            else{
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
            MathObjectNamed obj = new MathObjectNamed(MathOperators.valueOf(str));
            //If no error, we found our math object. USE IT!
            return obj;
        } catch (IllegalArgumentException er){
            //IGNORED (I'm such a badass)
        }
        //OK, now check the abbriviations table.
        MathOperators possible = (MathOperatorsAbbriviations.abbriviations.get(str));
        if(possible != null){ //A HashMap's .get() method returns null if there's no key.
            return new MathObjectNamed(possible);
        }
        //Now do the same for syntax operators
        try{
            SyntaxObjectType obj = (SyntaxObjectType.valueOf(str));
            return new SyntaxObject(obj);
        } catch (IllegalArgumentException er){
        }
        SyntaxObjectType possibleSyntax = (SyntaxObjectAbbriviations.abbriviations.get(str));
        if(possibleSyntax != null){ //A HashMap's .get() method returns null if there's no key.
            return new SyntaxObject(possibleSyntax);
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
            //Nope, not logical. Maybe it's a generic. If it's an expression it'll look like <genericType>_<genericName>
            int index = str.indexOf('_');
            if(index != -1){ //There's a _ so it's a generic.
                // NOTE: In the future you might make a function with a _ in it's name. Sorry, but I don't care enough to account for that.
                if(str.length() == 1){ //It's just a _, which means any expression of any type with any name.
                    return new GenericExpression();
                }
                String name = str.substring(index + 1, str.length());
                if(index == 0){ //They have nothing behind the _, so no type is specified.
                    return new GenericExpression(name);
                }
                String typeStr = str.substring(0, index);
                GenericType type;
                try{
                    type = GenericType.valueOf(typeStr);
                } catch (InvalidParameterException er){
                    throw new UncheckedIOException(new IOException("You put in an invalid generic expression type: " + typeStr));
                }
                if(index == str.length()-1){ //The _ is at the back, so no name specified.
                    return new GenericExpression(type);
                }

                //We've gotten here, so it must have both a name and a type defined.
                return new GenericExpression(type, name);
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