package CAS;

import CAS.EquationObjects.*;
import Database.EquationSubDatabase;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class EquationBuilder{
    public static Equation makeEquation(String str, int autoSimplifyLevel){
        return simplifyTree(makeEquationTree(str), autoSimplifyLevel);
    }
    public static Equation simplifyTree(Tree<MathObject> tree, int autoSimplifyLevel){
        Equation eq = new Equation(tree);
        Equation processedEquation;
        if(autoSimplifyLevel == 0){
            return new Equation(tree);
        }
        else{
            processedEquation = simplifyTree(tree, autoSimplifyLevel-1);
            if(processedEquation.tree.containsData(new MathObject(MathOperator.UNDEFINED))){
                return new Equation("UNDEFINED", 0);
            }
            switch(autoSimplifyLevel){
                case 1: //Only do meta-functions.
                    return Simplifier.simplifyMetaFunctions(processedEquation);
                case 2: //Autosimplify this
                    processedEquation = Simplifier.simplifyWithMetaFunction(processedEquation, MathOperator.SIMPLIFY_RATIONAL_EXPRESSION);
                    processedEquation = Simplifier.orderEquation(processedEquation);
                    return Simplifier.orderEquation(Simplifier.simplifyWithMetaFunction(processedEquation, MathOperator.AUTOSIMPLIFY));
                default:
                    return processedEquation;
            }
        }
    }
    public static Equation makeEquation(String str){
        return makeEquation(str, 2);
    }
    public static Equation makeUnprocessedEquation(String str){
        return new Equation(makeEquationTree(str));
    }
    private static Tree<MathObject> makeEquationTree(String equationStr) { //This stakes a string input of which I hope is correctly formatted.

        //NOTE TO FUTURE ME: At one point you might try to change this algorithm so that it implements infix notation or something. DO NOT DO THAT. Instead, Change the preprocess function.
        //If you ever think editing this function is a good idea, just remember the all-nighter you pulled trying to fix a bug in it, and where for every bug you fixed 3 more popped up.
        //This is your final warning. DO NOT TOUCH THIS FUNCTION.

        //Note from 3/27/17. Hey, I actually changed it. Not impossible, just a pain in the ass. Wouldn't do it again.
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
                selected.replaceWith(new Equation("SIMPLIFY_RATIONAL_FRACTION(" + new Equation(selected) + ")", 1).tree);
                if(!selected.isRoot()){
                    selected.getParent().addEmptyChild();
                }
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
        //Reorder for infix to prefix
        for(int i = 0; i<equationObjectList.size(); i++){
            Object current = equationObjectList.get(i);
            if(current instanceof AbbriviationData){
                AbbriviationData data = (AbbriviationData) current;
                if(data.isInfix){
                    //Find the function right before this one.
                    int checkBack = i;
                    Object backwardsCurrent = equationObjectList.get(checkBack);
                    int parenLevel = 0;
                    do {
                        checkBack--;
                        backwardsCurrent = equationObjectList.get(checkBack);
                        if(backwardsCurrent instanceof MathObject){
                            if(((MathObject) backwardsCurrent).getOperator().getSubType() != MathOperatorSubtype.SYMBOL){ //Found a function
                                parenLevel--;
                            }
                        }
                        else if(backwardsCurrent instanceof SyntaxTerminator){
                            parenLevel++;
                        }
                    } while(parenLevel != 0);
                    //If parenleve is 0, backwardsCurrent is our function we need to switch.
                    //Find the next complete function
                    parenLevel = 0;
                    int check = i;
                    do {
                        check++;
                        backwardsCurrent = equationObjectList.get(check);
                        if(backwardsCurrent instanceof MathObject){
                            if(((MathObject) backwardsCurrent).getOperator().getSubType() != MathOperatorSubtype.SYMBOL){ //Found a function
                                parenLevel++;
                            }
                        }
                        else if(backwardsCurrent instanceof SyntaxTerminator){
                            parenLevel--;
                        }
                    } while(parenLevel != 0);
                    //Add the ending parenthesis:
                    equationObjectList.add(check + 1, new EquationBuilder().new SyntaxTerminator());
                    //Swap the infix operator with the first statement and remove the infix operator
                    equationObjectList.remove(i);
                    equationObjectList.add(checkBack, data.op);
                }
                else{
                    equationObjectList.set(i, ((AbbriviationData) current).op); //Replace the abbriviationdata with it's operator
                }
            }
        }
        return equationObjectList;
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
                genEx.tag = genEx.tag.substring(1, genEx.tag.length()); //Remove the first character, it's an _
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
        AbbriviationData abbrData = (MathObjectAbbriviations.abbriviations.get(str));
        if(abbrData != null){ //A HashMap's .get() method returns null if there's no key.
            return abbrData;
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