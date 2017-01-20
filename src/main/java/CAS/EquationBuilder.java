package CAS;

import CAS.EquationObjects.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class EquationBuilder{
    public static Equation makeEquation(String str){
        return toCorrectForm(new Equation(makeEquationTree(str)));
    }
    public static Equation makeUnprocessedEquation(String str){
        return new Equation(makeEquationTree(str));
    }
    private static Tree<MathObject> makeEquationTree(String equationStr) { //This stakes a string input of which I hope is correctly formatted.

        //NOTE TO FUTURE ME: At one point you might try to change this algorithm so that it implements infix notation or something. DO NOT DO THAT. Instead, Change the preprocess function.
        //If you ever think editing this function is a good idea, just remember the all-nighter you pulled trying to fix a bug in it, and where for every bug you fixed 3 more popped up.
        //This is your final warning. DO NOT TOUCH THIS FUNCTION.
        List<Object> equationObjectList = preProcess(equationStr);
        Tree<MathObject> tree = new Tree<>();
        Tree<MathObject> selected = tree;
        for (Object equationObject : equationObjectList) {
            if (equationObject instanceof SyntaxTerminator) {
                selected = selected.getParent();
                selected.removeChild(selected.getNumberOfChildren() - 1);
                if (selected.isRoot()) {
                    break;
                }
                MathObject operator = (MathObject) selected.data;
                //Check the number of args isn't too little.
                if (operator.getArgs() > selected.getNumberOfChildren()) {
                    throw new UncheckedIOException(new IOException("You have the wrong number of arguments for the operator: " + selected.data + ". It takes " + operator.getArgs() + " arguments, but you put in too little. "));
                }
                selected = selected.getParent().getChild(selected.getParent().getNumberOfChildren() - 1);
            } else if (equationObject instanceof RationalTempInfoHolder) {  //It's not syntax. Is it a temporary info holder?
                selected.data = new MathObject(MathOperator.FRACTION);
                selected.addChildWithData(((RationalTempInfoHolder) equationObject).numer);
                selected.addChildWithData(((RationalTempInfoHolder) equationObject).denom);
                selected.replaceWith(makeUnprocessedEquation("SIMPLIFY_RATIONAL_FRACTION(" + new Equation(selected) + ")").tree);
            } else {
                MathObject current = (MathObject) equationObject;
                selected.data = current;
                if (current.getArgs() > 0){
                    if (!selected.isRoot()) {
                        selected.getParent().addEmptyChild();
                        selected.addEmptyChild();
                        selected = selected.getChild(selected.getNumberOfChildren() - 1);
                    } else {
                        selected.addEmptyChild();
                        selected = selected.getChild(0);
                    }
                } else {
                    if(!selected.isRoot()){
                        selected.getParent().addEmptyChild();
                        selected = selected.getParent().getChild(selected.getParent().getNumberOfChildren() - 1);
                    }
                }
            }
        }
        try{
            boolean catchThis = tree.containsData(null);
        } catch(Exception e){
            System.err.println("Missing a parenthesis on your equation: " + equationStr);
        }

        return tree;
    }
    private static List<Object> preProcess(String equationStr){
        String newStr = equationStr.replace(")", " )");
        String[] tokens = newStr.trim().split("[ (,]");
        List<Object> equationObjectList = new ArrayList<>();
        for(int i = 0; i<tokens.length; i++){
            Object next = parseString(tokens[i]);
            if(next != null){
                equationObjectList.add(next);
            }
        }
        return equationObjectList;
    }
    private static Equation toCorrectForm(Equation eq){
        //Until I solve the infinite loop issue below,
        return Simplifier.simplifyMetaFunctions(eq);
        //return Simplifier.simplify(Simplifier.simplifyMetaFunctions(eq), SimplificationType.AUTOSIMPLIFIED_EXPRESSION);
    }
    public static Object parseString(String str){
        if(str.length() == 0){
            return null;
        }
        //Check it it's a terminator
        if(str.equals(")")){
            return new EquationBuilder().new SyntaxTerminator();
        }
        //Next, check for numbers
        try{
            double num = Double.parseDouble(str);
            if(Math.floor(num) == num){ //Easy way to check if num is an int
                return new MathInteger((int) num);
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
            return new EquationBuilder().new RationalTempInfoHolder(new MathInteger(numer), new MathInteger(denom));
        } catch(NumberFormatException e){
            //IGNORE IT
        }
        //Maybe it's a generic. If it's an expression it'll look like _<genericName>
        if(str.charAt(0) == '_'){ //There's a _ so it's a generic.
            // NOTE: In the future you might make a function with a _ in it's name. Sorry, but I don't care enough to account for that.
            if(str.length() == 1){ //It's just a _, which means any expression of any type with any name.
                return new GenericExpression();
            }
            String name = str.substring(1, str.length());
            GenericExpression genEx = new GenericExpression(name);
            if(str.charAt(1) == '_'){
                //Double underscore means it's named
                genEx.named = true;
            }
            return genEx;
        }
        //It's not a generic, so we don't need to base case sensitive at this point
        str = str.toUpperCase();
        //Check the list of mathematical operators.
        try{
            MathObject obj = new MathObject(MathOperator.valueOf(str));
            //If no error, we found our math object. USE IT!
            return obj;
        } catch (IllegalArgumentException er){
            //IGNORED (I'm such a badass)
        }

        //Check the abbriviations table
        MathObject possibleObject = (MathObjectAbbriviations.abbriviations.get(str));
        if(possibleObject != null){ //A HashMap's .get() method returns null if there's no key.
            return possibleObject;
        }
        throw new UncheckedIOException(new IOException("Operator: " + str + " is not recognized by CAS.EquationBuilder. "));
    }
    private class RationalTempInfoHolder{
        public MathInteger numer;
        public MathInteger denom;
        public RationalTempInfoHolder(MathInteger numer, MathInteger denom){
            this.numer = numer;
            this.denom = denom;
        }

        @Override
        public boolean equals(Object n){
            if(n instanceof RationalTempInfoHolder){
                return this.numer.equals(((RationalTempInfoHolder) n).numer) && this.denom.equals(((RationalTempInfoHolder) n).denom);
            }
            return false;
        }
    }
    private class SyntaxTerminator{}
}